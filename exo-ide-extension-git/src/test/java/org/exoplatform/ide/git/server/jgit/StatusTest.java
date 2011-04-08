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
package org.exoplatform.ide.git.server.jgit;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.exoplatform.ide.git.server.jgit.JGitStatus;
import org.exoplatform.ide.git.shared.GitFile;
import org.exoplatform.ide.git.shared.StatusRequest;
import org.exoplatform.ide.git.shared.GitFile.FileStatus;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: StatusTest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class StatusTest extends BaseTest
{
   private File bbb;
   private File aaa;
   @SuppressWarnings("unused")
   private File untouched;
   private File remove;
   private Repository repository;
   private File readme;

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();

      System.out.println();

      repository = getDefaultRepository();

      Git git = new Git(repository);

      remove = addFile(repository.getWorkTree(), "toRemove", "xxxxx");
      untouched = addFile(repository.getWorkTree(), "untouched", "xxxxx");
      git.add().addFilepattern(".").call();
      git.commit().setMessage("to remove").call();

      remove.delete();
      git.add().addFilepattern(".").setUpdate(true).call();

      readme = new File(repository.getWorkTree(), "README.txt");
      FileOutputStream fos = new FileOutputStream(new File(repository.getWorkTree(), "README.txt"));
      fos.write("MODIFIED\n".getBytes());
      fos.flush();
      fos.close();

      File a = new File(repository.getWorkTree(), "a");
      File b = new File(a, "b");
      File c = new File(b, "c");
      File d = new File(c, "d");
      d.mkdirs();

      aaa = addFile(d, "aaa", "AAA\n");
      bbb = addFile(d, "bbb", "BBB\n");

      git.add().addFilepattern(calculateRelativePath(repository.getWorkTree(), aaa)).call();
   }

   public void testStatus() throws Exception
   {
      JGitStatus statusPage = (JGitStatus)getDefaultConnection().status(new StatusRequest());

      statusPage.writeTo(System.out);

      assertEquals("master", statusPage.getBranchName());

      List<GitFile> untracked = statusPage.getUntracked();
      assertEquals(1, untracked.size());
      assertEquals(calculateRelativePath(repository.getWorkTree(), bbb), untracked.get(0).getPath());

      List<GitFile> changedNotCommited = statusPage.getChangedNotCommited();
      assertEquals(2, changedNotCommited.size());
      List<String> l = new ArrayList<String>(2);
      for (GitFile f : changedNotCommited)
         l.add(f.getStatus().getLongStatus() + ":" + f.getPath());
      assertTrue(l.contains("new file:" + calculateRelativePath(repository.getWorkTree(), aaa)));
      assertTrue(l.contains("deleted:" + calculateRelativePath(repository.getWorkTree(), remove)));

      List<GitFile> changedNotUpdated = statusPage.getChangedNotUpdated();
      assertEquals(1, changedNotUpdated.size());
      assertEquals(calculateRelativePath(repository.getWorkTree(), readme), changedNotUpdated.get(0).getPath());
      assertEquals(FileStatus.MODIFIED, changedNotUpdated.get(0).getStatus());
   }

   public void testStatusClean() throws Exception
   {
      // Commit all changes to get clean state of repository.
      Git git = new Git(repository);
      git.add().addFilepattern(".").call();
      git.commit().setMessage("commit all changes").call();

      JGitStatus statusPage = (JGitStatus)getDefaultConnection().status(new StatusRequest());
      statusPage.writeTo(System.out);
   }

   public void testShortStatus() throws Exception
   {
      JGitStatus statusPage = (JGitStatus)getDefaultConnection().status(new StatusRequest(new String[]{"a/b/c/d"}, true));
      statusPage.writeTo(System.out);
   }
}
