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
package com.codenvy.ide.texteditor.openedfiles;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.api.projecttree.generic.FileNode;
import com.codenvy.ide.collections.Array;

import javax.validation.constraints.NotNull;

/**
 * View for displaying all opened files of the editor.
 *
 * @author Ann Shumilova
 */
public interface ListOpenedFilesView extends View<ListOpenedFilesView.ActionDelegate> {
    /**
     * Sets the values of the opened files.
     *
     * @param files
     */
    void setOpenedFiles(@NotNull Array<FileNode> files);

    /** Close dialog. */
    void close();

    /**
     * Show dialog.
     *
     * @param x
     *         x coordinate of the right top corner of the list view
     * @param y
     *         y coordinate of the right top corner of the list view
     */
    void showDialog(int x, int y);

    /** Needs for delegate some function into ListOpenedFiles view. */
    public interface ActionDelegate {

        /**
         * Performs any actions appropriate in response to the user having selected the file.
         *
         * @param file
         *         selected file
         */
        void onFileSelected(FileNode file);

        /**
         * Performs any actions appropriate in response to the user having clicked close the file.
         *
         * @param file
         *         file to be closed
         */
        void onCloseFile(FileNode file);

        /** Performs any actions appropriate in response to the user having closed the view. */
        void onClose();
    }
}
