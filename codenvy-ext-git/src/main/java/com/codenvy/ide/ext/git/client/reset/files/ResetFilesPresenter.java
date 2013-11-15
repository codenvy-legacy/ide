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
package com.codenvy.ide.ext.git.client.reset.files;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ext.git.client.GitClientService;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.shared.IndexFile;
import com.codenvy.ide.ext.git.shared.Status;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.StringUnmarshaller;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;

/**
 * Presenter for reseting files from index.
 * <p/>
 * When user tries to reset files from index:
 * 1. Find Git work directory by selected item in browser tree.
 * 2. Get status for found work directory.
 * 3. Display files ready for commit in grid. (Checked items will be reseted from index).
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 13, 2011 4:52:42 PM anya $
 */
@Singleton
public class ResetFilesPresenter implements ResetFilesView.ActionDelegate {
    private ResetFilesView          view;
    private GitClientService        service;
    private ResourceProvider        resourceProvider;
    private GitLocalizationConstant constant;
    private NotificationManager     notificationManager;
    private Project                 project;
    private JsonArray<IndexFile>    indexedFiles;
    private DtoFactory              dtoFactory;

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
    public ResetFilesPresenter(ResetFilesView view, GitClientService service, ResourceProvider resourceProvider,
                               GitLocalizationConstant constant, NotificationManager notificationManager, DtoFactory dtoFactory) {
        this.view = view;
        this.view.setDelegate(this);
        this.service = service;
        this.resourceProvider = resourceProvider;
        this.constant = constant;
        this.notificationManager = notificationManager;
        this.dtoFactory = dtoFactory;
    }

    /** Show dialog. */
    public void showDialog() {
        project = resourceProvider.getActiveProject();

        try {
            service.status(resourceProvider.getVfsId(), project.getId(), new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                @Override
                protected void onSuccess(String result) {
                    Status status = dtoFactory.createDtoFromJson(result, Status.class);
                    if (status.isClean()) {
                        Window.alert(constant.indexIsEmpty());
                        return;
                    }

                    JsonArray<IndexFile> values = JsonCollections.createArray();
                    ArrayList<String> valuesTmp = new ArrayList<String>();

                    valuesTmp.addAll(status.getAdded());
                    valuesTmp.addAll(status.getChanged());
                    valuesTmp.addAll(status.getRemoved());

                    for (String value: valuesTmp) {
                        IndexFile indexFile = dtoFactory.createDto(IndexFile.class).withPath(value).withIndexed(true);
                        values.add(indexFile);
                    }
                    view.setIndexedFiles(values);
                    indexedFiles = values;
                    view.showDialog();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    String errorMassage = exception.getMessage() != null ? exception.getMessage() : constant.statusFailed();
                    Notification notification = new Notification(errorMassage, ERROR);
                    notificationManager.showNotification(notification);
                }
            });
        } catch (RequestException e) {
            String errorMassage = e.getMessage() != null ? e.getMessage() : constant.statusFailed();
            Notification notification = new Notification(errorMassage, ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onResetClicked() {
        JsonArray<String> files = JsonCollections.createArray();
        for (int i = 0; i < indexedFiles.size(); i++) {
            IndexFile indexFile = indexedFiles.get(i);
            if (!indexFile.indexed()) {
                files.add(indexFile.getPath());
            }
        }

        if (files.isEmpty()) {
            view.close();
            Notification notification = new Notification(constant.nothingToReset(), INFO);
            notificationManager.showNotification(notification);
            return;
        }

        String projectId = project.getId();

        try {
            service.reset(resourceProvider.getVfsId(), projectId, "HEAD", null, new AsyncRequestCallback<String>() {
                @Override
                protected void onSuccess(String result) {
                    resourceProvider.getProject(project.getName(), new AsyncCallback<Project>() {
                        @Override
                        public void onSuccess(Project result) {
                            view.close();
                            Notification notification = new Notification(constant.resetFilesSuccessfully(), INFO);
                            notificationManager.showNotification(notification);
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            Log.error(ResetFilesPresenter.class, "can not get project " + project.getName());
                        }
                    });
                }

                @Override
                protected void onFailure(Throwable exception) {
                    String errorMassage = exception.getMessage() != null ? exception.getMessage() : constant.resetFilesFailed();
                    Notification notification = new Notification(errorMassage, ERROR);
                    notificationManager.showNotification(notification);
                }
            });
        } catch (RequestException e) {
            String errorMassage = e.getMessage() != null ? e.getMessage() : constant.resetFilesFailed();
            Notification notification = new Notification(errorMassage, ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }
}