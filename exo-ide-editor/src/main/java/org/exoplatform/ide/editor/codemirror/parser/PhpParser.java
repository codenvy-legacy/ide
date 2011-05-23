/*
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.editor.codemirror.parser;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.editor.api.codeassitant.Modifier;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.codemirror.Node;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: $
 *
 */
public class PhpParser extends CodeMirrorParserImpl
{

   String currentContentMimeType;

   private Stack<Node> nodeStack = new Stack<Node>();
   
   /**
    * Stack of blocks "{... {...} ...}"
    */
   private Stack<TokenType> enclosers = new Stack<TokenType>();
   
   @Override
   public void init() 
   {
      super.init();
      currentContentMimeType = MimeType.TEXT_HTML;    
      nodeStack.clear();
   }   

   @Override
   TokenBeenImpl parseLine(JavaScriptObject javaScriptNode, int lineNumber, TokenBeenImpl currentToken, boolean hasParentParser)
   {
      // interrupt at the end of the document
      if (javaScriptNode == null)
         return currentToken;
      
      String nodeContent = Node.getContent(javaScriptNode).trim(); // returns text without ended space " " in the text
      String nodeType = Node.getType(javaScriptNode);       
      
      // recognize "<?" open tag within the TEXT_HTML content
      if (isPhpOpenNode(nodeType, nodeContent) && MimeType.TEXT_HTML.equals(currentContentMimeType))
      {
         TokenBeenImpl newToken = new TokenBeenImpl("php code", TokenType.PHP_TAG, lineNumber, MimeType.APPLICATION_PHP);
         if (currentToken != null)
         {
            currentToken.addSubToken(newToken);
         }
         currentToken = newToken;

         currentContentMimeType = MimeType.APPLICATION_PHP;
      }

      // recognize "?>" close tag
      else if (isPhpCloseNode(nodeType, nodeContent) && !MimeType.TEXT_HTML.equals(currentContentMimeType))
      {
         currentToken = XmlParser.closeTag(lineNumber, currentToken);

         currentContentMimeType = MimeType.TEXT_HTML;
         CodeMirrorParserImpl.getParser(currentContentMimeType).init();
         javaScriptNode = Node.getNext(javaScriptNode); // pass parsed node
      } 

      if (!currentContentMimeType.equals(MimeType.APPLICATION_PHP))
      {
         currentToken = CodeMirrorParserImpl.getParser(currentContentMimeType).parseLine(javaScriptNode, lineNumber, currentToken, true);  // call child parser
      }
      else
      {
         // parse php code
         nodeStack.push(new Node(javaScriptNode));
         
         TokenBeenImpl newToken;

         // recognize class declaration like "final class testClass extends parentClass {..." or "class testClass \n {..." 
         if ((newToken = isClassDeclaration((Stack<Node>) nodeStack.clone(), lineNumber)) != null)
         {
            addSubToken(currentToken, newToken);
            currentToken = newToken;
         }
         
         // recognize function declaration like "public static function a(arg1, ...) .. {"         
         else if ((newToken = isFunctionDeclaration((Stack<Node>) nodeStack.clone(), lineNumber, currentToken.getType())) != null)
         {
            addSubToken(currentToken, newToken);
            currentToken = newToken;
         }
         
         // recognize open brace "{"
         else if (isOpenBrace(nodeStack.lastElement()))
         {
            enclosers.push(TokenType.BLOCK);
         }
               
         // recognize close brace "}"      
         else if (isCloseBrace(nodeStack.lastElement()))
         {         
            if (! enclosers.isEmpty())
            {
               if (TokenType.CLASS.equals(enclosers.lastElement())
                     || TokenType.FUNCTION.equals(enclosers.lastElement())
                     || TokenType.METHOD.equals(enclosers.lastElement())
                   )
               {
                  currentToken = closeToken(lineNumber, currentToken);
               }
               
               enclosers.pop();
            }
         }
         
         
      }
      
      if (javaScriptNode == null || Node.getName(javaScriptNode).equals("BR")) 
      {
         return currentToken;
      }
         
      return parseLine(Node.getNext(javaScriptNode), lineNumber, currentToken, false);  // call itself
   }

   /**
    * Recognize "<?" or "<?php" node.
    * @param nodeType
    * @param nodeContent
    * @return
    */
   private boolean isPhpOpenNode(String nodeType, String nodeContent)
   {
      return "xml-processing".equals(nodeType) 
               && ("&lt;?".equals(nodeContent)
                    || "&lt;?php".equals(nodeContent)
                  );
   }

   /**
    * Recognize "?>" node. 
    * @param nodeType
    * @param nodeContent
    * @return
    */
   private boolean isPhpCloseNode(String nodeType, String nodeContent)
   {
      return "xml-processing".equals(nodeType) && "?&gt;".equals(nodeContent);
   };
   
   /**
    * Return true if there is "class className" pattern at the top of nodeStack
    * @param nodeStack
    * @return
    */
   private boolean isClassName(Stack<Node> nodeStack)
   {
      if (nodeStack.size() > 1
               && isClassNode(nodeStack.get(nodeStack.size() - 2))
               && isPhpElementName(nodeStack.lastElement().getType())
          )
      {
         return true;
      }

      return false;
   }
   
   /**
    * Recognize "{"
    * @return true if there is open braces of method definition
    */
   private boolean isOpenBrace(Node node)
   {
      return "php-punctuation".equals(node.getType()) && "{".equals(node.getContent());
   }

   /**
    * Recognize "}"
    */
   private boolean isCloseBrace(Node node)
   {
      return "php-punctuation".equals(node.getType()) && "}".equals(node.getContent());
   }
   
   /**
    * Recognize class declaration like "final class testClass extends parentClass {..." or "class testClass \n {..." 
    * @param non-safe nodeStack
    * @param lineNumber
    * @return new class token
    */
   private TokenBeenImpl isClassDeclaration(Stack<Node> nodeStack, int lineNumber)
   {
      if (nodeStack.size() > 3)
      {
         if (isOpenBrace(nodeStack.pop()))
         {
            // pass BR or whitespace between "className ....  {"
            while (nodeStack.size() > 2) {
               // decrease line number if there is BR node between "className ....  {"
               if (nodeStack.lastElement().isLineBreak())
               {
                  lineNumber--;
               }
               
               // break if there is non-BR or non-whitespace node between "className ....  {"
               else if (!nodeStack.lastElement().isLineBreak() 
                        && !isWhitespace(nodeStack.lastElement())
                       )
               {
                  break;
               }
               
               nodeStack.pop();
            }
            
            // parse class name and class modifiers
            while (nodeStack.size() > 1) {
               // test if this is class name
               if (isClassName(nodeStack))
               {
                  TokenBeenImpl newToken = new TokenBeenImpl(nodeStack.lastElement().getContent(), TokenType.CLASS, lineNumber, MimeType.APPLICATION_PHP);                  
                  
                  // remove "class className" nodes
                  nodeStack.setSize(nodeStack.size() - 2);

                  checkModifiers(newToken, nodeStack);
                  
                  return newToken;
               }
               
               nodeStack.pop();
            }
         } 
      }
    
      return null;
   }   
   
   /**
    * Check modifiers like final, abstract, and then update "modifiers" property of token
    * @param token
    * @param nodeStack
    */
   private void checkModifiers(TokenBeenImpl token, Stack<Node> nodeStack)
   {
      LinkedList<Modifier> modifiers = new LinkedList<Modifier>();
      
      Modifier modifier;
      
      while (nodeStack.size() > 0) {
         if ((modifier = isModifier(nodeStack.lastElement())) != null)
         {
            modifiers.add(modifier);
            nodeStack.pop();
         }
         else
         {
            break;
         }
      }
      
      if (!modifiers.isEmpty())
      {
         token.setModifiers(modifiers);
      }    
   }

   /**
    * Check on 'abstract', 'final', 'private', 'protected', 'public', 'static' modifiers
    * @param lastNode
    * @return
    */
   private Modifier isModifier(Node lastNode)
   {
      if ("php-keyword".equals(lastNode.getType())
             && ("abstract".equals(lastNode.getContent())
                 || "final".equals(lastNode.getContent())
                 || "private".equals(lastNode.getContent())
                 || "protested".equals(lastNode.getContent())
                 || "public".equals(lastNode.getContent())
                 || "static".equals(lastNode.getContent())
             )
         )
      {
         // trying to get found java modifier from Modifier enum and add this modifier into the 'modifiers' property 
         try
         {
            return Modifier.valueOf(lastNode.getContent().toUpperCase()); 
         }
         catch(IllegalArgumentException ex)
         {
         }
      }
      
      return null;
   }

   /**
    * Recognize function declaration like "public static function a(arg1, ...) .. {" and return its token with preset modifiers and parameters
    * @param lineNumber 
    * @param non-safe nodeStack
    * @return new function token
    */
   private TokenBeenImpl isFunctionDeclaration(Stack<Node> nodeStack, int lineNumber, TokenType currentTokenType)
   {
      if (nodeStack.size() > 4)
      {
         if (isOpenBrace(nodeStack.pop()))
         {
            // pass BR or whitespace between ") ....  {"
            while (nodeStack.size() > 3) {
               Node node = nodeStack.pop();
               if (isCloseBracket(node))
               {
                  break;
               }
               
               // decrease line number if there is BR node between ") ....  {"
               else if (node.isLineBreak())
               {
                  lineNumber--;
               }
               // return if there is non-BR or non-whitespace node between ") ....  {"
               else if (!(node.isLineBreak() || isWhitespace(node)))
               {
                  return null;
               }
            }

            // read function parameters
            List<TokenBeenImpl> parameters = readFunctionParameters(nodeStack, lineNumber);
            
            // parse class name and class modifiers
            while (nodeStack.size() > 1) {
               // test if this is class name
               if (isFunctionName(nodeStack))
               {
                  TokenBeenImpl newToken = new TokenBeenImpl(nodeStack.lastElement().getContent(), TokenType.FUNCTION, lineNumber, MimeType.APPLICATION_PHP);
             
                  if (TokenType.CLASS.equals(currentTokenType))
                  {
                     // replace FUNCTION on METHOD
                     newToken.setType(TokenType.METHOD);
                  }
                  
                  // remove "function funcName" nodes
                  nodeStack.setSize(nodeStack.size() - 2);

                  checkModifiers(newToken, nodeStack);

                  if ((parameters != null) && !parameters.isEmpty())
                  {
                     newToken.setParameters(parameters);
                  }
                  
                  return newToken;
               }
               
               nodeStack.pop();
            }
         } 
      }
      
      return null;
   }
   
   /**
    * Return true if there is "function functionName" pattern at the top of nodeStack
    * @param nodeStack
    * @return
    */
   private boolean isFunctionName(Stack<Node> nodeStack)
   {
      if (nodeStack.size() > 1
               && isFunctionNode(nodeStack.get(nodeStack.size() - 2))
               && isPhpElementName(nodeStack.lastElement().getType())
          )
      {
         return true;
      }

      return false;
   }

   /**
    * Recognize parameters like '(util_FilePath $filename, $x, $types = array(), $coffeeMaker = NULL'.
    * Return empty list if there are no parameters or null, if there is syntax error.
    * @param non-safe nodeStack
    * @return parameters list
    */
   private List<TokenBeenImpl> readFunctionParameters(Stack<Node> nodeStack, int lineNumber)
   {
      List<TokenBeenImpl> parameters = new LinkedList<TokenBeenImpl>();
      
      // to ignore brackets from parameter initialization code like $types = array() 
      boolean ignoreNextOpenBracket = false;
      
      while (nodeStack.size() > 0) {
         Node node = nodeStack.pop();

         if (isSyntaxError(node.getType()))
         {
            return null;
         }
         
         // recognize close bracket ")"
         if (isCloseBracket(node))
         {
            ignoreNextOpenBracket = true;
         }
         
         // recognize open bracket "("
         else if (isOpenBracket(node))
         {
            if (!ignoreNextOpenBracket)
            {
               break;
            }
            
            ignoreNextOpenBracket = false;
         }
         
         else if (isVariable(node.getType()))
         {
            parameters.add(new TokenBeenImpl(node.getContent(), TokenType.PARAMETER, lineNumber, MimeType.APPLICATION_PHP));
         }
      }
      
      return parameters;
   }

   /**
    * Recognize PHP variable node with class name "php-variable"
    * @param nodeType
    * @return
    */
   private boolean isVariable(String nodeType)
   {
      return "php-variable".equals(nodeType);
   }

   /**
    * Recognize open brackets "(" 
    * @param node 
    * @return
    */
   private boolean isOpenBracket(Node node)
   {
      return "php-punctuation".equals(node.getType()) && "(".equals(node.getContent());
   }

   /**
    * Recognize open brackets ")" 
    * @param node
    * @return
    */
   private boolean isCloseBracket(Node node)
   {
      return "php-punctuation".equals(node.getType()) && ")".equals(node.getContent());
   }

   /**
    * 
    * @param node
    * @return true if this is whitespace node
    */
   private boolean isWhitespace(Node node)
   {
      return "whitespace".equals(node.getType());
   }
   
   private boolean isClassNode(Node node)
   {
      return "php-keyword".equals(node.getType()) && "class".equals(node.getContent());
   }

   private boolean isFunctionNode(Node node)
   {
      return "php-keyword".equals(node.getType()) && "function".equals(node.getContent());
   }
   
   /**
    * Verify name of class, or function, or method
    * @param nodeContent
    * @return
    */
   private boolean isPhpElementName(String nodeType)
   {
      return "php-t_string".equals(nodeType);
   }
     
   /**
    * Set lineNumber to newToken, add newToken as subToken of currentToken, clear nodeStack.
    * @param lineNumber
    * @param currentToken
    * @param newToken
    */
   private void addSubToken(int lineNumber, TokenBeenImpl currentToken, TokenBeenImpl newToken)
   {
      newToken.setLineNumber(lineNumber);
      addSubToken(currentToken, newToken);
   }   
   
   /**
    * Add newToken as subToken of currentToken, clear nodeStack.
    * @param currentToken
    * @param newToken
    */
   private void addSubToken(TokenBeenImpl currentToken, TokenBeenImpl newToken)
   {
      enclosers.push(newToken.getType());
      currentToken.addSubToken(newToken);
      nodeStack.clear();
   }
   
   /**
    * Set lastLineNumber property, clear nodeStack
    * @param lineNumber
    * @param currentToken
    * @return parent token of currentToken
    */
   private TokenBeenImpl closeToken(int lineNumber, TokenBeenImpl currentToken)
   {
//      clearVariables(currentToken.getType());
      currentToken.setLastLineNumber(lineNumber);
      nodeStack.clear();
      return currentToken.getParentToken();
   }
   
   /**
    * Return true, if node has class which contains " syntax-error" keyword
    * @param nodeType
    * @return
    */
   private boolean isSyntaxError(String nodeType)
   {
      return (nodeType != null) && nodeType.endsWith(" syntax-error");
   }
}