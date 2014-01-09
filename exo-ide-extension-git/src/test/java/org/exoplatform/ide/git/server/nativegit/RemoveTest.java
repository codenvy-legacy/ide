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
import org.exoplatform.ide.git.server.nativegit.commands.ListFilesCommand;
import org.exoplatform.ide.git.shared.RmRequest;
import org.junit.Test;

import java.io.File;
import java.util.List;

/**
 * @author <a href="maito:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class RemoveTest extends BaseTest {

    @Test
    public void testNotCachedRemove() throws GitException {
        RmRequest request = new RmRequest();
        request.setFiles(new String[]{"README.txt"});
        request.setCached(false);
        getDefaultConnection().rm(request);
        assertFalse(new File(getDefaultRepository(), "README.txt").exists());
        checkNotCached(getDefaultRepository(), "README.txt");
    }

    @Test
    public void testCachedRemove() throws GitException {
        RmRequest request = new RmRequest();
        request.setFiles(new String[]{"README.txt"});
        request.setCached(true);
        getDefaultConnection().rm(request);
        assertTrue(new File(getDefaultRepository(), "README.txt").exists());
        checkNotCached(getDefaultRepository(), "README.txt");
    }
}
