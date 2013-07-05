/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.ext.git.client.clone;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.ext.git.client.GitClientService;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.marshaller.RepoInfoUnmarshaller;
import com.codenvy.ide.ext.git.client.marshaller.RepoInfoUnmarshallerWS;
import com.codenvy.ide.ext.git.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.git.shared.RepoInfo;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.rest.AsyncRequestCallback;
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
    private void cloneRepository(final String remoteUri, String remoteName, final Project project) {
        DtoClientImpls.RepoInfoImpl repoInfo = DtoClientImpls.RepoInfoImpl.make();
        RepoInfoUnmarshallerWS unmarshallerWS = new RepoInfoUnmarshallerWS(repoInfo);
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
    private void cloneRepositoryREST(final String remoteUri, String remoteName, final Project project) {
        DtoClientImpls.RepoInfoImpl repoInfo = DtoClientImpls.RepoInfoImpl.make();
        RepoInfoUnmarshaller unmarshaller = new RepoInfoUnmarshaller(repoInfo);
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
    private void onCloneSuccess(final RepoInfo gitRepositoryInfo, final Project project) {
        console.print(constant.cloneSuccess(gitRepositoryInfo.getRemoteUri()));
        // TODO
        // IDE.fireEvent(new ConvertToProjectEvent(folder.getId(), vfs.getId(), null));

        // TODO
//        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
//            @Override
//            public void execute() {
//                String[] userRepo = GitURLParser.parseGitHubUrl(gitRepositoryInfo.getRemoteUri());
//                if (userRepo != null) {
//                    IDE.fireEvent(new CloneRepositoryCompleteEvent(userRepo[0], userRepo[1]));
//                }
//            }
//        });
    }

    /**
     * Delete project.
     *
     * @param path
     *         the path where project exist
     */
    private void deleteFolder(Project path) {
        // TODO need to add support of removing project
//        try {
//            VirtualFileSystem.getInstance().delete(path,
//                                                   new AsyncRequestCallback<String>() {
//                                                       @Override
//                                                       protected void onSuccess(String result) {
//                                                           // Do nothing
//                                                       }
//
//                                                       @Override
//                                                       protected void onFailure(Throwable exception) {
//                                                           IDE.fireEvent(new ExceptionThrownEvent(exception,
//                                                                                                  "Exception during folder removing"));
//                                                       }
//                                                   });
//        } catch (RequestException e) {
//            IDE.fireEvent(new ExceptionThrownEvent(e, "Exception during removing of directory project"));
//        }
    }

    /**
     * Handler some action whether some exception happened.
     *
     * @param e
     *         exception what happened
     * @param remoteUri
     *         rempote uri
     */
    private void handleError(Throwable e, String remoteUri) {
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