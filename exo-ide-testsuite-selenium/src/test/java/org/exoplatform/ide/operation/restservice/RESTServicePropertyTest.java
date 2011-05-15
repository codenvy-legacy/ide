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

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
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

   private static final String FILE_NAME = "RESTServicePropertyTest.groovy";

   private static final String FOLDER_NAME = RESTServicePropertyTest.class.getSimpleName();

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + FOLDER_NAME);
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testProperty() throws Exception
   {
      
      waitForRootElement();
      IDE.WORKSPACE.selectItem(WS_URL + FOLDER_NAME + "/");
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.REST_SERVICE_FILE);
      Thread.sleep(TestConstants.SLEEP);

      IDE.MENU.checkCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.SHOW_PROPERTIES, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.SET_AUTOLOAD, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.VALIDATE, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_REST_SERVICE, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.UNDEPLOY_REST_SERVICE, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.LAUNCH_REST_SERVICE, false);

      saveAsUsingToolbarButton(FILE_NAME);
      Thread.sleep(TestConstants.SLEEP);

      IDE.MENU.checkCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.SHOW_PROPERTIES, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.SET_AUTOLOAD, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.VALIDATE, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_REST_SERVICE, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.UNDEPLOY_REST_SERVICE, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.LAUNCH_REST_SERVICE, true);

      IDE.PROPERTIES.openProperties();
      assertEquals("false", IDE.PROPERTIES.getAutoloadProperty());
      assertEquals(TestConstants.NodeTypes.EXO_GROOVY_RESOURCE_CONTAINER, IDE.PROPERTIES.getContentNodeType());
      assertEquals(MimeType.GROOVY_SERVICE, IDE.PROPERTIES.getContentType());
      assertEquals(FILE_NAME, IDE.PROPERTIES.getDisplayName());
      assertEquals(TestConstants.NodeTypes.NT_FILE, IDE.PROPERTIES.getFileNodeType());
      
      IDE.EDITOR.closeTab(0);
   }

   @AfterClass
   public static void tearDown()
   {
      String url = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + FOLDER_NAME;
      try
      {
         VirtualFileSystemUtils.delete(url);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
      }
   }

}
