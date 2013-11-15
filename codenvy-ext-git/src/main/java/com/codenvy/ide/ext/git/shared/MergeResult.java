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
package com.codenvy.ide.ext.git.shared;

import com.codenvy.ide.dto.DTO;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: MergeResult.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
@DTO
public interface MergeResult {
    public enum MergeStatus {
        FAST_FORWARD("Fast-forward"),
        ALREADY_UP_TO_DATE("Already up-to-date"),
        FAILED("Failed"),
        MERGED("Merged"),
        CONFLICTING("Conflicting"),
        NOT_SUPPORTED("Not-yet-supported");

        private final String value;

        private MergeStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
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