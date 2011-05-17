/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.core;

import static org.junit.Assert.assertFalse;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:musienko.maxim@gmail.com">Musienko Maxim</a>
 * @version $Id: $
*/
public class SelectWorkspace extends AbstractTestModule
{
   public static final String SELECT_WORKSPACE_FORM_LOCATOR = "//div[@view-id='ideSelectWorkspaceView']";
   
   public static final String LIST_GRID_ID = "ideEntryPointListGrid";
   
   public static final String OK_BUTTON_ID = "ideEntryPointOkButton";
   
   public static final String CANCEL_BUTTON_ID = "ideEntryPointCancelButton";
   
   /**
    * Call "Select workspace" dialog and select workspace by workspaceName.
    *  
    * @param workspaceName
    * @throws Exception
    * @throws InterruptedException
    */
   public void changeWorkspace(String workspaceName) throws Exception, InterruptedException
   {
      IDE().MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.SELECT_WORKSPACE);

      waitForElementPresent(SELECT_WORKSPACE_FORM_LOCATOR);
      waitForElementPresent(LIST_GRID_ID);
      waitForElementPresent(OK_BUTTON_ID);
      waitForElementPresent(CANCEL_BUTTON_ID);
      String url = BaseTest.ENTRY_POINT_URL + workspaceName;
      if (!url.endsWith("/"))
      {
         url += "/";
      }
      selectWorkspaceInListGrid(url);
      
      // test is "Ok" button enabled
      checkButtonState(OK_BUTTON_ID, true);

      // click the "Ok" button 
      selenium().click(OK_BUTTON_ID);
      waitForElementNotPresent(SELECT_WORKSPACE_FORM_LOCATOR);
      assertFalse(selenium().isElementPresent(SELECT_WORKSPACE_FORM_LOCATOR));
   }
   
   public void selectWorkspaceInListGrid(String workspaceUrl) throws InterruptedException
   {
      selenium().click(SELECT_WORKSPACE_FORM_LOCATOR + "//span[text()='" + workspaceUrl + "']");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
   }
   
   public void waitForDialog() throws Exception
   {
      waitForElementPresent(SELECT_WORKSPACE_FORM_LOCATOR);
   }
   
   public boolean getOkButtonState() throws Exception
   {
      return getButtonState(OK_BUTTON_ID);
   }
   
   public boolean getCancelButtonState() throws Exception
   {
      return getButtonState(CANCEL_BUTTON_ID);
   }
   
   public void clickOkButton() throws Exception
   {
      selenium().click(OK_BUTTON_ID);
      waitForElementNotPresent(SELECT_WORKSPACE_FORM_LOCATOR);
   }
   
   public void clickCancelButton() throws Exception
   {
      selenium().click(CANCEL_BUTTON_ID);
      waitForElementNotPresent(SELECT_WORKSPACE_FORM_LOCATOR);
   }

}
