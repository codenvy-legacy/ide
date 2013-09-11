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
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.transport.RefSpec;
import org.exoplatform.ide.git.shared.Branch;
import org.exoplatform.ide.git.shared.BranchListRequest;
import org.exoplatform.ide.git.shared.CloneRequest;
import org.exoplatform.ide.git.shared.GitUser;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: BranchListTest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class BranchListTest extends BaseTest {
    private Repository repository2;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Repository repository = getDefaultRepository();

        File workDir = new File(repository.getWorkTree().getParentFile(), "ListBranchTest");
        // Clone repository.
        JGitConnection client2 =
                                 new JGitConnection(new FileRepository(new File(workDir, ".git")), new GitUser("andrey", "andrey@mail.com"));
        client2.clone(new CloneRequest(repository.getWorkTree().getAbsolutePath(), //
                                       null /* .git directory already set. Not need to pass it in this implementation. */));
        repository2 = client2.getRepository();

        File workDir2 = repository2.getWorkTree();
        forClean.add(workDir2);

        // Add file and commit in remote branch.
        addFile(workDir2, "file1", "init");
        Git git2 = new Git(repository2);
        git2.add().addFilepattern(".").call();
        git2.commit().setMessage("init").setAuthor("andrey", "andrey@mail.com").call();
        git2.push().setRefSpecs(new RefSpec("refs/heads/master:refs/remotes/test")).call();
    }

    public void testListBranchSimple() throws Exception {
        BranchListRequest request = new BranchListRequest();
        List<Branch> branchList = getDefaultConnection().branchList(request);
        validateBranchList(branchList, Arrays.asList(new Branch("refs/heads/master", true, "master", false)));
    }

    public void testListBranchRemote() throws Exception {
        BranchListRequest request = new BranchListRequest(BranchListRequest.LIST_REMOTE);
        List<Branch> branchList = getDefaultConnection().branchList(request);
        validateBranchList(branchList, Arrays.asList(new Branch("refs/remotes/test", false, "test", true)));
    }

    public void testListBranchAll() throws Exception {
        BranchListRequest request = new BranchListRequest(BranchListRequest.LIST_ALL);
        List<Branch> branchList = getDefaultConnection().branchList(request);
        validateBranchList(branchList,
                           Arrays.asList(new Branch("refs/remotes/test", false, "test", true), new Branch("refs/heads/master", true,
                                                                                                          "master", false)));
    }

    public void testListBranch2() throws Exception {
        new Git(getDefaultRepository()).branchCreate().setName("testListBranch2").call();
        BranchListRequest request = new BranchListRequest();
        List<Branch> branchList = getDefaultConnection().branchList(request);
        validateBranchList(branchList, Arrays.asList(new Branch("refs/heads/testListBranch2", false, "testListBranch2", false),
                                                     new Branch("refs/heads/master", true, "master", false)));
    }

    public void testListBranch3() throws Exception {
        Git git = new Git(getDefaultRepository());
        git.branchCreate().setName("testListBranch3").call();
        // Make newly created branch active.
        git.checkout().setName("testListBranch3").call();

        BranchListRequest request = new BranchListRequest();
        List<Branch> branchList = getDefaultConnection().branchList(request);
        validateBranchList(branchList, Arrays.asList(new Branch("refs/heads/master", false, "master", false),
                                                     new Branch("refs/heads/testListBranch3", true, "testListBranch3", false)));
    }
}
