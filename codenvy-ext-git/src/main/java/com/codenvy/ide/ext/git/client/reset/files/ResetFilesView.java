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
package com.codenvy.ide.ext.git.client.reset.files;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ext.git.shared.IndexFile;

import javax.validation.constraints.NotNull;

/**
 * The view of {@link com.codenvy.ide.ext.git.client.reset.commit.ResetToCommitPresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface ResetFilesView extends View<ResetFilesView.ActionDelegate> {
    String FILES = "Files for commit";

    /** Needs for delegate some function into ResetFiles view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Reset button. */
        void onResetClicked();

        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        void onCancelClicked();
    }

    /**
     * Set indexed files into table on view.
     *
     * @param indexedFiles
     *         indexed files
     */
    void setIndexedFiles(@NotNull Array<IndexFile> indexedFiles);

    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();
}