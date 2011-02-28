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
package org.exoplatform.ide.editor.codemirror;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenSelectedHandler;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidgetFactory;
import org.exoplatform.ide.editor.codemirror.codeassistant.css.CssCodeAssistant;

import com.google.gwt.event.shared.HandlerManager;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: GwtTestCssCodeAssistant Feb 25, 2011 10:06:40 AM evgen $
 *
 */
public class CssGwtTestCodeAssistant extends Base
{

   public void testCssSimpleParse()
   {
      String line = "asd as bor";
      paseLine(line, "asd as ", "bor", "", line.length() + 1);
   }

   public void testCssParseLineWithEndProperty()
   {
      System.out
         .println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
      final String line = "border-left: 1px;";
      paseLine(line, line, "", "", line.length() + 1);

   }

   public void testCssParseLineWithBeginClass()
   {
      final String line = ".button-clolapse{";
      paseLine(line, line, "", "", line.length() + 1);
   }

   public void testCssParseLineWithEndClass()
   {
      final String line = "border:1px #ddffaa;}";
      paseLine(line, line, "", "", line.length() + 1);
   }

   public void testCssParsePropertyName()
   {
      final String line = "border-bottom-color";
      paseLine(line, "", "border-", "bottom-color", 8);
   }

   /**
    * @param line
    */
   private void paseLine(final String line, final String before, final String token, final String after,
      final int curPos)
   {
      class CssAssistant extends CssCodeAssistant
      {

         @Override
         protected void openForm(int x, int y, List<Token> tokens, TokenWidgetFactory factory,
            TokenSelectedHandler handler)
         {
            assertEquals(token, tokenToComplete);
            assertEquals(before, beforeToken);
            assertEquals(after, afterToken);
            assertEquals(102, tokens.size());
         }
      }

      new CssAssistant().autocompleteCalled(null, "", 0, 0, line, curPos, 0,
         new ArrayList<Token>(), "", null);
   }

}
