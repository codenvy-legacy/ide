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
package org.exoplatform.ide.extension.samples.client.inviting.github;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.UnauthorizedException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.IDELoader;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.ui.JsPopUpOAuthWindow;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.samples.client.inviting.InviteClientService;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.clone.RepositoryClonedEvent;
import org.exoplatform.ide.git.client.clone.RepositoryClonedHandler;
import org.exoplatform.ide.git.client.clone.GitURLParser;
import org.exoplatform.ide.git.client.github.collaborators.GitHubClientService;
import org.exoplatform.ide.git.client.marshaller.RemoteListUnmarshaller;
import org.exoplatform.ide.git.shared.Collaborators;
import org.exoplatform.ide.git.shared.GitHubUser;
import org.exoplatform.ide.git.shared.Remote;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.ArrayList;
import java.util.List;

/** Possibility for user to invite into IDE selected collaborators from imported repository. */
public class InviteGitHubDevelopersPresenter implements RepositoryClonedHandler, ViewClosedHandler,
                                                        InviteGitHubCollaboratorsHandler, GitHubUserSelectionChangedHandler,
                                                        ProjectOpenedHandler, ProjectClosedHandler,
                                                        VfsChangedHandler {

    public interface Display extends IsView {
        void setDevelopers(List<GitHubUser> userList, GitHubUserSelectionChangedHandler selectionChangedHandler);

        boolean isSelected(GitHubUser user);

        void setSelected(GitHubUser user, boolean selected);

        HasValue<Boolean> getSelectAllCheckBox();

        HasClickHandlers getInviteButton();

        HasClickHandlers getCloseButton();

        String getInviteMessage();

        void setInviteButtonEnabled(boolean enabled);

        HasClickHandlers getAddMessageButton();

        void setAddMessageButtonEnabled(boolean enabled);

        void setAddMessageButtonText(String text);

        void setMessageFiledVisibility(boolean visible);

        String getAddMessageButtonText();
    }

    private Display display;

    private VirtualFileSystemInfo vfs;

    private List<GitHubUser> collaborators;

    private ProjectModel project;

    public InviteGitHubDevelopersPresenter() {
        IDE.getInstance().addControl(new InviteGitHubCollaboratorsControl());
        IDE.addHandler(InviteGitHubCollaboratorsEvent.TYPE, this);

        IDE.addHandler(RepositoryClonedEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
        IDE.addHandler(VfsChangedEvent.TYPE, this);
    }

    /** {@inheritDoc} */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onInviteGitHubCollaborators(InviteGitHubCollaboratorsEvent event) {
        if (project == null || vfs == null || display != null) {
            return;
        }

        try {
            GitClientService.getInstance()
                            .remoteList(vfs.getId(), project.getId(), null, true,
                                        new AsyncRequestCallback<List<Remote>>(
                                                new RemoteListUnmarshaller(new ArrayList<Remote>())) {
                                            @Override
                                            protected void onSuccess(List<Remote> result) {
                                                if (result.size() == 0) {
                                                    Dialogs.getInstance().showError(GitExtension.MESSAGES.remoteListFailed());
                                                    return;
                                                }

                                                Remote origin = getRemoteOrigin(result);
                                                String[] repo = GitURLParser.parseGitHubUrl(origin.getUrl());
                                                if (repo == null) {
                                                    Dialogs.getInstance().showError(GitExtension.MESSAGES.remoteListFailed());
                                                    return;
                                                }

                                                loadGitHubCollaborators(repo[0], repo[1], true);
                                            }

                                            @Override
                                            protected void onFailure(Throwable exception) {
                                                String errorMessage =
                                                        exception.getMessage() != null ? exception.getMessage()
                                                                                       : GitExtension.MESSAGES
                                                                                                     .remoteListFailed();
                                                Dialogs.getInstance().showError(errorMessage);
                                            }
                                        });
        } catch (RequestException e) {
            String errorMessage = (e.getMessage() != null) ? e.getMessage() : GitExtension.MESSAGES.remoteListFailed();
            Dialogs.getInstance().showError(errorMessage);
        }
    }

    /** Search "origin" repository */
    private Remote getRemoteOrigin(List<Remote> remotes) {
        if (remotes.size() == 1) {
            return remotes.get(0);
        }

        for (Remote remote : remotes) {
            if ("origin".equals(remote.getName())) {
                return remote;
            }
        }

        return remotes.get(0);
    }

    /** {@inheritDoc} */
    @Override
    public void onRepositoryCloned(RepositoryClonedEvent event) {
        if (!IDE.currentWorkspace.isTemporary()) {
            String[] userRepo = GitURLParser.parseGitHubUrl(event.getVcsUrl());
            if (userRepo != null) {
                loadGitHubCollaborators(userRepo[0], userRepo[1], false);
            }
        }
    }

    /** Load list of collaborators for specific repository on GitHub. */
    private void loadGitHubCollaborators(final String user, final String repository, final boolean showIfEmpty) {
        final JsPopUpOAuthWindow.Callback authCallback = new JsPopUpOAuthWindow.Callback() {
            @Override
            public void oAuthFinished(int authenticationStatus) {
                if (authenticationStatus == 2) {
                    loadGitHubCollaborators(user, repository, showIfEmpty);
                }
            }
        };

        AutoBean<Collaborators> autoBean = GitExtension.AUTO_BEAN_FACTORY.collaborators();
        AutoBeanUnmarshaller<Collaborators> unmarshaller = new AutoBeanUnmarshaller<Collaborators>(autoBean);
        try {
            GitHubClientService.getInstance()
                               .getCollaborators(user,
                                                 repository,
                                                 new AsyncRequestCallback<Collaborators>(unmarshaller) {
                                                     @Override
                                                     protected void onSuccess(Collaborators result) {
                                                         collaboratorsReceived(result, showIfEmpty);
                                                     }

                                                     @Override
                                                     protected void onFailure(Throwable e) {
                                                         if (e instanceof UnauthorizedException) {
                                                             askUserToLogin(authCallback);
                                                         } else {
                                                             Dialogs.getInstance().showError(e.getLocalizedMessage());
                                                         }
                                                     }
                                                 });
        } catch (RequestException e) {
            Dialogs.getInstance().showInfo("Codenvy failed to get the list of collaborators.");
        }
    }

    /** Ask user to login if unauthorized error appears. */
    private void askUserToLogin(final JsPopUpOAuthWindow.Callback callback) {
        Dialogs.getInstance().ask(GitExtension.MESSAGES.authorizeTitle(),
                                  GitExtension.MESSAGES.authorizeCollaboratorsBody("github.com"),
                                  new BooleanValueReceivedHandler() {
                                      @Override
                                      public void booleanValueReceived(Boolean value) {
                                          if (value != null && value) {
                                              new JsPopUpOAuthWindow().withOauthProvider("github")
                                                                      .withScope("repo")
                                                                      .withScope("user")
                                                                      .withCallback(callback)
                                                                      .login();
                                          }
                                      }
                                  });
    }

    /** Parse received list of collaborators and construct window. */
    private void collaboratorsReceived(Collaborators result, boolean showIfEmpty) {
        if (display != null || (!showIfEmpty && result.getCollaborators().size() == 0)) {
            return;
        }

        display = GWT.create(Display.class);
        IDE.getInstance().openView(display.asView());
        bindDisplay();

        collaborators = new ArrayList<GitHubUser>();
        for (GitHubUser user : result.getCollaborators()) {
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                collaborators.add(user);
            }
        }

        display.setDevelopers(collaborators, this);
        display.setInviteButtonEnabled(false);
        display.setAddMessageButtonEnabled(false);
        resetMessageEntry();
    }

    /** Checks if user has selected recipients for inviting. */
    private boolean hasSelectedUsers() {
        for (GitHubUser user : collaborators) {
            if (display.isSelected(user)) {
                return true;
            }
        }

        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void onGitHubUserSelectionChanged(GitHubUser user, boolean selected) {
        display.setInviteButtonEnabled(hasSelectedUsers());
        display.setAddMessageButtonEnabled(hasSelectedUsers());
        if (!hasSelectedUsers()) {
            resetMessageEntry();
        }
    }

    /** Bind buttons for their operations. */
    private void bindDisplay() {
        display.getCloseButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });
        display.getAddMessageButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                reactMessageEntry();
            }
        });
        display.getInviteButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                inviteCollaborators();
            }
        });

        display.getSelectAllCheckBox().addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                boolean selectAll = event.getValue() == null ? false : event.getValue();
                for (GitHubUser user : collaborators) {
                    display.setSelected(user, selectAll);
                }

                display.setInviteButtonEnabled(hasSelectedUsers());
                display.setAddMessageButtonEnabled(hasSelectedUsers());
                if (!hasSelectedUsers()) {
                    resetMessageEntry();
                }
            }
        });
    }

    private List<String> emailsToSend = new ArrayList<String>();

    private int invitations = 0;

    /** Collect all selected collaborators and send them invitations. */
    private void inviteCollaborators() {
        emailsToSend.clear();

        for (GitHubUser user : collaborators) {
            if (display.isSelected(user) && user.getEmail() != null && !user.getEmail().isEmpty()) {
                emailsToSend.add(user.getEmail());
            }
        }

        if (emailsToSend.size() > 0) {
            invitations = 0;
            sendNextEmail();
        }
    }

    private void reactMessageEntry() {
        if (display.getAddMessageButtonText().equals("Add a message")) {
            display.setAddMessageButtonText("Discard message");
            display.setMessageFiledVisibility(true);
        } else {
            display.setAddMessageButtonText("Add a message");
            display.setMessageFiledVisibility(false);
        }
    }

    private void resetMessageEntry() {
        if (display.getAddMessageButtonText().equals("Discard message")) {
            display.setAddMessageButtonText("Add a message");
            display.setMessageFiledVisibility(false);
        }
    }

    /** Send invitation to collaborator from common list. */
    private void sendNextEmail() {
        if (emailsToSend.size() == 0) {
            IDELoader.hide();
            IDE.getInstance().closeView(display.asView().getId());
            if (invitations == 1) {
                Dialogs.getInstance().showInfo("Codenvy", "One invitation was sent successfully.");
            } else {
                Dialogs.getInstance().showInfo("Codenvy", "" + invitations + " invitations were sent successfully.");
            }
            return;
        }

        String email = emailsToSend.remove(0);
        String inviteMessage = display.getInviteMessage();

        IDELoader.show("Inviting " + email);
        try {
            InviteClientService.getInstance().inviteUser(email, inviteMessage, new AsyncRequestCallback<String>() {
                @Override
                protected void onSuccess(String result) {
                    invitations++;
                    sendNextEmail();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    IDELoader.hide();
                    IDE.fireEvent(new ExceptionThrownEvent(exception));
                }
            });
        } catch (RequestException e) {
            IDELoader.hide();
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        project = event.getProject();
    }

    /** {@inheritDoc} */
    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        project = null;
    }

    /** {@inheritDoc} */
    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        vfs = event.getVfsInfo();
    }

}
