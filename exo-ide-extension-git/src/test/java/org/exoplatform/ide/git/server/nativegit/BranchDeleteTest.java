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
import org.exoplatform.ide.git.shared.*;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class BranchDeleteTest extends BaseTest {

    @Test
    public void testSimpleDelete() throws GitException {
        new NativeGit(getDefaultRepository()).createBranchCreateCommand().setBranchName("newbranch").execute();
        validateBranchList(getDefaultConnection().branchList(new BranchListRequest(BranchListRequest.LIST_LOCAL)),
                Arrays.asList(new Branch[]{new Branch("refs/heads/master", true, "master", false),
                        new Branch("refs/heads/newbranch", false, "newbranch", false)}));
        getDefaultConnection().branchDelete(new BranchDeleteRequest("newbranch", false));
        validateBranchList(getDefaultConnection().branchList(new BranchListRequest(BranchListRequest.LIST_LOCAL)),
                Arrays.asList(new Branch[]{new Branch("refs/heads/master", true, "master", false)}));
    }

    public void testForceDelete() throws GitException, IOException {
        NativeGit defaultGit = new NativeGit(getDefaultRepository());
        defaultGit.createBranchCheckoutCommand().setCreateNew(true).setBranchName("newbranch").execute();
        addFile(getDefaultRepository(), "newfile", "new file content");
        defaultGit.createAddCommand().setFilePattern(new String[]{"."}).execute();
        defaultGit.createCommitCommand().setMessage("second commit").execute();
        defaultGit.createBranchCheckoutCommand().setBranchName("master").execute();
        try {
            getDefaultConnection().branchDelete(new BranchDeleteRequest("newbranch", false));
            fail("not merged changes, should be exception");
        } catch (GitException ignored) {
        }
        getDefaultConnection().branchDelete(new BranchDeleteRequest("newbranch", true));
    }

}

