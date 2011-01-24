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
package org.exoplatform.ide.client.autocompletion;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.editor.api.Token;
import org.exoplatform.gwtframework.editor.event.EditorAutoCompleteCalledEvent;
import org.exoplatform.gwtframework.editor.event.EditorAutoCompleteCalledHandler;
import org.exoplatform.gwtframework.editor.event.EditorAutoCompleteEvent;
import org.exoplatform.ide.client.autocompletion.ui.AutoCompleteForm;
import org.exoplatform.ide.client.framework.codeassistant.TokenCollector;
import org.exoplatform.ide.client.framework.codeassistant.TokensCollectedCallback;
import org.exoplatform.ide.client.framework.editor.event.EditorSetFocusEvent;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class AutoCompletionManager implements EditorAutoCompleteCalledHandler, TokensCollectedCallback<Token>,
   AutocompleteTokenSelectedHandler
{

   private HandlerManager eventBus;

   private int cursorOffsetX;

   private int cursorOffsetY;

   private String editorId;

   private String afterToken;

   private String beforeToken;

   public AutoCompletionManager(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      eventBus.addHandler(EditorAutoCompleteCalledEvent.TYPE, this);
   }

   private List<Token> filterTokenByMimeType(List<Token> tokens, String mimeType)
   {
      ArrayList<Token> newTokenList = new ArrayList<Token>();
      boolean isAdd = false;
      for (int i = 0; i < tokens.size(); i++)
      {
         Token t = tokens.get(i);
         if ((t.getName() != null) && (t.getMimeType().equals(mimeType)))
         {
            newTokenList.add(t);
            isAdd = true;
         }
         if ((t.getSubTokenList() != null) && (!isAdd))
         {
            newTokenList.addAll(filterTokenByMimeType(t.getSubTokenList(), mimeType));
            isAdd = false;
         }
      }

      return newTokenList;
   }

   public void onEditorAutoCompleteCalled(EditorAutoCompleteCalledEvent event)
   {

      cursorOffsetX = event.getCursorOffsetX();
      cursorOffsetY = event.getCursorOffsetY();
      editorId = event.getEditorId();
      
      //TODO: to prevent call of JavaScript token collector,
      //when press ctrl+space in netvibes widget inside javascript tag.
      //See AutoCopletionManagerExt.
      if (event.getMimeType().equals(MimeType.UWA_WIDGET)
         && event.getLineMimeType().equals(MimeType.APPLICATION_JAVASCRIPT))
         return;
      
      TokenCollector<Token> collector = TokenCollectors.getTokenCollector(eventBus, event.getLineMimeType());
      if (collector != null)
      {
         collector.getTokens(event.getLineContent(), event.getLineMimeType(), event.getCursorPositionY(),
            event.getCursorPositionX(), filterTokenByMimeType(event.getTokenList(), event.getLineMimeType()), this);
      }
   }

   public void onTokensCollected(List<Token> tokens, String beforeToken, String tokenToComplete, String afterToken)
   {
      this.beforeToken = beforeToken;
      this.afterToken = afterToken;

      int x = cursorOffsetX - tokenToComplete.length() * 8 + 8;
      int y = cursorOffsetY + 4;
      new AutoCompleteForm(eventBus, x, y, tokenToComplete, tokens, TokenImageResolver.getImages(), this);
   }

   /**
    * @see org.exoplatform.ide.client.autocompletion.autocomlete.AutocompleteTokenSelectedHandler#onAutocompleteTokenSelected(java.lang.String)
    */
   public void onAutocompleteTokenSelected(Token token)
   {

      String tokenToPaste = "";
      int newCursorPos = 1;
      switch (token.getType())
      {
         case FUNCTION :
            if (token.getCode() != null && !"".equals(token.getCode()))
            {
               tokenToPaste = beforeToken + token.getCode() + "()" + afterToken;
               newCursorPos = (beforeToken + token.getCode() + "(").length() + 1;
            }
            else
            {
               tokenToPaste = beforeToken + token.getName() + "()" + afterToken;
               newCursorPos = (beforeToken + token.getName() + "(").length() + 1;
            }
            break;

         case TEMPLATE :
            if (token.getCode() != null && !"".equals(token.getCode()))
            {
               tokenToPaste = beforeToken + token.getCode() + afterToken;
               newCursorPos = getCursorPos(beforeToken + token.getCode());
            }
            else
            {
               tokenToPaste = beforeToken + token.getName() + afterToken;
               newCursorPos = getCursorPos(beforeToken + token.getName());
            }
            break;

         case ATTRIBUTE :
            if (!beforeToken.endsWith(" "))
               beforeToken += " ";
            tokenToPaste = beforeToken + token.getCode() + afterToken;

            newCursorPos = (beforeToken + token.getCode()).lastIndexOf("\"") + 1;

            break;

         case TAG :
            if (beforeToken.endsWith("<") || beforeToken.endsWith(" "))
               beforeToken = beforeToken.substring(0, beforeToken.length() - 1);
            if (token.getCode() != null && !"".equals(token.getCode()))
            {
               tokenToPaste = beforeToken + token.getCode() + afterToken;
               if (token.getCode().contains("/"))
                  newCursorPos = (beforeToken + token.getCode()).indexOf("/", beforeToken.length());
               else
                  newCursorPos = (beforeToken + token.getCode()).length() + 1;
            }
            else
            {
               tokenToPaste = beforeToken + "<" + token.getName() + ">" + "</" + token.getName() + ">" + afterToken;
               newCursorPos =
                  (beforeToken + "<" + token.getName() + ">" + "</" + token.getName() + ">").indexOf("/",
                     beforeToken.length());
            }
            break;

         default :
            if (token.getCode() != null && !"".equals(token.getCode()))
            {
               tokenToPaste = beforeToken + token.getCode() + afterToken;
               newCursorPos = (beforeToken + token.getCode()).length() + 1;
            }
            else
            {
               tokenToPaste = beforeToken + token.getName() + afterToken;
               newCursorPos = (beforeToken + token.getName()).length() + 1;
            }
            break;
      }
      eventBus.fireEvent(new EditorAutoCompleteEvent(editorId, tokenToPaste, newCursorPos));
   }

   private native int getCursorPos(String token)/*-{
      pattern = "[({]|\\n";
      d = token.search(pattern);
      return (d == -1) ? (token.length+1) : (d+2);
   }-*/;

   /**
    * @see org.exoplatform.ide.client.autocompletion.autocomlete.AutocompleteTokenSelectedHandler#onAutocompleteCancel()
    */
   public void onAutocompleteCancel()
   {
      eventBus.fireEvent(new EditorSetFocusEvent());
   }

}
