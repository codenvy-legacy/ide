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
package com.codenvy.ide.ext.git.server.nativegit.commands;

import com.codenvy.ide.ext.git.server.GitException;
import com.codenvy.ide.ext.git.server.nativegit.NativeGitMergeResult;
import com.codenvy.ide.ext.git.shared.MergeResult;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Join two development histories together
 *
 * @author <a href="maito:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class MergeCommand extends GitCommand<MergeResult> {

    private String commit;

    public MergeCommand(File repository) {
        super(repository);
    }

    /** @see com.codenvy.ide.ext.git.server.nativegit.commands.GitCommand#execute() */
    @Override
    public MergeResult execute() throws GitException {
        if (commit == null) {
            throw new GitException("Commit wasn't set.");
        }
        clear();
        commandLine.add("merge", commit);
        //result of merging
        NativeGitMergeResult mergeResult = new NativeGitMergeResult();
        //get merge commits
        ArrayList<String> mergedCommits = new ArrayList<String>();
        mergedCommits.add(new LogCommand(getRepository()).setCount(1).execute().get(0).getId());
        mergedCommits.add(new LogCommand(getRepository()).setBranch(commit).setCount(1).execute().get(0).getId());
        mergeResult.setMergedCommits(mergedCommits);
        try {
            start();
            // if not failed and not conflict
            if (getOutput().get(0).startsWith("Already")) {
                mergeResult.setStatus(MergeResult.MergeStatus.ALREADY_UP_TO_DATE);
            } else if (getOutput().get(0).startsWith("Updating") && getOutput().get(1).startsWith("Fast")) {
                mergeResult.setStatus(MergeResult.MergeStatus.FAST_FORWARD);
            } else {
                mergeResult.setStatus(MergeResult.MergeStatus.MERGED);
            }
        } catch (GitException e) {
            output = new LinkedList<>();
            output.addAll(Arrays.asList(e.getMessage().split("\n")));
            //if Auto-merging is first line then it is CONFLICT situation cause of exception.
            if (output.get(0).startsWith("Auto-merging")) {
                mergeResult.setStatus(MergeResult.MergeStatus.CONFLICTING);
                List<String> conflictFiles = new LinkedList<>();
                for (String outLine : output) {
                    if (outLine.startsWith("CONFLICT")) {
                        conflictFiles.add(outLine.substring(outLine.indexOf("in") + 3));
                    }
                }
                mergeResult.setConflicts(conflictFiles);
                //if Updating is first then it is Failed situation cause of exception
            } else if (output.get(0).startsWith("Updating")) {
                mergeResult.setStatus(MergeResult.MergeStatus.FAILED);
                List<String> failedFiles = new LinkedList<>();
                /*
                * First 2 lines contain not needed information:
                *
                * Updating commit1..commit2
                * error: The following untracked working tree files would be overwritten by merge:
                * file1
                * ....
                * fileN
                * Please move or remove them before you can merge.
                * Aborting
                * */
                int i = 2;
                while (!output.get(i).startsWith("Please")) {
                    failedFiles.add(output.get(i++).trim());
                }
                mergeResult.setFailed(failedFiles);
            } else {
                mergeResult.setStatus(MergeResult.MergeStatus.NOT_SUPPORTED);
            }
        }
        mergeResult.setHead(new LogCommand(getRepository()).setCount(1).execute().get(0).getId());
        return mergeResult;
    }

    /**
     * @param commit
     *         commit to merge with
     * @return MergeCommand with established commit
     */
    public MergeCommand setCommit(String commit) {
        this.commit = commit;
        return this;
    }
}
