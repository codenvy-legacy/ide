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

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.exoplatform.ide.git.shared.GitUser;
import org.exoplatform.ide.git.shared.InitRequest;
import org.exoplatform.ide.git.shared.RemoteUpdateRequest;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: RemoteUpdateTest.java 70163 2011-06-03 13:37:45Z andrew00x $
 */
public class RemoteUpdateTest extends BaseTest {
    private Repository repo;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Create clean repository instead use default one.
        URL testCls = Thread.currentThread().getContextClassLoader().getResource(".");
        File target = new File(testCls.toURI()).getParentFile();
        File repoDir = new File(target, "RemoteUpdateTest");
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
        remoteConfig.addFetchRefSpec(new RefSpec("+refs/heads/*:refs/remotes/origin/*"));
        remoteConfig.update(config);
        config.save();
    }

    public void testUpdateBranches() throws Exception {
        RemoteUpdateRequest request =
                new RemoteUpdateRequest("origin", new String[]{"test", "master"}, false, null, null, null, null);
        new JGitConnection(repo, new GitUser("andrey", "andrey@mail.com")).remoteUpdate(request);

        StoredConfig config = repo.getConfig();
        RemoteConfig remoteConfig = new RemoteConfig(config, "origin");
        List<RefSpec> fetchRefSpecs = remoteConfig.getFetchRefSpecs();
        assertEquals(2, fetchRefSpecs.size());
        assertTrue(fetchRefSpecs.contains(new RefSpec("+refs/heads/test:refs/remotes/origin/test")));
        assertTrue(fetchRefSpecs.contains(new RefSpec("+refs/heads/master:refs/remotes/origin/master")));
    }

    public void testUpdateBranchesAdd() throws Exception {
        StoredConfig config = repo.getConfig();
        RemoteConfig remoteConfig = new RemoteConfig(config, "origin");
        remoteConfig.setFetchRefSpecs(new ArrayList<RefSpec>());
        remoteConfig.addFetchRefSpec(new RefSpec("+refs/heads/master:refs/remotes/origin/master"));
        remoteConfig.update(config);
        config.save();

        RemoteUpdateRequest request =
                new RemoteUpdateRequest("origin", new String[]{"test"}, true, null, null, null, null);
        new JGitConnection(repo, new GitUser("andrey", "andrey@mail.com")).remoteUpdate(request);

        config.load();
        remoteConfig = new RemoteConfig(config, "origin");
        List<RefSpec> fetchRefSpecs = remoteConfig.getFetchRefSpecs();
        assertEquals(2, fetchRefSpecs.size());
        assertTrue(fetchRefSpecs.contains(new RefSpec("+refs/heads/test:refs/remotes/origin/test")));
        assertTrue(fetchRefSpecs.contains(new RefSpec("+refs/heads/master:refs/remotes/origin/master")));
    }

    public void testUpdateBranchesReplace() throws Exception {
        StoredConfig config = repo.getConfig();
        RemoteConfig remoteConfig = new RemoteConfig(config, "origin");
        remoteConfig.setFetchRefSpecs(new ArrayList<RefSpec>());
        remoteConfig.addFetchRefSpec(new RefSpec("+refs/heads/master:refs/remotes/origin/master"));
        remoteConfig.update(config);
        config.save();

        RemoteUpdateRequest request =
                new RemoteUpdateRequest("origin", new String[]{"test"}, false, null, null, null, null);
        new JGitConnection(repo, new GitUser("andrey", "andrey@mail.com")).remoteUpdate(request);

        config.load();
        remoteConfig = new RemoteConfig(config, "origin");
        List<RefSpec> fetchRefSpecs = remoteConfig.getFetchRefSpecs();
        assertEquals(1, fetchRefSpecs.size());
        assertTrue(fetchRefSpecs.contains(new RefSpec("+refs/heads/test:refs/remotes/origin/test")));
    }
}
