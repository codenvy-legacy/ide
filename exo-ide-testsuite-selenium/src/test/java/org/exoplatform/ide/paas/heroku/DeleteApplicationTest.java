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
package org.exoplatform.ide.paas.heroku;

import static org.junit.Assert.assertEquals;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.paas.heroku.core.Heroku;
import org.exoplatform.ide.paas.heroku.core.Heroku.Messages;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class DeleteApplicationTest extends BaseTest
{

   private static final String TEST_FOLDER = DeleteApplicationTest.class.getSimpleName();

   @Before
   public void setUp() throws Exception
   {
      try
      {
         VirtualFileSystemUtils.upoadZipFolder(
            "src/test/resources/org/exoplatform/ide/paas/heroku/DeleteApplicationTest.zip", WS_URL);
      }
      catch (Exception e)
      {
      }
   }

   @After
   public void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + TEST_FOLDER);
         Heroku.logout();
      }
      catch (Exception e)
      {
      }
   }

  // @Test
   public void testDeleteApplication() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");

      IDE.HEROKU.CREATE_APP.createApplication(TEST_FOLDER);
      IDE.HEROKU.DELETE_APP.callDeleteApplication();
      IDE.ASK_DIALOG.waitOpened();
      assertEquals("Delete application from Heroku", IDE.ASK_DIALOG.getQuestion());
      IDE.ASK_DIALOG.clickYes();
      IDE.ASK_DIALOG.waitClosed();

      IDE.OUTPUT.waitForMessageShow(3);
      String text = IDE.OUTPUT.getOutputMessage(3);
      assertEquals(Messages.DELETED, text);

   }
}
