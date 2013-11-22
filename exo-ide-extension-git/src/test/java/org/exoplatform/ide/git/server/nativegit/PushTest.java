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
import org.exoplatform.ide.git.shared.PushRequest;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @author <a href="maito:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class PushTest extends BaseTest {

    @Test
    public void testSimplePush() throws IOException, GitException, URISyntaxException {
        File pushTo = new File(getTarget().getAbsolutePath(), "repo2");
        pushTo.mkdir();
        forClean.add(pushTo);
        GitConnection connection = connectionFactory.getConnection(pushTo, getDefaultUser());
        connection.clone(new CloneRequest(getDefaultRepository().getAbsolutePath(), null));
        addFile(pushTo, "newfile", "content");
        NativeGit pushToGit = new NativeGit(pushTo);
        pushToGit.createAddCommand().setFilePattern(new String[]{"."}).execute();
        pushToGit.createCommitCommand().setMessage("Fake commit").execute();
        //make push
        connection.push(new PushRequest(new String[]{"refs/heads/master:refs/heads/test"}, "origin", false, -1));
        //check branches in origin repository
        NativeGit originGit = new NativeGit(getDefaultRepository());
        assertEquals(2, originGit.createBranchListCommand().execute().size());
        //chekcout test branch
        originGit.createBranchCheckoutCommand().setBranchName("test").execute();
        assertTrue(new File(getDefaultRepository().getAbsolutePath(), "newfile").exists());
    }

    @Test
    public void testPushRemote() throws GitException, IOException {
        File remoteRepo = new File(getTarget().getAbsolutePath(), "remoteRepo");
        remoteRepo.mkdir();
        forClean.add(remoteRepo);
        NativeGit remoteGit = new NativeGit(remoteRepo);
        remoteGit.createInitCommand().execute();
        addFile(remoteRepo, "README", "README");
        remoteGit.createAddCommand().setFilePattern(new String[]{"."}).execute();
        remoteGit.createCommitCommand().setMessage("Init commit.").execute();
        //make push
        int branchesBefore = remoteGit.createBranchListCommand().execute().size();
        getDefaultConnection().push(new PushRequest(new String[]{"refs/heads/master:refs/heads/test"},
                remoteRepo.getAbsolutePath(), false, -1));
        int branchesAfter = remoteGit.createBranchListCommand().execute().size();
        assertEquals(branchesAfter - 1, branchesBefore);
    }
}
