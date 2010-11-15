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
      runCommandFromMenuNewOnToolbar("XML File");
      Thread.sleep(TestConstants.SLEEP);
      String text = selenium.getText("//body[@class='editbox']");
      assertTrue(text.startsWith("<?xml version='1.0' encoding='UTF-8'?>"));
     
      selenium.mouseDownAt("//body[@class='editbox']//span[2]", "");
      selenium.mouseUpAt("//body[@class='editbox']//span[2]", "");
      selenium.keyDown("//body[@class='editbox']", "\\35");
      selenium.keyDown("//body[@class='editbox']", "\\13");
      selenium.typeKeys("//body[@class='editbox']", "<root>");
      selenium.keyDown("//body[@class='editbox']", "\\13");
      selenium.keyDown("//body[@class='editbox']", "\\13");
      selenium.typeKeys("//body[@class='editbox']", "</root>");
      selenium.keyPressNative("38");
      
      selenium.typeKeys("//body[@class='editbox']", "<rot>");
      selenium.keyDown("//body[@class='editbox']", "\\13");
      selenium.keyDown("//body[@class='editbox']", "\\13");
      selenium.typeKeys("//body[@class='editbox']", "</rot>");
      selenium.keyPressNative("38");
      
      
      selenium.typeKeys("//body[@class='editbox']", "<rt>");
      selenium.keyDown("//body[@class='editbox']", "\\13");
      selenium.keyDown("//body[@class='editbox']", "\\13");
      selenium.typeKeys("//body[@class='editbox']", "</rt>");
      
      selenium.keyPressNative("38");
      
      selenium.controlKeyDown();
      selenium.keyDown("//body[@class='editbox']//span[6]", "\\32");
      selenium.keyUp("//body[@class='editbox']//span[6]", "\\32");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      selenium.focus("//input[@class='exo-autocomplete-edit']");
      selenium.typeKeys("//input[@class='exo-autocomplete-edit']", "ro");
      selenium.keyPress("//input[@class='exo-autocomplete-edit']", "\\8");
      assertTrue(selenium.isElementPresent("//div[contains(text(), 'rot')]"));
      
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      selenium.keyPressNative(""+java.awt.event.KeyEvent.VK_ENTER);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      String textAfter = getTextFromCodeEditor(0);
      assertTrue(textAfter.contains("<root></root>"));

      closeUnsavedFileAndDoNotSave(0);      
   }
}
