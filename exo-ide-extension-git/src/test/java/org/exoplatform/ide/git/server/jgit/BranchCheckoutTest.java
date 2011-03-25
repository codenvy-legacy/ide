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
import org.exoplatform.ide.git.shared.BranchCheckoutRequest;

import java.io.File;
import java.util.List;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: BranchCheckoutTest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class BranchCheckoutTest extends BaseTest
{
   private final String branch1 = "BranchCheckoutTest1";
   private final String branch2 = "BranchCheckoutTest2";

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      Git git = new Git(getDefaultRepository());

      git.branchCreate().setName(branch1).call();
      git.checkout().setName(branch1).call();
      addFile(getDefaultRepository().getWorkTree(), "br1", "aaa");
      git.add().addFilepattern(".").call();
      git.commit().setMessage("aaa").call();

      git.checkout().setName("master").call();
   }

   public void testCheckout() throws Exception
   {
      BranchCheckoutRequest request = new BranchCheckoutRequest(branch1, null, false);
      getDefaultConnection().branchCheckout(request);
      assertTrue(new File(getDefaultRepository().getWorkTree(), "br1").exists());
      assertTrue(new File(getDefaultRepository().getWorkTree(), "README.txt").exists());
   }

   public void testCheckoutCreate() throws Exception
   {
      List<Ref> all = new Git(getDefaultRepository()).branchList().call();
      assertEquals(2, all.size());

      getDefaultConnection().branchCheckout(new BranchCheckoutRequest(branch2, null, true));

      all = new Git(getDefaultRepository()).branchList().call();
      assertEquals(3, all.size());
      assertTrue(new File(getDefaultRepository().getWorkTree(), "README.txt").exists());
   }

   public void testCheckoutCreateWithStartPoint() throws Exception
   {
      List<Ref> all = new Git(getDefaultRepository()).branchList().call();
      assertEquals(2, all.size());

      getDefaultConnection().branchCheckout(new BranchCheckoutRequest(branch2, branch1, true));

      all = new Git(getDefaultRepository()).branchList().call();

      assertEquals(3, all.size());
      assertTrue(new File(getDefaultRepository().getWorkTree(), "br1").exists());
      assertTrue(new File(getDefaultRepository().getWorkTree(), "README.txt").exists());
   }
}
