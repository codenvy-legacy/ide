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
   
   private void checkFindReplaceFormAppeared()
   {
      assertTrue(selenium().isElementPresent("ideFindReplaceTextView"));
      assertTrue(selenium().isElementPresent("ideFindReplaceTextFormFindButton"));
      assertTrue(selenium().isElementPresent("ideFindReplaceTextFormReplaceButton"));
      assertTrue(selenium().isElementPresent("ideFindReplaceTextFormReplaceFindButton"));
      assertTrue(selenium().isElementPresent("ideFindReplaceTextFormReplaceAllButton"));
      assertTrue(selenium().isElementPresent("ideFindReplaceTextFormCancelButton"));
      assertTrue(selenium()
         .isElementPresent("ideFindReplaceTextFormFindField"));
      assertTrue(selenium()
         .isElementPresent("ideFindReplaceTextFormReplaceField"));
      assertTrue(selenium()
         .isElementPresent("ideFindReplaceTextFormCaseSensitiveField"));
      // Check buttons state
      assertFalse(isButtonEnabled("Find"));
      assertFalse(isButtonEnabled("Replace/Find"));
      assertFalse(isButtonEnabled("Replace"));
      assertFalse(isButtonEnabled("Replace All"));
      assertTrue(isButtonEnabled("Cancel"));
   }   

   
   private boolean isButtonEnabled(String title)
   {
      if (selenium().isElementPresent("//div[@class='windowBody']//td[@class='buttonTitle' and text()='" + title + "']"))
      {
         return true;
      }
      else if (selenium().isElementPresent("//div[@class='windowBody']//td[@class='buttonTitleOver' and text()='" + title
         + "']"))
      {
         return true;
      }
      else if (selenium().isElementPresent("//div[@class='windowBody']//td[@class='buttonTitleDisabled' and text()='"
         + title + "']"))
      {
         return false;
      }
      return false;
   }
   
   
   
   
}
