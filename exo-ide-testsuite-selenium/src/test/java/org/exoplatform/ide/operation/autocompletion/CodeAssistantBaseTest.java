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
package org.exoplatform.ide.operation.autocompletion;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.Before;

import java.io.IOException;
import java.util.Map;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}:  Nov 8, 2011 3:48:28 PM evgen $
 *
 */
public abstract class CodeAssistantBaseTest extends BaseTest
{

   private static Map<String, Link> project;
   
   protected static String projectName;

   public static void createProject(String name)
   {
      projectName = name;
      try
      {
         project = VirtualFileSystemUtils.createDefaultProject(name);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   @AfterClass
   public static void deleteProject() throws IOException
   {
      if (project != null)
         VirtualFileSystemUtils.deleteFolder(project.get(Link.REL_DELETE));
   }
   
   @Before
   public void openProject() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(projectName);
   }

}
