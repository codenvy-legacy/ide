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

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileRepository;
import org.exoplatform.ide.git.shared.GitUser;
import org.exoplatform.ide.git.shared.InitRequest;
import org.exoplatform.ide.git.shared.RemoteAddRequest;

import java.io.File;
import java.net.URL;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class RemoteAddTest extends BaseTest
{
   private Repository repo;

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      
      // Create clean repository instead use default one.
      URL testCls = Thread.currentThread().getContextClassLoader().getResource(".");
      File target = new File(testCls.toURI()).getParentFile();
      File repoDir = new File(target, "RemoteAddTest");
      repoDir.mkdir();
      forClean.add(repoDir);
      repo = new FileRepository(new File(repoDir, ".git"));
      /* May be empty request in this impl. 
       * Working directory already specified but may be not initialized yet.
       * Directory .git does not exists yet. */
      InitRequest request = new InitRequest();
      new JGitConnection(repo, new GitUser("andrey", "andrey@mail.com")).init(request);
   }

   public void testRemoteAdd() throws Exception
   {
      String remoteUrl = getDefaultRepository().getWorkTree().getAbsolutePath();
      new JGitConnection(repo, new GitUser("andrey", "andrey@mail.com")).remoteAdd(new RemoteAddRequest("origin", remoteUrl));
      StoredConfig config = repo.getConfig();
      assertEquals(remoteUrl, config.getString("remote", "origin", "url"));
      assertEquals("+refs/heads/*:refs/remotes/origin/*", config.getString("remote", "origin", "fetch"));
   }

   public void testRemoteAddWithBranches() throws Exception
   {
      String remoteUrl = getDefaultRepository().getWorkTree().getAbsolutePath();
      new JGitConnection(repo, new GitUser("andrey", "andrey@mail.com")).remoteAdd(new RemoteAddRequest("origin", remoteUrl, new String[]{"test"}));
      StoredConfig config = repo.getConfig();
      assertEquals(remoteUrl, config.getString("remote", "origin", "url"));
      assertEquals("+refs/heads/test:refs/remotes/origin/test", config.getString("remote", "origin", "fetch"));
   }
}
