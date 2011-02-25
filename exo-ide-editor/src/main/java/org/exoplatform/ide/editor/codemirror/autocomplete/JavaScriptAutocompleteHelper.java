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

import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.codemirror.CodeMirrorTokenImpl;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: JavaScriptAutocompleteHelper Feb 11, 2011 2:51:38 PM evgen $
 *
 */
public class JavaScriptAutocompleteHelper extends CodeMirrorAutocompleteHelper
{

   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.autocompletehelper.AutoCompleteHelper#getTokenBeforeCursor(com.google.gwt.core.client.JavaScriptObject, int, int, java.util.List)
    */
   @Override
   public Token getTokenBeforeCursor(JavaScriptObject node, int lineNumber, int cursorPosition, List<? extends Token> tokenList)
   {
      // interrupt at the end of the line or content
      if ((node == null) || (node).equals("BR"))
      {
         return null;
      }

      String nodeContent = getStatementBeforePoint(node, cursorPosition);

      CodeMirrorTokenImpl tokenBeforeCursor;
      
      if (nodeContent != null && !nodeContent.isEmpty())
      {       
         int numberOfChainsBetweenPoint = nodeContent.split("[.]").length;   // nodeContent.split("[.]") returns 1 for "name", and 3 for "java.lang.Integer"         

         // search token for variables like "name._" or "name.ch_"
         if (numberOfChainsBetweenPoint == 1)
         {
            tokenBeforeCursor = getGenericToken(nodeContent, lineNumber, (List<CodeMirrorTokenImpl>) tokenList);            
            if (tokenBeforeCursor != null) 
            {
               CodeMirrorTokenImpl newToken = new CodeMirrorTokenImpl(
                  tokenBeforeCursor.getName(), 
                  tokenBeforeCursor.getType(), 
                  lineNumber, 
                  tokenBeforeCursor.getMimeType(), 
                  tokenBeforeCursor.getElementType()
               );               
               newToken.setInitializationStatement(tokenBeforeCursor.getInitializationStatement());
               
               return (Token) newToken;
            }
         }
      }
         
      return null;
   }

   
   private static CodeMirrorTokenImpl getGenericToken(String nodeContent, int targetLineNumber, List<CodeMirrorTokenImpl> tokenList)
   {
      if (tokenList == null || tokenList.size() == 0)
         return null;

      nearestToken = tokenList.get(0);
      
      for (CodeMirrorTokenImpl token : tokenList)
      {
         if (isContainerTokenAfterTheCurrentLine(targetLineNumber, token.getLineNumber()))
            break;

         searchNearestToken(targetLineNumber, token);
      }
      
      CodeMirrorTokenImpl genericToken;
      
      if (nearestToken != null)
      {
         if (nearestToken.getParentToken() != null)
         {
            // search as local variables among the subTokens
            genericToken = searchGenericTokenAmongLocalVariables(nodeContent, nearestToken, nearestToken.getParentToken());
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
