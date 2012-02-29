/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.maven.client;

import static org.junit.Assert.assertTrue;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.core.Build;
import org.exoplatform.ide.git.core.GIT;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: BuildPerformedTest.java Feb 27, 2012 6:55:26 PM azatsarynnyy $
 *
 */
public class BuildPerformedTest extends BaseTest
{
   private static final String PROJECT = BuildPerformedTest.class.getSimpleName();

   private static final String PATH_TO_POM = "src/test/resources/org/exoplatform/ide/miscellaneous/pom.xml";

   protected static Map<String, Link> project;

   @Before
   public void before()
   {
      try
      {
         project =
            VirtualFileSystemUtils.importZipProject(PROJECT,
               "src/test/resources/org/exoplatform/ide/project/classpath.zip");

         Thread.sleep(2000);
      }
      catch (Exception e)
      {
      }
   }

   @After
   public void after()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);

         Thread.sleep(2000);
      }
      catch (Exception e)
      {
      }
   }

   @Test
   public void testBuildPerformed() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();

      // Open project
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();

      // Add pom.xml
      VirtualFileSystemUtils.createFileFromLocal(project.get(Link.REL_CREATE_FILE), "pom.xml",
         MimeType.APPLICATION_XML, PATH_TO_POM);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);

      // Init Git repository
      IDE.GIT.INIT_REPOSITORY.initRepository();
      IDE.OUTPUT.waitForMessageShow(1, 15);
      String initSuccessMessage = IDE.OUTPUT.getOutputMessage(1);
      assertTrue(initSuccessMessage.endsWith(GIT.Messages.INIT_SUCCESS));
      IDE.LOADER.waitClosed();

      // Start the build of two projects
      IDE.MENU.runCommand(MenuCommands.Project.PROJECT, MenuCommands.Project.BUILD_PROJECT);
      IDE.MENU.runCommand(MenuCommands.Project.PROJECT, MenuCommands.Project.BUILD_PROJECT);

      // Get error message
      IDE.WARNING_DIALOG.waitOpened();
      String errorMessage = IDE.WARNING_DIALOG.getWarningMessage();
      assertTrue(errorMessage.startsWith(Build.Messages.BUILD_IN_PROGRESS));
      IDE.WARNING_DIALOG.clickOk();
      IDE.WARNING_DIALOG.waitClosed();
      
   // Delete Git repository
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.DELETE);
      IDE.ASK_DIALOG.waitOpened();
      IDE.ASK_DIALOG.clickYes();
      IDE.OUTPUT.waitForMessageShow(2, 15);
      String message = IDE.OUTPUT.getOutputMessage(2);
      assertTrue(message.endsWith(GIT.Messages.DELETE_SUCCESS));
   }
}
