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
package org.exoplatform.ide.editor.extension.gtmpl.client.codemirror;

import java.util.LinkedList;
import java.util.List;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.codemirror.AutocompleteHelper;
import org.exoplatform.ide.editor.codemirror.CodeValidator;
import org.exoplatform.ide.editor.extension.groovy.client.codemirror.GroovyAutocompleteHelper;
import org.exoplatform.ide.editor.extension.html.client.codemirror.HtmlAutocompleteHelper;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author <a href="mailto:dmitry.nochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 *
 */
public class GroovyTemplateAutocompleteHelper extends AutocompleteHelper
{
   
   HtmlAutocompleteHelper htmlAutocompleteHelper = new HtmlAutocompleteHelper();
   
   GroovyAutocompleteHelper groovyAutocompleteHelper = new GroovyAutocompleteHelper();
    
   List<? extends Token> groovyCode;

   public Token getTokenBeforeCursor(JavaScriptObject node, int lineNumber, int cursorPosition, List<? extends Token> tokenList, String currentLineMimeType)
   {          
      if (MimeType.APPLICATION_JAVASCRIPT.equals(currentLineMimeType))
      {
         return htmlAutocompleteHelper.getTokenBeforeCursor(node, lineNumber, cursorPosition, tokenList, currentLineMimeType);
      }
      
      else if (MimeType.APPLICATION_GROOVY.equals(currentLineMimeType))
      {
         groovyCode = CodeValidator.extractCode((List<TokenBeenImpl>)tokenList, new LinkedList<TokenBeenImpl>(), MimeType.APPLICATION_GROOVY);
         return groovyAutocompleteHelper.getTokenBeforeCursor(node, lineNumber, cursorPosition, groovyCode, currentLineMimeType);
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
