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
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Repository;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: StatusTest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class StatusTest extends BaseTest {
    public void testEmptyStatus() throws NoWorkTreeException, GitAPIException {
        Git git = new Git(getDefaultRepository());

        Status stat = git.status().call();
        assertEquals(0, stat.getAdded().size());
        assertEquals(0, stat.getChanged().size());
        assertEquals(0, stat.getMissing().size());
        assertEquals(0, stat.getModified().size());
        assertEquals(0, stat.getRemoved().size());
        assertEquals(0, stat.getUntracked().size());
    }

    public void testDifferentStates() throws IOException, NoFilepatternException, GitAPIException {
        Repository re = getDefaultRepository();
        File repo = re.getWorkTree();

        Git git = new Git(re);
        addFile(repo, "a", "content of a");
        addFile(repo, "b", "content of b");
        addFile(repo, "c", "content of c");
        git.add().addFilepattern("a").addFilepattern("b").call();
        Status stat = git.status().call();
        assertEquals(set("a", "b"), stat.getAdded());
        assertEquals(0, stat.getChanged().size());
        assertEquals(0, stat.getMissing().size());
        assertEquals(0, stat.getModified().size());
        assertEquals(0, stat.getRemoved().size());
        assertEquals(set("c"), stat.getUntracked());
        git.commit().setMessage("initial").call();

        addFile(repo, "a", "modified content of a");
        addFile(repo, "b", "modified content of b");
        addFile(repo, "d", "content of d");
        git.add().addFilepattern("a").addFilepattern("d").call();
        addFile(repo, "a", "again modified content of a");
        stat = git.status().call();
        assertEquals(set("d"), stat.getAdded());
        assertEquals(set("a"), stat.getChanged());
        assertEquals(0, stat.getMissing().size());
        assertEquals(set("b", "a"), stat.getModified());
        assertEquals(0, stat.getRemoved().size());
        assertEquals(set("c"), stat.getUntracked());
        git.add().addFilepattern(".").call();
        git.commit().setMessage("second").call();

        stat = git.status().call();
        assertEquals(0, stat.getAdded().size());
        assertEquals(0, stat.getChanged().size());
        assertEquals(0, stat.getMissing().size());
        assertEquals(0, stat.getModified().size());
        assertEquals(0, stat.getRemoved().size());
        assertEquals(0, stat.getUntracked().size());

        delete(new File(repo.getPath() + "/a"));
        assertFalse(new File(git.getRepository().getWorkTree(), "a").exists());
        git.add().addFilepattern("a").setUpdate(true).call();
        addFile(repo, "a", "recreated content of a");
        stat = git.status().call();
        assertEquals(0, stat.getAdded().size());
        assertEquals(0, stat.getChanged().size());
        assertEquals(0, stat.getMissing().size());
        assertEquals(0, stat.getModified().size());
        assertEquals(set("a"), stat.getRemoved());
        assertEquals(set("a"), stat.getUntracked());
        git.commit().setMessage("t").call();

        addFile(repo, "sub/a", "sub-file");
        stat = git.status().call();
        assertEquals(1, stat.getUntrackedFolders().size());
        assertTrue(stat.getUntrackedFolders().contains("sub"));
    }

    public static Set<String> set(String... elements) {
        Set<String> ret = new HashSet<String>();
        for (String element : elements)
            ret.add(element);
        return ret;
    }
}
