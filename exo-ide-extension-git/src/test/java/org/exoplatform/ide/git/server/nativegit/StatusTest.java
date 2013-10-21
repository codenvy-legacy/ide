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
import org.exoplatform.ide.git.shared.Status;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="maito:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class StatusTest extends BaseTest {

    @Test
    public void testEmptyStatus() throws GitException {
        Status stat = getDefaultConnection().status(true);
        assertEquals(0, stat.getAdded().size());
        assertEquals(0, stat.getChanged().size());
        assertEquals(0, stat.getMissing().size());
        assertEquals(0, stat.getModified().size());
        assertEquals(0, stat.getRemoved().size());
        assertEquals(0, stat.getUntracked().size());
    }

    public void testDifferentStates() throws IOException, GitException {
        NativeGit nativeGit = new NativeGit(getDefaultRepository());

        addFile(getDefaultRepository(), "a", "content of a");
        addFile(getDefaultRepository(), "b", "content of b");
        addFile(getDefaultRepository(), "c", "content of c");
        nativeGit.createAddCommand().setFilePattern(new String[]{"a", "b"}).execute();
        Status stat = getDefaultConnection().status(true);
        assertEquals(set("a", "b"), stat.getAdded());
        assertEquals(0, stat.getChanged().size());
        assertEquals(0, stat.getMissing().size());
        assertEquals(0, stat.getModified().size());
        assertEquals(0, stat.getRemoved().size());
        assertEquals(set("c"), stat.getUntracked());
        nativeGit.createCommitCommand().setMessage("initial").execute();

        addFile(getDefaultRepository(), "a", "modified content of a");
        addFile(getDefaultRepository(), "b", "modified content of b");
        addFile(getDefaultRepository(), "d", "content of d");
        nativeGit.createAddCommand().setFilePattern(new String[]{"a", "d"}).execute();
        addFile(getDefaultRepository(), "a", "again modified content of a");

        stat = getDefaultConnection().status(true);
        assertEquals(set("d"), stat.getAdded());
        assertEquals(set("a"), stat.getChanged());
        assertEquals(0, stat.getMissing().size());
        assertEquals(set("b", "a"), stat.getModified());
        assertEquals(0, stat.getRemoved().size());
        assertEquals(set("c"), stat.getUntracked());
        nativeGit.createAddCommand().setFilePattern(new String[]{"."}).execute();
        nativeGit.createCommitCommand().setMessage("second").execute();

        stat = getDefaultConnection().status(true);
        assertEquals(0, stat.getAdded().size());
        assertEquals(0, stat.getChanged().size());
        assertEquals(0, stat.getMissing().size());
        assertEquals(0, stat.getModified().size());
        assertEquals(0, stat.getRemoved().size());
        assertEquals(0, stat.getUntracked().size());

        delete(new File(getDefaultRepository() + "/a"));
        assertFalse(new File(getDefaultRepository(), "a").exists());
        nativeGit.createAddCommand().setFilePattern(new String[]{"a"}).setUpdate(true).execute();
        addFile(getDefaultRepository(), "a", "recreated content of a");
        stat = getDefaultConnection().status(true);
        assertEquals(0, stat.getAdded().size());
        assertEquals(0, stat.getChanged().size());
        assertEquals(0, stat.getMissing().size());
        assertEquals(0, stat.getModified().size());
        assertEquals(set("a"), stat.getRemoved());
        assertEquals(set("a"), stat.getUntracked());
        nativeGit.createCommitCommand().setMessage("t").execute();

        addFile(getDefaultRepository(), "sub/a", "sub-file");
        stat = getDefaultConnection().status(true);
        assertEquals(1, stat.getUntrackedFolders().size());
        assertTrue(stat.getUntrackedFolders().contains("sub"));
    }

    public static Set<String> set(String... elements) {
        Set<String> ret = new HashSet<>();
        for (String element : elements)
            ret.add(element);
        return ret;
    }
}
