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
package org.exoplatform.ideall.client.autocompletion.html;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.editor.api.Token;
import org.exoplatform.gwtframework.editor.api.Token.TokenType;
import org.exoplatform.ideall.client.autocompletion.JSONTokenParser;
import org.exoplatform.ideall.client.autocompletion.api.TokenCollector;
import org.exoplatform.ideall.client.autocompletion.api.TokensCollectedCallback;
import org.exoplatform.ideall.client.model.ApplicationContext;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.json.client.JSONArray;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class HtmlTokenCollector implements TokenCollector
{

   private static List<Token> htmlTokens;

   private static List<String> noEndTag = new ArrayList<String>();

   static
   {

      noEndTag.add("area");
      noEndTag.add("base");
      noEndTag.add("br");
      noEndTag.add("col");
      noEndTag.add("frame");
      noEndTag.add("hr");
      noEndTag.add("img");
      noEndTag.add("input");
      noEndTag.add("link");
      noEndTag.add("meta");
      noEndTag.add("param");

   }

   private HandlerManager eventBus;

   private ApplicationContext context;

   private TokensCollectedCallback tokensCollectedCallback;

   private String beforeToken;

   private String afterToken;

   private String tokenToComplete;

   private boolean isTag = false;

   public HtmlTokenCollector(HandlerManager eventBus, ApplicationContext context,
      TokensCollectedCallback tokensCollectedCallback)
   {
      this.context = context;
      this.eventBus = eventBus;
      this.tokensCollectedCallback = tokensCollectedCallback;
   }

   /**
    * @see org.exoplatform.ideall.client.autocompletion.TokenCollector#getTokens(java.lang.String, int, int, java.util.List)
    */
   public void getTokens(String line, int lineNum, int cursorPos, List<Token> tokenFromParser)
   {
      List<Token> token = new ArrayList<Token>();

      parseTokenLine(line, cursorPos);

      if (!isTag)
      {
         token.addAll(getTokensByType(TokenType.TAG));
         tokensCollectedCallback.onTokensCollected(token, beforeToken, tokenToComplete, afterToken);
         return;
      }

      if (tokenToComplete.endsWith(" ") || tokenToComplete.endsWith("\""))
      {
         //add attributes
         String tag = "";

         tag = tokenToComplete.substring(0, tokenToComplete.indexOf(" "));

         if (noEndTag.contains(tag))
         {
            token.add(new Token(tag, TokenType.TAG, "close tag with '/>'", " />", null));
         }
         else
         {
            token.add(new Token(tag, TokenType.TAG, "close tag with '></" + tag + ">'", "></" + tag + ">", null));
            token.add(new Token(tag, TokenType.TAG, "close tag with '>'", ">", null));
         }
         beforeToken += tokenToComplete;
         tokenToComplete = "";
         token.addAll(getTokensByType(TokenType.ATTRIBUTE));
      }
      else
      {
         long start = System.currentTimeMillis();
         long end = System.currentTimeMillis() - start;
      }

      tokensCollectedCallback.onTokensCollected(token, beforeToken, tokenToComplete, afterToken);
   }

   /**
    * @param line
    */
   private void parseTokenLine(String line, int cursorPos)
   {
      String tokenLine = "";
      afterToken = line.substring(cursorPos - 1, line.length());
      tokenLine = line.substring(0, cursorPos - 1);
      if (tokenLine.contains("<"))
      {
         beforeToken = tokenLine.substring(0, tokenLine.lastIndexOf("<") + 1);
         tokenLine = tokenLine.substring(tokenLine.lastIndexOf("<") + 1, tokenLine.length());
         if (tokenLine.contains(">"))
         {
            isTag = false;
            tokenToComplete = "";
            beforeToken = line.substring(0, cursorPos - 1);
         }
         else
         {
            isTag = true;
            tokenToComplete = tokenLine;
         }
      }
      else
      {
         beforeToken = tokenLine;
         tokenToComplete = "";
      }

   }

   private native JavaScriptObject getTokens() /*-{
                                               return $wnd.html_tokens;
                                               }-*/;

   /**
    * Filter token list by TokenType
    * @param type 
    * @return List<Token>
    */
   private List<Token> getTokensByType(TokenType type)
   {
      List<Token> tokens = new ArrayList<Token>();
      if (type != TokenType.TAG && type != TokenType.ATTRIBUTE)
      {
         return tokens;
      }

      if (htmlTokens == null)
      {
         JSONTokenParser parser = new JSONTokenParser();
         htmlTokens = parser.getTokens(new JSONArray(getTokens()));
      }

      for (Token t : htmlTokens)
      {
         if (t.getType() == type)
            tokens.add(t);
      }

      return tokens;
   }
}
