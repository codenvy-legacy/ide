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

import com.codenvy.ide.json.JsonArray;

/**
 * Request to show changes between commits. Use {@link #commitA} and {@link #commitB} to specify values for comparison.
 * <ul>
 * <li>If both are omitted then view changes between index and working tree.</li>
 * <li>If both are specified then view changes between two commits.</li>
 * <li>If {@link #commitA} is specified ONLY then behavior is dependent on state of {@link #cached}. If
 * <code>cached==false<code> then view changes between specified commit and working tree. If
 * <code>cached==true<code> then view changes between specified commit and index.</li>
 * </ul>
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: DiffRequest.java 22817 2011-03-22 09:17:52Z andrew00x $
 */
public interface DiffRequest extends GitRequest {
    /** Type of diff output. */
    public enum DiffType {
        /** Only names of modified, added, deleted files. */
        NAME_ONLY("--name-only"),
        /**
         * Names staus of modified, added, deleted files.
         * <p/>
         * Example:
         * <p/>
         * <p/>
         * <pre>
         * D   README.txt
         * A   HOW-TO.txt
         * </pre>
         */
        NAME_STATUS("--name-status"),
        RAW("--raw");

        private final String value;

        private DiffType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /** @return filter of file to show diff. It may be either list of file names or name of directory to show all files under them */
    JsonArray<String> getFileFilter();

    /** @return type of diff output */
    DiffType getType();

    /** @return <code>true</code> if renames must not be showing in diff result */
    boolean noRenames();

    /** @return limit of showing renames in diff output. This attribute has sense if {@link #noRenames} is <code>false</code> */
    int getRenameLimit();

    /** @return first commit to view changes */
    String getCommitA();

    /** @return second commit to view changes */
    String getCommitB();

    /**
     * @return if <code>false</code> (default) view changes between {@link #commitA} and working tree otherwise between {@link #commitA}
     *         and index
     */
    boolean cached();
}