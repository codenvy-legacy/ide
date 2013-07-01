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