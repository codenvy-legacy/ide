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
package org.exoplatform.ide.git.client.pull;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.project.api.TreeRefreshedEvent;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.rest.RequestCallback;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.shared.text.BadLocationException;
import org.exoplatform.ide.editor.shared.text.IDocument;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.remote.HasBranchesPresenter;
import org.exoplatform.ide.git.shared.Branch;
import org.exoplatform.ide.git.shared.BranchListRequest;
import org.exoplatform.ide.git.shared.Remote;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.FileContentUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Presenter of the view for pulling changes from remote repository. View must be pointed in Views.gwt.xml.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 20, 2011 4:20:24 PM anya $
 */
public class PullPresenter extends HasBranchesPresenter implements PullHandler, EditorFileOpenedHandler, EditorFileClosedHandler {

    interface Display extends IsView {
        /**
         * Get pull button's click handler.
         *
         * @return {@link HasClickHandlers} click handler
         */
        HasClickHandlers getPullButton();

        /**
         * Get cancel button's click handler.
         *
         * @return {@link HasClickHandlers} click handler
         */
        HasClickHandlers getCancelButton();

        /**
         * Get remote repository field.
         *
         * @return {@link HasValue}
         */
        HasValue<String> getRemoteName();

        /**
         * Get remote branches field.
         *
         * @return {@link HasValue}
         */
        HasValue<String> getRemoteBranches();

        /**
         * Get local branches field.
         *
         * @return {@link HasValue}
         */
        HasValue<String> getLocalBranches();

        /**
         * Set values of remote repository branches.
         *
         * @param values
         *         values to set
         */
        void setRemoteBranches(String[] values);

        /**
         * Set values of local repository branches.
         *
         * @param values
         *         values to set
         */
        void setLocalBranches(String[] values);

        /**
         * Change the enable state of the pull button.
         *
         * @param enable
         *         enable state
         */
        void enablePullButton(boolean enable);

        /**
         * Set values of remote repositories.
         *
         * @param values
         *         values to set
         */
        void setRemoteValues(LinkedHashMap<String, String> values);

        /**
         * Get the display name of the remote repository.
         *
         * @return String display name of the remote repository
         */
        String getRemoteDisplayValue();
    }

    private Display display;
    private Map<FileModel, Editor> openedEditor = new LinkedHashMap<FileModel, Editor>();

    public void bindDisplay(Display d) {
        this.display = d;

        display.getCancelButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getPullButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                doPull();
            }
        });

        display.getRemoteName().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                display.setRemoteBranches(getRemoteBranchesNamesToDisplay(display.getRemoteDisplayValue()));
            }
        });

        display.getRemoteBranches().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                boolean empty = (event.getValue() == null || event.getValue().length() <= 0);
                display.enablePullButton(!empty);
            }
        });
    }

    /**
     *
     */
    public PullPresenter() {
        IDE.addHandler(PullEvent.TYPE, this);
        IDE.addHandler(EditorFileOpenedEvent.TYPE, this);
        IDE.addHandler(EditorFileClosedEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.git.client.pull.PullHandler#onPull(org.exoplatform.ide.git.client.pull.PullEvent) */
    @Override
    public void onPull(PullEvent event) {
        if (makeSelectionCheck()) {
            getRemotes(getSelectedProject().getId());
        }
    }

    /** @see org.exoplatform.ide.git.client.remote.HasBranchesPresenter#onRemotesReceived(java.util.List) */
    @Override
    public void onRemotesReceived(List<Remote> remotes) {
        Display d = GWT.create(Display.class);
        IDE.getInstance().openView(d.asView());
        bindDisplay(d);

        String projectId = getSelectedProject().getId();

        LinkedHashMap<String, String> remoteValues = new LinkedHashMap<String, String>();
        for (Remote remote : remotes) {
            remoteValues.put(remote.getUrl(), remote.getName());
        }

        display.setRemoteValues(remoteValues);

        getBranches(projectId, BranchListRequest.LIST_REMOTE);
        getBranches(projectId, BranchListRequest.LIST_LOCAL);
    }

    /** @see org.exoplatform.ide.git.client.remote.HasBranchesPresenter#setRemoteBranches(java.util.List) */
    @Override
    protected void setRemoteBranches(List<Branch> result) {
        display.setRemoteBranches(getRemoteBranchesNamesToDisplay(display.getRemoteDisplayValue()));
    }

    /** @see org.exoplatform.ide.git.client.remote.HasBranchesPresenter#setLocalBranches(java.lang.String[]) */
    @Override
    protected void setLocalBranches(List<Branch> branches) {
        if (branches == null || branches.isEmpty()) {
            display.setLocalBranches(new String[]{"master"});
            return;
        }

        String[] values = new String[branches.size()];
        for (int i = 0; i < branches.size(); i++) {
            values[i] = branches.get(i).getDisplayName();
        }
        display.setLocalBranches(values);
    }

    /**
     * Perform pull from pointed by user remote repository, from pointed remote branch to local one. Local branch may not be pointed. Sends
     * request over WebSocket or HTTP.
     */
    private void doPull() {
        String remoteName = display.getRemoteDisplayValue();
        final String remoteUrl = display.getRemoteName().getValue();
        final ProjectModel project = getSelectedProject();
        IDE.getInstance().closeView(display.asView().getId());

        try {
            GitClientService.getInstance().pullWS(vfs.getId(), project, getRefs(), remoteName,
                                                  new RequestCallback<String>() {
                                                      @Override
                                                      protected void onSuccess(String result) {
                                                          IDE.fireEvent(
                                                                  new OutputEvent(GitExtension.MESSAGES.pullSuccess(remoteUrl), Type.GIT));
                                                          IDE.fireEvent(new RefreshBrowserEvent());
                                                      }

                                                      @Override
                                                      protected void onFailure(Throwable exception) {
                                                          handleError(exception, remoteUrl);
                                                          //if it is a merge conflict
                                                          if (exception.getMessage() != null &&
                                                              exception.getMessage().contains("Merge conflict appeared in files")) {
                                                              //set conflict icon
                                                              IDE.fireEvent(new TreeRefreshedEvent(project));
                                                              //get path for files wit conflicts
                                                              String[] filesWithConflicts = exception.getMessage().split("</br>");
                                                              //if file opened, update content for this file
                                                              for (int i = 1; i < filesWithConflicts.length - 1; i++) {
                                                                  Iterator<FileModel> iterator = openedEditor.keySet().iterator();
                                                                  while (iterator.hasNext()) {
                                                                      final FileModel openedFile = iterator.next();
                                                                      if (openedFile.getPath().contains(filesWithConflicts[i])) {
                                                                          try {
                                                                              VirtualFileSystem.getInstance().getContent(
                                                                                      new AsyncRequestCallback<FileModel>(
                                                                                              new FileContentUnmarshaller(openedFile)) {
                                                                                          @Override
                                                                                          protected void onSuccess(FileModel result) {
                                                                                              IDocument document =
                                                                                                      openedEditor.get(openedFile)
                                                                                                                  .getDocument();
                                                                                              try {
                                                                                                  document.replace(0,
                                                                                                                   document.getLength(),
                                                                                                                   result.getContent());
                                                                                              } catch (BadLocationException e) {
                                                                                                  handleError(e, remoteUrl);
                                                                                              }
                                                                                          }

                                                                                          @Override
                                                                                          protected void onFailure(Throwable exception) {
                                                                                              handleError(exception, remoteUrl);
                                                                                          }
                                                                                      });
                                                                          } catch (RequestException e) {
                                                                              IDE.fireEvent(new ExceptionThrownEvent(e));
                                                                          }
                                                                      }
                                                                  }
                                                              }

                                                          }
                                                      }
                                                  });
        } catch (WebSocketException e) {
            doPullREST(project, remoteUrl, remoteName);
        }
    }

    /**
     * Perform pull from pointed by user remote repository, from pointed remote branch to local one. Local branch may not be pointed. Sends
     * request over HTTP.
     */
    private void doPullREST(final ProjectModel project, final String remoteUrl, String remoteName) {
        try {
            GitClientService.getInstance().pull(vfs.getId(), project, getRefs(), remoteName,
                                                new AsyncRequestCallback<String>() {
                                                    @Override
                                                    protected void onSuccess(String result) {
                                                        IDE.fireEvent(
                                                                new OutputEvent(GitExtension.MESSAGES.pullSuccess(remoteUrl), Type.GIT));
                                                        IDE.fireEvent(new RefreshBrowserEvent());
                                                    }

                                                    @Override
                                                    protected void onFailure(Throwable exception) {
                                                        handleError(exception, remoteUrl);
                                                        //if it is a merge conflict
                                                        if (exception.getMessage() != null &&
                                                            exception.getMessage().contains("Merge conflict appeared in files")) {
                                                            //set conflict icons
                                                            IDE.fireEvent(new TreeRefreshedEvent(project));
                                                            //get path for files wit conflicts
                                                            String[] filesWithConflicts = exception.getMessage().split("</br>");
                                                            //if file opened, update content for this file
                                                            for (int i = 1; i < filesWithConflicts.length - 1; i++) {
                                                                Iterator<FileModel> iterator = openedEditor.keySet().iterator();
                                                                while (iterator.hasNext()) {
                                                                    final FileModel openedFile = iterator.next();
                                                                    if (openedFile.getPath().contains(filesWithConflicts[i])) {
                                                                        try {
                                                                            VirtualFileSystem.getInstance().getContent(
                                                                                    new AsyncRequestCallback<FileModel>(
                                                                                            new FileContentUnmarshaller(openedFile)) {
                                                                                        @Override
                                                                                        protected void onSuccess(FileModel result) {
                                                                                            IDocument document =
                                                                                                    openedEditor.get(openedFile)
                                                                                                                .getDocument();
                                                                                            try {
                                                                                                document.replace(0,
                                                                                                                 document.getLength(),
                                                                                                                 result.getContent());
                                                                                            } catch (BadLocationException e) {
                                                                                                handleError(e, remoteUrl);
                                                                                            }
                                                                                        }

                                                                                        @Override
                                                                                        protected void onFailure(Throwable exception) {
                                                                                            handleError(exception, remoteUrl);
                                                                                        }
                                                                                    });
                                                                        } catch (RequestException e) {
                                                                            IDE.fireEvent(new ExceptionThrownEvent(e));
                                                                        }
                                                                    }
                                                                }
                                                            }

                                                        }
                                                    }
                                                });
        } catch (RequestException e) {
            handleError(e, remoteUrl);
        }
    }

    /**
     * Returns list of refs to fetch.
     *
     * @return list of refs to fetch
     */
    private String getRefs() {
        String remoteName = display.getRemoteDisplayValue();
        String localBranch = display.getLocalBranches().getValue();
        String remoteBranch = display.getRemoteBranches().getValue();
        // Form the refspec. User points only the branch names:
        String refs =
                (localBranch == null || localBranch.length() == 0) ? remoteBranch : "refs/heads/" + remoteBranch + ":"
                                                                                    + "refs/remotes/" + remoteName + "/"
                                                                                    + remoteBranch;
        return refs;
    }

    private void handleError(Throwable t, String remoteUrl) {
        String errorMessage = (t.getMessage() != null) ? t.getMessage() : GitExtension.MESSAGES.pullFail(remoteUrl);
        IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
    }

    @Override
    public void onEditorFileOpened(EditorFileOpenedEvent event) {
        openedEditor.put(event.getFile(), event.getEditor());
    }

    @Override
    public void onEditorFileClosed(EditorFileClosedEvent event) {
        if (openedEditor.containsKey(event.getFile())) {
            openedEditor.remove(event.getFile());
        }
    }
}
