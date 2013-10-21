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

import org.exoplatform.ide.git.server.DiffPage;
import org.exoplatform.ide.git.shared.DiffRequest;
import org.exoplatform.ide.git.shared.DiffRequest.DiffType;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="maito:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class DiffTest extends BaseTest {

    private NativeGit nativeGit;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        addFile(getDefaultRepository(), "aaa", "AAA\n");
        new File(getDefaultRepository(), "README.txt").delete();
        nativeGit = new NativeGit(getDefaultRepository());
    }

    @Test
    public void testDiffNameStatus() throws Exception {
        List<String> diff = readDiff(new DiffRequest(null, DiffType.NAME_STATUS, false, 0));
        assertEquals(2, diff.size());
        assertTrue(diff.contains("D\tREADME.txt"));
        assertTrue(diff.contains("A\taaa"));
    }

    @Test
    public void testDiffNameStatusWithCommits() throws Exception {
        nativeGit.createAddCommand().setFilePattern(new String[]{"."}).execute();
        nativeGit.createRemoveCommand().setListOfFiles(new String[]{"README.txt"}).execute();
        nativeGit.createCommitCommand().setMessage("testDiffNameStatusWithCommits")
                .setAuthor(getDefaultUser()).execute();
        List<String> diff = readDiff(new DiffRequest(null, DiffType.NAME_STATUS, false, 0, "HEAD^", "HEAD"));
        assertEquals(2, diff.size());
        assertTrue(diff.contains("D\tREADME.txt"));
        assertTrue(diff.contains("A\taaa"));
    }

    @Test
    public void testDiffNameStatusWithFileFilter() throws Exception {
        List<String> diff = readDiff(new DiffRequest(new String[]{"aaa"}, DiffType.NAME_STATUS, false, 0));
        assertEquals(1, diff.size());
        assertTrue(diff.contains("A\taaa"));
    }

    @Test
    public void testDiffNameStatusWithFileFilterAndCommits() throws Exception {
        nativeGit.createAddCommand().setFilePattern(new String[]{"aaa"}).execute();
        nativeGit.createRemoveCommand().setListOfFiles(new String[]{"README.txt"}).execute();
        nativeGit.createCommitCommand().setMessage("testDiffNameStatusWithCommits")
                .setAuthor(getDefaultUser()).execute();
        List<String> diff = readDiff(new DiffRequest(new String[]{"aaa"},
                DiffRequest.DiffType.NAME_STATUS, false, 0, "HEAD^1", "HEAD"));
        assertEquals(1, diff.size());
        assertTrue(diff.contains("A\taaa"));
    }

    @Test
    public void testDiffNameOnly() throws Exception {
        List<String> diff = readDiff(new DiffRequest(null, DiffType.NAME_ONLY, false, 0));
        assertEquals(2, diff.size());
        assertTrue(diff.contains("README.txt"));
        assertTrue(diff.contains("aaa"));
    }

    @Test
    public void testDiffNameOnlyWithCommits() throws Exception {
        nativeGit.createAddCommand().setFilePattern(new String[]{"aaa"}).execute();
        nativeGit.createRemoveCommand().setListOfFiles(new String[]{"README.txt"}).execute();
        nativeGit.createCommitCommand().setMessage("testDiffNameStatusWithCommits")
                .setAuthor(getDefaultUser()).execute();
        List<String> diff = readDiff(new DiffRequest(null, DiffType.NAME_ONLY, false, 0, "HEAD^1", "HEAD"));
        assertEquals(2, diff.size());
        assertTrue(diff.contains("README.txt"));
        assertTrue(diff.contains("aaa"));
    }

    @Test
    public void testDiffNameOnlyCached() throws Exception {
        nativeGit.createAddCommand().setFilePattern(new String[]{"aaa"}).execute();
        List<String> diff = readDiff(new DiffRequest(null, DiffType.NAME_ONLY, false, 0, "HEAD", true));
        assertEquals(1, diff.size());
        assertTrue(diff.contains("aaa"));
    }

    @Test
    public void testDiffNameOnlyCachedNoCommit() throws Exception {
        nativeGit.createAddCommand().setFilePattern(new String[]{"aaa"}).execute();
        List<String> diff = readDiff(new DiffRequest(null, DiffType.NAME_ONLY, false, 0, null, true));
        assertEquals(1, diff.size());
        assertTrue(diff.contains("aaa"));
    }

    @Test
    public void testDiffNameOnlyWorkingTree() throws Exception {
        List<String> diff = readDiff(new DiffRequest(null, DiffType.NAME_ONLY, false, 0, "HEAD", false));
        assertEquals(2, diff.size());
        assertTrue(diff.contains("README.txt"));
        assertTrue(diff.contains("aaa"));
    }

    @Test
    public void testDiffNameOnlyWithFileFilter() throws Exception {
        List<String> diff = readDiff(new DiffRequest(new String[]{"aaa"}, DiffType.NAME_ONLY, false, 0));
        assertEquals(1, diff.size());
        assertTrue(diff.contains("aaa"));
    }

    @Test
    public void testDiffNameOnlyWithFileFilterAndCommits() throws Exception {
        nativeGit.createAddCommand().setFilePattern(new String[]{"aaa"}).execute();
        nativeGit.createRemoveCommand().setListOfFiles(new String[]{"README.txt"}).execute();
        nativeGit.createCommitCommand().setMessage("testDiffNameStatusWithCommits")
                .setAuthor(getDefaultUser()).execute();
        List<String> diff = readDiff(new DiffRequest(new String[]{"aaa"},
                DiffType.NAME_ONLY, false, 0, "HEAD^1", "HEAD"));
        assertEquals(1, diff.size());
        assertTrue(diff.contains("aaa"));
    }

    @Test
    public void testDiffRaw() throws Exception {
        DiffRequest request = new DiffRequest(null, DiffType.RAW, false, 0);
        DiffPage diffPage = getDefaultConnection().diff(request);
        diffPage.writeTo(System.out);
    }

    @Test
    public void testDiffRawWithCommits() throws Exception {
        nativeGit.createAddCommand().setFilePattern(new String[]{"aaa"}).execute();
        nativeGit.createRemoveCommand().setListOfFiles(new String[]{"README.txt"}).execute();
        nativeGit.createCommitCommand().setMessage("testDiffNameStatusWithCommits").execute();
        DiffRequest request = new DiffRequest(null, DiffType.RAW, false, 0, "HEAD^1", "HEAD");
        DiffPage diffPage = getDefaultConnection().diff(request);
        diffPage.writeTo(System.out);
    }

    @Test
    private List<String> readDiff(DiffRequest request) throws Exception {
        DiffPage diffPage = getDefaultConnection().diff(request);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        diffPage.writeTo(out);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(out.toByteArray())));

        String line;
        List<String> diff = new ArrayList<>();
        while ((line = reader.readLine()) != null)
            diff.add(line);

        return diff;
    }
}
