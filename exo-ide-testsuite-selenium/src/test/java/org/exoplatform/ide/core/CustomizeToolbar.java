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

import static org.junit.Assert.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:mmusienko@exoplatform.com">Musienko Maxim</a>
 *
 *  
 * */
public class CustomizeToolbar extends AbstractTestModule
{
   private interface Locators
   {
      String CANCEL_BUTTON_ID = "ide.core.customize-tolbar.cancel-button";

      String ADD_BUTTON_ID = "ide.core.customize-toolbar.add-button";

      String DELIMETER_BUTTON_ID = "ide.core.customize-toolbar.delimiter-button";

      String DELETE_BUTTON_ID = "ide.core.customize-toolbar.delete-button";

      String MOVE_UP_BUTTON_ID = "ide.core.customize-toolbar.move-up-button";

      String MOVE_DOWN_BUTTON_ID = "ide.core.customize-toolbar.move-down-button";

      String OK_BUTTON_ID = "ide.core.customize-toolbar.ok-button";

      String DEFAULTS_BUTTON_ID = "ide.core.customize-toolbar.defaults-button";

      String TOOLBAR_ID = "ide.core.customize-toolbar.toolbar-items-list";

      String TOOLBAR_COMMANDLIST_ID = "ide.core.customize-toolbar.commands-list";

      String CUSTOMIZE_TOOLBAR_FORM_ID = "ide.core.customize-toolbar-window";

      String GET_COMMANDS_BY_CSS = "table[id='ide.core.customize-toolbar.commands-list']>tbody tr";

      String GET_TOOLBAR_COMMANDS_BY_CSS = "table[id=" + "'" + TOOLBAR_ID + "'" + "]" + ">tbody tr";

      String SELECTION_ON_TOOLBAR_ELEMENT = "//table[@id=" + "'" + TOOLBAR_ID + "'" + "]" + "/tbody/tr[%s]";

      String SELECT_ON_TOOLBAR_ELEMENT_BY_NAME = "//table[@id=" + "'" + TOOLBAR_ID + "'" + "]"
         + "/tbody/tr/td//div[contains(.,'%s')]";

      String SELECT_ON_COMMANDLIST_ELEMENT_BY_NAME = "//table[@id=" + "'" + TOOLBAR_COMMANDLIST_ID + "'" + "]"
         + "/tbody//tr/td//div[contains(.,'%s')]";

      String SELECT_ON_COMMANDLIST_ELEMENT_BY_NUM = "//table[@id=" + "'" + TOOLBAR_COMMANDLIST_ID + "'" + "]"
         + "/tbody/tr[%s]";

   }

   @FindBy(id = Locators.DELETE_BUTTON_ID)
   private WebElement deleteButton;

   @FindBy(id = Locators.OK_BUTTON_ID)
   private WebElement okButton;

   @FindBy(id = Locators.CANCEL_BUTTON_ID)
   private WebElement cancelButton;

   @FindBy(id = Locators.DEFAULTS_BUTTON_ID)
   private WebElement defaultButton;

   @FindBy(id = Locators.CUSTOMIZE_TOOLBAR_FORM_ID)
   private WebElement customizeToolbarForm;

   @FindBy(id = Locators.MOVE_DOWN_BUTTON_ID)
   private WebElement moveDownButton;

   @FindBy(id = Locators.MOVE_UP_BUTTON_ID)
   private WebElement moveUpButton;

   @FindBy(id = Locators.DELIMETER_BUTTON_ID)
   private WebElement addDelimeterButton;
   
   @FindBy(id = Locators.ADD_BUTTON_ID)
   private WebElement addButton;

   /**
    * wait appearance customizeToolbarForm Form 
    */
   public void waitOpened()
   {
      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            return customizeToolbarForm != null && customizeToolbarForm.isDisplayed();
         }
      });
   }

   /**
    * waits disappearance toolbar-form
    */
   public void waitClosed()
   {
      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               input.findElement(By.id(Locators.CUSTOMIZE_TOOLBAR_FORM_ID));
               return false;
            }
            catch (Exception e)
            {
               return true;
            }
         }
      });

   }

   /**
    * Selects an item on the list toolbar by number
    * start 1
    * @param number
    */
   public void selectNumElementOnToolbar(int number)
   {
      WebElement toolbarMenu =
         driver().findElement(By.xpath(String.format(Locators.SELECTION_ON_TOOLBAR_ELEMENT, number)));
      toolbarMenu.click();
   }
   
   
   /**
    * Selects an item on the commandlist  by number
    * start 1
    * @param number
    */
   
   public void selectNumElementOnCommandListbar(int number)
   {
      WebElement toolbarMenu =
         driver().findElement(By.xpath(String.format(Locators.SELECT_ON_COMMANDLIST_ELEMENT_BY_NUM, number)));
      toolbarMenu.click();
   }
   

   /**
    * Selects an item on the list toolbar by name
    * @param name
    */
   public void selectElementOnToolbarByName(String name)
   {
      WebElement toolbarMenuByName =
         driver().findElement(By.xpath(String.format(Locators.SELECT_ON_TOOLBAR_ELEMENT_BY_NAME, name)));
      toolbarMenuByName.click();
   }

   
     
   /**
    * Selects an item on the commandlist by name
    * @param name
    */
   public void selectElementOnCommandlistbarByName(String name)
   {
      WebElement toolbarMenuByName =
         driver().findElement(By.xpath(String.format(Locators.SELECT_ON_COMMANDLIST_ELEMENT_BY_NAME, name)));
      toolbarMenuByName.click();
   }
   
   
   
   /**
    * click on add button on
    * Customize Toolbar form
    */
   public void addClick()
   {
      addButton.click();
   }
   
   /**
    * click on cancel button on
    * Customize Toolbar form
    */
   public void cancelClick()
   {
      cancelButton.click();
   }

   /**
    * click on delete button on
    * Customize Toolbar form
    */
   public void deleteClick()
   {
      deleteButton.click();
   }

   /**
    * click on delete button on
    * Customize Toolbar form
    */
   public void okClick()
   {
      okButton.click();
   }

   /**
    * click on default button on
    * Customize Toolbar form
    */
   public void defaultClick()
   {
      defaultButton.click();
   }

   /**
    * click on move up button on
    * Customize Toolbar form
    */
   public void moveUpClick()
   {
      moveUpButton.click();
   }

   /**
    * click on move down button on
    * Customize Toolbar form
    */
   public void moveDownClick()
   {
      moveDownButton.click();
   }

   /**
    * click on delimeter button on
    * Customize Toolbar form
    */
   public void delimiterClick()
   {
      addDelimeterButton.click();
   }

   /**
    * Return true if command is present
    * in Toolbar list.
    * @param nameMenu
    * @return
    */
   public boolean isToolbarListPresent(String nameMenu)
   {
      List<String> res = new ArrayList<String>();
      List<WebElement> allCommands = driver().findElements(By.cssSelector(Locators.GET_TOOLBAR_COMMANDS_BY_CSS));

      for (WebElement name : allCommands)
      {
         res.add(name.getText().trim());
      }
      return res.contains(nameMenu);
   }

   /**
    * Return true if command is present in
    * command list
    * @param nameMenu
    * @return
    */
   public boolean isCommandListPresent(String nameMenu)
   {
      List<String> res = new ArrayList<String>();
      List<WebElement> allCommands = driver().findElements(By.cssSelector(Locators.GET_COMMANDS_BY_CSS));

      for (WebElement name : allCommands)
      {

         res.add(name.getText().trim());

      }
      return res.contains(nameMenu);
   }

   /**
    * Return All names of commands
    * on toolbar list 
    * @return
    * 
    */
   public List<String> getallToolbarList()
   {
      List<String> toolbarList = new ArrayList<String>();
      List<WebElement> allCommands = driver().findElements(By.cssSelector(Locators.GET_TOOLBAR_COMMANDS_BY_CSS));

      for (WebElement name : allCommands)
      {
         toolbarList.add(name.getText().trim());
      }
      return toolbarList;
   }

   /**
    * Get all names of elements command list
    * @return
    */
   public List<String> getallCommandList()
   {
      List<String> commandList = new ArrayList<String>();
      List<WebElement> allCommands = driver().findElements(By.cssSelector(Locators.GET_COMMANDS_BY_CSS));

      for (WebElement name : allCommands)
      {
         commandList.add(name.getText().trim());
      }
      return commandList;
   }

   public void isDefaultToolbarList()
   {
      List<String> getSet = getallToolbarList();

      List<String> defaultSet =
         Arrays.asList("Delimiter", "New * [Popup]", "Save", "Save As...", "Delimiter", "Cut Item(s)", "Copy Item(s)",
            "Paste Item(s)", "Delete...", "Search...", "Refresh Selected Folder", "Delimiter", "Undo Typing",
            "Redo Typing", "Format", "Delimiter", "Find-Replace...", "Delimiter", "Lock / Unlock File", "Delimiter",
            "Show / Hide Outline", "Delimiter", "Show / Hide Documentation", "Spacer", "Properties", "Show Preview",
            "Deploy widget", "Show Netwibes Preview", "Set / Unset Autoload", "Validate", "Deploy", "Undeploy",
            "Run in Sandbox", "Deploy to Sandbox", "Undeploy from Sandbox", "Launch REST Service",
            "Show Template Preview", "Show Gadget Preview", "Preview node type", "Deploy node type");

      for (String chkName : defaultSet)
      {
         assertTrue(getSet.contains(chkName.toString()));
      }
   }

   public void isDefaultCommandlbarList()
   {
      List<String> getSet = getallCommandList();

      List<String> defaultSet =
         Arrays.asList("File", "New * [Popup]", "Upload File...", "Upload Zipped Folder...", "Open Local File...",
            "Open File By Path...", "Open by URL...", "Download File...", "Download Zipped Folder...", "Save",
            "Save As...", "Save All", "Save As Template...", "Rename...", "Delete...", "Search...",
            "Refresh Selected Folder", "File / New", "Create Folder...", "New TEXT", "New Google Gadget",
            "New Netvibes Widget", "New XML", "New Java Script", "New HTML", "New CSS", "New REST Service", "New POGO",
            "New Template", "New Data Object", "New Java Class", "New JSP File", "New Ruby File", "New PHP File",
            "Create File From Template...", "Project / New", "Create Project...", "Create Project...",
            "Import a Sample Project...", "Import from GitHub...", "Project", "Open...", "Close", "Properties...",
            "Configure Classpath...", "Git URL (Rread-Only)...", "Project / PaaS", "CloudBees", "CloudFoundry",
            "Heroku", "OpenShift", "Edit", "Cut Item(s)", "Copy Item(s)", "Paste Item(s)", "Undo Typing",
            "Redo Typing", "Format", "Find-Replace...", "Show / Hide Line Numbers", "Delete Current Line",
            "Go to Line...", "Lock / Unlock File", "View", "Properties", "Show / Hide Outline",
            "Show / Hide Documentation", "Go to Folder", "Get URL...", "Progress", "Output", "Log", "Run",
            "Show Preview", "Deploy widget", "Show Netwibes Preview", "Set / Unset Autoload", "Validate", "Deploy",
            "Undeploy", "Run in Sandbox", "Deploy to Sandbox", "Undeploy from Sandbox", "Launch REST Service",
            "Show Template Preview", "Show Gadget Preview", "Preview node type", "Deploy node type", "Git",
            "Initialize Repository", "Clone Repository...", "Delete Repository...", "Add...", "Reset Files...",
            "Reset...", "Remove...", "Commit...", "Branches...", "Merge...", "Show History...", "Status",
            "Git / Remote", "Push...", "Fetch...", "Pull...", "Remotes...", "PaaS / CloudBees", "InitializeApp",
            "ApplicationInfo", "UpdateApp", "DeleteApp", "AppList", "PaaS / CloudFoundry", "CreateApp", "UpdateApp",
            "DeleteApp", "ApplicationInfo", "StartApp", "StopApp", "RestartApp", "Map-UnmapUrl", "UpdMemory",
            "UpdInstances", "SwitchAccount", "Applications", "PaaS / Heroku", "Create application...",
            "Delete application...", "Rename application...", "Change environment...", "Application info...",
            "Logs...", "Rake...", "Deploy public key", "Switch account...", "PaaS / OpenShift", "Create domain...",
            "Create application...", "Delete application...", "Application info...", "Preview Application",
            "User info...", "Update SSH public key...", "Window / Show View", "Navigator", "Project Explorer",
            "Window", "Workspace...", "Customize Toolbar...", "HotKeys...", "Ssh Key Manager", "Help", "About...",
            "REST Services Discovery", "Show Available Dependencies...", "Welcome");
      for (String chkName : defaultSet)
      {
         assertTrue(getSet.contains(chkName.toString()));
      }
   }

   /**
    * return name of element which 
    * corresponds to a specific number
    * start with 1
    * @param numElem
    * @return
    */
   public String isElementNumPositionPresent(int numElem)
   {
      WebElement toolbarMenu =
         driver().findElement(By.xpath(String.format(Locators.SELECTION_ON_TOOLBAR_ELEMENT, numElem)));
      String nameElement = toolbarMenu.getText();
      return nameElement.trim();
   }

}
