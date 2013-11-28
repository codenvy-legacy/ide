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
import org.exoplatform.ide.git.server.nativegit.commands.BranchListCommand;
import org.exoplatform.ide.git.shared.BranchCheckoutRequest;
import org.junit.Test;

import java.io.File;

/**
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class BranchCheckoutTest extends BaseTest {

    private String firstBranchName = "firstBranch";
    private String secondBranchName = "secondBranch";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        NativeGit git = new NativeGit(getDefaultRepository());
        git.createBranchCreateCommand().setBranchName(firstBranchName).execute();
        git.createBranchCheckoutCommand().setBranchName(firstBranchName).execute();
        addFile(getDefaultRepository(), "newfile", "new file content");
        git.createAddCommand().setFilePattern(new String[]{"."}).execute();
        git.createCommitCommand().setMessage("Commit message").execute();
        git.createBranchCheckoutCommand().setBranchName("master").execute();
    }

    @Test
    public void testSimpleCheckout() throws GitException {
        assertFalse(new File(getDefaultRepository().getAbsolutePath(), "newfile").exists());
        getDefaultConnection().branchCheckout(new BranchCheckoutRequest(firstBranchName, null, false));
        assertTrue(new File(getDefaultRepository().getAbsolutePath(), "newfile").exists());
    }

    @Test
    public void testCreateNewAndCheckout() throws GitException {
        BranchListCommand blc = new NativeGit(getDefaultRepository()).createBranchListCommand();
        assertEquals(2, blc.execute().size());
        getDefaultConnection().branchCheckout(new BranchCheckoutRequest("thirdBranch", null, true));
        assertEquals(3, blc.execute().size());
    }

    @Test
    public void testCheckoutFromStartPoint() throws GitException {
        BranchListCommand blc = new NativeGit(getDefaultRepository()).createBranchListCommand();
        assertEquals(2, blc.execute().size());
        getDefaultConnection().branchCheckout(new BranchCheckoutRequest(secondBranchName, firstBranchName, true));
        assertEquals(3, blc.execute().size());
        assertTrue(new File(getDefaultRepository().getAbsolutePath(), "newfile").exists());
    }
}
