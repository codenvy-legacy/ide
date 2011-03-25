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
import org.exoplatform.ide.git.shared.InitRequest;
import org.exoplatform.ide.git.shared.RemoteUpdateRequest;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class RemoteUpdateTest extends BaseTest
{
   private Repository repo;

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();

      // Create clean repository instead use default one.
      URL testCls = Thread.currentThread().getContextClassLoader().getResource(".");
      File target = new File(testCls.toURI()).getParentFile();
      File repoDir = new File(target, "RemoteUpdateTest");
      forClean.add(repoDir);
      repo = new FileRepository(new File(repoDir, ".git"));
      /* May be empty request in this impl. 
       * Working directory already specified but may be not initialized yet.
       * Directory .git does not exists yet. */
      InitRequest request = new InitRequest();
      new JGitConnection(repo).init(request);

      StoredConfig config = repo.getConfig();
      RemoteConfig remoteConfig = new RemoteConfig(config, "origin");
      String remoteUrl = getDefaultRepository().getWorkTree().getAbsolutePath();
      remoteConfig.addURI(new URIish(remoteUrl));
      remoteConfig.addPushURI(new URIish(remoteUrl));
      remoteConfig.addFetchRefSpec(new RefSpec("+refs/heads/*:refs/remotes/origin/*"));
      remoteConfig.update(config);
      config.save();
   }

   public void testUpdateBranches() throws Exception
   {
      RemoteUpdateRequest request =
         new RemoteUpdateRequest("origin", new String[]{"test", "master"}, false, null, null, null, null);
      new JGitConnection(repo).remoteUpdate(request);
      
      StoredConfig config = repo.getConfig();
      RemoteConfig remoteConfig = new RemoteConfig(config, "origin");
      List<RefSpec> fetchRefSpecs = remoteConfig.getFetchRefSpecs();
      assertEquals(2, fetchRefSpecs.size());
      assertTrue(fetchRefSpecs.contains(new RefSpec("+refs/heads/test:refs/remotes/origin/test")));
      assertTrue(fetchRefSpecs.contains(new RefSpec("+refs/heads/master:refs/remotes/origin/master")));
   }

   public void testUpdateBranchesAdd() throws Exception
   {
      StoredConfig config = repo.getConfig();
      RemoteConfig remoteConfig = new RemoteConfig(config, "origin");
      remoteConfig.setFetchRefSpecs(new ArrayList<RefSpec>());
      remoteConfig.addFetchRefSpec(new RefSpec("+refs/heads/master:refs/remotes/origin/master"));
      remoteConfig.update(config);
      config.save();

      RemoteUpdateRequest request =
         new RemoteUpdateRequest("origin", new String[]{"test"}, true, null, null, null, null);
      new JGitConnection(repo).remoteUpdate(request);
      
      config.load();
      remoteConfig = new RemoteConfig(config, "origin");
      List<RefSpec> fetchRefSpecs = remoteConfig.getFetchRefSpecs();
      assertEquals(2, fetchRefSpecs.size());
      assertTrue(fetchRefSpecs.contains(new RefSpec("+refs/heads/test:refs/remotes/origin/test")));
      assertTrue(fetchRefSpecs.contains(new RefSpec("+refs/heads/master:refs/remotes/origin/master")));
   }

   public void testUpdateBranchesReplace() throws Exception
   {
      StoredConfig config = repo.getConfig();
      RemoteConfig remoteConfig = new RemoteConfig(config, "origin");
      remoteConfig.setFetchRefSpecs(new ArrayList<RefSpec>());
      remoteConfig.addFetchRefSpec(new RefSpec("+refs/heads/master:refs/remotes/origin/master"));
      remoteConfig.update(config);
      config.save();

      RemoteUpdateRequest request =
         new RemoteUpdateRequest("origin", new String[]{"test"}, false, null, null, null, null);
      new JGitConnection(repo).remoteUpdate(request);
      
      config.load();
      remoteConfig = new RemoteConfig(config, "origin");
      List<RefSpec> fetchRefSpecs = remoteConfig.getFetchRefSpecs();
      assertEquals(1, fetchRefSpecs.size());
      assertTrue(fetchRefSpecs.contains(new RefSpec("+refs/heads/test:refs/remotes/origin/test")));
   }
}
