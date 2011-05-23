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

import org.exoplatform.ide.TestConstants;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class Statusbar extends AbstractTestModule
{

   public static String STATUSBAR_LOCATOR =
      "//div[@id='exoIDEStatusbar']//div[@control-id='__editor_cursor_position']//table[@class='exo-statusText-table']//td[@class='exo-statusText-table-middle']";

   /**
    * get text on statusbar
    * @return
    */
   public String getCursorPosition()
   {
      return selenium().getText(STATUSBAR_LOCATOR);
   }

   /**
    * Get text shown in status bar.
    * 
    * @return {@link String} text
    */
   public String getStatusbarText()
   {
      
      return selenium()
         .getText(
            "//div[@id='exoIDEStatusbar']//div[@control-id='__editor_cursor_position']//table[@class='exo-statusText-table']//td[@class='exo-statusText-table-middle']");
   }

   /**
    * click on statusbar and check appear GoToLine form
    * @throws InterruptedException
    */
   public void clickOnStatusBar() throws InterruptedException
   {
      selenium().click(STATUSBAR_LOCATOR);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      IDE().GOTOLINE.checkAppearGoToLineForm();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

}
