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

import static org.junit.Assert.assertEquals;

import org.exoplatform.ide.TestConstants;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class Preview extends AbstractTestModule
{

   public void checkIsOpened(boolean isOpened)
   {
      String locator = "//div[@view-id='idePreviewHTMLView']";
      assertEquals(isOpened, selenium().isElementPresent(locator));
   }

   public void selectIFrame(String iFrameURL)
   {
      selenium().selectFrame("//iframe[@src='" + iFrameURL + "']");
   }
   
   /**
    * Selenium selects preview iframe.
    */
   public void selectPreviewIFrame()
   {
      selenium().selectFrame("eXo-IDE-preview-frame");
   }

   public void close() throws Exception
   {
      String locator =
         "//div[@panel-id='operation']//table[@class='gwt-DecoratedTabBar']//div[@role='tab']//div[@button-name='close-tab' and @tab-title='Preview']";
      selenium().mouseOver(locator);
      Thread.sleep(TestConstants.ANIMATION_PERIOD);

      selenium().click(locator);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

}
