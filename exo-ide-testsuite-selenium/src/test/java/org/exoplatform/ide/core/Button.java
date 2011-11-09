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

import org.openqa.selenium.WebElement;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Nov 9, 2011 12:09:26 PM anya $
 *
 */
public class Button extends AbstractTestModule
{
   private final String ENABLED_ATTRIBUTE = "button-enabled";

   /**
    * Get the enabled state of the button.
    * 
    * @param button
    * @return enabled state of the button
    */
   public boolean isButtonEnabled(WebElement button)
   {
      return Boolean.parseBoolean(button.getAttribute(ENABLED_ATTRIBUTE));
   }
}
