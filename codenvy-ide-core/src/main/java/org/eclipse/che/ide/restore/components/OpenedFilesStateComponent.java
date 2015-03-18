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
package org.eclipse.che.ide.restore.components;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.api.event.FileEvent;
import org.eclipse.che.ide.api.project.tree.TreeNode;
import org.eclipse.che.ide.api.project.tree.VirtualFile;
import org.eclipse.che.ide.restore.AppStateComponent;
import org.eclipse.che.ide.restore.Callback;
import org.eclipse.che.ide.restore.ProjectState;
import org.eclipse.che.ide.util.loging.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link AppStateComponent} responsible for saving/restoring opened files.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class OpenedFilesStateComponent implements AppStateComponent {

    private final AppContext  appContext;
    private final EventBus    eventBus;

    @Inject
    public OpenedFilesStateComponent(AppContext appContext, EventBus eventBus) {
        this.appContext = appContext;
        this.eventBus = eventBus;
    }

    /** {@inheritDoc} */
    @Override
    public void save(ProjectState appState, Callback callback) {
//        final StringMap<EditorPartPresenter> openedEditors = editorAgent.getOpenedEditors();
        final List<String> openedFilesPaths = new ArrayList<>();
//        for (String filePath : openedEditors.getKeys().asIterable()) {
//            openedFilesPaths.add(filePath);
//        }
        appState.setOpenedFilesPaths(openedFilesPaths);

        callback.onPerformed();
    }

    /** {@inheritDoc} */
    @Override
    public void restore(ProjectState appState, Callback callback) {
        final List<String> openedFilesPaths = appState.getOpenedFilesPaths();
        if (openedFilesPaths.isEmpty()) {
            callback.onPerformed();
        } else {
            for (String path : openedFilesPaths) {
                openFile(path);
            }
            callback.onPerformed();
        }
    }

    private void openFile(String path) {
        final CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject == null) {
            return;
        }

        currentProject.getCurrentTree().getNodeByPath(path, new AsyncCallback<TreeNode<?>>() {
            @Override
            public void onSuccess(TreeNode<?> result) {
                eventBus.fireEvent(new FileEvent((VirtualFile)result, FileEvent.FileOperation.OPEN));
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(OpenedFilesStateComponent.class, caught);
            }
        });
    }
}
