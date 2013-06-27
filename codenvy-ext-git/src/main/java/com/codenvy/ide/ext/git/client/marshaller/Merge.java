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
package com.codenvy.ide.ext.git.client.marshaller;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.ext.git.shared.MergeResult;
import com.codenvy.ide.json.JsonArray;

/**
 * Represents the merge operation result.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jul 20, 2011 11:47:56 AM anya $
 */
public class Merge implements MergeResult {
    /** Commit head after merge. */
    private String            newHead;
    /** Status of merge operation. */
    private MergeStatus       mergeStatus;
    /** List of merged commits. */
    private JsonArray<String> mergedCommits;
    /** List of files with conflicts. */
    private JsonArray<String> conflicts;
    private JsonArray<String> failed;

    /** {@inheritDoc} */
    @Override
    public String getNewHead() {
        return newHead;
    }

    /** {@inheritDoc} */
    @Override
    public MergeStatus getMergeStatus() {
        return mergeStatus;
    }

    /** {@inheritDoc} */
    @Override
    public JsonArray<String> getMergedCommits() {
        return mergedCommits;
    }

    /** {@inheritDoc} */
    @Override
    public JsonArray<String> getConflicts() {
        return conflicts;
    }

    /** {@inheritDoc} */
    @Override
    public JsonArray<String> getFailed() {
        return failed;
    }

    /**
     * @param newHead
     *         the newHead to set
     */
    public void setNewHead(String newHead) {
        this.newHead = newHead;
    }

    /**
     * @param mergeStatus
     *         the mergeStatus to set
     */
    public void setMergeStatus(MergeStatus mergeStatus) {
        this.mergeStatus = mergeStatus;
    }

    /**
     * @param mergedCommits
     *         the mergedCommits to set
     */
    public void setMergedCommits(JsonArray<String> mergedCommits) {
        this.mergedCommits = mergedCommits;
    }

    /**
     * @param conflicts
     *         the conflicts to set
     */
    public void setConflicts(JsonArray<String> conflicts) {
        this.conflicts = conflicts;
    }

    public void setFailed(JsonArray<String> failed) {
        this.failed = failed;
    }
}