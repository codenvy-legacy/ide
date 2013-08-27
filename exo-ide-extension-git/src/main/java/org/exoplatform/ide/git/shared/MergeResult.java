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
package org.exoplatform.ide.git.shared;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: MergeResult.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public interface MergeResult {
    public enum MergeStatus {
        FAST_FORWARD {
            @Override
            public String toString() {
                return "Fast-forward";
            }
        },
        ALREADY_UP_TO_DATE {
            @Override
            public String toString() {
                return "Already up-to-date";
            }
        },
        FAILED {
            @Override
            public String toString() {
                return "Failed";
            }
        },
        MERGED {
            @Override
            public String toString() {
                return "Merged";
            }
        },
        CONFLICTING {
            @Override
            public String toString() {
                return "Conflicting";
            }
        },
        NOT_SUPPORTED {
            @Override
            public String toString() {
                return "Not-yet-supported";
            }
        }
    }

    /** @return head after the merge */
    String getNewHead();

    /** @return status of merge */
    MergeStatus getMergeStatus();

    /** @return merged commits */
    String[] getMergedCommits();

    /** @return files that has conflicts. May return <code>null</code> or empty array if there is no conflicts */
    String[] getConflicts();

    /** @return files that failed to merge (not files that has conflicts). */
    String[] getFailed();
}
