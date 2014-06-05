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
package com.codenvy.ide.ext.github.client.load;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.user.shared.dto.User;
import com.codenvy.ide.Constants;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ext.git.client.GitServiceClient;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.shared.RepoInfo;
import com.codenvy.ide.ext.github.client.GitHubClientService;
import com.codenvy.ide.ext.github.client.GitHubSshKeyProvider;
import com.codenvy.ide.ext.github.client.marshaller.AllRepositoriesUnmarshaller;
import com.codenvy.ide.ext.github.shared.GitHubRepository;
import com.codenvy.ide.projecttype.SelectProjectTypePresenter;
import com.codenvy.ide.api.resources.ProjectTypeDescriptorRegistry;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.resources.model.ResourceNameValidator;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import javax.validation.constraints.NotNull;

import static com.codenvy.ide.api.notification.Notification.Status.FINISHED;
import static com.codenvy.ide.api.notification.Notification.Status.PROGRESS;
import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * Presenter for importing user's GitHub project to IDE.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 */
@Singleton
public class ImportPresenter implements ImportView.ActionDelegate {
    private final DtoFactory                         dtoFactory;
    private final DtoUnmarshallerFactory             dtoUnmarshallerFactory;
    private final ProjectServiceClient               projectServiceClient;
    private final SelectProjectTypePresenter         selectProjectTypePresenter;
    private       ImportView                         view;
    private       GitHubClientService                service;
    private       GitServiceClient                   gitService;
    private       EventBus                           eventBus;
    private       StringMap<Array<GitHubRepository>> repositories;
    private       ProjectData                        selectedRepository;
    private       GitLocalizationConstant            gitConstant;
    private       ResourceProvider                   resourceProvider;
    private       NotificationManager                notificationManager;
    private       Notification                       notification;
    private       GitHubSshKeyProvider               gitHubSshKeyProvider;
    private       ProjectTypeDescriptorRegistry      projectTypeDescriptorRegistry;

    /**
     * Create presenter.
     *
     * @param view
     * @param service
     * @param gitService
     * @param eventBus
     * @param gitConstant
     * @param resourceProvider
     * @param notificationManager
     * @param gitHubSshKeyProvider
     * @param dtoFactory
     * @param dtoUnmarshallerFactory
     * @param projectTypeDescriptorRegistry
     * @param projectServiceClient
     * @param selectProjectTypePresenter
     */
    @Inject
    public ImportPresenter(ImportView view,
                           GitHubClientService service,
                           GitServiceClient gitService,
                           EventBus eventBus,
                           GitLocalizationConstant gitConstant,
                           ResourceProvider resourceProvider,
                           NotificationManager notificationManager,
                           GitHubSshKeyProvider gitHubSshKeyProvider,
                           DtoFactory dtoFactory,
                           DtoUnmarshallerFactory dtoUnmarshallerFactory,
                           ProjectTypeDescriptorRegistry projectTypeDescriptorRegistry,
                           ProjectServiceClient projectServiceClient,
                           SelectProjectTypePresenter selectProjectTypePresenter) {
        this.view = view;
        this.dtoFactory = dtoFactory;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.projectTypeDescriptorRegistry = projectTypeDescriptorRegistry;
        this.projectServiceClient = projectServiceClient;
        this.selectProjectTypePresenter = selectProjectTypePresenter;
        this.view.setDelegate(this);
        this.service = service;
        this.gitService = gitService;
        this.eventBus = eventBus;
        this.gitConstant = gitConstant;
        this.resourceProvider = resourceProvider;
        this.notificationManager = notificationManager;
        this.gitHubSshKeyProvider = gitHubSshKeyProvider;
    }

    /** Show dialog. */
    public void showDialog(User user) {
        AsyncCallback<Void> callback = new AsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                getUserRepos();
            }

            @Override
            public void onFailure(Throwable exception) {
                notificationManager.showNotification(new Notification(exception.getMessage(), Notification.Type.ERROR));
                Log.error(ImportPresenter.class, "Can't generate ssh key", exception);
            }
        };
        gitHubSshKeyProvider.generateKey(user.getId(), callback);
    }

    /** Get the list of all authorized user's repositories. */
    private void getUserRepos() {
        service.getAllRepositories(
                new AsyncRequestCallback<StringMap<Array<GitHubRepository>>>(new AllRepositoriesUnmarshaller(dtoFactory)) {
                    @Override
                    protected void onSuccess(StringMap<Array<GitHubRepository>> result) {
                        onListLoaded(result);
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        if (exception.getMessage().contains("Bad credentials")) {
                            Window.alert("Looks like a problem with your SSH key.  Delete a GitHub key at Window > Preferences > " +
                                         "SSH Keys, and try importing your GitHub projects again.");
                        } else {
                            eventBus.fireEvent(new ExceptionThrownEvent(exception));
                            Notification notification = new Notification(exception.getMessage(), ERROR);
                            notificationManager.showNotification(notification);
                        }
                    }
                });
    }

    /**
     * Perform actions when the list of repositories was loaded.
     *
     * @param repositories
     *         loaded list of repositories
     */
    private void onListLoaded(@NotNull StringMap<Array<GitHubRepository>> repositories) {
        this.repositories = repositories;

        view.setAccountNames(repositories.getKeys());
        view.setEnableFinishButton(false);

        refreshProjectList();

        view.showDialog();
    }

    /** Refresh project list on view. */
    private void refreshProjectList() {
        Array<ProjectData> projectsData = Collections.createArray();

        String accountName = view.getAccountName();
        Array<GitHubRepository> repo = repositories.get(accountName);

        for (GitHubRepository repository : repo.asIterable()) {
            ProjectData projectData = new ProjectData(repository.getName(), repository.getDescription(), null, null, repository.getSshUrl(),
                                                      repository.getGitUrl());
            projectsData.add(projectData);
        }

        view.setRepositories(projectsData);
        view.setProjectName("");
        selectedRepository = null;
    }

    /** Return token for user. */
    @Override
    public void onFinishClicked() {
        final String projectName = view.getProjectName();
        boolean hasProjectNameIncorrectSymbol = !ResourceNameValidator.isProjectNameValid(projectName) || projectName.isEmpty();
        if (selectedRepository != null) {
            if (hasProjectNameIncorrectSymbol) {
                Window.alert(gitConstant.noIncorrectProjectNameMessage());
            } else {
                String remoteUri = selectedRepository.getRepositoryUrl();
                if (!remoteUri.endsWith(".git")) {
                    remoteUri += ".git";
                }

                notification = new Notification(gitConstant.cloneStarted(projectName, remoteUri), PROGRESS);
                notificationManager.showNotification(notification);

                final String finalRemoteUri = remoteUri;
                projectServiceClient.createFolder(projectName, new AsyncRequestCallback<Void>() {
                    @Override
                    protected void onSuccess(Void result) {
                        Project project = new Project(null, null, null, null);
                        project.setName(projectName);
                        cloneRepository(finalRemoteUri, projectName, project);
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        String errorMessage = (exception.getMessage() != null && exception.getMessage().length() > 0)
                                              ? exception.getMessage() : gitConstant.cloneFailed(finalRemoteUri);
                        notification.setStatus(FINISHED);
                        notification.setType(ERROR);
                        notification.setMessage(errorMessage);
                    }
                });
            }
        }
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
        try {
            gitService.cloneRepository(project, remoteUri, remoteName,
                                       new RequestCallback<RepoInfo>(dtoUnmarshallerFactory.newWSUnmarshaller(RepoInfo.class)) {
                                           @Override
                                           protected void onSuccess(RepoInfo result) {
                                               onCloneSuccess(result, project);
                                           }

                                           @Override
                                           protected void onFailure(Throwable exception) {
                                               deleteFolder(project);
                                               handleError(exception, remoteUri);
                                           }
                                       }
                                      );
        } catch (WebSocketException e) {
            deleteFolder(project);
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
                notification.setMessage(gitConstant.cloneSuccess(gitRepositoryInfo.getRemoteUri()));
                if (result.getDescription().getProjectTypeId().equals(Constants.NAMELESS_ID)) {
                    selectProjectTypePresenter.showDialog(result, new AsyncCallback<Project>() {
                        @Override
                        public void onFailure(Throwable caught) {

                        }

                        @Override
                        public void onSuccess(Project result) {

                        }
                    });
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                selectProjectTypePresenter.showDialog(project, new AsyncCallback<Project>() {
                    @Override
                    public void onSuccess(Project result) {
                        onCloneSuccess(gitRepositoryInfo, project);
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        Log.error(ImportPresenter.class, "can not set type for project " + project.getName());
                    }
                });
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
                (e.getMessage() != null && e.getMessage().length() > 0) ? e.getMessage() : gitConstant.cloneFailed(remoteUri);
        notification.setStatus(FINISHED);
        notification.setType(ERROR);
        notification.setMessage(errorMessage);
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

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onRepositorySelected(@NotNull ProjectData repository) {
        selectedRepository = repository;
        view.setProjectName(selectedRepository.getName());
        view.setEnableFinishButton(true);
    }

    /** {@inheritDoc} */
    @Override
    public void onAccountChanged() {
        refreshProjectList();
    }


}