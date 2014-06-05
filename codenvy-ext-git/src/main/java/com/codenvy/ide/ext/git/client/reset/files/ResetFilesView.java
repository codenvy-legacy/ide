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