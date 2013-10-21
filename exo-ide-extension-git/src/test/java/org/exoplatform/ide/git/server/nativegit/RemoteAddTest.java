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
import org.exoplatform.ide.git.server.nativegit.commands.RemoteListCommand;
import org.exoplatform.ide.git.shared.Branch;
import org.exoplatform.ide.git.shared.BranchListRequest;
import org.exoplatform.ide.git.shared.InitRequest;
import org.exoplatform.ide.git.shared.RemoteAddRequest;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

/**
 * @author <a href="maito:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class RemoteAddTest extends BaseTest {

    @Test
    public void testSimpleRemoteAdd() throws GitException {
        NativeGit nativeGit = new NativeGit(getDefaultRepository());
        RemoteListCommand rList = nativeGit.createRemoteListCommand();
        int beforeCount = rList.execute().size();
        getDefaultConnection().remoteAdd(new RemoteAddRequest("origin", "some.url", null));
        int afterCount = rList.execute().size();
        assertEquals(beforeCount, afterCount - 1);
    }

    @Test
    public void testAddNotAllBranchesTracked() throws GitException {
        File repository2 = new File(getTarget().getAbsolutePath(), "repository2");
        repository2.mkdir();
        forClean.add(repository2);
        NativeGit nativeGit = new NativeGit(getDefaultRepository());
        nativeGit.createBranchCreateCommand().setBranchName("b1").execute();
        nativeGit.createBranchCreateCommand().setBranchName("b2").execute();
        nativeGit.createBranchCreateCommand().setBranchName("b3").execute();
        GitConnection connection = connectionFactory.getConnection(repository2, getDefaultUser());
        connection.init(new InitRequest());
        //add remote tracked only to b1 and b3 branches
        connection.remoteAdd(new RemoteAddRequest("origin", getDefaultRepository().getAbsolutePath(),
                new String[]{"b1", "b3"}));
        //make pull
        new NativeGit(repository2).createPullCommand().setRemote("origin").execute();
        validateBranchList(connection.branchList(new BranchListRequest(BranchListRequest.LIST_REMOTE)),
                Arrays.asList(new Branch[]{new Branch("refs/remotes/origin/b1", false, "origin/b1", true),
                        new Branch("refs/remotes/origin/b3", false, "origin/b3", true)}));
    }


}
