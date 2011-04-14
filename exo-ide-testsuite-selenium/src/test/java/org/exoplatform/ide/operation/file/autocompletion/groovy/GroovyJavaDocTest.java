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
package org.exoplatform.ide.operation.file.autocompletion.groovy;

import static org.junit.Assert.assertFalse;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.core.CodeAssistant;
import org.junit.Test;

/**
 * TODO: ignore this test until we consider how receive javadoc for methods with generic parameters.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Dec 8, 2010 4:03:58 PM evgen $
 *
 */

public abstract class GroovyJavaDocTest extends BaseTest
{
   
   @Test
   public void testGroovyJavaDoc() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.REST_SERVICE_FILE);
      Thread.sleep(TestConstants.SLEEP);

      for (int i = 0; i < 9; i++)
      {
         selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
         Thread.sleep(TestConstants.SLEEP_SHORT);
      }
      selenium.keyDown("//body[@class='editbox']", "\\35");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      typeTextIntoEditor(0, "Collections.");

     IDE.codeAssistant().openForm();
      
      for (int i = 0; i < 4; i++)
      {
         selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
         Thread.sleep(TestConstants.SLEEP_SHORT);
      }
      Thread.sleep(TestConstants.SLEEP);
      IDE.codeAssistant().checDocFormPresent();
      Thread.sleep(TestConstants.SLEEP);
      selenium.selectFrame(CodeAssistant.Locators.JAVADOC_DIV);
      assertFalse(selenium.isElementPresent("//body/pre[text()='Not found']"));
      selectMainFrame();
      
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.SLEEP);
      selenium.selectFrame(CodeAssistant.Locators.JAVADOC_DIV);
      assertFalse(selenium.isElementPresent("//body/pre[text()=\"Not found\"]"));
      selectMainFrame();
      
      IDE.editor().closeUnsavedFileAndDoNotSave(0);
   }
   
}
