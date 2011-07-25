/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.editor.extension.php.client.codemirror;

import java.util.LinkedList;
import java.util.List;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.codemirror.AutocompleteHelper;
import org.exoplatform.ide.editor.codemirror.CodeValidator;
import org.exoplatform.ide.editor.codemirror.Node;
import org.exoplatform.ide.editor.extension.html.client.codemirror.HtmlAutocompleteHelper;
import org.exoplatform.ide.editor.extension.java.client.codemirror.JavaAutocompleteHelper;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author <a href="mailto:dnochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 *
 */
public class PhpAutocompleteHelper extends AutocompleteHelper
{

   HtmlAutocompleteHelper htmlAutocompleteHelper = new HtmlAutocompleteHelper();
   
   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.autocompletehelper.AutoCompleteHelper#getTokenBeforeCursor(com.google.gwt.core.client.JavaScriptObject, int, int, java.util.List)
    */
   @Override
   public Token getTokenBeforeCursor(JavaScriptObject node, int lineNumber, int cursorPosition,
      List<? extends Token> tokenList, String currentLineMimeType)
   {
      if (MimeType.APPLICATION_JAVASCRIPT.equals(currentLineMimeType))
         return htmlAutocompleteHelper.getTokenBeforeCursor(node, lineNumber, cursorPosition, tokenList, currentLineMimeType);

      else if (MimeType.APPLICATION_PHP.equals(currentLineMimeType))
      {
         List<? extends Token> phpCode = CodeValidator.extractCode((List<TokenBeenImpl>)tokenList, new LinkedList<TokenBeenImpl>(), MimeType.APPLICATION_PHP);
         return getTokenBeforeCursor(node, lineNumber, cursorPosition, phpCode);
      }
      
      return null;
   }

   /**
    * Recognize dynamic calling of method or property like "$obj->_" and return $obj generic token, or static calling like "Handler::_" and return "Handler" token of class type.
    * @param javaScriptNode
    * @param lineNumber 
    * @param cursorPosition
    * @param tokenList
    * @return
    */
   public Token getTokenBeforeCursor(JavaScriptObject javaScriptNode, int lineNumber, int cursorPosition, List<? extends Token> tokenList)
   {
      // interrupt at the end of the line or content
      if ((javaScriptNode == null) || Node.isLineBreak(javaScriptNode))
      {
         return null;
      }

      // check dynamic calling
      String nodeContent = getLastElementName(javaScriptNode, cursorPosition, PhpParser.dynamicCallingOperator);
      
      TokenBeenImpl tokenBeforeCursor;
      
      if (nodeContent != null && !nodeContent.isEmpty())
      {    
         // return element type = container class name for case $this->_ 
         if ("$this".equals(nodeContent))
         {

            String elementType = null;
            if ((elementType = getContainerClassName(lineNumber, (List<TokenBeenImpl>) tokenList)) != null)
            {
               return new TokenBeenImpl(
                  nodeContent, 
                  TokenType.VARIABLE, 
                  lineNumber, 
                  MimeType.APPLICATION_PHP,
                  elementType
               );
            }
         }
            
         // search token for variables like "$name->_" or "$name->ch_"
         else if ((tokenBeforeCursor = getGenericToken(nodeContent, lineNumber, (List<TokenBeenImpl>) tokenList)) != null) 
         {
            return new TokenBeenImpl(
               tokenBeforeCursor.getName(), 
               tokenBeforeCursor.getType(), 
               lineNumber, 
               tokenBeforeCursor.getMimeType(), 
               tokenBeforeCursor.getElementType(),
               tokenBeforeCursor.getModifiers()
            );
         }
         
         // return null if there is no such variable, or property, or parameter
         else
         {
            return null;
         }
      }
      
      // check on static calling
      else 
      {
         nodeContent = getLastElementName(javaScriptNode, cursorPosition, PhpParser.staticCallingOperator);
         
         if (nodeContent != null && !nodeContent.isEmpty())
         {    
            // return element type = container class name for case self::_ 
            String elementType = null;
            if ("self".equals(nodeContent))
            {
               if ((elementType = getContainerClassName(lineNumber, (List<TokenBeenImpl>) tokenList)) != null)
               {
                  return new TokenBeenImpl(
                     nodeContent, 
                     TokenType.KEYWORD, 
                     lineNumber, 
                     MimeType.APPLICATION_PHP,
                     elementType
                  );
               }
            }
            
            // search token for variables like "$name::_" or "$name::ch_"
            else if ((tokenBeforeCursor = getGenericToken(nodeContent, lineNumber, (List<TokenBeenImpl>) tokenList)) != null) 
            {
               return new TokenBeenImpl(
                  tokenBeforeCursor.getName(), 
                  tokenBeforeCursor.getType(), 
                  lineNumber, 
                  tokenBeforeCursor.getMimeType(), 
                  tokenBeforeCursor.getElementType(),
                  tokenBeforeCursor.getModifiers()
               );
            }
            
            else
            {
               // return class name before token like "Handler" in case like "Handler::_"
               return new TokenBeenImpl(
                  nodeContent, 
                  TokenType.CLASS, 
                  lineNumber, 
                  MimeType.APPLICATION_PHP
               );
            }
         }
      }      
      
      // if this is "name_" or " _" cases, trying to find out and return Token of parent element, like method or class
      return (Token) getContainerToken(lineNumber, (List<TokenBeenImpl>) tokenList);
   }

   /**
    * Recognize object name like "$obj" in case like "$obj->_"
    * @param javaScriptNode
    * @param cursorPosition
    * @param delimiter
    * @return
    */
   private String getLastElementName(JavaScriptObject javaScriptNode, int cursorPosition, String delimiter)
   {
      String nodeContent;
      String nodeType;
      
      String statement = "";
      
      while (javaScriptNode != null && !(javaScriptNode).equals("BR"))
      {         
         // pass nodes after the cursor
         if (Node.getNodePositionInLine(javaScriptNode) >= cursorPosition) 
         {
            // get previous token
            javaScriptNode = Node.getPrevious(javaScriptNode);
         }
         else
         {
            nodeContent = Node.getContent(javaScriptNode);
            nodeType = Node.getType(javaScriptNode);
   
            if ((!PhpParser.isVariable(nodeType) 
                     && !PhpParser.isPhpElementName(nodeType) 
                     && !PhpParser.isThisKeyword(new Node(nodeType, nodeContent)) 
                     && !isDelimiter(nodeType, nodeContent.trim(), delimiter))  // filter part with non-variable and non-point symbols, not "<delimiter> " symbol, not "$this" symbol
                   || (
                         nodeContent.indexOf(" ") != -1  // filter nodes like "String " in sentence "String name<delimiter>_", or like "<delimiter> " in sentence "<delimiter> String_", or like "<delimiter> _" in sentence like "String<delimiter> _", or like "ch " in sentence like "name<delimiter>ch _"  
                         && (statement.length() > 0  // filter nodes like "name <delimiter>_" or "name<delimiter> ch<delimiter>_"
                               || (Node.getNodePositionInLine(javaScriptNode) + nodeContent.length()) <= cursorPosition  // filter nodes like "name<delimiter> _" or "name<delimiter>ch _"
                             )
                      )
               )
            {
               break;
            }
   
            statement = nodeContent + statement;
            
            // get previous token
            javaScriptNode = Node.getPrevious(javaScriptNode);
         }
      }      
      
      if (statement.lastIndexOf(delimiter) == -1)
      {
         // return "" for statement like "name_"
         return "";
      }
      else
      {
         // clear last chain like "<delimiter>ch_" in javaScriptNode "java<delimiter>lang<delimiter>String<delimiter>ch_", or "<delimiter>" in javaScriptNode "name<delimiter>", or statement without point like "name_"         
         return statement.substring(0, statement.lastIndexOf(delimiter));
      }
   }

   /**
    * Recognize delimiter
    * @param nodeType
    * @param nodeContent
    * @param delimiter
    * @return
    */
   private boolean isDelimiter(String nodeType, String nodeContent, String delimiter)
   { 
      if (PhpParser.dynamicCallingOperator.equals(delimiter))
      {
         return PhpParser.isDynamicCallingOperator(new Node(nodeType, nodeContent));
      }
      
      if (PhpParser.staticCallingOperator.equals(delimiter))
      {
         return PhpParser.isStaticCallingOperator(new Node(nodeType, nodeContent));
      }
      
      return nodeContent.equals(delimiter);
   }
 
   protected static TokenBeenImpl getGenericToken(String nodeContent, int targetLineNumber, List<TokenBeenImpl> tokenList)
   {
      if (tokenList == null || tokenList.size() == 0)
         return null;

      nearestToken = tokenList.get(0);
      
      for (TokenBeenImpl token : tokenList)
      {
         // test is Container Token After The CurrentLine
         if (token.getLineNumber() > targetLineNumber)
            break;

         searchNearestToken(targetLineNumber, token);
      }
      
      TokenBeenImpl genericToken;
      
      if (nearestToken != null)
      {
         // test if nearest token is within the method
         if (nearestToken.getParentToken() != null
               && TokenType.METHOD.equals(nearestToken.getParentToken().getType()))
         {
            // search as local variables among the subTokens
            genericToken = searchGenericTokenAmongMethodVariables(nodeContent, nearestToken, nearestToken.getParentToken());
            if (genericToken != null) return genericToken;
   
            // search among the parameters of method
            genericToken = searchGenericTokenAmongParameters(nodeContent, nearestToken.getParentToken().getParameters());
            if (genericToken != null) return genericToken;
            
            // search among the properties (fields) of class
            genericToken = searchGenericTokenAmongProperties(nodeContent, nearestToken.getParentToken().getParentToken());
            if (genericToken != null) return genericToken;
         }
         
         // test if nearest token is method token 
         else if (TokenType.METHOD.equals(nearestToken.getType()))
         {
            // search among the parameters of method
            genericToken = searchGenericTokenAmongParameters(nodeContent, nearestToken.getParameters());
            if (genericToken != null) return genericToken;
            
            // search among the properties (fields) of class
            genericToken = searchGenericTokenAmongProperties(nodeContent, nearestToken.getParentToken());
            if (genericToken != null) return genericToken;
         }
         
         // test if nearest token is within the function
         if (nearestToken.getParentToken() != null
                && TokenType.FUNCTION.equals(nearestToken.getParentToken().getType())
            )
         {
            // search among the variables
            genericToken = searchGenericTokenAmongVariables(nodeContent, nearestToken.getParentToken());
            if (genericToken != null) return genericToken;
            
            // search among the parameters of method
            genericToken = JavaAutocompleteHelper.searchGenericTokenAmongParameters(nodeContent, nearestToken.getParentToken().getParameters());
            if (genericToken != null) return genericToken;            
         }
         
         // test if nearest token is in the root php node
         else if (nearestToken.getParentToken() != null
                && TokenType.PHP_TAG.equals(nearestToken.getParentToken().getType())
            )
         {
            // search among the variables
            genericToken = searchGenericTokenAmongVariables(nodeContent, nearestToken.getParentToken());
            if (genericToken != null) return genericToken;
         }
         
      }      
         
      return null;
   } 

   @Override
   public boolean isPossibleContainerTokenType(TokenBeenImpl token)
   {
      return TokenType.PHP_TAG.equals(token.getType()) 
               || TokenType.CLASS.equals(token.getType()) 
               || TokenType.METHOD.equals(token.getType()) 
               || TokenType.FUNCTION.equals(token.getType());
   }
 

   /**
    * Return class name in cases like "class a { _ }" or "class a { function b() { _ } }"
    * @param targetLineNumber
    * @param tokenList
    * @return
    */
   private String getContainerClassName(int targetLineNumber, List<TokenBeenImpl> tokenList)
   {
      if (tokenList == null || tokenList.size() == 0)
         return null;

      nearestToken = tokenList.get(0);
      
      for (TokenBeenImpl token : tokenList)
      {
         // test is Container Token After The CurrentLine
         if (token.getLineNumber() > targetLineNumber)
            break;

         searchNearestToken(targetLineNumber, token);
      }
      
      if (nearestToken != null)
      {
         // test if nearest token is within the class like "class a { _ }"
         if (TokenType.CLASS.equals(nearestToken.getType()))
         {
            return nearestToken.getName();
         }
         
         // test if nearest token is within the method of class like "class a { function b() { _ } }"
         else if (nearestToken.getParentToken() != null
                  && TokenType.METHOD.equals(nearestToken.getParentToken().getType())
                  && TokenType.CLASS.equals(nearestToken.getParentToken().getParentToken().getType()))
         {
            return nearestToken.getParentToken().getParentToken().getName();
         }
         
         // test if nearest token is within the method of class like "class a { function b() { _ } }"
         else if (nearestToken.getParentToken() != null
                  && TokenType.METHOD.equals(nearestToken.getType())
                  && TokenType.CLASS.equals(nearestToken.getParentToken().getType()))
         {
            return nearestToken.getParentToken().getName();
         }
      }
      
      return null;
   }
 
   public boolean isVariable(String nodeType)
   {
      return false;
   }

   public boolean isPoint(String nodeType, String nodeContent)
   {
      return false;
   }
   
}