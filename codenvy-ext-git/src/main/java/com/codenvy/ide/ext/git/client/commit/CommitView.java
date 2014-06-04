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
package com.codenvy.ide.ext.git.client.commit;

import com.codenvy.ide.api.mvp.View;

import javax.validation.constraints.NotNull;

/**
 * The view of {@link CommitPresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface CommitView extends View<CommitView.ActionDelegate> {
    /** Needs for delegate some function into Commit view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Commit button. */
        void onCommitClicked();

        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        void onCancelClicked();

        /** Performs any actions appropriate in response to the user having changed something. */
        void onValueChanged();
    }

    /** @return entered message */
    @NotNull
    String getMessage();

    /**
     * Set content into message field.
     *
     * @param message
     *         text what need to insert
     */
    void setMessage(@NotNull String message);

    /** @return <code>true</code> if need to include all changes except from new files, and <code>false</code> otherwise */
    boolean isAllFilesInclued();

    /**
     * Set status of include changes to commit.
     *
     * @param isAllFiles
     *         <code>true</code> need to include all changes except from new, <code>false</code> include all changes
     */
    void setAllFilesInclude(boolean isAllFiles);

    /** @return <code>true</code> if need to amend the last commit, and <code>false</code> otherwise */
    boolean isAmend();

    /**
     * Set status of amend the last commit.
     *
     * @param isAmend
     *         <code>true</code> need to amend the last commit, <code>false</code> need to create new commit
     */
    void setAmend(boolean isAmend);

    /**
     * Change the enable state of the commit button.
     *
     * @param enable
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setEnableCommitButton(boolean enable);

    /** Give focus to message field. */
    void focusInMessageField();

    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();
}