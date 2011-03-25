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
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.transport.RefSpec;
import org.exoplatform.ide.git.server.jgit.JGitConnection;
import org.exoplatform.ide.git.shared.Branch;
import org.exoplatform.ide.git.shared.BranchListRequest;
import org.exoplatform.ide.git.shared.CloneRequest;
import org.exoplatform.ide.git.shared.GitUser;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: BranchListTest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class BranchListTest extends BaseTest
{
   private Repository repository2;

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      Repository repository = getDefaultRepository();

      File workDir = new File(repository.getWorkTree().getParentFile(), "ListBranchTest");
      // Clone repository.
      JGitConnection client2 = new JGitConnection(new FileRepository(new File(workDir, ".git")));
      client2.clone(new CloneRequest(repository.getWorkTree().getAbsolutePath(), //
         null /* .git directory already set. Not need to pass it in this implementation. */, //
         new GitUser("andrey", "andrey@mail.com")));
      repository2 = client2.getRepository();

      File workDir2 = repository2.getWorkTree();
      forClean.add(workDir2);

      // Add file and commit in remote branch.
      addFile(workDir2, "file1", "init");
      Git git2 = new Git(repository2);
      git2.add().addFilepattern(".").call();
      git2.commit().setMessage("init").setAuthor("andrey", "andrey@mail.com").call();
      git2.push().setRefSpecs(new RefSpec("refs/heads/master:refs/remotes/test")).call();
   }

   public void testListBranchSimple() throws Exception
   {
      BranchListRequest request = new BranchListRequest();
      List<Branch> branchList = getDefaultConnection().branchList(request);
      validateBranchList(branchList, Arrays.asList(new Branch("refs/heads/master", true, "master")));
   }

   public void testListBranchRemote() throws Exception
   {
      BranchListRequest request = new BranchListRequest("r");
      List<Branch> branchList = getDefaultConnection().branchList(request);
      validateBranchList(branchList, Arrays.asList(new Branch("refs/remotes/test", false, "test")));
   }

   // Fails with IllegalArgumentException. Looks like it is not possible get all (local and remote) branches via JGit.
   // IllegalArgumentException is caused by adding 'refs/remotes/test' in RefMap.
   // But getting remote branches ONLY works as well. It seems to be bug in JGit.
   public void __testListBranchAll() throws Exception
   {
      BranchListRequest request = new BranchListRequest("a");
      List<Branch> branchList = getDefaultConnection().branchList(request);
      validateBranchList(branchList,
         Arrays.asList(new Branch("refs/remotes/test", false, "test"), new Branch("refs/heads/master", true, "master")));
   }

   public void testListBranch2() throws Exception
   {
      new Git(getDefaultRepository()).branchCreate().setName("testListBranch2").call();
      BranchListRequest request = new BranchListRequest();
      List<Branch> branchList = getDefaultConnection().branchList(request);
      validateBranchList(branchList, Arrays.asList(new Branch("refs/heads/testListBranch2", false, "testListBranch2"),
         new Branch("refs/heads/master", true, "master")));
   }

   public void testListBranch3() throws Exception
   {
      Git git = new Git(getDefaultRepository());
      git.branchCreate().setName("testListBranch3").call();
      // Make newly created branch active.
      git.checkout().setName("testListBranch3").call();

      BranchListRequest request = new BranchListRequest();
      List<Branch> branchList = getDefaultConnection().branchList(request);
      validateBranchList(branchList, Arrays.asList(new Branch("refs/heads/master", false, "master"), new Branch(
         "refs/heads/testListBranch3", true, "testListBranch3")));
   }
}
