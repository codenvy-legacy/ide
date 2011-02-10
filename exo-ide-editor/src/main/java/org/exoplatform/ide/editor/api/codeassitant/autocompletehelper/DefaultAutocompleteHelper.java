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
package org.exoplatform.ide.editor.api.codeassitant.autocompletehelper;

import java.util.List;

import org.exoplatform.ide.editor.api.codeassitant.Token;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: DefaultAutocomleteHelper Feb 10, 2011 9:49:35 AM evgen $
 *
 */
public class DefaultAutocompleteHelper extends AutoCompleteHelper
{

   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.autocompletehelper.AutoCompleteHelper#getTokenBeforeCursor(com.google.gwt.core.client.JavaScriptObject, int, int, java.util.List)
    */
   @Override
   public Token getTokenBeforeCursor(JavaScriptObject node, int lineNumber, int cursorPosition, List<Token> tokenList)
   {
      // TODO Auto-generated method stub
      return null;
   }

}
