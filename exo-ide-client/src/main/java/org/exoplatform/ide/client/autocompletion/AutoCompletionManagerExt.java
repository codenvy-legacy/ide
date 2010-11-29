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

import org.exoplatform.gwtframework.editor.event.EditorAutoCompleteCalledEvent;
import org.exoplatform.gwtframework.editor.event.EditorAutoCompleteCalledHandler;
import org.exoplatform.ide.client.autocompletion.ui.AutocompletionFormExt;
import org.exoplatform.ide.client.framework.codeassistant.TokenCollector;
import org.exoplatform.ide.client.framework.codeassistant.TokenExt;
import org.exoplatform.ide.client.framework.codeassistant.TokensCollectedCallback;
import org.exoplatform.ide.client.framework.codeassistant.api.TokenSelectedHandler;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 26, 2010 12:12:36 PM evgen $
 *
 */
public class AutoCompletionManagerExt implements EditorAutoCompleteCalledHandler, TokensCollectedCallback<TokenExt>,
   TokenSelectedHandler<TokenExt>
{

   private HandlerManager eventBus;

   private int cursorOffsetX;

   private int cursorOffsetY;

   private String editorId;

   private String afterToken;

   private String beforeToken;

   private String mimeType;

   private TokenFactories<TokenExt> factories;

   private TokenExtCollectors<TokenExt> collectors;

   public AutoCompletionManagerExt(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      factories = new TokenFactories<TokenExt>(eventBus);
      collectors = new TokenExtCollectors<TokenExt>(eventBus);

      eventBus.addHandler(EditorAutoCompleteCalledEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.gwtframework.editor.event.EditorAutoCompleteCalledHandler#onEditorAutoCompleteCalled(org.exoplatform.gwtframework.editor.event.EditorAutoCompleteCalledEvent)
    */
   public void onEditorAutoCompleteCalled(EditorAutoCompleteCalledEvent event)
   {
      mimeType = event.getMimeType();
      cursorOffsetX = event.getCursorOffsetX();
      cursorOffsetY = event.getCursorOffsetY();
      editorId = event.getEditorId();
      TokenCollector<TokenExt> collector = collectors.getTokenCollector(mimeType);
      if (collector != null)
      {
         collector.getTokens(event.getLineContent(), event.getLineMimeType(), event.getCursorPositionY(),
            event.getCursorPositionX(), new ArrayList<TokenExt>(), this);
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.codeassistant.TokensCollectedCallback#onTokensCollected(java.util.List, java.lang.String, java.lang.String, java.lang.String)
    */
   public void onTokensCollected(List<TokenExt> tokens, String beforeToken, String tokenToComplete, String afterToken)
   {
      System.out.println("AutoCompletionManagerExt.onTokensCollected()");
      this.beforeToken = beforeToken;
      this.afterToken = afterToken;

      int x = cursorOffsetX - tokenToComplete.length() * 8 + 8;
      int y = cursorOffsetY + 4;
      try
      {
      new AutocompletionFormExt<TokenExt>(eventBus, x, y, tokenToComplete, tokens, factories.getFactory(mimeType), this);
      }
      catch (Exception e) {
         Log.debug("Autocomplete error", e);
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.codeassistant.api.TokenSelectedHandler#onTokenSelected(java.lang.Object)
    */
   public void onTokenSelected(TokenExt token)
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.exoplatform.ide.client.framework.codeassistant.api.TokenSelectedHandler#onCancelAutoComplete()
    */
   public void onCancelAutoComplete()
   {
      // TODO Auto-generated method stub

   }

}
