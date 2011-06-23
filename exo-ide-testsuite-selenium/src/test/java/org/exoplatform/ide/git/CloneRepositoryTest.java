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
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 23, 2011 2:42:41 PM anya $
 *
 */
public class CloneRepositoryTest extends BaseTest
{
   private static final String TEST_FOLDER = CloneRepositoryTest.class.getSimpleName();

   private static final String TEST_FILE1 = "TestFile1";

   private static final String TEST_FILE2 = "TestFile2";

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/" + TEST_FOLDER + "/";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + TEST_FOLDER);
         VirtualFileSystemUtils.put(new byte[0], MimeType.GROOVY_SERVICE, URL + TEST_FILE1);
         VirtualFileSystemUtils.put(new byte[0], MimeType.GROOVY_SERVICE, URL + TEST_FILE2);
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
    * Tests the Clone repository view: opens it and checks elements,
    * then closes with Cancel button.
    * 
    * @throws Exception
    */
   @Test
   public void testCloneRepositoryView() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.CLONE);
      IDE.GIT.CLONE_REPOSITORY.waitForViewOpened();
      Assert.assertTrue(IDE.GIT.CLONE_REPOSITORY.isViewComponentsPresent());
      Assert.assertFalse(IDE.GIT.CLONE_REPOSITORY.isCloneButtonEnabled());
      Assert.assertTrue(IDE.GIT.CLONE_REPOSITORY.isCancelButtonEnabled());

      Assert.assertFalse(IDE.GIT.CLONE_REPOSITORY.getWorkDirectoryValue().isEmpty());
      Assert.assertTrue(IDE.GIT.CLONE_REPOSITORY.getRemoteUriFieldValue().isEmpty());
      Assert.assertFalse(IDE.GIT.CLONE_REPOSITORY.getRemoteNameFieldValue().isEmpty());

      IDE.GIT.CLONE_REPOSITORY.clickCancelButton();
      IDE.GIT.CLONE_REPOSITORY.waitForViewClosed();
   }
}
