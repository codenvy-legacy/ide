/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2014] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.texteditor.openedfiles;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.api.resources.model.File;

import javax.validation.constraints.NotNull;

/**
 * View for displaying all opened files of the editor.
 * 
 * @author Ann Shumilova
 */
public interface ListOpenedFilesView extends View<ListOpenedFilesView.ActionDelegate> {
    /** Needs for delegate some function into ListOpenedFiles view. */
    public interface ActionDelegate {

        /**
         * Performs any actions appropriate in response to the user having selected the file.
         * 
         * @param file selected file
         */
        void onFileSelected(File file);

        /**
         * Performs any actions appropriate in response to the user having clicked close the file.
         * 
         * @param file file to be closed
         */
        void onCloseFile(File file);

        /** Performs any actions appropriate in response to the user having closed the view. */
        void onClose();
    }

    /**
     * Sets the values of the opened files.
     * 
     * @param f
     */
    void setOpenedFiles(@NotNull Array<File> files);

    /** Close dialog. */
    void close();

    /** Show dialog.
     * @param x x coordinate of the right top corner of the list view
     * @param y y coordinate of the right top corner of the list view
     */
    void showDialog(int x, int y);
}
