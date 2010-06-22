/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.ideall.client.autocompletion;

import java.util.HashMap;
import java.util.List;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.editor.api.Token;
import org.exoplatform.gwtframework.editor.event.EditorAutoCompleteCalledEvent;
import org.exoplatform.gwtframework.editor.event.EditorAutoCompleteCalledHandler;
import org.exoplatform.gwtframework.editor.event.EditorAutoCompleteEvent;
import org.exoplatform.gwtframework.ui.client.component.autocomplete.AutocompleteTokenSelectedHandler;
import org.exoplatform.gwtframework.ui.client.component.autocomplete.NewAutoCompleteForm;
import org.exoplatform.ideall.client.autocompletion.groovy.GroovyTokenCollector;
import org.exoplatform.ideall.client.autocompletion.js.JavaScriptTokenCollector;
import org.exoplatform.ideall.client.editor.event.EditorSetFocusOnActiveFileEvent;
import org.exoplatform.ideall.client.model.ApplicationContext;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class AutoCompletionManager implements EditorAutoCompleteCalledHandler, TokensCollectedCallback,
   AutocompleteTokenSelectedHandler
{

   private HashMap<String, TokenCollector> factories = new HashMap<String, TokenCollector>();

   private HandlerManager eventBus;

   private ApplicationContext context;

   private int cursorOffsetX;

   private int cursorOffsetY;

   private String lineContent;

   private String editorId;

   private String afterToken;

   private String beforeToken;

   private int cursorPos;

   public AutoCompletionManager(HandlerManager eventBus, ApplicationContext context)
   {
      this.context = context;
      this.eventBus = eventBus;

      factories.put(MimeType.SCRIPT_GROOVY, new GroovyTokenCollector(eventBus, context, this));
      factories.put(MimeType.APPLICATION_JAVASCRIPT, new JavaScriptTokenCollector(eventBus, context, this));
      factories.put(MimeType.GOOGLE_GADGET, new JavaScriptTokenCollector(eventBus, context, this));

      eventBus.addHandler(EditorAutoCompleteCalledEvent.TYPE, this);
   }

   public void onEditorAutoCompleteCalled(EditorAutoCompleteCalledEvent event)
   {
      cursorOffsetX = event.getCursorOffsetX();
      cursorOffsetY = event.getCursorOffsetY();
      editorId = event.getEditorId();
      lineContent = event.getLineContent();
      cursorPos = event.getCursorPositionX();
      TokenCollector collector = factories.get(event.getMimeType());
      if (collector != null)
      {
         collector.getTokens(event.getLineContent(), event.getCursorPositionY(), cursorPos, event.getTokenList());
      }
   }

   public void onTokensCollected(List<Token> tokens, String beforeToken, String tokenToComplete, String afterToken)
   {
      this.beforeToken = beforeToken;
      this.afterToken = afterToken;

      int x = cursorOffsetX - tokenToComplete.length() * 8 + 8;
      int y = cursorOffsetY + 4;
      new NewAutoCompleteForm(x, y, tokenToComplete, tokens, TokenImageResolver.getImages(), this);
   }

   /**
    * @see org.exoplatform.gwtframework.ui.client.component.autocomlete.AutocompleteTokenSelectedHandler#onAutocompleteTokenSelected(java.lang.String)
    */
   public void onAutocompleteTokenSelected(String token)
   {
      String tokenToPaste = beforeToken + token + afterToken;
      
      int newCursorPos = 0;

      if (token.contains("\n"))
         newCursorPos = getCursorPos(beforeToken + token);
      else
         newCursorPos = (beforeToken + token).length() + 1;
      
      eventBus.fireEvent(new EditorAutoCompleteEvent(editorId, tokenToPaste, newCursorPos));
   }

   private native int getCursorPos(String token)/*-{
                                                pattern = "[({]|\\n";
                                                d = token.search(pattern);
                                                return (d == -1) ? (token.length+1) : (d+2);
                                                }-*/;

   /**
    * @see org.exoplatform.gwtframework.ui.client.component.autocomlete.AutocompleteTokenSelectedHandler#onAutocompleteCancel()
    */
   public void onAutocompleteCancel()
   {
      eventBus.fireEvent(new EditorSetFocusOnActiveFileEvent());
   }

}
