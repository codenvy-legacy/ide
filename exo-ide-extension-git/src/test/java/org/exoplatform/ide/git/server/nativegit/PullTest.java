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
package org.exoplatform.ide.git.server.nativegit;

import org.exoplatform.ide.git.server.GitConnection;
import org.exoplatform.ide.git.server.GitException;
import org.exoplatform.ide.git.shared.CloneRequest;
import org.exoplatform.ide.git.shared.PullRequest;
import org.junit.Test;

import java.io.IOException;
import java.io.File;
import java.net.URISyntaxException;

/**
 * @author <a href="maito:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class PullTest extends BaseTest {

    @Test
    public void testSimplePull() throws IOException, GitException, URISyntaxException {
        //create new repository clone of default
        File repo2 = new File(getTarget().getAbsolutePath(), "repo2");
        forClean.add(repo2);
        GitConnection repo2Connection = getDefaultConnection()
                .clone(new CloneRequest(getDefaultRepository().getAbsolutePath(), repo2.getAbsolutePath()));
        addFile(getDefaultRepository(), "newfile1", "new file1 content");
        NativeGit nGit = new NativeGit(getDefaultRepository());
        nGit.createAddCommand().setFilePattern(new String[]{"."}).execute();
        nGit.createCommitCommand().setMessage("Test commit").execute();
        //make pull
        repo2Connection.pull(new PullRequest("origin", null, -1));
        //check file exists
        assertTrue(new File(repo2.getAbsolutePath(), "newfile1").exists());
    }

    @Test
    public void testPullWithRefSpec() throws GitException, URISyntaxException, IOException {
        //create new repository clone of default
        File repo2 = new File(getTarget().getAbsolutePath(), "repo2");
        forClean.add(repo2);
        GitConnection repo2Connection = getDefaultConnection()
                .clone(new CloneRequest(getDefaultRepository().getAbsolutePath(), repo2.getAbsolutePath()));
        //add new branch
        NativeGit nGit = new NativeGit(getDefaultRepository());
        nGit.createBranchCheckoutCommand().setBranchName("b1").setCreateNew(true).execute();
        addFile(getDefaultRepository(), "newfile1", "new file1 content");
        nGit.createAddCommand().setFilePattern(new String[]{"."}).execute();
        nGit.createCommitCommand().setMessage("Test commit").execute();
        int branchesBefore = new NativeGit(repo2).createBranchListCommand().execute().size();
        //make pull
        repo2Connection.pull(new PullRequest("origin", "refs/heads/b1:refs/heads/b1", -1));
        int branchesAfter = new NativeGit(repo2).createBranchListCommand().execute().size();
        assertEquals(branchesAfter - 1, branchesBefore);
    }

    @Test
    public void testPullRemote() throws GitException, IOException {
        String branchName = "remoteBranch";
        NativeGit sourceGit = new NativeGit(getDefaultRepository());
        sourceGit.createBranchCheckoutCommand().setCreateNew(true).setBranchName(branchName).execute();
        addFile(getDefaultRepository(), "remoteFile", "");
        sourceGit.createAddCommand().setFilePattern(new String[]{"."}).execute();
        sourceGit.createCommitCommand().setMessage("remote test").execute();

        File newRepo = new File(getTarget().getAbsolutePath(), "newRepo");
        newRepo.mkdir();
        forClean.add(newRepo);
        NativeGit newRepoGit = new NativeGit(newRepo);
        newRepoGit.createInitCommand().execute();
        addFile(newRepo, "EMPTY", "");
        newRepoGit.createAddCommand().setFilePattern(new String[]{"."}).execute();
        newRepoGit.createCommitCommand().setMessage("init").execute();

        PullRequest request = new PullRequest();
        request.setRemote(getDefaultRepository().getAbsolutePath());
        request.setRefSpec(branchName);
        connectionFactory.getConnection(newRepo, getDefaultUser()).pull(request);

        assertTrue(new File(newRepo.getAbsolutePath(), "remoteFile").exists());
    }
}
