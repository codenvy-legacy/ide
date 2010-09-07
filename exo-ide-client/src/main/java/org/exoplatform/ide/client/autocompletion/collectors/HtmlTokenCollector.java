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
import java.util.List;

import org.exoplatform.gwtframework.editor.api.Token;
import org.exoplatform.gwtframework.editor.api.Token.TokenType;
import org.exoplatform.ide.client.autocompletion.JSONTokenParser;
import org.exoplatform.ide.client.autocompletion.TokenCollector;
import org.exoplatform.ide.client.autocompletion.TokensCollectedCallback;

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
   
   private static List<Token> htmlCoreAttributes;
   
   private static List<Token> htmlBaseEvents;

   private static List<String> noEndTag = new ArrayList<String>();

   private static List<String> noCoreAttributes = new ArrayList<String>();
   
   private static List<String> noBaseEvents = new ArrayList<String>();
   
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

      noCoreAttributes.add("base");
      noCoreAttributes.add("head");
      noCoreAttributes.add("html");
      noCoreAttributes.add("meta");
      noCoreAttributes.add("param");
      noCoreAttributes.add("script");
      noCoreAttributes.add("style");
      noCoreAttributes.add("title");
      
      
      noBaseEvents.add("base");
      noBaseEvents.add("bdo");
      noBaseEvents.add("br");
      noBaseEvents.add("frame");
      noBaseEvents.add("frameset");
      noBaseEvents.add("head");
      noBaseEvents.add("html");
      noBaseEvents.add("iframe");
      noBaseEvents.add("meta");
      noBaseEvents.add("param");
      noBaseEvents.add("script");
      noBaseEvents.add("style");
      noBaseEvents.add("title");
   }

   private String beforeToken;

   private String afterToken;

   private String tokenToComplete;

   private boolean isTag = false;

   public HtmlTokenCollector(HandlerManager eventBus)
   {
   }

   /**
    * @see org.exoplatform.ide.client.autocompletion.TokenCollector#getTokens(java.lang.String, int, int, java.util.List)
    */
   public void getTokens(String line, String lineMimeType, int lineNum, int cursorPos, List<Token> tokenFromParser, TokensCollectedCallback tokensCollectedCallback)
   {
      List<Token> token = new ArrayList<Token>();

      parseTokenLine(line, cursorPos);

      if (!isTag)
      {
         token.addAll(getHtmlTagTokens());
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
         if(noCoreAttributes.contains(tag))
         {
            //TODO Add only no attributes;
            Token t = findTokenByName(tag);
            fillAttributes(t,token);
         }
         else
         {
           fillCoreAttributes(token);
           Token t = findTokenByName(tag);
           fillAttributes(t,token);
         }
         if(!noBaseEvents.contains(tag))
           token.addAll(getBaseEvent());
        // token.addAll(getTokensByType(TokenType.ATTRIBUTE));
      }
      else
      {
         token.addAll(getHtmlTagTokens());
      }

      tokensCollectedCallback.onTokensCollected(token, beforeToken, tokenToComplete, afterToken);
   }



   /**
    * @return
    */
   private List<Token> getBaseEvent()
   {
      if(htmlBaseEvents == null)
      {
         JSONTokenParser parser = new JSONTokenParser();
         htmlBaseEvents = parser.getTokens(new JSONArray(getHtmlBaseEvents()));
      }
      
      return htmlBaseEvents;
   }

   /**
    * @param name
    * @return
    */
   private Token findTokenByName(String name)
   {
      for(Token t : getHtmlTagTokens())
      {
         if(t.getName().equals(name))
            return t;
      }
      return null;
   }

   /**
    * @param token 
    * @param token
    */
   private void fillAttributes(Token token, List<Token> tokens)
   {
      if(token.getSubTokenList()!= null)
      {
         tokens.addAll(token.getSubTokenList());
      }
   }

   /**
    * @param token
    */
   private void fillCoreAttributes(List<Token> token)
   {
      if(htmlCoreAttributes == null)
      {
         JSONTokenParser parser = new JSONTokenParser();
         htmlCoreAttributes = parser.getTokens(new JSONArray(getHtmlCoreAttributes()));
      }
      
      token.addAll(htmlCoreAttributes);
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

   private native JavaScriptObject getHtmlTagTokensJSO() /*-{
                                               return $wnd.html_tokens;
                                               }-*/;

   
   private native JavaScriptObject getHtmlCoreAttributes()/*-{
      return $wnd.html_attributes;
   }-*/;
   
   private native JavaScriptObject getHtmlBaseEvents()/*-{
      return $wnd.html_baseEvents;
   }-*/;
   
  
   
   /**
    * Filter token list by TokenType
    * @param type 
    * @return List<Token>
    */
   private List<Token> getHtmlTagTokens()
   {
//      List<Token> tokens = new ArrayList<Token>();
//      if (type != TokenType.TAG && type != TokenType.ATTRIBUTE)
//      {
//         return tokens;
//      }

      if (htmlTokens == null)
      {
         JSONTokenParser parser = new JSONTokenParser();
         htmlTokens = parser.getTokens(new JSONArray(getHtmlTagTokensJSO()));
      }

//      for (Token t : htmlTokens)
//      {
//         if (t.getType() == type)
//            tokens.add(t);
//      }

      return htmlTokens;
   }
}
