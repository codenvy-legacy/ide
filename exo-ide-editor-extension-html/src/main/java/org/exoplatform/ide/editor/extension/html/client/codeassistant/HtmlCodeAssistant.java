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
package org.exoplatform.ide.editor.extension.html.client.codeassistant;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ExternalTextResource;
import com.google.gwt.resources.client.ResourceCallback;
import com.google.gwt.resources.client.ResourceException;
import com.google.gwt.resources.client.TextResource;

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
import org.exoplatform.ide.editor.codeassistant.css.CssCodeAssistant;
import org.exoplatform.ide.editor.codeassistant.util.JSONTokenParser;
import org.exoplatform.ide.editor.extension.javascript.client.codeassistant.JavaScriptCodeAssistant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: HtmlCodeAssistant Feb 22, 2011 2:36:49 PM evgen $
 *
 */
public class HtmlCodeAssistant extends CodeAssistant implements TokenWidgetFactory, Comparator<Token>
{

   public interface HtmlBundle extends ClientBundle
   {
      @Source("org/exoplatform/ide/editor/extension/html/client/tokens/html_tokens.js")
      ExternalTextResource htmlTokens();
   }

   protected List<Token> htmlTokens = new ArrayList<Token>();

   private List<Token> htmlCoreAttributes;

   private List<Token> htmlBaseEvents;

   protected static List<String> noEndTag = new ArrayList<String>();

   protected static List<String> noCoreAttributes = new ArrayList<String>();

   protected static List<String> noBaseEvents = new ArrayList<String>();

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
    * @see org.exoplatform.ide.editor.api.codeassitant.CodeAssistant#errorMarkClicked(org.exoplatform.ide.editor.api.Editor, java.util.List, int, int, java.lang.String)
    */
   @Override
   public void errorMarkClicked(Editor editor, List<CodeLine> codeErrorList, int markOffsetX, int markOffsetY,
      String fileMimeType)
   {
   }

   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.CodeAssistant#autocompleteCalled(org.exoplatform.ide.editor.api.Editor, java.lang.String, int, int, java.lang.String, int, int, java.util.List, java.lang.String, org.exoplatform.ide.editor.api.codeassitant.Token)
    */
   @Override
   public void autocompleteCalled(Editor editor, final int cursorOffsetX, final int cursorOffsetY,
      List<Token> tokenList, String lineMimeType, Token currentToken)
   {
      if (MimeType.TEXT_CSS.equals(lineMimeType))
      {
         new CssCodeAssistant().autocompleteCalled(editor, cursorOffsetX, cursorOffsetY, tokenList, lineMimeType,
            currentToken);
         return;
      }

      if (MimeType.APPLICATION_JAVASCRIPT.equals(lineMimeType))
      {
         new JavaScriptCodeAssistant().autocompleteCalled(editor, cursorOffsetX, cursorOffsetY, tokenList,
            lineMimeType, currentToken);
         return;
      }

      this.editor = editor;
      this.posX = cursorOffsetX;
      this.posY = cursorOffsetY;
      try
      {

         if (htmlTokens.size() == 0)
         {
            getTokens(editor.getLineContent(editor.getCursorRow()), editor.getCursorCol());
            return;
         }

         autocompletion(editor.getLineContent(editor.getCursorRow()), editor.getCursorCol());
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Receive and parse tokens
    * @param lineContent
    * @param cursorPositionX
    * @throws ResourceException
    */
   protected void getTokens(final String lineContent, final int cursorPositionX) throws ResourceException
   {
      HtmlBundle buandle = GWT.create(HtmlBundle.class);
      buandle.htmlTokens().getText(new ResourceCallback<TextResource>()
      {

         @Override
         public void onSuccess(TextResource resource)
         {
            JavaScriptObject o = parseJson(resource.getText());
            JSONObject obj = new JSONObject(o);
            JSONTokenParser parser = new JSONTokenParser();
            htmlTokens.addAll(parser.getTokens(obj.get("tag").isArray()));
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
   }

   /**
    * Do autocompletion
    * @param lineContent
    * @param cursorPositionX
    */
   private void autocompletion(String lineContent, int cursorPositionX)
   {
      List<Token> token = new ArrayList<Token>();
      parseTokenLine(lineContent, cursorPositionX);

      if (!isTag)
      {
         showDefaultTags(token);
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
            t1.setProperty(TokenProperties.SHORT_HINT, new StringProperty(" close tag with '>'"));
            t1.setProperty(TokenProperties.CODE, new StringProperty(">"));
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

      Collections.sort(token, this);
      openForm(token, this, this);
   }

   /**
    * 
    * @param token
    */
   protected void showDefaultTags(List<Token> token)
   {
      token.addAll(htmlTokens);
      Collections.sort(token, this);
      openForm(token, this, this);
   }

   /**
    * Parse string line received from editor
    * @param line current line where autocompletion called
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
         isTag = false;
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

   /**
    * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
    */
   @Override
   public int compare(Token t1, Token t2)
   {
      if (t1.getType() == t2.getType())
         return t1.getName().compareToIgnoreCase(t2.getName());

      if (t1.getType() == TokenType.TAG)
         return -1;
      else if (t2.getType() == TokenType.TAG)
         return 1;
      else
         return 0;

   }

}
