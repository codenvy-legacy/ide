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
package org.exoplatform.ide.editor.codeassistant.java;

import java.util.List;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.editor.api.CodeLine;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.codeassitant.CodeAssistant;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidgetFactory;
import org.exoplatform.ide.editor.codeassistant.java.service.CodeAssistantService;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: JavaCodeAssistant Mar 2, 2011 4:41:28 PM evgen $
 *
 */
public class JavaCodeAssistant extends CodeAssistant
{

   private String activeFileHref;

   private TokenWidgetFactory factory;

   /**
    * @param factory
    */
   public JavaCodeAssistant(TokenWidgetFactory factory)
   {
      super();
      this.factory = factory;
   }

   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.CodeAssistant#errorMarckClicked(org.exoplatform.ide.editor.api.Editor, java.util.List, int, int, java.lang.String)
    */
   @Override
   public void errorMarckClicked(Editor editor, List<CodeLine> codeErrorList, final int markOffsetX, final int markOffsetY,
      String fileMimeType)
   {
      this.editor = editor;
      try
      {
      System.out.println("JavaCodeAssistant.errorMarckClicked()");
      System.out.println(codeErrorList.get(0).getLineContent());
      CodeAssistantService.getInstance().findClass(codeErrorList.get(0).getLineContent(), activeFileHref,
         new AsyncRequestCallback<List<Token>>()
         {

            @Override
            protected void onSuccess(List<Token> result)
            {
               openImportForm(markOffsetY, markOffsetX, result, factory, JavaCodeAssistant.this);
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               
            }
         });
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.CodeAssistant#autocompleteCalled(org.exoplatform.ide.editor.api.Editor, java.lang.String, int, int, java.lang.String, int, int, java.util.List, java.lang.String, org.exoplatform.ide.editor.api.codeassitant.Token)
    */
   @Override
   public void autocompleteCalled(Editor editor, String mimeType, int cursorOffsetX, int cursorOffsetY,
      String lineContent, int cursorPositionX, int cursorPositionY, List<Token> tokenList, String lineMimeType,
      Token currentToken)
   {
      this.editor = editor;

      System.out.println("JavaCodeAssistant.autocompleteCalled()");
   }

   public void setactiveFileHref(String href)
   {
      activeFileHref = href;
   }


}
