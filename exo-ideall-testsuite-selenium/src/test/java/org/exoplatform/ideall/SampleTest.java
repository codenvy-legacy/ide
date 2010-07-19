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
package org.exoplatform.ideall;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
@Test
public class SampleTest
{
   private Selenium selenium;

   @BeforeClass
   public void startSelenium()
   {
      this.selenium = new DefaultSelenium("localhost", 4444, "*firefox", "http://127.0.0.1:8888/");
      this.selenium.start();
   }

   @Test
   public void test1() throws Exception
   {
      selenium.open("http://127.0.0.1:8888/org.exoplatform.ideall.IDEApplication/IDEApplication.html?gwt.codesvr=127.0.0.1:9997");
      Thread.sleep(10000);
     /* selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='File']", "");
      Thread.sleep(3000);
      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), 'Upload...')]", "");
      selenium.mouseDownAt("//div[@class='windowBody']", "");
      selenium.mouseUpAt("//div[@class='windowBody']", "");*/
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]");
      Assert.assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideallUploadForm\"]/body/"));
   }
   
   @AfterClass(alwaysRun = true)
   public void stopSelenium()
   {
      this.selenium.stop();
   }
}