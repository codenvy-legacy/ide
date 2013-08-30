/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.ext.git.client.github.githubimport;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.user.User;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.git.client.GitClientService;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.github.GitHubClientService;
import com.codenvy.ide.ext.git.client.github.load.ProjectData;
import com.codenvy.ide.ext.git.client.marshaller.AllRepositoriesUnmarshaller;
import com.codenvy.ide.ext.git.client.marshaller.RepoInfoUnmarshaller;
import com.codenvy.ide.ext.git.client.marshaller.RepoInfoUnmarshallerWS;
import com.codenvy.ide.ext.git.client.marshaller.StringUnmarshaller;
import com.codenvy.ide.ext.git.shared.GitHubRepository;
import com.codenvy.ide.ext.git.shared.RepoInfo;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonStringMap;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.resources.model.ResourceNameValidator;
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.security.oauth.JsOAuthWindow;
import com.codenvy.ide.security.oauth.OAuthCallback;
import com.codenvy.ide.security.oauth.OAuthStatus;
import com.codenvy.ide.ui.loader.EmptyLoader;
import com.codenvy.ide.util.Utils;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.security.oauth.OAuthStatus.LOGGED_IN;
import static com.google.gwt.http.client.RequestBuilder.POST;

/**
 * Presenter for importing user's GitHub project to IDE.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ImportFromGithubPresenter.java Dec 7, 2011 3:37:11 PM vereshchaka $
 */
@Singleton
public class ImportPresenter implements ImportView.ActionDelegate, OAuthCallback {
    private ImportView                                 view;
    private GitHubClientService                        service;
    private EventBus                                   eventBus;
    private JsonStringMap<JsonArray<GitHubRepository>> repositories;
    private ProjectData                                selectedRepository;
    private String                                     restContext;
    private GitLocalizationConstant                    constant;
    private ResourceProvider                           resourceProvider;
    private ConsolePart                                console;
    private GitClientService                           gitService;


    /**
     * Create presenter.
     *
     * @param view
     * @param service
     * @param eventBus
     * @param restContext
     * @param constant
     * @param resourceProvider
     * @param console
     * @param gitService
     */
    @Inject
    public ImportPresenter(ImportView view, GitHubClientService service, EventBus eventBus, @Named("restContext") String restContext,
                           GitLocalizationConstant constant, ResourceProvider resourceProvider, ConsolePart console,
                           GitClientService gitService) {
        this.view = view;
        this.view.setDelegate(this);
        this.service = service;
        this.eventBus = eventBus;
        this.restContext = restContext;
        this.constant = constant;
        this.resourceProvider = resourceProvider;
        this.console = console;
        this.gitService = gitService;
    }

    /** Show dialog. */
    public void showDialog(User user) {
        getToken(user.getUserId());
    }

    /** Get the list of all authorized user's repositories. */
    private void getUserRepos() {
        AllRepositoriesUnmarshaller unmarshaller = new AllRepositoriesUnmarshaller();
        try {
            service.getAllRepositories(new AsyncRequestCallback<JsonStringMap<JsonArray<GitHubRepository>>>(unmarshaller) {
                @Override
                protected void onSuccess(JsonStringMap<JsonArray<GitHubRepository>> result) {
                    onListLoaded(result);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    if (exception.getMessage().contains("Bad credentials")) {
                        Window.alert("Looks like a problem with your SSH key.  Delete a GitHub key at Window > Preferences > " +
                                     "SSH Keys, and try importing your GitHub projects again.");
                    } else {
                        eventBus.fireEvent(new ExceptionThrownEvent(exception));
                        console.print(exception.getMessage());
                    }
                }
            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    /**
     * Perform actions when the list of repositories was loaded.
     *
     * @param repositories
     *         loaded list of repositories
     */
    private void onListLoaded(@NotNull JsonStringMap<JsonArray<GitHubRepository>> repositories) {
        this.repositories = repositories;

        view.setAccountNames(repositories.getKeys());
        view.setEnableFinishButton(false);

        refreshProjectList();

        view.showDialog();
    }

    /** Refresh project list on view. */
    private void refreshProjectList() {
        JsonArray<ProjectData> projectsData = JsonCollections.createArray();

        String accountName = view.getAccountName();
        JsonArray<GitHubRepository> repo = repositories.get(accountName);

        for (GitHubRepository repository : repo.asIterable()) {
            ProjectData projectData = new ProjectData(repository.getName(), repository.getDescription(), null, null, repository.getSshUrl(),
                                                      repository.getGitUrl());
            projectsData.add(projectData);
        }

        view.setRepositories(projectsData);
        view.setProjectName("");
        selectedRepository = null;
    }

    /**
     * Return token for user.
     *
     * @param user
     *         user which need token
     */
    private void getToken(final String user) {
        StringUnmarshaller unmarshaller = new StringUnmarshaller();
        try {
            service.getUserToken(user, new AsyncRequestCallback<String>(unmarshaller) {
                @Override
                protected void onSuccess(String result) {
                    if (result == null || result.isEmpty()) {
                        oAuthLoginStart(user);
                    } else {
                        getUserRepos();
                    }
                }

                @Override
                protected void onFailure(Throwable exception) {
                    oAuthLoginStart(user);
                }
            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    /** Log in  github */
    private void oAuthLoginStart(@NotNull String user) {
        boolean permitToRedirect = Window.confirm(constant.loginOAuthLabel());
        if (permitToRedirect) {
            String authUrl = "rest/ide/oauth/authenticate?oauth_provider=github"
                             + "&scope=user&userId=" + user + "&scope=repo&redirect_after_login=/ide/" + Utils.getWorkspaceName();
            JsOAuthWindow authWindow = new JsOAuthWindow(authUrl, "error.url", 500, 980, this);
            authWindow.loginWithOAuth();
        }
    }

    /** Generate github key. */
    public void generateGitHubKey() {
        try {
            AsyncRequestCallback<Void> callback = new AsyncRequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    getUserRepos();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    Log.error(ImportPresenter.class, "Can't generate ssh key", exception);
                }
            };

            String url = restContext + '/' + Utils.getWorkspaceName() + "/github/ssh/generate";
            AsyncRequest.build(POST, url).loader(new EmptyLoader()).send(callback);
        } catch (RequestException e) {
            Window.alert("Upload key to github failed.");
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onFinishClicked() {
        final String projectName = view.getProjectName();
        boolean hasProjectNameIncorrectSymbol = !ResourceNameValidator.isProjectNameValid(projectName) || projectName.isEmpty();
        if (selectedRepository != null && !projectName.isEmpty()) {
            if (hasProjectNameIncorrectSymbol) {
                Window.alert(constant.noIncorrectProjectNameMessage());
            } else {
                String remoteUri = selectedRepository.getRepositoryUrl();
                if (!remoteUri.endsWith(".git")) {
                    remoteUri += ".git";
                }

                final String finalRemoteUri = remoteUri;
                resourceProvider.createProject(projectName, JsonCollections.<Property>createArray(), new AsyncCallback<Project>() {
                    @Override
                    public void onSuccess(Project result) {
                        cloneRepository(finalRemoteUri, projectName, result);
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        String errorMessage =
                                (caught.getMessage() != null && caught.getMessage().length() > 0) ? caught.getMessage()
                                                                                                  : constant.cloneFailed(finalRemoteUri);
                        console.print(errorMessage);
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
        RepoInfoUnmarshallerWS unmarshallerWS = new RepoInfoUnmarshallerWS();
        try {
            gitService.cloneRepositoryWS(resourceProvider.getVfsId(), project, remoteUri, remoteName,
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
            gitService.cloneRepository(resourceProvider.getVfsId(), project, remoteUri, remoteName,
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
                Log.error(ImportPresenter.class, "Can not get project " + project.getName());
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

    /** {@inheritDoc} */
    @Override
    public void onAuthenticated(OAuthStatus authStatus) {
        if (LOGGED_IN.equals(authStatus)) {
            generateGitHubKey();
        }
    }
}