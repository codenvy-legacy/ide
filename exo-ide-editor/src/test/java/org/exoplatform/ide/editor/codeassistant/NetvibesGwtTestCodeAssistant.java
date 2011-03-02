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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenSelectedHandler;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidgetFactory;
import org.exoplatform.ide.editor.codeassistant.netvibes.NetvibesCodeAssistant;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: NetvibesGwtTestCodeAssistant Mar 2, 2011 2:30:56 PM evgen $
 *
 */
public class NetvibesGwtTestCodeAssistant extends Base
{
   public void testNVCodeAssistantFunction()
   {
      String line = "function(";
      paseLine(line, line, "", "", line.length() + 1);
   }

   public void testNVCodeAssistantSplit()
   {
      String line = "function{};";
      paseLine(line, line, "", "", line.length() + 1);
   }

   public void testNVCodeAssistantBegin()
   {
      String line = "function a() {";
      paseLine(line, line, "", "", line.length() + 1);
   }

   public void testNVCodeAssistantEnd()
   {
      String line = "some code; }";
      paseLine(line, line, "", "", line.length() + 1);
   }
   
   public void testNVCodeAssistantMiddle()
   {
      String line = "some name";
      paseLine(line, "some ", "na", "me", 8);
   }
   
   public void testNVCodeAssistantDot()
   {
      String line = "some name.split();";
      paseLine(line, "some name.", "sp", "lit();", 13);
   }
   
   /**
    * @param line
    */
   private void paseLine(final String line, final String before, final String token, final String after,
      final int curPos)
   {
      class NVAssist extends NetvibesCodeAssistant
      {
         /**
          * @see org.exoplatform.ide.editor.api.codeassitant.CodeAssistant#openForm(int, int, java.util.List, org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidgetFactory, org.exoplatform.ide.editor.api.codeassitant.ui.TokenSelectedHandler)
          */
         @Override
         protected void openForm(int x, int y, List<Token> tokens, TokenWidgetFactory factory,
            TokenSelectedHandler handler)
         {
            assertEquals(token, tokenToComplete);
            assertEquals(before, beforeToken);
            assertEquals(after, afterToken);
         }
      }

    new NVAssist().autocompleteCalled(null, "", 0, 0, line, curPos, 0, new ArrayList<Token>(), MimeType.TEXT_JAVASCRIPT, null);
   }
}
