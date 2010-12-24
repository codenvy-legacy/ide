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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Dec 22, 2010 3:49:01 PM evgen $
 *
 */
public class RestServicesDiscoveryTest extends BaseTest
{

   @Test
   public void testRestServicesDiscovery() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      IDE.menu().runCommand(MenuCommands.Help.HELP, MenuCommands.Help.REST_SERVICES);
      Thread.sleep(TestConstants.SLEEP);

      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideRestServiceDiscovery\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideRestServiceDiscoveryOkButton\"]/"));

      openNode(3, 0);

      assertEquals("/ide/class-info-storage/jar", getTitle(5, 0));

      openNode(5, 0);
      openNode(6, 0);
      openNode(7, 0);
      assertEquals("Query Param", getTitle(7, 0));
      assertEquals("POST", getTitle(6, 0));
      assertEquals("jar-path:string", getTitle(8, 0));
      assertEquals("package:string", getTitle(9, 0));

   }
   /**
    * @throws InterruptedException
    */
   private void openNode(int row, int col) throws InterruptedException
   {
      selenium.click("scLocator=//TreeGrid[ID=\"ideRestServiceTreeGrid\"]/body/row[" + String.valueOf(row) + "]/col["
         + String.valueOf(col) + "]/open");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   private String getTitle(int row, int col)
   {
      return selenium.getText("scLocator=//TreeGrid[ID=\"ideRestServiceTreeGrid\"]/body/row[" + String.valueOf(row)
         + "]/col[" + String.valueOf(col) + "]");
   }

}
