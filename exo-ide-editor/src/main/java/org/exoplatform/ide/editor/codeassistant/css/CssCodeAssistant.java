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
package org.exoplatform.ide.editor.codeassistant.css;

import java.util.List;

import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.codeassitant.CodeAssistant;
import org.exoplatform.ide.editor.api.codeassitant.CodeError;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.codeassistant.util.JSONTokenParser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ExternalTextResource;
import com.google.gwt.resources.client.ResourceCallback;
import com.google.gwt.resources.client.ResourceException;
import com.google.gwt.resources.client.TextResource;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: CssCodeAssistant Feb 22, 2011 2:15:17 PM evgen $
 *
 */
public class CssCodeAssistant extends CodeAssistant
{

   public interface CssBuandle extends ClientBundle
   {
      @Source("org/exoplatform/ide/editor/public/tokens/css_tokens.js")
      ExternalTextResource cssTokens();
   }
   
   private static List<Token> cssProperty;


//   private native JavaScriptObject getTokens() /*-{
//		return $wnd.css_tokens;
//   }-*/;

   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.CodeAssistant#errorMarckClicked(org.exoplatform.ide.editor.api.Editor, java.util.List, int, int, java.lang.String)
    */
   public void errorMarckClicked(Editor editor, List<CodeError> codeErrorList, int markOffsetX, int markOffsetY,
      String fileMimeType)
   {
   }

   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.CodeAssistant#autocompleteCalled(org.exoplatform.ide.editor.api.Editor, java.lang.String, int, int, java.lang.String, int, int, java.util.List, java.lang.String, org.exoplatform.ide.editor.api.codeassitant.Token)
    */
   public void autocompleteCalled(Editor editor, String mimeType, final int cursorOffsetX, final int cursorOffsetY,
      final String lineContent, final int cursorPositionX, int cursorPositionY, List<Token> tokenList, String lineMimeType,
      Token currentToken)
   {
      try
      {
         this.editor = editor;
         if (cssProperty == null)
         {
            CssBuandle buandle = GWT.create(CssBuandle.class);
            buandle.cssTokens().getText( new ResourceCallback<TextResource>()
            {
               
               @Override
               public void onSuccess(TextResource resource)
               {                  
                  JSONTokenParser parser = new JSONTokenParser();
                  JSONArray tokenArray = new JSONArray(parseJson(resource.getText()));
                  cssProperty = parser.getTokens(tokenArray);
                  fillTokens(cursorOffsetX, cursorOffsetY, lineContent, cursorPositionX);
               }
               
               @Override
               public void onError(ResourceException e)
               {
                  e.printStackTrace();
               }
            });
            return;
         }
         fillTokens(cursorOffsetX, cursorOffsetY, lineContent, cursorPositionX);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * @param cursorOffsetX
    * @param cursorOffsetY
    * @param lineContent
    * @param cursorPositionX
    */
   private void fillTokens(int cursorOffsetX, int cursorOffsetY, String lineContent, int cursorPositionX)
   {
      String subToken = lineContent.substring(0, cursorPositionX - 1);
      afterToken = lineContent.substring(cursorPositionX - 1);

      String token = "";
      if (!subToken.endsWith(" ") && !subToken.endsWith(":") && !subToken.endsWith(";") && !subToken.endsWith("}")&& !subToken.endsWith("{"))
      {
         String[] split = subToken.split("[/()|&\",' ]+");

         if (split.length != 0)
         {
            token = split[split.length - 1];
         }
      }
      beforeToken = subToken.substring(0, subToken.lastIndexOf(token));
      tokenToComplete = token;

      openForm(cursorOffsetX, cursorOffsetY, cssProperty, new CssTokenWidgetFactory(), this);
   }

}
