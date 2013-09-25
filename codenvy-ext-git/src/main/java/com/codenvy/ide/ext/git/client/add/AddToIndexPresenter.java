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
package com.codenvy.ide.ext.git.client.add;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.ext.git.client.GitClientService;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.js.JsoArray;
import com.codenvy.ide.resources.model.Folder;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Presenter for add changes to Git index.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 29, 2011 4:35:16 PM anya $
 */
@Singleton
public class AddToIndexPresenter implements AddToIndexView.ActionDelegate {
    private AddToIndexView          view;
    private GitClientService        service;
    private ConsolePart             console;
    private GitLocalizationConstant constant;
    private ResourceProvider        resourceProvider;
    private Project                 project;
    private SelectionAgent          selectionAgent;

    /**
     * Create presenter
     *
     * @param view
     * @param service
     * @param console
     * @param constant
     * @param resourceProvider
     * @param selectionAgent
     */
    @Inject
    public AddToIndexPresenter(AddToIndexView view, GitClientService service, ConsolePart console, GitLocalizationConstant constant,
                               ResourceProvider resourceProvider, SelectionAgent selectionAgent) {
        this.view = view;
        this.view.setDelegate(this);
        this.service = service;
        this.console = console;
        this.constant = constant;
        this.resourceProvider = resourceProvider;
        this.selectionAgent = selectionAgent;
    }

    /** Show dialog. */
    public void showDialog() {
        project = resourceProvider.getActiveProject();
        String workDir = project.getPath();
        view.setMessage(formMessage(workDir));
        view.setUpdated(false);
        view.showDialog();
    }

    /**
     * Form the message to display for adding to index, telling the user what is gonna to be added.
     *
     * @return {@link String} message to display
     */
    @NotNull
    private String formMessage(@NotNull String workdir) {
        Selection<Resource> selection = (Selection<Resource>)selectionAgent.getSelection();

        Resource element;
        if (selection == null) {
            element = project;
        } else {
            element = selection.getFirstElement();
        }

        String pattern = element.getPath().replaceFirst(workdir, "");
        pattern = (pattern.startsWith("/")) ? pattern.replaceFirst("/", "") : pattern;

        // Root of the working tree:
        if (pattern.length() == 0 || "/".equals(pattern)) {
            return constant.addToIndexAllChanges();
        }

        if (element instanceof Folder) {
            return constant.addToIndexFolder(pattern);
        } else {
            return constant.addToIndexFile(pattern);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onAddClicked() {
        boolean update = view.isUpdated();

        try {
            service.addWS(resourceProvider.getVfsId(), project, update, getFilePatterns(), new RequestCallback<String>() {
                @Override
                protected void onSuccess(String result) {
                    resourceProvider.getProject(project.getName(), new AsyncCallback<Project>() {
                        @Override
                        public void onSuccess(Project result) {
                            console.print(constant.addSuccess());
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            Log.error(AddToIndexPresenter.class, "can not get project " + project.getName());
                        }
                    });
                }

                @Override
                protected void onFailure(Throwable exception) {
                    handleError(exception);
                }
            });
            view.close();
        } catch (WebSocketException e) {
            doAddREST(update);
        }
    }

    /** Perform adding to index (sends request over HTTP). */
    private void doAddREST(boolean update) {
        try {
            service.add(resourceProvider.getVfsId(), project, update, getFilePatterns(), new AsyncRequestCallback<String>() {
                @Override
                protected void onSuccess(String result) {
                    resourceProvider.getProject(project.getName(), new AsyncCallback<Project>() {
                        @Override
                        public void onSuccess(Project result) {
                            console.print(constant.addSuccess());
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            Log.error(AddToIndexPresenter.class, "can not get project " + project.getName());
                        }
                    });
                }

                @Override
                protected void onFailure(Throwable exception) {
                    handleError(exception);
                }
            });
        } catch (RequestException e) {
            handleError(e);
        }
        view.close();
    }

    /**
     * Returns pattern of the files to be added.
     *
     * @return pattern of the files to be added
     */
    @NotNull
    private JsonArray<String> getFilePatterns() {
        String projectPath = project.getPath();

        Selection<Resource> selection = (Selection<Resource>)selectionAgent.getSelection();
        Resource element;
        if (selection == null) {
            element = project;
        } else {
            element = selection.getFirstElement();
        }

        String pattern = element.getPath().replaceFirst(projectPath, "");
        pattern = (pattern.startsWith("/")) ? pattern.replaceFirst("/", "") : pattern;

        JsoArray<String> patterns = JsoArray.create();
        if (pattern.isEmpty() || "/".equals(pattern)) {
            patterns.add(".");
        } else {
            patterns.add(pattern);
        }

        return patterns;
    }

    /**
     * Handler some action whether some exception happened.
     *
     * @param e
     *         exception what happened
     */
    private void handleError(@NotNull Throwable e) {
        String errorMessage = (e.getMessage() != null && !e.getMessage().isEmpty()) ? e.getMessage() : constant.addFailed();
        console.print(errorMessage);
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }
}