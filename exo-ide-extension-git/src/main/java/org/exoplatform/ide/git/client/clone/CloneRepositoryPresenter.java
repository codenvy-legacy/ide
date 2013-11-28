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
package org.exoplatform.ide.git.client.clone;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.project.ConvertToProjectEvent;
import org.exoplatform.ide.client.framework.ui.JsPopUpOAuthWindow;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.rest.RequestCallback;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.git.client.marshaller.GitUrlInfoUnmarshaller;
import org.exoplatform.ide.git.client.marshaller.RepoInfoUnmarshaller;
import org.exoplatform.ide.git.client.marshaller.RepoInfoUnmarshallerWS;
import org.exoplatform.ide.git.client.ssh.SSHKeyProcessor;
import org.exoplatform.ide.git.client.ssh.SSHKeyProcessorEvent;
import org.exoplatform.ide.git.shared.GitUrlVendorInfo;
import org.exoplatform.ide.git.shared.RepoInfo;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.FolderUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FolderModel;

/**
 * Presenter for Clone Repository View.
 * <p/>
 * NOTE @vlzhukovskii I think we should create some common class/method to validate input project name value globally in IDE
 * and define in one place rules for project name.
 */
public class CloneRepositoryPresenter extends GitPresenter implements CloneRepositoryHandler {
    public interface Display extends IsView {
        /**
         * Returns project name field.
         * <p/>
         * TODO: see note in class header.
         *
         * @return project name value
         */
        HasValue<String> getProjectNameValue();

        /**
         * Returns remote URI field.
         *
         * @return url for remote repository,
         */
        HasValue<String> getRemoteUriValue();

        /**
         * Returns remote name field.
         *
         * @return name for remote, by default "origin"
         */
        HasValue<String> getRemoteNameValue();

        /**
         * Returns clone repository button.
         *
         * @return button for starting clone
         */
        HasClickHandlers getCloneButton();

        /**
         * Returns cancel button.
         *
         * @return button for cancel cloning
         */
        HasClickHandlers getCancelButton();

        /**
         * Changes the state of clone button.
         *
         * @param enable
         *         state for cloning button value based on input project name
         */
        void enableCloneButton(boolean enable);

        /**
         * Set cursor focus in repository url input.
         */
        void focusInRemoteUrlField();
    }

    /** Presenter's display. */
    private Display display;

    /**
     * Create presenter
     */
    public CloneRepositoryPresenter() {
        IDE.addHandler(CloneRepositoryEvent.TYPE, this);
    }

    /**
     * Bind actions for control elements.
     * @param d {@link org.exoplatform.ide.git.client.clone.CloneRepositoryPresenter.Display}
     */
    public void bindDisplay(Display d) {
        this.display = d;

        display.getCancelButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getCloneButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (!display.getProjectNameValue().getValue().matches("(^[-.a-zA-Z0-9])([-._a-zA-Z0-9])*$")) {
                    if (display.getProjectNameValue().getValue().startsWith("_")) {
                        Dialogs.getInstance()
                               .showInfo(GitExtension.MESSAGES.noIncorrectProjectNameTitle(),
                                         GitExtension.MESSAGES.projectNameStartWith_Message());
                    } else {
                        Dialogs.getInstance()
                               .showInfo(GitExtension.MESSAGES.noIncorrectProjectNameTitle(),
                                         GitExtension.MESSAGES.noIncorrectProjectNameMessage());
                    }
                } else {
                    doClone(display.getRemoteUriValue().getValue(),
                            display.getRemoteNameValue().getValue(),
                            display.getProjectNameValue().getValue());
                }
            }
        });

        display.getRemoteUriValue().addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                if (event.getValue() != null) {
                    display.enableCloneButton(true);
                    String remoteUrl = event.getValue();
                    if (remoteUrl.endsWith("/")) {
                        remoteUrl = remoteUrl.substring(0, remoteUrl.length() - 1);
                    }
                    if (remoteUrl.endsWith(".git")) {
                        remoteUrl = remoteUrl.substring(0, remoteUrl.length() - 4);
                        String[] split = remoteUrl.split("/");
                        display.getProjectNameValue().setValue(split[split.length - 1]);
                    }
                } else {
                    display.enableCloneButton(false);
                }
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onCloneRepository(CloneRepositoryEvent event) {
        Display d = GWT.create(Display.class);
        IDE.getInstance().openView(d.asView());
        bindDisplay(d);
        display.focusInRemoteUrlField();
        display.getRemoteNameValue().setValue("origin");
        display.enableCloneButton(false);
    }

    /**
     * Going to cloning repository. Clone process flow 3 steps: - create new folder with name workDir - clone repository to this folder -
     * convert folder to project. This need because by default project with out file and folder not empty. It content ".project" item.
     * Clone
     * is impossible to not empty folder
     *
     * @param remoteUri
     *         - git url
     * @param remoteName
     *         - remote name (by default origin)
     * @param workDir
     *         - name of target folder
     */
    public void doClone(final String remoteUri, final String remoteName, final String workDir) {
        FolderModel folder = new FolderModel();
        folder.setName(workDir);
        try {
            VirtualFileSystem
                    .getInstance()
                    .createFolder(vfs.getRoot(),
                                  new AsyncRequestCallback<FolderModel>(new FolderUnmarshaller(folder)) {
                                      @Override
                                      protected void onSuccess(final FolderModel result) {
                                          IDE.fireEvent(new SSHKeyProcessorEvent(remoteUri, false,
                                                                                 new SSHKeyProcessor.Callback() {
                                                                                     @Override
                                                                                     public void onSuccess() {
                                                                                         cloneRepository(remoteUri, remoteName, result);
                                                                                     }
                                                                                 }));
                                      }

                                      @Override
                                      protected void onFailure(Throwable e) {
                                          GitExtension.handleError(e, remoteUri);
                                      }
                                  });
        } catch (RequestException e) {
            GitExtension.handleError(e, remoteUri);
        }
    }

    /**
     * Get the necessary parameters values and clone repository (over WebSocket or HTTP).
     *
     * @param remoteUri
     *         the location of the remote repository
     * @param remoteName
     *         remote name instead of "origin"
     * @param folder
     *         folder (root of GIT repository)
     */
    private void cloneRepository(final String remoteUri, final String remoteName, final FolderModel folder) {
        final JsPopUpOAuthWindow.Callback authCallback = new JsPopUpOAuthWindow.Callback() {
            @Override
            public void oAuthFinished(int authenticationStatus) {
                if (authenticationStatus == 2) {
                    IDE.fireEvent(new SSHKeyProcessorEvent(remoteUri, true, new SSHKeyProcessor.Callback() {
                        @Override
                        public void onSuccess() {
                            cloneRepository(remoteUri, remoteName, folder);
                        }
                    }));
                } else {
                    GitExtension.deleteFolder(folder);
                }
            }
        };

        try {
            GitClientService
                    .getInstance()
                    .cloneRepositoryWS(vfs.getId(),
                                       folder,
                                       remoteUri,
                                       remoteName,
                                       new RequestCallback<RepoInfo>(new RepoInfoUnmarshallerWS(new RepoInfo())) {
                                           @Override
                                           protected void onSuccess(RepoInfo result) {
                                               onCloneSuccess(result, folder);
                                           }

                                           @Override
                                           protected void onFailure(Throwable e) {
                                               if (e instanceof org.exoplatform.ide.client.framework.websocket.rest.exceptions
                                                       .UnauthorizedException
                                                   || (e instanceof org.exoplatform.ide.client.framework.websocket.rest.exceptions
                                                       .ServerException)) {
                                                   askToAuthorize(authCallback, remoteUri, folder);
                                               } else {
                                                   GitExtension.deleteFolder(folder);
                                                   GitExtension.handleError(e, remoteUri);
                                               }
                                           }
                                       });

            if (display != null) {
                IDE.getInstance().closeView(display.asView().getId());
            }

        } catch (WebSocketException e) {
            IDE.fireEvent(new SSHKeyProcessorEvent(remoteUri, false, new SSHKeyProcessor.Callback() {
                @Override
                public void onSuccess() {
                    cloneRepositoryREST(remoteUri, remoteName, folder);
                }
            }));

        }
    }

    /**
     * Ask user to authorize to perform clone repository into IDE.
     *
     * @param callback
     *         callback for user authorization
     * @param vcsUrl
     *         url for remote repository
     * @param folder
     *         folder to delete if user won't to authorize
     */
    private void askToAuthorize(final JsPopUpOAuthWindow.Callback callback, final String vcsUrl, final FolderModel folder) {
        GitUrlInfoUnmarshaller unmarshaller = new GitUrlInfoUnmarshaller(new GitUrlVendorInfo());

        try {
            GitClientService.getInstance().getUrlVendorInfo(vcsUrl, new AsyncRequestCallback<GitUrlVendorInfo>(unmarshaller) {
                @Override
                protected void onSuccess(final GitUrlVendorInfo info) {
                    if (info.getVendorName() != null && !(info.getVendorName().equals("bitbucket") && !info.isGivenUrlSSH())) {
                        Dialogs.getInstance().ask(GitExtension.MESSAGES.authorizeTitle(),
                                                  GitExtension.MESSAGES.authorizeBody(info.getVendorBaseHost()),
                                                  new BooleanValueReceivedHandler() {
                                                      @Override
                                                      public void booleanValueReceived(Boolean value) {
                                                          if (value != null && value) {
                                                              new JsPopUpOAuthWindow().withOauthProvider(info.getVendorName())
                                                                                      .withScopes(info.getOAuthScopes())
                                                                                      .withCallback(callback)
                                                                                      .login();
                                                          } else {
                                                              GitExtension.deleteFolder(folder);
                                                          }
                                                      }
                                                  });
                    } else {
                        IDE.fireEvent(new OutputEvent(GitExtension.MESSAGES.authorizeNotSupported()));
                    }
                }

                @Override
                protected void onFailure(Throwable e) {
                    GitExtension.handleError(e, vcsUrl);
                }
            });
        } catch (RequestException e) {
            GitExtension.handleError(e, vcsUrl);
        }
    }

    /**
     * Get the necessary parameters values and call the clone repository method (over HTTP).
     *
     * @param remoteUri
     *         the location of the remote repository
     * @param remoteName
     *         remote name instead of "origin"
     * @param folder
     *         folder (root of GIT repository)
     */
    private void cloneRepositoryREST(final String remoteUri, final String remoteName, final FolderModel folder) {
        final JsPopUpOAuthWindow.Callback authCallback = new JsPopUpOAuthWindow.Callback() {
            @Override
            public void oAuthFinished(int authenticationStatus) {
                if (authenticationStatus == 2) {
                    IDE.fireEvent(new SSHKeyProcessorEvent(remoteUri, true, new SSHKeyProcessor.Callback() {
                        @Override
                        public void onSuccess() {
                            cloneRepositoryREST(remoteUri, remoteName, folder);
                        }
                    }));
                } else {
                    GitExtension.deleteFolder(folder);
                }
            }
        };

        try {
            GitClientService
                    .getInstance()
                    .cloneRepository(vfs.getId(),
                                     folder,
                                     remoteUri,
                                     remoteName,
                                     new AsyncRequestCallback<RepoInfo>(new RepoInfoUnmarshaller(new RepoInfo())) {
                                         @Override
                                         protected void onSuccess(RepoInfo result) {
                                             onCloneSuccess(result, folder);
                                         }

                                         @Override
                                         protected void onFailure(Throwable e) {
                                             if (e instanceof org.exoplatform.gwtframework.commons.exception.UnauthorizedException ||
                                                 (e instanceof org.exoplatform.gwtframework.commons.exception.ServerException)) {
                                                 askToAuthorize(authCallback, remoteUri, folder);
                                             } else {
                                                 GitExtension.deleteFolder(folder);
                                                 GitExtension.handleError(e, remoteUri);
                                             }
                                         }
                                     });
        } catch (RequestException e) {
            GitExtension.handleError(e, remoteUri);
        }
        if (display != null) {
            IDE.getInstance().closeView(display.asView().getId());
        }
    }

    /**
     * Perform actions when repository was successfully cloned.
     *
     * @param folder
     *         {@link FolderModel} to clone
     */
    private void onCloneSuccess(RepoInfo gitRepositoryInfo, final FolderModel folder) {
        IDE.fireEvent(new OutputEvent(GitExtension.MESSAGES.cloneSuccess(gitRepositoryInfo.getRemoteUri()), Type.GIT));
        IDE.fireEvent(new ConvertToProjectEvent(folder.getId(), vfs.getId(), null));
        IDE.fireEvent(new RepositoryClonedEvent(gitRepositoryInfo.getRemoteUri()));
    }

    /** {@inheritDoc} */
    @Override
    protected boolean makeSelectionCheck() {
        return true;
    }
}
