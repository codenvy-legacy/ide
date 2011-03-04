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
package org.exoplatform.ide.editor.codeassistant.html;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.editor.api.CodeLine;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.codeassitant.CodeAssistant;
import org.exoplatform.ide.editor.api.codeassitant.StringProperty;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperties;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidget;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidgetFactory;
import org.exoplatform.ide.editor.codeassistant.CodeAssistantFactory;
import org.exoplatform.ide.editor.codeassistant.css.CssCodeAssistant;
import org.exoplatform.ide.editor.codeassistant.javascript.JavaScriptCodeAssistant;
import org.exoplatform.ide.editor.codeassistant.util.JSONTokenParser;
import org.exoplatform.ide.editor.codeassistant.xml.XmlCodeAssistant;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ExternalTextResource;
import com.google.gwt.resources.client.ResourceCallback;
import com.google.gwt.resources.client.ResourceException;
import com.google.gwt.resources.client.TextResource;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: HtmlCodeAssistant Feb 22, 2011 2:36:49 PM evgen $
 *
 */
public class HtmlCodeAssistant extends CodeAssistant implements TokenWidgetFactory
{

   public interface HtmlBuandle extends ClientBundle
   {

      @Source("org/exoplatform/ide/editor/public/tokens/html_tokens.js")
      ExternalTextResource htmlTokens();
   }

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

   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.CodeAssistant#errorMarckClicked(org.exoplatform.ide.editor.api.Editor, java.util.List, int, int, java.lang.String)
    */
   @Override
   public void errorMarckClicked(Editor editor, List<CodeLine> codeErrorList, int markOffsetX, int markOffsetY,
      String fileMimeType)
   {
   }

   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.CodeAssistant#autocompleteCalled(org.exoplatform.ide.editor.api.Editor, java.lang.String, int, int, java.lang.String, int, int, java.util.List, java.lang.String, org.exoplatform.ide.editor.api.codeassitant.Token)
    */
   @Override
   public void autocompleteCalled(Editor editor, String mimeType, final int cursorOffsetX, final int cursorOffsetY,
      final String lineContent, final int cursorPositionX, int cursorPositionY, List<Token> tokenList,
      String lineMimeType, Token currentToken)
   {
      if (!MimeType.TEXT_HTML.equals(lineMimeType))
      {
         CodeAssistantFactory.getCodeAssistant(lineMimeType).autocompleteCalled(editor, mimeType, cursorOffsetX,
            cursorOffsetY, lineContent, cursorPositionX, cursorPositionY, tokenList, lineMimeType, currentToken);
         return;
      }

      this.editor = editor;
      this.posX = cursorOffsetX;
      this.posY = cursorOffsetY;
      try
      {

         if (htmlTokens == null)
         {
            HtmlBuandle buandle = GWT.create(HtmlBuandle.class);
            buandle.htmlTokens().getText(new ResourceCallback<TextResource>()
            {

               @Override
               public void onSuccess(TextResource resource)
               {
                  JavaScriptObject o = parseJson(resource.getText());
                  JSONObject obj = new JSONObject(o);
                  JSONTokenParser parser = new JSONTokenParser();
                  htmlTokens = parser.getTokens(obj.get("tag").isArray());
                  htmlCoreAttributes = parser.getTokens(obj.get("attributes").isArray());
                  htmlBaseEvents = parser.getTokens(obj.get("baseEvents").isArray());
                  autocompletion(lineContent, cursorPositionX);
               }

               @Override
               public void onError(ResourceException e)
               {
                  e.printStackTrace();
               }
            });
            return;
         }

         autocompletion(lineContent, cursorPositionX);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * @param cursorOffsetX
    * @param cursorOffsetY
    * @param lineContent
    * @param cursorPositionX
    */
   private void autocompletion(String lineContent, int cursorPositionX)
   {
      List<Token> token = new ArrayList<Token>();
      parseTokenLine(lineContent, cursorPositionX);

      if (!isTag)
      {
         token.addAll(htmlTokens);
         openForm(token, this, this);
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
            token.addAll(htmlCoreAttributes);
            Token t = findTokenByName(tag);
            fillAttributes(t, token);
         }
         if (!noBaseEvents.contains(tag))
            token.addAll(htmlBaseEvents);
      }
      else
      {
         token.addAll(htmlTokens);
      }

      openForm(token, this, this);
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
      for (Token t : htmlTokens)
      {
         if (name.equals(t.getName()))
            return t;
      }
      return null;
   }

   private void fillAttributes(Token token, List<Token> tokens)
   {
      if (token.hasProperty(TokenProperties.SUB_TOKEN_LIST))
      {
         tokens.addAll(token.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue());
      }
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
