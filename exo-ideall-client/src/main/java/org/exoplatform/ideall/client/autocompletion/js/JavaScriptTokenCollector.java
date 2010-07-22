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
package org.exoplatform.ideall.client.autocompletion.js;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.editor.api.Token;
import org.exoplatform.gwtframework.editor.api.Token.TokenType;
import org.exoplatform.ideall.client.autocompletion.JSONTokenParser;
import org.exoplatform.ideall.client.autocompletion.api.TokenCollector;
import org.exoplatform.ideall.client.autocompletion.api.TokensCollectedCallback;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.json.client.JSONArray;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class JavaScriptTokenCollector implements TokenCollector
{

   private static List<Token> defaultTokens;
   
   private HandlerManager eventBus;

   private List<Token> filteredToken = new ArrayList<Token>();

   private String beforeToken;

   private String afterToken;

   private String tokenToComplete;

   public JavaScriptTokenCollector(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
   }
   
   private native JavaScriptObject getTokens() /*-{
                                               return $wnd.javascript_tokens;
                                               }-*/;
   
   public void getTokens(String line, String lineMimeType, int lineNum, int cursorPos, List<Token> tokenFromParser, TokensCollectedCallback tokensCollectedCallback)
   {

      List<Token> tokens = new ArrayList<Token>();

      filteredToken.clear();

      parseTokenLine(line, cursorPos);

      if (beforeToken.endsWith("."))
      {
         filterToken(beforeToken, tokenFromParser);
      }
      else
      {
         if(defaultTokens == null)
         {
            JSONTokenParser parser = new JSONTokenParser();
            JSONArray tokenArray = new JSONArray(getTokens());
            
            defaultTokens = parser.getTokens(tokenArray);
         }
         tokens.addAll(defaultTokens);
//         printTokens(defaultTokens);
         filterToken(lineNum, tokenFromParser);
      }
      tokens.addAll(filteredToken);

      tokensCollectedCallback.onTokensCollected(tokens, beforeToken, tokenToComplete, afterToken);
   }

   /**
    * @param beforeToken2
    * @param tokenFromParser
    */
   private void filterToken(String token, List<Token> list)
   {
      filteredToken.clear();
      token = token.substring(0, token.length() - 1);

      String tokens[] = token.split("[({)}; ]");
      Token t = null;
      if (tokens.length != 0)
      {
         t = findToken(tokens[tokens.length - 1], list);
      }
      else
      {
         t = findToken(token, list);
      }
      if (t != null)
      {
         if (t.getSubTokenList() != null)
         {
            for (Token sub : t.getSubTokenList())
            {
               filteredToken.add(sub);
            }
         }
      }
   }

   /**
    * @param token
    * @return
    */
   private Token findToken(String token, List<Token> list)
   {
      for (Token t : list)
      {
         if (token.equals(t.getName()))
         {
            return t;
         }
      }
      return null;
   }

   /**
    * @param currentLine
    * @param token
    */
   private void filterToken(int currentLine, List<Token> token)
   {
      Token lastFunction = null;

      for (int i = 0; i < token.size(); i++)
      {
         Token t = token.get(i);
         if (t.getLineNumber() > currentLine)
         {
            break;
         }
         if (t.getType().equals(TokenType.FUNCTION))
         {
            lastFunction = t;
         }
         filteredToken.add(t);
      }

      if (lastFunction != null && lastFunction.getSubTokenList() != null)
      {
         filterSubToken(currentLine, lastFunction.getSubTokenList());

      }

   }

   /**
    * @param currentLine
    * @param subToken
    */
   private void filterSubToken(int currentLine, List<Token> subToken)
   {
      for (int i = 0; i < subToken.size(); i++)
      {
         Token t = subToken.get(i);

         if (t.getSubTokenList() != null)
         {
            filterSubToken(currentLine, t.getSubTokenList());
         }
         filteredToken.add(t);
      }
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
         if (line.endsWith(" "))
         {
            tokenToComplete = "";
            beforeToken = line;
            return;
         }

         tokenLine = line;
      }

      for (int i = tokenLine.length() - 1; i >= 0; i--)
      {
         switch (tokenLine.charAt(i))
         {
            case ' ' :
               beforeToken = tokenLine.substring(0, i + 1);
               tokenToComplete = tokenLine.substring(i + 1);
               return;

            case '.' :
               beforeToken = tokenLine.substring(0, i + 1);
               tokenToComplete = tokenLine.substring(i + 1);
               return;

            case '(' :
               beforeToken = tokenLine.substring(0, i + 1);
               tokenToComplete = tokenLine.substring(i + 1);
               return;

            case ')' :
               beforeToken = tokenLine.substring(0, i + 1);
               tokenToComplete = tokenLine.substring(i + 1);
               return;

            case '{' :
               beforeToken = tokenLine.substring(0, i + 1);
               tokenToComplete = tokenLine.substring(i + 1);
               return;

            case '}' :
               beforeToken = tokenLine.substring(0, i + 1);
               tokenToComplete = tokenLine.substring(i + 1);
               return;

            case ';' :
               beforeToken = tokenLine.substring(0, i + 1);
               tokenToComplete = tokenLine.substring(i + 1);
               return;

            case '[' :
               beforeToken = tokenLine.substring(0, i + 1);
               tokenToComplete = tokenLine.substring(i + 1);
               return;

            case ']' :
               beforeToken = tokenLine.substring(0, i + 1);
               tokenToComplete = tokenLine.substring(i + 1);
               return;
         }
         beforeToken = "";
         tokenToComplete = tokenLine;
      }

   }

   private void printTokens(List<Token> token)
   {
      for (Token t : token)
      {
         if (t.getSubTokenList() != null)
         {
            printTokens(t.getSubTokenList());
         }
         System.out.println(t.getName() + " " + t.getType());
      }
      System.out.println("+++++++++++++++++++++++++");
   }

}
