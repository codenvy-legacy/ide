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
import org.exoplatform.ide.TestConstants;
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
   private static final String DEV_MONIT = "dev-monit";
   
   private static final String PRODUCTION = "production";
   
   @AfterClass
   public static void tearDown()
   {
      deleteCookies();
   }
   
   @Test
   public void testDefaultEntryPoint() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      //check dev-monit is root of navigation tree
      assertElementPresentInWorkspaceTree(DEV_MONIT);
      checkCurrentWorkspace(DEV_MONIT);
   }
   
   @Test
   public void testSelectWorkspace() throws Exception
   {
      //----- 1 ---------------
      //check form Workspace
      //call select workspace window
      runTopMenuCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.SELECT_WORKSPACE);
      //check select workspace window
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideSelectWorkspaceForm\"]"));
      assertTrue(selenium.isTextPresent("Workspace"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideSelectWorkspaceFormOkButton\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideSelectWorkspaceFormCancelButton\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//ListGrid[ID=\"ideEntryPointListGrid\"]"));
      assertTrue(selenium.isTextPresent("/rest/private/jcr/repository/production"));
      assertTrue(selenium.isTextPresent("/rest/private/jcr/repository/dev-monit"));
      
      //check Ok button is disabled
      assertTrue(selenium.isElementPresent("//div[@eventproxy='ideSelectWorkspaceFormOkButton']//td[@class='buttonTitleDisabled' and text()='OK']"));
      //check Cancel button is enabled
      assertTrue(selenium.isElementPresent("//div[@eventproxy='ideSelectWorkspaceFormCancelButton']//td[@class='buttonTitle' and text()='Cancel']"));
      //click Cancel button and check form dissapeared
      selenium.click("scLocator=//IButton[ID=\"ideSelectWorkspaceFormCancelButton\"]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideSelectWorkspaceForm\"]"));
      //check workspace doesn't chanched
      checkCurrentWorkspace(DEV_MONIT);
      
      //----- 2 ---------------
      //check changing of workspace
      //call select workspace window
      runTopMenuCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.SELECT_WORKSPACE);
      
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideSelectWorkspaceForm\"]"));
      //select production workspace
      selectWorkspaceFromListGrid(PRODUCTION);
      //click ok button
      selenium.click("scLocator=//IButton[ID=\"ideSelectWorkspaceFormOkButton\"]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      //check production workspace is selected
      checkCurrentWorkspace(PRODUCTION);
   }
   
   private void checkCurrentWorkspace(String workspaceName)
   {
      assertEquals(workspaceName, selenium.getText("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[0]/col[0]"));
   }
   
   private void selectWorkspaceFromListGrid(String workspaceName) throws Exception
   {
      selenium.mouseDownAt("//div[@eventproxy='ideEntryPointListGrid']//table[@class='listTable']//span[contains(text(), '"
         + workspaceName + "/')]", "");
      selenium.mouseUpAt("//div[@eventproxy='ideEntryPointListGrid']//table[@class='listTable']//span[contains(text(), '" 
         + workspaceName + "/')]", "");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
   }

}
