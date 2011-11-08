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

import org.exoplatform.ide.MenuCommands;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Folder.java May 13, 2011 4:52:00 PM vereshchaka $
 *
 */
public class Folder extends AbstractTestModule
{

   public interface Locators
   {
      String VIEW_LOCATOR = "//div[@view-id='ideCreateFolderForm']";

      String INPUT_FIELD_NAME = "ideCreateFolderFormNameField";

      String CREATE_BUTTON_ID = "ideCreateFolderFormCreateButton";

      String CANCEL_BUTTON_ID = "ideCreateFolderFormCancelButton";
   }

   @FindBy(id = Locators.VIEW_LOCATOR)
   WebElement view;

   @FindBy(name = Locators.INPUT_FIELD_NAME)
   WebElement nameField;

   @FindBy(id = Locators.CREATE_BUTTON_ID)
   WebElement createButton;

   @FindBy(id = Locators.CANCEL_BUTTON_ID)
   WebElement cancelButton;

   public void waitOpened() throws Exception
   {
      new WebDriverWait(driver(), 2).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            return view != null && view.isDisplayed();
         }
      });
   }
   
   public void waitClosed() throws Exception
   {
      new WebDriverWait(driver(), 2).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            return view == null;
         }
      });
   }

   public void typeFolderName(String name) throws InterruptedException
   {
      IDE().INPUT.typeToElement(nameField, name, true);
   }

   public void clickCreateButton() throws Exception
   {
      createButton.click();
   }
   
   public void clickCancelButton() throws Exception
   {
      cancelButton.click();
   }
   
   public void createFolder(String name) throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.File.FILE, MenuCommands.New.NEW, MenuCommands.New.FOLDER);
      waitOpened();
      typeFolderName(name);
      clickCreateButton();
      waitClosed();
   }
}
