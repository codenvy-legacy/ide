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

import org.exoplatform.ide.TestConstants;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Musienko Maksim</a>
 * @version $
 */

public class FindReplace extends AbstractTestModule
{
   private final static String FIND_REPLACE_VIEW_ID = "ideFindReplaceTextView";

   private final static String FIND_REPLACE_VIEW_LOCATOR = "//div[@view-id='" + FIND_REPLACE_VIEW_ID + "']";

   private final static String FIND_BUTTON_ID = "ideFindReplaceTextFormFindButton";

   private final static String REPLACE_BUTTON_ID = "ideFindReplaceTextFormReplaceButton";

   private final static String REPLACE_FIND_BUTTON_ID = "ideFindReplaceTextFormReplaceFindButton";

   private final static String REPLACE_ALL_BUTTON_ID = "ideFindReplaceTextFormReplaceAllButton";

   private final static String CANCEL_BUTTON_ID = "ideFindReplaceTextFormCancelButton";

   private final static String FIND_FIELD_ID = "ideFindReplaceTextFormFindField";

   private final static String REPLACE_FIELD_ID = "ideFindReplaceTextFormReplaceField";

   private final static String CASE_SENSITIVE_FIELD_ID = "ideFindReplaceTextFormCaseSensitiveField";

   private final static String FIND_RESULT_LABEL_ID = "ideFindReplaceTextFormFindResult";

   private final static String NOT_FOUND_RESULT = "String not found.";

   public void waitForFindReplaceViewOpened() throws Exception
   {
      waitForElementPresent(FIND_REPLACE_VIEW_LOCATOR);
   }

   public void waitForFindReplaceViewClosed() throws Exception
   {
      waitForElementNotPresent(FIND_REPLACE_VIEW_LOCATOR);
   }

   /**
    * Check the state of the find replace form: all elements to be present and the state of buttons.
    */
   public void checkFindReplaceFormAppeared()
   {
      assertTrue(selenium().isElementPresent(FIND_REPLACE_VIEW_LOCATOR));
      assertTrue(selenium().isElementPresent(FIND_BUTTON_ID));
      assertTrue(selenium().isElementPresent(REPLACE_BUTTON_ID));
      assertTrue(selenium().isElementPresent(REPLACE_FIND_BUTTON_ID));
      assertTrue(selenium().isElementPresent(REPLACE_ALL_BUTTON_ID));
      assertTrue(selenium().isElementPresent(CANCEL_BUTTON_ID));
      assertTrue(selenium().isElementPresent(FIND_FIELD_ID));
      assertTrue(selenium().isElementPresent(REPLACE_FIELD_ID));
      assertTrue(selenium().isElementPresent(CASE_SENSITIVE_FIELD_ID));
      // Check buttons state

      assertFalse(isReplaceButtonEnabled());
      assertFalse(isReplaceFindButtonEnabled());
      assertFalse(isFindButtonEnabled());
      assertFalse(isReplaceAllButtonEnabled());
      assertTrue(isCancelButtonEnabled());
   }
   
   public void checkFindReplaceFormNotAppeared()
   {
      assertFalse(selenium().isElementPresent(FIND_REPLACE_VIEW_LOCATOR));
   }
   

   /**
    * Get enabled state of cancel button.
    * 
    * @return boolean enabled state of cancel button
    */
   public boolean isCancelButtonEnabled()
   {
      String attribute = selenium().getAttribute("//div[@id=\"" + CANCEL_BUTTON_ID + "\"]/@button-enabled");
      return Boolean.parseBoolean(attribute);
   }

   /**
    * Get enabled state of find button.
    * 
    * @return boolean enabled state of find button
    */
   public boolean isFindButtonEnabled()
   {
      String attribute = selenium().getAttribute("//div[@id=\"" + FIND_BUTTON_ID + "\"]/@button-enabled");
      return Boolean.parseBoolean(attribute);
   }

   /**
    * Get enabled state of replace button.
    * 
    * @return boolean enabled state of replace button
    */
   public boolean isReplaceButtonEnabled()
   {
      String attribute = selenium().getAttribute("//div[@id=\"" + REPLACE_BUTTON_ID + "\"]/@button-enabled");
      return Boolean.parseBoolean(attribute);
   }

   /**
    * Get enabled state of replace/find button.
    * 
    * @return boolean enabled state of replace/find button
    */
   public boolean isReplaceFindButtonEnabled()
   {
      String attribute = selenium().getAttribute("//div[@id=\"" + REPLACE_FIND_BUTTON_ID + "\"]/@button-enabled");
      return Boolean.parseBoolean(attribute);
   }

   /**
    * Get enabled state of replace all button.
    * 
    * @return boolean enabled state of replace all button
    */
   public boolean isReplaceAllButtonEnabled()
   {
      String attribute = selenium().getAttribute("//div[@id=\"" + REPLACE_ALL_BUTTON_ID + "\"]/@button-enabled");
      return Boolean.parseBoolean(attribute);
   }

   public void typeInFindField(String text) throws InterruptedException
   {

      if (text == "")
      {
         selenium().click(FIND_FIELD_ID);
         selenium().controlKeyDown();
         selenium().keyPress(FIND_FIELD_ID, "A");
         selenium().controlKeyUp();
         selenium().keyDown(FIND_FIELD_ID, "\b");
         selenium().keyUp(FIND_FIELD_ID, "\b");
         return;
      }
      selenium().type(FIND_FIELD_ID, "");
      selenium().typeKeys(FIND_FIELD_ID, text);
   }

   public void typeInReplaceField(String text) throws InterruptedException
   {
      if (text == "")
      {
         selenium().click(REPLACE_FIELD_ID);
         selenium().controlKeyDown();
         selenium().keyPress(REPLACE_FIELD_ID, "A");
         selenium().controlKeyUp();
         selenium().keyPress(REPLACE_FIELD_ID, "\b");
         return;
      }
      selenium().type(REPLACE_FIELD_ID, "");
      selenium().typeKeys(REPLACE_FIELD_ID, text);
   }

   public void checkFindFieldNotEmptyState()
   {
      assertTrue(isFindButtonEnabled());
      assertTrue(isReplaceAllButtonEnabled());
      assertFalse(isReplaceButtonEnabled());
      assertFalse(isReplaceFindButtonEnabled());
      assertTrue(isCancelButtonEnabled());
   }
   
   public void checkFindFieldEmptyState()
   {
      assertTrue(isFindButtonEnabled());
      assertTrue(isReplaceAllButtonEnabled());
      assertFalse(isReplaceButtonEnabled());
      assertFalse(isReplaceFindButtonEnabled());
      assertTrue(isCancelButtonEnabled());
   }

   public void clickFindButton() throws InterruptedException
   {
      selenium().click(FIND_BUTTON_ID);
      Thread.sleep(TestConstants.SLEEP_SHORT);
   }

   public void clickReplaceButton() throws InterruptedException
   {
      selenium().click(REPLACE_BUTTON_ID);
      Thread.sleep(TestConstants.SLEEP_SHORT);
   }

   public void clickReplaceFindButton() throws InterruptedException
   {
      selenium().click(REPLACE_FIND_BUTTON_ID);
      Thread.sleep(TestConstants.SLEEP_SHORT);
   }

   public void clickReplaceAllButton() throws InterruptedException
   {
      selenium().click(REPLACE_ALL_BUTTON_ID);
      Thread.sleep(TestConstants.SLEEP_SHORT);
   }

   public void clickCancelButton() throws InterruptedException
   {
      selenium().click(CANCEL_BUTTON_ID);
      Thread.sleep(TestConstants.SLEEP_SHORT);
   }

   /**
    * Check buttons when text is found.
    */
   public void checkStateWhenTextFound()
   {
      assertTrue(isFindButtonEnabled());
      assertTrue(isReplaceAllButtonEnabled());
      assertTrue(isReplaceButtonEnabled());
      assertTrue(isReplaceFindButtonEnabled());
      assertTrue(isCancelButtonEnabled());
      assertEquals("", getFindResultText());
   }

   /**
    * Check buttons when text is not found.
    */
   public void checkStateWhenTextNotFound()
   {
      assertTrue(isFindButtonEnabled());
      assertTrue(isReplaceAllButtonEnabled());
      assertFalse(isReplaceButtonEnabled());
      assertFalse(isReplaceFindButtonEnabled());
      assertTrue(isCancelButtonEnabled());
      assertEquals(NOT_FOUND_RESULT, getFindResultText());
   }

   public String getFindResultText()
   {
      return selenium().getText(FIND_RESULT_LABEL_ID);
   }

   public void clickCaseSensitiveField()
   {
      selenium().click(CASE_SENSITIVE_FIELD_ID);
   }

}
