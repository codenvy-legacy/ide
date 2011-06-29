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
package org.exoplatform.ide.editor.codemirror.autocomplete;

import java.util.Arrays;
import java.util.List;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.editor.api.codeassitant.Modifier;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.codemirror.parser.GroovyParser;
import org.exoplatform.ide.editor.codevalidator.GroovyCodeValidator;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author <a href="mailto:dmitry.nochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 *
 */
public class GroovyAutocompleteHelper extends AutocompleteHelper
{

   GroovyCodeValidator groovyCodeValidator = new GroovyCodeValidator();
   
   public Token getTokenBeforeCursor(JavaScriptObject node, int lineNumber, int cursorPosition, List<? extends Token> tokenList, String currentLineMimeType)
   {
      return getTokenBeforeCursor(node, lineNumber, cursorPosition, tokenList);
   }
   
   /**
    * 
    * @param node
    * @param lineNumber 
    * @param cursorPosition
    * @param tokenList
    * @return
    */
   public Token getTokenBeforeCursor(JavaScriptObject node, int lineNumber, int cursorPosition, List<? extends Token> tokenList)
   {
      // interrupt at the end of the line or content
      if ((node == null) || (node).equals("BR"))
      {
         return null;
      }

      String nodeContent = getStatementBeforePoint(node, cursorPosition);

      TokenBeenImpl tokenBeforeCursor;
      
      if (nodeContent != null && !nodeContent.isEmpty())
      {       
         int numberOfChainsBetweenPoint = nodeContent.split("[.]").length;   // nodeContent.split("[.]") returns 1 for "name", and 3 for "java.lang.Integer"         

         // search token for variables like "name._" or "name.ch_"
         if (numberOfChainsBetweenPoint == 1)
         {
            tokenBeforeCursor = getGenericToken(nodeContent, lineNumber, (List<TokenBeenImpl>) tokenList);            
            if (tokenBeforeCursor != null && tokenBeforeCursor.getFqn() != null) 
            {
               return new TokenBeenImpl(
                  tokenBeforeCursor.getName(), 
                  tokenBeforeCursor.getType(), 
                  lineNumber, 
                  tokenBeforeCursor.getMimeType(), 
                  tokenBeforeCursor.getElementType(),
                  tokenBeforeCursor.getModifiers(), 
                  tokenBeforeCursor.getFqn()
               );
            }
         }

         // search fqn among default packages
         String fqn = groovyCodeValidator.getFqnFromDefaultPackages(nodeContent);
         if (fqn != null) 
            return new TokenBeenImpl(null, TokenType.TYPE, lineNumber, MimeType.APPLICATION_GROOVY, nodeContent, Arrays.asList(Modifier.STATIC), fqn);
         
         // search fqn among the import statements from the import block 
         List<TokenBeenImpl> importStatementBlock = GroovyCodeValidator.getImportStatementBlock((List<TokenBeenImpl>)tokenList);
         for (TokenBeenImpl importStatement : importStatementBlock)
         {
            if (importStatement.getElementType().endsWith(nodeContent))
            {
               return (Token) new TokenBeenImpl(null, TokenType.TYPE, lineNumber, MimeType.APPLICATION_GROOVY, nodeContent, Arrays.asList(Modifier.STATIC), importStatement.getElementType());
            }
         }           
         
      }
      
      // if this is "name_" or " _" cases, return Token of container element like method, class or module, from token list
      else
      {
         return (Token) getContainerToken(lineNumber, (List<TokenBeenImpl>) tokenList);
      }
      
      return null;
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
         
         // trying to search generic token whitin the scriptlets of JSP, or Groovy Template files
         else 
         {
            // search among the properties (fields) of class
            genericToken = searchGenericTokenAmongProperties(nodeContent, nearestToken.getParentToken());
            if (genericToken != null) return genericToken;
         }
      }      
         
      return null;
   } 

   public boolean isVariable(String nodeType)
   {
      return GroovyParser.isGroovyVariable(nodeType);
   }
   
   public boolean isPoint(String nodeType, String nodeContent)
   {
      return GroovyParser.isPoint(nodeType, nodeContent);
   }
}
