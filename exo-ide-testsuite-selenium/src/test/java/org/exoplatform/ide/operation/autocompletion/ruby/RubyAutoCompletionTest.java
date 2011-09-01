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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: RubyAutoCompletionTest May 11, 2011 11:52:33 AM evgen $
 *
 */
public class RubyAutoCompletionTest extends BaseTest
{
   private static final String FOLDER_NAME = RubyAutoCompletionTest.class.getSimpleName();

   private static final String FILE_NAME = "RubyCodeAssistantTest.rb";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + FOLDER_NAME + "/");
         VirtualFileSystemUtils.put(
            "src/test/resources/org/exoplatform/ide/operation/file/autocomplete/ruby/rubyAutocompletion.rb",
            MimeType.APPLICATION_RUBY, WS_URL + FOLDER_NAME + "/" + FILE_NAME);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail("Can't create test folder");
      }
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WORKSPACE_URL + FOLDER_NAME);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
   
   @Test
   public void testRubyAutocompletion() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.doubleClickOnFolder(WS_URL + FOLDER_NAME + "/");

      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + FOLDER_NAME + "/" + FILE_NAME, false);
      goToLine(26);

      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.checkElementPresent("h");
      IDE.CODEASSISTANT.checkElementPresent("w");
      IDE.CODEASSISTANT.checkElementPresent("@i");
      IDE.CODEASSISTANT.checkElementPresent("@@ins");
      IDE.CODEASSISTANT.checkElementPresent("$cl");

      IDE.CODEASSISTANT.typeToInput("@@");

      IDE.CODEASSISTANT.insertSelectedItem();

      IDE.EDITOR.typeTextIntoEditor(0, ".");

      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.checkElementPresent("prec_f()");
      IDE.CODEASSISTANT.checkElementPresent("between?(arg1, arg2, arg3)");
      IDE.CODEASSISTANT.checkElementPresent("abs()");
      IDE.CODEASSISTANT.checkElementPresent("next()");

      IDE.CODEASSISTANT.typeToInput("ro");

      IDE.CODEASSISTANT.insertSelectedItem();

      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).contains("@@ins.round()"));

      goToLine(32);

      
      IDE.EDITOR.typeTextIntoEditor(0, "M");
      //this method fix problem of returning cursor in codeeditor before character "M"
      IDE.EDITOR.runHotkeyWithinEditor(0, false, false, 35);
      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.checkElementPresent("MDA");
      IDE.CODEASSISTANT.checkElementPresent("MyClass");
      IDE.CODEASSISTANT.checkElementPresent("Method");
      IDE.CODEASSISTANT.checkElementPresent("Math");

      IDE.CODEASSISTANT.insertSelectedItem();

      IDE.EDITOR.typeTextIntoEditor(0, ".");
      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.checkElementPresent("finite?()");

      IDE.CODEASSISTANT.typeToInput("inf");
      IDE.CODEASSISTANT.insertSelectedItem();
      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).contains("MDA.infinite?()"));

      goToLine(33);

      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.checkElementPresent("g");
      IDE.CODEASSISTANT.checkElementPresent("num");
      IDE.CODEASSISTANT.checkElementPresent("$cl");

      IDE.CODEASSISTANT.insertSelectedItem();
      IDE.EDITOR.typeTextIntoEditor(0, ".");
      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.checkElementPresent("get");
      IDE.CODEASSISTANT.checkElementPresent("set");
      IDE.CODEASSISTANT.checkElementPresent("hello");
      IDE.CODEASSISTANT.checkElementPresent("initialize");

      IDE.CODEASSISTANT.closeForm();

      //IDE.EDITOR.closeUnsavedFileAndDoNotSave(0);
      IDE.EDITOR.closeTabIgnoringChanges(0);
   }

}
