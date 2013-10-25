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
import org.exoplatform.ide.git.shared.*;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;

/**
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class BranchListTest extends BaseTest {

    private File repository;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        repository = new File(getTarget().getAbsolutePath(), "repository2");
        forClean.add(repository);
    }

    @Test
    public void testLocalBranchList() throws GitException {
        new NativeGit(getDefaultRepository()).createBranchCreateCommand().setBranchName("newbranch1").execute();
        validateBranchList(getDefaultConnection()
                .branchList(new BranchListRequest(BranchListRequest.LIST_LOCAL)),
                Arrays.asList(new Branch[]{new Branch("refs/heads/master", true, "master", false),
                        new Branch("refs/heads/newbranch1", false, "newbranch1", false)}));
    }

    @Test
    public void testRemoteBranchList() throws GitException, URISyntaxException {
        GitConnection connection = connectionFactory.getConnection(repository, getDefaultUser());
        connection.clone(new CloneRequest(getDefaultRepository().getAbsolutePath(), null));
        validateBranchList(connection
                .branchList(new BranchListRequest(BranchListRequest.LIST_REMOTE)),
                Arrays.asList(new Branch[]{new Branch("refs/remotes/origin/HEAD", false, "origin/HEAD", true),
                        new Branch("refs/remotes/origin/master", false, "origin/master", true)}));
    }

    @Test
    public void testAllBranchList() throws GitException, URISyntaxException {
        GitConnection connection = connectionFactory.getConnection(repository, getDefaultUser());
        connection.clone(new CloneRequest(getDefaultRepository().getAbsolutePath(), null));
        validateBranchList(connection
                .branchList(new BranchListRequest(BranchListRequest.LIST_ALL)),
                Arrays.asList(new Branch[]{new Branch("refs/remotes/origin/HEAD", false, "origin/HEAD", true),
                        new Branch("refs/remotes/origin/master", false, "origin/master", true),
                        new Branch("refs/heads/master", true, "master", false)}));
    }
}
