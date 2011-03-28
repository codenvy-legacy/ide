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
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepository;
import org.exoplatform.ide.git.server.jgit.JGitConnection;
import org.exoplatform.ide.git.shared.CloneRequest;
import org.exoplatform.ide.git.shared.FetchRequest;
import org.exoplatform.ide.git.shared.GitUser;

import java.io.File;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: FetchTest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class FetchTest extends BaseTest
{
   private Repository fetchTestRepo;

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();

      Repository origRepository = getDefaultRepository();
      File origWorkDir = origRepository.getWorkTree();

      File fetchWorkDir = new File(origWorkDir.getParentFile(), "FetchTestRepo");
      forClean.add(fetchWorkDir);

      JGitConnection client =
         new JGitConnection(new FileRepository(new File(fetchWorkDir, ".git")),
            new GitUser("andrey", "andrey@mail.com"));
      client.clone(new CloneRequest(origWorkDir.getAbsolutePath(), //
         null /* .git directory already set. Not need to pass it in this implementation. */));

      fetchTestRepo = client.getRepository();

      addFile(origWorkDir, "t-fetch1", "AAA\n");
      addFile(origWorkDir, "t-fetch2", "BBB\n");

      Git git = new Git(origRepository);
      git.add().addFilepattern(".").call();
      git.commit().setMessage("fetch test").setAuthor("andrey", "andrey@mail.com").call();
   }

   public void testFetch() throws Exception
   {
      // Use default remote settings.
      new JGitConnection(fetchTestRepo, new GitUser("andrey", "andrey@mail.com")).fetch(new FetchRequest());

      Git git = new Git(fetchTestRepo);
      git.merge().include(fetchTestRepo.getRef(Constants.FETCH_HEAD)).call();

      File fetchWorkDir = fetchTestRepo.getWorkTree();
      assertTrue(new File(fetchWorkDir, "t-fetch1").exists());
      assertTrue(new File(fetchWorkDir, "t-fetch2").exists());
      assertEquals("fetch test", git.log().call().iterator().next().getFullMessage());
   }

   public void testFetchBranch() throws Exception
   {
      String branchName = "testFetchBranch";
      Repository origin = getDefaultRepository();
      Git originGit = new Git(origin);
      originGit.branchCreate().setName(branchName).call();
      originGit.checkout().setName(branchName).call();
      addFile(origin.getWorkTree(), "aaa", "AAA\n");
      originGit.add().addFilepattern(".").call();
      originGit.commit().setMessage("aaa").call();

      FetchRequest request = new FetchRequest();
      request.setRemote("origin");
      request.setRefSpec(new String[]{/*"refs/heads/" + */branchName});
      new JGitConnection(fetchTestRepo, new GitUser("andrey", "andrey@mail.com")).fetch(request);

      Git newGit = new Git(fetchTestRepo);

      newGit.merge().include(fetchTestRepo.getRef(Constants.FETCH_HEAD)).call();

      File fetchWorkDir = fetchTestRepo.getWorkTree();
      assertTrue(new File(fetchWorkDir, "t-fetch1").exists());
      assertTrue(new File(fetchWorkDir, "t-fetch2").exists());
      assertTrue(new File(fetchWorkDir, "aaa").exists());
      assertEquals("aaa", newGit.log().call().iterator().next().getFullMessage());
   }
}
