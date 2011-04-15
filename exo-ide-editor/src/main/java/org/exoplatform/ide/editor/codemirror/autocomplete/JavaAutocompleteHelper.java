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
import org.exoplatform.ide.editor.codevalidator.JavaCodeValidator;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author <a href="mailto:dmitry.nochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 *
 */
public class JavaAutocompleteHelper extends GroovyAutocompleteHelper
{

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
            if (tokenBeforeCursor != null) 
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
         String fqn = JavaCodeValidator.getFqnFromDefaultPackages(nodeContent);
         if (fqn != null) 
            return new TokenBeenImpl(null, TokenType.TYPE, lineNumber, MimeType.APPLICATION_JAVA, nodeContent, Arrays.asList(Modifier.STATIC), fqn);
         
         // search fqn among the import statements from the import block 
         List<TokenBeenImpl> importStatementBlock = JavaCodeValidator.getImportStatementBlock((List<TokenBeenImpl>)tokenList);
         for (TokenBeenImpl importStatement : importStatementBlock)
         {
            if (importStatement.getElementType().endsWith(nodeContent))
            {
               return (Token) new TokenBeenImpl(null, TokenType.TYPE, lineNumber, MimeType.APPLICATION_JAVA, nodeContent, Arrays.asList(Modifier.STATIC), importStatement.getElementType());
            }
         }           
         
      }
      
      // if this is "name_" or " _" cases, return Token of parent element, like method or class
      else
      {
         return (Token) getParentToken(lineNumber, (List<TokenBeenImpl>) tokenList);
      }
      
      return null;
   }

}
