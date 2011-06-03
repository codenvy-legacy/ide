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
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.exoplatform.ide.git.shared.GitUser;
import org.exoplatform.ide.git.shared.InitRequest;

import java.io.File;
import java.net.URL;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class RemoteDeleteTest extends BaseTest
{
   private Repository repo;

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      
      // Create clean repository instead use default one.
      URL testCls = Thread.currentThread().getContextClassLoader().getResource(".");
      File target = new File(testCls.toURI()).getParentFile();
      File repoDir = new File(target, "RemoteDeleteTest");
      repoDir.mkdir();
      forClean.add(repoDir);
      repo = new FileRepository(new File(repoDir, ".git"));
      /* May be empty request in this impl. 
       * Working directory already specified but may be not initialized yet.
       * Directory .git does not exists yet. */
      InitRequest request = new InitRequest();
      new JGitConnection(repo, new GitUser("andrey", "andrey@mail.com")).init(request);
      
      StoredConfig config = repo.getConfig();
      RemoteConfig remoteConfig = new RemoteConfig(config, "origin");
      String remoteUrl = getDefaultRepository().getWorkTree().getAbsolutePath();
      remoteConfig.addURI(new URIish(remoteUrl));
      remoteConfig.addPushURI(new URIish(remoteUrl));
      remoteConfig.addFetchRefSpec(new RefSpec("refs/heads/*:refs/remotes/origin/*"));
      config.setString("branch", "master", "remote", "origin");
      config.setString("branch", "master", "merge", "refs/heads/master");
      remoteConfig.update(config);
      config.save();
   }
   
   public void testRemoteDelete() throws Exception
   {
      StoredConfig config = repo.getConfig();
      assertNotNull(config.getString("remote", "origin", "url"));
      assertNotNull(config.getString("remote", "origin", "pushurl"));
      assertNotNull(config.getString("remote", "origin", "fetch"));
      assertNotNull(config.getString("branch", "master", "remote"));
      assertNotNull(config.getString("branch", "master", "merge"));
      //System.out.println(repo.getConfig().toText());
      new JGitConnection(repo, new GitUser("andrey", "andrey@mail.com")).remoteDelete("origin");
      //System.out.println();
      //System.out.println(repo.getConfig().toText());
      config = repo.getConfig();
      assertNull(config.getString("remote", "origin", "url"));
      assertNull(config.getString("remote", "origin", "pushurl"));
      assertNull(config.getString("remote", "origin", "fetch"));
      assertNull(config.getString("branch", "master", "remote"));
      assertNull(config.getString("branch", "master", "merge"));
   }
}
