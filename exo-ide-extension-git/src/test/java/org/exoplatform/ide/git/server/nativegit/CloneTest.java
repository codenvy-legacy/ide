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
import org.exoplatform.ide.git.shared.CloneRequest;
import org.exoplatform.ide.git.shared.GitUser;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * NativeGitConnection clone test.
 *
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class CloneTest extends BaseTest {

    @Test
    public void testSimpleClone() throws GitException, URISyntaxException, IOException {
        File destination = new File(getTarget().getAbsolutePath(), "repository2");
        destination.mkdir();
        forClean.add(destination);
        NativeGitConnection connection = (NativeGitConnection) connectionFactory
                .getConnection(destination, new GitUser("username", "username@email.com"));
        connection.clone(new CloneRequest(getDefaultRepository().getAbsolutePath(), null));
        File clone = connection.getNativeGit().getRepository();
        //check destination repository configuration
        Config config = new Config(clone).loadUser();
        assertEquals("username", config.getUsername());
        assertEquals("username@email.com", config.getEmail());
        //check cloned repository content
        assertEquals(readFile(new File(clone.getAbsolutePath(), "README.txt")), CONTENT);
    }

}
