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
package org.exoplatform.ide.editor.ruby.client.codemirror;

import java.util.HashMap;
import java.util.LinkedList;
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
public class RubyParser extends CodeMirrorParserImpl
{
   private Stack<Node> nodeStack = new Stack<Node>();
   
   /**
    * Stack of blocks "def ... end" etc.
    */
   private Stack<TokenType> enclosers = new Stack<TokenType>();

   private HashMap<TokenType, LinkedList<String>> localVariables = new HashMap<TokenType, LinkedList<String>>();
   
   private HashMap<TokenType, LinkedList<String>> classVariables = new HashMap<TokenType, LinkedList<String>>();
   
   private HashMap<TokenType, LinkedList<String>> instanceVariables = new HashMap<TokenType, LinkedList<String>>();
   
   private LinkedList<String> globalVariables = new LinkedList<String>();
   
   private LinkedList<String> constants = new LinkedList<String>();
   
   @Override
   public void init()
   {
      super.init();

      nodeStack.clear();
      
      // initialize variable lists
      localVariables.put(TokenType.ROOT, new LinkedList<String>());
      localVariables.put(TokenType.CLASS, new LinkedList<String>()); 
      localVariables.put(TokenType.MODULE, new LinkedList<String>());      
      localVariables.put(TokenType.METHOD, new LinkedList<String>()); 
      globalVariables.clear();
      
      classVariables.put(TokenType.ROOT, new LinkedList<String>());
      classVariables.put(TokenType.CLASS, new LinkedList<String>()); 
      instanceVariables.put(TokenType.ROOT, new LinkedList<String>());
      instanceVariables.put(TokenType.CLASS, new LinkedList<String>()); 
      
      constants.clear();
   }

   @Override
   public TokenBeenImpl parseLine(JavaScriptObject javaScriptNode, int lineNumber, TokenBeenImpl currentToken, boolean hasParentParser)
   {
      // interrupt at the end of content
      if (javaScriptNode == null)
      {
         return currentToken;
      }
      
      nodeStack.push(new Node(javaScriptNode));
      
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
         if (!enclosers.empty())
         {
            // to filter block nodes like "if ... end"
            if (!enclosers.lastElement().equals(TokenType.BLOCK)
                 && currentToken.getParentToken() != null)
            {        
               currentToken = closeToken(lineNumber, currentToken);
            }
            else
            {
               enclosers.pop();
            }
         }
      
         nodeStack.clear();
      }
      
//      // recognize local variable
//      else if (isLocalVariable(nodeStack.lastElement().getType()))
//      {
//         if (isFirstVariableOccurance(nodeStack.lastElement().getContent(), TokenType.LOCAL_VARIABLE, currentToken))
//         {
//            addToken(currentToken, nodeStack.lastElement().getContent(), TokenType.LOCAL_VARIABLE, lineNumber);
//         }
//      }   
      
      // recognize variable
      else if ((newToken = isVariableWithAssigmentValue(nodeStack, currentToken)) != null)
      {
         addToken(newToken, lineNumber, currentToken);
      }
      
      // recognize global and instance variable without assignments like "$test/n" or "@test/n", so has nil value
      else if ((newToken = isVariableWithoutAssignment(nodeStack, currentToken)) != null)
      {
         newToken.setElementType("nil");
         addToken(newToken, lineNumber, currentToken);
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

      if (Node.isLineBreak(javaScriptNode)) 
      {
         return currentToken; // return current token if this is the end of line node
      } 
      
      return parseLine(Node.getNext(javaScriptNode), lineNumber, currentToken, false);
   }
   
   private void addToken(TokenBeenImpl newToken, int lineNumber, TokenBeenImpl currentToken)
   {
      newToken.setLineNumber(lineNumber);
      nodeStack.clear();
      
      switch (newToken.getType()) {
         case LOCAL_VARIABLE:
            updateLocalVariableList(currentToken, newToken.getName());
            break;
            
         case GLOBAL_VARIABLE:
            globalVariables.add(newToken.getName());
            break;

         case CLASS_VARIABLE:
            updateClassVariableList(currentToken, newToken.getName());
            
            // Pull Up class variable from method level to class level
            if (TokenType.METHOD.equals(currentToken.getType())
                  && currentToken.getParentToken() != null
                  && TokenType.CLASS.equals(currentToken.getParentToken().getType())
                )
            {
               currentToken.getParentToken().addSubToken(newToken);
               return;
            }

            break;       

         case INSTANCE_VARIABLE:
            updateInstanceVariableList(currentToken, newToken.getName());
            
            // Pull Up instance variable from method level to class level
            if (TokenType.METHOD.equals(currentToken.getType())
                  && currentToken.getParentToken() != null
                  && TokenType.CLASS.equals(currentToken.getParentToken().getType())
                )
            {
               currentToken.getParentToken().addSubToken(newToken);
               return;
            }
            
            break;
            
         case CONSTANT:
            constants.add(newToken.getName());
            break;
            
         default:
      }

      currentToken.addSubToken(newToken);

   }

   /**
    * Search variable and constants with name variableName among the previous variables or constants assignments  
    * @param variableName
    * @param variableType
    * @param currentToken
    * @return
    */
   private boolean isFirstVariableOccurance(String variableName, TokenType variableType, TokenBeenImpl currentToken)
   {
      switch (variableType) {
         case LOCAL_VARIABLE:
            // find variable in the toplevel local variable list
            if (currentToken.getParentToken() == null)
            {
               if (localVariables.get(TokenType.ROOT).contains(variableName))
                     return false;
            }

            // find variable in the method's local variable list
            if (TokenType.METHOD.equals(currentToken.getType()))
            {
               if (localVariables.get(TokenType.METHOD).contains(variableName))
                  return false;
            }       
            
            // find variable in the class's local variable list
            else if (TokenType.CLASS.equals(currentToken.getType()))
            {
               if (localVariables.get(TokenType.CLASS).contains(variableName))
                  return false;

            }

            // find variable in the module's local variable list
            else if (TokenType.MODULE.equals(currentToken.getType()))
            {
               if (localVariables.get(TokenType.MODULE).contains(variableName))
                  return false;
            }                     

            break;
            
         case GLOBAL_VARIABLE:
            // Ruby does not allow redefinitions of constants in a method.
            if (TokenType.METHOD.equals(currentToken.getType()) 
                 || globalVariables.contains(variableName)
               )
            {   
               return false;
            }            
            break;
            
         case CLASS_VARIABLE:            
            // find variable in the toplevel local variable list
            if (currentToken.getParentToken() == null)
            {
               if (classVariables.get(TokenType.ROOT).contains(variableName))
                     return false;
            }

            // find variable in the class's local variable list
            else if (TokenType.METHOD.equals(currentToken.getType())
                     || TokenType.CLASS.equals(currentToken.getType()))
            {
               if (classVariables.get(TokenType.CLASS).contains(variableName))
                  return false;
            }                        

            break;            

         case INSTANCE_VARIABLE:
            // find variable in the toplevel local variable list
            if (currentToken.getParentToken() == null)
            {
               if (instanceVariables.get(TokenType.ROOT).contains(variableName))
                     return false;
            }

            // find variable in the instance's local variable list
            else if (TokenType.METHOD.equals(currentToken.getType())
                     || TokenType.CLASS.equals(currentToken.getType()))
            {
               if (instanceVariables.get(TokenType.CLASS).contains(variableName))
                  return false;
            }                        

            break;
            
         case CONSTANT:
            if (constants.contains(variableName))
               return false;
            
            // ignore constant assigned within the method
            else if (TokenType.METHOD.equals(currentToken.getType()))
               return false;
            
            break;            
            
         default:
            return true;
      }
      
      return true;
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
      clearVariables(currentToken.getType());
      currentToken.setLastLineNumber(lineNumber);
      enclosers.pop();
      nodeStack.clear();
      
      if (currentToken.getParentToken() != null)
      {
         return currentToken.getParentToken();         
      }
      
      return currentToken;
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
            localVariables.get(TokenType.CLASS).clear();
            
            classVariables.get(TokenType.CLASS).clear();
            
            instanceVariables.get(TokenType.CLASS).clear();
            
            break;
            
         case MODULE:
            localVariables.get(TokenType.MODULE).clear();
            break;

         case METHOD:
            localVariables.get(TokenType.METHOD).clear();
            break;
            
         default:
      }
   }
   
   private void updateLocalVariableList(TokenBeenImpl currentToken, String localVariableName)
   {
      // update toplevel local variable list
      if (currentToken.getParentToken() == null)
      {
         localVariables.get(TokenType.ROOT).add(localVariableName);
      }
      
      // update method's local variable list
      else if (TokenType.METHOD.equals(currentToken.getType()))
      {
         localVariables.get(TokenType.METHOD).add(localVariableName);
      }

      // update module's local variable list
      else if (TokenType.MODULE.equals(currentToken.getType()))
      {
         localVariables.get(TokenType.MODULE).add(localVariableName);
      }
      
      // update class's local variable list
      else if (TokenType.CLASS.equals(currentToken.getType()))
      {
         localVariables.get(TokenType.CLASS).add(localVariableName);
      }
   }
   
   private void updateClassVariableList(TokenBeenImpl currentToken, String classVariableName)
   {
      // update toplevel variable list
      if (currentToken.getParentToken() == null)
      {
         classVariables.get(TokenType.ROOT).add(classVariableName);
      }
      
      // update class's variable list
      else if (TokenType.METHOD.equals(currentToken.getType())
               || TokenType.CLASS.equals(currentToken.getType()))
      {
         classVariables.get(TokenType.CLASS).add(classVariableName);
      }
   }

   private void updateInstanceVariableList(TokenBeenImpl currentToken, String instanceVariableName)
   {
      // update toplevel variable list
      if (currentToken.getParentToken() == null)
      {
         instanceVariables.get(TokenType.ROOT).add(instanceVariableName);
      }
      
      // update instance's variable list
      else if (TokenType.METHOD.equals(currentToken.getType())
               || TokenType.CLASS.equals(currentToken.getType()))
      {
         instanceVariables.get(TokenType.CLASS).add(instanceVariableName);
      }
   }
   
   private boolean isClassName(Stack<Node> nodeStack)
   {
      if (nodeStack.size() > 1
               && isClassNode(nodeStack.get(nodeStack.size() - 2))
               && isConstant(nodeStack.lastElement().getType())
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
               && (isMethodNode(nodeStack.lastElement().getType()) 
                     || isConstant(nodeStack.lastElement().getType())
                   )
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
    * Recognize point in case like "a."
    * @param node
    * @return
    */
   public static boolean isPoint(Node node)
   {
      return "rb-method-call".equals(node.getType()) && ".".equals(node.getContent());
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
   private static boolean isConstant(String nodeType)
   {
      return "rb-constant".equals(nodeType);
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
   
   /**
    * Recognize ruby local variable
    * @param nodeType
    * @return
    */
   private static boolean isLocalVariable(String nodeType)
   {
      return "rb-variable".equals(nodeType);
   }

   /**
    * Recognize ruby global variable like "$globalVar"
    * @param nodeType
    * @return
    */
   private static boolean isGlobalVariable(String nodeType)
   {
      return "rb-global-variable".equals(nodeType);
   }
   
   /**
    * Recognize instance variable like "@instanceVar"
    * @param nodeType
    * @return
    */
   private static boolean isInstanceVariable(String nodeType)
   {
      return "rb-instance-var".equals(nodeType);
   }
   
   /**
    * Recognize class variable like "@@classVar"
    * @param nodeType
    * @return
    */
   private static boolean isClassVariable(String nodeType)
   {
      return "rb-class-var".equals(nodeType);
   }
   
   /**
    * Recognize variable and constant with first assigment "h = 11" 
    * @param nodeStack 
    * @param currentToken
    * @return element type from "number", "string" etc.
    */
   private TokenBeenImpl isVariableWithAssigmentValue(Stack<Node> nodeStack, TokenBeenImpl currentToken)
   {
      if (nodeStack.size() > 2)
      {
         // recognize type of assignment
         Stack<Node> cloneNodeStack = (Stack<Node>) nodeStack.clone();
         String possibleElementType = null;
         Node lastNode = cloneNodeStack.pop();
       
         // parse wrong statement like "a = /n"
         if (lastNode.isLineBreak())
         {
            return null;
         }
         
         // parse object creation like "b = ClassName.new" 
         else if (isNewMethodCall(lastNode))
         {
            if ((possibleElementType = readClassName(cloneNodeStack)) == null)
               return null;
         }
         
         // pass constant to recognize object creation in the next cycle
         else if (isConstant(lastNode.getType()))
         {
            return null; 
         }
         
         else if (isFixNumber(lastNode.getType()))
         {
            possibleElementType = "Fixnum";
         }

         else if (isFloatNumber(lastNode.getType()))
         {
            possibleElementType = "Float";
         }

         else if (isHexNumber(lastNode.getType())
                  || isBinaryNumber(lastNode.getType())
                 )
         {
            possibleElementType = "Number";
         }
         
         // recognize TrueClass
         else if (isTrue(lastNode))
         {
            possibleElementType = "TrueClass";
         }

         // recognize FalseClass
         else if (isFalse(lastNode))
         {
            possibleElementType = "FalseClass";
         }
         
         else if (isAscii(lastNode.getType()))
         {
            possibleElementType = "Ascii";
         }         
         
         else if (isRegexp(lastNode.getType()))
         {
            possibleElementType = "Regexp";
         }
         
         // recognize start of array
         else if (isOpenSquareBracket(lastNode))
         {
            possibleElementType = "Array";
         }
         
         // recognize start of hashes
         else if (isOpenBrace(lastNode))
         {
            possibleElementType = "Hash";
         }
         
         // test if this is symbol like ":name"
         else if (isSymbol(lastNode))
         {
            possibleElementType = "Symbol";
         }   
         
         // test if this is "nil" value
         else if (isNil(lastNode))
         {
            possibleElementType = "NilClass";
         }
         
         // test if this is string value
         else if (isString(lastNode.getType()))
         {
            possibleElementType = "String";
         }
      
         // recognize variable assignment statement like "a ="
         TokenBeenImpl newToken;
         if (cloneNodeStack.size() > 1)
         {
            TokenType variableType;
            if (isEqualSign(cloneNodeStack.get(cloneNodeStack.size() - 1))
                 && (variableType = isVariable(cloneNodeStack.get(cloneNodeStack.size() - 2).getType())) != null 
                 && isFirstVariableOccurance(
                       cloneNodeStack.get(cloneNodeStack.size() - 2).getContent(),
                       variableType,
                       currentToken
                    )
               )
            {

               newToken = new TokenBeenImpl(cloneNodeStack.get(cloneNodeStack.size() - 2).getContent(), variableType, 0, MimeType.APPLICATION_RUBY);
               newToken.setElementType("Object");
            
               if (possibleElementType != null)
               {
                  newToken.setElementType(possibleElementType);
               }
            
               return newToken;
            }
         }
      }
         
      return null;
   }
   
   /**
    * Recognize ".new" node in object creation statement like "b = ClassName.new"
    * @param node
    * @return
    */
   private boolean isNewMethodCall(Node node)
   {
      return "rb-method-call".equals(node.getType()) && ".new".equals(node.getContent());
   }

   /**
    * b = ClassName.new
    * b = ClassName.new()
    * b = ClassName.new v, t
    * @param nodeStack
    * @return ClassName
    */
   private String readClassName(Stack<Node> nodeStack)
   {
      Node classNode = nodeStack.pop();
      if (isConstant(classNode.getType()))
         return classNode.getContent();
      
      return null;
   }

   /**
    * Recognize GLOBAL_VARIABLE or INSTANCE_VARIABLE variable without assignment like "@h /n" 
    * @param safe nodeStack
    * @return CodeMirrorTokenImpl with global variable in case like "$a /n"
    */
   private TokenBeenImpl isVariableWithoutAssignment(Stack<Node> nodeStack, TokenBeenImpl currentToken)
   {
      if (nodeStack.size() > 1)
      {
         TokenType variableType;
         if (nodeStack.get(nodeStack.size() - 1).isLineBreak()
             && (variableType = isVariable(nodeStack.get(nodeStack.size() - 2).getType())) != null
             && (TokenType.GLOBAL_VARIABLE.equals(variableType) 
                   || TokenType.INSTANCE_VARIABLE.equals(variableType)
                )
             && isFirstVariableOccurance(
                nodeStack.get(nodeStack.size() - 2).getContent(),
                 variableType,
                 currentToken
             )
         )
         {            
            return new TokenBeenImpl(nodeStack.get(nodeStack.size() - 2).getContent(), variableType, 0, MimeType.APPLICATION_RUBY);
         }
      }
      
      return null;
   }  
   
   
   /**
    * Return TokenType of variable or constant or null 
    * @param node type
    * @return
    */
   public static TokenType isVariable(String nodeType)   {
      if (isLocalVariable(nodeType))
         return TokenType.LOCAL_VARIABLE;
         
      if (isGlobalVariable(nodeType))
         return TokenType.GLOBAL_VARIABLE;

      if (isInstanceVariable(nodeType))
         return TokenType.INSTANCE_VARIABLE;
      
      if (isClassVariable(nodeType))
         return TokenType.CLASS_VARIABLE;
      
      if (isConstant(nodeType))
         return TokenType.CONSTANT;
      
      return null;
   }

   /**
    * @return true if this is fix number
    */
   private boolean isFixNumber(String nodeType) {
      return nodeType.contains("rb-fixnum");
   };
   
   /**
    * @param type
    * @return true if this is string value
    */
   private boolean isString(String nodeType)
   {
      return "rb-string".equals(nodeType);
   }
   
   /**
    * @param node
    * @return true if this is "nil" value
    */
   private boolean isNil(Node node)
   {
      return "rb-keyword".equals(node.getType()) && ("nil".equals(node.getContent()));
   }
   
   /**
    * @param node
    * @return true if this is "true" value
    */
   private boolean isTrue(Node node)
   {
      return "rb-keyword".equals(node.getType()) && ("true".equals(node.getContent()));
   }   

   /**
    * @param node
    * @return true if this is "false" value
    */
   private boolean isFalse(Node node)
   {
      return "rb-keyword".equals(node.getType()) && ("false".equals(node.getContent()));
   }
   
   /**
    * @param node
    * @return true if this is "symbol" value
    */
   private boolean isSymbol(Node node)
   {
      return "rb-symbol".equals(node.getType());
   }

   /**
    * @return true if this is float number
    */
   private boolean isFloatNumber(String nodeType)
   {
      return "rb-float".equals(nodeType);
   }
   
   /**
    * @return true if this is regular expression
    */
   private boolean isRegexp(String nodeType)
   {
      return "rb-regexp".equals(nodeType);
   }
   
   /**
    * @param node
    * @return true if this is "["
    */
   private boolean isOpenSquareBracket(Node node)
   {
      return "rb-normal".equals(node.getType()) && ("[".equals(node.getContent()));
   }
   
   /**
    * @param nodeType
    * @return true if this is hex number like  "0xffff"
    */
   private boolean isHexNumber(String nodeType)
   {
      return "rb-hexnum".equals(nodeType);
   }
   
   /**
    * @param nodeType
    * @return true if this is binary number like  "0b01011"
    */
   private boolean isBinaryNumber(String nodeType)
   {
      return "rb-binary".equals(nodeType);
   }

   /**
    * @param nodeType
    * @return true if this is ascii value like "?\C-a  "
    */
   private boolean isAscii(String nodeType)
   {
      return "rb-ascii".equals(nodeType);
   }
   
}