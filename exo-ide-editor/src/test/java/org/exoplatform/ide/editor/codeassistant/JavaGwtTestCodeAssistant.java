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
import org.exoplatform.ide.editor.codeassistant.java.JavaCodeAssistant;
import org.exoplatform.ide.editor.codeassistant.java.JavaCodeAssistantErrorHandler;
import org.exoplatform.ide.editor.codeassistant.java.JavaTokenWidgetFactory;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: JavaGwtTestCodeAssistant Mar 4, 2011 4:57:07 PM evgen $
 *
 */
public class JavaGwtTestCodeAssistant extends Base
{

   public void testJavaParseLine()
   {
      String line = "java";
      paseLine(line, "", line, "", line.length() +1);
   }
   
   public void testJavaParseLineSpace()
   {
      String line = "java ";
      paseLine(line, line,"", "", line.length() +1);
   }
   
   
   public void testJavaParseLineWithEnd()
   {
      String line = "groovy";
      paseLine(line, "", "groo", "vy", 5);
   }
   
   private void paseLine(final String line, final String before, final String token, final String after,
      final int curPos)
   {
      class JavaCodeAssist extends JavaCodeAssistant
      {
         
         /**
          * 
          */
         public JavaCodeAssist()
         {
            super(new JavaTokenWidgetFactory(""), new JavaCodeAssistantErrorHandler()
            {
               
               @Override
               public void handleError(Throwable exception)
               {
                  fail();
               }
            });
         }
         @Override
         protected void openForm(List<Token> tokens, TokenWidgetFactory factory,
            TokenSelectedHandler handler)
         {
            assertEquals(before, beforeToken);
            assertEquals(token, tokenToComplete);
            assertEquals(after, afterToken);
         }
      }

      new JavaCodeAssist().autocompleteCalled(null, "", 0, 0, line, curPos, 0,
         new ArrayList<Token>(), MimeType.APPLICATION_GROOVY, null);
   }
}
