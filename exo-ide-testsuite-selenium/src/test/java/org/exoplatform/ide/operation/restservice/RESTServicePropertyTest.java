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
package org.exoplatform.ide.operation.restservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class RESTServicePropertyTest extends BaseTest
{

   private static final String FILE_NAME = "RESTServicePropertyTest.grs";

   private static final String PROJECT = RESTServicePropertyTest.class.getSimpleName();

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.createDefaultProject(PROJECT);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testProperty() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();
      
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.REST_SERVICE_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.grs");
      
      
      assertFalse(IDE.MENU.isCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.SHOW_PROPERTIES));
      assertFalse(IDE.MENU.isCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.SET_AUTOLOAD));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.VALIDATE));
      assertFalse(IDE.MENU.isCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_REST_SERVICE));
      assertFalse(IDE.MENU.isCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.UNDEPLOY_REST_SERVICE));
      assertFalse(IDE.MENU.isCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.LAUNCH_REST_SERVICE));

      IDE.EDITOR.saveAs(1, FILE_NAME);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);
      
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.SHOW_PROPERTIES));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.SET_AUTOLOAD));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.VALIDATE));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_REST_SERVICE));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.UNDEPLOY_REST_SERVICE));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.LAUNCH_REST_SERVICE));

      IDE.PROPERTIES.openProperties();
      assertEquals(MimeType.GROOVY_SERVICE, IDE.PROPERTIES.getContentType());
      assertEquals(FILE_NAME, IDE.PROPERTIES.getDisplayName());
      
      IDE.EDITOR.closeFile(FILE_NAME);
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

}
