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

import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.Locators;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Output May 11, 2011 5:05:08 PM evgen $
 *
 */
public class Output extends AbstractTestModule
{

   /**
    * 
    */
   private static final String IDE_OUTPUT_CONTENT_DIV_1S = "//div[@id='ideOutputContent']/div[%1s]/";

   /**
    * Get Output message text
    * @param messageNumber Number of message. <b>Message count starts with 1 !</b>
    * @return Text of output message 
    */
   public String getOutputMessageText(int messageNumber)
   {
      String locator = String.format(IDE_OUTPUT_CONTENT_DIV_1S, messageNumber);
      return selenium().getText(locator);
   }

   /**
    * Check is Output form opened 
    */
   public void checkOutputOpened()
   {
      assertTrue(selenium().isElementPresent(Locators.OperationForm.OUTPUT_FORM_LOCATOR));
   }

   public void waitForOutputOpened() throws Exception
   {
      waitForElementPresent("ideOutputContent");
   }

   /**
    *  Wait for Output message present
    * @param messageIndex of message. <b>Message count starts with 1 !</b>
    * @throws Exception 
    */
   public void waitForMessageShow(int messageIndex) throws Exception
   {
      String locator = String.format(IDE_OUTPUT_CONTENT_DIV_1S, messageIndex);
      waitForElementPresent(locator);
   }
   
   /**
    * Click on error message, pointed by its position in output panel.
    * The index starts from 1.
    * 
    * @param messageNumber number of the message
    */
   public void clickOnErrorMessage(int messageNumber)
   {
      String locator = String.format(IDE_OUTPUT_CONTENT_DIV_1S, messageNumber) + "/font/span";
      selenium().click(locator);
   }
   
}
