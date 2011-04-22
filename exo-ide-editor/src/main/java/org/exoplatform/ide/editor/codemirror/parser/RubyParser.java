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
      if (javaScriptNode == null)
      {
         return currentToken;
      }
      
      // interrupt at the end of the line
      else if (Node.getName(javaScriptNode).equals("BR"))
      {
         nodeStack.push(new Node("BR", ""));
      }
      
      else
      {
         nodeStack.push(new Node(javaScriptNode));
      }
      
      verifyClassName(nodeStack, enclosers, currentToken, lineNumber);
    
      verifyEndNode(nodeStack, enclosers, currentToken, lineNumber); 
      
      // recognize "("
      if (isOpenBracket(nodeStack.lastElement()))
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

   private void verifyClassName(Stack<Node> nodeStack, Stack<TokenType> enclosers, TokenBeenImpl currentToken, int lineNumber)
   {
      if (nodeStack.size() > 1
               && isClassNode(nodeStack.get(nodeStack.size() - 2))
               && isConstant(nodeStack.lastElement())
          )
      {
         TokenBeenImpl newToken = new TokenBeenImpl(nodeStack.lastElement().getContent(), TokenType.CLASS, lineNumber, MimeType.APPLICATION_RUBY);
         
         currentToken.addSubToken(newToken);            
         currentToken = newToken;
         
         enclosers.push(TokenType.CLASS);
      }

   }
   
   private void verifyEndNode(Stack<Node> nodeStack, Stack<TokenType> enclosers, TokenBeenImpl currentToken, int lineNumber)
   {
      if (nodeStack.size() > 1 && ! enclosers.isEmpty()
               && isEndNode(nodeStack.lastElement())
          )
      {
         enclosers.pop();
         
         // close token
         currentToken.setLastLineNumber(lineNumber);         
         currentToken = currentToken.getParentToken();  
      }
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
      return isKeyword(node) && "end".equals(node.getContent()); 
   }
   
   /**
    * Recognize ruby keywords 'begin', 'class', 'ensure', 'nil', 'self', 'when', 'end', 'def', 'false', 'not', 'super', 'while', 'alias', 'defined', 'for', 'or', 'then', 'yield', 'and', 'do', 'if', 'redo', 'true', 'begin', 'else', 'in', 'rescue', 'undef', 'break', 'elsif', 'module', 'retry', 'unless', 'case', 'end', 'next', 'return', 'until'
    * @param nodeType
    * @return
    */
   private boolean isKeyword(Node node)
   {
      return "rb-keyword".equals(node.getType());
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

   /**
    * Recognize "def varname"
    * @param node
    * @return
    */
   private boolean isDef(Node node)
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