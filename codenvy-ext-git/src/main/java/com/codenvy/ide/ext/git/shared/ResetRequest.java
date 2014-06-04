/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.git.shared;


import com.codenvy.dto.shared.DTO;

/**
 * Request to reset current HEAD to the specified state.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: ResetRequest.java 22817 2011-03-22 09:17:52Z andrew00x $
 */
@DTO
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
    
    void setCommit(String commit);
    
    ResetRequest withCommit(String commit);

    /** @return type of reset. */
    ResetType getType();
    
    void setType(ResetType type);
}