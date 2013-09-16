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
import org.exoplatform.ide.git.shared.MergeRequest;
import org.exoplatform.ide.git.shared.MergeResult;

import java.io.File;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: MergeTest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class MergeTest extends BaseTest {
    private String branchName = "MergeTestBranch";

    public void testMergeNoChanges() throws Exception {
        Git git = new Git(getDefaultRepository());
        git.branchCreate().setName(branchName).call();
        MergeResult mergeResult = getDefaultConnection().merge(new MergeRequest("MergeTestBranch"));
        assertEquals(MergeResult.MergeStatus.ALREADY_UP_TO_DATE, mergeResult.getMergeStatus());
    }

    public void testMerge() throws Exception {
        Git git = new Git(getDefaultRepository());
        git.branchCreate().setName(branchName).call();
        git.checkout().setName(branchName).call();
        File file = addFile(git.getRepository().getWorkTree(), "t-merge", "aaa\n");

        git.add().addFilepattern(".").call();
        git.commit().setMessage("add file in new branch").call();
        git.checkout().setName("master").call();

        MergeResult mergeResult = getDefaultConnection().merge(new MergeRequest("MergeTestBranch"));
        assertEquals(MergeResult.MergeStatus.FAST_FORWARD, mergeResult.getMergeStatus());
        assertTrue(file.exists());
        assertEquals("aaa\n", readFile(file));
        assertEquals("add file in new branch", git.log().call().iterator().next().getFullMessage());
    }

    public void testMergeConflict() throws Exception {
        Git git = new Git(getDefaultRepository());
        git.branchCreate().setName(branchName).call();
        git.checkout().setName(branchName).call();
        addFile(git.getRepository().getWorkTree(), "t-merge-conflict", "aaa\n");

        git.add().addFilepattern(".").call();
        git.commit().setMessage("add file in new branch").call();
        git.checkout().setName("master").call();

        addFile(git.getRepository().getWorkTree(), "t-merge-conflict", "bbb\n");
        git.add().addFilepattern(".").call();
        git.commit().setMessage("add file in new master").call();

        MergeResult mergeResult = getDefaultConnection().merge(new MergeRequest("MergeTestBranch"));
        String[] conflicts = mergeResult.getConflicts();
        assertEquals(1, conflicts.length);
        assertEquals("t-merge-conflict", conflicts[0]);

        assertEquals(MergeResult.MergeStatus.CONFLICTING, mergeResult.getMergeStatus());

        String expContent = "<<<<<<< HEAD\n" //
                            + "bbb\n" //
                            + "=======\n" //
                            + "aaa\n" //
                            + ">>>>>>> refs/heads/MergeTestBranch\n";

        String actual = readFile(new File(git.getRepository().getWorkTree(), "t-merge-conflict"));
        assertEquals(expContent, readFile(new File(git.getRepository().getWorkTree(), "t-merge-conflict")));
        System.out.println(actual);
    }
}
