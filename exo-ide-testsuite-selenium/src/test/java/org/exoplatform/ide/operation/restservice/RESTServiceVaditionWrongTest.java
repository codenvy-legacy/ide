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
package org.exoplatform.ide.operation.restservice;

import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.Locators;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.junit.Test;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
//IDE-25
public class RESTServiceVaditionWrongTest extends BaseTest
{

   @Test
   public void testValidaton() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.REST_SERVICE_FILE);
      Thread.sleep(TestConstants.SLEEP);

      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_END);

      IDE.EDITOR.typeTextIntoEditor(0, "1");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.VALIDATE);

      waitForElementPresent(Locators.OperationForm.OUTPUT_FORM_LOCATOR);

      String mess = IDE.OUTPUT.getOutputMessageText(1);
      assertTrue(mess.contains("[ERROR] Untitled file.grs validation failed. Error (400: Bad Request)"));
   }

}
