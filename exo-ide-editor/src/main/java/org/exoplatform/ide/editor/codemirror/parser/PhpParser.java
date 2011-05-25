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

import java.util.HashMap;
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

   private HashMap<TokenType, LinkedList<String>> variables = new HashMap<TokenType, LinkedList<String>>();
   
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
      enclosers.clear();
      
      // initialize variable lists
      variables.put(TokenType.PHP_TAG, new LinkedList<String>());
      variables.put(TokenType.CLASS, new LinkedList<String>());       
      variables.put(TokenType.METHOD, new LinkedList<String>()); 
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
            enclosers.push(TokenType.CLASS);
         }
         
         // recognize function/method declaration like "public static function a(arg1, ...) .. {"         
         else if ((newToken = isFunctionDeclaration((Stack<Node>) nodeStack.clone(), lineNumber, currentToken.getType())) != null)
         {
            addSubToken(currentToken, newToken);
            currentToken = newToken;
            enclosers.push(newToken.getType());
         }

         // recognize first variable and property declaration like "$a = True;", "private $p1;" or "private $p1 = 2;"
         else if ((newToken = isFirstVariableDefinition((Stack<Node>) nodeStack.clone(), currentToken)) != null)
         {
            addSubToken(lineNumber, currentToken, newToken);
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
    * Recognize first field declaration like "$a;", "private $a", 
    * or field or variable declaration like "$a = 1;", "private $a = True". 
    * @param non-safe node stack
    * @param currentToken
    * @return
    */
   private TokenBeenImpl isFirstVariableDefinition(Stack<Node> nodeStack, TokenBeenImpl currentToken)
   {
      TokenBeenImpl newToken = null;
      
      if (nodeStack.size() > 1)
      {         
         if (isSemicolonNode(nodeStack.pop()))
         {            
            
            // recognize field declaration like "$a;"
            if (TokenType.CLASS.equals(currentToken.getType())
                     && isVariable(nodeStack.lastElement().getType())
                     && isFirstVariableOccurance(currentToken, nodeStack.lastElement().getContent())                        
                )
            {  
                 newToken = new TokenBeenImpl(nodeStack.lastElement().getContent(), TokenType.PROPERTY, 0, MimeType.APPLICATION_PHP);  
            }
            
            // recognize field or variable declaration like "$a = 1;"
            else if (nodeStack.size() > 2)
            {
            
               Node lastNode = nodeStack.pop();
               
               String possibleElementType = null;
               // test if this is string value
               if (isString(lastNode.getType()))
               {
                  possibleElementType = "String";
               }
   
               // recognize variable assignment statement like "$a ="               
               if (nodeStack.size() > 1)
               {
                  if (isEqualSign(nodeStack.get(nodeStack.size() - 1))
                           && isVariable(nodeStack.get(nodeStack.size() - 2).getType())
                           && isFirstVariableOccurance(currentToken, nodeStack.get(nodeStack.size() - 2).getContent())
                     )
                  {   
                     String variableName = nodeStack.get(nodeStack.size() - 2).getContent();
                     
                     newToken = new TokenBeenImpl(variableName, TokenType.VARIABLE, 0, MimeType.APPLICATION_PHP);
   
                     if (possibleElementType != null)
                     {
                        newToken.setElementType(possibleElementType);
                     }
                     
                     // replace VARIABLE on PROPERTY
                     if (TokenType.CLASS.equals(currentToken.getType()))
                     {
                        newToken.setType(TokenType.PROPERTY);
                     }
                  }
               }
            }

            if (newToken != null)
            {
               updateVariableList(currentToken, newToken.getName());
               
               // remove "$a =" nodes
               nodeStack.setSize(nodeStack.size() - 2);
   
               checkModifiers(newToken, nodeStack);
            }
         } 
      }
            
      return newToken;
   }

   private boolean isFirstVariableOccurance(TokenBeenImpl currentToken, String variableName)
   {
      // find variable in the toplevel local variable list
      if (TokenType.PHP_TAG.equals(currentToken.getType()))
      {
         if (variables.get(TokenType.PHP_TAG).contains(variableName))
               return false;
      }

      // find variable in the method's local variable list
      if (TokenType.METHOD.equals(currentToken.getType()))
      {
         if (variables.get(TokenType.METHOD).contains(variableName))
            return false;
      }       
      
      // find variable in the class's local variable list
      else if (TokenType.CLASS.equals(currentToken.getType()))
      {
         if (variables.get(TokenType.CLASS).contains(variableName))
            return false;
      }
      
      return true;
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
      return isPunctuation(node) && "{".equals(node.getContent());
   }

   /**
    * Recognize "}"
    */
   private boolean isCloseBrace(Node node)
   {
      return isPunctuation(node) && "}".equals(node.getContent());
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
      if (isKeyword(lastNode)
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
      return (nodeType != null) && (nodeType.startsWith("php-variable"));
   }

   /**
    * Recognize open brackets "(" 
    * @param node 
    * @return
    */
   private boolean isOpenBracket(Node node)
   {
      return isPunctuation(node) && "(".equals(node.getContent());
   }

   /**
    * Recognize open brackets ")" 
    * @param node
    * @return
    */
   private boolean isCloseBracket(Node node)
   {
      return isPunctuation(node) && ")".equals(node.getContent());
   }

   private boolean isPunctuation(Node node)
   {
      return (node.getType() != null) && (node.getType().startsWith("php-punctuation"));
   }

   /**
    * 
    * @param node
    * @return true if this is whitespace node
    */
   private boolean isWhitespace(Node node)
   {
      return (node.getType() != null) && (node.getType().startsWith("whitespace"));
   }
   
   private boolean isClassNode(Node node)
   {
      return isKeyword(node) && "class".equals(node.getContent());
   }

   private boolean isFunctionNode(Node node)
   {
      return isKeyword(node) && "function".equals(node.getContent());
   }

   private boolean isKeyword(Node node)
   {
      return (node.getType() != null) && (node.getType().startsWith("php-keyword"));
   }
   
   /**
    * Verify name of class, or function, or method
    * @param nodeContent
    * @return
    */
   private boolean isPhpElementName(String nodeType)
   {
      return (nodeType != null) && (nodeType.startsWith("php-t_string"));
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
      currentToken.addSubToken(newToken);
      nodeStack.clear();
   }
   
   /**
    * Clear context variables, set lastLineNumber property, clear nodeStack
    * @param lineNumber
    * @param currentToken
    * @return parent token of currentToken
    */
   private TokenBeenImpl closeToken(int lineNumber, TokenBeenImpl currentToken)
   {
      clearVariables(currentToken.getType());
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
   
   /**
    * Recognize ";" node
    * @param node
    * @return
    */
   private boolean isSemicolonNode(Node node)
   {
      return (node.getType() != null) && (node.getType().startsWith("php-punctuation")) && ";".equals(node.getContent());
   };
   
   /**
    * @param type
    * @return true if this is string value
    */
   private boolean isString(String nodeType)
   {
      return (nodeType != null) && nodeType.startsWith("php-string");
   }
   
   /**
    * Recognize "=" operation
    * @param node
    * @return
    */
   private boolean isEqualSign(Node node)
   {
      return "php-operator".equals(node.getType()) && "=".equals(node.getContent());
   }
   
   /**
    * Clear variable list within the method, or module, or class
    * @param currentTokenType
    */
   private void clearVariables(TokenType tokenType)
   {
      switch (tokenType)
      {
         case CLASS:
            variables.get(TokenType.CLASS).clear();
            
            break;
            
         case METHOD:
            variables.get(TokenType.METHOD).clear();
            break;
            
         default:
      }
   }
   
   private void updateVariableList(TokenBeenImpl currentToken, String variableName)
   {
      // update toplevel variable list
      if (TokenType.PHP_TAG.equals(currentToken.getType()))
      {
         variables.get(TokenType.PHP_TAG).add(variableName);
      }
      
      // update method's local variable list
      else if (TokenType.METHOD.equals(currentToken.getType()))
      {
         variables.get(TokenType.METHOD).add(variableName);
      }
      
      // update class's local variable list
      else if (TokenType.CLASS.equals(currentToken.getType()))
      {
         variables.get(TokenType.CLASS).add(variableName);
      }
   }
}