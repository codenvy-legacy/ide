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

import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.codeassistant.javascript.JavaScriptCodeAssistant;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: JavaScriptGwtTestCodeAssistant Mar 1, 2011 9:53:05 AM evgen $
 *
 */
public class JavaScriptGwtTestCodeAssistant extends Base
{

   private JSAssistant assist = new JSAssistant();

   public void testJSCodeAssistantFunction()
   {
      String line = "function(";
      paseLine(line, line, "", "", line.length() + 1);
   }

   public void testJSCodeAssistantSplit()
   {
      String line = "function{};";
      paseLine(line, line, "", "", line.length() + 1);
   }

   public void testJSCodeAssistantBegin()
   {
      String line = "function a() {";
      paseLine(line, line, "", "", line.length() + 1);
   }

   public void testJSCodeAssistantEnd()
   {
      String line = "some code; }";
      paseLine(line, line, "", "", line.length() + 1);
   }
   
   public void testJSCodeAssistantMiddle()
   {
      String line = "some name";
      paseLine(line, "some ", "na", "me", 8);
   }
   
   public void testJSCodeAssistantDot()
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
     assist.autocompleteCalled(null, 0, 0, new ArrayList<Token>(), "", null);
     assertEquals(before, assist.getBeforeToken());
     assertEquals(token, assist.getToken());
     assertEquals(after, assist.getAfterToken());
   }
   
   private class JSAssistant extends JavaScriptCodeAssistant
   {
            
      public String getBeforeToken()
      {
         return beforeToken;
      }
      
      public String getToken()
      {
         return tokenToComplete;
      }
      
      public String getAfterToken()
      {
         return afterToken;
      }
   }
   
   
}
