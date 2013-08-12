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

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.ext.git.client.GitClientService;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.marshaller.StatusUnmarshaller;
import com.codenvy.ide.ext.git.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.git.shared.IndexFile;
import com.codenvy.ide.ext.git.shared.Status;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

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
    private ConsolePart             console;
    private Project                 project;
    private JsonArray<IndexFile>    indexedFiles;

    /**
     * Create presenter.
     *
     * @param view
     * @param service
     * @param resourceProvider
     * @param constant
     * @param console
     */
    @Inject
    public ResetFilesPresenter(ResetFilesView view, GitClientService service, ResourceProvider resourceProvider,
                               GitLocalizationConstant constant, ConsolePart console) {
        this.view = view;
        this.view.setDelegate(this);
        this.service = service;
        this.resourceProvider = resourceProvider;
        this.constant = constant;
        this.console = console;
    }

    /** Show dialog. */
    public void showDialog() {
        project = resourceProvider.getActiveProject();
        DtoClientImpls.StatusImpl status = DtoClientImpls.StatusImpl.make();
        StatusUnmarshaller unmarshaller = new StatusUnmarshaller(status);

        try {
            service.status(resourceProvider.getVfsId(), project.getId(), new AsyncRequestCallback<Status>(unmarshaller) {
                @Override
                protected void onSuccess(Status result) {
                    if (result.clean()) {
                        Window.alert(constant.indexIsEmpty());
                        return;
                    }

                    JsonArray<IndexFile> values = JsonCollections.createArray();
                    JsonArray<String> valuesTmp = JsonCollections.createArray();

                    valuesTmp.addAll(result.getAdded());
                    valuesTmp.addAll(result.getChanged());
                    valuesTmp.addAll(result.getRemoved());

                    for (int i = 0; i < valuesTmp.size(); i++) {
                        String value = valuesTmp.get(i);

                        DtoClientImpls.IndexFileImpl indexFile = DtoClientImpls.IndexFileImpl.make();
                        indexFile.setPath(value);
                        indexFile.setIndexed(true);

                        values.add(indexFile);
                    }
                    view.setIndexedFiles(values);
                    indexedFiles = values;
                    view.showDialog();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    String errorMassage = exception.getMessage() != null ? exception.getMessage() : constant.statusFailed();
                    console.print(errorMassage);
                }
            });
        } catch (RequestException e) {
            String errorMassage = e.getMessage() != null ? e.getMessage() : constant.statusFailed();
            console.print(errorMassage);
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
            console.print(constant.nothingToReset());
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
                            console.print(constant.resetFilesSuccessfully());
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
                    console.print(errorMassage);
                }
            });
        } catch (RequestException e) {
            String errorMassage = e.getMessage() != null ? e.getMessage() : constant.resetFilesFailed();
            console.print(errorMassage);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }
}