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
package org.exoplatform.ide.editor.gtmpl.client.codeassistant;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidgetFactory;
import org.exoplatform.ide.editor.groovy.client.codeassistant.GroovyCodeAssistant;
import org.exoplatform.ide.editor.html.client.codeassistant.HtmlCodeAssistant;
import org.exoplatform.ide.editor.java.client.codeassistant.JavaCodeAssistantErrorHandler;
import org.exoplatform.ide.editor.java.client.codeassistant.services.CodeAssistantService;

import java.util.List;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: GroovyTamplateCodeAssistant Mar 3, 2011 5:06:09 PM evgen $
 *
 */
public class GroovyTemplateCodeAssistant extends GroovyCodeAssistant
{

   /**
    * @param factory
    * @param errorHandler
    */
   public GroovyTemplateCodeAssistant(CodeAssistantService service, TokenWidgetFactory factory,
      JavaCodeAssistantErrorHandler errorHandler)
   {
      super(service, factory, errorHandler);
   }

   /**
    * @see org.exoplatform.ide.editor.java.client.codeassistant.JavaCodeAssistant#autocompleteCalled(org.exoplatform.ide.editor.api.Editor, java.lang.String, int, int, java.lang.String, int, int, java.util.List, java.lang.String, org.exoplatform.ide.editor.api.codeassitant.Token)
    */
   @Override
   public void autocompleteCalled(Editor editor, int cursorOffsetX, int cursorOffsetY, List<Token> tokenList,
      String lineMimeType, Token currentToken)
   {

      if (MimeType.APPLICATION_GROOVY.equals(lineMimeType))
      {
         super.autocompleteCalled(editor, cursorOffsetX, cursorOffsetY, tokenList, lineMimeType, currentToken);
      }
      else
      {
         new HtmlCodeAssistant().autocompleteCalled(editor, cursorOffsetX, cursorOffsetY, tokenList, lineMimeType, currentToken);
      }
   }

}
