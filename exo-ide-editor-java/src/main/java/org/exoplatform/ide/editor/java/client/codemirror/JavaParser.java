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
package org.exoplatform.ide.editor.java.client.codemirror;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.editor.api.codeassitant.Modifier;
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
public class JavaParser extends CodeMirrorParserImpl
{

   private String lastNodeContent;

   private String lastNodeType;

   private final static String TRIANGLE_BRACKET = "<...>";

   /**
    * Position within the 'method(...)'
    */
   private boolean inMethodBrackets;

   /**
    * Position after the 'method()...'
    */
   private boolean wereMethodBrackets;

   /**
    * Position within the statement like "import java.lang.String;"
    */
   private boolean inImportStatement;

   /**
    * Position within the statement like "package java.lang.String;"
    */
   private boolean inPackageStatement;

   /**
    * Stack of blocks "{... {...} ...}"
    */
   private Stack<String> enclosers = new Stack<String>();

   /**
    * To store complex java types for properties/methods like "java.lang.String a;"
    */
   private String currentJavaType;

   /**
    * Possible annotations list
    */
   private TokenBeenImpl annotationStorage;

   /**
    * Possible methodParameter token
    */
   private TokenBeenImpl methodParameter;

   /**
    * Possible parameters list
    */
   private TokenBeenImpl parameterStorage;

   /**
    * To store java types for properties like "String a,b,c;"
    */
   private String lastJavaType;

   // java modifiers
   private List<Modifier> modifiers;

   @Override
   public void init()
   {
      super.init();

      inImportStatement = inPackageStatement = inMethodBrackets = wereMethodBrackets = false;
      lastNodeType = lastNodeContent = null;
      enclosers.clear();
      methodParameter = null;
      annotationStorage = parameterStorage = new TokenBeenImpl();
      currentJavaType = lastJavaType = "";
      modifiers = new LinkedList<Modifier>();
   }

   @Override
   public TokenBeenImpl parseLine(JavaScriptObject node, int lineNumber, TokenBeenImpl currentToken,
      boolean hasParentParser)
   {
      // interrupt at the end of the line or content
      if ((node == null) || Node.getName(node).equals("BR"))
      {
         currentJavaType = lastJavaType = ""; // clear variables to correct complex JavaTypes like 'java.lang.String'

         modifiers = new LinkedList<Modifier>();

         // clear variables after the end of the import or package statement
         if (inImportStatement || inPackageStatement)
         {
            inImportStatement = false;
            inPackageStatement = false;
         }

         lastNodeContent = lastNodeType = null;

         // pass lines with code like "a < b" with operators "<", "<<=", "<=", "<<"
         clearAllLastTriangularBrackets(enclosers);

         return currentToken;
      }

      String nodeContent = Node.getContent(node).trim(); // returns text without ended space " " in the text
      String nodeType = Node.getType(node);

      parsePackageOrImportStatement(lineNumber, currentToken, nodeContent, nodeType);

      checkModifiers(nodeType, nodeContent);

      // recognize "("
      if (isOpenBracket(nodeType, nodeContent))
      {
         currentJavaType = lastJavaType = ""; // clear type for cases like
                                              // "ShoppingCart cart = session.findByPath(ShoppingCart, name);"

         // recognize "(" within the annotation like '@PathParam("name")'
         if (isAnnotation(lastNodeType))
         {
            enclosers.push(TokenType.ANNOTATION.toString());

            if (!inMethodBrackets)
            {
               annotationStorage.lastAnnotationTokenNameConcat("("); // to display token like '@PathParam("pathParam")'
            }
            else
            {
               methodParameter.lastAnnotationTokenNameConcat("("); // to display token like 'hello(@PathParam("pathParam") String
                                                                   // par1'
            }
         }

         // filter code like this "boolean isSelected = false; /n for(category in categories) {"
         else
         {
            inMethodBrackets = false;
            wereMethodBrackets = false;

            if (!inMethodBraces(currentToken) && !isStartJavaStatement(lastNodeType, lastNodeContent))
            {
               inMethodBrackets = true;
            }
         }
      }

      // recognize ")"
      else if (isCloseBracket(nodeType, nodeContent))
      {
         wereMethodBrackets = false;

         // recognize "inAnnotationBrackets" firstly to filter annotation like 'String get(@PathParam("pathParam") String
         // pathParam) {...'
         if (inAnnotationBrackets(enclosers))
         {
            if (!enclosers.isEmpty())
            {
               enclosers.pop(); // recognize ")" within the annotation
            }

            if (!inMethodBrackets)
            {
               annotationStorage.lastAnnotationTokenNameConcat(")"); // to display token like '@PathParam("pathParam")'
            }
            else
            {
               methodParameter.lastAnnotationTokenNameConcat(")"); // to display token like 'hello(@PathParam("pathParam") String
                                                                   // par1'
            }
         }

         else if (inMethodBrackets)
         {
            inMethodBrackets = false;
            wereMethodBrackets = true;
         }
      }

      // recognize open brace "{"
      else if (isOpenBrace(nodeType, nodeContent))
      {
         currentJavaType = lastJavaType = ""; // clear type after new "{"

         // filter code like this "boolean isSelected = false; /n for(category in categories) {"
         if (wereMethodBrackets && currentToken.getLastSubToken() != null
            && TokenType.PROPERTY.equals(currentToken.getLastSubToken().getType())
            && !TokenType.INTERFACE.equals(currentToken.getType()))
         {
            transformPropertyOnMethod(currentToken);

            enclosers.push(TokenType.METHOD.toString());

            // set method as current token to add variables
            if (currentToken != null && currentToken.getLastSubToken() != null)
            {
               currentToken = currentToken.getLastSubToken();
            }
         }

         // filter open brace after the class or interface declaration like "class test { ..." or "interface test { ..."
         else if (!TokenType.CLASS.equals(currentToken.getType())
            && !TokenType.INTERFACE.equals(currentToken.getType()))
         {
            enclosers.push(TokenType.BLOCK.toString());
         }

         wereMethodBrackets = false;
      }

      // recognize close brace "}"
      else if (isCloseBrace(nodeType, nodeContent))
      {
         if (TokenType.CLASS.equals(currentToken.getType()) || TokenType.INTERFACE.equals(currentToken.getType())
            || TokenType.METHOD.equals(currentToken.getType()))
         {
            currentToken.setLastLineNumber(lineNumber);

            if (currentToken.getParentToken() != null
               && !enclosers.isEmpty()
               && (TokenType.METHOD.toString().equals(enclosers.lastElement())
                  || TokenType.CLASS.toString().equals(enclosers.lastElement()) || TokenType.INTERFACE.toString()
                  .equals(enclosers.lastElement())))
            {
               currentToken = currentToken.getParentToken();
            }
         }

         if (!enclosers.isEmpty())
         {
            enclosers.pop();
         }
      }

      // recognize ";" for method within the interface block like "String getValue(int param);"
      else if (isSemicolon(nodeType, nodeContent) && TokenType.INTERFACE.equals(currentToken.getType())
         && wereMethodBrackets && currentToken.getLastSubToken() != null
         && TokenType.PROPERTY.equals(currentToken.getLastSubToken().getType()))
      {
         transformPropertyOnMethod(currentToken);
      }

      else if (isOpenTriangleBracket(lastNodeType, lastNodeContent) && isEqualSign(nodeType, nodeContent) // pass "<=" signs and
                                                                                                          // fix enclosers tree
         || isOpenTriangleBracket(lastNodeType, lastNodeContent) && isOpenTriangleBracket(nodeType, nodeContent) // pass left
                                                                                                                 // shift operator
                                                                                                                 // "<<" in code
                                                                                                                 // like
                                                                                                                 // "col << row"
                                                                                                                 // and fix
                                                                                                                 // enclosers tree
      )
      {
         clearAllLastTriangularBrackets(enclosers);
         currentJavaType = "";
      }

      // recognize "<" or "<?" for java type;
      else if ((isOpenTriangleBracket(nodeType, nodeContent) || isOpenTriangleBracketAndQuestionSign(nodeType,
         nodeContent)) && isJavaVariable(lastNodeType))
      {
         // taking in mind type definition before first open bracket like "HashMap" in type "HashMap<String, List<String>>"
         if (enclosers.isEmpty() || !inTriangularBracket())
         {
            currentJavaType += lastNodeContent;
         }

         currentJavaType += Node.getContent(node).replaceAll("&lt;", "<"); // get node content with possible ended spaces

         enclosers.push(TRIANGLE_BRACKET);
      }

      // recognize ">" for java type
      else if (isCloseTriangleBracket(nodeType, nodeContent) && inTriangularBracket())
      {
         if (!enclosers.isEmpty())
         {
            enclosers.pop();
            currentJavaType += ">";
         }
      }

      // recognize ">>" for java type
      else if (isDoubleCloseTriangleBracket(nodeType, nodeContent) && inTriangularBracket())
      {
         if (enclosers.size() > 1)
         {
            enclosers.pop();
            enclosers.pop();
            currentJavaType += ">>";
         }
      }

      // parse parameterized types code between "<..>" like "Map<String, HashMap<String, Object>> ",
      // "ItemTreeGrid<T extends Item>" etc.
      else if (inTriangularBracket())
      {
         currentJavaType += Node.getContent(node).replaceAll("  ", " "); // taking in mind spaces in code "<? extends Tree>"
      }

      // parse elements not within the "{}" of method
      else
      {
         if (!isWhitespace(nodeType))
         {
            // filter ") throws java.lang.IllegalAccessException" in code like
            // "public Hello() throws java.lang.IllegalAccessException \n { ..."
            if (wereMethodBrackets //
               && (isJavaKeyword(nodeType) && nodeContent.equals("throws") // filter "throws" keyword
                  || isJavaKeyword(lastNodeType) && isJavaVariable(nodeType) // filter "throws IllegalAccessException" keyword
                  || isJavaVariable(lastNodeType) && isPoint(nodeType, nodeContent) // filter "java." keyword
               || isPoint(lastNodeType, lastNodeContent) && isJavaVariable(nodeType) // filter ".lang" keyword
               ))
            {
            }
            else
            {
               wereMethodBrackets = false;
            }
         }

         // recognize types like this "java.lang.String a"
         if (isPoint(nodeType, nodeContent) && !inAnnotationBrackets(enclosers) && isJavaVariable(lastNodeType))
         {
            currentJavaType += lastNodeContent + ".";
         }

         // parse elements within the "()" of method
         else if (inMethodBrackets)
         {
            // parse annotations inside the method brackets like 'get(@PathParam("pathParam") String pathParam) {'
            if (isAnnotation(nodeType))
            {
               // to recognize several annotations for one parameter like 'public java.lang.String post(@PathParam("pathParam")
               // @DefaultValue("pathParam Default") String pathParam,'
               if (methodParameter == null)
               {
                  methodParameter = new TokenBeenImpl(null, TokenType.PARAMETER, 0, MimeType.APPLICATION_JAVA, null);
               }

               methodParameter.addAnnotation(new TokenBeenImpl(nodeContent, TokenType.ANNOTATION, lineNumber,
                  MimeType.APPLICATION_JAVA, getAnnotationJavaType(nodeContent)));
            }

            // recognize content within the brackets of java annotation within the method brackets
            else if (inAnnotationBrackets(enclosers))
            {
               methodParameter.lastAnnotationTokenNameConcat(nodeContent); // to display token like '@PathParam("pathParam")'
            }

            // recognize method's parameter like
            // "hello(String par1, int par2, List<Item> par3,  List<Item> par3, Collection<HashMap<String,String>>par4, ...)"
            else if ((isJavaVariable(lastNodeType) || isJavaType(lastNodeType, lastNodeContent) || isSingleOrDoubleCloseTriangleBracket(
               lastNodeType, lastNodeContent) && !currentJavaType.equals(""))
               && !isComma(nodeType, nodeContent))
            {
               // taking in mind "String" type, not "List<Item>"
               if (!isSingleOrDoubleCloseTriangleBracket(lastNodeType, lastNodeContent))
               {
                  currentJavaType += lastNodeContent;
               }

               if (methodParameter == null)
               {
                  methodParameter =
                     new TokenBeenImpl(nodeContent, TokenType.PARAMETER, lineNumber, MimeType.APPLICATION_JAVA,
                        currentJavaType);
               }
               else
               {
                  methodParameter.setName(nodeContent);
                  methodParameter.setLineNumber(lineNumber);
                  methodParameter.setElementType(currentJavaType);
               }

               parameterStorage.addParameter(methodParameter);
               methodParameter = null;

               currentJavaType = "";
            }

         }

         // parse elements outside the "()" of method
         else
         {
            // parse annotations outside the method brackets like '@Override /n get(String pathParam) {'
            if (isAnnotation(nodeType))
            {
               annotationStorage.addAnnotation(new TokenBeenImpl(nodeContent, TokenType.ANNOTATION, lineNumber,
                  MimeType.APPLICATION_JAVA, getAnnotationJavaType(nodeContent)));
            }

            // recognize content within the brackets of java annotation outside the method brackets
            else if (inAnnotationBrackets(enclosers))
            {
               annotationStorage.lastAnnotationTokenNameConcat(nodeContent); // to display token like '@PathParam("pathParam")'
            }

            // filter variable
            else if (isJavaVariable(nodeType))
            {
               // recognize "class" or "interface" token
               if (isJavaClassNode(lastNodeType, lastNodeContent) || isJavaInterfaceNode(lastNodeType, lastNodeContent))
               {
                  TokenBeenImpl newToken =
                     new TokenBeenImpl(nodeContent, TokenType.valueOf(lastNodeContent.toUpperCase()), lineNumber,
                        MimeType.APPLICATION_JAVA, null, modifiers);

                  // set collected earlier annotations in case of @Path("/my-service1"), public class HelloWorld {
                  setPossibleAnnotations(newToken);

                  currentToken.addSubToken(newToken);
                  currentToken = newToken;

                  enclosers.push(TokenType.valueOf(lastNodeContent.toUpperCase()).toString());
               }

               // recognize variable/method declaration like "String hello(..." or "boolean hello", or "List<Item>"
               else if (isJavaVariable(lastNodeType)
                  || isJavaType(lastNodeType, lastNodeContent)
                  || (isSingleOrDoubleCloseTriangleBracket(lastNodeType, lastNodeContent) && !currentJavaType
                     .equals("")))
               {
                  // taking in mind "String" type, not "List<Item>"
                  if (!isSingleOrDoubleCloseTriangleBracket(lastNodeType, lastNodeContent))
                  {
                     currentJavaType += lastNodeContent;
                  }

                  currentToken.addSubToken(new TokenBeenImpl(nodeContent, (inMethodBraces(currentToken)
                     ? TokenType.VARIABLE : TokenType.PROPERTY), lineNumber, MimeType.APPLICATION_JAVA,
                     currentJavaType, modifiers));

                  // set collected earlier annotations in case of '@Mandatory @MappedBy("product") Product product'
                  setPossibleAnnotations(currentToken.getLastSubToken());

                  lastJavaType = currentJavaType;
                  currentJavaType = "";
               }

               // recognize variables like this "String a, b, c;"
               else if (isComma(lastNodeType, lastNodeContent)
                  && currentToken.getLastSubToken() != null
                  && !lastJavaType.isEmpty()
                  && currentToken.getLastSubToken().getLineNumber() == lineNumber
                  && (TokenType.VARIABLE.equals(currentToken.getLastSubToken().getType()) || TokenType.PROPERTY
                     .equals(currentToken.getLastSubToken().getType())))
               {
                  currentToken.addSubToken(new TokenBeenImpl(nodeContent, (inMethodBraces(currentToken)
                     ? TokenType.VARIABLE : TokenType.PROPERTY), lineNumber, MimeType.APPLICATION_JAVA, lastJavaType,
                     modifiers));

                  // set collected earlier annotations in case of '@Mandatory @MappedBy("product") Product a, b ...'
                  setPossibleAnnotations(currentToken.getLastSubToken());
               }

               // recognize constructor with at least one modifier and with name the same as of container-class name, e.g.
               // CartController in code like "class Controller { public CartController"
               else if (!inMethodBrackets && modifiers.size() > 0 && TokenType.CLASS.equals(currentToken.getType())
                  && currentToken.getName().equals(nodeContent))
               {
                  currentToken.addSubToken(new TokenBeenImpl(nodeContent, TokenType.PROPERTY, lineNumber,
                     MimeType.APPLICATION_JAVA, currentToken.getName(), modifiers));

                  // set collected earlier annotations in case of '@Mandatory @MappedBy("product") public Product'
                  setPossibleAnnotations(currentToken.getLastSubToken());

                  lastJavaType = currentJavaType = "";
               }
            }
         }
      }

      // filter whitespaces in code like "method() \n   {}" or "method( \n  String param \n ) \n   {}"
      if (!isWhitespace(nodeType))
      {
         lastNodeContent = nodeContent;
         lastNodeType = nodeType;
      }

      if (hasParentParser)
      {
         return currentToken; // return current token to parent parser
      }

      return parseLine(Node.getNext(node), lineNumber, currentToken, false);
   }

   private void checkModifiers(String nodeType, String nodeContent)
   {
      if (isJavaModifier(nodeType, nodeContent) && (nodeContent != null))
      {
         // trying to get found java modifier from Modifier enum and add this modifier into the 'modifiers' property
         try
         {
            Modifier modifier = Modifier.valueOf(nodeContent.toUpperCase());
            if (!modifiers.contains(modifier))
            {
               modifiers.add(modifier);
            }
         }
         catch (IllegalArgumentException ex)
         {
         }
      }
   }

   /**
    * Parse package statement like this "package java.lang.String" or import statement like this "import java.lang.String"
    * 
    * @param lineNumber
    * @param currentToken
    * @param nodeContent
    * @param nodeType
    */
   private void parsePackageOrImportStatement(int lineNumber, TokenBeenImpl currentToken, String nodeContent,
      String nodeType)
   {
      if (isJavaPackageStatement(nodeType, nodeContent))
      {
         inPackageStatement = true;
      }

      else if (isJavaImportStatement(nodeType, nodeContent))
      {
         inImportStatement = true;
      }

      else if (inPackageStatement || inImportStatement)
      {
         // recognize started "package java" or "import java"
         if ((isJavaPackageStatement(lastNodeType, lastNodeContent) || isJavaImportStatement(lastNodeType,
            lastNodeContent)) && isJavaVariable(nodeType))
         {
            currentToken.addSubToken(new TokenBeenImpl(null,
               (inPackageStatement ? TokenType.PACKAGE : TokenType.IMPORT), lineNumber, MimeType.APPLICATION_JAVA,
               nodeContent));
         }

         // recognize end of package or import statement like this ".String"
         else if (isJavaVariable(nodeType) && isPoint(lastNodeType, lastNodeContent))
         {
            if (currentToken.getLastSubToken() != null)
            {
               // update java type of lastSubToken of current token
               currentToken.getLastSubToken().setElementType(
                  currentToken.getLastSubToken().getElementType() + "." + nodeContent);
            }
         }

         // recognize the end of package or import statement
         else if (!isJavaVariable(lastNodeType) && isPoint(nodeType, nodeContent)) // filter package or import statement like this
                                                                                   // "java."
         {
            inPackageStatement = false;
            inImportStatement = false;
         }
      }
   }

   /**
    * 
    * @param nodeContent
    * @return "Path" for node content "@Path"
    */
   private String getAnnotationJavaType(String nodeContent)
   {
      return nodeContent.substring(1);
   }

   private boolean isJavaImportStatement(String nodeType, String nodeContent)
   {
      return isJavaKeyword(nodeType) && (nodeContent != null) && nodeContent.equals("import");
   }

   private boolean isJavaPackageStatement(String nodeType, String nodeContent)
   {
      return isJavaKeyword(nodeType) && (nodeContent != null) && nodeContent.equals("package");
   }

   /**
    * Transform currentToken from Property Token to Method Token
    * 
    * @param currentToken
    */
   private void transformPropertyOnMethod(TokenBeenImpl currentToken)
   {
      // replace last sub token type from variable to method
      currentToken.updateTypeOfLastSubToken(TokenType.METHOD);

      // set collected earlier parameters in case of 'String hello(String par1, int par2 ...){...'
      setPossibleParameters(currentToken.getLastSubToken());
   }

   /**
    * Add collected earlier annotations from annotationStorage into token
    * 
    * @param token
    */
   private void setPossibleAnnotations(TokenBeenImpl token)
   {
      if (annotationStorage != null && token != null)
      {
         token.setAnnotations(annotationStorage.getAnnotations());
      }

      // clear annotationStorage
      annotationStorage = new TokenBeenImpl();
   }

   /**
    * Add collected earlier parameters from parameterStorage into token
    * 
    * @param token
    */
   private void setPossibleParameters(TokenBeenImpl token)
   {
      if (parameterStorage != null && token != null)
      {
         token.setParameters(parameterStorage.getParameters());
      }

      // clear parameterStorage
      parameterStorage = new TokenBeenImpl();
   }

   /**
    * Recognize java keywords 'if', 'switch', 'while', 'else', 'do', 'try', 'finally', 'break', 'continue', 'extends',
    * 'implements', 'import', 'new', 'package', 'return', 'super', 'this', 'throws'
    * 
    * @param nodeType
    * @return
    */
   private boolean isJavaKeyword(String nodeType)
   {
      return (nodeType != null) && nodeType.equals("java-keyword");
   }

   /**
    * Recognize java keywords 'abstract', 'final', 'native', 'private', 'protected', 'public', 'static', 'strictfp',
    * 'synchronized', 'threadsafe', 'transient', 'volatile'
    * 
    * @param nodeType
    * @return
    */
   private boolean isJavaModifier(String nodeType, String nodeContent)
   {
      return (nodeType != null)
         && nodeType.equals("java-keyword")
         && ("abstract".equals(nodeContent) || "final".equals(nodeContent) || "private".equals(nodeContent)
            || "protected".equals(nodeContent) || "public".equals(nodeContent) || "static".equals(nodeContent)
            || "strictfp".equals(nodeContent) || "synchronized".equals(nodeContent) || "threadsafe".equals(nodeContent)
            || "transient".equals(nodeContent) || "volatile".equals(nodeContent));
   }

   /**
    * Recognize sign ","
    * 
    * @param nodeType
    * @param nodeContent
    * @return
    */
   private boolean isComma(String nodeType, String nodeContent)
   {
      return "java-punctuation".equals(nodeType) && ",".equals(nodeContent);
   }

   /**
    * Recognize sign "."
    * 
    * @param nodeType
    * @param nodeContent
    * @return
    */
   public static boolean isPoint(String nodeType, String nodeContent)
   {
      return "java-punctuation".equals(nodeType) && ".".equals(nodeContent);
   }

   /**
    * Recognize sign ";"
    * 
    * @param nodeType
    * @param nodeContent
    * @return
    */
   private boolean isSemicolon(String nodeType, String nodeContent)
   {
      return "java-punctuation".equals(nodeType) && ";".equals(nodeContent);
   }

   /**
    * Recognize java type keywords 'boolean', 'byte', 'char', 'enum', 'double', 'float', 'int', 'long', 'short', 'void'
    * 
    * @param nodeType
    * @param nodeContent
    * @return
    */
   private boolean isJavaType(String nodeType, String nodeContent)
   {
      return (nodeType != null)
         && nodeType.equals("java-keyword")
         && ("boolean".equals(nodeContent) || "byte".equals(nodeContent) || "char".equals(nodeContent)
            || "enum".equals(nodeContent) || "double".equals(nodeContent) || "float".equals(nodeContent)
            || "int".equals(nodeContent) || "long".equals(nodeContent) || "short".equals(nodeContent) || "void"
            .equals(nodeContent));
   }

   /**
    * Recognize "{"
    * 
    * @return true if there is open braces of method definition
    */
   private boolean isOpenBrace(String nodeType, String nodeContent)
   {
      return "java-punctuation".equals(nodeType) && "{".equals(nodeContent);
   }

   /**
    * Recognize "}"
    */
   private boolean isCloseBrace(String nodeType, String nodeContent)
   {
      return "java-punctuation".equals(nodeType) && "}".equals(nodeContent);
   }

   public static boolean isJavaVariable(String nodeType)
   {
      return "java-variable".equals(nodeType);
   }

   /**
    * Recognize "=" operation
    * 
    * @param nodeType
    * @param nodeContent
    * @return
    */
   private boolean isEqualSign(String nodeType, String nodeContent)
   {
      return (nodeType != null) && (nodeContent != null) && nodeType.equals("java-operator") && nodeContent.equals("=");
   }

   /**
    * Recognize open brackets "("
    * 
    * @param nodeType
    * @param nodeContent
    * @return
    */
   private boolean isOpenBracket(String nodeType, String nodeContent)
   {
      return (nodeType != null) && (nodeContent != null) && nodeType.equals("java-punctuation")
         && nodeContent.equals("(");
   }

   /**
    * Recognize open brackets ")"
    * 
    * @param nodeType
    * @param nodeContent
    * @return
    */
   private boolean isCloseBracket(String nodeType, String nodeContent)
   {
      return (nodeType != null) && (nodeContent != null) && nodeType.equals("java-punctuation")
         && nodeContent.equals(")");
   }

   private boolean isJavaClassNode(String nodeType, String nodeContent)
   {
      return (nodeType != null) && (nodeContent != null) && nodeType.equals("java-keyword")
         && nodeContent.equals("class");
   }

   private boolean isJavaInterfaceNode(String nodeType, String nodeContent)
   {
      return (nodeType != null) && (nodeContent != null) && nodeType.equals("java-keyword")
         && nodeContent.equals("interface");
   }

   /**
    * Recognize annotation like '@Path("/my-service")'
    * 
    * @param nodeType
    * @return
    */
   private boolean isAnnotation(String nodeType)
   {
      return (nodeType != null) && nodeType.equals("java-annotation");
   }

   /**
    * Recognize keyword "catch, for, if, switch, while" in statement like 'catch (..) {', 'for(..) {', 'if (..) {', 'switch (...)
    * {', 'while (...) {'
    */
   private boolean isStartJavaStatement(String nodeType, String nodeContent)
   {
      return isJavaKeyword(nodeType)
         && (nodeContent != null)
         && (nodeContent.equals("catch") || nodeContent.equals("for") || nodeContent.equals("if") || nodeContent
            .equals("while"));

   }

   /**
    * @param nodeType
    * @return <b>true</b> only if this is the nodeType = 'whitespace'
    */
   private boolean isWhitespace(String nodeType)
   {
      return "whitespace".equals(nodeType);
   }

   /**
    * Position within the method(){...}
    */
   private boolean inMethodBraces(TokenBeenImpl currentToken)
   {
      return TokenType.METHOD.equals(currentToken.getType());
   }

   /**
    * Position within the '@Path(...)'
    */
   private boolean inAnnotationBrackets(Stack<String> enclosers)
   {
      return !enclosers.isEmpty() && TokenType.ANNOTATION.toString().equals(enclosers.lastElement());
   }

   /**
    * Is current node between "<...>" brackets
    * 
    * @return
    */
   private boolean inTriangularBracket()
   {
      return !enclosers.isEmpty() && TRIANGLE_BRACKET.equals(enclosers.lastElement());
   }

/**
    * Recognize open brackets "<"
    * @param node
    */
   private boolean isOpenTriangleBracket(String nodeType, String nodeContent)
   {
      return "java-operator".equals(nodeType) && "&lt;".equals(nodeContent);
   }

   /**
    * Recognize word "<?"
    * 
    * @param node
    */
   private boolean isOpenTriangleBracketAndQuestionSign(String nodeType, String nodeContent)
   {
      return "java-operator".equals(nodeType) && "&lt;?".equals(nodeContent);
   }

   /**
    * Recognize close brackets ">"
    * 
    * @param node
    */
   private boolean isCloseTriangleBracket(String nodeType, String nodeContent)
   {
      return "java-operator".equals(nodeType) && "&gt;".equals(nodeContent);
   }

   /**
    * Recognize close brackets ">>"
    * 
    * @param node
    */
   private boolean isDoubleCloseTriangleBracket(String nodeType, String nodeContent)
   {
      return "java-operator".equals(nodeType) && "&gt;&gt;".equals(nodeContent);
   }

   /**
    * Recognize single ">" or double ">>" close triangular bracket
    * 
    * @param nodeType
    * @param nodeContent
    * @return
    */
   private boolean isSingleOrDoubleCloseTriangleBracket(String nodeType, String nodeContent)
   {
      return isCloseTriangleBracket(nodeType, nodeContent) || isDoubleCloseTriangleBracket(nodeType, nodeContent);
   }

   /**
    * Remove all last triangular bracket enclosers
    * 
    * @param enclosers
    */
   private void clearAllLastTriangularBrackets(Stack<String> enclosers)
   {
      while (!enclosers.isEmpty() && TRIANGLE_BRACKET.toString().equals(enclosers.lastElement()))
      {
         enclosers.pop();
      }
   }
}