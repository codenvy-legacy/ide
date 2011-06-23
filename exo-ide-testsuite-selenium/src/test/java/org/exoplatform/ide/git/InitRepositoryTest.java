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
package org.exoplatform.ide.git;

import junit.framework.Assert;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.git.core.GIT;
import org.exoplatform.ide.git.core.InitRepository;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 23, 2011 9:55:53 AM anya $
 *
 */
public class InitRepositoryTest extends BaseTest
{
   private static final String TEST_FOLDER = InitRepositoryTest.class.getSimpleName();

   private static final String TEST_FILE = "TestFile";

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/" + TEST_FOLDER + "/";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + TEST_FOLDER);
         VirtualFileSystemUtils.put(new byte[0], MimeType.GROOVY_SERVICE, URL + TEST_FILE);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @AfterClass
   public static void tearDown()
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

   /**
    * Tests the Init repository view: opens it and checks elements,
    * then closes with Cancel button.
    * 
    * @throws Exception
    */
   @Test
   public void testInitRepositoryView() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.INIT);
      IDE.GIT.INIT_REPOSITORY.waitForViewOpened();
      Assert.assertTrue(IDE.GIT.INIT_REPOSITORY.isViewComponentsPresent());
      Assert.assertTrue(IDE.GIT.INIT_REPOSITORY.isInitButtonEnabled());
      Assert.assertTrue(IDE.GIT.INIT_REPOSITORY.isCancelButtonEnabled());
      Assert.assertFalse(IDE.GIT.INIT_REPOSITORY.getWorkDirectoryValue().isEmpty());

      //Check element's titles:
      Assert.assertEquals(InitRepository.Titles.INIT_BUTTON, IDE.GIT.INIT_REPOSITORY.getInitButtonTitle());
      Assert.assertEquals(InitRepository.Titles.CANCEL_BUTTON, IDE.GIT.INIT_REPOSITORY.getCancelButtonTitle());

      IDE.GIT.INIT_REPOSITORY.clickCancelButton();
      IDE.GIT.INIT_REPOSITORY.waitForViewClosed();
   }

   /**
    * Tests the Init repository command for workspace. 
    * Must be not allowed.
    * 
    * @throws Exception
    */
   @Test
   public void testInitRepositoryInWorkspace() throws Exception
   {
      selenium.refresh();
      IDE.WORKSPACE.waitForRootItem();

      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.INIT, true);

      IDE.WORKSPACE.selectRootItem();
      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.INIT, false);

      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.INIT, true);
   }

   /**
    * Tests the Init repository command for selected file. 
    * Must be not allowed.
    * 
    * @throws Exception
    */
   @Test
   public void testInitRepositoryWithSelectedFile() throws Exception
   {
      selenium.refresh();
      IDE.WORKSPACE.waitForRootItem();

      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.INIT, true);

      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + TEST_FILE);

      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + TEST_FILE);
      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.INIT, false);

      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.INIT, true);
   }

   /**
    * Test init Git repository.
    * 
    * @throws Exception
    */
   @Test
   public void testInitRepository() throws Exception
   {
      selenium.refresh();
      IDE.WORKSPACE.waitForRootItem();

      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.INIT);
      IDE.GIT.INIT_REPOSITORY.waitForViewOpened();
      Assert.assertTrue(IDE.GIT.INIT_REPOSITORY.getWorkDirectoryValue().endsWith(TEST_FOLDER + "/"));

      IDE.GIT.INIT_REPOSITORY.clickInitButton();
      IDE.GIT.INIT_REPOSITORY.waitForViewClosed();

      IDE.OUTPUT.waitForMessageShow(1);
      String message = IDE.OUTPUT.getOutputMessageText(1);
      Assert.assertTrue(message.endsWith(GIT.Messages.INIT_SUCCESS));
      
      selenium.open(WS_URL + TEST_FOLDER);
      selenium.waitForPageToLoad(""+5000);
      Assert.assertTrue(selenium.isElementPresent("link=.git"));
      selenium.goBack();
      IDE.WORKSPACE.waitForRootItem();
   }

   /**
    * Test init Git repository in folder with Git repository.
    * 
    * @throws Exception
    */
   @Test
   public void testInitRepositoryIfExists() throws Exception
   {
      selenium.refresh();
      IDE.WORKSPACE.waitForRootItem();

      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.INIT);

      IDE.INFORMATION_DIALOG.waitForInfoDialog();
      Assert.assertEquals(GIT.Messages.GIT_REPO_EXISTS, IDE.INFORMATION_DIALOG.getMessage());

      IDE.INFORMATION_DIALOG.clickOk();
      IDE.INFORMATION_DIALOG.waitForInfoDialogNotPresent();
   }
}
