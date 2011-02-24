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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.codeassitant.CodeAssistant;
import org.exoplatform.ide.editor.api.codeassitant.CodeError;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperties;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.codemirror.codeassistant.util.JSONTokenParser;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.json.client.JSONArray;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: JavaScriptCodeAssistant Feb 24, 2011 11:26:32 AM evgen $
 *
 */
public class JavaScriptCodeAssistant extends CodeAssistant implements Comparator<Token>
{

   private static List<Token> defaultTokens;

   private static Map<String, Token> tokensByFQN = new HashMap<String, Token>();

   /**
    * @param eventBus
    */
   public JavaScriptCodeAssistant(HandlerManager eventBus)
   {
      super(eventBus);
   }

   private native JavaScriptObject getTokens() /*-{
		return $wnd.javascript_tokens;
   }-*/;

   private native JavaScriptObject getJavaScriptApiTokens() /*-{
		return $wnd.java_script_api;
   }-*/;

   private native JavaScriptObject getJavaScriptGlobalTokens() /*-{
		return $wnd.java_script_grobal_var;
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
      this.editor = editor;
      try
      {
         if (defaultTokens == null)
         {
            JSONTokenParser parser = new JSONTokenParser();
            JSONArray tokenArray = new JSONArray(getTokens());

            defaultTokens = parser.getTokens(tokenArray);
            List<Token> jsApiTokens = parser.getTokens(new JSONArray(getJavaScriptGlobalTokens()));
            defaultTokens.addAll(jsApiTokens);

            jsApiTokens.addAll(parser.getTokens(new JSONArray(getJavaScriptApiTokens())));

            for (Token t : jsApiTokens)
            {
               tokensByFQN.put(t.getName().toLowerCase(), t);
               System.out.println(t.getName());
            }
         }

         printTokens(tokenList, 0);

         parseTokenLine(lineContent, cursorPositionX);
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
         }
         Collections.sort(tokens, this);
         openForm(cursorOffsetX, cursorOffsetY, tokens, new JavaScriptTokenWidgetFactory(), this);
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

   private void printTokens(List<? extends Token> tokens, int i)
   {
      String spacer = "";
      for (int j = 0; j < i; j++)
      {
         spacer += " ";
      }
      for (Token t : tokens)
      {
         System.out.println(spacer + t.getName() + " " + t.getType());
         for (String s : t.getPropertiesNames())
         {
            System.out.println("== " + s);
         }
         if (t.hasProperty(TokenProperties.SUB_TOKEN_LIST)
            && t.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue() != null)
         {
            System.out.println();
            printTokens(t.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue(), i++);
            i--;
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

      /*
       * At the end of list must be keywords.
       */
      if (t1.getType() == TokenType.KEYWORD)
      {
         return 1;
      }

      if (t2.getType() == TokenType.KEYWORD)
      {
         return -1;
      }

      /*
       * If first and second template is not keyword
       * check, if one of them is template.
       * Template must be less, that all other types, except keyword.
       */
      if (t1.getType() == TokenType.TEMPLATE)
      {
         return 1;
      }
      if (t2.getType() == TokenType.TEMPLATE)
      {
         return -1;
      }

      return t1.getName().compareTo(t2.getName());
   }
}
