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
import org.exoplatform.ide.git.shared.BranchDeleteRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: BranchDeleteTest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class BranchDeleteTest extends BaseTest
{
   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      Git git = new Git(getRepository());
      git.branchCreate().setName("branch1").call();
      git.branchCreate().setName("branch2").call();
   }

   public void testDelete() throws Exception
   {
      getClient().branchDelete(new BranchDeleteRequest("branch1", false));
      testBranch(new String[]{"refs/heads/master", "refs/heads/branch2"});
   }

   public void testDeleteCurrent() throws Exception
   {
      try
      {
         getClient().branchDelete(new BranchDeleteRequest("master", true));
         fail("Expected exception was not thrown. ");
      }
      catch (IllegalArgumentException e)
      {
         // expected
      }
      testBranch(new String[]{"refs/heads/master", "refs/heads/branch1", "refs/heads/branch2"});
   }

   public void testDeleteNotMerged() throws Exception
   {
      Git git = new Git(getRepository());
      git.checkout().setName("branch2").call();
      addFile(getRepository().getWorkTree(), "br2-file", "aaa");
      git.add().addFilepattern(".").call();
      git.commit().setMessage("br2 commit").setAuthor("andrey", "andrey@mail.com").call();
      git.checkout().setName("master").call();
      BranchDeleteRequest request = new BranchDeleteRequest("branch2", false);
      try
      {
         getClient().branchDelete(request);
         fail("Expected exception was not thrown. ");
      }
      catch (IllegalArgumentException e)
      {
         // expected
      }
      testBranch(new String[]{"refs/heads/master", "refs/heads/branch1", "refs/heads/branch2"});

      request.setForce(true);
      // Able to delete now.
      getClient().branchDelete(request);

      testBranch(new String[]{"refs/heads/master", "refs/heads/branch1"});
   }

   private void testBranch(String[] exp) throws Exception
   {
      List<Ref> list = new Git(getRepository()).branchList().call();
      assertEquals(exp.length, list.size());
      List<String> refNames = new ArrayList<String>(list.size());
      for (Ref refName : list)
         refNames.add(refName.getName());
      for (String e : exp)
         assertTrue("Not found " + e + " branch. ", refNames.contains(e));
   }
}
