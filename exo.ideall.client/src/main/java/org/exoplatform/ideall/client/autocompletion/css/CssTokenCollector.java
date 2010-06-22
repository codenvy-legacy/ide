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
package org.exoplatform.ideall.client.autocompletion.css;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.editor.api.Token;
import org.exoplatform.gwtframework.editor.api.Token.TokenType;
import org.exoplatform.ideall.client.autocompletion.TokenCollector;
import org.exoplatform.ideall.client.autocompletion.TokensCollectedCallback;
import org.exoplatform.ideall.client.model.ApplicationContext;

import com.google.gwt.event.shared.HandlerManager;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class CssTokenCollector implements TokenCollector
{

   private static List<Token> cssProperty = new ArrayList<Token>();

   static
   {
      cssProperty.add(new Token("!important", TokenType.PROPERTY, "", "", null));
      cssProperty.add(new Token("@charset", TokenType.PROPERTY, "", "", null));
      cssProperty.add(new Token("@import", TokenType.PROPERTY, "", "", null));
      cssProperty.add(new Token("@font-face", TokenType.PROPERTY, "", "", null));
      cssProperty.add(new Token("@media", TokenType.PROPERTY, "", "", null));
      cssProperty.add(new Token("@page", TokenType.PROPERTY, "", "", null));
      cssProperty.add(new Token("background", TokenType.PROPERTY, "", "",
         "Sets all the background properties in one declaration"));
      cssProperty.add(new Token("background-attachment", TokenType.PROPERTY, "", "",
         "Sets whether a background image is fixed or scrolls with the rest of the page"));
      cssProperty.add(new Token("background-color", TokenType.PROPERTY, "", "",
         "Sets the background color of an element"));
      cssProperty.add(new Token("background-image", TokenType.PROPERTY, "", "",
         "Sets the background image for an element"));
      cssProperty.add(new Token("background-position", TokenType.PROPERTY, "", "",
         "Sets the starting position of a background image"));
      cssProperty.add(new Token("background-repeat", TokenType.PROPERTY, "", "",
         "Sets how a background image will be repeated"));
      cssProperty.add(new Token("border", TokenType.PROPERTY, "", "",
         "Sets all the border properties in one declaration"));
      cssProperty.add(new Token("border-bottom", TokenType.PROPERTY, "", "",
         "Sets all the bottom border properties in one declaration"));
      cssProperty.add(new Token("border-bottom-color", TokenType.PROPERTY, "", "",
         "Sets the color of the bottom border"));
      cssProperty.add(new Token("border-bottom-style", TokenType.PROPERTY, "", "",
         "Sets the style of the bottom border"));
      cssProperty.add(new Token("border-bottom-width", TokenType.PROPERTY, "", "",
         "Sets the width of the bottom border"));
      cssProperty.add(new Token("border-color", TokenType.PROPERTY, "", "", "Sets the color of the four borders"));
      cssProperty.add(new Token("border-collapse", TokenType.PROPERTY, "", "",
         "Specifies whether or not table borders should be collapsed"));
      cssProperty.add(new Token("border-left", TokenType.PROPERTY, "", "",
         "Sets all the left border properties in one declaration"));
      cssProperty.add(new Token("border-left-color", TokenType.PROPERTY, "", "", "Sets the color of the left border"));
      cssProperty.add(new Token("border-left-style", TokenType.PROPERTY, "", "", "Sets the style of the left border"));
      cssProperty.add(new Token("border-left-width", TokenType.PROPERTY, "", "", "Sets the width of the left border"));
      cssProperty.add(new Token("border-right", TokenType.PROPERTY, "", "",
         "Sets all the right border properties in one declaration"));
      cssProperty
         .add(new Token("border-right-color", TokenType.PROPERTY, "", "", "Sets the color of the right border"));
      cssProperty
         .add(new Token("border-right-style", TokenType.PROPERTY, "", "", "Sets the style of the right border"));
      cssProperty
         .add(new Token("border-right-width", TokenType.PROPERTY, "", "", "Sets the width of the right border"));
      cssProperty.add(new Token("border-spacing", TokenType.PROPERTY, "", "",
         "Specifies the distance between the borders of adjacent cells"));
      cssProperty.add(new Token("border-style", TokenType.PROPERTY, "", "", "Sets the style of the four borders"));
      cssProperty.add(new Token("border-top", TokenType.PROPERTY, "", "",
         "Sets all the top border properties in one declaration"));
      cssProperty.add(new Token("border-top-color", TokenType.PROPERTY, "", "", "Sets the color of the top border"));
      cssProperty.add(new Token("border-top-style", TokenType.PROPERTY, "", "", "Sets the style of the top border"));
      cssProperty.add(new Token("border-top-width", TokenType.PROPERTY, "", "", "Sets the width of the top border"));
      cssProperty.add(new Token("border-width", TokenType.PROPERTY, "", "", "Sets the width of the four borders"));
      cssProperty.add(new Token("bottom", TokenType.PROPERTY, "", "",
         "Sets the bottom margin edge for a positioned box"));
      cssProperty.add(new Token("caption-side", TokenType.PROPERTY, "", "",
         "Specifies the placement of a table caption"));
      cssProperty.add(new Token("clear", TokenType.PROPERTY, "", "",
         "Specifies which sides of an element where other floating elements are not allowed"));
      cssProperty.add(new Token("clip", TokenType.PROPERTY, "", "", "Clips an absolutely positioned element"));
      cssProperty.add(new Token("color", TokenType.PROPERTY, "", "", "Sets the color of text"));
      cssProperty.add(new Token("content", TokenType.PROPERTY, "", "",
         "Used with the :before and :after pseudo-elements, to insert generated content"));
      cssProperty.add(new Token("counter-increment", TokenType.PROPERTY, "", "", "Increments one or more counters"));
      cssProperty.add(new Token("counter-reset", TokenType.PROPERTY, "", "", "Creates or resets one or more counters"));
      cssProperty.add(new Token("cursor", TokenType.PROPERTY, "", "", "Specifies the type of cursor to be displayed"));
      cssProperty.add(new Token("direction", TokenType.PROPERTY, "", "",
         "Specifies the text direction/writing direction"));
      cssProperty.add(new Token("display", TokenType.PROPERTY, "", "",
         "Specifies the type of box an element should generate"));
      cssProperty.add(new Token("empty-cells", TokenType.PROPERTY, "", "",
         "Specifies whether or not to display borders and background on empty cells in a table"));
      cssProperty.add(new Token("float", TokenType.PROPERTY, "", "", "Specifies whether or not a box should float"));
      cssProperty.add(new Token("font", TokenType.PROPERTY, "", "", "Sets all the font properties in one declaration"));
      cssProperty.add(new Token("font-family", TokenType.PROPERTY, "", "", "Specifies the font family for text"));
      cssProperty.add(new Token("font-size", TokenType.PROPERTY, "", "", "Specifies the font size of text"));
      cssProperty.add(new Token("font-style", TokenType.PROPERTY, "", "", "Specifies the font style for text"));
      cssProperty.add(new Token("font-variant", TokenType.PROPERTY, "", "",
         "Specifies whether or not a text should be displayed in a small-caps font"));
      cssProperty.add(new Token("font-weight", TokenType.PROPERTY, "", "", "Specifies the weight of a font"));
      cssProperty.add(new Token("height", TokenType.PROPERTY, "", "", "Sets the height of an element"));
      cssProperty.add(new Token("left", TokenType.PROPERTY, "", "", "Sets the left margin edge for a positioned box"));
      cssProperty.add(new Token("letter-spacing", TokenType.PROPERTY, "", "",
         "Increase or decrease the space between characters in a text"));
      cssProperty.add(new Token("line-height", TokenType.PROPERTY, "", "", "Sets the line height"));
      cssProperty.add(new Token("list-style", TokenType.PROPERTY, "", "",
         "Sets all the properties for a list in one declaration"));
      cssProperty.add(new Token("list-style-image", TokenType.PROPERTY, "", "",
         "Specifies an image as the list-item marker"));
      cssProperty.add(new Token("list-style-position", TokenType.PROPERTY, "", "",
         "Specifies if the list-item markers should appear inside or outside the content flow"));
      cssProperty
         .add(new Token("list-style-type", TokenType.PROPERTY, "", "", "Specifies the type of list-item marker"));
      cssProperty.add(new Token("margin", TokenType.PROPERTY, "", "",
         "Sets all the margin properties in one declaration"));
      cssProperty.add(new Token("margin-bottom", TokenType.PROPERTY, "", "", "Sets the bottom margin of an element"));
      cssProperty.add(new Token("margin-left", TokenType.PROPERTY, "", "", "Sets the left margin of an element"));
      cssProperty.add(new Token("margin-right", TokenType.PROPERTY, "", "", "Sets the right margin of an element"));
      cssProperty.add(new Token("margin-top", TokenType.PROPERTY, "", "", "Sets the top margin of an element"));
      cssProperty.add(new Token("max-height", TokenType.PROPERTY, "", "", "Sets the maximum height of an element"));
      cssProperty.add(new Token("max-width", TokenType.PROPERTY, "", "", "Sets the maximum width of an element"));
      cssProperty.add(new Token("min-height", TokenType.PROPERTY, "", "", "Sets the minimum height of an element"));
      cssProperty.add(new Token("min-width", TokenType.PROPERTY, "", "", "Sets the minimum width of an element"));
      cssProperty
         .add(new Token("orphans", TokenType.PROPERTY, "", "",
            "Sets the minimum number of lines that must be left at the bottom of a page when a page break occurs inside an element"));
      cssProperty.add(new Token("outline", TokenType.PROPERTY, "", "",
         "Sets all the outline properties in one declaration"));
      cssProperty.add(new Token("outline-color", TokenType.PROPERTY, "", "", "Sets the color of an outline"));
      cssProperty.add(new Token("outline-style", TokenType.PROPERTY, "", "", "Sets the style of an outline"));
      cssProperty.add(new Token("outline-width", TokenType.PROPERTY, "", "", "Sets the width of an outline"));
      cssProperty.add(new Token("overflow", TokenType.PROPERTY, "", "",
         "Specifies what happens if content overflows an element's box"));
      cssProperty.add(new Token("padding", TokenType.PROPERTY, "", "",
         "Sets all the padding properties in one declaration"));
      cssProperty.add(new Token("padding-bottom", TokenType.PROPERTY, "", "", "Sets the bottom padding of an element"));
      cssProperty.add(new Token("padding-left", TokenType.PROPERTY, "", "", "Sets the left padding of an element"));
      cssProperty.add(new Token("padding-right", TokenType.PROPERTY, "", "", "Sets the right padding of an element"));
      cssProperty.add(new Token("padding-top", TokenType.PROPERTY, "", "", "Sets the top padding of an element"));
      cssProperty.add(new Token("page-break-after", TokenType.PROPERTY, "", "",
         "Sets the page-breaking behavior after an element"));
      cssProperty.add(new Token("page-break-before", TokenType.PROPERTY, "", "",
         "Sets the page-breaking behavior before an element"));
      cssProperty.add(new Token("page-break-inside", TokenType.PROPERTY, "", "",
         "Sets the page-breaking behavior inside an element"));
      cssProperty.add(new Token("position Specifies", TokenType.PROPERTY, "", "",
         "the type of positioning for an element"));
      cssProperty.add(new Token("quotes", TokenType.PROPERTY, "", "",
         "Sets the type of quotation marks for embedded quotations"));
      cssProperty
         .add(new Token("right", TokenType.PROPERTY, "", "", "Sets the right margin edge for a positioned box"));
      cssProperty.add(new Token("table-layout", TokenType.PROPERTY, "", "",
         "Sets the layout algorithm to be used for a table"));
      cssProperty
         .add(new Token("text-align", TokenType.PROPERTY, "", "", "Specifies the horizontal alignment of text"));
      cssProperty
         .add(new Token("text-decoration", TokenType.PROPERTY, "", "", "Specifies the decoration added to text"));
      cssProperty.add(new Token("text-indent", TokenType.PROPERTY, "", "",
         "Specifies the indentation of the first line in a text-block"));
      cssProperty
         .add(new Token("text-shadow", TokenType.PROPERTY, "", "", "Specifies the shadow effect added to text"));
      cssProperty.add(new Token("text-transform", TokenType.PROPERTY, "", "", "Controls the capitalization of text"));
      cssProperty.add(new Token("top", TokenType.PROPERTY, "", "", "Sets the top margin edge for a positioned box"));
      cssProperty.add(new Token("unicode-bidi", TokenType.PROPERTY, "", "", null));
      cssProperty.add(new Token("vertical-align", TokenType.PROPERTY, "", "",
         "Sets the vertical alignment of an element"));
      cssProperty.add(new Token("visibility", TokenType.PROPERTY, "", "",
         "Specifies whether or not an element is visible"));
      cssProperty.add(new Token("white-space", TokenType.PROPERTY, "", "",
         "Specifies how white-space inside an element is handled"));
      cssProperty
         .add(new Token("widows", TokenType.PROPERTY, "", "",
            "Sets the minimum number of lines that must be left at the top of a page when a page break occurs inside an element"));
      cssProperty.add(new Token("width", TokenType.PROPERTY, "", "", "Sets the width of an element"));
      cssProperty.add(new Token("word-spacing", TokenType.PROPERTY, "", "",
         "Increases or decreases the space between words in a text"));
      cssProperty.add(new Token("z-index", TokenType.PROPERTY, "", "", "Sets the stack order of an element"));
   }

   private ApplicationContext context;

   private HandlerManager eventBus;

   private TokensCollectedCallback tokensCollectedCallback;

   private String afterToken;

   private String tokenToComplete;

   private String beforeToken;

   public CssTokenCollector(HandlerManager eventBus, ApplicationContext context,
      TokensCollectedCallback tokensCollectedCallback)
   {
      this.context = context;
      this.eventBus = eventBus;
      this.tokensCollectedCallback = tokensCollectedCallback;

   }

   /**
    * @see org.exoplatform.ideall.client.autocompletion.TokenCollector#getTokens(java.lang.String, int, int, java.util.List)
    */
   public void getTokens(String line, int lineNum, int cursorPos, List<Token> tokenFromParser)
   {
      List<Token> tokens = new ArrayList<Token>();
      tokens.addAll(cssProperty);
      
      parseTokenLine(line, cursorPos);
      
      tokensCollectedCallback.onTokensCollected(tokens, beforeToken,tokenToComplete, afterToken);
   }
   
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
