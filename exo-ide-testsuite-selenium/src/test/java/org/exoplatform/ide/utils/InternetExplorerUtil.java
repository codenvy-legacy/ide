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
package org.exoplatform.ide.utils;

import org.exoplatform.ide.TestConstants;

import com.thoughtworks.selenium.Selenium;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: 2010
 *
 */
public class InternetExplorerUtil extends AbstractTextUtil
{

   /**
    * @param selenium
    */
   public InternetExplorerUtil(Selenium selenium)
   {
      super(selenium);
   }

   /**
    * @see org.exoplatform.ide.utils.AbstractTextUtil#typeTextToEditor(java.lang.String)
    */
   @Override
   public void typeTextToEditor(String locator, String text) throws Exception
   {
      selenium.type(locator, text);

      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   /**
    * @see org.exoplatform.ide.utils.AbstractTextUtil#typeTextToInput(java.lang.String, java.lang.String)
    */
   @Override
   public void typeTextToInput(String locator, String text) throws Exception
   {
      selenium.type(locator, text);
   }
}
