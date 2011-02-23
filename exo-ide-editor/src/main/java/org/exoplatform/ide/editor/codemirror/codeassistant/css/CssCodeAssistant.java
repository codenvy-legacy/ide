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
package org.exoplatform.ide.editor.codemirror.codeassistant.css;

import java.util.List;

import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.codeassitant.CodeAssistant;
import org.exoplatform.ide.editor.api.codeassitant.CodeError;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.ui.AutocompletionForm;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenSelectedHandler;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidget;
import org.exoplatform.ide.editor.codemirror.codeassistant.util.JSONTokenParser;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.json.client.JSONArray;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: CssCodeAssistant Feb 22, 2011 2:15:17 PM evgen $
 *
 */
public class CssCodeAssistant implements CodeAssistant, TokenSelectedHandler
{

   private static List<Token> cssProperty;

   private HandlerManager evenBus;

   private Editor editor;

   private String beforeToken;

   private String tokenToComplete;

   private String afterToken;

   /**
    * @param evenBus
    */
   public CssCodeAssistant(HandlerManager evenBus)
   {
      super();
      this.evenBus = evenBus;
   }

   private native JavaScriptObject getTokens() /*-{
		return $wnd.css_tokens;
   }-*/;

   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.CodeAssistant#errorMarckClicked(org.exoplatform.ide.editor.api.Editor, java.util.List, int, int, java.lang.String)
    */
   @Override
   public void errorMarckClicked(Editor editor, List<CodeError> codeErrorList, int markOffsetX, int markOffsetY,
      String fileMimeType)
   {
   }

   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.CodeAssistant#autocompleteCalled(org.exoplatform.ide.editor.api.Editor, java.lang.String, int, int, java.lang.String, int, int, java.util.List, java.lang.String, org.exoplatform.ide.editor.api.codeassitant.Token)
    */
   @Override
   public void autocompleteCalled(Editor editor, String mimeType, int cursorOffsetX, int cursorOffsetY,
      String lineContent, int cursorPositionX, int cursorPositionY, List<Token> tokenList, String lineMimeType,
      Token currentToken)
   {
      try
      {
         this.editor = editor;
         if (cssProperty == null)
         {
            JSONTokenParser parser = new JSONTokenParser();
            JSONArray tokenArray = new JSONArray(getTokens());
            cssProperty = parser.getTokens(tokenArray);
         }

         String subToken = lineContent.substring(0, cursorPositionX - 1);
         afterToken = lineContent.substring(cursorPositionX - 1);

         String token = "";
         if (!subToken.endsWith(" ") && !subToken.endsWith(":") && !subToken.endsWith(";"))
         {
            String[] split = subToken.split("[/()|&\",']+");

            if (split.length != 0)
            {
               token = split[split.length - 1];
            }
         }
         beforeToken = subToken.substring(0, subToken.lastIndexOf(token));
         tokenToComplete = token;

         int x = cursorOffsetX - tokenToComplete.length() * 8 + 8;
         int y = cursorOffsetY + 4;

         new AutocompletionForm(evenBus, x, y, tokenToComplete, cssProperty, new CssTokenWidgetFactory(), this);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.ui.TokenSelectedHandler#onStringSelected(java.lang.String)
    */
   @Override
   public void onStringSelected(String value)
   {
      editor.replaceTextAtCurrentLine(beforeToken + value + afterToken, beforeToken.length() + value.length());
   }

   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.ui.TokenSelectedHandler#onTokenSelected(org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidget)
    */
   @Override
   public void onTokenSelected(TokenWidget value)
   {
      editor.replaceTextAtCurrentLine(beforeToken + value.getTokenValue() + afterToken, beforeToken.length()
         + value.getTokenValue().length());
   }

   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.ui.TokenSelectedHandler#onCancelAutoComplete()
    */
   @Override
   public void onCancelAutoComplete()
   {
      editor.setFocus();
   }

}
