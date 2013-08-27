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

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.merge.ResolveMerger;
import org.exoplatform.ide.git.shared.MergeResult;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: JGitMergeResult.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class JGitMergeResult implements MergeResult {
    private final org.eclipse.jgit.api.MergeResult jgitMergeResult;

    /**
     * @param jgitMergeResult back-end instance
     */
    public JGitMergeResult(org.eclipse.jgit.api.MergeResult jgitMergeResult) {
        this.jgitMergeResult = jgitMergeResult;
    }

    /** @see org.exoplatform.ide.git.shared.MergeResult#getNewHead() */
    @Override
    public String getNewHead() {
        ObjectId newHead = jgitMergeResult.getNewHead();
        if (newHead != null) {
            return newHead.getName();
        }
        // Merge failed.
        return null;
    }

    /** @see org.exoplatform.ide.git.shared.MergeResult#getMergeStatus() */
    @Override
    public MergeStatus getMergeStatus() {
        switch (jgitMergeResult.getMergeStatus()) {
            case ALREADY_UP_TO_DATE:
                return MergeStatus.ALREADY_UP_TO_DATE;
            case CONFLICTING:
                return MergeStatus.CONFLICTING;
            case FAILED:
                return MergeStatus.FAILED;
            case FAST_FORWARD:
                return MergeStatus.FAST_FORWARD;
            case MERGED:
                return MergeStatus.MERGED;
            case NOT_SUPPORTED:
                return MergeStatus.NOT_SUPPORTED;
            case CHECKOUT_CONFLICT:
                return MergeStatus.CONFLICTING;
        }
        throw new IllegalStateException("Unknown merge status " + jgitMergeResult.getMergeStatus());
    }

    /** @see org.exoplatform.ide.git.shared.MergeResult#getMergedCommits() */
    @Override
    public String[] getMergedCommits() {
        ObjectId[] jgitMergedCommits = jgitMergeResult.getMergedCommits();
        if (jgitMergedCommits == null) {
            jgitMergedCommits = new ObjectId[0];
        }
        String[] mergedCommits = new String[jgitMergedCommits.length];
        for (int i = 0; i < jgitMergedCommits.length; i++) {
            mergedCommits[i] = jgitMergedCommits[i].getName();
        }
        return mergedCommits;
    }

    /** @see org.exoplatform.ide.git.shared.MergeResult#getConflicts() */
    @Override
    public String[] getConflicts() {
        if (jgitMergeResult.getMergeStatus().equals(org.eclipse.jgit.api.MergeResult.MergeStatus.CHECKOUT_CONFLICT)) {
            List<String> conflicts = jgitMergeResult.getCheckoutConflicts();
            return conflicts.toArray(new String[conflicts.size()]);
        }
        Map<String, int[][]> conflicts = jgitMergeResult.getConflicts();
        String[] files = null;
        if (conflicts != null) {
            files = new String[conflicts.size()];
            int i = 0;
            for (String file : conflicts.keySet()) {
                files[i++] = file;
            }
        }
        return files;
    }

    /** @see org.exoplatform.ide.git.shared.MergeResult#getFailed() */
    @Override
    public String[] getFailed() {
        String[] files = null;
        Map<String, ResolveMerger.MergeFailureReason> failing = jgitMergeResult.getFailingPaths();
        if (failing != null) {
            files = failing.keySet().toArray(new String[failing.keySet().size()]);
        }
        return files;
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString() {
        return "JGitMergeResult [getNewHead()=" + getNewHead() + ", getMergeStatus()=" + getMergeStatus()
               + ", getMergedCommits()=" + Arrays.toString(getMergedCommits()) + ", getConflicts()="
               + Arrays.toString(getConflicts()) + "]";
    }
}
