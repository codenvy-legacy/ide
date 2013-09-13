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
import org.exoplatform.ide.git.shared.GitUser;
import org.exoplatform.ide.git.shared.InitRequest;
import org.exoplatform.ide.git.shared.RemoteAddRequest;

import java.io.File;
import java.net.URL;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: RemoteAddTest.java 70163 2011-06-03 13:37:45Z andrew00x $
 */
public class RemoteAddTest extends BaseTest {
    private Repository repo;

    @Override
    protected void setUp() throws Exception {
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

    public void testRemoteAdd() throws Exception {
        String remoteUrl = getDefaultRepository().getWorkTree().getAbsolutePath();
        new JGitConnection(repo, new GitUser("andrey", "andrey@mail.com")).remoteAdd(new RemoteAddRequest("origin", remoteUrl));
        StoredConfig config = repo.getConfig();
        assertEquals(remoteUrl, config.getString("remote", "origin", "url"));
        assertEquals("+refs/heads/*:refs/remotes/origin/*", config.getString("remote", "origin", "fetch"));
    }

    public void testRemoteAddWithBranches() throws Exception {
        String remoteUrl = getDefaultRepository().getWorkTree().getAbsolutePath();
        new JGitConnection(repo, new GitUser("andrey", "andrey@mail.com"))
                .remoteAdd(new RemoteAddRequest("origin", remoteUrl, new String[]{"test"}));
        StoredConfig config = repo.getConfig();
        assertEquals(remoteUrl, config.getString("remote", "origin", "url"));
        assertEquals("+refs/heads/test:refs/remotes/origin/test", config.getString("remote", "origin", "fetch"));
    }
}
