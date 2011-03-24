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
import org.exoplatform.ide.git.shared.ResetRequest;
import org.exoplatform.ide.git.shared.ResetRequest.ResetType;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: ResetTest.java 22817 2011-03-22 09:17:52Z andrew00x $
 */
public class ResetTest extends BaseTest
{
   public void testResetHard() throws Exception
   {
      Git git = new Git(getRepository());

      File aaa = addFile(git.getRepository().getWorkTree(), "aaa", "aaa\n");

      FileOutputStream fos = new FileOutputStream(new File(git.getRepository().getWorkTree(), "README.txt"));
      fos.write("MODIFIED\n".getBytes());
      fos.flush();
      fos.close();

      String initMessage = git.log().call().iterator().next().getFullMessage();

      git.add().addFilepattern(".").call();
      git.commit().setMessage("add file").call();

      getConnection().reset(new ResetRequest("HEAD^", ResetRequest.ResetType.HARD));

      // Revert to previous revision.
      assertEquals(initMessage, git.log().call().iterator().next().getFullMessage());
      // Removed.
      assertFalse(aaa.exists());
      checkNoFilesInCache(aaa);
      // previous content.
      assertEquals(CONTENT, readFile(new File(git.getRepository().getWorkTree(), "README.txt")));
   }

   public void testResetSoft() throws Exception
   {
      Git git = new Git(getRepository());

      File aaa = addFile(git.getRepository().getWorkTree(), "aaa", "aaa\n");

      FileOutputStream fos = new FileOutputStream(new File(git.getRepository().getWorkTree(), "README.txt"));
      fos.write("MODIFIED\n".getBytes());
      fos.flush();
      fos.close();

      String initMessage = git.log().call().iterator().next().getFullMessage();

      git.add().addFilepattern(".").call();
      git.commit().setMessage("add file").call();

      getConnection().reset(new ResetRequest("HEAD^", ResetRequest.ResetType.SOFT));

      // Revert to previous revision.
      assertEquals(initMessage, git.log().call().iterator().next().getFullMessage());
      // New file untouched.
      assertTrue(aaa.exists());
      checkFilesInCache(aaa);
      // Modified content.
      assertEquals("MODIFIED\n", readFile(new File(git.getRepository().getWorkTree(), "README.txt")));
   }

   public void testResetMixed() throws Exception
   {
      Git git = new Git(getRepository());

      File aaa = addFile(git.getRepository().getWorkTree(), "aaa", "aaa\n");

      FileOutputStream fos = new FileOutputStream(new File(git.getRepository().getWorkTree(), "README.txt"));
      fos.write("MODIFIED\n".getBytes());
      fos.flush();
      fos.close();

      String initMessage = git.log().call().iterator().next().getFullMessage();

      git.add().addFilepattern(".").call();
      git.commit().setMessage("add file").call();

      getConnection().reset(new ResetRequest("HEAD^", ResetRequest.ResetType.MIXED));

      // Revert to previous revision.
      assertEquals(initMessage, git.log().call().iterator().next().getFullMessage());
      // New file untouched.
      assertTrue(aaa.exists());
      // But removed from index.
      checkNoFilesInCache(aaa);
      // Modified content.
      assertEquals("MODIFIED\n", readFile(new File(git.getRepository().getWorkTree(), "README.txt")));
   }

   public void testResetWithPath() throws Exception
   {
      Git git = new Git(getRepository());

      File aaa = addFile(git.getRepository().getWorkTree(), "aaa", "aaa\n");
      File bbb = addFile(git.getRepository().getWorkTree(), "bbb", "bbb\n");

      FileOutputStream fos = new FileOutputStream(new File(git.getRepository().getWorkTree(), "README.txt"));
      fos.write("MODIFIED\n".getBytes());
      fos.flush();
      fos.close();

      git.add().addFilepattern(".").call();

      checkFilesInCache(aaa);
      checkFilesInCache(bbb);

      getConnection().reset(new ResetRequest(new String[]{"aaa"}));

      // New files untouched.
      assertTrue(aaa.exists());
      checkNoFilesInCache(aaa);
      assertTrue(bbb.exists());
      checkFilesInCache(bbb);
      // Modified content.
      assertEquals("MODIFIED\n", readFile(new File(git.getRepository().getWorkTree(), "README.txt")));
   }

   public void testResetWithPathFail() throws Exception
   {
      Git git = new Git(getRepository());

      File aaa = addFile(git.getRepository().getWorkTree(), "aaa", "aaa\n");
      File bbb = addFile(git.getRepository().getWorkTree(), "bbb", "bbb\n");

      git.add().addFilepattern(".").call();

      checkFilesInCache(aaa);
      checkFilesInCache(bbb);

      ResetRequest request = new ResetRequest(new String[]{"aaa"});
      request.setType(ResetType.HARD);
      try
      {
         getConnection().reset(request);
         fail("Expected exception was not thrown. ");
      }
      catch (IllegalArgumentException e)
      {
         // expected.
      }
   }
}
