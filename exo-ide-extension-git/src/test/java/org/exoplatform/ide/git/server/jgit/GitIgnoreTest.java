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
import org.exoplatform.ide.git.server.GitHelper;
import org.exoplatform.ide.git.server.StatusPage;
import org.exoplatform.ide.git.shared.GitFile;
import org.exoplatform.ide.git.shared.StatusRequest;

import java.io.File;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Oct 4, 2011 evgen $
 */
public class GitIgnoreTest extends BaseTest
{
   private Repository repository;

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      repository = getDefaultRepository();
      Git git = new Git(repository);
      addFile(repository.getWorkTree(), "added_commited", "xxxxx");
      File a = new File(repository.getWorkTree(), "a");
      File b = new File(a, "b");
      b.mkdirs();
      GitHelper.addToGitIgnore(a, "b/");
      git.add().addFilepattern(".").call();
      git.commit().setMessage("add .gitignore file").call();
      addFile(b, "ignored", "xxxxx");
   }
   
   public void testStatusWithGitIgnore() throws Exception
   {
      StatusPage statusPage = getDefaultConnection().status(new StatusRequest());
      statusPage.writeTo(System.out);
      assertEquals("master", statusPage.getBranchName());
      List<GitFile> untracked = statusPage.getUntracked();
      assertEquals(0, untracked.size());
   }
}
