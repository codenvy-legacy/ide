/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.git.client.clone;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.project.ConvertToProjectEvent;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.rest.RequestCallback;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.git.client.marshaller.RepoInfoUnmarshaller;
import org.exoplatform.ide.git.client.marshaller.RepoInfoUnmarshallerWS;
import org.exoplatform.ide.git.shared.RepoInfo;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.FolderUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FolderModel;

/**
 * Presenter for Clone Repository View.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 22, 2011 4:31:12 PM anya $
 */
public class CloneRepositoryPresenter extends GitPresenter implements CloneRepositoryHandler {
    public interface Display extends IsView {
        /**
         * Returns project name field.
         * 
         * @return {@link HasValue<{@link String}>}
         */
        HasValue<String> getProjectNameValue();

        /**
         * Returns remote URI field.
         * 
         * @return {@link HasValue<{@link String}>}
         */
        HasValue<String> getRemoteUriValue();

        /**
         * Returns remote name field.
         * 
         * @return {@link HasValue<{@link String}>}
         */
        HasValue<String> getRemoteNameValue();

        /**
         * Returns clone repository button.
         * 
         * @return {@link HasClickHandlers}
         */
        HasClickHandlers getCloneButton();

        /**
         * Returns cancel button.
         * 
         * @return {@link HasClickHandlers}
         */
        HasClickHandlers getCancelButton();

        /**
         * Changes the state of clone button.
         * 
         * @param enable
         */
        void enableCloneButton(boolean enable);

        void focusInRemoteUrlField();
    }

    /** Presenter's display. */
    private Display             display;

    private static final String DEFAULT_REPO_NAME = "origin";

    /** @param eventBus */
    public CloneRepositoryPresenter() {
        IDE.addHandler(CloneRepositoryEvent.TYPE, this);
    }

    /** @param d */
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
                    doClone(display.getRemoteUriValue().getValue(),//
                            display.getRemoteNameValue().getValue(),//
                            display.getProjectNameValue().getValue());
                }
            }
        });

        display.getRemoteUriValue().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                String remoteUrl = event.getValue();
                boolean enable = (remoteUrl != null && remoteUrl.length() > 0);
                if (remoteUrl.endsWith("/")) {
                    remoteUrl = remoteUrl.substring(0, remoteUrl.length() - 1);
                }
                if (remoteUrl.endsWith(".git")) {
                    remoteUrl = remoteUrl.substring(0, remoteUrl.length() - 4);
                    String[] split = remoteUrl.split("/");
                    display.getProjectNameValue().setValue(split[split.length - 1]);
                }
                display.enableCloneButton(enable);
            }
        });
    }

    /**
     * @see org.exoplatform.ide.git.client.clone.CloneRepositoryHandler#onCloneRepository(org.exoplatform.ide.git.client.clone
     *      .CloneRepositoryEvent)
     */
    @Override
    public void onCloneRepository(CloneRepositoryEvent event) {
        Display d = GWT.create(Display.class);
        IDE.getInstance().openView(d.asView());
        bindDisplay(d);
        display.focusInRemoteUrlField();
        display.getRemoteNameValue().setValue(DEFAULT_REPO_NAME);
        display.enableCloneButton(false);
    }

    /**
     * Going to cloning repository. Clone process flow 3 steps: - create new folder with name workDir - clone repository to this folder -
     * convert folder to project. This need because by default project with out file and folder not empty. It content ".project" item. Clone
     * is impossible to not empty folder
     * 
     * @param remoteUri - git url
     * @param remoteName - remote name (by default origin)
     * @param workDir - name of target folder
     */
    public void doClone(final String remoteUri, final String remoteName, final String workDir) {
        FolderModel folder = new FolderModel();
        folder.setName(workDir);
        try {
            VirtualFileSystem.getInstance().createFolder(vfs.getRoot(),
                                                         new AsyncRequestCallback<FolderModel>(new FolderUnmarshaller(folder)) {
                                                             @Override
                                                             protected void onSuccess(FolderModel result) {
                                                                 cloneRepository(remoteUri, remoteName, result);
                                                             }

                                                             @Override
                                                             protected void onFailure(Throwable exception) {
                                                                 String errorMessage =
                                                                                       (exception.getMessage() != null && exception.getMessage()
                                                                                                                                   .length() > 0)
                                                                                           ? exception.getMessage()
                                                                                           : GitExtension.MESSAGES.cloneFailed(remoteUri);
                                                                 IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
                                                             }
                                                         });
        } catch (RequestException e) {
            String errorMessage = (e.getMessage() != null && e.getMessage().length() > 0) ?
                e.getMessage() : GitExtension.MESSAGES.cloneFailed(remoteUri);
            IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
        }
    }

    private void deleteFolder(FolderModel path) {
        try {
            VirtualFileSystem.getInstance().delete(path,
                                                   new AsyncRequestCallback<String>() {
                                                       @Override
                                                       protected void onSuccess(String result) {
                                                           // Do nothing
                                                       }

                                                       @Override
                                                       protected void onFailure(Throwable exception) {
                                                           IDE.fireEvent(new ExceptionThrownEvent(exception,
                                                                                                  "Exception during folder removing"));
                                                       }
                                                   });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e, "Exception during removing of directory project"));
        }
    }

    /**
     * Get the necessary parameters values and clone repository (over WebSocket or HTTP).
     * 
     * @param remoteUri the location of the remote repository
     * @param remoteName remote name instead of "origin"
     * @param folder folder (root of GIT repository)
     */
    private void cloneRepository(final String remoteUri, String remoteName, final FolderModel folder) {
        try {
            GitClientService.getInstance().cloneRepositoryWS(vfs.getId(), folder, remoteUri, remoteName,
                                                             new RequestCallback<RepoInfo>(new RepoInfoUnmarshallerWS(new RepoInfo())) {

                                                                 @Override
                                                                 protected void onSuccess(RepoInfo result) {
                                                                     onCloneSuccess(result, folder);
                                                                 }

                                                                 @Override
                                                                 protected void onFailure(Throwable exception) {
                                                                     deleteFolder(folder);
                                                                     handleError(exception, remoteUri);

                                                                 }
                                                             });
            IDE.getInstance().closeView(display.asView().getId());
        } catch (WebSocketException e) {
            cloneRepositoryREST(remoteUri, remoteName, folder);
        }
    }

    /**
     * Get the necessary parameters values and call the clone repository method (over HTTP).
     * 
     * @param remoteUri the location of the remote repository
     * @param remoteName remote name instead of "origin"
     * @param folder folder (root of GIT repository)
     */
    private void cloneRepositoryREST(final String remoteUri, String remoteName, final FolderModel folder) {
        try {
            GitClientService.getInstance().cloneRepository(vfs.getId(), folder, remoteUri, remoteName,
                                                           new AsyncRequestCallback<RepoInfo>(new RepoInfoUnmarshaller(new RepoInfo())) {
                                                               @Override
                                                               protected void onSuccess(RepoInfo result) {
                                                                   onCloneSuccess(result, folder);
                                                               }

                                                               @Override
                                                               protected void onFailure(Throwable exception) {
                                                                   deleteFolder(folder);
                                                                   handleError(exception, remoteUri);
                                                               }
                                                           });
        } catch (RequestException e) {
            handleError(e, remoteUri);
        }
        if (display != null) {
            IDE.getInstance().closeView(display.asView().getId());
        }
    }

    /**
     * Perform actions when repository was successfully cloned.
     * 
     * @param folder {@link FolderModel} to clone
     */
    private void onCloneSuccess(final RepoInfo gitRepositoryInfo, final FolderModel folder) {
        IDE.fireEvent(new OutputEvent(GitExtension.MESSAGES.cloneSuccess(gitRepositoryInfo.getRemoteUri()), Type.GIT));
        IDE.fireEvent(new ConvertToProjectEvent(folder.getId(), vfs.getId(), null));

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                String[] userRepo = GitURLParser.parseGitHubUrl(gitRepositoryInfo.getRemoteUri());
                if (userRepo != null) {
                    IDE.fireEvent(new CloneRepositoryCompleteEvent(userRepo[0], userRepo[1]));
                }
            }
        });
    }

    private void handleError(Throwable e, String remoteUri) {
        String errorMessage = (e.getMessage() != null && e.getMessage().length() > 0) ?
            e.getMessage() : GitExtension.MESSAGES.cloneFailed(remoteUri);
        IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
    }

    @Override
    protected boolean makeSelectionCheck() {
        return true;
    }

}
