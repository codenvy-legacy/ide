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
package org.eclipse.che.ide.restore;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.event.FileEvent;
import org.eclipse.che.ide.api.event.OpenProjectEvent;
import org.eclipse.che.ide.api.event.ProjectActionEvent;
import org.eclipse.che.ide.api.event.ProjectActionHandler;
import org.eclipse.che.ide.api.project.tree.TreeNode;

import java.util.List;

/**
 * //
 *
 * @author Artem Zatsarynnyy
 */
public class RestoreFilesAction extends AbstractRestoreAction {
    private final EventBus   eventBus;
    private final AppContext appContext;

    private HandlerRegistration handlerRegistration;

    public RestoreFilesAction(EventBus eventBus, AppContext appContext) {
        this.eventBus = eventBus;
        this.appContext = appContext;
    }

    @Override
    protected void actionPerformed(ActionEvent e, Callback callback) {
        List<String> openedFilesPaths;

        appContext.getCurrentProject().getCurrentTree().getNodeByPath("filePath", new AsyncCallback<TreeNode<?>>() {
            @Override
            public void onSuccess(TreeNode<?> result) {

            }

            @Override
            public void onFailure(Throwable caught) {
            }
        });

//        eventBus.fireEvent(new FileEvent(this, FileEvent.FileOperation.OPEN));

//        handlerRegistration = eventBus.addHandler(ProjectActionEvent.TYPE, getProjectOpenedHandler(callback));
//        eventBus.fireEvent(new OpenProjectEvent("projectName"));
    }

    private ProjectActionHandler getProjectOpenedHandler(final Callback callback) {
        return new ProjectActionHandler() {
            @Override
            public void onProjectOpened(ProjectActionEvent event) {
                if (handlerRegistration != null) {
                    handlerRegistration.removeHandler();
                }
                callback.onPerformed();
            }

            @Override
            public void onProjectClosed(ProjectActionEvent event) {
            }
        };
    }
}
