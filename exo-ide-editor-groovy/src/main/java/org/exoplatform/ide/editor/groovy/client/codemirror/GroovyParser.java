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
package org.exoplatform.ide.editor.groovy.client.codemirror;

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
public class GroovyParser extends CodeMirrorParserImpl
{

   private String lastNodeContent;

   private String lastNodeType;

   /**
    * Position within the 'method(...)'
    */   
   private boolean inMethodBrackets;

   /**
    * Position after the 'method()...'
    */   
   private boolean wereMethodBrackets;   
   
   /**
    * Position within the statement like "import java.lang.String"
    */
   private boolean inImportStatement;

   /**
    * Position within the statement like "package java.lang.String"
    */
   private boolean inPackageStatement;
   
   /**
    * Position within the statement like "def String a"
    */
   private boolean inDefStatement; 

   /**
    * Stack of blocks "{... {...} ...}"
    */
   private Stack<TokenType> enclosers = new Stack<TokenType>();
   
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

      inImportStatement = inPackageStatement = inMethodBrackets = wereMethodBrackets = inDefStatement = false;
      lastNodeType = lastNodeContent = null;
      enclosers.clear();
      methodParameter = null;
      annotationStorage = parameterStorage = new TokenBeenImpl();
      currentJavaType = lastJavaType = "";
      modifiers = new LinkedList<Modifier>();  
   }

   @Override
   public TokenBeenImpl parseLine(JavaScriptObject node, int lineNumber, TokenBeenImpl currentToken, boolean hasParentParser)
   {
      // interrupt at the end of the line or content
      if ((node == null) || Node.getName(node).equals("BR"))
      {
         currentJavaType = lastJavaType = "";  // clear variables to correct complex JavaTypes like 'java.lang.String'
         
//         inAnnotationBrackets = false;  // prevent multilines annotations
         
         modifiers = new LinkedList<Modifier>();
         
         // clear variables after the end of line 
         inImportStatement = inPackageStatement = inDefStatement = false;
         
         lastNodeContent = lastNodeType = null;
         
         return currentToken;
      }

      String nodeContent = Node.getContent(node).trim(); // returns text without ended space " " in the text
      String nodeType = Node.getType(node);      
      
      parsePackageOrImportStatement(lineNumber, currentToken, nodeContent, nodeType);
      
      checkModifiers(nodeType, nodeContent);
      
      // recognize "("
      if (isOpenBracket(nodeType, nodeContent))
      {        
         // recognize "(" within the annotation like '@PathParam("name")'
         if (isAnnotation(lastNodeType))
         {
            enclosers.push(TokenType.ANNOTATION);
            
            if (!inMethodBrackets)
            {
               annotationStorage.lastAnnotationTokenNameConcat("(");  // to display token like '@PathParam("pathParam")'
            }
            else
            {
               methodParameter.lastAnnotationTokenNameConcat("(");  // to display token like 'hello(@PathParam("pathParam") String par1'
            }
         }
         
         // filter code like this "boolean isSelected = false; /n for(category in categories) {"
         else 
         {  
            inMethodBrackets = false;
            wereMethodBrackets = false;
            
            if (! inMethodBraces(currentToken)
                     && ! isStartJavaStatement(lastNodeType, lastNodeContent))
            {
               inMethodBrackets = true;
            }
         }
      }

      // recognize ")"      
      else if (isCloseBracket(nodeType, nodeContent))
      {  
         wereMethodBrackets = false;
         
         // recognize "inAnnotationBrackets" firstly to filter annotation like 'String get(@PathParam("pathParam") String pathParam) {...'
         if (inAnnotationBrackets(enclosers))
         {
            if (! enclosers.isEmpty())
            {
               enclosers.pop();  // recognize ")" within the annotation
            }
            
            if (!inMethodBrackets)
            {
               annotationStorage.lastAnnotationTokenNameConcat(")");  // to display token like '@PathParam("pathParam")'
            }
            else
            {
               methodParameter.lastAnnotationTokenNameConcat(")");  // to display token like 'hello(@PathParam("pathParam") String par1'
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
         currentJavaType = lastJavaType = "";  // clear type for cases like "cart.items.each { ItemToPurchase item ->"
         
         // filter code like this "boolean isSelected = false; /n for(category in categories) {"
         if (wereMethodBrackets
                    && currentToken.getLastSubToken() != null
                    && TokenType.PROPERTY.equals(currentToken.getLastSubToken().getType())
                    && ! TokenType.INTERFACE.equals(currentToken.getType())
                 )
         {            
            transformPropertyOnMethod(currentToken);                     

            enclosers.push(TokenType.METHOD);
            
            // set method as current token to add variables
            if (currentToken != null
                   && currentToken.getLastSubToken() != null)
            {
               currentToken = currentToken.getLastSubToken();
            }
         }
         
         // filter open brace after the class or interface declaration like "class test { ..." or "interface test { ..."  
         else if (! TokenType.CLASS.equals(currentToken.getType())
                    &&  ! TokenType.INTERFACE.equals(currentToken.getType())
                 )
         {
            enclosers.push(TokenType.BLOCK);
         }
         
         wereMethodBrackets = false;
      }
      
      // recognize close brace "}"      
      else if (isCloseBrace(nodeType, nodeContent))
      {                 
         if (TokenType.CLASS.equals(currentToken.getType()) 
               || TokenType.INTERFACE.equals(currentToken.getType()) 
               || TokenType.METHOD.equals(currentToken.getType())
            )
         {
            currentToken.setLastLineNumber(lineNumber);
            
            if (currentToken.getParentToken() != null
                   && !enclosers.isEmpty() 
                   && (TokenType.METHOD.equals(enclosers.lastElement()) 
                            || TokenType.CLASS.equals(enclosers.lastElement()) 
                            || TokenType.INTERFACE.equals(enclosers.lastElement())
                      )
               )
            {         
               currentToken = currentToken.getParentToken();                
            }
         }
         
         if (! enclosers.isEmpty()) 
         {
            enclosers.pop();
         }
      }
      
      // recognize ";" for method within the interface block like "String getValue(int param);"
      else if (isSemicolon(nodeType, nodeContent) 
               && TokenType.INTERFACE.equals(currentToken.getType())
               && wereMethodBrackets 
               && currentToken.getLastSubToken() != null
               && TokenType.PROPERTY.equals(currentToken.getLastSubToken().getType()))
      {
         transformPropertyOnMethod(currentToken);
      }
      
      // parse elements not within the "{}" of method
//      else if (!inMethodBraces)
      else
      {
         if (! isWhitespace(nodeType))
         {
            wereMethodBrackets = false;
         }
         
         // recognize types like this "java.lang.String a" 
         if (isPoint(nodeType, nodeContent)
                  && ! inAnnotationBrackets(enclosers)
                  && isGroovyVariable(lastNodeType))
         {
               currentJavaType += lastNodeContent + ".";
         }
         
         // parse elements within the "()" of method 
         else if (inMethodBrackets)
         {            
            // parse annotations inside the method brackets like 'get(@PathParam("pathParam") String pathParam) {'
            if (isAnnotation(nodeType))  
            {
               // to recognize several annotations for one parameter like 'public java.lang.String post(@PathParam("pathParam") @DefaultValue("pathParam Default") String pathParam,' 
               if (methodParameter == null)
               {
                  methodParameter = new TokenBeenImpl(null, TokenType.PARAMETER, 0, MimeType.APPLICATION_GROOVY, null);
               }
               
               methodParameter.addAnnotation(new TokenBeenImpl(
                  nodeContent, 
                  TokenType.ANNOTATION, 
                  lineNumber, 
                  MimeType.APPLICATION_GROOVY, 
                  getAnnotationJavaType(nodeContent)
                ));
            }
            
            // recognize content within the brackets of java annotation within the method brackets
            else if (inAnnotationBrackets(enclosers)) 
            {
               methodParameter.lastAnnotationTokenNameConcat(nodeContent); // to display token like '@PathParam("pathParam")'
            }
                        
            // recognize method's parameter like "hello(String par1, int par2, ..."
            else if ((isGroovyVariable(lastNodeType) || isJavaType(lastNodeType))
                       && !isComma(nodeType, nodeContent)
                    )
            {
               currentJavaType += lastNodeContent;

               if (methodParameter == null)
               {
                  methodParameter = new TokenBeenImpl(nodeContent, TokenType.PARAMETER, lineNumber,
                     MimeType.APPLICATION_GROOVY, currentJavaType);
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
               annotationStorage.addAnnotation(new TokenBeenImpl(
                  nodeContent, 
                  TokenType.ANNOTATION, 
                  lineNumber, 
                  MimeType.APPLICATION_GROOVY, 
                  getAnnotationJavaType(nodeContent)
               ));
            }
            
            // recognize content within the brackets of java annotation outside the method brackets
            else if (inAnnotationBrackets(enclosers)) 
            {
               annotationStorage.lastAnnotationTokenNameConcat(nodeContent); // to display token like '@PathParam("pathParam")'
            }
            
            // filter variable
            else if (isGroovyVariable(nodeType)) 
            {
               // recognize "class" or "interface" token
               if (isJavaClassNode(lastNodeType, lastNodeContent) || isJavaInterfaceNode(lastNodeType, lastNodeContent))
               {
                  TokenBeenImpl newToken = new TokenBeenImpl(nodeContent, TokenType.valueOf(lastNodeContent.toUpperCase()), lineNumber, MimeType.APPLICATION_GROOVY, null, modifiers);
   
                  // set collected earlier annotations in case of @Path("/my-service1"),  public class HelloWorld {
                  setPossibleAnnotations(newToken);
                  
                  currentToken.addSubToken(newToken);            
                  currentToken = newToken;
                  
                  enclosers.push(TokenType.valueOf(lastNodeContent.toUpperCase()));
               }
      
               // recognize variable/method definition "def var = "
               else if (isGroovyDef(lastNodeType, lastNodeContent))
               {
                  currentToken
                     .addSubToken(new TokenBeenImpl(nodeContent, 
                        (inMethodBraces(currentToken) ? TokenType.VARIABLE : TokenType.PROPERTY), 
                        lineNumber, 
                        MimeType.APPLICATION_GROOVY)
                     );

                  // set collected earlier annotations in case of '@Mandatory @MappedBy("product") def Product product'
                  setPossibleAnnotations(currentToken.getLastSubToken());
                  inDefStatement = true;
               }
      
               // recognize variable/method declaration like "String hello(..." or "boolean hello"
               else if (isGroovyVariable(lastNodeType) || isJavaType(lastNodeType))
               {
                  currentJavaType += lastNodeContent;
                  
                  // update property token in case like "def String a"; so, the currentToken.lastSubToken() should be with name "String" and type of PROPERTY or VARIABLE 
                  if (inDefStatement)
                  {
                     if (currentToken.getLastSubToken() != null 
                          && (currentToken.getLastSubToken().getType() == TokenType.PROPERTY
                              || currentToken.getLastSubToken().getType() == TokenType.VARIABLE)
                        )
                     {
                        currentToken.getLastSubToken().setName(nodeContent);
                        currentToken.getLastSubToken().setElementType(currentJavaType);
                     }                     
                  }
                  else
                  {
                     currentToken.addSubToken(new TokenBeenImpl(
                        nodeContent, 
                        (inMethodBraces(currentToken) ? TokenType.VARIABLE : TokenType.PROPERTY), 
                        lineNumber,
                        MimeType.APPLICATION_GROOVY, 
                        currentJavaType,
                        modifiers
                     ));
                     
                     // set collected earlier annotations in case of '@Mandatory @MappedBy("product") Product product'
                     setPossibleAnnotations(currentToken.getLastSubToken());
                  }
                                 
                  lastJavaType = currentJavaType;
                  currentJavaType = "";
               }
      
               // recognize variables like this "String a, b, c;" 
               else if (isComma(lastNodeType, lastNodeContent)
                        && currentToken.getLastSubToken() != null
                        && currentToken.getLastSubToken().getLineNumber() == lineNumber
                        && (TokenType.VARIABLE.equals(currentToken.getLastSubToken().getType())
                              || TokenType.PROPERTY.equals(currentToken.getLastSubToken().getType())
                            )
                        )
               {
                  currentToken.addSubToken(new TokenBeenImpl(
                     nodeContent, 
                     (inMethodBraces(currentToken) ? TokenType.VARIABLE : TokenType.PROPERTY), 
                     lineNumber,
                     MimeType.APPLICATION_GROOVY, 
                     lastJavaType,
                     modifiers
                  ));
                  
                  // set collected earlier annotations in case of '@Mandatory @MappedBy("product") Product a, b ...'
                  setPossibleAnnotations(currentToken.getLastSubToken());
               }
            }
         }
      }   
      
      // filter whitespaces in code like "method() \n   {}" or "method( \n  String param \n ) \n   {}"
      if (! isWhitespace(nodeType))
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
      if (isJavaModifier(nodeType) 
               && (nodeContent != null) )
      {
         // trying to get found java modifier from Modifier enum and add this modifier into the 'modifiers' property 
         try
         {
            Modifier modifier = Modifier.valueOf(nodeContent.toUpperCase());
            if (! modifiers.contains(modifier))
            {
               modifiers.add(modifier);
            }
         }
         catch(IllegalArgumentException ex)
         {
         }
      }
   }

   /**
    * Parse package statement like this "package java.lang.String" or import statement like this "import java.lang.String"
    * @param lineNumber
    * @param currentToken
    * @param nodeContent
    * @param nodeType
    */
   private void parsePackageOrImportStatement(int lineNumber, TokenBeenImpl currentToken, String nodeContent, String nodeType)
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
         if ((isJavaPackageStatement(lastNodeType, lastNodeContent) || isJavaImportStatement(lastNodeType, lastNodeContent))
               && isGroovyVariable(nodeType)) 
         {
            currentToken.addSubToken(new TokenBeenImpl(
               null, 
               (inPackageStatement ? TokenType.PACKAGE : TokenType.IMPORT), 
               lineNumber, 
               MimeType.APPLICATION_GROOVY, 
               nodeContent
            ));
         }

         // recognize end of package or import statement like this ".String"
         else if (isGroovyVariable(nodeType) && isPoint(lastNodeType, lastNodeContent) )
         {
            if (currentToken.getLastSubToken() != null)
            {
               // update java type of lastSubToken of current token 
               currentToken.getLastSubToken().setElementType(currentToken.getLastSubToken().getElementType() + "." + nodeContent);
            }
         }
         
         // recognize the end of package or import statement
         else if (! isGroovyVariable(lastNodeType) && isPoint(nodeType, nodeContent)) // filter package or import statement like this "java." 
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
    * Recognize java keywords 'if', 'switch', 'while', 'else', 'do', 'try', 'finally', 'break', 'continue', 'extends', 'implements', 'import', 'new', 'package', 'return', 'super', 'this', 'throws'
    * @param nodeType
    * @return
    */
   private boolean isJavaKeyword(String nodeType)
   {
      return (nodeType != null) && nodeType.equals("javaKeyword");
   }

   /**
    * Recognize java keywords 'abstract', 'final', 'native', 'private', 'protected', 'public', 'static', 'strictfp', 'synchronized', 'threadsafe', 'transient', 'volatile'
    * @param nodeType
    * @return
    */
   private boolean isJavaModifier(String nodeType)
   {
      return (nodeType != null) && nodeType.equals("javaModifier");
   }
   
   /**
    * Recognize sign ","
    * @param nodeType
    * @param nodeContent
    * @return
    */
   private boolean isComma(String nodeType, String nodeContent)
   {
      return "groovyPunctuation".equals(nodeType) && ",".equals(nodeContent);
   }

   /**
    * Recognize sign "."
    * @param nodeType
    * @param nodeContent
    * @return
    */
   public static boolean isPoint(String nodeType, String nodeContent)
   {
      return "groovyPunctuation".equals(nodeType) && ".".equals(nodeContent);
   }   

   /**
    * Recognize sign ";"
    * @param nodeType
    * @param nodeContent
    * @return
    */
   private boolean isSemicolon(String nodeType, String nodeContent)
   {
      return "groovyPunctuation".equals(nodeType) && ";".equals(nodeContent);
   }   
   
   private boolean isJavaType(String nodeType)
   {
      return (nodeType != null) && nodeType.equals("javaType");
   }

   /**
    * Recognize "{"
    * @return true if there is open braces of method definition
    */
   private boolean isOpenBrace(String nodeType, String nodeContent)
   {
      return "groovyPunctuation".equals(nodeType) && "{".equals(nodeContent);
   }

   /**
    * Recognize "}"
    */
   private boolean isCloseBrace(String nodeType, String nodeContent)
   {
      return "groovyPunctuation".equals(nodeType) && "}".equals(nodeContent);
   }   
   
   public static boolean isGroovyVariable(String nodeType)
   {
      return "groovyVariable".equals(nodeType);
   }

   /**
    * Recognize "=" operation
    * @param nodeType
    * @param nodeContent
    * @return
    */
   private boolean isEqualSign(String nodeType, String nodeContent)
   {
      return (nodeType != null) && (nodeContent != null) && nodeType.equals("groovyOperator")
         && nodeContent.equals("=");
   }

   /**
    * Recognize open brackets "(" 
    * @param nodeType
    * @param nodeContent
    * @param inMethodBrackets 
    * @return
    */
   private boolean isOpenBracket(String nodeType, String nodeContent)
   {
      return (nodeType != null) && (nodeContent != null) 
         && nodeType.equals("groovyPunctuation")
         && nodeContent.equals("(");
   }

   /**
    * Recognize open brackets ")" 
    * @param nodeType
    * @param nodeContent 
    * @return
    */
   private boolean isCloseBracket(String nodeType, String nodeContent)
   {
      return (nodeType != null) && (nodeContent != null) 
         && nodeType.equals("groovyPunctuation")
         && nodeContent.equals(")");
   }

   private boolean isJavaClassNode(String nodeType, String nodeContent)
   {
      return (nodeType != null) && (nodeContent != null) && nodeType.equals("javaType") && nodeContent.equals("class");
   }
   
   private boolean isJavaInterfaceNode(String nodeType, String nodeContent)
   {
      return (nodeType != null) && (nodeContent != null) && nodeType.equals("javaType") && nodeContent.equals("interface");
   }

   /**
    * Recognize "def varname"
    * @param nodeType
    * @param nodeContent
    * @return
    */
   private boolean isGroovyDef(String nodeType, String nodeContent)
   {
      return (nodeType != null) && (nodeContent != null) && nodeType.equals("groovyKeyword")
         && nodeContent.equals("def");
   }

   /**
    * Recognize annotation like '@Path("/my-service")'
    * @param nodeType
    * @return
    */
   private boolean isAnnotation(String nodeType)
   {
      return (nodeType != null) && nodeType.equals("javaAnnotation");
   }

   /**
    * Recognize keyword "catch, for, if, switch, while" in statement like 'catch (..) {', 'for(..) {', 'if (..) {', 'switch (...) {', 'while (...) {'
    */
   private boolean isStartJavaStatement(String nodeType, String nodeContent)
   {
      return isJavaKeyword(nodeType) && (nodeContent != null) 
         && (nodeContent.equals("catch") || nodeContent.equals("for") || nodeContent.equals("if") || nodeContent.equals("while"));
      
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
   private boolean inAnnotationBrackets(Stack<TokenType> enclosers)
   {
      return ! enclosers.isEmpty()&& TokenType.ANNOTATION.equals(enclosers.lastElement());
   }
}