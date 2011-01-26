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
package org.exoplatform.ide.client.module.netvibes.codeassistant.autocomplete;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.json.client.JSONArray;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.editor.api.Token;
import org.exoplatform.gwtframework.editor.api.Token.TokenType;
import org.exoplatform.ide.client.framework.codeassistant.TokenExt;
import org.exoplatform.ide.client.framework.codeassistant.TokenExtProperties;
import org.exoplatform.ide.client.framework.codeassistant.TokenExtType;
import org.exoplatform.ide.client.framework.codeassistant.TokensCollectedCallback;
import org.exoplatform.ide.client.framework.codeassistant.api.TokenCollectorExt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Token Collector for tokens, that used in netvibes files.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: NetvibesTokenCollector.java Jan 19, 2011 12:01:24 PM vereshchaka $
 *
 */
public class NetvibesTokenCollector implements TokenCollectorExt, Comparator<TokenExt>, ExceptionThrownHandler
{

   private static List<Token> defaultTokens;

   private static List<Token> apiTokens;

   private HashMap<String, Token> filteredToken = new HashMap<String, Token>();

   private String beforeToken;

   private String afterToken;

   private String tokenToComplete;

   private Handlers handlers;

   public NetvibesTokenCollector(HandlerManager eventBus)
   {
      handlers = new Handlers(eventBus);
   }

   private native JavaScriptObject getTokens() /*-{
      return $wnd.javascript_tokens;
   }-*/;

   private native JavaScriptObject getNetvibesTokens() /*-{
      return $wnd.js_netvibes_tokens;
   }-*/;

   private native JavaScriptObject getNetvibesApiTokens() /*-{
      return $wnd.netvibes_api_tokens;
   }-*/;

   /**
    * @see org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler#onError(org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent)
    */
   @Override
   public void onError(ExceptionThrownEvent event)
   {
      handlers.removeHandlers();
   }

   /**
    * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
    */
   @Override
   public int compare(TokenExt t1, TokenExt t2)
   {
      /*
       * If tokens have the same types,
       * than compare in alphabetic order.
       */
      if (t1.getType() == t2.getType())
      {
         return t1.getName().compareTo(t2.getName());
      }

      /*
       * At the begin of list must be variables.
       */
      if (t2.getType() == TokenExtType.VARIABLE)
      {
         return 1;
      }
      if (t1.getType() == TokenExtType.VARIABLE)
      {
         return -1;
      }

      /*
       * At the end of list must be keywords.
       */
      if (t1.getType() == TokenExtType.KEYWORD)
      {
         return 1;
      }

      if (t2.getType() == TokenExtType.KEYWORD)
      {
         return -1;
      }

      /*
       * If first and second template is not keyword
       * check, if one of them is template.
       * Template must be less, that all other types, except keyword.
       */
      if (t1.getType() == TokenExtType.TEMPLATE)
      {
         return 1;
      }
      if (t2.getType() == TokenExtType.TEMPLATE)
      {
         return -1;
      }

      return t1.getName().compareTo(t2.getName());
   }

   /**
    * @see org.exoplatform.ide.client.framework.codeassistant.api.TokenCollectorExt#collectTokens(java.lang.String, org.exoplatform.gwtframework.editor.api.Token, int, int, java.util.List, org.exoplatform.ide.client.framework.codeassistant.TokensCollectedCallback)
    */
   @Override
   public void collectTokens(String line, Token token, int lineNum, int cursorPos, List<Token> tokenFromParser,
      TokensCollectedCallback<TokenExt> tokensCollectedCallback)
   {
      List<Token> tokens = new ArrayList<Token>();

      filteredToken.clear();

      tokenFromParser = getJsTokens(tokenFromParser);
      tokenFromParser = getTokenJavaScript(tokenFromParser);

      parseTokenLine(line, cursorPos);

      if (defaultTokens == null)
      {
         JSONTokenParser parser = new JSONTokenParser();
         JSONArray tokenArray = new JSONArray(getTokens());

         defaultTokens = parser.getTokens(tokenArray);

         tokenArray = new JSONArray(getNetvibesTokens());

         defaultTokens.addAll(parser.getTokens(tokenArray));
      }
      if (apiTokens == null)
      {
         JSONTokenParser parser = new JSONTokenParser();
         JSONArray tokenArray = new JSONArray(getNetvibesApiTokens());
         apiTokens = parser.getTokens(tokenArray);
      }

      if (beforeToken.endsWith("."))
      {
         tokenFromParser.addAll(apiTokens);
         filterToken(beforeToken, tokenFromParser);
      }
      else
      {
         tokens.addAll(defaultTokens);
         tokens.addAll(apiTokens);
         filterToken(lineNum, tokenFromParser);

      }
      tokens.addAll(filteredToken.values());

      List<TokenExt> tokensExt = convertTokens(tokens);

      Collections.sort(tokensExt, this);

      tokensCollectedCallback.onTokensCollected(tokensExt, beforeToken, tokenToComplete, afterToken);
   }

   private List<Token> getJsTokens(List<Token> tokens)
   {
      final String mimeType = MimeType.APPLICATION_JAVASCRIPT;
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
            newTokenList.addAll(getJsTokens(t.getSubTokenList()));
            isAdd = false;
         }
      }

      return newTokenList;
   }

   /**
    * @param beforeToken2
    * @param tokenFromParser
    */
   private void filterToken(String token, List<Token> list)
   {
      filteredToken.clear();
      token = token.substring(0, token.length() - 1);

      String[] tokens = token.split("[({)}; ]");
      Token foundToken = null;
      if (tokens.length != 0)
      {
         foundToken = findToken(tokens[tokens.length - 1], list);
      }
      else
      {
         foundToken = findToken(token, list);
      }
      if (foundToken != null)
      {
         if (foundToken.getSubTokenList() != null)
         {
            for (Token t : foundToken.getSubTokenList())
            {
               filteredToken.put(t.getName(), t);
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

      if (token.contains("."))
      {
         String to[] = token.split("\\.");
         List<Token> tokens = list;
         Token t = null;
         for(String fqnPath : to)
         {
           t = findTokenInList(fqnPath, tokens);
           if(t != null)
              tokens = t.getSubTokenList();
           else
           {
              return null;
           }
         }
         return t;
      }
      else
      {
        return findTokenInList(token, list);
      }
   }
   
   private Token findTokenInList(String token, List<Token> list)
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
         filteredToken.put(t.getName(), t);
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
         filteredToken.put(t.getName(), t);
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

            default :
               break;
         }
         beforeToken = "";
         tokenToComplete = tokenLine;
      }

   }

   /**
    * @param tokenFromParser
    */
   private List<Token> getTokenJavaScript(List<Token> tokenFromParser)
   {
      List<Token> tokens = new ArrayList<Token>();

      final String tagName = "script";

      //condition for the return of the recursion:
      //if we reach the script tag
      if (!tokenFromParser.isEmpty() && !tokenFromParser.get(0).getName().equals(tagName))
      {
         return tokenFromParser;
      }
      for (int i = 0; i < tokenFromParser.size(); i++)
      {
         Token token = tokenFromParser.get(i);
         if (token.getName() == null)
            continue;

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

   private TokenExt getMethodOrFunctionToken(Token token)
   {
      TokenExt tex = new TokenExt(token.getName(), TokenExtType.METHOD);
      if (token.getShortDescription() != null)
      {
         tex.setProperty(TokenExtProperties.SHORT_HINT, token.getShortDescription());
      }
      if (token.getFullDescription() != null)
      {
         tex.setProperty(TokenExtProperties.FULL_TEXT, token.getFullDescription());
      }
      if (token.getFqn() != null)
      {
         tex.setProperty(TokenExtProperties.FQN, token.getFqn());
      }
      if (token.getCode() != null)
      {
         tex.setProperty(TokenExtProperties.CODE, token.getCode());
      }

      return tex;
   }

   private List<TokenExt> convertTokens(List<Token> tokensFromParser)
   {
      List<TokenExt> tokens = new ArrayList<TokenExt>();
      for (Token t : tokensFromParser)
      {
         final TokenExt tex;
         switch (t.getType())
         {
            case METHOD :
            case FUNCTION :
               tokens.add(getMethodOrFunctionToken(t));
               break;
            case PROPERTY :
               tex = new TokenExt(t.getName(), TokenExtType.PROPERTY);
               tex.setProperty(TokenExtProperties.CODE, t.getCode());
               tex.setProperty(TokenExtProperties.SHORT_HINT, t.getShortDescription());
               if (t.getFullDescription() != null)
                  tex.setProperty(TokenExtProperties.FULL_TEXT, t.getFullDescription());
               tokens.add(tex);
               break;
            case VARIABLE :
               tex = new TokenExt(t.getName(), TokenExtType.VARIABLE);
               if (t.getShortDescription() != null)
                  tex.setProperty(TokenExtProperties.SHORT_HINT, t.getShortDescription());
               if (t.getFullDescription() != null)
                  tex.setProperty(TokenExtProperties.FULL_TEXT, t.getFullDescription());
               tokens.add(tex);
               break;
            case KEYWORD :
               tex = new TokenExt(t.getName(), TokenExtType.KEYWORD);
               tokens.add(tex);
               break;
            case TEMPLATE :
               tex = new TokenExt(t.getName(), TokenExtType.TEMPLATE);
               tex.setProperty(TokenExtProperties.CODE, t.getCode());
               tex.setProperty(TokenExtProperties.SHORT_HINT, t.getShortDescription());
               tex.setProperty(TokenExtProperties.FULL_TEXT, t.getFullDescription());
               tokens.add(tex);
               break;
            case CLASS :
               tex = new TokenExt(t.getName(), TokenExtType.CLASS);
               tex.setProperty(TokenExtProperties.CODE, t.getCode());
               tex.setProperty(TokenExtProperties.SHORT_HINT, t.getShortDescription());
               if (t.getFqn() != null)
               {
                  tex.setProperty(TokenExtProperties.FQN, t.getFqn());
               }
               tokens.add(tex);
               break;
            default :
               break;
         }
      }

      return tokens;
   }

}
