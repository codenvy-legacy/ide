/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.texteditor.openedfiles;

import org.eclipse.che.ide.api.event.FileEvent;
import org.eclipse.che.ide.api.event.FileEvent.FileOperation;
import org.eclipse.che.ide.api.project.tree.VirtualFile;
import org.eclipse.che.ide.collections.Array;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nonnull;

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
     * @param files
     *         opened files
     * @param x
     *         x coordinate of the right top corner of the list view
     * @param y
     *         y coordinate of the right top corner of the list view
     * @param callback
     */
    public void showDialog(@Nonnull Array<VirtualFile> files, int x, int y, @Nonnull AsyncCallback<Void> callback) {
        this.callback = callback;
        view.setOpenedFiles(files);
        view.showDialog(x, y);
    }

    /** {@inheritDoc} */
    @Override
    public void onFileSelected(VirtualFile file) {
        view.close();
        eventBus.fireEvent(new FileEvent(file, FileOperation.OPEN));
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseFile(VirtualFile file) {
        view.close();
        eventBus.fireEvent(new FileEvent(file, FileOperation.CLOSE));
    }

    /** {@inheritDoc} */
    @Override
    public void onClose() {
        callback.onSuccess(null);
    }
}
