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

import java.util.Stack;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.codemirror.Node;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: $
 *
 */
public class RubyParser extends CodeMirrorParserImpl
{
   private Stack<Node> nodeStack = new Stack<Node>();
   
   /**
    * Stack of blocks "def ... end" etc.
    */
   private Stack<TokenType> enclosers = new Stack<TokenType>();
      
   @Override
   public void init()
   {
      super.init();

      nodeStack.clear();
   }

   @Override
   TokenBeenImpl parseLine(JavaScriptObject javaScriptNode, int lineNumber, TokenBeenImpl currentToken, boolean hasParentParser)
   {
      // interrupt at the end of content
      if (javaScriptNode == null || Node.getName(javaScriptNode).equals("BR"))
      {
         return currentToken;
      }
//      
//      // interrupt at the end of the line
//      else if (Node.getName(javaScriptNode).equals("BR"))
//      {
//         nodeStack.push(new Node("BR", ""));
//      }
      
      else
      {
         nodeStack.push(new Node(javaScriptNode));
      }
      
      TokenBeenImpl newToken;

      // verify on class name 
      if (isClassName(nodeStack))
      {
         currentToken = addSubToken(lineNumber, currentToken, TokenType.CLASS);
      }

      // verify on module name 
      else if (isModuleName(nodeStack))
      {
         currentToken = addSubToken(lineNumber, currentToken, TokenType.MODULE);
      }      
      
      // verify on method name 
      else if (isMethodName(nodeStack))
      {
         currentToken = addSubToken(lineNumber, currentToken, TokenType.METHOD);
      }
      
      else if (isBlockNode(nodeStack.lastElement()))
      {
         enclosers.push(TokenType.BLOCK);         
      }
      
      // verify on "end" node
      else if (isEndNode(nodeStack.lastElement()))
      {
         // to filter block nodes like "if ... end"
         if (!enclosers.empty() && !enclosers.lastElement().equals(TokenType.BLOCK))
         {        
            currentToken = closeToken(lineNumber, currentToken);
         }
         else
         {
            enclosers.pop();
            nodeStack.clear();
         }
      }
      
      // recognize "("
      else if (isOpenBracket(nodeStack.lastElement()))
      {        
      }

      // recognize ")"      
      else if (isCloseBracket(nodeStack.lastElement()))
      {  
      }
     
      // recognize open brace "{"
      else if (isOpenBrace(nodeStack.lastElement()))
      {        
      }
      
      // recognize close brace "}"      
      else if (isCloseBrace(nodeStack.lastElement()))
      {                 
      }
 

      return parseLine(Node.getNext(javaScriptNode), lineNumber, currentToken, false);
   }

   private TokenBeenImpl addSubToken(int lineNumber, TokenBeenImpl currentToken, TokenType tokenType)
   {
      TokenBeenImpl newToken = new TokenBeenImpl(nodeStack.lastElement().getContent(), tokenType, lineNumber, MimeType.APPLICATION_RUBY);
      currentToken.addSubToken(newToken);
      enclosers.push(tokenType);
      nodeStack.clear();
      return newToken;
   }

   private TokenBeenImpl closeToken(int lineNumber, TokenBeenImpl currentToken)
   {
      currentToken.setLastLineNumber(lineNumber);
      enclosers.pop();
      nodeStack.clear();
      return currentToken.getParentToken();
   }
   
   private boolean isClassName(Stack<Node> nodeStack)
   {
      if (nodeStack.size() > 1
               && isClassNode(nodeStack.get(nodeStack.size() - 2))
               && isConstant(nodeStack.lastElement())
          )
      {
         return true;
      }

      return false;
   }

   private boolean isModuleName(Stack<Node> nodeStack)
   {
      if (nodeStack.size() > 1
               && isModuleNode(nodeStack.get(nodeStack.size() - 2))
               && isMethodNode(nodeStack.lastElement().getType())
          )
      {
         return true;
      }

      return false;
   }   
   
   private boolean isMethodName(Stack<Node> nodeStack)
   {
      if (nodeStack.size() > 1
               && isDefNode(nodeStack.get(nodeStack.size() - 2))
               && isMethodNode(nodeStack.lastElement().getType())
          )
      {
         return true;
      }

      return false;
   }

   /**
    * Recognize block node keywords like "case", "if", "unless", "do", "begin"
    * @param node
    * @return
    */
   private boolean isBlockNode(Node node)
   {
      return isKeyword(node.getType()) &&
                (
                    "case".equals(node.getContent())
                    || "if".equals(node.getContent())
                    || "unless".equals(node.getContent())
                    || "do".equals(node.getContent())  // it is mean "while", "until", "for" keywords 
                    || "begin".equals(node.getContent())                   
                );
   }
   
   /**
    * Recognize ruby method name type "rb-method rb-methodname"
    * @param nodeType
    * @return
    */
   private boolean isMethodNode(String nodeType)
   {
      return "rb-method rb-methodname".equals(nodeType) || "rb-method".equals(nodeType); 
   }   
   
   /**
    * Recognize ruby constant name and class name
    * @param node
    * @return
    */
   private boolean isConstant(Node node)
   {
      return "rb-constant".equals(node.getType());
   }
   
   /**
    * Recognize block closer "end"
    * @param node
    * @return
    */
   private boolean isEndNode(Node node)
   {
      return isKeyword(node.getType()) && "end".equals(node.getContent()); 
   }
   
   /**
    * Recognize ruby keywords 'begin', 'class', 'ensure', 'nil', 'self', 'when', 'end', 'def', 'false', 'not', 'super', 'while', 'alias', 'defined', 'for', 'or', 'then', 'yield', 'and', 'do', 'if', 'redo', 'true', 'begin', 'else', 'in', 'rescue', 'undef', 'break', 'elsif', 'module', 'retry', 'unless', 'case', 'end', 'next', 'return', 'until'
    * @param nodeType
    * @return
    */
   private boolean isKeyword(String nodeType)
   {
      return "rb-keyword".equals(nodeType);
   }

   /**
    * Recognize 'private' keyword
    * @param node
    * @return
    */
   private boolean isPrivateMethod(String nodeType, String nodeContent)
   {
      return "rb-method".equals(nodeType) && "private".equals(nodeContent); 
   }   

   /**
    * Recognize 'protected' keyword
    * @param node
    * @return
    */
   private boolean isProtectedMethod(String nodeType, String nodeContent)
   {
      return "rb-method".equals(nodeType) && "protected".equals(nodeContent); 
   }   
      
   /**
    * Recognize 'public' keyword
    * @param node
    * @return
    */
   private boolean isPublicMethod(String nodeType, String nodeContent)
   {
      return "rb-method".equals(nodeType) && "public".equals(nodeContent); 
   }   
   
   /**
    * Recognize sign ","
    * @param node
    * @return
    */
   private boolean isComma(Node node)
   {
      return "rb-normal".equals(node.getType()) && ",".equals(node.getContent());
   }

   /**
    * Recognize "{"
    * @return true if there is open braces of method definition
    */
   private boolean isOpenBrace(Node node)
   {
      return "rb-normal".equals(node.getType()) && "{".equals(node.getContent());
   }

   /**
    * Recognize "}"
    */
   private boolean isCloseBrace(Node node)
   {
      return "rb-normal".equals(node.getType()) && "}".equals(node.getContent());
   }   

   /**
    * Recognize "=" operation
    * @param node
    * @return
    */
   private boolean isEqualSign(Node node)
   {
      return "rb-operator".equals(node.getType()) && "=".equals(node.getContent());
   }

   /**
    * Recognize open brackets "(" 
    * @param node
    * @return
    */
   private boolean isOpenBracket(Node node)
   {
      return "rb-normal".equals(node.getType()) && "(".equals(node.getContent());
   }

   /**
    * Recognize close brackets ")" 
    * @param node 
    * @return
    */
   private boolean isCloseBracket(Node node)
   {
      return "rb-normal".equals(node.getType()) && ")".equals(node.getContent());
   }

   private boolean isClassNode(Node node)
   {
      return "rb-keyword".equals(node.getType()) && "class".equals(node.getContent());
   }

   private boolean isModuleNode(Node node)
   {
      return "rb-keyword".equals(node.getType()) && "module".equals(node.getContent());
   }   
   
   /**
    * Recognize "def varname"
    * @param node
    * @return
    */
   private boolean isDefNode(Node node)
   {
      return "rb-keyword".equals(node.getType()) && "def".equals(node.getContent());
   }

   /**
    * @param nodeType
    * @return <b>true</b> only if this is the nodeType = 'whitespace'
    */
   private boolean isWhitespace(Node node)
   {
      return "whitespace".equals(node.getType());
   }
}