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

      
      typeTextIntoEditor(0, "<div class=\"ItemDetail\" style=\"display:block\">");
      selenium.keyDown("//body[@class='editbox']", "\\13");

      typeTextIntoEditor(0,"<div class=\"NoneAppsMessage\" style=\"display:block\">");
      selenium.keyDown("//body[@class='editbox']", "\\13");

      typeTextIntoEditor(0, "<%=_ctx.appRes(\"UIAddNewApplication.label.NoneApp\")%>");
      selenium.keyDown("//body[@class='editbox']", "\\13");

      typeTextIntoEditor(0, "</div>");
      selenium.keyDown("//body[@class='editbox']", "\\13");

      typeTextIntoEditor(0,"</div>");

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);

      selenium.keyDown("//body[@class='editbox']", "\\35");

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_LEFT);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_LEFT);

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);

      selenium.keyDown("//body[@class='editbox']", "\\36");

      IDE.codeAssistant().openForm();

      IDE.codeAssistant().checkElementPresent("!DOCTYPE");
      IDE.codeAssistant().checkElementPresent("acronym");
      IDE.codeAssistant().checkElementPresent("a");
      IDE.codeAssistant().closeForm();

      IDE.editor().closeUnsavedFileAndDoNotSave(0);
   }

   private void htmlTest() throws Exception
   {
      selenium.keyDown("//body[@class='editbox']", "\\35");
      selenium.keyDown("//body[@class='editbox']", "\\13");

      selenium.typeKeys("//body[@class='editbox']", "<t");

      IDE.codeAssistant().openForm();

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      IDE.codeAssistant().insertSelectedItem();
      
      String textAfter = getTextFromCodeEditor(0);
      assertTrue(textAfter.contains("<textarea></textarea>"));

      typeTextIntoEditor(0, "<p ");

      IDE.codeAssistant().openForm();

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
     IDE.codeAssistant().insertSelectedItem();
     
      String textA = getTextFromCodeEditor(0);
      assertTrue(textA.contains("<p class=\"\""));

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_RIGHT);

      IDE.codeAssistant().openForm();
      IDE.codeAssistant().insertSelectedItem();

      String text = getTextFromCodeEditor(0);
      assertTrue(text.contains("<p class=\"\"></p>"));
   }

   private void GoogleGadgetTest() throws Exception
   {
      selenium.keyDown("//body[@class='editbox']", "\\35");
      selenium.keyDown("//body[@class='editbox']", "\\13");
      
      for (int i = 0; i < 16; i++)
      {
         selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_RIGHT);
         Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      }
      typeTextIntoEditor(0, "<t");

      IDE.codeAssistant().openForm();

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      IDE.codeAssistant().insertSelectedItem();
      
      String textAfter = getTextFromCodeEditor(0);
      assertTrue(textAfter.contains("<textarea></textarea>"));

      typeTextIntoEditor(0, "<p ");
      
      IDE.codeAssistant().openForm();

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      IDE.codeAssistant().insertSelectedItem();
      
      String textA = getTextFromCodeEditor(0);
      assertTrue(textA.contains("<p class=\"\""));

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_RIGHT);

      IDE.codeAssistant().openForm();
      IDE.codeAssistant().insertSelectedItem();

      String text = getTextFromCodeEditor(0);
      assertTrue(text.contains("<p class=\"\"></p>"));
   }
}
