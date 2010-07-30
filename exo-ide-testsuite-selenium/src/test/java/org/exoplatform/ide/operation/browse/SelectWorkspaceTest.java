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
package org.exoplatform.ide.operation.browse;

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
public class SelectWorkspaceTest extends BaseTest
{
   
   @Test
   public void testSelectWorkspace() throws Exception
   {
      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='Window']", "");
      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), 'Select Workspace')]", "");
      Thread.sleep(1000);
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideSelectWorkspaceForm\"]"));
      assertTrue(selenium.isTextPresent("Workspace"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideSelectWorkspaceFormOkButton\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideSelectWorkspaceFormCancelButton\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//ListGrid[ID=\"ideEntryPointListGrid\"]"));
      assertTrue(selenium.isTextPresent("/rest/private/jcr/repository/production"));
      assertTrue(selenium.isTextPresent("/rest/private/jcr/repository/dev-monit"));
      selenium.click("scLocator=//IButton[ID=\"ideSelectWorkspaceFormOkButton\"]");
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideSelectWorkspaceForm\"]"));
      selenium.click("scLocator=//IButton[ID=\"ideSelectWorkspaceFormCancelButton\"]");
      Thread.sleep(1000);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideSelectWorkspaceForm\"]"));
      assertTrue(selenium.isTextPresent("dev-monit"));
      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='Window']", "");
      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), 'Select Workspace')]", "");
      Thread.sleep(1000);
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideSelectWorkspaceForm\"]"));
      selenium.click("scLocator=//ListGrid[ID=\"ideEntryPointListGrid\"]/body/row[0]/col[fieldName=entryPoint||0]\"");
      selenium.click("scLocator=//IButton[ID=\"ideSelectWorkspaceFormOkButton\"]");
      Thread.sleep(1000);
      assertTrue(selenium.isTextPresent("production"));
      assertTrue(selenium.isTextPresent("exo:registry"));
      assertTrue(selenium.isTextPresent("jcr:system"));
   }

}
