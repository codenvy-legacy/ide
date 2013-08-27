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
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheEntry;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.lib.UserConfig;
import org.eclipse.jgit.storage.file.FileRepository;
import org.exoplatform.ide.git.shared.CloneRequest;
import org.exoplatform.ide.git.shared.GitUser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: CloneTest.java 22817 2011-03-22 09:17:52Z andrew00x $
 */
public class CloneTest extends BaseTest {
    private File cloneRepoDir;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        cloneRepoDir = new File(getDefaultRepository().getWorkTree().getParentFile(), "repository2");
        forClean.add(cloneRepoDir);
    }

    public void testSimpleClone() throws Exception {
        Repository repository = getDefaultRepository();
        JGitConnection client = new JGitConnection(new FileRepository(new File(cloneRepoDir, ".git")),
                                                   new GitUser("andrey", "andrey@mail.com"));
        client.clone(new CloneRequest(repository.getWorkTree().getAbsolutePath(), //
                                      null /* .git directory already set. Not need to pass it in this implementation. */));
        Repository clone = client.getRepository();

        assertEquals(cloneRepoDir.getAbsolutePath(), clone.getWorkTree().getAbsolutePath());

        StoredConfig config = clone.getConfig();
        UserConfig userConfig = config.get(UserConfig.KEY);
        assertEquals("andrey", userConfig.getAuthorName());
        assertEquals("andrey", userConfig.getCommitterName());
        assertEquals("andrey@mail.com", userConfig.getAuthorEmail());
        assertEquals("andrey@mail.com", userConfig.getCommitterEmail());

        List<File> files = new ArrayList<File>(1);
        DirCache dirCache = null;
        try {
            dirCache = clone.lockDirCache();
            for (int i = 0; i < dirCache.getEntryCount(); ++i) {
                DirCacheEntry e = dirCache.getEntry(i);
                File file = new File(cloneRepoDir, e.getPathString());
                files.add(file);
            }
        } finally {
            if (dirCache != null)
                dirCache.unlock();
        }
        assertEquals(1, files.size());
        assertEquals(CONTENT, readFile(files.get(0)));
    }

    public void testCloneBranch() throws Exception {
        Repository repository = getDefaultRepository();
        Git git = new Git(repository);
        git.branchCreate().setName("featured").call();

        JGitConnection client = new JGitConnection(new FileRepository(new File(cloneRepoDir, ".git")),
                                                   new GitUser("andrey", "andrey@mail.com"));
        CloneRequest request = new CloneRequest(repository.getWorkTree().getAbsolutePath(), //
                                                null /* .git directory already set. Not need to pass it in this implementation. */);
        request.setBranchesToFetch(new String[]{"refs/heads/featured"});
        client.clone(request);

        Repository clone = client.getRepository();
        Git cloneGit = new Git(clone);
        List<Ref> brlist = cloneGit.branchList().setListMode(ListMode.REMOTE).call();
        List<String> brnames = new ArrayList<String>(brlist.size());
        for (Ref ref : brlist)
            brnames.add(ref.getName());
        assertEquals(1, brnames.size());
        assertEquals("refs/remotes/origin/featured", brnames.get(0));
    }
}
