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
package org.exoplatform.ide.editor.ruby.client;

import org.exoplatform.ide.editor.ruby.client.RubyEditorExtension;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * GWT JUnit <b>integration</b> tests must extend GWTTestCase. Using <code>"GwtTest*"</code> naming pattern exclude them from
 * running with surefire during the test phase.
 */
public class GwtTestRubyEditor extends GWTTestCase
{

   String content = "# Ruby Sample program\n\n" + "class HelloClass\n" + "  def sayHello\n"
      + "    puts( \"Hello, wolrd!\" )\n" + "  end\n" + "end\n\n" + "ob = HelloClass.new\n" + "ob.sayHello";

   /**
    * Must refer to a valid module that sources this class.
    */
   public String getModuleName()
   {
      return "org.exoplatform.ide.editor.ruby.client.RubyEditorJUnit";
   }

   public void testdefaultContent()
   {
      String content = RubyEditorExtension.DEFAULT_CONTENT.getSource().getText();
      assertNotNull(content);
      assertEquals(this.content, content);
   }

}
