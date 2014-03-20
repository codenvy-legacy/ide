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

import com.codenvy.ide.api.resources.FileEvent;
import com.codenvy.ide.api.resources.FileEvent.FileOperation;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.resources.model.File;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import javax.validation.constraints.NotNull;

/**
 * Presenter for listing opened files of the editor.
 * 
 * @author Ann Shumilova
 */
@Singleton
public class ListOpenedFilesPresenter implements ListOpenedFilesView.ActionDelegate {

    private ListOpenedFilesView view;
    private EventBus            eventBus;
    private AsyncCallback<Void> callback;

    @Inject
    public ListOpenedFilesPresenter(ListOpenedFilesView view, EventBus eventBus) {
        this.view = view;
        this.eventBus = eventBus;
        this.view.setDelegate(this);
    }

    /**
     * Show the view with the list of opened files.
     * 
     * @param files opened files
     * @param x x coordinate of the right top corner of the list view
     * @param y y coordinate of the right top corner of the list view 
     * @param callback
     */
    public void showDialog(@NotNull Array<File> files, int x, int y, @NotNull AsyncCallback<Void> callback) {
        this.callback = callback;
        view.setOpenedFiles(files);
        view.showDialog(x, y);
    }


    /** {@inheritDoc} */
    @Override
    public void onFileSelected(File file) {
        view.close();
        eventBus.fireEvent(new FileEvent(file, FileOperation.OPEN));
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseFile(File file) {
        view.close();
        eventBus.fireEvent(new FileEvent(file, FileOperation.CLOSE));
    }

    /** {@inheritDoc} */
    @Override
    public void onClose() {
        callback.onSuccess(null);
    }
}
