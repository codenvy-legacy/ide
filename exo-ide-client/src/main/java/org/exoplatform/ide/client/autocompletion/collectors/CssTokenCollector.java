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
import org.exoplatform.ide.client.autocompletion.JSONTokenParser;
import org.exoplatform.ide.client.framework.codeassistant.TokenCollector;
import org.exoplatform.ide.client.framework.codeassistant.TokensCollectedCallback;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.json.client.JSONArray;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class CssTokenCollector implements TokenCollector<Token>
{

   private static List<Token> cssProperty;

   private String afterToken;

   private String tokenToComplete;

   private String beforeToken;

   public CssTokenCollector(HandlerManager eventBus)
   {
   }

   /**
    * @see org.exoplatform.ide.client.framework.codeassistant.TokenCollector#getTokens(java.lang.String, int, int, java.util.List)
    */
   public void getTokens(String line, String lineMimeType, int lineNum, int cursorPos, List<Token> tokenFromParser, TokensCollectedCallback<Token> tokensCollectedCallback)
   {
      List<Token> tokens = new ArrayList<Token>();
      if(cssProperty == null)
      {
         JSONTokenParser parser = new JSONTokenParser();
         JSONArray tokenArray = new JSONArray(getTokens());
         cssProperty = parser.getTokens(tokenArray);
         
      }
      tokens.addAll(cssProperty);
      
      parseTokenLine(line, cursorPos);
      
      tokensCollectedCallback.onTokensCollected(tokens, beforeToken,tokenToComplete, afterToken);
   }
   private native JavaScriptObject getTokens() /*-{
      return $wnd.css_tokens;
   }-*/;
   
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

}
