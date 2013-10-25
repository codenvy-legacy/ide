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
import org.exoplatform.ide.git.shared.GitUser;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author <a href="maito:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class GetCommittersTest extends BaseTest {

    @Test
    public void testGetCommitters() throws IOException, GitException {
        NativeGit defGit = new NativeGit(getDefaultRepository());
        addFile(getDefaultRepository(), "newfile", "newfile content");
        defGit.createAddCommand().setFilePattern(new String[]{"."}).execute();
        defGit.createCommitCommand().setMessage("test commit")
                .setCommitter(new GitUser("Chuck Norris", "gmail@chucknorris.com")).execute();
        List<GitUser> committers = getDefaultConnection().getCommiters();
        assertEquals("There is 2 committers, repository owner and Chuck",
                committers.size(), 2);
        assertEquals("gmail@chucknorris.com", committers.get(0).getEmail());
        assertEquals("Chuck Norris", committers.get(0).getName());
    }
}
