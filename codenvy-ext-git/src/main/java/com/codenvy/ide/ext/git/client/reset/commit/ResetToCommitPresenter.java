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
package com.codenvy.ide.ext.git.client.reset.commit;

import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.editor.EditorInitException;
import com.codenvy.ide.api.editor.EditorInput;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.FileEvent;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.File;
import com.codenvy.ide.api.resources.model.Resource;
import com.codenvy.ide.ext.git.client.GitServiceClient;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.shared.DiffRequest;
import com.codenvy.ide.ext.git.shared.LogResponse;
import com.codenvy.ide.ext.git.shared.ResetRequest;
import com.codenvy.ide.ext.git.shared.Revision;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.StringUnmarshaller;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import javax.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;

/**
 * Presenter for resetting head to commit.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 */
@Singleton
public class ResetToCommitPresenter implements ResetToCommitView.ActionDelegate {
    private final DtoUnmarshallerFactory    dtoUnmarshallerFactory;
    private       ResetToCommitView         view;
    private       GitServiceClient          service;
    private       Revision                  selectedRevision;
    private       ResourceProvider          resourceProvider;
    private       GitLocalizationConstant   constant;
    private       NotificationManager       notificationManager;
    private       String                    projectId;
    private       EditorAgent               editorAgent;
    private       EventBus                  eventBus;
    private       List<EditorPartPresenter> openedEditors;

    /**
     * Create presenter.
     *
     * @param view
     * @param service
     * @param resourceProvider
     * @param constant
     * @param notificationManager
     */
    @Inject
    public ResetToCommitPresenter(ResetToCommitView view,
                                  GitServiceClient service,
                                  GitLocalizationConstant constant,
                                  EventBus eventBus,
                                  EditorAgent editorAgent,
                                  ResourceProvider resourceProvider,
                                  NotificationManager notificationManager,
                                  DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        this.view = view;
        this.view.setDelegate(this);
        this.service = service;
        this.constant = constant;
        this.eventBus = eventBus;
        this.editorAgent = editorAgent;
        this.resourceProvider = resourceProvider;
        this.notificationManager = notificationManager;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
    }

    /** Show dialog. */
    public void showDialog() {
        projectId = resourceProvider.getActiveProject().getId();

        service.log(projectId, false,
                    new AsyncRequestCallback<LogResponse>(dtoUnmarshallerFactory.newUnmarshaller(LogResponse.class)) {
                        @Override
                        protected void onSuccess(LogResponse result) {
                            selectedRevision = null;
                            view.setRevisions(result.getCommits());
                            view.setMixMode(true);
                            view.setEnableResetButton(false);
                            view.showDialog();
                        }

                        @Override
                        protected void onFailure(Throwable exception) {
                            String errorMessage = (exception.getMessage() != null) ? exception.getMessage() : constant.logFailed();
                            Notification notification = new Notification(errorMessage, ERROR);
                            notificationManager.showNotification(notification);
                        }
                    }
                   );
    }

    /** {@inheritDoc} */
    @Override
    public void onResetClicked() {
        view.close();

        openedEditors = new ArrayList<>();
        final List<String> listOpenedFiles = new ArrayList<>();

        for (EditorPartPresenter partPresenter : editorAgent.getOpenedEditors().getValues().asIterable()) {
            openedEditors.add(partPresenter);
            listOpenedFiles.add(partPresenter.getEditorInput().getFile().getRelativePath());
        }

        getDiff(listOpenedFiles, selectedRevision.getId(), new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable caught) {
                String errorMessage = caught.getMessage() != null ? caught.getMessage() : constant.diffFailed();
                Notification notification = new Notification(errorMessage, ERROR);
                notificationManager.showNotification(notification);
            }

            @Override
            public void onSuccess(String diff) {
                reset(diff);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onRevisionSelected(@NotNull Revision revision) {
        selectedRevision = revision;
        view.setEnableResetButton(true);
    }

    /**
     * Compare commit to reset with working tree, get the diff for pointed file(s) in text format.
     *
     * @param listFiles
     *         files for which to get changes
     * @param commit
     *         commit to compare
     * @param callback
     */
    private void getDiff(List<String> listFiles, final String commit, final AsyncCallback<String> callback) {
        String projectId = resourceProvider.getActiveProject().getId();
        service.diff(projectId, listFiles, DiffRequest.DiffType.RAW, true, 0, commit, false,
                     new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                         @Override
                         protected void onSuccess(String diff) {
                             callback.onSuccess(diff);
                         }

                         @Override
                         protected void onFailure(Throwable exception) {
                             callback.onFailure(exception);
                         }
                     });
    }

    /**
     * Reset current HEAD to the specified state and refresh project in the success case.
     *
     * @param diff
     *         diff between the specified state and current state for pointed file(s) in text format.
     */
    private void reset(final String diff) {
        ResetRequest.ResetType type = view.isMixMode() ? ResetRequest.ResetType.MIXED : null;
        type = (type == null && view.isSoftMode()) ? ResetRequest.ResetType.SOFT : type;
        type = (type == null && view.isHardMode()) ? ResetRequest.ResetType.HARD : type;
        type = (type == null && view.isKeepMode()) ? ResetRequest.ResetType.KEEP : type;
        type = (type == null && view.isMergeMode()) ? ResetRequest.ResetType.MERGE : type;

        final ResetRequest.ResetType finalType = type;
        service.reset(projectId, selectedRevision.getId(), finalType, new AsyncRequestCallback<Void>() {
            @Override
            protected void onSuccess(Void result) {

                if(ResetRequest.ResetType.HARD.equals(finalType) || ResetRequest.ResetType.MERGE.equals(finalType)) {
                    //Only in the cases of <code>ResetRequest.ResetType.HARD</code>  or <code>ResetRequest.ResetType.MERGE</code>
                    // must change the workdir
                    refreshProject(diff);
                }
                Notification notification = new Notification(constant.resetSuccessfully(), INFO);
                notificationManager.showNotification(notification);

            }

            @Override
            protected void onFailure(Throwable exception) {
                String errorMessage = (exception.getMessage() != null) ? exception.getMessage() : constant.resetFail();
                Notification notification = new Notification(errorMessage, ERROR);
                notificationManager.showNotification(notification);
            }
        });
    }

    /**
     * Refresh project.
     *
     * @param diff
     *         diff between the specified state and current state for pointed file(s) in text format.
     */
    private void refreshProject(final String diff) {
        resourceProvider.getActiveProject().refreshChildren(new AsyncCallback<Project>() {
            @Override
            public void onSuccess(Project result) {
                for (EditorPartPresenter partPresenter : openedEditors) {
                    final File file = partPresenter.getEditorInput().getFile();
                    String filePath = file.getRelativePath();

                    if (diff.contains(filePath)) {
                        int firstIndex = diff.indexOf(filePath);
                        int lastIndex = diff.lastIndexOf(filePath);
                        String between = diff.substring(firstIndex, lastIndex);

                        if (between.contains("new file mode")) {
                            //<code>diff</code> contains the string "new file mode" in the case if working tree has file
                            // that is not exist in the commit to reset. So this file is necessary to close.
                            eventBus.fireEvent(new FileEvent(file, FileEvent.FileOperation.CLOSE));
                        } else {
                            //File is changed in the commit to reset, so this file is necessary to refresh
                            refreshFile(file, partPresenter);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(ResetToCommitPresenter.class, "can not refresh children for project");
            }
        });
    }

    /**
     * Refresh file.
     *
     * @param file
     *         file to refresh
     * @param partPresenter
     *        editor that corresponds to the <code>file</code>.
     */
    private void refreshFile(final File file, final EditorPartPresenter partPresenter) {
        final Project project = resourceProvider.getActiveProject();
        project.findResourceByPath(file.getPath(), new AsyncCallback<Resource>() {
            @Override
            public void onFailure(Throwable caught) {
                String errorMessage = (caught.getMessage() != null) ? caught.getMessage() : constant.findResourceFailed( );
                Notification notification = new Notification(errorMessage, ERROR);
                notificationManager.showNotification(notification);
            }

            @Override
            public void onSuccess(final Resource result) {
                updateOpenedFile((File)result, partPresenter);
            }
        });
    }

    /**
     * Update content of the file.
     *
     * @param file
     *         file to update
     * @param partPresenter
     *        editor that corresponds to the <code>file</code>.
     */
    private void updateOpenedFile(final File file, final EditorPartPresenter partPresenter) {
        resourceProvider.getActiveProject().getContent(file, new AsyncCallback<File>() {
            @Override
            public void onSuccess(File result) {
                try {
                    EditorInput editorInput = partPresenter.getEditorInput();

                    editorInput.setFile(result);
                    partPresenter.init(editorInput);

                } catch (EditorInitException event) {
                    Log.error(ResetToCommitPresenter.class, "can not initializes the editor with the given input " + event);
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                String errorMessage = (caught.getMessage() != null) ? caught.getMessage() : constant.getContentFailed();
                Notification notification = new Notification(errorMessage, ERROR);
                notificationManager.showNotification(notification);
            }
        });
    }
}

