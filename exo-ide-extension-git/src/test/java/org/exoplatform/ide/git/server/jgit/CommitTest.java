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
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.FileTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.exoplatform.ide.git.shared.CommitRequest;
import org.exoplatform.ide.git.shared.Revision;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: CommitTest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class CommitTest extends BaseTest
{
   public void testCommit() throws Exception
   {
      // Add file.
      addFile(getDefaultRepository().getWorkTree(), "file1", "file1");

      Git git = new Git(getDefaultRepository());
      git.add().addFilepattern(".").call();

      CommitRequest request = new CommitRequest("add file1");
      Revision revision = getDefaultConnection().commit(request);

      RevCommit revCommit = git.log().call().iterator().next();

      assertEquals("add file1", revision.getMessage());
      assertEquals(revCommit.getId().getName(), revision.getId());
      assertEquals("andrey", revision.getCommitter().getName());
      assertEquals("andrey@mail.com", revision.getCommitter().getEmail());
   }

   public void testCommitAll() throws Exception
   {
      // Add file and commit.
      Repository repository = getDefaultRepository();
      File file = addFile(repository.getWorkTree(), "file2", "file2");

      Git git = new Git(repository);
      git.add().addFilepattern(".").call();
      git.commit().setMessage("add file2").call();

      // Update file.
      FileOutputStream fileStream = new FileOutputStream(file);
      fileStream.write("updated".getBytes());
      fileStream.close();
      
      // Remove.
      delete(new File(repository.getWorkTree(), "README.txt"));

      CommitRequest request = new CommitRequest("update file2", true);
      Revision revision = getDefaultConnection().commit(request);

      RevCommit revCommit = git.log().call().iterator().next();

      assertEquals("update file2", revision.getMessage());
      assertEquals(revCommit.getId().getName(), revision.getId());
      assertEquals("andrey", revision.getCommitter().getName());
      assertEquals("andrey@mail.com", revision.getCommitter().getEmail());

      checkNoFilesInCache(repository, "README.txt");

      TreeWalk treeWalk = new TreeWalk(repository);
      treeWalk.reset();
      treeWalk.setRecursive(true);
      try
      {
         ObjectId head = repository.resolve(Constants.HEAD);

         RevWalk revWalk = new RevWalk(repository);
         RevTree headTree;
         try
         {
            headTree = revWalk.parseTree(head);
         }
         finally
         {
            revWalk.release();
         }
         treeWalk.addTree(headTree);
         treeWalk.addTree(new FileTreeIterator(repository));
         treeWalk.setFilter(TreeFilter.ANY_DIFF);
         // Index and file system the same so changes added.
         assertFalse(treeWalk.next());
      }
      finally
      {
         treeWalk.release();
      }
   }
}
