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
import org.exoplatform.ide.git.shared.GitUser;
import org.exoplatform.ide.git.shared.InitRequest;
import org.junit.Test;

import java.io.File;

/**
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class InitTest extends BaseTest {

    @Test
    public void testInit() throws GitException {
        File destination = new File(getTarget().getAbsolutePath(), "repository2");
        destination.mkdir();
        forClean.add(destination);
        GitConnection connection = connectionFactory.getConnection(destination,
                new GitUser("user", "user@email.com"));
        connection.init(new InitRequest(null, false));
        Config config = new Config(destination);
        config.loadUser();
        assertEquals("user", config.getUsername());
        assertEquals("user@email.com", config.getEmail());
    }
}
