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

import org.exoplatform.ide.MenuCommands;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;

/**
 * Operations with Upload (Open Local File) dialogs:
 * open dialog, upload file.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Upload.java May 5, 2011 11:57:54 AM vereshchaka $
 *
 */
public class Upload extends AbstractTestModule
{
   /**
    * Form names, that are available for uploading:
    * upload file, upload ziiped folder or open local file.
    */
   public interface FormName
   {
      /**
       * Form for uploading single file.
       */
      public static final String UPLOAD_FILE = "Upload File...";

      /**
       * Form for uploading zipped folder.
       */
      public static final String UPLOAD_FOLDER = "Upload Zipped Folder...";

      /**
       * Form for opening local file in editor as new file.
       */
      public static final String OPEN_LOCAL_FILE = "Open Local File...";
   }

   private interface Locators
   {
      String VIEW_ID = "ideUploadForm";

      String VIEW_LOCATOR = "//div[@view-id='" + VIEW_ID + "']";

      String BROWSER_BUTTON_ID = "ideUploadFormBrowseButton";

      String FILE_NAME_FIELD_ID = "ideUploadFormFilenameField";

      String MIME_TYPE_FIELD_ID = "ideUploadFormMimeTypeField";

      String UPLOAD_BUTTON_ID = "ideUploadFormUploadButton";

      String CANCEL_BUTTON_ID = "ideUploadFormCloseButton";

      String FILE_FIELD_LOCATOR = "//input[@type='file']";
   }

   @FindBy(xpath = Locators.VIEW_LOCATOR)
   WebElement view;

   @FindBy(name = Locators.FILE_NAME_FIELD_ID)
   WebElement fileNameField;

   @FindBy(name = Locators.MIME_TYPE_FIELD_ID)
   WebElement mimeTypeField;

   @FindBy(id = Locators.UPLOAD_BUTTON_ID)
   WebElement uploadButton;

   @FindBy(id = Locators.CANCEL_BUTTON_ID)
   WebElement cancelButton;

   @FindBy(id = Locators.BROWSER_BUTTON_ID)
   WebElement browserButton;

   @FindBy(xpath = Locators.FILE_FIELD_LOCATOR)
   WebElement fileField;

   /**
    * Wait Upload view opened.
    * 
    * @throws Exception
    */
   public void waitOpened() throws Exception
   {
      new WebDriverWait(driver(), 2).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
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
    * Wait Upload view closed.
    * 
    * @throws Exception
    */
   public void waitClosed() throws Exception
   {
      new WebDriverWait(driver(), 2).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               input.findElement(By.xpath(Locators.VIEW_LOCATOR));
               return false;
            }
            catch (NoSuchElementException e)
            {
               return true;
            }
         }
      });
   }

   public boolean isOpened()
   {
      return (view != null && view.isDisplayed() && browserButton != null && browserButton.isDisplayed()
         && uploadButton != null && uploadButton.isDisplayed() && cancelButton != null && uploadButton.isDisplayed()
         && fileNameField != null && fileNameField.isDisplayed());
   }

   public void open(String formName, String filePath, String mimeType) throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.File.FILE, formName);
      waitOpened();
      try
      {
         File file = new File(filePath);
         setUploadFilePath(file.getCanonicalPath());
      }
      catch (Exception e)
      {
      }

      String fileName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
      assertEquals(fileName, getFilePathValue());

      setMimeType(mimeType);
      clickUploadButton();

      waitClosed();
   }

   /**
    * Set path of the file to be uploaded to file's upload input.
    * 
    * @param path file's path
    * @throws InterruptedException 
    */
   public void setUploadFilePath(String path) throws InterruptedException
   {
      IDE().INPUT.typeToElement(fileField, path, true);
   }

   /**
    * Check the mime type suggest panel contains pointed proposes.
    * 
    * @param proposes proposes to be contained
    */
   public boolean isMimeTypeContainsProposes(String... proposes)
   {
      for (String propose : proposes)
      {
         if (!IDE().INPUT.isComboboxValuePresent(mimeTypeField, propose))
         {
            return false;
         }
      }
      return true;
   }

   /**
    * Type text to Mime type field.
    * 
    * @param text text to type
    * @throws InterruptedException 
    */
   public void setMimeType(String mimeType) throws InterruptedException
   {
      IDE().INPUT.setComboboxValue(mimeTypeField, mimeType);
   }

   /**
    * Select Mime type propose by name
    * 
    * @param mimetype Mime type to select
    */
   public void selectMimeTypeByName(String mimetype)
   {
      IDE().INPUT.selectComboboxValue(mimeTypeField, mimetype);
   }

   /**
    * Get value of Mime type field.
    * 
    * @return {@link String} value
    */
   public String getMimeTypeValue()
   {
      return mimeTypeField.getText();
   }

   /**
    * Get value of file path field.
    * 
    * @return {@link String} value
    */
   public String getFilePathValue()
   {
      return fileNameField.getText();
   }

   /**
    * Click Upload button.
    */
   public void clickUploadButton()
   {
      uploadButton.click();
   }

   /**
    * Click Cancel button.
    */
   public void clickCancelButton()
   {
      cancelButton.click();
   }

   /**
    * Returns enabled state of browser button.
    * 
    * @return {@link Boolean} enabled state
    */
   public boolean isBrowserButtonEnabled()
   {
      return IDE().BUTTON.isButtonEnabled(browserButton);
   }

   /**
    * Returns enabled state of upload button.
    * 
    * @return {@link Boolean} enabled state
    */
   public boolean isUploadButtonEnabled()
   {
      return IDE().BUTTON.isButtonEnabled(uploadButton);
   }
}
