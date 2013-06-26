/*
 * Copyright (C) 2013 eXo Platform SAS.
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