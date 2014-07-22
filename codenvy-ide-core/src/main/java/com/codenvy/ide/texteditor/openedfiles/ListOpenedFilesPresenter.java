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

import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.api.resources.FileEvent;
import com.codenvy.ide.api.resources.FileEvent.FileOperation;
import com.codenvy.ide.collections.Array;
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
    public void showDialog(@NotNull Array<ItemReference> files, int x, int y, @NotNull AsyncCallback<Void> callback) {
        this.callback = callback;
        view.setOpenedFiles(files);
        view.showDialog(x, y);
    }

    /** {@inheritDoc} */
    @Override
    public void onFileSelected(ItemReference file) {
        view.close();
        eventBus.fireEvent(new FileEvent(file, FileOperation.OPEN));
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseFile(ItemReference file) {
        view.close();
        eventBus.fireEvent(new FileEvent(file, FileOperation.CLOSE));
    }

    /** {@inheritDoc} */
    @Override
    public void onClose() {
        callback.onSuccess(null);
    }
}
