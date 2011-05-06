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
import org.exoplatform.ide.Utils;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Musienko Maksim</a>
 * @version $
 */

public class FindReplace extends AbstractTestModule
{

   public void checkFindReplaceFormAppeared()
   {
      assertTrue(selenium().isElementPresent("//div[@view-id='ideFindReplaceTextView']"));
      assertTrue(selenium().isElementPresent("ideFindReplaceTextFormFindButton"));
      assertTrue(selenium().isElementPresent("ideFindReplaceTextFormReplaceButton"));
      assertTrue(selenium().isElementPresent("ideFindReplaceTextFormReplaceFindButton"));
      assertTrue(selenium().isElementPresent("ideFindReplaceTextFormReplaceAllButton"));
      assertTrue(selenium().isElementPresent("ideFindReplaceTextFormCancelButton"));
      assertTrue(selenium().isElementPresent("ideFindReplaceTextFormFindField"));
      assertTrue(selenium().isElementPresent("ideFindReplaceTextFormReplaceField"));
      assertTrue(selenium().isElementPresent("ideFindReplaceTextFormCaseSensitiveField"));
      // Check buttons state

      checkStateButtonReplaseFind(false);
      checkStateButtonReplaseFind(false);
      checkStateButtonReplace(false);
      checkStateButtonReplaceAll(false);
      checkStateButtonCancel(true);

      //      assertFalse(isButtonEnabled("Find"));
      //      assertFalse(isButtonEnabled("Replace/Find"));
      //      assertFalse(isButtonEnabled("Replace"));
      //      assertFalse(isButtonEnabled("Replace All"));
      //      assertTrue(isButtonEnabled("Cancel"));
   }

   /**
    * check state Find button. If param true button enabled. If param false button disabled. 
    * @param state
    */
   public void checkStateButtonEnabledFind(boolean state)
   {
      if (state == false)
      {
         assertTrue(selenium().isElementPresent(
            "//div[@id='ideFindReplaceTextFormFindButton' and @button-enabled='false']"));
      }
      else if (state == true)
      {
         assertTrue(selenium().isElementPresent(
            "//div[@id='ideFindReplaceTextFormFindButton' and @button-enabled='true']"));
      }
      else
      {
         assertTrue(false);
      }

   }

   /**
    * check state Replace button. If param true button enabled. If param false button disabled. 
    * @param state
    */
   public void checkStateButtonReplace(boolean state)
   {
      if (state == false)
      {
         assertTrue(selenium().isElementPresent(
            "//div[@id='ideFindReplaceTextFormReplaceButton' and @button-enabled='false']"));
      }
      else if (state == true)
      {
         assertTrue(selenium().isElementPresent(
            "//div[@id='ideFindReplaceTextFormReplaceButton' and @button-enabled='true']"));
      }
      else
      {
         assertTrue(false);
      }
   }

   /**
    * check state Find button. If param true button enabled. If param false button disabled. 
    * @param state
    */
   public void checkStateButtonReplaseFind(boolean state)
   {
      if (state == false)
      {
         assertTrue(selenium().isElementPresent(
            "//div[@id='ideFindReplaceTextFormReplaceFindButton' and @button-enabled='false']"));
      }
      else if (state == true)
      {
         assertTrue(selenium().isElementPresent(
            "//div[@id='ideFindReplaceTextFormReplaceFindButton' and @button-enabled='true']"));
      }
      else
      {
         assertTrue(false);
      }

   }

   /**
    * check state ReplaceAll button. If param true button enabled. If param false button disabled. 
    * @param state
    */
   public void checkStateButtonReplaceAll(boolean state)
   {
      if (state == false)
      {
         assertTrue(selenium().isElementPresent(
            "//div[@id='ideFindReplaceTextFormReplaceAllButton' and @button-enabled='false']"));
      }
      else if (state == true)
      {
         assertTrue(selenium().isElementPresent(
            "//div[@id='ideFindReplaceTextFormReplaceAllButton' and @button-enabled='true']"));
      }
      else
      {
         assertTrue(false);
      }

   }

   /**
    * check state Cancel button. If param true button enabled. If param false button disabled. 
    * @param state
    */
   public void checkStateButtonCancel(boolean state)
   {
      if (state == false)
      {
         assertTrue(selenium().isElementPresent(
            "//div[@id='ideFindReplaceTextFormCancelButton' and @button-enabled='false']"));
      }
      else if (state == true)
      {
         assertTrue(selenium().isElementPresent(
            "//div[@id='ideFindReplaceTextFormCancelButton' and @button-enabled='true']"));
      }
      else
      {
         assertTrue(false);
      }

   }

   public void typeWordInFindField(String type) throws InterruptedException
   {
      selenium().typeKeys("ideFindReplaceTextFormFindField", type);
      Thread.sleep(TestConstants.SLEEP_SHORT);
   }

   public void checkFindFieldNotEmptyState()
   {
      checkStateButtonEnabledFind(true);
      checkStateButtonReplaceAll(true);
      checkStateButtonReplace(false);
      checkStateButtonReplaseFind(false);
      checkStateButtonCancel(true);
   }

   public void clickOnFindButton() throws InterruptedException
   {
      selenium().click("ideFindReplaceTextFormFindButton");
      Thread.sleep(TestConstants.SLEEP_SHORT);
   }

   /**
    * Check buttons when text is found.
    */
   private void checkTextFoundState()
   {
      checkStateButtonEnabledFind(true);
      checkStateButtonReplaceAll(true);
      checkStateButtonReplace(true);
      checkStateButtonReplaseFind(true);
      checkStateButtonCancel(true);
   }
      
      
      //      assertTrue(isButtonEnabled("Find"));
      //      assertTrue(isButtonEnabled("Replace All"));
      //      assertTrue(isButtonEnabled("Replace"));
      //      assertTrue(isButtonEnabled("Replace/Find"));
      //      assertTrue(isButtonEnabled("Cancel"));
      //      assertEquals("", getFindResultText());
   

   /**
    * Check buttons when text is not found.
    */
   private void checkTextNotFoundState()
   {
      //      assertTrue(isButtonEnabled("Find"));
      //      assertTrue(isButtonEnabled("Replace All"));
      //      assertFalse(isButtonEnabled("Replace"));
      //      assertFalse(isButtonEnabled("Replace/Find"));
      //      assertTrue(isButtonEnabled("Cancel"));
      //      assertEquals(NOT_FOUND, getFindResultText());
   }

}
