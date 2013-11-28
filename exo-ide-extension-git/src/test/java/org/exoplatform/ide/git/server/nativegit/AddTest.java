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
import org.exoplatform.ide.git.server.nativegit.commands.ListFilesCommand;
import org.exoplatform.ide.git.shared.AddRequest;
import org.exoplatform.ide.git.shared.GitUser;
import org.junit.Test;

import java.io.IOException;

/**
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class AddTest extends BaseTest {

    @Test
    public void testSimpelAdd() throws GitException, IOException {
        GitConnection connection = connectionFactory
                .getConnection(getDefaultRepository(), new GitUser("user", "user@email.com"));
        addFile(getDefaultRepository(), "testAdd", CONTENT);
        connection.add(new AddRequest());
        //check added files
        ListFilesCommand command = new ListFilesCommand(getDefaultRepository());
        command.execute();
        assertTrue(command.getOutput().contains("testAdd"));
    }

    @Test
    public void testNoAddWithWrongFilePattern() throws GitException {
        GitConnection connection = connectionFactory
                .getConnection(getDefaultRepository(), new GitUser("user", "user@email.com"));
        try {
            connection.add(new AddRequest(new String[]{"otherFile"}, false));
            fail("GitException expected");
        } catch (GitException ignored) {
        }
    }

    @Test
    public void testAddUpdate() throws GitException, IOException {
        GitConnection connection = connectionFactory
                .getConnection(getDefaultRepository(), new GitUser("user", "user@email.com"));
        //modify README.txt
        addFile(getDefaultRepository(), "README.txt", "SOME NEW CONTENT");
        ListFilesCommand command = new ListFilesCommand(getDefaultRepository()).setModified(true);
        command.execute();
        //modified but not added to stage
        assertTrue(command.getOutput().contains("README.txt"));
        AddRequest addReq = new AddRequest();
        addReq.setUpdate(true);
        connection.add(addReq);
        command.execute();
        //added to stage
        assertFalse(command.getOutput().contains("README.txt"));
    }
}
