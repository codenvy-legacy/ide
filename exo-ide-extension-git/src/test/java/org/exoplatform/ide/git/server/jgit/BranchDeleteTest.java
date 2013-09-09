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
import org.exoplatform.ide.git.server.GitException;
import org.exoplatform.ide.git.shared.BranchDeleteRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: BranchDeleteTest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class BranchDeleteTest extends BaseTest {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Git git = new Git(getDefaultRepository());
        git.branchCreate().setName("branch1").call();
        git.branchCreate().setName("branch2").call();
    }

    public void testDelete() throws Exception {
        getDefaultConnection().branchDelete(new BranchDeleteRequest("branch1", false));
        testBranch(new String[]{"refs/heads/master", "refs/heads/branch2"});
    }

    public void testDeleteCurrent() throws Exception {
        try {
            getDefaultConnection().branchDelete(new BranchDeleteRequest("master", true));
            fail("Expected exception was not thrown. ");
        } catch (GitException e) {
            // expected
        }
        testBranch(new String[]{"refs/heads/master", "refs/heads/branch1", "refs/heads/branch2"});
    }

    public void testDeleteNotMerged() throws Exception {
        Git git = new Git(getDefaultRepository());
        git.checkout().setName("branch2").call();
        addFile(getDefaultRepository().getWorkTree(), "br2-file", "aaa");
        git.add().addFilepattern(".").call();
        git.commit().setMessage("br2 commit").setAuthor("andrey", "andrey@mail.com").call();
        git.checkout().setName("master").call();
        BranchDeleteRequest request = new BranchDeleteRequest("branch2", false);
        try {
            getDefaultConnection().branchDelete(request);
            fail("Expected exception was not thrown. ");
        } catch (GitException e) {
            // expected
        }
        testBranch(new String[]{"refs/heads/master", "refs/heads/branch1", "refs/heads/branch2"});

        request.setForce(true);
        // Able to delete now.
        getDefaultConnection().branchDelete(request);

        testBranch(new String[]{"refs/heads/master", "refs/heads/branch1"});
    }

    private void testBranch(String[] exp) throws Exception {
        List<Ref> list = new Git(getDefaultRepository()).branchList().call();
        assertEquals(exp.length, list.size());
        List<String> refNames = new ArrayList<String>(list.size());
        for (Ref refName : list)
            refNames.add(refName.getName());
        for (String e : exp)
            assertTrue("Not found " + e + " branch. ", refNames.contains(e));
    }
}
