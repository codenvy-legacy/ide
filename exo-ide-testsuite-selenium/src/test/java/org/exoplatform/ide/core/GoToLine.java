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

   /**
    * check Go To LineForm present
    */
   public void checkAppearGoToLineForm()
   {

      assertTrue(selenium().isElementPresent("ideGoToLineForm"));
      assertTrue(selenium().isElementPresent("//div[@id='ideGoToLineForm']//div/img[@title='Close']"));
      assertTrue(selenium().isElementPresent("ideGoToLineFormGoButton"));
      assertTrue(selenium().isElementPresent("ideGoToLineFormCancelButton"));
   }

   public void checkGoToLineFormNotAppeared()
   {
      assertFalse(selenium().isElementPresent("ideGoToLineForm"));
   }

   /**
    * check warning message with label of message. If input invalid parameter
    * @param warnmessage
    */
   public void checkAppearExoWarningDialogGoToLineForm(String warnmessage)
   {

      assertTrue(selenium().isElementPresent("exoWarningDialog"));
      assertTrue(selenium().isElementPresent(
         "//div[@id='exoWarningDialog']//div[@class='Caption']/span[text()=\"Error\"]"));
      assertTrue(selenium().isElementPresent(
         "//div[@id='exoWarningDialog']//table//td//div[@class='gwt-Label' and text()=" + "\"" + warnmessage + "\""
            + "]"));
   }

   /**
    * close WarningDialog and check disappear
    * @throws InterruptedException
    */
   public void closeExoWarningDialogGoToLineForm() throws InterruptedException
   {
      selenium().click("exoWarningDialogOkButton");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      assertFalse(selenium().isElementPresent("exoWarningDialog"));
      assertFalse(selenium().isElementPresent(
         "//div[@id='exoWarningDialog']//div[@class='Caption']/span[text()=\"Error\"]"));
      assertFalse(selenium().isElementPresent("exoWarningDialogOkButton"));
   }

   /**
    * check if all ok and warning massage not appear 
    */
   public void checkNotAppearExoWarningDialogGoToLineForm()
   {

      assertFalse(selenium().isElementPresent("exoWarningDialog"));
      assertFalse(selenium().isElementPresent(
         "//div[@id='ideGoToLineForm']//div[@class='Caption']/span[text()='Go to Line']"));
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
      assertFalse(selenium().isElementPresent("ideGoToLineForm"));
      assertFalse(selenium().isElementPresent("exoWarningDialogOkButton"));
      assertFalse(selenium().isElementPresent("//div[@id='ideGoToLineForm']//div/img[@title='Close']"));
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
      assertFalse(selenium().isElementPresent("ideGoToLineForm"));
      assertFalse(selenium().isElementPresent("exoWarningDialogOkButton"));
      assertFalse(selenium().isElementPresent("//div[@id='ideGoToLineForm']//div/img[@title='Close']"));
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
