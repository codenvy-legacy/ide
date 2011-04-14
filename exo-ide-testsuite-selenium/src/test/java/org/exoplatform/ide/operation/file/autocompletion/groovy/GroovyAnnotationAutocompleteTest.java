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

import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Dec 16, 2010 10:46:04 AM evgen $
 *
 */
public class GroovyAnnotationAutocompleteTest extends BaseTest
{

   @Test
   public void testGroovyAnnotation() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.REST_SERVICE_FILE);
      Thread.sleep(TestConstants.SLEEP);

      for (int i = 0; i < 8; i++)
      {
         selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
         Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      }
      selenium.keyDown("//body[@class='editbox']", "\\35");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      typeTextIntoEditor(0, "@");
      
      IDE.codeAssistant().openForm();
      IDE.codeAssistant().checkElementNotPresent("hello(String):String");
      IDE.codeAssistant().checkElementPresent("Deprecated");
      IDE.codeAssistant().checkElementPresent("Documented");
      IDE.codeAssistant().checkElementPresent("Inherited");
      IDE.codeAssistant().checkElementPresent("Override");
      IDE.codeAssistant().checkElementPresent("Retention");
      IDE.codeAssistant().checkElementPresent("SuppressWarnings");
      IDE.codeAssistant().checkElementPresent("Target");
      
      for (int i = 0; i < 3; i++)
      {
         selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
         Thread.sleep(TestConstants.SLEEP_SHORT);
      }
      
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertTrue(getTextFromCodeEditor(0).contains("@Override"));
   }
   
}
