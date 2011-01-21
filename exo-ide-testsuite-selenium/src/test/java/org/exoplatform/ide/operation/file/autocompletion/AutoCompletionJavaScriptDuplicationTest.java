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
import org.exoplatform.ide.utils.AbstractTextUtil;
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
      Thread.sleep(TestConstants.SLEEP);
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.JAVASCRIPT_FILE);
      Thread.sleep(TestConstants.SLEEP);

      AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CODEMIRROR_EDITOR_LOCATOR, "var a;\n \n function a() {\n}");
      Thread.sleep(TestConstants.SLEEP);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
      Thread.sleep(TestConstants.SLEEP);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
      Thread.sleep(TestConstants.SLEEP);
      
      Thread.sleep(10000);
      
      openAutoCompleteForm();
      Thread.sleep(TestConstants.SLEEP);
      
      assertTrue(selenium.isElementPresent("//div[contains(text(), 'a')]") && selenium.isElementPresent("//div[contains(text(), 'VARIABLE')]"));
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ESCAPE);
      
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      
      openAutoCompleteForm();
      Thread.sleep(TestConstants.SLEEP);
      
      assertTrue(selenium.isElementPresent("//div[contains(text(), 'a')]") && selenium.isElementPresent("//div[contains(text(), 'FUNCTION')]"));
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ESCAPE);
      
      IDE.editor().closeUnsavedFileAndDoNotSave(0);
   }

}
