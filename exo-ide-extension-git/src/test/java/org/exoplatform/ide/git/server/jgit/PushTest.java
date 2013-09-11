/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.git.server.jgit;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepository;
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
public class PushTest extends BaseTest {
    private Repository pushTestRepo;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        File pushWorkDir = new File(getDefaultRepository().getWorkTree().getParentFile(), "PushTestRepo");
        forClean.add(pushWorkDir);

        JGitConnection client =
                new JGitConnection(new FileRepository(new File(pushWorkDir, ".git")), new GitUser("andrey", "andrey@mail.com"));
        client.clone(new CloneRequest(getDefaultRepository().getWorkTree().getAbsolutePath(), //
                                      null/* .git directory already set. Not need to pass it in this implementation. */));
        pushTestRepo = client.getRepository();
    }

    public void testPush() throws Exception {
        addFile(pushTestRepo.getWorkTree(), "testPush", CONTENT);
        Git git = new Git(pushTestRepo);
        git.add().addFilepattern(".").call();
        git.commit().setMessage("init").setAuthor("andrey", "andrey@mail.com").call();

        String remote = "origin";
        boolean force = false;

        new JGitConnection(pushTestRepo, new GitUser("andrey", "andrey@mail.com")).push(new PushRequest(
                new String[]{"refs/heads/master:refs/heads/test"}, remote, force, 0));

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

    public void testPushRemote() throws Exception {
        File remoteWorkDir = new File(getDefaultRepository().getWorkTree().getParentFile(), "RemoteRepo");
        forClean.add(remoteWorkDir);

        Git remoteGit = Git.init().setDirectory(remoteWorkDir).call();

        // XXX : Need commit to init 'master' branch. If not then checkout operation fails with NPE.
        remoteGit.add().addFilepattern(".").call();
        remoteGit.commit().setMessage("init").call();

        String remote = remoteWorkDir.getAbsolutePath();
        boolean force = false;

        new JGitConnection(getDefaultRepository(), new GitUser("andrey", "andrey@mail.com")).push(new PushRequest(
                new String[]{"refs/heads/master:refs/heads/test"}, remote, force, 0));

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
