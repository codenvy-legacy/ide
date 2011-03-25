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
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheEntry;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.lib.UserConfig;
import org.eclipse.jgit.storage.file.FileRepository;
import org.exoplatform.ide.git.server.jgit.JGitConnection;
import org.exoplatform.ide.git.shared.CloneRequest;
import org.exoplatform.ide.git.shared.GitUser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: CloneTest.java 22817 2011-03-22 09:17:52Z andrew00x $
 */
public class CloneTest extends BaseTest
{
   private File cloneRepoDir;

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      cloneRepoDir = new File(getDefaultRepository().getWorkTree().getParentFile(), "repository2");
      forClean.add(cloneRepoDir);
   }

   public void testSimpleClone() throws Exception
   {
      Repository repository = getDefaultRepository();
      JGitConnection client = new JGitConnection(new FileRepository(new File(cloneRepoDir, ".git")));
      client.clone(new CloneRequest(repository.getWorkTree().getAbsolutePath(), //
         null /* .git directory already set. Not need to pass it in this implementation. */, //
         new GitUser("andrey", "andrey@mail.com")));
      Repository clone = client.getRepository();

      assertEquals(cloneRepoDir.getAbsolutePath(), clone.getWorkTree().getAbsolutePath());

      StoredConfig config = clone.getConfig();
      UserConfig userConfig = config.get(UserConfig.KEY);
      assertEquals("andrey", userConfig.getAuthorName());
      assertEquals("andrey", userConfig.getCommitterName());
      assertEquals("andrey@mail.com", userConfig.getAuthorEmail());
      assertEquals("andrey@mail.com", userConfig.getCommitterEmail());

      List<File> files = new ArrayList<File>(1);
      DirCache dirCache = null;
      try
      {
         dirCache = clone.lockDirCache();
         for (int i = 0; i < dirCache.getEntryCount(); ++i)
         {
            DirCacheEntry e = dirCache.getEntry(i);
            File file = new File(cloneRepoDir, e.getPathString());
            files.add(file);
         }
      }
      finally
      {
         if (dirCache != null)
            dirCache.unlock();
      }
      assertEquals(1, files.size());
      assertEquals(CONTENT, readFile(files.get(0)));
   }

   public void testCloneBranch() throws Exception
   {
      Repository repository = getDefaultRepository();
      Git git = new Git(repository);
      git.branchCreate().setName("featured").call();

      JGitConnection client = new JGitConnection(new FileRepository(new File(cloneRepoDir, ".git")));
      CloneRequest request = new CloneRequest(repository.getWorkTree().getAbsolutePath(), //
         null /* .git directory already set. Not need to pass it in this implementation. */, //
         new GitUser("andrey", "andrey@mail.com"));
      request.setBranchesToFetch(new String[]{"refs/heads/featured:refs/remotes/origin/featured"});
      client.clone(request);

      Repository clone = client.getRepository();
      Git cloneGit = new Git(clone);
      List<Ref> brlist = cloneGit.branchList().setListMode(ListMode.REMOTE).call();
      List<String> brnames = new ArrayList<String>(brlist.size());
      for (Ref ref : brlist)
         brnames.add(ref.getName());
      assertEquals(1, brnames.size());
      assertEquals("refs/remotes/origin/featured", brnames.get(0));
   }
}
