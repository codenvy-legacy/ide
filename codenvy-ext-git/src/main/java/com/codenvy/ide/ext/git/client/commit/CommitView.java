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
package com.codenvy.ide.ext.git.client.commit;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.mvp.View;

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