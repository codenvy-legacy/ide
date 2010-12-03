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
package org.exoplatform.ide.operation.file.autocompletion;

import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
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
      Thread.sleep(TestConstants.SLEEP);
      IDE.toolbar().runCommandFromNewPopupMenu("CSS File");
      Thread.sleep(TestConstants.SLEEP);

      cssTest();

//      closeTab("0");
//      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton");
      IDE.editor().closeUnsavedFileAndDoNotSave(0);
   }

   @Test
   public void testGoogleGadget() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad("30000");
      Thread.sleep(TestConstants.SLEEP);
      IDE.toolbar().runCommandFromNewPopupMenu("Google Gadget");
      Thread.sleep(TestConstants.SLEEP);

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);

      Thread.sleep(3000);

     //************fixed**********
      for (int i = 0; i < 16; i++)
      {
         selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_RIGHT);
         Thread.sleep(500);
      }
      //****************************
      selenium.keyDown("//body[@class='editbox']", "\\35");
      selenium.keyDown("//body[@class='editbox']", "\\13");

      AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CODEMIRROR_EDITOR_LOCATOR, "<style>\n");
      selenium.keyDown("//body[@class='editbox']", "\\13");
      selenium.keyDown("//body[@class='editbox']", "\\13");
      AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CODEMIRROR_EDITOR_LOCATOR, "</style>");
      Thread.sleep(300);

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);

      cssTest();

      IDE.editor().closeUnsavedFileAndDoNotSave(0);
   }

      @Test
   public void testHTML() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad("30000");
      Thread.sleep(TestConstants.SLEEP);
      IDE.toolbar().runCommandFromNewPopupMenu("HTML File");
      Thread.sleep(TestConstants.SLEEP);

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_END);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);

      AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CODEMIRROR_EDITOR_LOCATOR, "<script>\n</script>");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_END);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);

      AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CODEMIRROR_EDITOR_LOCATOR, "<style>\n\n</style>");

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);

      cssTest();

      IDE.editor().closeUnsavedFileAndDoNotSave(0);
   }

   /**
    * @throws InterruptedException
    */
   private void cssTest() throws InterruptedException
   {
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_PERIOD);
      selenium.typeKeys("//body[@class='editbox']", "main{");
      selenium.keyDown("//body[@class='editbox']", "\\13");

      openAutoCompleteForm();
      //      openAutoCompleteForm();

      selenium.focus("//input[@class='exo-autocomplete-edit']");
      selenium.typeKeys("//input[@class='exo-autocomplete-edit']", "list-st");

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.SLEEP);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.SLEEP);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.SLEEP);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.SLEEP);
      selenium.keyDown("//input[@class='exo-autocomplete-edit']", "\\13");

      String text = selenium.getText("//body[@class='editbox']");

      assertTrue(text.contains("list-style-type:"));

   }

   /**
    * @throws InterruptedException
    */
   private void openAutoCompleteForm() throws InterruptedException
   {
      selenium.controlKeyDown();
      selenium.keyDown("//body[@class='editbox']//br", "\\32");
      selenium.keyUp("//body[@class='editbox']//br", "\\32");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP);
   }

}
