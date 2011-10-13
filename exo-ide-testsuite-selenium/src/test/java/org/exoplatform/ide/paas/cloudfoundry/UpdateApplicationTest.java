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
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
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

public class UpdateApplicationTest extends CloudFoundryTest
{

   private static final String TEST_FOLDER = "cloudfoundry-test-application-info-" + System.currentTimeMillis();

   private static final String PROJECT_NAME = "java-ror-project";

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
   public void testUpdateApplication() throws Exception
   {
      if (IDE.OUTPUT.isOutputOpened()) {
         IDE.OUTPUT.clickClearButton();
      }
      
      IDE.WORKSPACE.waitForRootItem();

      IDE.NAVIGATION.createFolder(TEST_FOLDER);
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");

      IDE.NAVIGATION.createFolder(PROJECT_NAME);
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + PROJECT_NAME + "/");
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);

      CloudFoundry.CREATE_APPLICATION.openCreateApplicationForm();
      CloudFoundry.CREATE_APPLICATION.waitForFormOpened();
      CloudFoundry.CREATE_APPLICATION.clickAutodetectCheckBox();
      CloudFoundry.CREATE_APPLICATION.selectApplicationType("Grails");
      CloudFoundry.CREATE_APPLICATION.clickCreateButton();
      Thread.sleep(1000);

      int messageIndex = 1;
      if (CloudFoundry.LOGIN.isLoginDialogOpened())
      {
         CloudFoundry.LOGIN.typeEmail("testuser@exoplatform.com");
         CloudFoundry.LOGIN.typePassword("12345");
         CloudFoundry.LOGIN.clickLoginButton();
         CloudFoundry.LOGIN.waitFormLoginDialogClosed();
         messageIndex++;
      }

      CloudFoundry.CREATE_APPLICATION.waitForFormClosed();
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      assertTrue(IDE.OUTPUT.isOutputOpened());
      assertEquals("[INFO] Application " + PROJECT_NAME + " successfully created.", IDE.OUTPUT.getOutputMessageText(messageIndex));
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      IDE.MENU.runCommand(MenuCommands.PaaS.PAAS, MenuCommands.PaaS.CloudFoundry.CLOUDFOUNDRY, MenuCommands.PaaS.CloudFoundry.UPDATE_APPLICATION);
      Thread.sleep(TestConstants.FOLDER_REFRESH_PERIOD);

      messageIndex++;
      /*
       * mock service always responses with project name test-spring-project
       */
      assertEquals("[INFO] Application test-spring-project successfully updated.", IDE.OUTPUT.getOutputMessageText(messageIndex));
      Thread.sleep(10000);
   }

}
