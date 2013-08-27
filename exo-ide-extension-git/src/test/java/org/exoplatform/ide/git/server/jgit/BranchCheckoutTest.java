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
import org.eclipse.jgit.lib.Ref;
import org.exoplatform.ide.git.shared.BranchCheckoutRequest;

import java.io.File;
import java.util.List;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: BranchCheckoutTest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class BranchCheckoutTest extends BaseTest {
    private final String branch1 = "BranchCheckoutTest1";
    private final String branch2 = "BranchCheckoutTest2";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Git git = new Git(getDefaultRepository());

        git.branchCreate().setName(branch1).call();
        git.checkout().setName(branch1).call();
        addFile(getDefaultRepository().getWorkTree(), "br1", "aaa");
        git.add().addFilepattern(".").call();
        git.commit().setMessage("aaa").call();

        git.checkout().setName("master").call();
    }

    public void testCheckout() throws Exception {
        BranchCheckoutRequest request = new BranchCheckoutRequest(branch1, null, false);
        getDefaultConnection().branchCheckout(request);
        assertTrue(new File(getDefaultRepository().getWorkTree(), "br1").exists());
        assertTrue(new File(getDefaultRepository().getWorkTree(), "README.txt").exists());
    }

    public void testCheckoutCreate() throws Exception {
        List<Ref> all = new Git(getDefaultRepository()).branchList().call();
        assertEquals(2, all.size());

        getDefaultConnection().branchCheckout(new BranchCheckoutRequest(branch2, null, true));

        all = new Git(getDefaultRepository()).branchList().call();
        assertEquals(3, all.size());
        assertTrue(new File(getDefaultRepository().getWorkTree(), "README.txt").exists());
    }

    public void testCheckoutCreateWithStartPoint() throws Exception {
        List<Ref> all = new Git(getDefaultRepository()).branchList().call();
        assertEquals(2, all.size());

        getDefaultConnection().branchCheckout(new BranchCheckoutRequest(branch2, branch1, true));

        all = new Git(getDefaultRepository()).branchList().call();

        assertEquals(3, all.size());
        assertTrue(new File(getDefaultRepository().getWorkTree(), "br1").exists());
        assertTrue(new File(getDefaultRepository().getWorkTree(), "README.txt").exists());
    }
}
