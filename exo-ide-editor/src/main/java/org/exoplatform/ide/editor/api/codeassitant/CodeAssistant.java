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
package org.exoplatform.ide.editor.api.codeassitant;

import java.util.List;

import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.codeassitant.ui.AutocompletionForm;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenSelectedHandler;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidget;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidgetFactory;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerManager;

/**
 * Callback interface for codeaasistant feature.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: CodeAssistant Feb 22, 2011 12:43:13 PM evgen $
 *
 */
public abstract class CodeAssistant implements TokenSelectedHandler
{

   protected HandlerManager eventBus;
   
   protected String beforeToken;

   protected String tokenToComplete;

   protected String afterToken;
   
   protected Editor editor;

   /**
    * @param eventBys
    */
   public CodeAssistant(HandlerManager eventBus)
   {
      super();
      this.eventBus = eventBus;
   }

   /**
    * 
    * @param editor
    * @param codeErrorList
    * @param markOffsetX
    * @param markOffsetY
    * @param fileMimeType
    */
   public abstract void errorMarckClicked(Editor editor, List<CodeError> codeErrorList, int markOffsetX,
      int markOffsetY, String fileMimeType);

   /**
    * If editor support autocompletion, he calls this method.  
    * @param editor
    * @param mimeType
    * @param cursorOffsetX
    * @param cursorOffsetY
    * @param lineContent
    * @param cursorPositionX
    * @param cursorPositionY
    * @param tokenList
    * @param lineMimeType
    * @param currentToken
    */
   public abstract void autocompleteCalled(Editor editor, String mimeType, int cursorOffsetX, int cursorOffsetY,
      String lineContent, int cursorPositionX, int cursorPositionY, List<Token> tokenList, String lineMimeType,
      Token currentToken);
   
   protected void openForm(int x, int y, List<Token> tokens, TokenWidgetFactory factory, TokenSelectedHandler handler)
   {
       x = x - tokenToComplete.length() * 8 + 8;
       y = y+ 4;
      new AutocompletionForm(eventBus, x, y, tokenToComplete, tokens, factory, handler);
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
      String tokenValue = value.getTokenValue() ;
      String tokenToPaste = "";
      int newCursorPos = 1;
      switch (value.getToken().getType())
      {
         case ATTRIBUTE :
            if (!beforeToken.endsWith(" "))
               beforeToken += " ";
            tokenToPaste = beforeToken +tokenValue + afterToken;
            newCursorPos = (beforeToken + tokenValue).lastIndexOf("\"") ;
            break;
            
         case TAG :
            if (beforeToken.endsWith("<") || beforeToken.endsWith(" "))
               beforeToken = beforeToken.substring(0, beforeToken.length() - 1);
            tokenToPaste = beforeToken + tokenValue+ afterToken;
            if (tokenValue.contains("/"))
               newCursorPos = (beforeToken + tokenValue).indexOf("/", beforeToken.length()) - 1 ;
            else
               newCursorPos = (beforeToken + tokenValue).length() + 1;
            break;
            
          default :
              tokenToPaste = beforeToken + tokenValue+ afterToken;
              newCursorPos = beforeToken.length() + tokenValue.length();
             break;
      }
      editor.replaceTextAtCurrentLine(tokenToPaste, newCursorPos);
   }

   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.ui.TokenSelectedHandler#onCancelAutoComplete()
    */
   @Override
   public void onCancelAutoComplete()
   {
      editor.setFocus();
   }
   
   /*
    * Takes in a trusted JSON String and evals it.
    * @param JSON String that you trust
    * @return JavaScriptObject that you can cast to an Overlay Type
    */
   protected native JavaScriptObject parseJson(String json) /*-{
     return eval('(' + json + ')'); ;
   }-*/;

}
