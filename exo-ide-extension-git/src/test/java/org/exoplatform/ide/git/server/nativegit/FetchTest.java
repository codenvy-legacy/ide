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

import org.exoplatform.ide.git.server.GitException;
import org.exoplatform.ide.git.server.nativegit.commands.LogCommand;
import org.exoplatform.ide.git.shared.FetchRequest;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * @author <a href="maito:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class FetchTest extends BaseTest {

    private File fetchTestRepo;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        fetchTestRepo = new File(getTarget().getAbsolutePath(), "fetchTestRepo");
        fetchTestRepo.mkdir();
        forClean.add(fetchTestRepo);
        //clone default repo into fetchRepo
        new NativeGit(fetchTestRepo).createCloneCommand()
                .setUri(getDefaultRepository().getAbsolutePath())
                .execute();
        //add new File into defaultRepository
        addFile(getDefaultRepository(), "newfile1", "newfile1 content");
        //add file to index and make commit
        NativeGit defaultGit = new NativeGit(getDefaultRepository());
        defaultGit.createAddCommand().setFilePattern(new String[]{"."}).execute();
        defaultGit.createCommitCommand().setMessage("fetch test").execute();
    }

    @Test
    public void testSimpleFetch() throws IOException, GitException {
        NativeGit fetchTestGit = new NativeGit(fetchTestRepo);
        //make fetch
        FetchRequest request = new FetchRequest();
        request.setRemote(getDefaultRepository().getAbsolutePath());
        connectionFactory.getConnection(fetchTestRepo, getDefaultUser()).fetch(request);
        //make merge with FETCH_HEAD
        new NativeGit(fetchTestRepo).createMergeCommand().setCommit("FETCH_HEAD").execute();
        assertTrue(new File(fetchTestRepo, "newfile1").exists());
    }

    @Test
    public void testFetchBranch() throws GitException, IOException {
        String branchName = "branch";
        NativeGit defaultGit = new NativeGit(getDefaultRepository());
        defaultGit.createBranchCheckoutCommand()
                .setCreateNew(true)
                .setBranchName(branchName)
                .execute();
        addFile(getDefaultRepository(), "otherfile1", "otherfile1 content");
        addFile(getDefaultRepository(), "otherfile2", "otherfile2 content");
        defaultGit.createAddCommand().setFilePattern(new String[]{"."}).execute();
        defaultGit.createCommitCommand().setMessage("fetch branch test").execute();
        //make fetch
        FetchRequest request = new FetchRequest();
        request.setRemote(getDefaultRepository().getAbsolutePath());
        request.setRefSpec(new String[]{branchName});
        connectionFactory.getConnection(fetchTestRepo, getDefaultUser()).fetch(request);
        //make merge with FETCH_HEAD
        new NativeGit(fetchTestRepo).createMergeCommand().setCommit("FETCH_HEAD").execute();
        assertTrue(new File(fetchTestRepo, "otherfile1").exists());
        assertTrue(new File(fetchTestRepo, "otherfile2").exists());
        assertEquals("fetch branch test",
                new LogCommand(fetchTestRepo).setCount(1).execute().get(0).getMessage());
    }
}