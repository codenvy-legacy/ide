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
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.core.CodeAssistant;
import org.junit.Test;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class AutoCompletionHTMLTest extends BaseTest
{  
   @Test
   public void testHTML() throws InterruptedException, Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad(TestConstants.IDE_LOAD_PERIOD + "");
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);
      
      // select root folder
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      IDE.navigator().selectItem(WS_URL);
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.HTML_FILE);
      Thread.sleep(TestConstants.SLEEP);

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);

      htmlTest();
      
      IDE.editor().closeUnsavedFileAndDoNotSave(0);
   }

   /**
    * @throws InterruptedException
    */

   @Test
   public void testGoogleGadget() throws InterruptedException, Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad(TestConstants.IDE_LOAD_PERIOD + "");
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);
      
      // select root folder
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      IDE.navigator().selectItem(WS_URL);
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH); 
      
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.GOOGLE_GADGET_FILE);
      
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);

      //************fixed
      GoogleGadgetTest();
      //***********
      IDE.editor().closeUnsavedFileAndDoNotSave(0);
   }

    @Test
   public void testGroovyTemplate() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad(TestConstants.IDE_LOAD_PERIOD + "");
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);
      
      // select root folder
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      IDE.navigator().selectItem(WS_URL);
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
       
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.GROOVY_TEMPLATE_FILE);
      Thread.sleep(TestConstants.SLEEP);
      IDE.editor().deleteFileContent();

      selenium.typeKeys("//body[@class='editbox']", "<div class=\"ItemDetail\" st");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_Y);
      selenium.typeKeys("//body[@class='editbox']", "le=\"displa");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_Y);
      selenium.typeKeys("//body[@class='editbox']", ":block\">");
      selenium.keyDown("//body[@class='editbox']", "\\13");

      selenium.typeKeys("//body[@class='editbox']", "<div class=\"NoneAppsMessage\" st");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_Y);
      selenium.typeKeys("//body[@class='editbox']", "le=\"displa");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_Y);
      selenium.typeKeys("//body[@class='editbox']", ":block\">");
      selenium.keyDown("//body[@class='editbox']", "\\13");

      selenium.typeKeys("//body[@class='editbox']", "<%=_ctx");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_PERIOD);
      selenium.typeKeys("//body[@class='editbox']", "appRes(\"UIAddNewApplication");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_PERIOD);
      selenium.typeKeys("//body[@class='editbox']", "label");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_PERIOD);
      selenium.typeKeys("//body[@class='editbox']", "NoneApp\")%>");
      selenium.keyDown("//body[@class='editbox']", "\\13");

      selenium.typeKeys("//body[@class='editbox']", "</div>");
      selenium.keyDown("//body[@class='editbox']", "\\13");

      selenium.typeKeys("//body[@class='editbox']", "</div>");

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);

      selenium.keyDown("//body[@class='editbox']", "\\35");

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_LEFT);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_LEFT);

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);

      selenium.keyDown("//body[@class='editbox']", "\\36");

      IDE.codeAssistant().openForm();

      assertTrue(selenium.isElementPresent("//div[contains(text(), '!DOCTYPE')]"));
      assertTrue(selenium.isElementPresent("//div[contains(text(), 'acronym')]"));
      assertTrue(selenium.isElementPresent("//div[contains(text(), 'a')]"));

      selenium.keyDown("//input[@class='exo-autocomplete-edit']", "\\27");

      IDE.editor().closeUnsavedFileAndDoNotSave(0);
   }

   private void htmlTest() throws Exception
   {
      selenium.keyDown("//body[@class='editbox']", "\\35");
      selenium.keyDown("//body[@class='editbox']", "\\13");

      Thread.sleep(20000);

      selenium.typeKeys("//body[@class='editbox']", "<t");

      //Autocomplete.openForm();
      IDE.codeAssistant().openForm();

      selenium.focus("//input[@class='exo-autocomplete-edit']");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium.keyDown("//input[@class='exo-autocomplete-edit']", "\\13");

      String textAfter = selenium.getText("//body[@class='editbox']");
      assertTrue(textAfter.contains("<textarea></textarea>"));

      selenium.typeKeys("//body[@class='editbox']", "<p ");

      IDE.codeAssistant().openForm();

      selenium.focus("//input[@class='exo-autocomplete-edit']");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium.keyDown("//input[@class='exo-autocomplete-edit']", "\\13");

      String textA = selenium.getText("//body[@class='editbox']");
      assertTrue(textA.contains("<p class=\"\""));

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_RIGHT);

      IDE.codeAssistant().openForm();
      selenium.focus("//input[@class='exo-autocomplete-edit']");
      selenium.keyDown("//input[@class='exo-autocomplete-edit']", "\\13");

      String text = selenium.getText("//body[@class='editbox']");
      assertTrue(text.contains("<p class=\"\"></p>"));
   }

 //************fixed**********
   private void GoogleGadgetTest() throws Exception
   {
      selenium.keyDown("//body[@class='editbox']", "\\35");
      selenium.keyDown("//body[@class='editbox']", "\\13");

      Thread.sleep(5000);

      
      for (int i = 0; i < 16; i++)
      {
         selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_RIGHT);
         Thread.sleep(500);
      }
      selenium.typeKeys("//body[@class='editbox']", "<t");

      //Autocomplete.openForm();
      IDE.codeAssistant().openForm();

      selenium.focus("//input[@class='exo-autocomplete-edit']");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium.keyDown("//input[@class='exo-autocomplete-edit']", "\\13");

      String textAfter = selenium.getText("//body[@class='editbox']");
      assertTrue(textAfter.contains("<textarea></textarea>"));

      selenium.typeKeys("//body[@class='editbox']", "<p ");

      IDE.codeAssistant().openForm();

      selenium.focus("//input[@class='exo-autocomplete-edit']");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium.keyDown("//input[@class='exo-autocomplete-edit']", "\\13");

      String textA = selenium.getText("//body[@class='editbox']");
      assertTrue(textA.contains("<p class=\"\""));

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_RIGHT);

      IDE.codeAssistant().openForm();
      selenium.focus("//input[@class='exo-autocomplete-edit']");
      selenium.keyDown("//input[@class='exo-autocomplete-edit']", "\\13");

      String text = selenium.getText("//body[@class='editbox']");
      assertTrue(text.contains("<p class=\"\"></p>"));
      //****************************
   }
}
