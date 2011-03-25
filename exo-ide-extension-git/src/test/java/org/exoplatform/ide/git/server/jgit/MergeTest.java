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
import org.exoplatform.ide.git.shared.MergeRequest;
import org.exoplatform.ide.git.shared.MergeResult;

import java.io.File;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: MergeTest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class MergeTest extends BaseTest
{
   private String branchName = "MergeTestBranch";

   public void testMergeNoChanges() throws Exception
   {
      Git git = new Git(getDefaultRepository());
      git.branchCreate().setName(branchName).call();
      MergeResult mergeResult = getDefaultConnection().merge(new MergeRequest("MergeTestBranch"));
      assertEquals(MergeResult.MergeStatus.ALREADY_UP_TO_DATE, mergeResult.getMergeStatus());
   }

   public void testMerge() throws Exception
   {
      Git git = new Git(getDefaultRepository());
      git.branchCreate().setName(branchName).call();
      git.checkout().setName(branchName).call();
      File file = addFile(git.getRepository().getWorkTree(), "t-merge", "aaa\n");

      git.add().addFilepattern(".").call();
      git.commit().setMessage("add file in new branch").call();
      git.checkout().setName("master").call();

      MergeResult mergeResult = getDefaultConnection().merge(new MergeRequest("MergeTestBranch"));
      assertEquals(MergeResult.MergeStatus.FAST_FORWARD, mergeResult.getMergeStatus());
      assertTrue(file.exists());
      assertEquals("aaa\n", readFile(file));
      assertEquals("add file in new branch", git.log().call().iterator().next().getFullMessage());
   }

   public void testMergeConflict() throws Exception
   {
      Git git = new Git(getDefaultRepository());
      git.branchCreate().setName(branchName).call();
      git.checkout().setName(branchName).call();
      addFile(git.getRepository().getWorkTree(), "t-merge-conflict", "aaa\n");

      git.add().addFilepattern(".").call();
      git.commit().setMessage("add file in new branch").call();
      git.checkout().setName("master").call();

      addFile(git.getRepository().getWorkTree(), "t-merge-conflict", "bbb\n");
      git.add().addFilepattern(".").call();
      git.commit().setMessage("add file in new master").call();

      MergeResult mergeResult = getDefaultConnection().merge(new MergeRequest("MergeTestBranch"));
      String[] conflicts = mergeResult.getConflicts();
      assertEquals(1, conflicts.length);
      assertEquals("t-merge-conflict", conflicts[0]);

      assertEquals(MergeResult.MergeStatus.CONFLICTING, mergeResult.getMergeStatus());

      String expContent = "<<<<<<< HEAD\n" //
         + "bbb\n" //
         + "=======\n" //
         + "aaa\n" //
         + ">>>>>>> refs/heads/MergeTestBranch\n";

      String actual = readFile(new File(git.getRepository().getWorkTree(), "t-merge-conflict"));
      assertEquals(expContent, readFile(new File(git.getRepository().getWorkTree(), "t-merge-conflict")));
      System.out.println(actual);
   }
}
