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
package org.exoplatform.ide.client.autocompletion.collectors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.exoplatform.gwtframework.editor.api.Token;
import org.exoplatform.gwtframework.editor.api.Token.TokenType;
import org.exoplatform.ide.client.framework.codeassistant.TokenCollector;
import org.exoplatform.ide.client.framework.codeassistant.TokensCollectedCallback;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class XmlTokenCollector implements TokenCollector<Token>
{

   private String beforeToken;

   private String afterToken;

   private String tokenToComplete;

   private HashMap<String, Token> filteredToken = new HashMap<String, Token>();

   /**
    * @see org.exoplatform.ide.client.framework.codeassistant.api.TokenCollector#collectTokens(java.lang.String, Token, int, int, java.util.List, org.exoplatform.ide.client.framework.codeassistant.api.TokensCollectedCallback)
    */
   public void getTokens(String line, String lineMimeType, int lineNum, int cursorPos, List<Token> tokenFromParser,
      TokensCollectedCallback<Token> tokensCollectedCallback)
   {
      List<Token> token = new ArrayList<Token>();
      filteredToken.clear();

      parseTokenLine(line, cursorPos);
      filterXMLTagTokens(tokenFromParser);
      if ("".equals(tokenToComplete))
      {
         token.addAll(filteredToken.values());
         tokensCollectedCallback.onTokensCollected(token, beforeToken, tokenToComplete, afterToken);
         return;
      }

      if (tokenToComplete.endsWith(" ") || tokenToComplete.endsWith("\""))
      {
         //add attributes
         String tag = tokenToComplete.substring(0, tokenToComplete.indexOf(" "));

         token.add(new Token(tag, TokenType.TAG, "close tag with '/>'", " />", null));
         token.add(new Token(tag, TokenType.TAG, "close tag with '></" + tag + ">'", "></" + tag + ">", null));
         //         token.addAll(filteredToken.values());
         beforeToken += tokenToComplete;
         tokenToComplete = "";
      }
      else
      {
         token
            .add(new Token(tokenToComplete, TokenType.TAG, "close tag with '/>'", "<" + tokenToComplete + " />", null));
         token.add(new Token(tokenToComplete, TokenType.TAG, "close tag with '></" + tokenToComplete + ">'", "<"
            + tokenToComplete + "></" + tokenToComplete + ">", null));
         token.addAll(filteredToken.values());
      }

      tokensCollectedCallback.onTokensCollected(token, beforeToken, tokenToComplete, afterToken);
   }

   /**
    * @return
    */
   private void filterXMLTagTokens(List<Token> tokens)
   {
      for (Token t : tokens)
      {
         if (t.getName() != null || t.getType() != TokenType.TAG_BREAK)
         {
            t.setCode("<" + t.getName() + "></" + t.getName() + ">");
            filteredToken.put(t.getName(), t);
            if (t.getSubTokenList() != null && !t.getSubTokenList().isEmpty())
            {
               filterXMLTagTokens(t.getSubTokenList());
            }
         }
      }
   }

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
            tokenToComplete = "";
            beforeToken = line.substring(0, cursorPos - 1);
         }
         else
         {
            tokenToComplete = tokenLine;
         }
      }
      else
      {
         beforeToken = tokenLine;
         tokenToComplete = "";
      }

   }
}
