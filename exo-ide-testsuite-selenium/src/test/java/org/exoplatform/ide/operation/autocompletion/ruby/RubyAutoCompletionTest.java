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
package org.exoplatform.ide.operation.autocompletion.ruby;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: RubyAutoCompletionTest May 11, 2011 11:52:33 AM evgen $
 * 
 */
public class RubyAutoCompletionTest extends BaseTest
{
   private static final String PROJECT = RubyAutoCompletionTest.class.getSimpleName();

   private static final String FILE_NAME = "RubyCodeAssistantTest.rb";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         Link link = project.get(Link.REL_CREATE_FILE);
         VirtualFileSystemUtils.createFileFromLocal(link, FILE_NAME, MimeType.APPLICATION_RUBY,
            "src/test/resources/org/exoplatform/ide/operation/file/autocomplete/ruby/rubyAutocompletion.rb");
      }
      catch (Exception e)
      {
         fail("Can't create test folder");
      }
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (Exception e)
      {
      }
   }

   @Test
   public void testRubyAutocompletion() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);
      IDE.LOADER.waitClosed();

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile();

      IDE.GOTOLINE.goToLine(26);

      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("h");
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("w");
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("@i");
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("@@ins");
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("$cl");

      IDE.CODEASSISTANT.typeToInput("@@");
      IDE.CODEASSISTANT.typeToInput("\n");

      IDE.EDITOR.typeTextIntoEditor(".");
      // Pause is necessary for parsing tokens by CodeMirror
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.waitForElementInCodeAssistant("prec_f()");
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("between?(arg1, arg2, arg3)");
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("abs()");
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("next()");

      IDE.CODEASSISTANT.typeToInput("ro");
      IDE.CODEASSISTANT.typeToInput("\n");

      assertTrue(IDE.EDITOR.getTextFromCodeEditor().contains("@@ins.round()"));

      IDE.GOTOLINE.goToLine(32);

      IDE.EDITOR.typeTextIntoEditor("M");
      // this method fix problem of returning cursor in codeeditor before
      // character "M"
      IDE.EDITOR.typeTextIntoEditor(Keys.END.toString());
      // Pause is necessary for parsing tokens by CodeMirror
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.waitForElementInCodeAssistant("MDA");
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("MyClass");
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("Method");
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("Math");

      IDE.CODEASSISTANT.typeToInput("\n");
      IDE.EDITOR.typeTextIntoEditor(".");
      // Pause is necessary for parsing tokens by CodeMirror
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.waitForElementInCodeAssistant("finite?()");

      IDE.CODEASSISTANT.typeToInput("inf");
      IDE.CODEASSISTANT.typeToInput("\n");
      assertTrue(IDE.EDITOR.getTextFromCodeEditor().contains("MDA.infinite?()"));

      IDE.GOTOLINE.goToLine(33);

      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("g");
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("num");
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("$cl");

      IDE.CODEASSISTANT.typeToInput("\n");
      IDE.EDITOR.typeTextIntoEditor(".");
      // Pause is necessary for parsing tokens by CodeMirror
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.waitForElementInCodeAssistant("get");
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("set");
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("hello");
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("initialize");

      IDE.CODEASSISTANT.closeForm();
   }
}
