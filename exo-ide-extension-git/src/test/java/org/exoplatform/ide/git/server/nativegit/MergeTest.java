package org.exoplatform.ide.git.server.nativegit;

import org.exoplatform.ide.git.server.GitException;
import org.exoplatform.ide.git.shared.MergeRequest;
import org.exoplatform.ide.git.shared.MergeResult;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class MergeTest extends BaseTest {

    private String branchName = "MergeTestBranch";

    @Test
    public void testMergeNoChanges() throws Exception {
        new NativeGit(getDefaultRepository()).createBranchCreateCommand().setBranchName(branchName).execute();
        MergeResult mergeResult = getDefaultConnection().merge(new MergeRequest("MergeTestBranch"));
        assertEquals(MergeResult.MergeStatus.ALREADY_UP_TO_DATE, mergeResult.getMergeStatus());
    }

    @Test
    public void testMerge() throws Exception {
        NativeGit git = new NativeGit(getDefaultRepository());
        git.createBranchCheckoutCommand().setBranchName(branchName).setCreateNew(true).execute();
        File file = addFile(getDefaultRepository(), "t-merge", "aaa\n");

        git.createAddCommand().setFilePattern(new String[]{"."}).execute();
        git.createCommitCommand().setMessage("add file in new branch").execute();
        git.createBranchCheckoutCommand().setBranchName("master").execute();

        MergeResult mergeResult = getDefaultConnection().merge(new MergeRequest("MergeTestBranch"));
        assertEquals(MergeResult.MergeStatus.FAST_FORWARD, mergeResult.getMergeStatus());
        assertTrue(file.exists());
        assertEquals("aaa\n", readFile(file));
        assertEquals("add file in new branch", git.createLogCommand().setCount(1).execute().get(0).getMessage());
    }

    @Test
    public void testMergeConflict() throws Exception {
        NativeGit git = new NativeGit(getDefaultRepository());
        git.createBranchCheckoutCommand().setBranchName(branchName).setCreateNew(true).execute();
        addFile(getDefaultRepository(), "t-merge-conflict", "aaa\n");

        git.createAddCommand().setFilePattern(new String[]{"."}).execute();
        git.createCommitCommand().setMessage("add file in new branch").execute();
        git.createBranchCheckoutCommand().setBranchName("master").execute();

        addFile(getDefaultRepository(), "t-merge-conflict", "bbb\n");
        git.createAddCommand().setFilePattern(new String[]{"."}).execute();
        git.createCommitCommand().setMessage("add file in new branch").execute();

        MergeResult mergeResult = getDefaultConnection().merge(new MergeRequest("MergeTestBranch"));
        String[] conflicts = mergeResult.getConflicts();
        assertEquals(1, conflicts.length);
        assertEquals("t-merge-conflict", conflicts[0]);

        assertEquals(MergeResult.MergeStatus.CONFLICTING, mergeResult.getMergeStatus());

        String expContent = "<<<<<<< HEAD\n" //
                + "bbb\n" //
                + "=======\n" //
                + "aaa\n" //
                + ">>>>>>> MergeTestBranch\n";

        String actual = readFile(new File(getDefaultRepository(), "t-merge-conflict"));
        assertEquals(expContent, actual);
    }

    @Test
    public void testFailed() throws GitException, IOException {
        NativeGit git = new NativeGit(getDefaultRepository());
        git.createBranchCheckoutCommand().setBranchName(branchName).setCreateNew(true).execute();
        addFile(getDefaultRepository(), "t-merge-failed", "aaa\n");
        git.createAddCommand().setFilePattern(new String[]{"."}).execute();
        git.createCommitCommand().setMessage("add file in new branch").execute();
        git.createBranchCheckoutCommand().setBranchName("master").execute();

        addFile(getDefaultRepository(), "t-merge-failed", "bbb\n");
        MergeResult mergeResult = getDefaultConnection().merge(new MergeRequest("MergeTestBranch"));
        assertTrue(mergeResult.getMergeStatus() == MergeResult.MergeStatus.FAILED);
        assertEquals(1, mergeResult.getFailed().length);
        assertEquals("t-merge-failed", mergeResult.getFailed()[0]);
    }
}