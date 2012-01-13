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
package org.exoplatform.ide.editor.php.client.codemirror;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.editor.api.codeassitant.Modifier;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.codemirror.CodeMirrorParserImpl;
import org.exoplatform.ide.editor.codemirror.Node;
import org.exoplatform.ide.editor.html.client.codemirror.HtmlParser;
import org.exoplatform.ide.editor.xml.client.codemirror.XmlParser;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: $
 * 
 */
public class PhpParser extends CodeMirrorParserImpl
{

   String currentContentMimeType;

   HtmlParser htmlParser = new HtmlParser();

   private HashMap<TokenType, LinkedList<String>> variables = new HashMap<TokenType, LinkedList<String>>();

   private LinkedList<String> constants = new LinkedList<String>();

   private Stack<Node> mainNodeStack = new Stack<Node>();

   /**
    * Dynamic calling operator name.
    */
   public static String dynamicCallingOperator = "-&gt;";

   /**
    * Static calling operator name.
    */
   public static String staticCallingOperator = "::";

   /**
    * Stack of blocks "{... {...} ...}"
    */
   private Stack<TokenType> enclosers = new Stack<TokenType>();

   @Override
   public void init()
   {
      super.init();
      currentContentMimeType = MimeType.TEXT_HTML;
      mainNodeStack.clear();
      enclosers.clear();

      // initialize variable lists
      variables.put(TokenType.PHP_TAG, new LinkedList<String>());
      variables.put(TokenType.CLASS, new LinkedList<String>());
      variables.put(TokenType.METHOD, new LinkedList<String>());

      constants.clear();
   }

   public TokenBeenImpl parseLine(JavaScriptObject javaScriptNode, int lineNumber, TokenBeenImpl currentToken,
      boolean hasParentParser)
   {
      // interrupt at the end of the document
      if (javaScriptNode == null)
         return currentToken;

      String nodeContent = Node.getContent(javaScriptNode).trim(); // returns text without ended space " " in the text
      String nodeType = Node.getType(javaScriptNode);

      // recognize "<?" open tag within the TEXT_HTML content
      if (isPhpOpenTag(nodeType, nodeContent) && MimeType.TEXT_HTML.equals(currentContentMimeType))
      {
         TokenBeenImpl newToken =
            new TokenBeenImpl("php code", TokenType.PHP_TAG, lineNumber, MimeType.APPLICATION_PHP);
         if (currentToken != null)
         {
            currentToken.addSubToken(newToken);
         }
         currentToken = newToken;

         currentContentMimeType = MimeType.APPLICATION_PHP;
      }

      // recognize "?>" close tag
      else if (isPhpCloseTag(nodeType, nodeContent) && !MimeType.TEXT_HTML.equals(currentContentMimeType))
      {
         currentToken = XmlParser.closeTag(lineNumber, currentToken);

         currentContentMimeType = MimeType.TEXT_HTML;
         htmlParser.init();
         javaScriptNode = Node.getNext(javaScriptNode); // pass parsed node
      }

      if (!currentContentMimeType.equals(MimeType.APPLICATION_PHP))
      {
         currentToken = htmlParser.parseLine(javaScriptNode, lineNumber, currentToken, true); // call child parser
      }
      else
      {
         // parse php code
         mainNodeStack.push(new Node(javaScriptNode));

         TokenBeenImpl newToken;

         // recognize class declaration like "final class testClass extends parentClass {..." or "class testClass \n {..."
         if ((newToken = isClassDeclaration((Stack<Node>)mainNodeStack.clone(), lineNumber)) != null)
         {
            addSubToken(currentToken, newToken);
            currentToken = newToken;
            enclosers.push(TokenType.CLASS);
         }

         // recognize interface declaration like "interface test extends foo {..." or "interface test \n {..."
         else if ((newToken = isInterfaceDeclaration((Stack<Node>)mainNodeStack.clone(), lineNumber)) != null)
         {
            addSubToken(currentToken, newToken);
            currentToken = newToken;
            enclosers.push(TokenType.INTERFACE);
         }

         // recognize function/method declaration like "public static function a(arg1, ...) .. {"
         else if ((newToken =
            isFunctionDeclaration((Stack<Node>)mainNodeStack.clone(), lineNumber, currentToken.getType())) != null)
         {
            addSubToken(currentToken, newToken);

            // do not add method declaration within the interface to encloser list and such set method token as currentToken
            if (!TokenType.INTERFACE.equals(currentToken.getType()))
            {
               currentToken = newToken;
               enclosers.push(newToken.getType());
            }
         }

         // recognize first variable and property declaration like "$a = True;", "private $p1;" or "private $p1 = 2;"
         else if ((newToken = isFirstVariableDefinition((Stack<Node>)mainNodeStack.clone(), currentToken, lineNumber)) != null)
         {
            addSubToken(currentToken, newToken);
         }

         // recognize class constant definition like "const MYCONST = 'some string';" within the class, not within the method of
         // class
         else if ((newToken =
            isFirstClassConstantDefinition((Stack<Node>)mainNodeStack.clone(), currentToken, lineNumber)) != null)
         {
            addSubToken(currentToken, newToken);
         }

         // recognize constant definition like 'define("CONSTANT_EX", False);' outside the class
         else if ((newToken = isFirstConstantDefinition((Stack<Node>)mainNodeStack.clone(), currentToken, lineNumber)) != null)
         {
            addSubToken(currentToken, newToken);
         }

         // recognize namespace declaration like 'namespace \my\name;' outside the class
         else if ((newToken = isNamespaceDeclaration((Stack<Node>)mainNodeStack.clone(), currentToken)) != null)
         {
            addSubToken(lineNumber, currentToken, newToken);
         }

         // recognize open brace "{"
         else if (isOpenBrace(mainNodeStack.lastElement()))
         {
            enclosers.push(TokenType.BLOCK);
         }

         // recognize close brace "}"
         else if (isCloseBrace(mainNodeStack.lastElement()))
         {
            if (!enclosers.isEmpty())
            {
               if (TokenType.CLASS.equals(enclosers.lastElement())
                  || TokenType.INTERFACE.equals(enclosers.lastElement())
                  || TokenType.FUNCTION.equals(enclosers.lastElement())
                  || TokenType.METHOD.equals(enclosers.lastElement()))
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

      return parseLine(Node.getNext(javaScriptNode), lineNumber, currentToken, false); // call itself
   }

   /**
    * Recognize interface declaration like "interface test extends foo {..." or "interface test \n {..."
    * 
    * @param nodeStack non-safe
    * @param lineNumber
    * @return
    */
   private TokenBeenImpl isInterfaceDeclaration(Stack<Node> nodeStack, int lineNumber)
   {
      if (nodeStack.size() > 2)
      {
         if (isOpenBrace(nodeStack.pop()))
         {
            // pass BR or whitespace between "interface foo ....  {"
            while (nodeStack.size() > 1)
            {
               // decrease line number if there is BR node between "foo ....  {"
               if (nodeStack.lastElement().isLineBreak())
               {
                  lineNumber--;
               }

               // break if there is non-BR or non-whitespace node between "foo ....  {"
               else if (!nodeStack.lastElement().isLineBreak() && !isWhitespace(nodeStack.lastElement()))
               {
                  break;
               }

               nodeStack.pop();
            }

            // parse class name and class modifiers
            while (nodeStack.size() > 1)
            {
               // pass "extends" and test if this is interface name
               if (isInterfaceName(nodeStack))
               {
                  TokenBeenImpl newToken =
                     new TokenBeenImpl(nodeStack.lastElement().getContent(), TokenType.INTERFACE, lineNumber,
                        MimeType.APPLICATION_PHP);

                  return newToken;
               }

               nodeStack.pop();
            }
         }
      }

      return null;
   }

   /**
    * Recognize interface name like "\n interface foo"
    * 
    * @param nodeStack safe
    * @return
    */
   private boolean isInterfaceName(Stack<Node> nodeStack)
   {
      return (nodeStack.size() > 1) && isPhpElementName(nodeStack.get(nodeStack.size() - 1).getType())
         && isInterfaceKeyword(nodeStack.get(nodeStack.size() - 2));
   }

   /**
    * Recognize "interface" keyword.
    * 
    * @param node
    * @return
    */
   private boolean isInterfaceKeyword(Node node)
   {
      return isKeyword(node) && "interface".equals(node.getContent());
   }

   /**
    * Recognize namespace declaration like '\n namespace \my\name;' outside the class
    * 
    * @param nodeStack non-safe
    * @param currentToken
    * @return
    */
   private TokenBeenImpl isNamespaceDeclaration(Stack<Node> nodeStack, TokenBeenImpl currentToken)
   {
      if (isInsideTheClassOrInterface(currentToken))
         return null;

      if (nodeStack.size() > 2)
      {
         if (isSemicolonNode(nodeStack.pop()) && isPhpElementName(nodeStack.lastElement().getType())
            && isNamespaceKeyword(nodeStack.get(nodeStack.size() - 2)))
         {
            return new TokenBeenImpl(nodeStack.lastElement().getContent(), TokenType.NAMESPACE, 0,
               MimeType.APPLICATION_PHP);
         }
      }

      return null;
   }

   /**
    * Recognize class constant single-line or multi-line definition like "const MYCONST = 'some string';" inside the class, not
    * within the method of class
    * 
    * @param currentToken
    * @param lineNumber
    * @param non-safe nodeStack
    * @return
    */
   private TokenBeenImpl isFirstClassConstantDefinition(Stack<Node> nodeStack, TokenBeenImpl currentToken,
      int lineNumber)
   {
      // class constants should be defined inside the class or interface
      if (!isInsideTheClassOrInterface(currentToken))
         return null;

      TokenBeenImpl newToken = null;
      int lastLineNumber = lineNumber;

      if (nodeStack.size() > 4)
      {
         if (isSemicolonNode(nodeStack.pop()))
         {
            String possibleElementType = analyzeTypeOfAssignment((Stack<Node>)nodeStack.clone(), false);

            // pass nodes before "="
            while ((nodeStack.size() > 3) && !isEqualSign(nodeStack.lastElement()))
            {
               // taking into account multi-line definition
               if (nodeStack.pop().isLineBreak())
               {
                  lineNumber--;
               }
            }

            // recognize variable assignment statement like "const MYCONST ="
            if (isEqualSign(nodeStack.pop()) && isClassConstantName(nodeStack)
               && isFirstVariableOccurance(currentToken, nodeStack.lastElement().getContent()))
            {
               String constName = nodeStack.lastElement().getContent();

               newToken = new TokenBeenImpl(constName, TokenType.CLASS_CONSTANT, lineNumber, MimeType.APPLICATION_PHP);

               updateVariableList(currentToken, constName);

               if (possibleElementType != null)
               {
                  newToken.setElementType(possibleElementType);
               }

               // taking into account multi-line definition
               if (lastLineNumber != lineNumber)
               {
                  newToken.setLastLineNumber(lastLineNumber);
               }
            }
         }
      }

      return newToken;
   }

   /**
    * Recognize class constant statement like "const MYCONST".
    * 
    * @param nodeStack safe
    * @return
    */
   private boolean isClassConstantName(Stack<Node> nodeStack)
   {
      return (nodeStack.size() > 1) && isPhpElementName(nodeStack.get(nodeStack.size() - 1).getType())
         && isConstKeyword(nodeStack.get(nodeStack.size() - 2));
   }

   /**
    * Recognize "const" keyword.
    * 
    * @param node
    * @return
    */
   private boolean isConstKeyword(Node node)
   {
      return isKeyword(node) && "const".equals(node.getContent());
   }

   /**
    * Recognize first definition of constant with only scalar data (boolean, integer, float and string) like
    * 'define("CONSTANT_EX", "test");'. Also constants should be defined outside the class.
    * 
    * @param nodeStack non-safe
    * @param currentToken
    * @return
    */
   private TokenBeenImpl isFirstConstantDefinition(Stack<Node> nodeStack, TokenBeenImpl currentToken, int lineNumber)
   {
      // constants should be defined outside the class
      if (isInsideTheClassOrInterface(currentToken))
         return null;

      TokenBeenImpl newToken = null;
      int lastLineNumber = lineNumber;

      if (nodeStack.size() > 6)
      {
         // check on ");"
         if (isSemicolonNode(nodeStack.pop()) && isCloseBracket(nodeStack.pop()))
         {
            String possibleElementType = analyzeTypeOfAssignment((Stack<Node>)nodeStack.clone(), true);

            // pass nodes before "," within the brackets 'define("CONSTANT_EX", "test");'
            while ((nodeStack.size() > 4) && !isComma(nodeStack.lastElement()))
            {
               // taking into account multi-line definition
               if (nodeStack.pop().isLineBreak())
               {
                  lineNumber--;
               }
            }

            // recognize variable assignment statement like 'define("CONSTANT_EX",'
            if (isComma(nodeStack.pop()))
            {
               String constName = nodeStack.lastElement().getContent().replaceAll("[\'\"]", ""); // remove " and ' enclosers
               // recognize variable assignment statement like 'define("CONSTANT_EX"'
               if (isConstantName(nodeStack) && !constants.contains(constName) // check if this is first occurrence of constant
               )
               {
                  newToken = new TokenBeenImpl(constName, TokenType.CONSTANT, lineNumber, MimeType.APPLICATION_PHP);

                  constants.add(constName);

                  if (possibleElementType != null)
                  {
                     newToken.setElementType(possibleElementType);
                  }

                  // taking into account multi-line definition
                  if (lastLineNumber != lineNumber)
                  {
                     newToken.setLastLineNumber(lastLineNumber);
                  }
               }
            }
         }
      }

      return newToken;
   }

   /**
    * Test if current token is class or the token inside the class or inside the interface.
    * 
    * @param currentToken
    * @return
    */
   private boolean isInsideTheClassOrInterface(TokenBeenImpl currentToken)
   {
      return TokenType.CLASS == currentToken.getType() || TokenType.INTERFACE == currentToken.getType()
         || (currentToken.getParentToken() != null // check constants within the method
         && TokenType.CLASS == currentToken.getParentToken().getType());
   }

   /**
    * Recognize start of constant definition like 'define("CONST_NAME"'
    * 
    * @param nodeStack safe
    * @return
    */
   private boolean isConstantName(Stack<Node> nodeStack)
   {
      return (nodeStack.size() > 2) && isString(nodeStack.get(nodeStack.size() - 1).getType())
         && isOpenBracket(nodeStack.get(nodeStack.size() - 2)) && isDefineKeyword(nodeStack.get(nodeStack.size() - 3));
   }

   /**
    * Recognize "define" function keyword
    * 
    * @param node
    * @return
    */
   private boolean isDefineKeyword(Node node)
   {
      return "php-predefined-function".equals(node.getType()) && "define".equals(node.getContent());
   }

   /**
    * Recognize first field declaration like "$a;", "private $a", or field or variable single-line or multi-line declaration like
    * "$a = 1;", "private $a = True", not inside the interface.
    * 
    * @param currentToken
    * @param lineNumber
    * @param non-safe node stack
    * @return
    */
   private TokenBeenImpl isFirstVariableDefinition(Stack<Node> nodeStack, TokenBeenImpl currentToken, int lineNumber)
   {
      if (TokenType.INTERFACE.equals(currentToken))
         return null;

      TokenBeenImpl newToken = null;
      int lastLineNumber = lineNumber;

      if (nodeStack.size() > 1)
      {
         if (isSemicolonNode(nodeStack.pop()))
         {
            // recognize field declaration like "$a"
            if (TokenType.CLASS.equals(currentToken.getType()) && isVariable(nodeStack.lastElement().getType())
               && isFirstVariableOccurance(currentToken, nodeStack.lastElement().getContent())
               && !isReference(nodeStack) // pass reference on variables like '&$a'
            )
            {
               newToken =
                  new TokenBeenImpl(nodeStack.lastElement().getContent(), TokenType.PROPERTY, lineNumber,
                     MimeType.APPLICATION_PHP);

               nodeStack.setSize(nodeStack.size() - 1); // remove property name "$a" node
               checkModifiers(newToken, nodeStack);
            }

            // recognize field or variable declaration like "$a = 1"
            else if (nodeStack.size() > 2)
            {
               String possibleElementType = analyzeTypeOfAssignment((Stack<Node>)nodeStack.clone(), false);

               // pass nodes before "="
               while ((nodeStack.size() > 2) && !isEqualSign(nodeStack.lastElement()))
               {

                  // taking into account multi-line definition
                  if (nodeStack.pop().isLineBreak())
                  {
                     lineNumber--;
                  }
               }

               // recognize variable assignment statement like "$a ="
               if (isEqualSign(nodeStack.pop()) && isVariable(nodeStack.lastElement().getType())
                  && isFirstVariableOccurance(currentToken, nodeStack.lastElement().getContent())
                  && !isReference(nodeStack) // pass reference on variables like '&$a'
               )
               {
                  String variableName = nodeStack.lastElement().getContent();

                  newToken = new TokenBeenImpl(variableName, TokenType.VARIABLE, lineNumber, MimeType.APPLICATION_PHP);

                  if (possibleElementType != null)
                  {
                     newToken.setElementType(possibleElementType);
                  }

                  // taking into account multi-line definition
                  if (lastLineNumber != lineNumber)
                  {
                     newToken.setLastLineNumber(lastLineNumber);
                  }

                  // replace VARIABLE on PROPERTY
                  if (TokenType.CLASS.equals(currentToken.getType()))
                  {
                     newToken.setType(TokenType.PROPERTY);

                     nodeStack.setSize(nodeStack.size() - 1); // remove property name "$a" node
                     checkModifiers(newToken, nodeStack);
                  }
               }
            }

            if (newToken != null)
            {
               updateVariableList(currentToken, newToken.getName());
            }
         }
      }

      return newToken;
   }

   /**
    * Trying to predict possible type of variable by analyzing of assignment from nodeStack
    * 
    * @param onlyScalarDataType if true, then will be analyze only scalar data (boolean, integer, float, string, null)
    * @param nodeStack non-safe
    * @return
    */
   private String analyzeTypeOfAssignment(Stack<Node> nodeStack, boolean onlyScalarDataType)
   {
      Node lastNode = nodeStack.lastElement();

      String possibleClassName = null;

      if (isString(lastNode.getType()))
      {
         return "String";
      }

      else if (isBoolean(lastNode))
      {
         return "Boolean";
      }

      else if (isInteger(lastNode))
      {
         return "Integer";
      }

      else if (isFloat(lastNode))
      {
         return "Float";
      }

      else if (isNull(lastNode))
      {
         return "Null";
      }

      else if (!onlyScalarDataType && isArray((Stack<Node>)nodeStack.clone()))
      {
         return "Array";
      }

      else if (!onlyScalarDataType && ((possibleClassName = isObject((Stack<Node>)nodeStack.clone())) != null))
      {
         return possibleClassName;
      }

      return null;
   }

   /**
    * Recognize "True", "false", "TRUE" etc... case-insensitive variants of boolean keywords.
    * 
    * @param node
    * @return
    */
   private boolean isBoolean(Node node)
   {
      return ("php-predefined-constant".startsWith(node.getType()) || "php-atom".startsWith(node.getType()) || "php-t_string"
         .startsWith(node.getType()))
         && ("true".equalsIgnoreCase(node.getContent()) || "false".equalsIgnoreCase(node.getContent().toLowerCase()));
   }

   /**
    * Recognize floating point numbers like "-123.45" or "1.2e-3"
    * 
    * @param node
    * @return
    */
   private boolean isFloat(Node node)
   {
      return "php-atom".startsWith(node.getType())
         && (node.getContent().toLowerCase().matches("^[-+]?[0-9]+[.]([0-9]+)?|[.][0-9]+$") // matches a floating point number
                                                                                            // like "-123.45" with optional
                                                                                            // integer as well as optional
                                                                                            // fractional part.
         || node.getContent().toLowerCase().matches("^[-+]?([0-9]+?[.])?[0-9]+([e][-+]?[0-9]+)?$") // matches a number in
                                                                                                   // scientific notation like
                                                                                                   // "1.2e-3". The mantissa can
                                                                                                   // be an integer or floating
                                                                                                   // point number with optional
                                                                                                   // integer part. The exponent
                                                                                                   // is optional.
         );
   }

   /**
    * Recognize integer type like "-1" or hexadecimal integer numbers like "0xa34"
    * 
    * @param node
    * @return
    */
   private boolean isInteger(Node node)
   {
      return "php-atom".startsWith(node.getType()) && (node.getContent().toLowerCase().matches("^[-+]?[0-9]+$") // integer like
                                                                                                                // "-1"
         || node.getContent().toLowerCase().matches("^0x[0-9a-f]+$") // hexadecimal integer like "0xa34"
         );
   }

   /**
    * Recognize "NULL", "Null", "null" etc... case-insensitive variants of keyword null.
    * 
    * @param node
    * @return
    */
   private boolean isNull(Node node)
   {
      return ("php-predefined-constant".startsWith(node.getType()) || "php-atom".startsWith(node.getType()) || "php-t_string"
         .startsWith(node.getType())) && "null".equalsIgnoreCase(node.getContent());
   }

   /**
    * Recognize array creation like "array(1=>1, 'a'=>2, 3)"
    * 
    * @param non-safe nodeStack
    * @return
    */
   private boolean isArray(Stack<Node> nodeStack)
   {
      if (nodeStack.size() > 2 && isCloseBracket(nodeStack.pop()))
      {
         // pass nodes before code like "array("
         while ((nodeStack.size() > 2) && !isOpenBracket(nodeStack.pop()))
         {
         }

         // test if there is array keyword
         if (isArrayKeyword(nodeStack.lastElement()))
         {
            return true;
         }
      }

      return false;
   }

   /**
    * Recognize "array" keyword.
    * 
    * @param node
    * @return
    */
   private boolean isArrayKeyword(Node node)
   {
      return "php-reserved-language-construct".startsWith(node.getType()) && "array".equals(node.getContent());
   }

   /**
    * Recognize object creation like "new Data($a, null)" or "new SimpleXMLElement"
    * 
    * @param nodeStack
    * @return
    */
   private String isObject(Stack<Node> nodeStack)
   {
      while (nodeStack.size() > 1)
      {
         // get class name before "new" keyword like "new Data"
         if (isPhpElementName(nodeStack.lastElement().getType()) && isNewKeyword(nodeStack.get(nodeStack.size() - 2)))
         {
            return nodeStack.lastElement().getContent();
         }

         nodeStack.pop();
      }

      return null;
   }

   /**
    * Recognize "new" keyword.
    * 
    * @param node
    * @return
    */
   private boolean isNewKeyword(Node node)
   {
      return isKeyword(node) && "new".equals(node.getContent());
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
    * 
    * @param nodeType
    * @param nodeContent
    * @return
    */
   private boolean isPhpOpenTag(String nodeType, String nodeContent)
   {
      return "xml-processing".equals(nodeType) && ("&lt;?".equals(nodeContent) || "&lt;?php".equals(nodeContent));
   }

   /**
    * Recognize "?>" node.
    * 
    * @param nodeType
    * @param nodeContent
    * @return
    */
   private boolean isPhpCloseTag(String nodeType, String nodeContent)
   {
      return "xml-processing".equals(nodeType) && "?&gt;".equals(nodeContent);
   };

   /**
    * Return true if there is "\n class className" pattern at the top of nodeStack
    * 
    * @param nodeStack safe
    * @return
    */
   private boolean isClassName(Stack<Node> nodeStack)
   {
      if (nodeStack.size() > 1 && isPhpElementName(nodeStack.lastElement().getType())
         && isClassKeyword(nodeStack.get(nodeStack.size() - 2)))
      {
         return true;
      }

      return false;
   }

   /**
    * Recognize "{"
    * 
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
    * 
    * @param non-safe nodeStack
    * @param lineNumber
    * @return new class token
    */
   private TokenBeenImpl isClassDeclaration(Stack<Node> nodeStack, int lineNumber)
   {
      if (nodeStack.size() > 2)
      {
         if (isOpenBrace(nodeStack.pop()))
         {
            // pass BR or whitespace between "className ....  {"
            while (nodeStack.size() > 2)
            {
               // decrease line number if there is BR node between "className ....  {"
               if (nodeStack.lastElement().isLineBreak())
               {
                  lineNumber--;
               }

               // break if there is non-BR or non-whitespace node between "className ....  {"
               else if (!nodeStack.lastElement().isLineBreak() && !isWhitespace(nodeStack.lastElement()))
               {
                  break;
               }

               nodeStack.pop();
            }

            // parse class name and class modifiers
            while (nodeStack.size() > 1)
            {
               // test if this is class name
               if (isClassName(nodeStack))
               {
                  TokenBeenImpl newToken =
                     new TokenBeenImpl(nodeStack.lastElement().getContent(), TokenType.CLASS, lineNumber,
                        MimeType.APPLICATION_PHP);

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
    * 
    * @param token
    * @param nodeStack
    */
   private void checkModifiers(TokenBeenImpl token, Stack<Node> nodeStack)
   {
      LinkedList<Modifier> modifiers = new LinkedList<Modifier>();

      Modifier modifier;

      while (nodeStack.size() > 0)
      {
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
    * Check on supported by PHP 'abstract', 'final', 'private', 'protected', 'public', 'static' modifiers.
    * 
    * @param lastNode
    * @return
    */
   private Modifier isModifier(Node lastNode)
   {
      if (isKeyword(lastNode)
         && (Modifier.ABSTRACT.toString().toLowerCase().equals(lastNode.getContent())
            || Modifier.FINAL.toString().toLowerCase().equals(lastNode.getContent())
            || Modifier.PRIVATE.toString().toLowerCase().equals(lastNode.getContent())
            || Modifier.PROTECTED.toString().toLowerCase().equals(lastNode.getContent())
            || Modifier.PUBLIC.toString().toLowerCase().equals(lastNode.getContent()) || Modifier.STATIC.toString()
            .toLowerCase().equals(lastNode.getContent())))
      {
         // trying to get found java modifier from Modifier enum and add this modifier into the 'modifiers' property
         try
         {
            return Modifier.valueOf(lastNode.getContent().toUpperCase());
         }
         catch (IllegalArgumentException ex)
         {
         }
      }

      return null;
   }

   /**
    * Recognize function declaration like "\n public static function a($arg1, ...) .. {" and return its token with preset
    * modifiers and parameters. Recognize function declaration like "\n public static function a(arg1, ...);" inside the interface
    * 
    * @param lineNumber
    * @param non-safe nodeStack
    * @return new function token
    */
   private TokenBeenImpl isFunctionDeclaration(Stack<Node> nodeStack, int lineNumber, TokenType currentTokenType)
   {
      if (nodeStack.size() > 4)
      {
         // test if this is inside the interface
         if (TokenType.INTERFACE.equals(currentTokenType))
         {
            // recognize function declaration like "public static function a(arg1, ...);" inside the interface
            if (!isSemicolonNode(nodeStack.pop()))
               return null;
         }
         else
         {
            // recognize function declaration like "public static function a($arg1, ...) .. {" and return its token with preset
            // modifiers and parameters.
            if (!isOpenBrace(nodeStack.pop()))
               return null;
         }

         // pass BR or whitespace between ") ....  {"
         while (nodeStack.size() > 4 && !isCloseBracket(nodeStack.lastElement()))
         {
            Node node = nodeStack.pop();

            // decrease line number if there is BR node between ") ....  {"
            if (node.isLineBreak())
            {
               lineNumber--;
            }
            // return if there is non-BR or non-whitespace node between ") ....  {"
            else if (!(node.isLineBreak() || isWhitespace(node)))
            {
               return null;
            }
         }

         // test if this is code like "function a(...)"
         if (isCloseBracket(nodeStack.pop()))
         {
            // read function parameters
            List<TokenBeenImpl> parameters = readFunctionParameters((Stack<Node>)nodeStack.clone(), lineNumber);

            // parse function name and function modifiers
            while (nodeStack.size() > 1)
            {

               // taking into consideration multi-line parameters definition like "($a, \n $b, ...)"
               if (nodeStack.lastElement().isLineBreak())
               {
                  lineNumber--;
               }

               // test if this is function name
               if (isFunctionName(nodeStack))
               {
                  TokenBeenImpl newToken =
                     new TokenBeenImpl(nodeStack.lastElement().getContent(), TokenType.FUNCTION, lineNumber,
                        MimeType.APPLICATION_PHP);

                  if (TokenType.CLASS.equals(currentTokenType) || TokenType.INTERFACE.equals(currentTokenType))
                  {
                     // replace FUNCTION on METHOD
                     newToken.setType(TokenType.METHOD);

                     nodeStack.setSize(nodeStack.size() - 2); // remove "function funcName" nodes
                     checkModifiers(newToken, nodeStack);
                  }

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
    * Return true if there is "\n function functionName" pattern at the top of nodeStack
    * 
    * @param nodeStack
    * @return
    */
   private boolean isFunctionName(Stack<Node> nodeStack)
   {
      return (nodeStack.size() > 1) && isPhpElementName(nodeStack.lastElement().getType())
         && isFunctionKeyword(nodeStack.get(nodeStack.size() - 2));
   }

   /**
    * Recognize parameters like '(util_FilePath $filename, $x, $types = array(), $coffeeMaker = NULL'. Return empty list if there
    * are no parameters or null, if there is syntax error.
    * 
    * @param nodeStack non-safe
    * @return parameters list
    */
   private List<TokenBeenImpl> readFunctionParameters(Stack<Node> nodeStack, int lineNumber)
   {
      List<TokenBeenImpl> parameters = new LinkedList<TokenBeenImpl>();

      // to ignore brackets from parameter initialization code like $types = array()
      boolean ignoreNextOpenBracket = false;

      while (nodeStack.size() > 0)
      {
         Node node = nodeStack.pop();

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
            TokenBeenImpl newParameter =
               new TokenBeenImpl(node.getContent(), TokenType.PARAMETER, lineNumber, MimeType.APPLICATION_PHP);
            checkTypeHinting((Stack<Node>)nodeStack.clone(), newParameter);
            parameters.add(0, newParameter); // insert parameter at the top of list
         }
      }

      return parameters;
   }

   /**
    * Recognize parameter type hinting like "function test(OtherClass $otherclass, /n array $inputArray)" and set elementType of
    * parameter.
    * 
    * @param nodeStack non-safe
    * @param parameter
    */
   private void checkTypeHinting(Stack<Node> nodeStack, TokenBeenImpl parameter)
   {
      Node possibleTypeNode = null;

      // pass BR or whitespace between ", .... Type $par"
      while (nodeStack.size() > 2 && !(isComma(nodeStack.lastElement()) || isOpenBracket(nodeStack.lastElement())))
      {
         Node node = nodeStack.pop();

         if (!(node.isLineBreak() || isWhitespace(node)))
         {
            possibleTypeNode = node;
         }
      }

      // check case like "(OtherClass" or ", OtherClass" or "(array" or ", array"
      if (possibleTypeNode != null
         && (isPhpElementName(possibleTypeNode.getType()) || isArrayKeyword(possibleTypeNode))
         && (isComma(nodeStack.lastElement()) || isOpenBracket(nodeStack.lastElement())))
      {
         parameter.setElementType(possibleTypeNode.getContent());
         return;
      }
   }

   /**
    * Recognize PHP reference on variable like "&$a"
    * 
    * @param nodeStack safe
    * @return
    */
   private boolean isReference(Stack<Node> nodeStack)
   {
      return (nodeStack.size() > 1) && isVariable(nodeStack.lastElement().getType())
         && isAmpersant(nodeStack.get(nodeStack.size() - 2));
   }

   /**
    * Recognize PHP variable node with class name "php-variable"
    * 
    * @param nodeType
    * @return
    */
   public static boolean isVariable(String nodeType)
   {
      return (nodeType != null) && (nodeType.startsWith("php-variable"));
   }

   /**
    * Recognize open brackets "("
    * 
    * @param node
    * @return
    */
   private boolean isOpenBracket(Node node)
   {
      return isPunctuation(node) && "(".equals(node.getContent());
   }

   /**
    * Recognize open brackets ")"
    * 
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

   /**
    * Recognize "class" keyword.
    * 
    * @param node
    * @return
    */
   private boolean isClassKeyword(Node node)
   {
      return isKeyword(node) && "class".equals(node.getContent());
   }

   /**
    * Recognize "function" keyword
    * 
    * @param node
    * @return
    */
   private boolean isFunctionKeyword(Node node)
   {
      return isKeyword(node) && "function".equals(node.getContent());
   }

   private static boolean isKeyword(Node node)
   {
      return (node.getType() != null) && (node.getType().startsWith("php-keyword"));
   }

   /**
    * Verify name of class, or function, or method, or interface, or namespace, or class constant (including predefined)
    * 
    * @param nodeType
    * @return
    */
   public static boolean isPhpElementName(String nodeType)
   {
      return (nodeType != null)
         && (nodeType.startsWith("php-t_string") || nodeType.startsWith("php-predefined-class") || nodeType
            .startsWith("php-predefined-function"));
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
    * Add newToken as subToken of currentToken, clear nodeStack.
    * 
    * @param currentToken
    * @param newToken
    */
   private void addSubToken(TokenBeenImpl currentToken, TokenBeenImpl newToken)
   {
      currentToken.addSubToken(newToken);
      mainNodeStack.clear();
   }

   /**
    * Clear context variables, set lastLineNumber property, clear nodeStack
    * 
    * @param lineNumber
    * @param currentToken
    * @return parent token of currentToken
    */
   private TokenBeenImpl closeToken(int lineNumber, TokenBeenImpl currentToken)
   {
      clearVariables(currentToken.getType());
      currentToken.setLastLineNumber(lineNumber);
      mainNodeStack.clear();

      if (currentToken.getParentToken() != null)
      {
         return currentToken.getParentToken();
      }

      return currentToken;
   }

   /**
    * Recognize ";" node
    * 
    * @param node
    * @return
    */
   private boolean isSemicolonNode(Node node)
   {
      return (node.getType() != null) && (node.getType().startsWith("php-punctuation"))
         && ";".equals(node.getContent());
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
    * 
    * @param node
    * @return
    */
   private boolean isEqualSign(Node node)
   {
      return (node.getType() != null) && node.getType().startsWith("php-operator") && "=".equals(node.getContent());
   }

   /**
    * Recognize sign ","
    * 
    * @param node
    * @return
    */
   private boolean isComma(Node node)
   {
      return "php-punctuation".equals(node.getType()) && ",".equals(node.getContent());
   }

   /**
    * Recognize "namespace" keyword
    * 
    * @param node
    * @return
    */
   private boolean isNamespaceKeyword(Node node)
   {
      return isKeyword(node) && "namespace".equals(node.getContent());
   }

   /**
    * Recognize "&" symbol
    * 
    * @param node
    * @return
    */
   private boolean isAmpersant(Node node)
   {
      return "php-operator".startsWith(node.getType()) && "&amp;".equals(node.getContent());
   }

   /**
    * Clear variable list within the method, or module, or class
    * 
    * @param currentTokenType
    */
   private void clearVariables(TokenType tokenType)
   {
      switch (tokenType)
      {
         case CLASS :
            variables.get(TokenType.CLASS).clear();

            break;

         case METHOD :
            variables.get(TokenType.METHOD).clear();
            break;

         default :
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

   /**
    * Recognize dynamic calling operator "->"
    * 
    * @param node
    * @return
    */
   public static boolean isDynamicCallingOperator(Node node)
   {
      return node.getType().startsWith("php-operator") && dynamicCallingOperator.equals(node.getContent());
   }

   /**
    * Recognize static calling operator "::"
    * 
    * @param node
    * @return
    */
   public static boolean isStaticCallingOperator(Node node)
   {
      return node.getType().startsWith("php-operator") && staticCallingOperator.equals(node.getContent());
   }

   /**
    * Recognize "$this" keyword
    * 
    * @param node
    * @return
    */
   public static boolean isThisKeyword(Node node)
   {
      return isKeyword(node) && "$this".equals(node.getContent());
   }
}