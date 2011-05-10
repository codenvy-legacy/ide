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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.exoplatform.ide.ToolbarCommands;

/**
 * Search component.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  May 10, 2011 9:54:12 AM anya $
 *
 */
public class Search extends AbstractTestModule
{
   private static final String SEARCH_VIEW_ID = "ideSearchView";

   private static final String SEARCH_RESULTS_VIEW_ID = "ideSearchResultView";

   private static final String PATH_FIELD_ID = "ideSearchFormPathField";

   private static final String CONTAINING_TEXT_FIELD_ID = "ideSearchFormContentField";

   private static final String MIME_TYPE_FIELD_ID = "ideSearchFormMimeTypeField";

   private static final String SEARCH_BUTTON_ID = "ideSearchFormSearchButton";

   private static final String CANCEL_BUTTON_ID = "ideSearchFormCancelButton";

   public static final String SEARCH_RESULT_TREE = "ideSearchResultItemTreeGrid";

   /**
    * Check the search view with it's elements is shown(visible).
    * 
    * @throws Exception
    */
   public void checkSearchViewVisible() throws Exception
   {
      waitForElementPresent("//div[@view-id=\"" + SEARCH_VIEW_ID + "\"]");
      assertTrue(selenium().isElementPresent(PATH_FIELD_ID));
      assertTrue(selenium().isElementPresent(CONTAINING_TEXT_FIELD_ID));
      assertTrue(selenium().isElementPresent(MIME_TYPE_FIELD_ID));
      assertTrue(selenium().isElementPresent(SEARCH_BUTTON_ID));
      assertTrue(selenium().isElementPresent(CANCEL_BUTTON_ID));
   }

   /**
    * Check the search view with it's elements is not shown(hidden).
    * 
    * @throws Exception
    */
   public void checkSearchViewClosed() throws Exception
   {
      waitForElementNotPresent("//div[@view-id=\"" + SEARCH_VIEW_ID + "\"]");
      assertFalse(selenium().isElementPresent(PATH_FIELD_ID));
      assertFalse(selenium().isElementPresent(CONTAINING_TEXT_FIELD_ID));
      assertFalse(selenium().isElementPresent(MIME_TYPE_FIELD_ID));
      assertFalse(selenium().isElementPresent(SEARCH_BUTTON_ID));
      assertFalse(selenium().isElementPresent(CANCEL_BUTTON_ID));
   }

   /**
    * Get the value(content) of path field.
    * 
    * @return String path field's value
    */
   public String getPathFieldValue()
   {
      return selenium().getValue(PATH_FIELD_ID);
   }
   
   /**
    * Get the value(content) of containing text field.
    * 
    * @return String containing text field's value
    */
   public String getContainingTextFieldValue()
   {
      return selenium().getValue(CONTAINING_TEXT_FIELD_ID);
   }
   
   /**
    * Get the value(content) of Mime type field.
    * 
    * @return String Mime type field's value
    */
   public String getMimeTypeFieldValue()
   {
      return selenium().getValue(MIME_TYPE_FIELD_ID);
   }

   /**
    * Type text into path field.
    * 
    * @param value value to type
    */
   public void typePathFieldValue(String value)
   {
      selenium().type(PATH_FIELD_ID, value);
   }
   
   /**
    * Type text into containing text field.
    * 
    * @param value value to type
    */
   public void typeContainingTextFieldValue(String value)
   {
      selenium().type(CONTAINING_TEXT_FIELD_ID, value);
   }
   
   /**
    * Type text in Mime type field.
    * 
    * @param value value to type
    */
   public void typeMimiTypeFieldValue(String value)
   {
      selenium().type(MIME_TYPE_FIELD_ID, value);
   }

   /**
    * Click on Search button.
    */
   public void clickSearchButton()
   {
      selenium().click(SEARCH_BUTTON_ID);
   }
   
   /**
    * Click on Cancel button.
    */
   public void clickCancelButton()
   {
      selenium().click(CANCEL_BUTTON_ID);
   }

   /**
    * Performs search from clicking the control to showing the results panel.
    * 
    * @param checkPath check path of the search
    * @param text text to search
    * @param mimeType Mime type
    * @throws Exception
    */
   public void performSearch(String checkPath, String text, String mimeType) throws Exception
   {
      IDE().TOOLBAR.runCommand(ToolbarCommands.File.SEARCH);
      checkSearchViewVisible();

      assertEquals(checkPath, getPathFieldValue());
      typeContainingTextFieldValue(text);
      typeMimiTypeFieldValue(mimeType);

      clickSearchButton();
      waitForElementNotPresent("//div[@view-id=\"" + SEARCH_VIEW_ID + "\"]");
   }

   /**
    * Wait for search results view to appear.
    * 
    * @throws Exception
    */
   public void waitSearchResultsPresent() throws Exception
   {
      waitForElementPresent("//div[@view-id=\"" + SEARCH_RESULTS_VIEW_ID + "\"]");
   }

   /**
    * Get the number of found results.
    * 
    * @return number of the found results
    */
   public int getResultsCount()
   {
      return selenium().getXpathCount("//div[@id='" + SEARCH_RESULT_TREE + "']//div[@class='ide-Tree-label']")
         .intValue() - 1;
   }
}
