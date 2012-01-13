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
package org.exoplatform.ide.editor.javascript.client.codemirror;

import java.util.Stack;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.codemirror.CodeMirrorParserImpl;
import org.exoplatform.ide.editor.codemirror.Node;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: $
 * 
 */
public class JavaScriptParser extends CodeMirrorParserImpl
{

   private Stack<Node> nodeStack = new Stack<Node>();

   /**
    * Stack of blocks "{... {...} ...}"
    */
   private Stack<TokenType> enclosers = new Stack<TokenType>();

   @Override
   public void init()
   {
      super.init();

      nodeStack.clear();
   }

   @Override
   public TokenBeenImpl parseLine(JavaScriptObject javaScriptNode, int lineNumber, TokenBeenImpl currentToken,
      boolean hasParentParser)
   {
      // interrupt at the end of content
      if (javaScriptNode == null)
      {
         return currentToken;
      }

      nodeStack.push(new Node(javaScriptNode));

      Stack<Node> cloneNodeStack = null;
      TokenBeenImpl newToken = null;

      // recognize ended line break or ";" or "// ....." or whitespace
      if (nodeStack.size() > 1
         && (nodeStack.lastElement().isLineBreak() || isSemicolonNode(nodeStack.lastElement())
            || isJsComment(nodeStack.lastElement()) || isWhitespace(nodeStack.lastElement())))
      {
         cloneNodeStack = (Stack<Node>)nodeStack.clone();
         cloneNodeStack.setSize(cloneNodeStack.size() - 1);

         // to recognize "var a/n" or "var a;" or "var a "
         if ((newToken = isVariableWithoutAssignmentStatement(cloneNodeStack)) != null)
         {
            addSubToken(lineNumber, currentToken, newToken);
         }

         // to recognize reference variable like "var h = window;" or "var h = window /n"
         else if ((newToken = isVariableWithReferenceValue(cloneNodeStack)) != null)
         {
            addSubToken(lineNumber, currentToken, newToken);
         }

         // to recognize variable declaration like "var h = 11;" or "var h = 11 /n"
         else if ((newToken = isVariableWithAtomicValue(cloneNodeStack)) != null)
         {
            addSubToken(lineNumber, currentToken, newToken);
         }
      }

      // to recognize function definition like "var a = function() {"
      else if ((newToken = isVariableWithFunctionAssignmentStatement((Stack<Node>)nodeStack.clone(), lineNumber)) != null)
      {
         enclosers.push(TokenType.FUNCTION);
         addSubToken(currentToken, newToken);
         currentToken = currentToken.getLastSubToken();
      }

      // to recognize variable declaration like "var h = ["
      else if ((newToken = isVariableWithArrayValue((Stack<Node>)nodeStack.clone())) != null)
      {
         addSubToken(lineNumber, currentToken, newToken);
      }

      // recognize open bracket "("
      else if (nodeStack.size() > 4 && isOpenBracket(nodeStack.lastElement()))
      {
         cloneNodeStack = (Stack<Node>)nodeStack.clone();
         cloneNodeStack.setSize(cloneNodeStack.size() - 1);

         // to recognize variable like "var h = window.document.getElementById('test')"
         if ((newToken = isVariableWithReferenceValue((Stack<Node>)cloneNodeStack.clone())) != null)
         {
            newToken.setInitializationStatement(newToken.getInitializationStatement() + "()");
            addSubToken(lineNumber, currentToken, newToken);
         }

         // to recognize object creation like "var a = new UWA.Data()"
         else if ((newToken = isObjectCreation((Stack<Node>)cloneNodeStack.clone())) != null)
         {
            addSubToken(lineNumber, currentToken, newToken);
         }
      }

      // recognize open brace "{"
      else if (isOpenBrace(nodeStack.lastElement()))
      {
         if ((newToken = isFunctionStatement((Stack<Node>)nodeStack.clone(), lineNumber)) != null)
         {
            enclosers.push(TokenType.FUNCTION);
            addSubToken(currentToken, newToken);
            currentToken = newToken;
         }

         // to recognize anonymous function definition like "function() {"
         else if (isAnonymousFunctionStatement((Stack<Node>)nodeStack.clone()) != -1)
         {
            enclosers.push(TokenType.FUNCTION);

            newToken = new TokenBeenImpl("function", TokenType.FUNCTION, lineNumber, MimeType.APPLICATION_JAVASCRIPT);
            currentToken.addSubToken(newToken);
            currentToken = newToken;

            nodeStack.clear();
         }

         // to recognize variable declaration like "var i = {"
         else if ((newToken = isVariableWithObjectValue((Stack<Node>)nodeStack.clone())) != null)
         {
            addSubToken(lineNumber, currentToken, newToken);
            enclosers.push(TokenType.BLOCK);
         }

         else
         {
            enclosers.push(TokenType.BLOCK);
         }
      }

      // recognize close brace "}"
      else if (!nodeStack.isEmpty() && isCloseBrace(nodeStack.lastElement()))
      {
         if (!enclosers.isEmpty())
         {
            if (TokenType.FUNCTION.equals(enclosers.lastElement()))
            {
               currentToken.setLastLineNumber(lineNumber);

               if (currentToken.getParentToken() != null)
               {
                  currentToken = currentToken.getParentToken();
               }
            }

            enclosers.pop();
         }
      }

      if (hasParentParser || Node.isLineBreak(javaScriptNode))
      {
         return currentToken; // return current token to parent parser
      }
      else
      {
         return parseLine(Node.getNext(javaScriptNode), lineNumber, currentToken, false);
      }

   }

   /**
    * Recognize JS comment node like "// comment" with type "js-comment"
    * 
    * @param node
    * @return
    */
   private boolean isJsComment(Node node)
   {
      return "js-comment".equals(node.getType());
   }

   /**
    * Add newToken as subToken of currentToken, clear nodeStack.
    * 
    * @param currentToken
    * @param newToken
    */
   private void addSubToken(TokenBeenImpl currentToken, TokenBeenImpl newToken)
   {
      currentToken.addSubToken(newToken);
      nodeStack.clear();
   }

   /**
    * Set lineNumber to newToken, add newToken as subToken of currentToken, clear nodeStack.
    * 
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
    * Recognize js property
    * 
    * @param nodeType
    * @return
    */
   private boolean isJsProperty(String nodeType)
   {
      return "js-property".equals(nodeType);
   }

   /**
    * Recognize object creation like "var a = new UWA.Data()"
    * 
    * @param nodeStack
    * @return token "a" with elementType like "UWA.Data" in case like "var a = new UWA.Data()"
    */
   private TokenBeenImpl isObjectCreation(Stack<Node> nodeStack)
   {
      if (nodeStack.size() > 4)
      {
         String elementType = "";
         // construct initialization statement before "new" keyword like "UWA.Data"
         while (nodeStack.size() > 3)
         {
            Node node = nodeStack.lastElement();
            if (isPoint(node.getType(), node.getContent()) || isJsVariable(node.getType())
               || isJsProperty(node.getType()))
            {
               elementType = node.getContent() + elementType;
               nodeStack.pop();
            }
            else
            {
               break;
            }
         }

         // recognize variable assignment statement like "var a = new"
         TokenBeenImpl newToken;
         if (isNewKeyword(nodeStack.pop()) && nodeStack.size() > 2
            && (newToken = isVariableWithAssignmentStatement(nodeStack)) != null && isJsCorrectExpression(elementType))
         {
            newToken.setElementType(elementType);
            return newToken;
         }
      }

      return null;
   }

   private boolean isVarNode(Node node)
   {
      return "js-keyword".equals(node.getType()) && "var".equals(node.getContent());
   }

   /**
    * Recognize "function" keyword
    * 
    * @param node
    * @return
    */
   private boolean isFunctionNode(Node node)
   {
      return "js-keyword".equals(node.getType()) && "function".equals(node.getContent());
   }

   private boolean isEqualNode(Node node)
   {
      return "js-operator".equals(node.getType()) && "=".equals(node.getContent());
   }

   /**
    * Recognize ";" node
    * 
    * @param node
    * @return
    */
   private boolean isSemicolonNode(Node node)
   {
      return isJsPunctuation(node) && ";".equals(node.getContent());
   };

   /**
    * Recognize variable out of the function
    * 
    * @param nodeType
    * @return
    */
   public static boolean isJsVariable(String nodeType)
   {
      return "js-variable".equals(nodeType);
   }

   /**
    * Recognize local variable definition within the function like "function a() { var b = 1;  }"
    * 
    * @param nodeType
    * @return
    */
   public static boolean isJsLocalVariableDef(String nodeType)
   {
      return "js-variabledef".equals(nodeType);
   }

   /**
    * Recognize local variable within the function like "function a() { b = 1;  }"
    * 
    * @param nodeType
    * @return
    */
   public static boolean isJsLocalVariable(String nodeType)
   {
      return "js-localvariable".equals(nodeType);
   }

   /**
    * Recognize "new" keyword
    * 
    * @param nodeType
    * @param nodeContent
    * @return
    */
   private boolean isNewKeyword(Node node)
   {
      return "js-keyword".equals(node.getType()) && "new".equals(node.getContent());
   };

   /**
    * Recognize "." out of the js string
    * 
    * @return
    */
   public static boolean isPoint(String nodeType, String nodeContent)
   {
      return "js-punctuation".equals(nodeType) && ".".equals(nodeContent);
   }

   /**
    * Recognize "{"
    * 
    * @return true if there is open braces of method definition
    */
   private boolean isOpenBrace(Node node)
   {
      return isJsPunctuation(node) && "{".equals(node.getContent());
   }

   /**
    * Recognize "}"
    */
   private boolean isCloseBrace(Node node)
   {
      return isJsPunctuation(node) && "}".equals(node.getContent());
   }

   /**
    * Recognize function definition like function a(...){ and return its name
    * 
    * @param lineNumber
    * @param non-safe nodeStack
    * @return token "a"
    */
   private TokenBeenImpl isFunctionStatement(Stack<Node> nodeStack, int lineNumber)
   {
      if (nodeStack.size() > 4)
      {
         if (isOpenBrace(nodeStack.pop()))
         {
            // pass BR or whitespace between ") ....  {"
            while (nodeStack.size() > 3)
            {
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

            while (nodeStack.size() > 2)
            {
               // test if this is like "function a ("
               if (isOpenBracket(nodeStack.lastElement()))
               {
                  if ((isJsVariable(nodeStack.get(nodeStack.size() - 2).getType()) || isJsLocalVariableDef(nodeStack
                     .get(nodeStack.size() - 2).getType())) && isFunctionNode(nodeStack.get(nodeStack.size() - 3)))
                  {
                     return new TokenBeenImpl(nodeStack.get(nodeStack.size() - 2).getContent(), TokenType.FUNCTION,
                        lineNumber, MimeType.APPLICATION_JAVASCRIPT);
                  }
               }

               nodeStack.pop();
            }
         }
      }

      return null;
   }

   /**
    * Recognize function definition like "function (...) {" and return index of function node "function"
    * 
    * @param non-safe nodeStack
    * @return index of function node "function"
    */
   private int isAnonymousFunctionStatement(Stack<Node> nodeStack)
   {
      if (nodeStack.size() > 3)
      {
         Stack<Node> initialNodeStack = (Stack<Node>)nodeStack.clone();
         if (isOpenBrace(nodeStack.pop()))
         {
            // pass BR or whitespace between ") ....  {"
            while (nodeStack.size() > 2)
            {
               Node node = nodeStack.pop();
               if (isCloseBracket(node))
               {
                  break;
               }

               // return if there is non-BR or non-whitespace node between ") ....  {"
               else if (!(node.isLineBreak() || isWhitespace(node)))
               {
                  return -1;
               }
            }

            while (nodeStack.size() > 1)
            {
               // test if this is like "function ("
               if (isOpenBracket(nodeStack.lastElement()))
               {
                  if (isFunctionNode(nodeStack.get(nodeStack.size() - 2)))
                  {
                     return initialNodeStack.indexOf(nodeStack.get(nodeStack.size() - 2));
                  }
               }

               nodeStack.pop();
            }
         }
      }

      return -1;
   }

   /**
    * 
    * @param safe nodeStack
    * @return CodeMirrorTokenImpl with variable "a" in case like "var a"
    */
   private TokenBeenImpl isVariableWithoutAssignmentStatement(Stack<Node> nodeStack)
   {
      if (nodeStack.size() > 1)
      {
         Stack<Node> cloneNodeStack = (Stack<Node>)nodeStack.clone();
         if ((isJsVariable(cloneNodeStack.get(cloneNodeStack.size() - 1).getType()) || isJsLocalVariableDef(cloneNodeStack
            .get(cloneNodeStack.size() - 1).getType())) && isVarNode(nodeStack.get(cloneNodeStack.size() - 2)))
         {
            return new TokenBeenImpl(cloneNodeStack.get(cloneNodeStack.size() - 1).getContent(), TokenType.VARIABLE, 0,
               MimeType.APPLICATION_JAVASCRIPT);
         }
      }

      return null;
   }

   /**
    * 
    * @param safe nodeStack
    * @return variable token with variable in case like "var a = " with elementType = "Object"
    */
   private TokenBeenImpl isVariableWithAssignmentStatement(Stack<Node> nodeStack)
   {
      if (nodeStack.size() > 2)
      {
         Stack<Node> cloneNodeStack = (Stack<Node>)nodeStack.clone();
         if (isEqualNode(cloneNodeStack.get(cloneNodeStack.size() - 1))
            && (isJsVariable(cloneNodeStack.get(cloneNodeStack.size() - 2).getType()) || isJsLocalVariableDef(cloneNodeStack
               .get(cloneNodeStack.size() - 2).getType())) && isVarNode(cloneNodeStack.get(nodeStack.size() - 3)))
         {
            TokenBeenImpl newToken =
               new TokenBeenImpl(cloneNodeStack.get(cloneNodeStack.size() - 2).getContent(), TokenType.VARIABLE, 0,
                  MimeType.APPLICATION_JAVASCRIPT);
            newToken.setElementType("Object");
            return newToken;
         }
      }

      return null;
   }

   /**
    * Recognize variable with function assignment like "var a = function(...) {"
    * 
    * @param lineNumber
    * @param non-safe nodeStack
    * @return function token with function like "a()" in case like "var a = function(...) {"
    */
   private TokenBeenImpl isVariableWithFunctionAssignmentStatement(Stack<Node> nodeStack, int lineNumber)
   {
      if (nodeStack.size() > 6)
      {
         Stack<Node> cloneNodeStack = (Stack<Node>)nodeStack.clone();
         // get index of function node "function"
         int indexOfFunctionNode = isAnonymousFunctionStatement(nodeStack);

         if (indexOfFunctionNode > 2)
         {
            nodeStack.setSize(indexOfFunctionNode);

            TokenBeenImpl newToken;
            if ((newToken = isVariableWithAssignmentStatement(nodeStack)) != null)
            {
               newToken.setType(TokenType.FUNCTION);
               newToken.setElementType(null);
               newToken.setName(newToken.getName());
               newToken.setLineNumber(getFirstLineNumber(lineNumber, cloneNodeStack));
               return newToken;
            }
         }
      }

      return null;
   }

   /**
    * 
    * @param lineNumber
    * @param nodeStack
    * @return
    */
   private int getFirstLineNumber(int lineNumber, Stack<Node> nodeStack)
   {
      if (nodeStack.size() > 3)
      {
         Stack<Node> initialNodeStack = (Stack<Node>)nodeStack.clone();
         if (isOpenBrace(nodeStack.pop()))
         {
            // pass BR or whitespace between ") ....  {"
            while (nodeStack.size() > 2)
            {
               Node node = nodeStack.pop();
               if (isCloseBracket(node))
               {
                  break;
               }

               // return if there is non-BR or non-whitespace node between ") ....  {"
               else if (node.isLineBreak())
               {
                  lineNumber--;
               }
            }
         }
      }

      return lineNumber;
   }

   /**
    * Recognize variable with reference value returned from a function "var k = window.document"
    * 
    * @param non-safe nodeStack
    * @return CodeMirrorTokenImpl k with initializationStatement statement like "window.document"
    */
   private TokenBeenImpl isVariableWithReferenceValue(Stack<Node> nodeStack)
   {
      if (nodeStack.size() > 3)
      {
         Stack<Node> cloneNodeStack = (Stack<Node>)nodeStack.clone();
         // construct initialization statement
         String initializationStatement = "";
         while (cloneNodeStack.size() > 2)
         {
            Node node = cloneNodeStack.lastElement();
            if (isPoint(node.getType(), node.getContent()) || isJsVariable(node.getType())
               || isJsProperty(node.getType()))
            {
               initializationStatement = node.getContent() + initializationStatement;
               cloneNodeStack.pop();
            }
            else
            {
               break;
            }
         }

         // recognize variable assignment statement like "var a ="
         TokenBeenImpl newToken;
         if (cloneNodeStack.size() > 2 && (newToken = isVariableWithAssignmentStatement(cloneNodeStack)) != null
            && isJsCorrectExpression(initializationStatement))
         {
            newToken.setInitializationStatement(initializationStatement);
            return newToken;
         }
      }

      return null;
   }

   /**
    * Filter code like "Data..test" or ".Data" or "Data."
    * 
    * @param expression
    * @return
    */
   private boolean isJsCorrectExpression(String expression)
   {
      return !expression.matches("^.*[.]{2,}.*|[.]{1,}.*|.*[.]{1,}$");
   }

   /**
    * Recognize variable with atomic value "var h = 11" like "number", "boolean", "string", null
    * 
    * @param nodeStack
    * @return variable token like "h" with element type from "number", "boolean", "string", "object" set
    */
   private TokenBeenImpl isVariableWithAtomicValue(Stack<Node> nodeStack)
   {
      // nodeStack = clearStartedWhitespaces(nodeStack);
      if (nodeStack.size() > 3)
      {
         // recognize type of assignment
         Stack<Node> cloneNodeStack = (Stack<Node>)nodeStack.clone();
         String possibleElementType = null;
         Node lastNode = cloneNodeStack.pop();
         // test if this is JavaScript number
         if (isJsNumber(lastNode))
         {
            possibleElementType = "Number";
         }

         // test if this is boolean
         else if (isJsBoolean(lastNode))
         {
            possibleElementType = "Boolean";
         }

         // test if this is "null" value
         else if (isJsNull(lastNode))
         {
            possibleElementType = "null";
         }

         // test if this is string value
         else if (isJsString(lastNode))
         {
            possibleElementType = "String";
         }

         // recognize variable assignment statement like "var a ="
         TokenBeenImpl newToken;
         if (cloneNodeStack.size() > 2 && (newToken = isVariableWithAssignmentStatement(cloneNodeStack)) != null
            && possibleElementType != null)
         {
            newToken.setElementType(possibleElementType);
            return newToken;
         }

      }

      return null;
   }

   /**
    * Recognize variable with array value like "var h = ["
    * 
    * @param non-safe nodeStack
    * @return node of variable with array type
    */
   private TokenBeenImpl isVariableWithArrayValue(Stack<Node> nodeStack)
   {
      if (nodeStack.size() > 3)
      {
         // recognize variable assignment statement like "var a ="
         TokenBeenImpl newToken;
         if (isOpenSquareBracket(nodeStack.pop()) && (newToken = isVariableWithAssignmentStatement(nodeStack)) != null)
         {
            newToken.setElementType("Array");
            return newToken;
         }
      }

      return null;
   }

   /**
    * Recognize variable with array value like "var i = {"
    * 
    * @param lineNumber
    * @param non-safe nodeStack
    * @return node of variable with array type
    */
   private TokenBeenImpl isVariableWithObjectValue(Stack<Node> nodeStack)
   {
      if (nodeStack.size() > 3)
      {
         // recognize variable assignment statement like "var a ="
         TokenBeenImpl newToken;
         if (isOpenBrace(nodeStack.pop()) && (newToken = isVariableWithAssignmentStatement(nodeStack)) != null)
         {
            newToken.setElementType("Object");
            return newToken;
         }
      }

      return null;
   }

   /**
    * Recognize open brackets "("
    * 
    * @param node
    * @return
    */
   private boolean isOpenBracket(Node node)
   {
      return isJsPunctuation(node) && "(".equals(node.getContent());
   }

   /**
    * Recognize open brackets ")"
    * 
    * @param node
    * @return
    */
   private boolean isCloseBracket(Node node)
   {
      return isJsPunctuation(node) && ")".equals(node.getContent());
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

   /**
    * Recognize "[" square bracket
    * 
    * @param node
    * @return
    */
   private boolean isOpenSquareBracket(Node node)
   {
      return isJsPunctuation(node) && "[".equals(node.getContent());
   }

   /**
    * Recognize node type "js-punctuation"
    * 
    * @param node
    * @return
    */
   private boolean isJsPunctuation(Node node)
   {
      return "js-punctuation".equals(node.getType());
   }

   /**
    * @return true if this is JavaScript number
    */
   private boolean isJsNumber(Node node)
   {
      return isJsAtomicType(node) && isCorrectJsNumberToken(node.getContent());
   };

   /**
    * @return true if this is truly JavaScript number content
    */
   private boolean isCorrectJsNumberToken(String token)
   {
      try
      {
         return !Double.isNaN(Double.valueOf(token));
      }
      catch (Exception e)
      {
         return false;
      }
   };

   /**
    * @return true if this is truly JavaScript boolean
    */
   private boolean isJsBoolean(Node node)
   {
      return isJsAtomicType(node)
         && ("true".equals(node.getContent().toLowerCase()) || "false".equals(node.getContent().toLowerCase()));
   };

   /**
    * Recognize "null" node
    * 
    * @param lastElement
    * @return
    */
   private boolean isJsNull(Node node)
   {
      return isJsAtomicType(node) && "null".equals(node.getContent());
   }

   /**
    * Recognize "js-atom" node type
    * 
    * @param node
    * @return
    */
   private boolean isJsAtomicType(Node node)
   {
      return "js-atom".equals(node.getType());
   }

   /**
    * Recognize "js-string" node type
    * 
    * @param node
    * @return
    */
   private boolean isJsString(Node node)
   {
      return "js-string".equals(node.getType());
   }
}