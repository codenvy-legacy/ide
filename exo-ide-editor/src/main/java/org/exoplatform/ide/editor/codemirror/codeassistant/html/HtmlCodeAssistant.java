/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.editor.codemirror.codeassistant.html;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.codeassitant.CodeAssistant;
import org.exoplatform.ide.editor.api.codeassitant.CodeError;
import org.exoplatform.ide.editor.api.codeassitant.StringProperty;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperties;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidget;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidgetFactory;
import org.exoplatform.ide.editor.codemirror.codeassistant.util.JSONTokenParser;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.json.client.JSONArray;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: HtmlCodeAssistant Feb 22, 2011 2:36:49 PM evgen $
 *
 */
public class HtmlCodeAssistant extends CodeAssistant implements TokenWidgetFactory
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


   private boolean isTag = false;

   public HtmlCodeAssistant(HandlerManager eventBus)
   {
      super(eventBus);
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
    * @see org.exoplatform.ide.editor.api.codeassitant.CodeAssistant#errorMarckClicked(org.exoplatform.ide.editor.api.Editor, java.util.List, int, int, java.lang.String)
    */
   @Override
   public void errorMarckClicked(Editor editor, List<CodeError> codeErrorList, int markOffsetX, int markOffsetY,
      String fileMimeType)
   {
   }

   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.CodeAssistant#autocompleteCalled(org.exoplatform.ide.editor.api.Editor, java.lang.String, int, int, java.lang.String, int, int, java.util.List, java.lang.String, org.exoplatform.ide.editor.api.codeassitant.Token)
    */
   @Override
   public void autocompleteCalled(Editor editor, String mimeType, int cursorOffsetX, int cursorOffsetY,
      String lineContent, int cursorPositionX, int cursorPositionY, List<Token> tokenList, String lineMimeType,
      Token currentToken)
   {
      try
      {
         List<Token> token = new ArrayList<Token>();

         this.editor = editor;
         parseTokenLine(lineContent, cursorPositionX);

         if (!isTag)
         {
            token.addAll(getHtmlTagTokens());
            openForm(cursorOffsetX, cursorOffsetY, token, this, this);
            return;
         }

         if (tokenToComplete.endsWith(" ") || tokenToComplete.endsWith("\""))
         {
            //add attributes
            String tag = "";

            tag = tokenToComplete.substring(0, tokenToComplete.indexOf(" "));

            if (noEndTag.contains(tag))
            {
               Token t = new TokenImpl(tag, TokenType.TAG);
               t.setProperty(TokenProperties.SHORT_HINT, new StringProperty(" close tag with '/>'"));
               t.setProperty(TokenProperties.CODE, new StringProperty(" />"));

               token.add(t);
            }
            else
            {
               Token t = new TokenImpl(tag, TokenType.TAG);
               t.setProperty(TokenProperties.SHORT_HINT, new StringProperty(" close tag with '></" + tag + ">'"));
               t.setProperty(TokenProperties.CODE, new StringProperty("></" + tag + ">"));
               token.add(t);

               Token t1 = new TokenImpl(tag, TokenType.TAG);
               t.setProperty(TokenProperties.SHORT_HINT, new StringProperty(" close tag with '>'"));
               t.setProperty(TokenProperties.CODE, new StringProperty(">"));
               token.add(t1);
            }
            beforeToken += tokenToComplete;
            tokenToComplete = "";
            if (noCoreAttributes.contains(tag))
            {
               Token t = findTokenByName(tag);
               fillAttributes(t, token);
            }
            else
            {
               fillCoreAttributes(token);
               Token t = findTokenByName(tag);
               fillAttributes(t, token);
            }
            if (!noBaseEvents.contains(tag))
               token.addAll(getBaseEvent());
         }
         else
         {
            token.addAll(getHtmlTagTokens());
         }

         openForm(cursorOffsetX, cursorOffsetY, token, this, this);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
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

   private Token findTokenByName(String name)
   {
      for (Token t : getHtmlTagTokens())
      {
         if (name.equals(t.getName()))
            return t;
      }
      return null;
   }

   private void fillAttributes(Token token, List<Token> tokens)
   {
      if (token.hasProperty(TokenProperties.CHILD_TOKEN_LIST))
      {
         tokens.addAll(token.getProperty(TokenProperties.CHILD_TOKEN_LIST).isArrayProperty().arrayValue());
      }
   }

   private List<Token> getBaseEvent()
   {
      if (htmlBaseEvents == null)
      {
         JSONTokenParser parser = new JSONTokenParser();
         htmlBaseEvents = parser.getTokens(new JSONArray(getHtmlBaseEvents()));
      }

      return htmlBaseEvents;
   }

   /**
    * @param token
    */
   private void fillCoreAttributes(List<Token> token)
   {
      if (htmlCoreAttributes == null)
      {
         JSONTokenParser parser = new JSONTokenParser();
         htmlCoreAttributes = parser.getTokens(new JSONArray(getHtmlCoreAttributes()));
      }

      token.addAll(htmlCoreAttributes);
   }

   private List<Token> getHtmlTagTokens()
   {
      if (htmlTokens == null)
      {
         JSONTokenParser parser = new JSONTokenParser();
         htmlTokens = parser.getTokens(new JSONArray(getHtmlTagTokensJSO()));
      }
      return htmlTokens;
   }

   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidgetFactory#buildTokenWidget(org.exoplatform.ide.editor.api.codeassitant.Token)
    */
   @Override
   public TokenWidget buildTokenWidget(Token token)
   {
      return new HtmlTokenWidget(token);
   }

}
