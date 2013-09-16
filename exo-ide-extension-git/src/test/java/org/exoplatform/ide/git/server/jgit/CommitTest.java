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
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.FileTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.exoplatform.ide.git.shared.CommitRequest;
import org.exoplatform.ide.git.shared.Revision;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: CommitTest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class CommitTest extends BaseTest {
    public void testCommit() throws Exception {
        Repository repository = getDefaultRepository();
        // Add file.
        addFile(getDefaultRepository().getWorkTree(), "file1", "file1");

        Git git = new Git(getDefaultRepository());
        git.add().addFilepattern(".").call();

        CommitRequest request = new CommitRequest("add file1");
        Revision revision = getDefaultConnection().commit(request);

        RevCommit revCommit = git.log().call().iterator().next();

        assertEquals("add file1", revision.getMessage());
        assertEquals(revCommit.getId().getName(), revision.getId());

        String configName = repository.getConfig().getString("user", null, "name");
        String configEmail = repository.getConfig().getString("user", null, "email");

        if (configName != null) {
            assertEquals(configName, revision.getCommitter().getName());
        } else {
            assertEquals("andrey", revision.getCommitter().getName());
        }

        if (configEmail != null) {
            assertEquals(configEmail, revision.getCommitter().getEmail());
        } else {
            assertEquals("andrey@mail.com", revision.getCommitter().getEmail());
        }
    }

    public void testCommitAll() throws Exception {
        // Add file and commit.
        Repository repository = getDefaultRepository();
        File file = addFile(repository.getWorkTree(), "file2", "file2");

        Git git = new Git(repository);
        git.add().addFilepattern(".").call();
        git.commit().setMessage("add file2").call();

        // Update file.
        FileOutputStream fileStream = new FileOutputStream(file);
        fileStream.write("updated".getBytes());
        fileStream.close();

        // Remove.
        delete(new File(repository.getWorkTree(), "README.txt"));

        CommitRequest request = new CommitRequest("update file2", true, false);
        Revision revision = getDefaultConnection().commit(request);

        RevCommit revCommit = git.log().call().iterator().next();

        assertEquals("update file2", revision.getMessage());
        assertEquals(revCommit.getId().getName(), revision.getId());

        String configName = repository.getConfig().getString("user", null, "name");
        String configEmail = repository.getConfig().getString("user", null, "email");

        if (configName != null) {
            assertEquals(configName, revision.getCommitter().getName());
        } else {
            assertEquals("andrey", revision.getCommitter().getName());
        }

        if (configEmail != null) {
            assertEquals(configEmail, revision.getCommitter().getEmail());
        } else {
            assertEquals("andrey@mail.com", revision.getCommitter().getEmail());
        }

        checkNoFilesInCache(repository, "README.txt");

        TreeWalk treeWalk = new TreeWalk(repository);
        treeWalk.reset();
        treeWalk.setRecursive(true);
        try {
            ObjectId head = repository.resolve(Constants.HEAD);

            RevWalk revWalk = new RevWalk(repository);
            RevTree headTree;
            try {
                headTree = revWalk.parseTree(head);
            } finally {
                revWalk.release();
            }
            treeWalk.addTree(headTree);
            treeWalk.addTree(new FileTreeIterator(repository));
            treeWalk.setFilter(TreeFilter.ANY_DIFF);
            // Index and file system the same so changes added.
            assertFalse(treeWalk.next());
        } finally {
            treeWalk.release();
        }
    }
}
