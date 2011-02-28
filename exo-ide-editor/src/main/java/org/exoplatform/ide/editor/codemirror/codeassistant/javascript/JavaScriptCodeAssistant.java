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
package org.exoplatform.ide.editor.codemirror.codeassistant.javascript;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.codeassitant.CodeAssistant;
import org.exoplatform.ide.editor.api.codeassitant.CodeError;
import org.exoplatform.ide.editor.api.codeassitant.NumericProperty;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperties;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperty;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.codemirror.codeassistant.util.JSONTokenParser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ExternalTextResource;
import com.google.gwt.resources.client.ResourceCallback;
import com.google.gwt.resources.client.ResourceException;
import com.google.gwt.resources.client.TextResource;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: JavaScriptCodeAssistant Feb 24, 2011 11:26:32 AM evgen $
 *
 */
public class JavaScriptCodeAssistant extends CodeAssistant implements Comparator<Token>
{

   public interface JavaScriptBuandle extends ClientBundle
   {
      @Source("org/exoplatform/ide/editor/public/tokens/java_script_api.js")
      ExternalTextResource jsApiTokens();
   }

   private static List<Token> defaultTokens;

   private static Map<String, Token> tokensByFQN = new HashMap<String, Token>();

   /**
    * @param eventBus
    */
   public JavaScriptCodeAssistant(HandlerManager eventBus)
   {
      super(eventBus);
   }

   //   private native JavaScriptObject getTokens() /*-{
   //		return $wnd.javascript_tokens;
   //   }-*/;
   //
   //   private native JavaScriptObject getJavaScriptApiTokens() /*-{
   //		return $wnd.java_script_api;
   //   }-*/;
   //
   //   private native JavaScriptObject getJavaScriptGlobalTokens() /*-{
   //		return $wnd.java_script_grobal_var;
   //   }-*/;

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
   public void autocompleteCalled(Editor editor, String mimeType, final int cursorOffsetX, final int cursorOffsetY,
      final String lineContent, final int cursorPositionX, final int cursorPositionY, final List<Token> tokenList,
      String lineMimeType, final Token currentToken)
   {
      this.editor = editor;
      try
      {
         printTokens(tokenList, 0);
         parseTokenLine(lineContent, cursorPositionX);

         if (defaultTokens == null)
         {
            JavaScriptBuandle buandle = GWT.create(JavaScriptBuandle.class);
            buandle.jsApiTokens().getText(new ResourceCallback<TextResource>()
            {

               @Override
               public void onSuccess(TextResource resource)
               {
                  JSONObject obj = new JSONObject(parseJson(resource.getText()));
                  JSONTokenParser parser = new JSONTokenParser();

                  defaultTokens = parser.getTokens(obj.get("javascript_tokens").isArray());

                  List<Token> jsApiTokens = parser.getTokens(obj.get("java_script_grobal_var").isArray());
                  defaultTokens.addAll(jsApiTokens);

                  jsApiTokens.addAll(parser.getTokens(obj.get("java_script_api").isArray()));

                  for (Token t : jsApiTokens)
                  {
                     tokensByFQN.put(t.getName().toLowerCase(), t);
                  }
                  autocompletion(cursorOffsetX, cursorOffsetY, cursorPositionY, tokenList, currentToken);
               }

               @Override
               public void onError(ResourceException e)
               {
                  e.printStackTrace();
               }
            });

            return;
         }

         autocompletion(cursorOffsetX, cursorOffsetY, cursorPositionY, tokenList, currentToken);
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
   private void autocompletion(int cursorOffsetX, int cursorOffsetY, int lineNum, List<Token> tokenList,
      Token currentToken)
   {

      List<Token> tokens = new ArrayList<Token>();

      if (beforeToken.endsWith("."))
      {
         String fqn = beforeToken.substring(0, beforeToken.length());
         String[] posFQN = fqn.split("[/();{}|&\",'\\\\ \\+\\-=\\*\\.]+");
         if (posFQN.length > 0)
         {
            Token clazz = tokensByFQN.get(posFQN[posFQN.length - 1]);

            if (clazz != null && clazz.hasProperty(TokenProperties.CHILD_TOKEN_LIST))
            {
               tokens.addAll(clazz.getProperty(TokenProperties.CHILD_TOKEN_LIST).isArrayProperty().arrayValue());
            }
         }
      }
      else
      {
         tokens.addAll(defaultTokens);
         List<Token> tokenFromParser = getTokenJavaScript(tokenList);
         tokens.addAll(getTokensForLine(lineNum, tokenFromParser));
      }
      Collections.sort(tokens, this);
      openForm(cursorOffsetX, cursorOffsetY, tokens, new JavaScriptTokenWidgetFactory(), this);
   }

   /**
    * @param lineNum
    * @param tokenFromParser
    * @return
    */
   @SuppressWarnings("unchecked")
   private List<Token> getTokensForLine(int lineNum, List<Token> tokenFromParser)
   {
      List<Token> tokens = new ArrayList<Token>();

      Token tok = null;
      for (Token t : tokenFromParser)
      {
         tokens.add(t);
         NumericProperty s = t.getProperty(TokenProperties.LINE_NUMBER).isNumericProperty();
         TokenProperty f = t.getProperty(TokenProperties.LAST_LINE_NUMBER);
         if (f != null && f.isNumericProperty() != null && s.numberValue().intValue() < lineNum
            && f.isNumericProperty().numberValue().intValue() > lineNum)
         {
            tok = t;
         }

      }
      if (tok != null && tok.getType() == TokenType.FUNCTION)
      {
         tokens.addAll(getTokensInFunction(lineNum, tok));
      }
      return tokens;
   }

   /**
    * @param lineNum
    * @param tok
    * @return
    */
   private List<Token> getTokensInFunction(int lineNum, Token tok)
   {
      List<Token> tokens = new ArrayList<Token>();
      if(tok.hasProperty(TokenProperties.SUB_TOKEN_LIST))
      {
         for(Token t : tok.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue())
         {
            NumericProperty s = t.getProperty(TokenProperties.LINE_NUMBER).isNumericProperty();
            if(s.numberValue().intValue() < lineNum)
            {
               tokens.add(t);
               if(t.getType() == TokenType.FUNCTION)
               {
                  tokens.addAll(getTokensInFunction(lineNum, t));
               }               
            }
         }
      }
      
      return tokens;
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
   @SuppressWarnings("unchecked")
   private List<Token> getTokenJavaScript(List<Token> tokenFromParser)
   {
      List<Token> tokens = new ArrayList<Token>();

      String tagName = "script";

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
            tokens.addAll(token.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue());
         }
         else if (token.hasProperty(TokenProperties.SUB_TOKEN_LIST)
            && token.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue() != null)
         {
            tokens.addAll(getTokenJavaScript((List<Token>)token.getProperty(TokenProperties.SUB_TOKEN_LIST)
               .isArrayProperty().arrayValue()));
         }
      }

      return tokens;
   }

   private void printTokens(List<? extends Token> tokens, int i)
   {
      String spacer = "";
      for (int j = 0; j < i; j++)
      {
         spacer += " ";
      }
      i++;
      for (Token t : tokens)
      {
         System.out.println(spacer + t.getName() + " " + t.getType());
         TokenProperty p = t.getProperty(TokenProperties.LAST_LINE_NUMBER);
         if (p != null && p.isNumericProperty() != null)
            System.out.println(spacer + p.isNumericProperty().numberValue());
         if (t.hasProperty(TokenProperties.SUB_TOKEN_LIST)
            && t.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue() != null)
         {
            printTokens(t.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue(), i);
         }
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

      if (t2.getType() == TokenType.FUNCTION)
      {
         return 1;
      }
      if (t1.getType() == TokenType.FUNCTION)
      {
         return -1;
      }

      if (t2.getType() == TokenType.CLASS)
      {
         return 1;
      }
      if (t1.getType() == TokenType.CLASS)
      {
         return -1;
      }

      //
      //      /*
      //       * If first and second template is not keyword
      //       * check, if one of them is template.
      //       * Template must be less, that all other types, except keyword.
      //       */
      //      if (t1.getType() == TokenType.TEMPLATE)
      //      {
      //         return 1;
      //      }
      //      if (t2.getType() == TokenType.TEMPLATE)
      //      {
      //         return -1;
      //      }
      //      
      //      /*
      //       * At the end of list must be keywords.
      //       */
      //      if (t1.getType() == TokenType.KEYWORD)
      //      {
      //         return 1;
      //      }
      //
      //      if (t2.getType() == TokenType.KEYWORD)
      //      {
      //         return -1;
      //      }

      return t1.getName().compareTo(t2.getName());
   }
}
