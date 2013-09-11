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
import org.eclipse.jgit.lib.Repository;
import org.exoplatform.ide.git.server.GitHelper;
import org.exoplatform.ide.git.shared.Status;

import java.io.File;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Oct 4, 2011 evgen $
 */
public class GitIgnoreTest extends BaseTest {
    private Repository repository;

    @Override
    protected void setUp() throws Exception {
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

    public void testStatusWithGitIgnore() throws Exception {
        Status statusPage = getDefaultConnection().status(false);
        assertEquals("master", statusPage.getBranchName());
        Set<String> untracked = statusPage.getUntracked();
        assertEquals(0, untracked.size());
    }
}
