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
package org.exoplatform.ide.editor.php.client;

import org.exoplatform.ide.editor.php.client.PhpEditorExtension;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * GWT JUnit <b>integration</b> tests must extend GWTTestCase. Using <code>"GwtTest*"</code> naming pattern exclude them from
 * running with surefire during the test phase.
 */
public class GwtTestPhpEditor extends GWTTestCase
{

   String content = "<? echo \"Hello\" ?>";

   /**
    * Must refer to a valid module that sources this class.
    */
   public String getModuleName()
   {
      return "org.exoplatform.ide.editor.php.client.PhpEditorJUnit";
   }

   public void testdefaultContent()
   {
      String content = PhpEditorExtension.DEFAULT_CONTENT.getSource().getText();
      assertNotNull(content);
      assertEquals(this.content, content);
   }

}
