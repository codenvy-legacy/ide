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
package org.exoplatform.ide.shell.vfs;

import static org.junit.Assert.*;
import static org.fest.assertions.Assertions.*;
import org.exoplatform.ide.shell.BaseTest;
import org.exoplatform.ide.shell.VfsUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}:  Dec 27, 2011 12:27:34 PM evgen $
 *
 */
public class GitCommandsTest extends BaseTest
{

   private static final String MAIN_FOLDER = VfsCommandTest.class.getSimpleName();

   private static final String GIT_MAIN_FOLDER = VfsCommandTest.class.getSimpleName() + "Git";

   private static Map<String, Link> folderLinks;

   private static Map<String, Link> gitFolderLinks;

   @BeforeClass
   public static void uploadFolders() throws IOException
   {
      folderLinks =
         VfsUtils.importZipProject(MAIN_FOLDER, "src/test/resources/org/exoplatform/ide/shell/test-project.zip");
      gitFolderLinks =
         VfsUtils.importZipProject(GIT_MAIN_FOLDER, "src/test/resources/org/exoplatform/ide/shell/gitaddtest.zip");
   }

   @AfterClass
   public static void deleteFolders() throws IOException
   {
      VfsUtils.deleteFolder(folderLinks.get(Link.REL_DELETE));
      VfsUtils.deleteFolder(gitFolderLinks.get(Link.REL_DELETE));
   }

   @Test
   public void gitCommands() throws Exception
   {
      shell.executeCommand("cd " + MAIN_FOLDER);
      shell.executeCommand("git status");
      assertThat(shell.getText()).contains("HEAD reference not found. Seems working directory is not git repository.");
      shell.executeCommand("clear");

      shell.executeCommand("git init");
      Thread.sleep(1000);
      shell.executeCommand("git status");
      Thread.sleep(1000);
      assertThat(shell.getText()).doesNotContain(
         "HEAD reference not found. Seems working directory is not git repository.").contains("# On branch master");

      shell.executeCommand("cd /" + GIT_MAIN_FOLDER);

      shell.executeCommand("clear");
      shell.executeCommand("git status");
      Thread.sleep(1000);
      assertThat(shell.getText()).doesNotContain(
         "HEAD reference not found. Seems working directory is not git repository.").contains(
         "modified:").contains("TestFile1.txt");
      shell.executeCommand("git add TestFile1.txt");
      
      shell.executeCommand("git commit -m \"test commit\"");
      Thread.sleep(1000);
      assertThat(shell.getText()).contains("\"committer\"").contains("\"commitTime\"").contains("\"id\"");
      
      shell.executeCommand("clear");
      shell.executeCommand("git status");
      
      
   }

}
