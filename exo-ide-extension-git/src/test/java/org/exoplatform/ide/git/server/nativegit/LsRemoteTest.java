/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
import org.exoplatform.ide.git.shared.LsRemoteRequest;
import org.exoplatform.ide.git.shared.RemoteReference;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Alexander Garagatyi
 */
public class LsRemoteTest extends BaseTest {
    public void testShouldBeAbleToGetResultFromPublicRepo() throws GitException {
        GitConnection connection = connectionFactory.getConnection("/tmp", null);
        Set<RemoteReference> remoteReferenceSet =
                new HashSet<>(connection.lsRemote(new LsRemoteRequest("https://github.com/codenvy/everrest.git", false)));

        assertTrue(remoteReferenceSet.contains(new RemoteReference("259e24c83c8a122af858c8306c3286586404ef3f", "refs/tags/1.1.9")));
    }

    public void testShouldThrowGitExceptionIfUserTryGetInfoAboutPrivateRepoAndUserIsUnauthorized() throws GitException {
        try {
            GitConnection connection = connectionFactory.getConnection("/tmp", null);
            connection.lsRemote(new LsRemoteRequest("https://github.com/codenvy/cloud-ide.git", false));
            fail();
        } catch (GitException ignored) {
        }
    }
}
