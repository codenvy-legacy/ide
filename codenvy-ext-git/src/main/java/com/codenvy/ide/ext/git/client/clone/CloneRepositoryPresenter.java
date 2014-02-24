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

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ext.git.client.GitClientService;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.shared.RepoInfo;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.codenvy.ide.websocket.rest.StringUnmarshallerWS;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

import static com.codenvy.ide.api.notification.Notification.Status.FINISHED;
import static com.codenvy.ide.api.notification.Notification.Status.PROGRESS;
import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * The presenter for Clone Repository from github.com.
 *
 * @author Ann Zhuleva
 */
@Singleton
public class CloneRepositoryPresenter implements CloneRepositoryView.ActionDelegate {
    public static final String DEFAULT_REPO_NAME = "origin";
    private final DtoUnmarshallerFactory  dtoUnmarshallerFactory;
    private       CloneRepositoryView     view;
    private       GitClientService        service;
    private       ResourceProvider        resourceProvider;
    private       GitLocalizationConstant constant;
    private       NotificationManager     notificationManager;
    private       Notification            notification;

    @Inject
    public CloneRepositoryPresenter(CloneRepositoryView view, GitClientService service, ResourceProvider resourceProvider,
                                    GitLocalizationConstant constant, NotificationManager notificationManager,
                                    DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        this.view = view;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.view.setDelegate(this);
        this.service = service;
        this.resourceProvider = resourceProvider;
        this.constant = constant;
        this.notificationManager = notificationManager;
    }

    /** {@inheritDoc} */
    @Override
    public void onCloneClicked() {
        String projectName = view.getProjectName();
        final String remoteName = view.getRemoteName();
        final String remoteUri = view.getRemoteUri();
        notification = new Notification(constant.cloneStarted(projectName, remoteName), PROGRESS);
        notificationManager.showNotification(notification);

        resourceProvider.createProject(projectName, Collections.<Property>createArray(), new AsyncCallback<Project>() {
            @Override
            public void onSuccess(Project result) {
                cloneRepository(remoteUri, remoteName, result);
            }

            @Override
            public void onFailure(Throwable caught) {
                String errorMessage = (caught.getMessage() != null && caught.getMessage().length() > 0) ? caught.getMessage()
                                                                                                        : constant.cloneFailed(remoteUri);
                notification.setStatus(FINISHED);
                notification.setType(ERROR);
                notification.setMessage(errorMessage);
            }
        });
    }

    /**
     * Get the necessary parameters values and clone repository.
     *
     * @param remoteUri
     *         the location of the remote repository
     * @param remoteName
     *         remote name instead of "origin"
     * @param project
     *         folder (root of GIT repository)
     */
    private void cloneRepository(@NotNull final String remoteUri, @NotNull String remoteName, @NotNull final Project project) {
        try {
            service.cloneRepositoryWS(resourceProvider.getVfsInfo().getId(), project, remoteUri, remoteName,
                                      new RequestCallback<RepoInfo>(dtoUnmarshallerFactory.newWSUnmarshaller(RepoInfo.class)) {
                                          @Override
                                          protected void onSuccess(RepoInfo result) {
                                              onCloneSuccess(result, project);
                                          }

                                          @Override
                                          protected void onFailure(Throwable exception) {
                                              resourceProvider.showListProjects();
                                              handleError(exception, remoteUri);
                                          }
                                      });
        } catch (WebSocketException e) {
            resourceProvider.showListProjects();
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
                notification.setStatus(FINISHED);
                notification.setMessage(constant.cloneSuccess(gitRepositoryInfo.getRemoteUri()));
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(CloneRepositoryPresenter.class, "can not get project " + project.getName());
            }
        });
    }

    /**
     * Handler some action whether some exception happened.
     *
     * @param e
     *         exception what happened
     * @param remoteUri
     *         remote uri
     */
    private void handleError(@NotNull Throwable e, @NotNull String remoteUri) {
        String errorMessage =
                (e.getMessage() != null && e.getMessage().length() > 0) ? e.getMessage() : constant.cloneFailed(remoteUri);
        notification.setStatus(FINISHED);
        notification.setType(ERROR);
        notification.setMessage(errorMessage);
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
        view.setEnableCloneButton(false);
        view.showDialog();
        view.focusInRemoteUrlField();
    }
}