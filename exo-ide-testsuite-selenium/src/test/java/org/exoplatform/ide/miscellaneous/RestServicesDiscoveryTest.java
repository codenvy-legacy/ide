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
      selenium.click("scLocator=//ListGrid[ID=\"ideRestServiceListGrid\"]/header/headerButton[fieldName=Path]/");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertTrue(selenium.isTextPresent("org.exoplatform.services.rest.ext.service.RestServicesList"));
      
      selenium.click("scLocator=//IButton[ID=\"ideRestServiceDiscoveryOkButton\"]/");

   }
   
}
