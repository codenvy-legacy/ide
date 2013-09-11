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
package org.exoplatform.ide.git.client.marshaller;

import org.exoplatform.ide.git.shared.MergeResult;

/**
 * Represents the merge operation result.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jul 20, 2011 11:47:56 AM anya $
 */
public class Merge implements MergeResult {
    /** Commit head after merge. */
    private String      newHead;

    /** Status of merge operation. */
    private MergeStatus mergeStatus;

    /** List of merged commits. */
    private String[]    mergedCommits;

    /** List of files with conflicts. */
    private String[]    conflicts;

    private String[]    failed;

    /** @see org.exoplatform.ide.git.shared.MergeResult#getNewHead() */
    @Override
    public String getNewHead() {
        return newHead;
    }

    /** @see org.exoplatform.ide.git.shared.MergeResult#getMergeStatus() */
    @Override
    public MergeStatus getMergeStatus() {
        return mergeStatus;
    }

    /** @see org.exoplatform.ide.git.shared.MergeResult#getMergedCommits() */
    @Override
    public String[] getMergedCommits() {
        return mergedCommits;
    }

    /** @see org.exoplatform.ide.git.shared.MergeResult#getConflicts() */
    @Override
    public String[] getConflicts() {
        return conflicts;
    }

    @Override
    public String[] getFailed() {
        return failed;
    }

    /**
     * @param newHead the newHead to set
     */
    public void setNewHead(String newHead) {
        this.newHead = newHead;
    }

    /**
     * @param mergeStatus the mergeStatus to set
     */
    public void setMergeStatus(MergeStatus mergeStatus) {
        this.mergeStatus = mergeStatus;
    }

    /**
     * @param mergedCommits the mergedCommits to set
     */
    public void setMergedCommits(String[] mergedCommits) {
        this.mergedCommits = mergedCommits;
    }

    /**
     * @param conflicts the conflicts to set
     */
    public void setConflicts(String[] conflicts) {
        this.conflicts = conflicts;
    }

    public void setFailed(String[] failed) {
        this.failed = failed;
    }
}
