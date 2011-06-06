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
package org.exoplatform.ide.editor.codemirror.autocomplete;

import java.util.List;

import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author <a href="mailto:dmitry.nochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 *
 */
public class JavaScriptAutocompleteHelper extends AutocompleteHelper
{

   public Token getTokenBeforeCursor(JavaScriptObject node, int lineNumber, int cursorPosition, List<? extends Token> tokenList, String currentLineMimeType)
   {
      return getTokenBeforeCursor(node, lineNumber, cursorPosition, tokenList);
   }   
   
   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.autocompletehelper.AutoCompleteHelper#getTokenBeforeCursor(com.google.gwt.core.client.JavaScriptObject, int, int, java.util.List)
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
            if (tokenBeforeCursor != null) 
            {
               TokenBeenImpl newToken = new TokenBeenImpl(
                  tokenBeforeCursor.getName(), 
                  tokenBeforeCursor.getType(), 
                  lineNumber, 
                  tokenBeforeCursor.getMimeType(), 
                  tokenBeforeCursor.getElementType(),
                  tokenBeforeCursor.getInitializationStatement()
               );               
               
               return (Token) newToken;
            }
         }
      }
         
      return null;
   }

   
   private static TokenBeenImpl getGenericToken(String nodeContent, int targetLineNumber, List<TokenBeenImpl> tokenList)
   {
      if (tokenList == null || tokenList.size() == 0)
         return null;

      nearestToken = tokenList.get(0);
      
      for (TokenBeenImpl token : tokenList)
      {
         if (isContainerTokenAfterTheCurrentLine(targetLineNumber, token.getLineNumber()))
            break;

         searchNearestToken(targetLineNumber, token);
      }
      
      TokenBeenImpl genericToken;
      
      if (nearestToken != null)
      {
         if (nearestToken.getParentToken() != null)
         {
            // search as local variables among the subTokens
            genericToken = searchGenericTokenAmongMethodVariables(nodeContent, nearestToken, nearestToken.getParentToken());
            if (genericToken != null) 
            {
               return genericToken;
            }
            else
            {
               if (nearestToken.getParentToken().getParentToken() != null)
               {
                  return getGenericToken(nodeContent, nearestToken.getParentToken().getLineNumber() - 1, nearestToken.getParentToken().getParentToken().getSubTokenList());
               }
            }
         }
         
         if (TokenType.VARIABLE.equals(nearestToken.getType())
                     && nodeContent.equals(nearestToken.getName()))
         {
           return nearestToken;
         }
         

      }
         
      return null;
   }


}
