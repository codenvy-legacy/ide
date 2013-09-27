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
 * Request to reset current HEAD to the specified state.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: ResetRequest.java 22817 2011-03-22 09:17:52Z andrew00x $
 */
public class ResetRequest extends GitRequest {
    /** Type of reset operation. */
    public enum ResetType {
        /**
         * Change the ref and the index, the workdir is not changed (default).
         */
        MIXED {
            @Override
            public String toString() {
                return "--mixed";
            }
        },
        /**
         * Just change the ref, the index and workdir are not changed.
         */
        SOFT {
            @Override
            public String toString() {
                return "--soft";
            }
        },
        /**
         * Change the ref, the index and the workdir.
         */
        HARD {
            @Override
            public String toString() {
                return "--hard";
            }
        },
        /**
         * Change the ref, the index and the workdir that are different between respective commit and HEAD.
         */
        KEEP {
            @Override
            public String toString() {
                return "--keep";
            }
        },
        /**
         * Resets the index and updates the files in the working tree that are different between respective commit and HEAD, but keeps those
         * which are different between the index and working tree
         */
        MERGE {
            @Override
            public String toString() {
                return "--merge";
            }
        };

    }

    /**
     * Commit to which current head should be reset, e.g. 'HEAD^' one commit back in history.
     */
    private String    commit;

    /**
     * Type of reset.
     * 
     * @see ResetType
     */
    private ResetType type = ResetType.MIXED;

    /**
     * @param commit commit to which current head should be reset
     * @param type type of reset
     * @see #commit
     * @see #type
     */
    public ResetRequest(String commit, ResetType type) {
        this.commit = commit;
        this.type = type;
    }

    /**
     * "Empty" reset request. Corresponding setters used to setup required parameters.
     */
    public ResetRequest() {
    }

    /**
     * @return commit to which current head should be reset
     * @see #commit
     */
    public String getCommit() {
        return commit;
    }

    /**
     * @param commit commit to which current head should be reset
     * @see #commit
     */
    public void setCommit(String commit) {
        this.commit = commit;
    }

    /**
     * @return type of reset.
     * @see ResetType
     */
    public ResetType getType() {
        return type;
    }

    /**
     * @param type type of reset.
     * @see ResetType
     */
    public void setType(ResetType type) {
        this.type = type;
    }
}
