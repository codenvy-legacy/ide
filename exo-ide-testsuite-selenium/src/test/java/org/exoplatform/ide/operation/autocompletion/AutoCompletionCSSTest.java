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
package org.exoplatform.ide.operation.autocompletion;

import static org.junit.Assert.assertTrue;

import java.awt.event.KeyEvent;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.utils.AbstractTextUtil;
import org.junit.Test;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class AutoCompletionCSSTest extends BaseTest
{

   @Test
   public void testPlainCSS() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();      
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.CSS_FILE);

      cssTest();
      //IDE.EDITOR.closeUnsavedFileAndDoNotSave(0);
      IDE.EDITOR.closeTabIgnoringChanges(0);
   }

   @Test
   public void testGoogleGadget() throws Exception
   {
      refresh();
      IDE.WORKSPACE.waitForRootItem();      
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.GOOGLE_GADGET_FILE);

      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);

      //************fixed**********
      for (int i = 0; i < 16; i++)
      {
         selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_RIGHT);
         Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      }
      //****************************
      selenium().keyDown("//body[@class='editbox']", "\\35");
      selenium().keyDown("//body[@class='editbox']", "\\13");

      AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CODEMIRROR_EDITOR_LOCATOR, "<style>\n");
      selenium().keyDown("//body[@class='editbox']", "\\13");
      selenium().keyDown("//body[@class='editbox']", "\\13");
      AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CODEMIRROR_EDITOR_LOCATOR, "</style>");
      Thread.sleep(300);

      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_UP);

      cssTest();

      IDE.EDITOR.closeTabIgnoringChanges(0);
   }

   @Test
   public void testHTML() throws Exception
   {
      selenium().refresh();
      selenium().waitForPageToLoad("" + TestConstants.PAGE_LOAD_PERIOD);
      IDE.WORKSPACE.waitForRootItem();
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.HTML_FILE);

      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);

      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_END);
      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);

      AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CODEMIRROR_EDITOR_LOCATOR, "<script>\n</script>");
      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);

      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_END);
      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);

      AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CODEMIRROR_EDITOR_LOCATOR, "<style>\n\n</style>");

      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_UP);

      cssTest();

      //IDE.EDITOR.closeUnsavedFileAndDoNotSave(0);
      IDE.EDITOR.closeTabIgnoringChanges(0);
   }

   /**
    * @throws InterruptedException
    */
   private void cssTest() throws Exception
   {
      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_PERIOD);
      IDE.EDITOR.typeTextIntoEditor(0, "main{");
      IDE.EDITOR.runHotkeyWithinEditor(0, false, false, KeyEvent.VK_ENTER);

      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.typeToInput("list-st");

      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      IDE.CODEASSISTANT.insertSelectedItem();

      String text = IDE.EDITOR.getTextFromCodeEditor(0);

      assertTrue(text.contains("list-style-type:"));

   }
}
