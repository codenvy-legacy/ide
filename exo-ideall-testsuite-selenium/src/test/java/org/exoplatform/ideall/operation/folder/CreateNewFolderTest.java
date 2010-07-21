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
package org.exoplatform.ideall.operation.folder;

import org.exoplatform.ideall.AbstractTest;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
@Test
public class CreateNewFolderTest extends AbstractTest
{
   @Test
   public void testSelectWorkspace() throws Exception
   {
      selenium.deleteAllVisibleCookies();
      selenium.open("/org.exoplatform.ideall.IDEApplication/IDEApplication.html?gwt.codesvr=127.0.0.1:9997");
      selenium.waitForPageToLoad("10000");
      Assert.assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]"));
      Assert.assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/"));
      Assert.assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/"));
      Assert.assertTrue(selenium
         .isTextPresent("Workspace is not set. Goto Window->Select workspace in main menu for set working workspace?"));
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/");
      Thread.sleep(1000);
      selenium.isElementPresent("scLocator=//Window[ID=\"ideSelectWorkspaceForm\"]");
      selenium.click("scLocator=//ListGrid[ID=\"ideEntryPointListGrid\"]/body/row[0]/col[fieldName=entryPoint||0]");
      selenium.click("scLocator=//IButton[ID=\"ideSelectWorkspaceFormOkButton\"]/");
      Assert.assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideSelectWorkspaceForm\"]"));
      Thread.sleep(1000);
      selenium.mouseDownAt("//div[@title='New']//img", "");
      selenium.mouseUpAt("//div[@title='New']//img", "");
      selenium.mouseDownAt("//td[@class=\"exo-popupMenuTitleField\"]//nobr[contains(text(), \"Folder\")]", "");
      Assert.assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]"));
      Assert.assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]//input"));
      Assert.assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideCreateFolderFormCreateButton\"]"));
      Assert.assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideCreateFolderFormCancelButton\"]"));
      selenium.click("scLocator=//IButton[ID=\"ideCreateFolderFormCreateButton\"]");
      Thread.sleep(1000);
      selenium.isTextPresent("New Folder");

      /*  
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/");
      selenium.click("scLocator=//Window[ID=\"ideallSelectWorkspaceForm\"]/item[0][Class=\"VLayout\"]/");
      selenium.click("scLocator=//ListGrid[ID=\"isc_EntryPointListGrid_0\"]/body/row[entryPoint=%3Cspan%20title%20%3D%20%22http%3A%24fs%24/127.0.0.1%3A8888/rest/private/jcr/repository/dev-monit/%22%3Ehttp%3A//127.0.0.1%3A8888/rest/private/jcr/repository/dev-monit/%3C/span%3E||1]/col[fieldName=entryPoint||0]");
      selenium.click("scLocator=//ListGrid[ID=\"isc_EntryPointListGrid_0\"]/body/row[entryPoint=%3Cspan%20title%20%3D%20%22http%3A%24fs%24/127.0.0.1%3A8888/rest/private/jcr/repository/production/%22%3Ehttp%3A//127.0.0.1%3A8888/rest/private/jcr/repository/production/%3C/span%3E||0]/col[fieldName=entryPoint||0]");
      selenium.click("scLocator=//ListGrid[ID=\"isc_EntryPointListGrid_0\"]/body/row[entryPoint=%3Cspan%20title%20%3D%20%22http%3A%24fs%24/127.0.0.1%3A8888/rest/private/jcr/repository/dev-monit/%22%3Ehttp%3A//127.0.0.1%3A8888/rest/private/jcr/repository/dev-monit/%3C/span%3E||1]/col[fieldName=entryPoint||0]");
      selenium.click("scLocator=//ListGrid[ID=\"isc_EntryPointListGrid_0\"]/body/row[entryPoint=%3Cspan%20title%20%3D%20%22http%3A%24fs%24/127.0.0.1%3A8888/rest/private/jcr/repository/dev-monit/%22%3Ehttp%3A//127.0.0.1%3A8888/rest/private/jcr/repository/dev-monit/%3C/span%3E||1]/col[fieldName=entryPoint||0]");
      selenium.click("scLocator=//ListGrid[ID=\"isc_EntryPointListGrid_0\"]/body/row[entryPoint=%3Cspan%20title%20%3D%20%22http%3A%24fs%24/127.0.0.1%3A8888/rest/private/jcr/repository/dev-monit/%22%3Ehttp%3A//127.0.0.1%3A8888/rest/private/jcr/repository/dev-monit/%3C/span%3E||1]/col[fieldName=entryPoint||0]");*/
   }
   /* public void testCreateFolder() throws Exception
    {
       selenium.waitForPageToLoad("http://127.0.0.1:8888/org.exoplatform.ideall.IDEApplication/IDEApplication.html?gwt.codesvr=127.0.0.1:9997");
       selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]");
       Assert.assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/"));
       selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/");
       Thread.sleep(5000);
    }*/
}
