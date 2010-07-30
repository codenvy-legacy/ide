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
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class DialogAboutTest extends BaseTest
{
   @Test
   public void testDialogAbout() throws Exception
   {
      Thread.sleep(1000);
      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='Help']", "");
      Thread.sleep(1000);
      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), 'About')]", "");
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideAboutForm\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideAboutFormOkButton\"]"));
      assertTrue(selenium.isTextPresent("eXo IDE"));
      assertTrue(selenium.isTextPresent("Version: 1.0-Beta03-SNAPSHOT"));
      assertTrue(selenium.isTextPresent("2009-2010 eXo Platform SAS (c)"));
      assertTrue(selenium.isTextPresent("Revision"));
      assertTrue(selenium.isTextPresent("Build Time"));
      selenium.click("scLocator=//IButton[ID=\"ideAboutFormOkButton\"]");
      
      Thread.sleep(1000);
      

      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideAboutForm\"]"));
      assertFalse(selenium.isTextPresent("About"));
      assertFalse(selenium.isTextPresent("eXo IDE"));
      assertFalse(selenium.isTextPresent("Version: 1.0-Beta03-SNAPSHOT"));
      assertFalse(selenium.isTextPresent("2009-2010 eXo Platform SAS (c)"));
      assertFalse(selenium.isTextPresent("Revision"));
      assertFalse(selenium.isTextPresent("Build Time"));
   }
}
