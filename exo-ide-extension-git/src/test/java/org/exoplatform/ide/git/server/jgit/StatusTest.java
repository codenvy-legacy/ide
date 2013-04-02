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
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Repository;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: StatusTest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class StatusTest extends BaseTest
{
   public void testEmptyStatus() throws NoWorkTreeException, GitAPIException
   {
      Git git = new Git(getDefaultRepository());

      Status stat = git.status().call();
      assertEquals(0, stat.getAdded().size());
      assertEquals(0, stat.getChanged().size());
      assertEquals(0, stat.getMissing().size());
      assertEquals(0, stat.getModified().size());
      assertEquals(0, stat.getRemoved().size());
      assertEquals(0, stat.getUntracked().size());
   }

   public void testDifferentStates() throws IOException, NoFilepatternException, GitAPIException
   {
      Repository re = getDefaultRepository();
      File repo = re.getWorkTree();

      Git git = new Git(re);
      addFile(repo, "a", "content of a");
      addFile(repo, "b", "content of b");
      addFile(repo, "c", "content of c");
      git.add().addFilepattern("a").addFilepattern("b").call();
      Status stat = git.status().call();
      assertEquals(set("a", "b"), stat.getAdded());
      assertEquals(0, stat.getChanged().size());
      assertEquals(0, stat.getMissing().size());
      assertEquals(0, stat.getModified().size());
      assertEquals(0, stat.getRemoved().size());
      assertEquals(set("c"), stat.getUntracked());
      git.commit().setMessage("initial").call();

      addFile(repo, "a", "modified content of a");
      addFile(repo, "b", "modified content of b");
      addFile(repo, "d", "content of d");
      git.add().addFilepattern("a").addFilepattern("d").call();
      addFile(repo, "a", "again modified content of a");
      stat = git.status().call();
      assertEquals(set("d"), stat.getAdded());
      assertEquals(set("a"), stat.getChanged());
      assertEquals(0, stat.getMissing().size());
      assertEquals(set("b", "a"), stat.getModified());
      assertEquals(0, stat.getRemoved().size());
      assertEquals(set("c"), stat.getUntracked());
      git.add().addFilepattern(".").call();
      git.commit().setMessage("second").call();

      stat = git.status().call();
      assertEquals(0, stat.getAdded().size());
      assertEquals(0, stat.getChanged().size());
      assertEquals(0, stat.getMissing().size());
      assertEquals(0, stat.getModified().size());
      assertEquals(0, stat.getRemoved().size());
      assertEquals(0, stat.getUntracked().size());

      delete(new File(repo.getPath() + "/a"));
      assertFalse(new File(git.getRepository().getWorkTree(), "a").exists());
      git.add().addFilepattern("a").setUpdate(true).call();
      addFile(repo, "a", "recreated content of a");
      stat = git.status().call();
      assertEquals(0, stat.getAdded().size());
      assertEquals(0, stat.getChanged().size());
      assertEquals(0, stat.getMissing().size());
      assertEquals(0, stat.getModified().size());
      assertEquals(set("a"), stat.getRemoved());
      assertEquals(set("a"), stat.getUntracked());
      git.commit().setMessage("t").call();

      addFile(repo, "sub/a", "sub-file");
      stat = git.status().call();
      assertEquals(1, stat.getUntrackedFolders().size());
      assertTrue(stat.getUntrackedFolders().contains("sub"));
   }

   public static Set<String> set(String... elements)
   {
      Set<String> ret = new HashSet<String>();
      for (String element : elements)
         ret.add(element);
      return ret;
   }
}
