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
import org.junit.Test;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class AutoCompletionXMLTest extends BaseTest
{

   @Test
   public void openForm() throws Throwable
   {
      Thread.sleep(TestConstants.SLEEP);
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      Thread.sleep(TestConstants.SLEEP);
      String text =IDE.EDITOR.getTextFromCodeEditor(0);
      assertTrue(text.startsWith("<?xml version='1.0' encoding='UTF-8'?>"));

     IDE.EDITOR.runHotkeyWithinEditor(0, false, false, KeyEvent.VK_HOME);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      //      selenium.keyDown("//body[@class='editbox']", "\\35");
     IDE.EDITOR.pressEnter();
     IDE.EDITOR.typeTextIntoEditor(0, "<root>");
     IDE.EDITOR.pressEnter();
     IDE.EDITOR.pressEnter();
     IDE.EDITOR.typeTextIntoEditor(0, "</root>");
      selenium.keyPressNative("" + KeyEvent.VK_UP);

     IDE.EDITOR.typeTextIntoEditor(0, "<rot>");
     IDE.EDITOR.pressEnter();
     IDE.EDITOR.pressEnter();
     IDE.EDITOR.typeTextIntoEditor(0, "</rot>");
      selenium.keyPressNative("" + KeyEvent.VK_UP);

     IDE.EDITOR.typeTextIntoEditor(0, "<rt>");
     IDE.EDITOR.pressEnter();
     IDE.EDITOR.pressEnter();
     IDE.EDITOR.typeTextIntoEditor(0, "</rt>");

      selenium.keyPressNative("" + KeyEvent.VK_UP);

      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.typeToInput("ro");
      IDE.CODEASSISTANT.checkElementPresent("rot");
      IDE.CODEASSISTANT.insertSelectedItem();

      String textAfter =IDE.EDITOR.getTextFromCodeEditor(0);
      assertTrue(textAfter.contains("<root></root>"));

     //IDE.EDITOR.closeUnsavedFileAndDoNotSave(0);
     IDE.EDITOR.closeTabIgnoringChanges(0);
   }

}
