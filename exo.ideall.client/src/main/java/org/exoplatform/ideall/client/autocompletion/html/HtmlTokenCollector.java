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
import org.exoplatform.ideall.client.autocompletion.TokenCollector;
import org.exoplatform.ideall.client.autocompletion.TokensCollectedCallback;
import org.exoplatform.ideall.client.model.ApplicationContext;

import com.google.gwt.event.shared.HandlerManager;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class HtmlTokenCollector implements TokenCollector
{

   private static List<Token> tags = new ArrayList<Token>();
   
   private static List<Token> attributes = new ArrayList<Token>();

   static
   {
      tags.add(new Token("a", TokenType.TAG, "", "<a></a>", null));
      
      attributes.add(new Token("class", TokenType.ATTRIBUTE, null,"class=\"\"", null));
   }

   private HandlerManager eventBus;

   private ApplicationContext context;

   private TokensCollectedCallback tokensCollectedCallback;

   private String beforeToken;

   private String afterToken;

   private String tokenToComplete;

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

      if (tokenToComplete.endsWith(" "))
      {
         //add attributes
         tokenToComplete = tokenToComplete.substring(0, tokenToComplete.length() - 1);
         System.out.println("tag name - " + tokenToComplete);
         token.add(new Token(tokenToComplete, TokenType.TAG, "close tag with '></" + tokenToComplete + "'", "></"
            + tokenToComplete + ">", null));
         token.add(new Token(tokenToComplete, TokenType.TAG, "close tag with '>'", ">", null));
         beforeToken += tokenToComplete;
         tokenToComplete = "";
         token.addAll(attributes);
      }
      else
      {
         //add tags
         token.addAll(tags);
         System.out.println(tokenToComplete);
      }

      System.out.println(tokenToComplete);
      tokensCollectedCallback.onTokensCollected(token, beforeToken, tokenToComplete, afterToken);
   }

   /**
    * @param line
    */
   private void parseTokenLine(String line, int cursorPos)
   {
      String tokenLine = "";
      tokenToComplete = "";
      afterToken = "";
      beforeToken = "";
      if (line.length() > cursorPos - 1)
      {
         afterToken = line.substring(cursorPos - 1, line.length());
         tokenLine = line.substring(0, cursorPos - 1);

      }
      else
      {
         afterToken = "";

         tokenLine = line;
      }

      for (int i = tokenLine.length() - 1; i >= 0; i--)
      {
         switch (tokenLine.charAt(i))
         {
            case '<' :
               beforeToken = tokenLine.substring(0, i + 1);
               tokenToComplete = tokenLine.substring(i + 1);
               return;

            case '>' :
               beforeToken = tokenLine.substring(0, i + 1);
               tokenToComplete = tokenLine.substring(i + 1);
               return;

            case '/' :
               beforeToken = tokenLine.substring(0, i + 1);
               tokenToComplete = tokenLine.substring(i + 1);
               return;

         }
         beforeToken = "";
         tokenToComplete = tokenLine;
      }

   }

}
