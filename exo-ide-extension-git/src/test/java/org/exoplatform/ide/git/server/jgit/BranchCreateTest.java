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
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.exoplatform.ide.git.shared.Branch;
import org.exoplatform.ide.git.shared.BranchCreateRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: BranchCreateTest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class BranchCreateTest extends BaseTest
{
   public void testCreateBranch() throws Exception
   {
      testBranch("new-branch", null, new File[]{new File(getDefaultRepository().getWorkTree(), "README.txt")}, new File[0]);
   }

   public void testCreateBranchRevision() throws Exception
   {
      Git git = new Git(getDefaultRepository());
      File file1 = addFile(getDefaultRepository().getWorkTree(), "file1", "file1");
      git.add().addFilepattern(".").call();
      git.commit().setMessage("file1").setAuthor("andrey", "andrey@mail.com").call();

      Iterator<RevCommit> commitIter = git.log().call().iterator();
      commitIter.next();
      RevCommit commit = commitIter.next();

      testBranch("new-branch", commit.getId().getName(), new File[]{new File(getDefaultRepository().getWorkTree(),
         "README.txt")}, new File[]{file1});
   }

   private void testBranch(String name, String start, File[] exists, File[] notExists) throws Exception
   {
      Branch branch = getDefaultConnection().branchCreate(new BranchCreateRequest(name, start));

      Git git = new Git(getDefaultRepository());
      List<Ref> branches = git.branchList().call();
      assertEquals(2, branches.size());
      List<String> bNames = new ArrayList<String>(2);
      for (Ref br : branches)
         bNames.add(br.getName());
      assertTrue(bNames.contains("refs/heads/master"));
      assertTrue(bNames.contains(branch.getName()));

      git.checkout().setName(branch.getDisplayName()).call();

      File workDir = git.getRepository().getWorkTree();
      for (File f : exists)
         assertTrue("Expected file " + calculateRelativePath(workDir, f) + " not found. ", f.exists());

      for (File f : notExists)
         assertTrue("Unexpected file " + calculateRelativePath(workDir, f) + " found. ", !f.exists());
   }
}
