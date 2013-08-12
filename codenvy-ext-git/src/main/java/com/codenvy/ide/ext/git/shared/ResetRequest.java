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


/**
 * Request to reset current HEAD to the specified state.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: ResetRequest.java 22817 2011-03-22 09:17:52Z andrew00x $
 */
public interface ResetRequest extends GitRequest {
    /** Type of reset operation. */
    public enum ResetType {
        /** Change the ref and the index, the workdir is not changed (default). */
        MIXED("--mixed"),
        /** Just change the ref, the index and workdir are not changed. */
        SOFT("--soft"),
        /** Change the ref, the index and the workdir. */
        HARD("--hard"),
        /** Change the ref, the index and the workdir that are different between respective commit and HEAD. */
        KEEP("--keep"),
        /**
         * Resets the index and updates the files in the working tree that are different between respective commit and HEAD, but keeps
         * those
         * which are different between the index and working tree
         */
        MERGE("--merge");

        private final String value;

        private ResetType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /** @return commit to which current head should be reset */
    String getCommit();

    /** @return type of reset. */
    ResetType getType();
}