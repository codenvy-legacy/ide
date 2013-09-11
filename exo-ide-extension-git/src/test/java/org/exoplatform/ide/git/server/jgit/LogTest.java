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
import org.exoplatform.ide.git.shared.LogRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: LogTest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class LogTest extends BaseTest {
    public void testLog() throws Exception {
        Git git = new Git(getDefaultRepository());
        File workDir = git.getRepository().getWorkTree();
        addFile(workDir, "t-log1", "AAA\n");
        git.add().addFilepattern(".").call();
        git.commit().setMessage("log\ntest").setCommitter("andrey", "andrey@mail.com").call();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        getDefaultConnection().log(new LogRequest()).writeTo(out);
        // TODO test output.
        System.out.println(new String(out.toByteArray()));
    }
}
