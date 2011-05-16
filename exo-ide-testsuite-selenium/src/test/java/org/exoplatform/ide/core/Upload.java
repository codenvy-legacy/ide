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
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.MenuCommands;

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

   //---------- Id of elements, needed in this test ------------------
   /**
    * Id of upload form div.
    */
   public static final String UPLOAD_FORM_ID = "ideUploadForm";

   public static final String BROWSER_BUTTON_ID = "ideUploadFormBrowseButton";

   public static final String FILE_NAME_FIELD_ID = "ideUploadFormFilenameField";

   public static final String MIME_TYPE_FILED_ID = "ideUploadFormMimeTypeField";

   public static final String UPLOAD_BUTTON_ID = "ideUploadFormUploadButton";

   public static final String CANCEL_BUTTON_ID = "ideUploadFormCloseButton";

   //---------- Locators ------------------
   /**
    * Locator for div, that contains upload form.
    */
   public static final String UPLOAD_FORM_LOCATOR = "//div[@id='" + UPLOAD_FORM_ID + "']";

   public static final String INPUT_FILE_FIELD_LOCATOR = "//input[@type='file']";

   private static final String MIME_TYPE_SUGGEST_PANEL_TEXT_LOCATOR =
      "//div[@id=\"exoSuggestPanel\"]//td[contains(., '%1s')]";

   /**
    * Check Upload view is opened.
    */
   public void checkIsOpened()
   {
      assertTrue(selenium().isElementPresent(UPLOAD_FORM_ID));
      assertTrue(selenium().isElementPresent(UPLOAD_BUTTON_ID));
   }

   /**
    * Check the opened state of the Upload view.
    * 
    * @param isOpened if <code>true</code> view is opened
    */
   public void checkIsOpened(boolean isOpened)
   {
      if (isOpened)
      {
         checkIsOpened();
      }
      else
      {
         assertFalse(selenium().isElementPresent(UPLOAD_FORM_ID));
         assertFalse(selenium().isElementPresent(UPLOAD_BUTTON_ID));
      }
   }

   /**
    * Wait for upload view to be opened.
    * 
    * @throws Exception
    */
   public void waitUploadViewOpened() throws Exception
   {
      waitForElementPresent(UPLOAD_FORM_ID);
   }

   /**
    * Wait for upload view to be closed.
    * 
    * @throws Exception
    */
   public void waitUploadViewClosed() throws Exception
   {
      waitForElementNotPresent(UPLOAD_FORM_ID);
   }

   public void open(String formName, String filePath, String mimeType) throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.File.FILE, formName);
      waitForElementPresent(UPLOAD_FORM_ID);

      final String uploadForm = "uploadFormId";
      selenium().assignId(UPLOAD_FORM_LOCATOR, uploadForm);

      assertTrue(selenium().isElementPresent(uploadForm));
      assertTrue(selenium().isElementPresent(BROWSER_BUTTON_ID));

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

      selenium().type(MIME_TYPE_FILED_ID, mimeType);
      assertTrue(selenium().isElementPresent(UPLOAD_BUTTON_ID));

      clickUploadButton();
      waitForElementNotPresent(uploadForm);

      assertFalse(selenium().isElementPresent(uploadForm));
   }

   /**
    * Set path of the file to be uploaded to file's upload input.
    * 
    * @param path file's path
    */
   public void setUploadFilePath(String path)
   {
      selenium().type(INPUT_FILE_FIELD_LOCATOR, path);
   }

   /**
    * Check the mime type suggest panel contains pointed proposes.
    * 
    * @param proposes proposes to be contained
    */
   public void checkMimeTypeContainsProposes(String... proposes)
   {
      for (String propose : proposes)
      {
         String locator = String.format(MIME_TYPE_SUGGEST_PANEL_TEXT_LOCATOR, propose);
         assertTrue(selenium().isElementPresent(locator));
      }
   }

   /**
    * Type text to Mime type field.
    * 
    * @param text text to type
    */
   public void typeToMimeTypeField(String text)
   {
      selenium().type(MIME_TYPE_FILED_ID, "");
      selenium().typeKeys(MIME_TYPE_FILED_ID, text);
   }

   /**
    * Select Mime type propose by name
    * 
    * @param mimetype Mime type to select
    */
   public void selectMimeTypeByName(String mimetype)
   {
      String locator = String.format(MIME_TYPE_SUGGEST_PANEL_TEXT_LOCATOR, mimetype);
      selenium().click(locator);
   }

   /**
    * Get value of Mime type field.
    * 
    * @return {@link String} value
    */
   public String getMimeTypeValue()
   {
      return selenium().getValue(MIME_TYPE_FILED_ID);
   }

   /**
    * Get value of file path field.
    * 
    * @return {@link String} value
    */
   public String getFilePathValue()
   {
      return selenium().getValue(FILE_NAME_FIELD_ID);
   }

   /**
    * Click upload button.
    */
   public void clickUploadButton()
   {
      selenium().click(UPLOAD_BUTTON_ID);
   }

   /**
    * Click cancel button.
    */
   public void clickCancelButton()
   {
      selenium().click(CANCEL_BUTTON_ID);
   }

   public void openMimeTypesList() throws Exception
   {
      selenium().click("//div[@id='"+UPLOAD_FORM_ID+"']//td/img");
      waitForElementPresent("exoSuggestPanel");
   }
}
