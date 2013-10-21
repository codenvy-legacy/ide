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
import org.exoplatform.ide.git.server.GitException;
import org.exoplatform.ide.git.shared.DiffRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Contains information about difference between two commits, commit and working tree,
 * working tree and index, commit and index.
 *
 * @author <a href="maito:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class NativeGitDiffPage extends DiffPage {

    private static Logger LOG = LoggerFactory.getLogger(NativeGitDiffPage.class);
    private DiffRequest request;
    private NativeGit nativeGit;

    public NativeGitDiffPage(DiffRequest request, NativeGit nativeGit) {
        this.request = request;
        this.nativeGit = nativeGit;
    }

    /**
     * @see org.exoplatform.ide.git.server.DiffPage#writeTo(java.io.OutputStream)
     */
    @Override
    public void writeTo(OutputStream out) throws IOException {
        try (PrintWriter outWriter = new PrintWriter(out);){
            if (request.getCommitA() == null && request.getCommitB() == null && !request.isCached()) {
                writeUntracked(outWriter);
                workingTreeToIndex(outWriter);
            } else if (request.getCommitA() != null && request.getCommitB() == null && !request.isCached()) {
                writeUntracked(outWriter);
                commitToWorkingTree(request.getCommitA(), outWriter);
            } else if (request.getCommitB() == null && request.isCached()) {
                commitToIndex(request.getCommitA(), outWriter);
            } else {
                commitToCommit(request.getCommitA(), request.getCommitB(), outWriter);
            }
        } catch (GitException e) {
            LOG.error("Diff page creating exception", e);
        }
    }

    /**
     * @param commit commit
     * @return difference between index and commit
     * @throws GitException when any error occurs
     */
    private void commitToIndex(String commit, PrintWriter outWriter) throws GitException {
        outWriter.print(nativeGit.createDiffCommand()
                .setCommitA(commit)
                .setCached(true)
                .setType(request.getType().toString())
                .setFileFilter(request.getFileFilter())
                .setNoRenames(request.isNoRenames())
                .setRenamesCount(request.getRenameLimit())
                .execute());
    }

    /**
     * @return difference between working tree and index
     * @throws GitException when any error occurs
     */
    private void workingTreeToIndex(PrintWriter outWriter) throws GitException {
        outWriter.print(nativeGit.createDiffCommand()
                .setType(request.getType().toString())
                .setFileFilter(request.getFileFilter())
                .setNoRenames(request.isNoRenames())
                .setRenamesCount(request.getRenameLimit())
                .execute());
    }

    /**
     * @param commitA commit
     * @param commitB commit
     * @return difference between two commits
     * @throws GitException when any error occurs
     */
    private void commitToCommit(String commitA, String commitB, PrintWriter outWriter) throws GitException {
        outWriter.print(nativeGit.createDiffCommand()
                .setCommitA(commitA)
                .setCommitB(commitB)
                .setType(request.getType().toString())
                .setFileFilter(request.getFileFilter())
                .setNoRenames(request.isNoRenames())
                .setRenamesCount(request.getRenameLimit())
                .execute());
    }

    /**
     * @param commit commit
     * @return difference between commit and working tree
     * @throws GitException
     */
    private void commitToWorkingTree(String commit, PrintWriter outWriter) throws GitException {
         outWriter.print(nativeGit.createDiffCommand()
                 .setCommitA(commit)
                 .setType(request.getType().toString())
                 .setFileFilter(request.getFileFilter())
                 .setNoRenames(request.isNoRenames())
                 .setRenamesCount(request.getRenameLimit())
                 .execute());
    }


    /**
     * Just created files is in working tree and default git diff, doesn't
     * show it, cause this files untracked. When need to show difference between working tree and
     * something else, this files need to add, it is possible using difference between every untracked file
     * and /dev/null.
     *
     * @return difference between untracked files and /dev/null
     * @throws GitException when any error occurs
     */
    private void writeUntracked(PrintWriter outWriter) throws GitException {
        List<String> untrackedFileNames = nativeGit.createListFilesCommand()
                .setOthers(true).setExcludeStandard(true).execute();
        //filter untracked files
        if (request.getFileFilter() != null) {
            Set<String> filtered = new HashSet<>();
            for (String filterFile : request.getFileFilter()) {
                for (String untFile : untrackedFileNames) {
                    if (untFile.startsWith(filterFile)) {
                        filtered.add(untFile);
                    }
                }
            }
            untrackedFileNames = new LinkedList<>(filtered);
        }
        if (untrackedFileNames.size() != 0) {
            if (request.getType() == null || request.getType() == DiffRequest.DiffType.RAW) {
                //make diff with /dev/null
                String fileFilter[] = request.getFileFilter();
                request.setFileFilter(null);
                for (String fileName : untrackedFileNames) {
                    try {
                        commitToCommit("/dev/null", fileName, outWriter);
                    } catch (GitException e) {
                        //when it is diff with /dev/null exit value is 1
                        outWriter.append(e.getMessage());
                        if (request.getType() == DiffRequest.DiffType.RAW) {
                            outWriter.println();
                        }
                    }
                }
                request.setFileFilter(fileFilter);
            } else if (request.getType() == DiffRequest.DiffType.NAME_ONLY) {
                for (String fileName : untrackedFileNames) {
                    outWriter.println(fileName);
                }
            } else {
                //untracked files status is A
                for (String fileName : untrackedFileNames) {
                    outWriter.println("A\t" + fileName);
                }
            }
        }
    }

}
