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

import java.io.IOException;

/**
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class CommitTest extends BaseTest {

    @Test
    public void testSimpleCommit() throws GitException, IOException {
        GitConnection connection = getDefaultConnection();
        //add new File
        addFile(getDefaultRepository(), "DONTREADME", "secret");
        //add changes
        connection.add(new AddRequest());
        CommitRequest commitRequest = new CommitRequest("Commit message", false, false);
        Revision revision = connection.commit(commitRequest);
        assertEquals(revision.getMessage(), commitRequest.getMessage());
    }

    @Test
    public void testCommitWithAddAll() throws GitException, IOException {
        GitConnection connection = getDefaultConnection();
        //change existing README
        addFile(getDefaultRepository(), "README.txt", "not secret");
        CommitRequest commitRequest = new CommitRequest("Other commit message", true, false);
        Revision revision = connection.commit(commitRequest);
        assertEquals(revision.getMessage(), commitRequest.getMessage());
    }

    @Test
    public void testAmendCommit() throws GitException, IOException {
        GitConnection connection = getDefaultConnection();
        int beforeCount = getCountOfCommitsInCurrentBranch(getDefaultRepository());
        //change existing README
        addFile(getDefaultRepository(), "README.txt", "some new content");
        CommitRequest commitRequest = new CommitRequest("Amend commit", true, true);
        Revision revision = connection.commit(commitRequest);
        int afterCount = getCountOfCommitsInCurrentBranch(getDefaultRepository());
        assertEquals(revision.getMessage(), commitRequest.getMessage());
        assertEquals(beforeCount, afterCount);
    }
}
