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

import org.exoplatform.ide.git.shared.MergeResult;

/**
 * NativeGit implementation of org.exoplatform.ide.git.shared.MergeResult
 *
 * @author <a href="maito:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class NativeGitMergeResult implements MergeResult {

    private String head;
    private MergeStatus status;
    private String[] conflicts;
    private String[] failed;
    private String[] mergedCommits;

    /**
     * @param mergedCommits commits that was merged
     */
    public void setMergedCommits(String[] mergedCommits) {
        this.mergedCommits = mergedCommits;
    }

    /**
     * @param failed file names that failed after merge
     */
    public void setFailed(String[] failed) {
        this.failed = failed;
    }

    /**
     * @param conflicts file names that conflicting after merge
     */
    public void setConflicts(String[] conflicts) {
        this.conflicts = conflicts;
    }

    /**
     * @param status status after merge
     */
    public void setStatus(MergeStatus status) {
        this.status = status;
    }

    /**
     * @param head head after merge
     */
    public void setHead(String head) {
        this.head = head;
    }

    /**
     * @see org.exoplatform.ide.git.shared.MergeResult#getNewHead()
     */
    @Override
    public String getNewHead() {
        return head;
    }

    /**
     * @see org.exoplatform.ide.git.shared.MergeResult#getMergeStatus()
     */
    @Override
    public MergeStatus getMergeStatus() {
        return status;
    }

    /**
     * @see org.exoplatform.ide.git.shared.MergeResult#getMergedCommits()
     */
    @Override
    public String[] getMergedCommits() {
        return mergedCommits;
    }

    /**
     * @see org.exoplatform.ide.git.shared.MergeResult#getConflicts()
     */
    @Override
    public String[] getConflicts() {
        return conflicts;
    }

    /**
     * @see org.exoplatform.ide.git.shared.MergeResult#getFailed()
     */
    @Override
    public String[] getFailed() {
        return failed;
    }
}
