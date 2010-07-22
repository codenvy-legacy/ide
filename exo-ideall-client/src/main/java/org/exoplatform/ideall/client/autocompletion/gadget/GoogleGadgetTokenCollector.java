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
package org.exoplatform.ideall.client.autocompletion.gadget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.editor.api.Token;
import org.exoplatform.ideall.client.autocompletion.TokenCollectors;
import org.exoplatform.ideall.client.autocompletion.api.TokenCollector;
import org.exoplatform.ideall.client.autocompletion.api.TokensCollectedCallback;

import com.google.gwt.event.shared.HandlerManager;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class GoogleGadgetTokenCollector implements TokenCollector, TokensCollectedCallback
{

   private static HashMap<String, String> mimeTypeToTag = new HashMap<String, String>();

   private HandlerManager eventBus;

   private TokensCollectedCallback callback;

   static
   {
      mimeTypeToTag.put(MimeType.APPLICATION_JAVASCRIPT, "script");
      mimeTypeToTag.put(MimeType.TEXT_CSS, "style");
   }

   public GoogleGadgetTokenCollector(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
   }

   /**
    * @see org.exoplatform.ideall.client.autocompletion.api.TokenCollector#getTokens(java.lang.String, int, int, java.util.List, org.exoplatform.ideall.client.autocompletion.api.TokensCollectedCallback)
    */
   public void getTokens(String line, String lineMimeType, int lineNum, int cursorPos, List<Token> tokenFromParser,
      TokensCollectedCallback tokensCollectedCallback)
   {
      this.callback = tokensCollectedCallback;
      if (lineMimeType.equals(MimeType.APPLICATION_JAVASCRIPT))
      {
         TokenCollectors.getTokenCollector(eventBus, lineMimeType).getTokens(line, lineMimeType, lineNum, cursorPos,
            getTokenJavaScript(tokenFromParser), this);
      }
      else
      {
        TokenCollectors.getTokenCollector(eventBus, lineMimeType).getTokens(line, lineMimeType, lineNum, cursorPos, tokenFromParser, this);
      }

   }

   /**
    * @param tokenFromParser
    */
   private List<Token> getTokenJavaScript(List<Token> tokenFromParser)
   {
      List<Token> tokens = new ArrayList<Token>();

      String tagName = "script";

      for (int i = 0; i < tokenFromParser.size(); i++)
      {
         Token token = tokenFromParser.get(i);
         if (token.getName().equals(tagName))
         {
            tokens.addAll(token.getSubTokenList());
         }
         else if (!token.getSubTokenList().isEmpty())
         {
            tokens.addAll(getTokenJavaScript(token.getSubTokenList()));
         }
      }

      return tokens;
   }

   /**
    * @see org.exoplatform.ideall.client.autocompletion.api.TokensCollectedCallback#onTokensCollected(java.util.List, java.lang.String, java.lang.String, java.lang.String)
    */
   public void onTokensCollected(List<Token> tokens, String beforeToken, String tokenToComplete, String afterToken)
   {
      callback.onTokensCollected(tokens, beforeToken, tokenToComplete, afterToken);
   }

}
