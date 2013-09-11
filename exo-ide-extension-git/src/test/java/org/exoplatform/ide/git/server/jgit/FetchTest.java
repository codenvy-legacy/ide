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
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepository;
import org.exoplatform.ide.git.shared.CloneRequest;
import org.exoplatform.ide.git.shared.FetchRequest;
import org.exoplatform.ide.git.shared.GitUser;

import java.io.File;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: FetchTest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class FetchTest extends BaseTest {
    private Repository fetchTestRepo;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        Repository origRepository = getDefaultRepository();
        File origWorkDir = origRepository.getWorkTree();

        File fetchWorkDir = new File(origWorkDir.getParentFile(), "FetchTestRepo");
        forClean.add(fetchWorkDir);

        JGitConnection client =
                new JGitConnection(new FileRepository(new File(fetchWorkDir, ".git")),
                                   new GitUser("andrey", "andrey@mail.com"));
        client.clone(new CloneRequest(origWorkDir.getAbsolutePath(), //
                                      null /* .git directory already set. Not need to pass it in this implementation. */));

        fetchTestRepo = client.getRepository();

        addFile(origWorkDir, "t-fetch1", "AAA\n");
        addFile(origWorkDir, "t-fetch2", "BBB\n");

        Git git = new Git(origRepository);
        git.add().addFilepattern(".").call();
        git.commit().setMessage("fetch test").setAuthor("andrey", "andrey@mail.com").call();
    }

    public void testFetch() throws Exception {
        // Use default remote settings.
        new JGitConnection(fetchTestRepo, new GitUser("andrey", "andrey@mail.com")).fetch(new FetchRequest());

        Git git = new Git(fetchTestRepo);
        git.merge().include(fetchTestRepo.getRef(Constants.FETCH_HEAD)).call();

        File fetchWorkDir = fetchTestRepo.getWorkTree();
        assertTrue(new File(fetchWorkDir, "t-fetch1").exists());
        assertTrue(new File(fetchWorkDir, "t-fetch2").exists());
        assertEquals("fetch test", git.log().call().iterator().next().getFullMessage());
    }

    public void testFetchBranch() throws Exception {
        String branchName = "testFetchBranch";
        Repository origin = getDefaultRepository();
        Git originGit = new Git(origin);
        originGit.branchCreate().setName(branchName).call();
        originGit.checkout().setName(branchName).call();
        addFile(origin.getWorkTree(), "aaa", "AAA\n");
        originGit.add().addFilepattern(".").call();
        originGit.commit().setMessage("aaa").call();

        FetchRequest request = new FetchRequest();
        request.setRemote("origin");
        request.setRefSpec(new String[]{/*"refs/heads/" + */branchName});
        new JGitConnection(fetchTestRepo, new GitUser("andrey", "andrey@mail.com")).fetch(request);

        Git newGit = new Git(fetchTestRepo);

        newGit.merge().include(fetchTestRepo.getRef(Constants.FETCH_HEAD)).call();

        File fetchWorkDir = fetchTestRepo.getWorkTree();
        assertTrue(new File(fetchWorkDir, "t-fetch1").exists());
        assertTrue(new File(fetchWorkDir, "t-fetch2").exists());
        assertTrue(new File(fetchWorkDir, "aaa").exists());
        assertEquals("aaa", newGit.log().call().iterator().next().getFullMessage());
    }
}
