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
package org.exoplatform.ide.editor.extension.netvibes.client.codeassistant;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ExternalTextResource;
import com.google.gwt.resources.client.ResourceCallback;
import com.google.gwt.resources.client.ResourceException;
import com.google.gwt.resources.client.TextResource;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.editor.api.CodeLine;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.codeassitant.ArrayProperty;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperties;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.codeassistant.util.JSONTokenParser;
import org.exoplatform.ide.editor.extension.html.client.codeassistant.HtmlCodeAssistant;
import org.exoplatform.ide.editor.extension.javascript.client.codeassistant.JavaScriptTokenWidgetFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: NetvibesCodeAssistant Mar 2, 2011 11:11:45 AM evgen $
 *
 */
public class NetvibesCodeAssistant extends HtmlCodeAssistant implements Comparator<Token>, ResourceCallback<TextResource>
{

   public interface NetvibesBundle extends ClientBundle
   {

      @Source("org/exoplatform/ide/editor/extension/netvibes/client/tokens/netvibes_tokens.js")
      ExternalTextResource netvibesTokens();
   }

   private static List<Token> defaultTokens;

   private static List<Token> nVApiTokens;

   private static Map<String, Token> tokensByFQN = new HashMap<String, Token>();

   private HashMap<String, Token> filteredToken = new HashMap<String, Token>();

   private String lineContent;

   private int cursorPositionX;

   private int cursorPositionY;

   private List<Token> tokenFromParser;

   private Token currentToken;

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
      final List<Token> tokenFromParser, String lineMimeType, final Token currentToken)
   {
      if (!MimeType.APPLICATION_JAVASCRIPT.equals(lineMimeType))
      {
         super.autocompleteCalled(editor, cursorOffsetX, cursorOffsetY, tokenFromParser, lineMimeType, currentToken);
         return;
      }
      try
      {
         this.editor = editor;
         this.posX = cursorOffsetX;
         this.posY = cursorOffsetY;
         if (defaultTokens == null && nVApiTokens == null)
         {
            NetvibesBundle bundle = GWT.create(NetvibesBundle.class);
            cursorPositionY = editor.getCursorRow();
            lineContent = editor.getLineContent(cursorPositionY);
            cursorPositionX = editor.getCursorCol();
            this.tokenFromParser = tokenFromParser;
            this.currentToken = currentToken;
            bundle.netvibesTokens().getText(this);
            return;
         }

         autocompletion(lineContent, cursorPositionX, cursorPositionY, tokenFromParser, currentToken);
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
    * @param cursorPositionY
    * @param tokenFromParser
    * @param currentToken
    */
   private void autocompletion(String lineContent, int cursorPositionX, int cursorPositionY,
      List<Token> tokenFromParser, Token currentToken)
   {
      List<Token> tokens = new ArrayList<Token>();

      filteredToken.clear();

      tokenFromParser = getJsTokens(tokenFromParser);
      tokenFromParser = getTokenJavaScript(tokenFromParser);

      parseTokenLine(lineContent, cursorPositionX);

      if (beforeToken.endsWith("."))
      {
         if (currentToken != null && currentToken.hasProperty(TokenProperties.ELEMENT_TYPE))
         {
            Token clazz =
               tokensByFQN.get(currentToken.getProperty(TokenProperties.ELEMENT_TYPE).isStringProperty().stringValue());
            if (clazz != null)
               tokens.addAll(clazz.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue());
         }
         else
         {
            tokenFromParser.addAll(nVApiTokens);
            filterToken(beforeToken, tokenFromParser);
         }
      }
      else
      {
         tokens.addAll(defaultTokens);
         tokens.addAll(nVApiTokens);
         filterToken(cursorPositionY, tokenFromParser);
      }
      tokens.addAll(filteredToken.values());

      Collections.sort(tokens, this);
      openForm(tokens, new JavaScriptTokenWidgetFactory(), this);
   }

   /**
    * Put all Class tokens in to map, where key is FQN plus Name of the Class
    * @param tokens list 
    */
   @SuppressWarnings("unchecked")
   private void putClassTokensToMapByFQN(List<Token> tokens)
   {
      for (Token t : tokens)
      {
         if (t.getType() == TokenType.CLASS)
         {
            String pack =
               t.hasProperty(TokenProperties.FQN) ? t.getProperty(TokenProperties.FQN).isStringProperty().stringValue()
                  + "." : "";
            tokensByFQN.put(pack + t.getName(), t);
         }
         if (t.hasProperty(TokenProperties.SUB_TOKEN_LIST))
         {
            putClassTokensToMapByFQN((List<Token>)t.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty()
               .arrayValue());
         }
      }
   }

   @SuppressWarnings("unchecked")
   private List<Token> getJsTokens(List<Token> tokens)
   {
      final String mimeType = MimeType.APPLICATION_JAVASCRIPT;
      ArrayList<Token> newTokenList = new ArrayList<Token>();
      boolean isAdd = false;
      for (int i = 0; i < tokens.size(); i++)
      {
         Token t = tokens.get(i);
         if ((t.getName() != null)
            && (t.getProperty(TokenProperties.MIME_TYPE).isStringProperty().stringValue().equals(mimeType)))
         {
            newTokenList.add(t);
            isAdd = true;
         }
         if ((t.hasProperty(TokenProperties.SUB_TOKEN_LIST)) && (!isAdd))
         {
            newTokenList.addAll(getJsTokens((List<Token>)t.getProperty(TokenProperties.SUB_TOKEN_LIST)
               .isArrayProperty().arrayValue()));
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
         if ("widget".equals(tokens[tokens.length - 1]))
            foundToken = tokensByFQN.get("UWA.Widget");
         else
            foundToken = findToken(tokens[tokens.length - 1], list);
      }
      else
      {
         if ("widget".equals(token))
            foundToken = tokensByFQN.get("UWA.Widget");
         else
            foundToken = findToken(token, list);
      }
      if (foundToken != null)
      {
         if (foundToken.hasProperty(TokenProperties.SUB_TOKEN_LIST))
         {
            for (Token t : foundToken.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue())
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
   @SuppressWarnings("unchecked")
   private Token findToken(String token, List<Token> list)
   {

      if (token.contains("."))
      {
         String to[] = token.split("\\.");
         List<Token> tokens = list;
         Token t = null;
         for (String fqnPath : to)
         {
            t = findTokenInList(fqnPath, tokens);
            if (t != null && t.hasProperty(TokenProperties.SUB_TOKEN_LIST))
               tokens = (List<Token>)t.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue();
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
   @SuppressWarnings("unchecked")
   private void filterToken(int currentLine, List<Token> token)
   {
      Token lastFunction = null;

      for (Token t : token)
      {
         if (t.getName() == null)
            continue;

         if (t.getProperty(TokenProperties.LINE_NUMBER).isNumericProperty().numericValue().intValue() > currentLine)
         {
            break;
         }
         if (t.getType().equals(TokenType.FUNCTION))
         {
            lastFunction = t;
         }
         filteredToken.put(t.getName(), t);
      }

      if (lastFunction != null && lastFunction.hasProperty(TokenProperties.SUB_TOKEN_LIST))
      {
         filterSubToken(currentLine, (List<Token>)lastFunction.getProperty(TokenProperties.SUB_TOKEN_LIST)
            .isArrayProperty());

      }

   }

   /**
    * @param currentLine
    * @param subToken
    */
   @SuppressWarnings("unchecked")
   private void filterSubToken(int currentLine, List<Token> subToken)
   {
      for (Token t : subToken)
      {
         if (t.hasProperty(TokenProperties.SUB_TOKEN_LIST)
            && t.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue() != null)
         {
            filterSubToken(currentLine, (List<Token>)t.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty()
               .arrayValue());
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
   * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
   */
   @Override
   public int compare(Token t1, Token t2)
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
      if (t2.getType() == TokenType.VARIABLE)
      {
         return 1;
      }
      if (t1.getType() == TokenType.VARIABLE)
      {
         return -1;
      }

      return t1.getName().compareTo(t2.getName());
   }

   /**
    * @see com.google.gwt.resources.client.ResourceCallback#onError(com.google.gwt.resources.client.ResourceException)
    */
   @Override
   public void onError(ResourceException e)
   {
      e.printStackTrace();
   }

   /**
    * @see com.google.gwt.resources.client.ResourceCallback#onSuccess(com.google.gwt.resources.client.ResourcePrototype)
    */
   @Override
   public void onSuccess(TextResource resource)
   {
      JSONTokenParser parser = new JSONTokenParser();
      JSONObject obj = new JSONObject(parseJson(resource.getText()));
      tokensByFQN.clear();

      defaultTokens = parser.getTokens(obj.get("javascript_tokens").isArray());

      defaultTokens.addAll(parser.getTokens(obj.get("js_netvibes_tokens").isArray()));

      nVApiTokens = parser.getTokens(obj.get("netvibes_api_tokens").isArray());
      try
      {
         putClassTokensToMapByFQN(nVApiTokens);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      List<Token> jSApiTokens = parser.getTokens(obj.get("java_script_api").isArray());
      for (Token t : jSApiTokens)
      {
         if (tokensByFQN.containsKey(t.getName()))
         {
            Token nVt = tokensByFQN.get(t.getName());
            if (nVt.hasProperty(TokenProperties.SUB_TOKEN_LIST))
            {
               @SuppressWarnings("unchecked")
               List<Token> subTokens =
                  (List<Token>)nVt.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue();

               subTokens.addAll(t.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue());
            }
            else
               nVt.setProperty(TokenProperties.SUB_TOKEN_LIST,
                  new ArrayProperty(t.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue()));
         }
         else
         {
            tokensByFQN.put(t.getName(), t);
         }
      }

      autocompletion(lineContent, cursorPositionX, cursorPositionY, tokenFromParser, currentToken);

   }

}
