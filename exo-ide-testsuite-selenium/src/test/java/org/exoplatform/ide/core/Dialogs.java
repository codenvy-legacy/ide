/*
 * Copyright (C) 2010 eXo Platform SAS.
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

import org.exoplatform.ide.TestConstants;

import com.thoughtworks.selenium.Selenium;

/**
 * Standard dialogs, such as warning dialogs (error, info, warn) 
 * and operations with them.
 * 
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Dec 29, 2010 $
 *
 */
public class Dialogs
{
   private Selenium selenium;
   
   public interface Locators
   {
      public static final String SC_WARN_DIALOG = "scLocator=//Dialog[ID=\"isc_globalWarn\"]";
      
      public static final String SC_WARN_DIALOG_HEADER = SC_WARN_DIALOG + "/header";
      
      public static final String SC_WARN_DIALOG_YES_BTN = SC_WARN_DIALOG + "/yesButton";
      
      public static final String SC_WARN_DIALOG_NO_BTN = SC_WARN_DIALOG + "/noButton";
      
      public static final String SC_WARN_DIALOG_OK_BTN = SC_WARN_DIALOG + "/okButton";
   }
   
   public Dialogs(Selenium selenium)
   {
      this.selenium = selenium;
   }
   
   /**
    * Check, that warning dialog with two buttons (YES, NO) appeared and 
    * all elements are present.
    * 
    * @param header - the header of dialog (Info, Error etc)
    */
   public void checkTwoBtnDialog(String header)
   {
      assertTrue(selenium.isElementPresent(Dialogs.Locators.SC_WARN_DIALOG));
      assertEquals(header, selenium.getText(Dialogs.Locators.SC_WARN_DIALOG_HEADER));
      assertTrue(selenium.isElementPresent(Dialogs.Locators.SC_WARN_DIALOG_YES_BTN));
      assertTrue(selenium.isElementPresent(Dialogs.Locators.SC_WARN_DIALOG_NO_BTN));
   }
   
   /**
    * Check, that warning dialog with one button (OK) appeared and 
    * all elements are present.
    * 
    * @param header - the header of dialog (Info, Error etc)
    */
   public void checkOneBtnDialog(String header)
   {
      assertTrue(selenium.isElementPresent(Dialogs.Locators.SC_WARN_DIALOG));
      assertEquals(header, selenium.getText(Dialogs.Locators.SC_WARN_DIALOG_HEADER));
      assertTrue(selenium.isElementPresent(Dialogs.Locators.SC_WARN_DIALOG_OK_BTN));
   }
   
   /**
    * Click on Yes button.
    * 
    * @throws Exception
    */
   public void clickYesButton() throws Exception
   {
      selenium.click(Dialogs.Locators.SC_WARN_DIALOG_YES_BTN);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }
   
   /**
    * Click on No button.
    * 
    * @throws Exception
    */
   public void clickNoButton() throws Exception
   {
      selenium.click(Dialogs.Locators.SC_WARN_DIALOG_NO_BTN);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }
   
   /**
    * Click on Ok button.
    * 
    * @throws Exception
    */
   public void clickOkButton() throws Exception
   {
      selenium.click(Dialogs.Locators.SC_WARN_DIALOG_OK_BTN);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }
   
   public void checkTextInDialog(String text)
   {
      final String textInDialog = selenium.getText(Locators.SC_WARN_DIALOG + "/blurb/");
      assertEquals(text, textInDialog);
   }

}
