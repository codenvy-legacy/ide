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

import static org.junit.Assert.fail;

import org.exoplatform.ide.IDE;

import com.thoughtworks.selenium.Selenium;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public abstract class AbstractTestModule
{

   protected Selenium selenium()
   {
      return IDE.getInstance().getSelenium();
   }
   
   protected IDE IDE()
   {
      return IDE.getInstance();
   }

   /**
    * Wait while element present.
    * 
    * @param locator - element locator
    * @throws Exception
    */
   protected void waitForElementPresent(String locator) throws Exception
   {
      int WAITING_MAX_SECONDS = 10;

      for (int second = 0;; second++)
      {
         if (second >= WAITING_MAX_SECONDS * 10)
         {
            fail("timeout for element " + locator);
         }

         if (selenium().isElementPresent(locator))
         {
            break;
         }

         Thread.sleep(100);
      }
   }

}
