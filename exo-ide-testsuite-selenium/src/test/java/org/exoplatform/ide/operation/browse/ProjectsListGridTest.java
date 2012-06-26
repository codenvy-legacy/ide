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
package org.exoplatform.ide.operation.browse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: ProjectsListGridTest.java Apr 25, 2012 12:53:58 PM azatsarynnyy $
 *
 */
public class ProjectsListGridTest extends BaseTest
{
   private static final String PROJECT1 = ProjectsListGridTest.class.getSimpleName() + "1";

   private static final String PROJECT2 = ProjectsListGridTest.class.getSimpleName() + "2";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.createDefaultProject(PROJECT1);
         VirtualFileSystemUtils.createDefaultProject(PROJECT2);
      }
      catch (Exception e)
      {
         fail("Can't create test project");
      }
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT1 + "/");
         VirtualFileSystemUtils.delete(WS_URL + PROJECT2 + "/");
      }
      catch (Exception e)
      {
         fail("Can't create test folders");
      }
   }

   @Test
   public void projectsListTest() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.waitForItemInProjectList(PROJECT1);
      IDE.PROJECT.EXPLORER.waitForItemInProjectList(PROJECT2);
      assertTrue(IDE.PROJECT.EXPLORER.isProjectsListGridVisible());
      int countOfProjects = IDE.PROJECT.EXPLORER.getProjectsCountInProjectsListGrid();

      // make sure that two test projects are present in the projects list grid
      IDE.PROJECT.EXPLORER.selectProjectByNameInProjectsListGrid(PROJECT1);
      IDE.PROJECT.EXPLORER.selectProjectByNameInProjectsListGrid(PROJECT2);

      // open project1
      IDE.PROJECT.OPEN.openProject(PROJECT1);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT1);
      IDE.LOADER.waitClosed();

      assertFalse(IDE.PROJECT.EXPLORER.isProjectsListGridVisible());

      IDE.MENU.runCommand(MenuCommands.Project.PROJECT, MenuCommands.Project.CLOSE_PROJECT);
      IDE.PROJECT.EXPLORER.waitForItemInProjectList(PROJECT1);
      assertTrue(IDE.PROJECT.EXPLORER.isProjectsListGridVisible());
      
      // delete project1
      VirtualFileSystemUtils.delete(WS_URL + PROJECT1 + "/");
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.waitForItemInProjectList(PROJECT2);
      assertTrue(IDE.PROJECT.EXPLORER.isProjectsListGridVisible());
      assertEquals(countOfProjects - 1, IDE.PROJECT.EXPLORER.getProjectsCountInProjectsListGrid());
   }

}
