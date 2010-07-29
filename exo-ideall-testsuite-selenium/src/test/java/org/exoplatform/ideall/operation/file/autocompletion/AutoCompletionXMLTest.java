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
package org.exoplatform.ideall.operation.file.autocompletion;

import static org.junit.Assert.*;

import com.thoughtworks.selenium.*;
import org.exoplatform.ideall.BaseTest;
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
      selenium.refresh();
      selenium.waitForPageToLoad("30000");
      Thread.sleep(1000);
      selenium.mouseDownAt("//div[@title='New']//img", "");
      selenium.mouseUpAt("//div[@title='New']//img", "");
      selenium.mouseDownAt("//td[@class=\"exo-popupMenuTitleField\"]//nobr[contains(text(), \"XML\")]", "");
      Thread.sleep(1000);
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
      Thread.sleep(500);
      
      selenium.focus("//input[@class='exo-autocomplete-edit']");
      selenium.typeKeys("//input[@class='exo-autocomplete-edit']", "ro");
      selenium.keyPress("//input[@class='exo-autocomplete-edit']", "\\8");
      //selenium.keyPress("//input[@class='exo-autocomplete-edit']", "\\13");
      assertTrue(selenium.isElementPresent("//div[contains(text(), 'rot')]"));
      
      selenium.keyPressNative("10");
      String textAfter = selenium.getText("//body[@class='editbox']");
      assertTrue(textAfter.contains("<root></root>"));
      closeTab("0");
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton");
   }
}
