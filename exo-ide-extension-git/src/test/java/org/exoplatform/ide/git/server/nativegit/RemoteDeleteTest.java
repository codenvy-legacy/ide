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
import org.junit.Test;

/**
 * @author <a href="maito:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class RemoteDeleteTest extends BaseTest {

    @Test
    public void testRemoteDelete() throws GitException {
        NativeGit nativeGit = new NativeGit(getDefaultRepository());
        nativeGit.createRemoteAddCommand()
                .setName("origin")
                .setUrl("host.com:username/Repo.git")
                .execute();
        //now it is 1 remote
        assertEquals(1, nativeGit.createRemoteListCommand().execute().size());
        //try delete not existing remote
        try {
            getDefaultConnection().remoteDelete("donotexists");
            fail("should be exception");
        } catch (GitException ignored) {
        }
        getDefaultConnection().remoteDelete("origin");
        //now it is 0 remotes
        assertEquals(0, nativeGit.createRemoteListCommand().execute().size());
    }
}
