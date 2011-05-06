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
      assertTrue(selenium().isElementPresent("ideFindReplaceTextView"));
      assertTrue(selenium().isElementPresent("ideFindReplaceTextFormFindButton"));
      assertTrue(selenium().isElementPresent("ideFindReplaceTextFormReplaceButton"));
      assertTrue(selenium().isElementPresent("ideFindReplaceTextFormReplaceFindButton"));
      assertTrue(selenium().isElementPresent("ideFindReplaceTextFormReplaceAllButton"));
      assertTrue(selenium().isElementPresent("ideFindReplaceTextFormCancelButton"));
      assertTrue(selenium().isElementPresent("ideFindReplaceTextFormFindField"));
      assertTrue(selenium().isElementPresent("ideFindReplaceTextFormReplaceField"));
      assertTrue(selenium().isElementPresent("ideFindReplaceTextFormCaseSensitiveField"));
      // Check buttons state
      //      assertFalse(isButtonEnabled("Find"));
      //      assertFalse(isButtonEnabled("Replace/Find"));
      //      assertFalse(isButtonEnabled("Replace"));
      //      assertFalse(isButtonEnabled("Replace All"));
      //      assertTrue(isButtonEnabled("Cancel"));
   }

   /**
    * @param state
    */
   public void checkStateButtonEnabled(boolean state)
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
    
   
   
   public void checkStateButtonReplaseFind (boolean state)
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
   
   
    public void checkStateButtonReplaceAll(boolean state)
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
   
  
    
    public void checkStateButtonCancel(boolean state)
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

    
    
    
    
   }
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   

}
