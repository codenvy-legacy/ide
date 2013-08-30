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
package com.codenvy.ide.ext.git.client.clone;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.git.client.GitClientService;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.marshaller.RepoInfoUnmarshaller;
import com.codenvy.ide.ext.git.client.marshaller.RepoInfoUnmarshallerWS;
import com.codenvy.ide.ext.git.shared.RepoInfo;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * The presenter for Clone Repository from github.com.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 22, 2011 4:31:12 PM anya $
 */
@Singleton
public class CloneRepositoryPresenter implements CloneRepositoryView.ActionDelegate {
    private static final String DEFAULT_REPO_NAME = "origin";
    private CloneRepositoryView     view;
    private GitClientService        service;
    private ResourceProvider        resourceProvider;
    private EventBus                eventBus;
    private GitLocalizationConstant constant;
    private ConsolePart             console;

    @Inject
    public CloneRepositoryPresenter(CloneRepositoryView view, GitClientService service, ResourceProvider resourceProvider,
                                    EventBus eventBus, GitLocalizationConstant constant, ConsolePart console) {
        this.view = view;
        this.view.setDelegate(this);
        this.service = service;
        this.resourceProvider = resourceProvider;
        this.eventBus = eventBus;
        this.constant = constant;
        this.console = console;
    }

    /** {@inheritDoc} */
    @Override
    public void onCloneClicked() {
        String projectName = view.getProjectName();
        final String remoteName = view.getRemoteName();
        final String remoteUri = view.getRemoteUri();

        resourceProvider.createProject(projectName, JsonCollections.<Property>createArray(), new AsyncCallback<Project>() {
            @Override
            public void onSuccess(Project result) {
                cloneRepository(remoteUri, remoteName, result);
            }

            @Override
            public void onFailure(Throwable caught) {
                String errorMessage = (caught.getMessage() != null && caught.getMessage().length() > 0) ? caught.getMessage()
                                                                                                        : constant.cloneFailed(remoteUri);
                console.print(errorMessage);
            }
        });
    }

    /**
     * Get the necessary parameters values and clone repository (over WebSocket or HTTP).
     *
     * @param remoteUri
     *         the location of the remote repository
     * @param remoteName
     *         remote name instead of "origin"
     * @param project
     *         folder (root of GIT repository)
     */
    private void cloneRepository(@NotNull final String remoteUri, @NotNull String remoteName, @NotNull final Project project) {
        RepoInfoUnmarshallerWS unmarshallerWS = new RepoInfoUnmarshallerWS();
        try {
            service.cloneRepositoryWS(resourceProvider.getVfsId(), project, remoteUri, remoteName,
                                      new RequestCallback<RepoInfo>(unmarshallerWS) {
                                          @Override
                                          protected void onSuccess(RepoInfo result) {
                                              onCloneSuccess(result, project);
                                          }

                                          @Override
                                          protected void onFailure(Throwable exception) {
                                              deleteFolder(project);
                                              handleError(exception, remoteUri);

                                          }
                                      });
            view.close();
        } catch (WebSocketException e) {
            cloneRepositoryREST(remoteUri, remoteName, project);
        }
    }

    /**
     * Get the necessary parameters values and call the clone repository method (over HTTP).
     *
     * @param remoteUri
     *         the location of the remote repository
     * @param remoteName
     *         remote name instead of "origin"
     * @param project
     *         folder (root of GIT repository)
     */
    private void cloneRepositoryREST(@NotNull final String remoteUri, @NotNull String remoteName, @NotNull final Project project) {
        RepoInfoUnmarshaller unmarshaller = new RepoInfoUnmarshaller();
        try {
            service.cloneRepository(resourceProvider.getVfsId(), project, remoteUri, remoteName,
                                    new AsyncRequestCallback<RepoInfo>(unmarshaller) {
                                        @Override
                                        protected void onSuccess(RepoInfo result) {
                                            onCloneSuccess(result, project);
                                        }

                                        @Override
                                        protected void onFailure(Throwable exception) {
                                            deleteFolder(project);
                                            handleError(exception, remoteUri);
                                        }
                                    });
        } catch (RequestException e) {
            handleError(e, remoteUri);
        }
        view.close();
    }

    /**
     * Perform actions when repository was successfully cloned.
     *
     * @param project
     *         {@link Project} to clone
     */
    private void onCloneSuccess(@NotNull final RepoInfo gitRepositoryInfo, @NotNull final Project project) {
        resourceProvider.getProject(project.getName(), new AsyncCallback<Project>() {
            @Override
            public void onSuccess(Project result) {
                console.print(constant.cloneSuccess(gitRepositoryInfo.getRemoteUri()));
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(CloneRepositoryPresenter.class, "can not get project " + project.getName());
            }
        });
    }

    /**
     * Delete project.
     *
     * @param path
     *         the path where project exist
     */
    private void deleteFolder(@NotNull Project path) {
        resourceProvider.delete(path, new AsyncCallback<String>() {
            @Override
            public void onSuccess(String result) {
                // do nothing
            }

            @Override
            public void onFailure(Throwable caught) {
                eventBus.fireEvent(new ExceptionThrownEvent(caught, "Exception during project removing"));
            }
        });
    }

    /**
     * Handler some action whether some exception happened.
     *
     * @param e
     *         exception what happened
     * @param remoteUri
     *         rempote uri
     */
    private void handleError(@NotNull Throwable e, @NotNull String remoteUri) {
        String errorMessage =
                (e.getMessage() != null && e.getMessage().length() > 0) ? e.getMessage() : constant.cloneFailed(remoteUri);
        console.print(errorMessage);
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onValueChanged() {
        String remoteUrl = view.getRemoteUri();
        boolean enable = !remoteUrl.isEmpty();
        if (remoteUrl.endsWith("/")) {
            remoteUrl = remoteUrl.substring(0, remoteUrl.length() - 1);
        }
        if (remoteUrl.endsWith(".git")) {
            remoteUrl = remoteUrl.substring(0, remoteUrl.length() - 4);
            String[] split = remoteUrl.split("/");
            view.setProjectName(split[split.length - 1]);
        }
        view.setEnableCloneButton(enable);
    }

    /** Show dialog. */
    public void showDialog() {
        view.setProjectName("");
        view.setRemoteUri("");
        view.setRemoteName(DEFAULT_REPO_NAME);
        view.focusInRemoteUrlField();
        view.setEnableCloneButton(false);
        view.showDialog();
    }
}