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
package org.exoplatform.ide.editor.codeassistant;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenSelectedHandler;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidgetFactory;
import org.exoplatform.ide.editor.codeassistant.html.HtmlCodeAssistant;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: GwtTestHtmlCodeAssistant Feb 25, 2011 10:58:45 AM evgen $
 *
 */
public class HtmlGwtTestCodeAssistant extends Base
{
  
   /**
    * 
    */
   private static final int TagNum = 82;

   /**
    * @see com.google.gwt.junit.client.GWTTestCase#gwtSetUp()
    */
   @Override
   protected void gwtSetUp() throws Exception
   {
      super.gwtSetUp();
   }
   
   public void testHtmlParseLine()
   {
      paseLine("<bod", "<", "bod", "", 5, TagNum);
   }
   
   public void testHtmlParseLineWithAttribute()
   {
      paseLine("<body >", "<body ", "", ">", 7, 18);
   }
   
   public void testHtmlParseLineWithAndTag()
   {
      paseLine("<body> ", "<body> ", "", "", 8, TagNum);
   }
   
   /**
    * @param line
    */
   private void paseLine(final String line, final String before, final String token, final String after,
      final int curPos, final int numToken)
   {
      class HtmlCodeAssist extends HtmlCodeAssistant
      {

         @Override
         protected void openForm(int x, int y, List<Token> tokens, TokenWidgetFactory factory,
            TokenSelectedHandler handler)
         {
            assertEquals(token, tokenToComplete);
            assertEquals(before, beforeToken);
            assertEquals(after, afterToken);
            assertEquals(numToken, tokens.size());
         }
      }

      new HtmlCodeAssist().autocompleteCalled(null, "", 0, 0, line, curPos, 0,
         new ArrayList<Token>(), "", null);
   }
}
