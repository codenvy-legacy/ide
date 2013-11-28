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
import org.exoplatform.ide.git.server.nativegit.commands.BranchCheckoutCommand;
import org.exoplatform.ide.git.server.nativegit.commands.BranchListCommand;
import org.exoplatform.ide.git.server.nativegit.commands.LogCommand;
import org.exoplatform.ide.git.shared.*;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class BranchCreateTest extends BaseTest {

    @Test
    public void testSimpleBranchCreate() throws GitException {
        GitConnection connection = getDefaultConnection();
        BranchListCommand brListCommand = new BranchListCommand(getDefaultRepository());
        //count of branches before creating new
        int beforeCountOfBranches = brListCommand.execute().size();
        Branch branch = connection.branchCreate(new BranchCreateRequest("new-branch", null));
        //count of branches after creating new
        int afterCountOfBranches = brListCommand.execute().size();
        assertEquals(beforeCountOfBranches + 1, afterCountOfBranches);
    }

    @Test
    public void testBranchCreateWithStartPoint() throws IOException, GitException {
        GitConnection connection = getDefaultConnection();
        //make 2 commits in default repository
        addFile(getDefaultRepository(), "newfile1", "file 1 content");
        NativeGit defaultGit = new NativeGit(getDefaultRepository());
        defaultGit.createAddCommand().setFilePattern(new String[]{"."}).execute();
        defaultGit.createCommitCommand().setMessage("file1 added").execute();
        //change content
        addFile(getDefaultRepository(), "newfile1", "new file 1 content");
        defaultGit.createCommitCommand().setMessage("content changed").setAll(true).execute();
        //get list of master branch commits
        LogCommand log = new LogCommand(getDefaultRepository());
        List<Revision> revCommitList = log.execute();
        int beforeCheckoutCommitsCount = revCommitList.size();
        //create new branch to 2nd commit
        Branch branch = connection.branchCreate(
                new BranchCreateRequest("new-branch", revCommitList.get(1).getId()));
        BranchCheckoutCommand checkout = new BranchCheckoutCommand(getDefaultRepository());
        checkout.setBranchName(branch.getDisplayName()).execute();
        log.execute();
        int afterCheckoutCommitsCount = log.execute().size();
        assertEquals(afterCheckoutCommitsCount, beforeCheckoutCommitsCount - 1);
    }
}
