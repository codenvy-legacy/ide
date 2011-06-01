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

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Musienko Maksim</a>
 * @version $
 */

public class GoToLine extends AbstractTestModule
{
   public static final String GO_TO_LINE_FORM_ID = "ideGoToLineForm";
   
   public static final String GO_TO_LINE_BUTTON_ID = "ideGoToLineFormGoButton";
   
   public static final String CANCEL_BUTTON_ID = "ideGoToLineFormCancelButton";

   /**
    * check Go To LineForm present
    */
   public void checkAppearGoToLineForm()
   {
      assertTrue(selenium().isElementPresent(GO_TO_LINE_FORM_ID));
      assertTrue(selenium().isElementPresent(GO_TO_LINE_BUTTON_ID));
      assertTrue(selenium().isElementPresent(CANCEL_BUTTON_ID));
   }
   
   public void waitForGoToLineForm() throws Exception
   {
      waitForElementPresent(GO_TO_LINE_FORM_ID);
   }

   /**
    * check "GoToLineForm" label on form with string range.
    * @param label
    */
   public void checkLineNumberLabel(String label)
   {
      assertTrue(selenium().isElementPresent(
         "//form[@id='ideGoToLineFormDynamicForm']/div/nobr/span[text()=" + "'" + label + "'" + "]"));
   }

   public void typeIntoGoToLineFormField(String typeText)
   {
      selenium().type("ideGoToLineFormLineNumberField", typeText);
   }

   /**
    * clicks on go button of form. Checks closing form
    * @throws InterruptedException 
    */
   public void pressGoButtonWithCorrectValue() throws InterruptedException
   {
      selenium().click("ideGoToLineFormGoButton");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      assertFalse(selenium().isElementPresent(GO_TO_LINE_FORM_ID));
      assertFalse(selenium().isElementPresent("exoWarningDialogOkButton"));
      assertFalse(selenium().isElementPresent("//div[@id='" + GO_TO_LINE_FORM_ID + "']//div/img[@title='Close']"));
      assertFalse(selenium().isElementPresent("ideGoToLineFormGoButton"));
      assertFalse(selenium().isElementPresent("ideGoToLineFormCancelButton"));
   }

   /**
    * clicks on cancel button of form. Checks closing form
    * @throws InterruptedException 
    */
   public void pressCancelButtonWithCorrectValue() throws InterruptedException
   {
      selenium().click("ideGoToLineFormCancelButton");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      assertFalse(selenium().isElementPresent(GO_TO_LINE_FORM_ID));
      assertFalse(selenium().isElementPresent("exoWarningDialogOkButton"));
      assertFalse(selenium().isElementPresent("//div[@id='" + GO_TO_LINE_FORM_ID + "']//div/img[@title='Close']"));
      assertFalse(selenium().isElementPresent("ideGoToLineFormGoButton"));
      assertFalse(selenium().isElementPresent("ideGoToLineFormCancelButton"));
   }

   /**
    * press on button not check disappear form
    * @throws InterruptedException
    */
   public void pressCancelButton() throws InterruptedException
   {
      selenium().click("ideGoToLineFormCancelButton");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

   }

   /**
    * press on button not check disappear form
    * @throws InterruptedException
    */
   public void pressGoButton() throws InterruptedException
   {
      selenium().click("ideGoToLineFormGoButton");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

   }

}
