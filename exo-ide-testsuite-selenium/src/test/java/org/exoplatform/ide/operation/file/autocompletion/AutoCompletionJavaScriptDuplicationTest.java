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
import org.junit.Test;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class AutoCompletionJavaScriptDuplicationTest extends BaseTest
{
   @Test
   public void testDuplication() throws Exception
   {
      Thread.sleep(1000);
      openNewFileFromToolbar("JavaScript File");
      Thread.sleep(1000);

      typeText("var a;\n \n function a() {\n}");
      
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
      
      openAutoCompleteForm();
      
      assertTrue(selenium.isElementPresent("//div[contains(text(), 'a')]") && selenium.isElementPresent("//div[contains(text(), 'VARIABLE')]"));
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ESCAPE);
      
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(500);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(500);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      
      openAutoCompleteForm();
      
      assertTrue(selenium.isElementPresent("//div[contains(text(), 'a')]") && selenium.isElementPresent("//div[contains(text(), 'FUNCTION')]"));
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ESCAPE);
      
      closeTab("0");
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton");
   }
   
   /**
    * @throws InterruptedException
    */
   private void openAutoCompleteForm() throws InterruptedException
   {
      selenium.controlKeyDown();
      selenium.keyDown("//body[@class='editbox']//span[6]", "\\32");
      selenium.keyUp("//body[@class='editbox']//span[6]", "\\32");
      selenium.controlKeyUp();
      Thread.sleep(1500);
      selenium.focus("//input[@class='exo-autocomplete-edit']");
   }
}
