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

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public abstract class BaseTest
{
   protected static Selenium selenium;

   @BeforeClass
   public static void startSelenium()
   {
      selenium = new DefaultSelenium("localhost", 4444, "*firefox", "http://127.0.0.1:8888/");
      selenium.start();
      selenium.open("/org.exoplatform.ideall.IDEApplication/IDEApplication.html?gwt.codesvr=127.0.0.1:9997");
      selenium.waitForPageToLoad("10000");
      selenium.windowMaximize();
   }

   @AfterClass
   public static void stopSelenium()
   {
      selenium.stop();
   }
}
