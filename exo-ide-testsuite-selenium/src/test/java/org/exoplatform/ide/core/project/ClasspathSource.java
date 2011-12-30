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
package org.exoplatform.ide.core.project;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.Utils;
import org.exoplatform.ide.core.AbstractTestModule;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Class for operations with classpath form (for configuring classpath of project).
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Project.java May 12, 2011 12:35:39 PM vereshchaka $
 *
 */
public class ClasspathSource extends AbstractTestModule
{

   private static final String OK_BUTTON_ID = "ideChooseSourcePathViewOkButton";

   private static final String CANCEL_BUTTON_ID = "ideChooseSourcePathViewCancelButton";

   private static final String SOURCE_VIEW_ID = "//div[@view-id='ideChooseSourcePathView']";

   private static final String TREE_ID = "ideChooseSourcePathViewTreeGrid";

   @FindBy(id = OK_BUTTON_ID)
   private WebElement okButton;

   @FindBy(id = CANCEL_BUTTON_ID)
   private WebElement cancelButton;

   @FindBy(xpath = SOURCE_VIEW_ID)
   private WebElement sourceForm;

   @FindBy(id = TREE_ID)
   private WebElement tree;

   public boolean isOkButtonEnabled()
   {
      return IDE().BUTTON.isButtonEnabled(okButton);
   }

   public boolean isCancelButtonEnabled()
   {
      return IDE().BUTTON.isButtonEnabled(cancelButton);
   }

   public void clickOkButton()
   {
      okButton.click();
   }

   public boolean isTreeVisible()
   {
      return tree != null && tree.isDisplayed();
   }

   public void waitOpened()
   {
      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            return sourceForm != null && sourceForm.isDisplayed();
         }
      });
   }

   public void waitClosed()
   {
      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               input.findElement(By.xpath(SOURCE_VIEW_ID));
               return false;
            }
            catch (NoSuchElementException e)
            {
               return true;
            }
         }
      });
   }

   public boolean isPathPresent(String path) throws Exception
   {
      //String.format(Add.Messages.ADD_FILE, TEST_ADD_FILE)
      return tree.findElement(By.id(getItemId(path))) != null;
   }

   /**
    * Select item in project explorer view.
    * 
    * @param path item's path
    * @throws Exception
    */
   public void selectItem(String path) throws Exception
   {
      WebElement item = tree.findElement(By.id(getItemId(path)));
      new Actions(driver()).moveToElement(item, 1, 1).click().perform();
   }

   public String getItemId(String path) throws Exception
   {
      path = (path.startsWith(BaseTest.WS_URL)) ? path.replace(BaseTest.WS_URL, "") : path;
      String itemId = (path.startsWith("/")) ? path : "/" + path;
      itemId = Utils.md5(itemId);
      //TODO: fix in client item id.
      return "null" + itemId;
   }

   public void waitOkButtonEnabled(boolean enabled)
   {
      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            return IDE().BUTTON.isButtonEnabled(okButton);
         }
      });
   }
}
