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
package org.exoplatform.ide.operation.browse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.core.SelectWorkspace;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class WorkspaceTest extends BaseTest
{
   @AfterClass
   public static void tearDown()
   {
      deleteCookies();
   }

   @Test
   public void testDefaultEntryPoint() throws Exception
   {
      waitForRootElement();
      //check default workspace is root of navigation tree
      IDE.NAVIGATION.assertItemVisible(WS_URL);
      assertEquals(WS_NAME, IDE.NAVIGATION.getRowTitle(1));
   }

   @Test
   public void testSelectWorkspace() throws Exception
   {
      waitForRootElement();
      //----- 1 ---------------
      //check form Workspace
      //call select workspace window
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.SELECT_WORKSPACE);
      IDE.SELECT_WORKSPACE.waitForDialog();
      //check select workspace window
      assertTrue(selenium.isElementPresent(SelectWorkspace.SELECT_WORKSPACE_FORM_LOCATOR));
      assertTrue(selenium.isElementPresent(IDE.SELECT_WORKSPACE.getGwtDialogCaptionLocator("Workspace")));
      assertTrue(selenium.isElementPresent(SelectWorkspace.OK_BUTTON_ID));
      assertTrue(selenium.isElementPresent(SelectWorkspace.CANCEL_BUTTON_ID));
      assertTrue(selenium.isElementPresent(SelectWorkspace.LIST_GRID_ID));
      assertTrue(selenium.isTextPresent(ENTRY_POINT_URL + WS_NAME_2 + "/"));
      assertTrue(selenium.isTextPresent(ENTRY_POINT_URL + WS_NAME + "/"));

      //check Ok button is disabled
      assertFalse(IDE.SELECT_WORKSPACE.getOkButtonState());
      //check Cancel button is enabled
      assertTrue(IDE.SELECT_WORKSPACE.getCancelButtonState());
      //click Cancel button and check form disappeared
      IDE.SELECT_WORKSPACE.clickCancelButton();
      IDE.SELECT_WORKSPACE.waitForDialogNotPresent();
      //check workspace doesn't changed
      assertEquals(WS_NAME, selenium.getText(IDE.NAVIGATION.getItemId(WS_URL)));

      //----- 2 ---------------
      //check changing of workspace
      //select second workspace
      IDE.SELECT_WORKSPACE.changeWorkspace(WS_NAME_2);
      assertEquals(WS_NAME_2, IDE.NAVIGATION.getRowTitle(1));

      // return to initial workspace
      IDE.SELECT_WORKSPACE.changeWorkspace(WS_NAME);
      assertEquals(WS_NAME, IDE.NAVIGATION.getRowTitle(1));
   }

}
