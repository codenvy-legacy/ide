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
import junit.framework.Assert;

import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.git.core.GIT;
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

public class TestCreateApplication extends CloudFoundryTest
{

   private String TEST_FOLDER;

   private static final String PROJECT_NAME = "cloudFoundry-test-project";
   
   private static final String TEST_ROR_PROJECT = "cloudfoundry-test-ror-project";
   
   @Before
   public void setUp() throws Exception
   {
      TEST_FOLDER = "cloudfoundry-test-create-application" + System.currentTimeMillis();
      
      try
      {
         resetMockService();
         //         VirtualFileSystemUtils.mkcol(WS_URL + TEST_FOLDER);
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
         VirtualFileSystemUtils.delete(WS_URL + TEST_FOLDER);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
   
   @Test
   public void testCreateRORApplication() throws Exception {
      if (IDE.OUTPUT.isOutputOpened()) {
         IDE.OUTPUT.clickClearButton();
      }
      
      IDE.WORKSPACE.waitForRootItem();
      IDE.NAVIGATION.createFolder(TEST_FOLDER);

      IDE.NAVIGATION.createFolder(TEST_ROR_PROJECT);
      
      CloudFoundry.CREATE_APPLICATION.openCreateApplicationForm();
      CloudFoundry.CREATE_APPLICATION.waitForFormOpened();      
      CloudFoundry.CREATE_APPLICATION.clickAutodetectCheckBox();
      CloudFoundry.CREATE_APPLICATION.selectApplicationType("Grails");      
      CloudFoundry.CREATE_APPLICATION.clickCreateButton();
      
      int successMessageIndex = 1;
      
      Thread.sleep(1000);
      if (CloudFoundry.LOGIN.isLoginDialogOpened()) {
         CloudFoundry.LOGIN.typeEmail("testuser@exoplatform.com");
         CloudFoundry.LOGIN.typePassword("12345");
         CloudFoundry.LOGIN.clickLoginButton();
         CloudFoundry.LOGIN.waitFormLoginDialogClosed();
         successMessageIndex = 2;
      }
      
      CloudFoundry.CREATE_APPLICATION.waitForFormClosed();      
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      assertTrue(IDE.OUTPUT.isOutputOpened());
      assertEquals("[INFO] Application cloudfoundry-test-ror-project successfully created.", IDE.OUTPUT.getOutputMessageText(successMessageIndex));
   }

   @Test
   public void testCreateApplication() throws Exception
   {
      if (IDE.OUTPUT.isOutputOpened()) {
         IDE.OUTPUT.clickClearButton();
      }
      
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.selectRootItem();
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.NAVIGATION.createFolder(TEST_FOLDER);

      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.New.NEW, MenuCommands.New.JAVA_SPRING_PROJECT);
      IDE.PROJECT.waitForDialogOpened();
      IDE.PROJECT.typeProjectName(PROJECT_NAME);
      assertTrue(IDE.PROJECT.isCreateButtonEnabled());
      IDE.PROJECT.clickCreateButton();
      
      int successMessageIndex = 1;
      
      Thread.sleep(1000);
      if (CloudFoundry.LOGIN.isLoginDialogOpened()) {
         CloudFoundry.LOGIN.typeEmail("testuser@exoplatform.com");
         CloudFoundry.LOGIN.typePassword("12345");
         CloudFoundry.LOGIN.clickLoginButton();
         CloudFoundry.LOGIN.waitFormLoginDialogClosed();
         successMessageIndex = 2;
      }      
      
      IDE.PROJECT.waitForDialogClosed();

      IDE.OUTPUT.waitForOutputOpened();
      String outputMessage = IDE.OUTPUT.getOutputMessageText(successMessageIndex);
      assertEquals("[INFO] Java project " + PROJECT_NAME + " is successfully created.", outputMessage);

      IDE.OUTPUT.clickClearButton();

      IDE.WORKSPACE.doubleClickOnFolder(WS_URL + TEST_FOLDER + "/" + PROJECT_NAME + "/");

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.INIT);
      IDE.GIT.INIT_REPOSITORY.waitForViewOpened();
      IDE.GIT.INIT_REPOSITORY.clickInitButton();
      IDE.GIT.INIT_REPOSITORY.waitForViewClosed();

      IDE.OUTPUT.waitForMessageShow(1, 30000);
      String mes1 = IDE.OUTPUT.getOutputMessageText(1);
      Assert.assertTrue(mes1.endsWith(GIT.Messages.INIT_SUCCESS));

      IDE.OUTPUT.clickClearButton();
      
      CloudFoundry.CREATE_APPLICATION.openCreateApplicationForm();
      CloudFoundry.CREATE_APPLICATION.waitForFormOpened();      
      CloudFoundry.CREATE_APPLICATION.clickCreateButton();
      
      Thread.sleep(1000);
      if (CloudFoundry.LOGIN.isLoginDialogOpened()) {
         CloudFoundry.LOGIN.typeEmail("testuser@exoplatform.com");
         CloudFoundry.LOGIN.typePassword("12345");
         CloudFoundry.LOGIN.clickLoginButton();
         CloudFoundry.LOGIN.waitFormLoginDialogClosed();
      }
      
      CloudFoundry.CREATE_APPLICATION.waitForFormClosed();
      
      IDE.OUTPUT.waitForMessageShow(1, 30000);
      String mes2 = IDE.OUTPUT.getOutputMessageText(1);
      System.out.println("ORIGINAL MESSAGE [" + mes2 + "]");
      Assert.assertTrue(mes2.equals("[INFO] Logged in CloudFoundry successfully."));
      
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.NAVIGATION.deleteSelectedItems();
   }

}
