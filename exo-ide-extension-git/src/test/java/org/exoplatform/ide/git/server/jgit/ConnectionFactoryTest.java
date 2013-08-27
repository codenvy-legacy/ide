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

import org.exoplatform.ide.git.server.GitConnection;
import org.exoplatform.ide.git.server.GitConnectionFactory;
import org.exoplatform.ide.git.shared.GitUser;
import org.exoplatform.ide.git.shared.InitRequest;

import java.io.File;
import java.net.URL;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: ConnectionFactoryTest.java 22811 2011-03-22 07:28:35Z andrew00x
 *          $
 */
public class ConnectionFactoryTest extends BaseTest {
    public void testClientFactory() throws Exception {
        GitConnectionFactory gitConnectionFactory = GitConnectionFactory.getInstance();

        URL testCls = Thread.currentThread().getContextClassLoader().getResource(".");
        File target = new File(testCls.toURI()).getParentFile();
        File repoDir = new File(target, "ConnectionFactoryTest");
        repoDir.mkdir();
        forClean.add(repoDir);

        GitConnection gitConnection =
                gitConnectionFactory.getConnection(repoDir, new GitUser("andrey", "andrey@mail.com"));

        // Try to initialize repository via obtained connection.
        gitConnection.init(new InitRequest());
    }
}
