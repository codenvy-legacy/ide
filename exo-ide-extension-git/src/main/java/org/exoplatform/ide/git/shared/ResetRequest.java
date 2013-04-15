/*
 * Copyright (C) 2011 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
