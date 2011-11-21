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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import junit.framework.Assert;

import com.sun.org.apache.xerces.internal.impl.xpath.XPath;

import org.openqa.selenium.By;
import org.openqa.selenium.By.ByXPath;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.selenesedriver.FindElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:mmusienko@exoplatform.com">Musienko Maksim</a>
 * @version $
 */

public class AboutDialog extends AbstractTestModule
{

   //Locators basic elements Dialog About Menu
   interface Locators
   {
      String HELP_MENU_LOCATOR =
         "//table[@class=\"exo-menuBarTable\"]//td[@class=\"exo-menuBarItem\" and text()=\"Help\"]";

      String ABOUT_MENU_LOCATOR =
         "//table[@class=\"exo-popupMenuTable\"]//td[@class=\"exo-popupMenuTitleField\"]/nobr[text()=\"About...\"]";

      String ABOUT_WINDOW_LOCATOR = "ideAboutView-window";

      String OK_BUTTON_DIALOGABOUT = "ideAboutViewOkButton";

      String HELP_MENU = "table.exo-popupMenuTable";

      String INFO_CONTETNT = "//div[@view-id=\"ideAboutView\"]//table/tbody//tr//div[@class=\"gwt-Label\"]";

      String LOGO = "//div[@view-id=\"ideAboutView\"]//table//tbody//tbody//td/img[@class=\"gwt-Image\"]";
   }

   //WebElemnts DialogAbout menu
   @FindBy(xpath = Locators.HELP_MENU_LOCATOR)
   public WebElement help;

   @FindBy(xpath = Locators.ABOUT_MENU_LOCATOR)
   private WebElement about;

   @FindBy(id = Locators.OK_BUTTON_DIALOGABOUT)
   private WebElement okButton;

   @FindBy(xpath = Locators.INFO_CONTETNT)
   private WebElement content;

   @FindBy(xpath = Locators.LOGO)
   private WebElement logo;

   /**
    * Call help menu (with the help of click)
    * @throws Exception
    * 
    */
   public void callHelpMenu() throws Exception
   {
      WebElement check = driver().findElement(By.xpath(Locators.HELP_MENU_LOCATOR));
      help.click();
      waitOpenedMenuHelp();
   }

   /**
    * click on About submenu
    * @throws Exception
    */
   public void callAboutMenu() throws Exception
   {
      about.click();
      waitOpenedDialogAbout();
   }

   /**
    * Click on button 'ok' and closing About menu 
    * @throws Exception
    */
   public void closeDialogAbout() throws Exception
   {
      okButton.click();
      waitClosedDialogAbout();
   }

   /**
    * Check basic elements DialogAbout form 
    */
   public void checkAboutWindow()
   {

      logo.isDisplayed();
      String textContentAbout = content.getText();
      Assert.assertTrue(textContentAbout.contains("eXo IDE"));
      Assert.assertTrue(textContentAbout.contains("Version:"));
      Assert.assertTrue(textContentAbout.contains("eXo Platform SAS (c)"));
      Assert.assertTrue(textContentAbout.contains("Revision:"));
      Assert.assertTrue(textContentAbout.contains("Build Time:"));

   }

   /**
    * Wait DialogAbout dialog opened.
    * 
    * @throws Exception
    */
   public void waitOpenedDialogAbout() throws Exception
   {
      new WebDriverWait(driver(), 3).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               WebElement view = input.findElement(By.id(Locators.ABOUT_WINDOW_LOCATOR));
               return (view != null && view.isDisplayed());
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   /**
    * Wait opened Help Menu
    * @throws Exception
    */
   public void waitOpenedMenuHelp() throws Exception
   {
      new WebDriverWait(driver(), 3).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               WebElement view = input.findElement(By.cssSelector(Locators.HELP_MENU));
               return (view != null && view.isDisplayed());
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   /**
    * Wait DialogAbout dialog closed.
    * 
    * @throws Exception
    */
   public void waitClosedDialogAbout() throws Exception
   {
      new WebDriverWait(driver(), 2).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               input.findElement(By.id(Locators.ABOUT_WINDOW_LOCATOR));
               return false;
            }
            catch (NoSuchElementException e)
            {
               return true;
            }
         }
      });

   }

}
