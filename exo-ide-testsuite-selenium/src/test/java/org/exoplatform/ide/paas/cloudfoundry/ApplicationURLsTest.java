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

package org.exoplatform.ide.paas.cloudfoundry;

import static org.junit.Assert.assertEquals;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.paas.cloudfoundry.core.CloudFoundry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ApplicationURLsTest extends CloudFoundryTest
{
   
   private static final String TEST_FOLDER = "cloudfoundry-test-application-urls-" + System.currentTimeMillis();

   private static final String PROJECT_NAME = "java-spring-project";

   @Before
   public void setUp() throws Exception
   {
      System.out.println("REST WORKSPACE URL [" + BaseTest.REST_WORKSPACE_URL + "]");
      System.out.println("IDE WORKSPACE URL [" + BaseTest.IDE_WORKSPACE_URL + "]");

      try
      {
         resetMockService();
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @After
   public void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(REST_WORKSPACE_URL + TEST_FOLDER);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testApplicationURLs() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();

      IDE.NAVIGATION.createFolder(TEST_FOLDER);

      uploadResource("src/test/resources/org/exoplatform/ide/paas/cloudfoundry/java-spring-project.zip", REPO_NAME
         + "/" + WS_NAME + "/" + TEST_FOLDER + "/java-spring-project.zip");

      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);

      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + PROJECT_NAME + "/");

      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);

      CloudFoundry.APPLICATION_URLS.openFromMenu();
      CloudFoundry.APPLICATION_URLS.waitApplicationURLsOpened();
      assertEquals(1, CloudFoundry.APPLICATION_URLS.getURLsCount());
      
      CloudFoundry.APPLICATION_URLS.typeMapURL("testurl");
      CloudFoundry.APPLICATION_URLS.clickMapButton();
      Thread.sleep(1000);
      assertEquals(2, CloudFoundry.APPLICATION_URLS.getURLsCount());

      CloudFoundry.APPLICATION_URLS.clickUnMapButton(1);
      IDE.ASK_DIALOG.waitForAskDialogOpened();
      IDE.ASK_DIALOG.clickYes();
      IDE.ASK_DIALOG.waitForAskDialogClosed();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      assertEquals(1, CloudFoundry.APPLICATION_URLS.getURLsCount());
      
      assertEquals("testurl", CloudFoundry.APPLICATION_URLS.getURL(1));      
   }

}
