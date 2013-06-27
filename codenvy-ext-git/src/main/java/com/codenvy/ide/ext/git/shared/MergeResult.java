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
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: MergeResult.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
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
    JsonArray<String> getMergedCommits();

    /** @return files that has conflicts. May return <code>null</code> or empty array if there is no conflicts */
    JsonArray<String> getConflicts();

    /** @return files that failed to merge (not files that has conflicts). */
    JsonArray<String> getFailed();
}