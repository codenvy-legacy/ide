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
import org.eclipse.jgit.lib.UserConfig;
import org.eclipse.jgit.storage.file.FileRepository;
import org.exoplatform.ide.git.shared.GitUser;
import org.exoplatform.ide.git.shared.InitRequest;

import java.io.File;
import java.net.URL;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: InitTest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class InitTest extends BaseTest {
    private File workDir;

    @Override
    protected void setUp() throws Exception {
        URL testCls = Thread.currentThread().getContextClassLoader().getResource(".");
        File target = new File(testCls.toURI()).getParentFile();
        workDir = new File(target, "InitRepoTest");
        workDir.mkdir();
        forClean.add(workDir);
    }

    public void testInitRepo() throws Exception {
        JGitConnection client =
                new JGitConnection(new FileRepository(new File(workDir, ".git")), new GitUser("andrey", "andrey@mail.com"));
        client.init(new InitRequest(null/* .git directory already set. Not need to pass it in this implementation. */, //
                                    false));
        Repository repository = client.getRepository();
        assertNotNull(repository);
        StoredConfig config = repository.getConfig();
        UserConfig userConfig = config.get(UserConfig.KEY);
        assertEquals("andrey", userConfig.getAuthorName());
        assertEquals("andrey", userConfig.getCommitterName());
        assertEquals("andrey@mail.com", userConfig.getAuthorEmail());
        assertEquals("andrey@mail.com", userConfig.getCommitterEmail());
        System.out.println(config.toText());
    }
}
