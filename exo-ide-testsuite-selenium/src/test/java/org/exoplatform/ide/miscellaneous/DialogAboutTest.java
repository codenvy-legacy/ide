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
package org.exoplatform.ide.miscellaneous;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class DialogAboutTest extends BaseTest
{

   private static final String COMPANY = "2009-2010 eXo Platform SAS (c)";

   private static final String PRODUCT_NAME = "eXo IDE";

   @Test
   public void testDialogAbout() throws Exception
   {
      //TODO: you can change information in About window
      //as you see fit
      Thread.sleep(TestConstants.SLEEP);
      IDE.menu().runCommand(MenuCommands.Help.HELP, MenuCommands.Help.ABOUT);
      //      Thread.sleep(TestConstants.SLEEP);
      //check About form
      assertTrue(selenium.isElementPresent("//div[@view-id=\"ideAboutView\"]"));
      assertTrue(selenium.isElementPresent("ideAboutFormOkButton"));
      assertTrue(selenium.isElementPresent("//div[@view-id=\"ideAboutView\"]//td//img[@class=\"gwt-Image\"]"));
      assertTrue(selenium.isTextPresent(PRODUCT_NAME));
      assertTrue(selenium.isTextPresent(COMPANY));
      assertTrue(selenium.isTextPresent("Revision:"));
      assertTrue(selenium.isTextPresent("Build Time:"));

      //click Ok button
      selenium.click("ideAboutFormOkButton");
      waitForElementNotPresent("ideAboutFormOkButton");

      //check About form dissapeared
      assertFalse(selenium.isElementPresent("//div[@view-id=\"ideAboutView\"]"));
      assertFalse(selenium.isElementPresent("ideAboutFormOkButton"));
      assertFalse(selenium.isElementPresent("//div[@view-id=\"ideAboutView\"]//td//img[@class=\"gwt-Image\"]"));
      assertFalse(selenium.isTextPresent("About"));
      assertFalse(selenium.isTextPresent(PRODUCT_NAME));
      //      assertFalse(selenium.isTextPresent("Version: 1.0-Beta03-SNAPSHOT"));
      assertFalse(selenium.isTextPresent(COMPANY));
      assertFalse(selenium.isTextPresent("Revision:"));
      assertFalse(selenium.isTextPresent("Build Time:"));
   }
}
