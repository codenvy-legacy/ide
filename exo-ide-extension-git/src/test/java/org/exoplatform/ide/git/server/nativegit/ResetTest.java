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

import org.exoplatform.ide.git.shared.ResetRequest;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @author <a href="maito:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class ResetTest extends BaseTest {

    @Test
    public void testResetHard() throws Exception {
        NativeGit git = new NativeGit(getDefaultRepository());

        File aaa = addFile(getDefaultRepository(), "aaa", "aaa\n");
        FileOutputStream fos = new FileOutputStream(new File(getDefaultRepository(), "README.txt"));
        fos.write("MODIFIED\n".getBytes());
        fos.flush();
        fos.close();
        String initMessage = git.createLogCommand().setCount(1).execute().get(0).getMessage();
        git.createAddCommand().setFilePattern(new String[]{"."}).execute();
        git.createCommitCommand().setMessage("add file").execute();
        //make hard reset
        getDefaultConnection().reset(new ResetRequest("HEAD^", ResetRequest.ResetType.HARD));
        // Revert to previous revision.
        assertEquals(initMessage, git.createLogCommand().setCount(1).execute().get(0).getMessage());
        // Removed.
        assertFalse(aaa.exists());
        checkNotCached(getDefaultRepository(), "aaa");
        // previous content.
        assertEquals(CONTENT, readFile(new File(getDefaultRepository(), "README.txt")));
    }

    @Test
    public void testResetSoft() throws Exception {
        NativeGit git = new NativeGit(getDefaultRepository());
        File aaa = addFile(getDefaultRepository(), "aaa", "aaa\n");
        FileOutputStream fos = new FileOutputStream(new File(getDefaultRepository(), "README.txt"));
        fos.write("MODIFIED\n".getBytes());
        fos.flush();
        fos.close();
        String initMessage = git.createLogCommand().setCount(1).execute().get(0).getMessage();
        git.createAddCommand().setFilePattern(new String[]{"."}).execute();
        git.createCommitCommand().setMessage("add file").execute();
        //make soft reset
        getDefaultConnection().reset(new ResetRequest("HEAD^", ResetRequest.ResetType.SOFT));
        // Revert to previous revision.
        assertEquals(initMessage, git.createLogCommand().setCount(1).execute().get(0).getMessage());
        // New file untouched.
        assertTrue(aaa.exists());
        checkCached(getDefaultRepository(), "aaa");
        // Modified content.
        assertEquals("MODIFIED\n", readFile(new File(getDefaultRepository(), "README.txt")));
    }

    @Test
    public void testResetMixed() throws Exception {
        NativeGit git = new NativeGit(getDefaultRepository());
        File aaa = addFile(getDefaultRepository(), "aaa", "aaa\n");
        FileOutputStream fos = new FileOutputStream(new File(getDefaultRepository(), "README.txt"));
        fos.write("MODIFIED\n".getBytes());
        fos.flush();
        fos.close();
        String initMessage = git.createLogCommand().setCount(1).execute().get(0).getMessage();
        git.createAddCommand().setFilePattern(new String[]{"."}).execute();
        git.createCommitCommand().setMessage("add file").execute();
        //make mixed reset
        getDefaultConnection().reset(new ResetRequest("HEAD^", ResetRequest.ResetType.MIXED));
        // Revert to previous revision.
        assertEquals(initMessage, git.createLogCommand().setCount(1).execute().get(0).getMessage());
        // New file untouched.
        assertTrue(aaa.exists());
        // But removed from index.
        checkNotCached(getDefaultRepository(), "aaa");
        // Modified content.
        assertEquals("MODIFIED\n", readFile(new File(getDefaultRepository(), "README.txt")));
    }
}
