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
import org.eclipse.jgit.lib.Repository;
import org.exoplatform.ide.git.shared.ResetRequest;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: ResetTest.java 22817 2011-03-22 09:17:52Z andrew00x $
 */
public class ResetTest extends BaseTest {
    public void testResetHard() throws Exception {
        Repository repository = getDefaultRepository();
        Git git = new Git(repository);

        File aaa = addFile(repository.getWorkTree(), "aaa", "aaa\n");

        FileOutputStream fos = new FileOutputStream(new File(repository.getWorkTree(), "README.txt"));
        fos.write("MODIFIED\n".getBytes());
        fos.flush();
        fos.close();

        String initMessage = git.log().call().iterator().next().getFullMessage();

        git.add().addFilepattern(".").call();
        git.commit().setMessage("add file").call();

        getDefaultConnection().reset(new ResetRequest("HEAD^", ResetRequest.ResetType.HARD));

        // Revert to previous revision.
        assertEquals(initMessage, git.log().call().iterator().next().getFullMessage());
        // Removed.
        assertFalse(aaa.exists());
        checkNoFilesInCache(repository, aaa);
        // previous content.
        assertEquals(CONTENT, readFile(new File(repository.getWorkTree(), "README.txt")));
    }

    public void testResetSoft() throws Exception {
        Repository repository = getDefaultRepository();
        Git git = new Git(repository);

        File aaa = addFile(repository.getWorkTree(), "aaa", "aaa\n");

        FileOutputStream fos = new FileOutputStream(new File(repository.getWorkTree(), "README.txt"));
        fos.write("MODIFIED\n".getBytes());
        fos.flush();
        fos.close();

        String initMessage = git.log().call().iterator().next().getFullMessage();

        git.add().addFilepattern(".").call();
        git.commit().setMessage("add file").call();

        getDefaultConnection().reset(new ResetRequest("HEAD^", ResetRequest.ResetType.SOFT));

        // Revert to previous revision.
        assertEquals(initMessage, git.log().call().iterator().next().getFullMessage());
        // New file untouched.
        assertTrue(aaa.exists());
        checkFilesInCache(repository, aaa);
        // Modified content.
        assertEquals("MODIFIED\n", readFile(new File(repository.getWorkTree(), "README.txt")));
    }

    public void testResetMixed() throws Exception {
        Repository repository = getDefaultRepository();
        Git git = new Git(repository);

        File aaa = addFile(repository.getWorkTree(), "aaa", "aaa\n");

        FileOutputStream fos = new FileOutputStream(new File(repository.getWorkTree(), "README.txt"));
        fos.write("MODIFIED\n".getBytes());
        fos.flush();
        fos.close();

        String initMessage = git.log().call().iterator().next().getFullMessage();

        git.add().addFilepattern(".").call();
        git.commit().setMessage("add file").call();

        getDefaultConnection().reset(new ResetRequest("HEAD^", ResetRequest.ResetType.MIXED));

        // Revert to previous revision.
        assertEquals(initMessage, git.log().call().iterator().next().getFullMessage());
        // New file untouched.
        assertTrue(aaa.exists());
        // But removed from index.
        checkNoFilesInCache(repository, aaa);
        // Modified content.
        assertEquals("MODIFIED\n", readFile(new File(repository.getWorkTree(), "README.txt")));
    }
}
