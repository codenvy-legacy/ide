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
package org.exoplatform.ide.progress;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Sep 20, 2011 evgen $
 *
 */
public class ProgressViewTest extends BaseTest
{
   private static final String ID_CONTROL = "//div[@control-id='__request-notification-control']";

   private static final String ID_VIEW = "//div[@view-id='ideRequestNotificationView']";

   private static final String TEST_FOLDER = ProgressViewTest.class.getSimpleName();

   private static final String REPOSITORY = "progress-repo";

   private static final String ZIP_PATH = "src/test/resources/org/exoplatform/ide/progress/progress-repo.zip";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + TEST_FOLDER);
         VirtualFileSystemUtils.upoadZipFolder(ZIP_PATH, WS_URL + TEST_FOLDER + "/");
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

   @Test
   public void testProgress() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.INIT);
      IDE.GIT.INIT_REPOSITORY.waitOpened();
      IDE.GIT.INIT_REPOSITORY.clickInitButton();
      IDE.GIT.INIT_REPOSITORY.waitClosed();

      Assert.assertTrue(selenium().isElementPresent(ID_CONTROL));
      Assert.assertTrue(selenium().isVisible(ID_CONTROL));

      selenium().click(ID_CONTROL);
      waitForElementPresent(ID_VIEW);

      String text = selenium().getText(ID_VIEW);
      Assert.assertTrue(text.contains("Initializing " + TEST_FOLDER + " repository."));
   }
}
