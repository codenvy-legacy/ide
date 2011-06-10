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

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;

/**
 * Operations with form for selection and changing current workspace.
 * 
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
      String url = BaseTest.ENTRY_POINT_URL_IDE + workspaceName;
      if (!url.endsWith("/"))
      {
         url += "/";
      }
      selectWorkspaceInListGrid(url);
      
      // test is "Ok" button enabled
      checkButtonState(OK_BUTTON_ID, true);

      // click the "Ok" button 
      clickOkButton();
      waitForElementNotPresent(SELECT_WORKSPACE_FORM_LOCATOR);
   }
   
   /**
    * Select workspace by URL in list grid.
    * @param workspaceUrl - the URL of workspace
    * @throws InterruptedException
    */
   public void selectWorkspaceInListGrid(String workspaceUrl) throws InterruptedException
   {
      selenium().click(SELECT_WORKSPACE_FORM_LOCATOR + "//span[text()='" + workspaceUrl + "']");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
   }
   
   /**
    * Wait while "Select workspace" dialog appears.
    * 
    * @throws Exception
    */
   public void waitForDialog() throws Exception
   {
      waitForElementPresent(SELECT_WORKSPACE_FORM_LOCATOR);
   }
   
   /**
    * Wait while "Select workspace" dialog disappears.
    * 
    * @throws Exception
    */
   public void waitForDialogNotPresent() throws Exception
   {
      waitForElementNotPresent(SELECT_WORKSPACE_FORM_LOCATOR);
   }
   
   /**
    * Return is Ok button enabled.
    * 
    * @return boolean
    * @throws Exception
    */
   public boolean getOkButtonState() throws Exception
   {
      return getButtonState(OK_BUTTON_ID);
   }
   
   /**
    * Return is Ok button enabled.
    * @return boolean
    * @throws Exception
    */
   public boolean getCancelButtonState() throws Exception
   {
      return getButtonState(CANCEL_BUTTON_ID);
   }
   
   /**
    * Click Ok button.
    * @throws Exception
    */
   public void clickOkButton() throws Exception
   {
      selenium().click(OK_BUTTON_ID);
   }
   
   /**
    * Click Cancel button.
    * @throws Exception
    */
   public void clickCancelButton() throws Exception
   {
      selenium().click(CANCEL_BUTTON_ID);
   }
   
   /**
    * Make double click on workspace pointed by name.
    * 
    * @param workspaceUrl workspace's name
    * @throws InterruptedException
    */
   public void doubleClickInListGrid(String workspaceName) throws InterruptedException
   {
      selenium().click("//table[@id='"+LIST_GRID_ID+"']//tr//span[contains(., '" + workspaceName + "')]");
      selenium().doubleClick("//table[@id='"+LIST_GRID_ID+"']//tr//span[contains(., '" + workspaceName + "')]");
   }
}
