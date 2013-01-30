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

import java.io.File;

import org.exoplatform.ide.MenuCommands;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Operations with Upload (Open Local File) dialogs: open dialog, upload file.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Upload.java May 5, 2011 11:57:54 AM vereshchaka $
 * 
 */
public class Upload extends AbstractTestModule
{
   /**
    * Form names, that are available for uploading: upload file, upload ziiped folder or open local file.
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

      String OPEN_LOCAL_FILE_VIEW_ID = "ideOpenLocalFile";

      String OPEN_LOCAL_FILE_VIEW_LOCATOR = "//div[@view-id='" + OPEN_LOCAL_FILE_VIEW_ID + "']";

      String FILE_NAME_FIELD_ID = "ideUploadFormFilenameField";

      String MIME_TYPE_FIELD_ID = "ideUploadFormMimeTypeField";

      String UPLOAD_BUTTON_ID = "ideUploadFormUploadButton";

      String CANCEL_BUTTON_ID = "ideUploadFormCloseButton";

      String FILE_FIELD_LOCATOR = "//input[@type='file']";
   }

   @FindBy(xpath = Locators.VIEW_LOCATOR)
   WebElement view;

   @FindBy(xpath = Locators.OPEN_LOCAL_FILE_VIEW_LOCATOR)
   WebElement openLocalFileView;

   @FindBy(how = How.ID_OR_NAME, using = Locators.FILE_NAME_FIELD_ID)
   WebElement fileNameField;

   @FindBy(name = Locators.MIME_TYPE_FIELD_ID)
   WebElement mimeTypeField;

   @FindBy(id = Locators.UPLOAD_BUTTON_ID)
   WebElement uploadButton;

   @FindBy(id = Locators.CANCEL_BUTTON_ID)
   WebElement cancelButton;

   @FindBy(xpath = Locators.FILE_FIELD_LOCATOR)
   WebElement fileField;

   /**
    * Wait Upload view opened.
    * 
    * @throws Exception
    */
   public void waitOpened() throws Exception
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               return isOpened();
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
      new WebDriverWait(driver(), 30).until(ExpectedConditions.invisibilityOfElementLocated(By
         .id(Locators.VIEW_LOCATOR)));
   }

   public boolean isOpened()
   {
      try
      {
         return (view != null && view.isDisplayed() && uploadButton != null && uploadButton.isDisplayed()
            && cancelButton != null && cancelButton.isDisplayed() && fileNameField != null && fileNameField
               .isDisplayed());
      }
      catch (Exception e)
      {
         return false;
      }
   }

   /**
    * Wait Upload view opened.
    * 
    * @throws Exception
    */
   public void waitOpenLocalFileViewOpened() throws Exception
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            return isOpenLocalFileViewOpened();
         }
      });
   }

   /**
    * Wait Open local file view closed.
    * 
    * @throws Exception
    */
   public void waitOpenLocalFileViewClosed() throws Exception
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               input.findElement(By.xpath(Locators.OPEN_LOCAL_FILE_VIEW_LOCATOR));
               return false;
            }
            catch (NoSuchElementException e)
            {
               return true;
            }
         }
      });
   }

   public boolean isOpenLocalFileViewOpened()
   {
      try
      {
         return (openLocalFileView != null && openLocalFileView.isDisplayed() && uploadButton != null
            && uploadButton.isDisplayed() && cancelButton != null && cancelButton.isDisplayed()
            && fileNameField != null && fileNameField.isDisplayed());
      }
      catch (Exception e)
      {
         e.printStackTrace();
         return false;
      }
   }

   public void open(String formName, String filePath, String mimeType) throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.File.FILE, formName);

      if (FormName.OPEN_LOCAL_FILE.equals(formName))
      {
         waitOpenLocalFileViewOpened();
      }
      else
      {
         waitOpened();
      }
      try
      {
         File file = new File(filePath);
         setUploadFilePath(file.getAbsolutePath());
      }
      catch (Exception e)
      {
      }

      final String fileName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());

      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            return fileName.equals(getFilePathValue());
         }
      });

      setMimeType(mimeType);
      clickUploadButton();

      if (FormName.OPEN_LOCAL_FILE.equals(formName))
      {
         waitOpenLocalFileViewClosed();
      }
      else
      {
         waitClosed();
      }
   }

   /**
    * Set path of the file to be uploaded to file's upload input.
    * 
    * @param path file's path
    * @throws InterruptedException
    */
   public void setUploadFilePath(String path) throws InterruptedException
   {
      IDE().INPUT.typeToElement(fileField, path);
   }

   /**
    * Wait the mime-type suggest panel contains pointed proposes.
    * 
    * @param proposes proposes to be contained
    */
   public void waitMimeTypeContainsProposes(final String... proposes)
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
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
      });
   }

   /**
    * Wait the mime-type suggest panel  not contains pointed proposes.
    * 
    * @param proposes proposes to be contained
    */
   public void waitMimeTypeNotContainsProposes(final String... proposes)
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            for (String propose : proposes)
            {
               if (!IDE().INPUT.isComboboxValuePresent(mimeTypeField, propose))
               {
                  return true;
               }
            }
            return false;
         }
      });
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

   public void typeToMimeTypeField(String mimeType) throws InterruptedException
   {
      IDE().INPUT.typeToElement(mimeTypeField, mimeType, true);
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
      return IDE().INPUT.getValue(mimeTypeField);
   }

   /**
    * Get value of file path field.
    * 
    * @return {@link String} value
    */
   public String getFilePathValue()
   {
      return IDE().INPUT.getValue(fileNameField);
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
    * Returns enabled state of upload button.
    * 
    * @return {@link Boolean} enabled state
    */
   public boolean isUploadButtonEnabled()
   {
      return IDE().BUTTON.isButtonEnabled(uploadButton);
   }
}
