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
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepository;
import org.exoplatform.ide.git.server.jgit.JGitConnection;
import org.exoplatform.ide.git.shared.CloneRequest;
import org.exoplatform.ide.git.shared.GitUser;
import org.exoplatform.ide.git.shared.PushRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: PushTest.java 22817 2011-03-22 09:17:52Z andrew00x $
 */
public class PushTest extends BaseTest
{
   private Repository pushTestRepo;

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();

      File pushWorkDir = new File(getDefaultRepository().getWorkTree().getParentFile(), "PushTestRepo");
      forClean.add(pushWorkDir);

      JGitConnection client = new JGitConnection(new FileRepository(new File(pushWorkDir, ".git")));
      client.clone(new CloneRequest(getDefaultRepository().getWorkTree().getAbsolutePath(), //
         null/* .git directory already set. Not need to pass it in this implementation. */,//
         new GitUser("andrey", "andrey@mail.com")));
      pushTestRepo = client.getRepository();
   }

   public void testPush() throws Exception
   {
      addFile(pushTestRepo.getWorkTree(), "testPush", CONTENT);
      Git git = new Git(pushTestRepo);
      git.add().addFilepattern(".").call();
      git.commit().setMessage("init").setAuthor("andrey", "andrey@mail.com").call();

      String remote = "origin";
      boolean force = false;

      new JGitConnection(pushTestRepo).push(new PushRequest(new String[]{"refs/heads/master:refs/heads/test"}, remote,
         force, 0));

      Git origGit = new Git(getDefaultRepository());
      List<Ref> branches = origGit.branchList().call();
      List<String> bNames = new ArrayList<String>(2);
      for (Ref br : branches)
         bNames.add(br.getName());
      assertTrue(bNames.contains("refs/heads/master"));
      assertTrue(bNames.contains("refs/heads/test"));

      origGit.checkout().setName("test").call();

      File workDir = origGit.getRepository().getWorkTree();
      assertTrue(new File(workDir, "README.txt").exists());
      assertTrue(new File(workDir, "testPush").exists());
   }

   public void testPushRemote() throws Exception
   {
      File remoteWorkDir = new File(getDefaultRepository().getWorkTree().getParentFile(), "RemoteRepo");
      forClean.add(remoteWorkDir);

      Git remoteGit = Git.init().setDirectory(remoteWorkDir).call();

      // XXX : Need add file to init 'master' branch. If not then checkout operation fails with NPE.
      addFile(remoteWorkDir, "init", "init");
      remoteGit.add().addFilepattern(".").call();
      remoteGit.commit().setMessage("init").call();

      String remote = remoteWorkDir.getAbsolutePath();
      boolean force = false;

      new JGitConnection(getDefaultRepository()).push(new PushRequest(new String[]{"refs/heads/master:refs/heads/test"}, remote,
         force, 0));

      // Check remote repository.
      List<Ref> branches = remoteGit.branchList().call();
      List<String> bNames = new ArrayList<String>(2);
      for (Ref br : branches)
         bNames.add(br.getName());
      assertTrue(bNames.contains("refs/heads/master"));
      assertTrue(bNames.contains("refs/heads/test"));

      remoteGit.checkout().setName("test").setForce(true).call();

      assertTrue(new File(remoteWorkDir, "README.txt").exists());
   }
}
