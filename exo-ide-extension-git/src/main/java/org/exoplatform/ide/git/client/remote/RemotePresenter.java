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
package org.exoplatform.ide.git.client.remote;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.git.client.marshaller.RemoteListUnmarshaller;
import org.exoplatform.ide.git.shared.Remote;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter for view to work with remote repository list (view, add and delete). View must be pointed in Views.gwt.xml.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 18, 2011 11:13:30 AM anya $
 */
public class RemotePresenter extends GitPresenter implements ShowRemotesHandler, ViewClosedHandler {

    public interface Display extends IsView {
        /**
         * Get add button's click handler.
         * 
         * @return {@link HasClickHandlers} click handler
         */
        HasClickHandlers getAddButton();

        /**
         * Get delete button's click handler.
         * 
         * @return {@link HasClickHandlers} click handler
         */
        HasClickHandlers getDeleteButton();

        /**
         * Get close button's click handler.
         * 
         * @return {@link HasClickHandlers} click handler
         */
        HasClickHandlers getCloseButton();

        /**
         * Get list grid with remote repositories.
         * 
         * @return {@link ListGridItem}
         */
        ListGridItem<Remote> getRemoteGrid();

        /**
         * Get the selected remote repository in list grid.
         * 
         * @return {@link Remote} selected remote repository
         */
        Remote getSelectedRemote();

        /**
         * Change the enabled state of the delete button.
         * 
         * @param enable enabled state
         */
        void enableDeleteButton(boolean enable);

    }

    /** Presenter's display. */
    private Display display;

    public RemotePresenter() {
        IDE.addHandler(ShowRemotesEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    /**
     * Bind pointed display with presenter.
     * 
     * @param d display
     */
    public void bindDisplay(Display d) {
        this.display = d;

        display.getAddButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                doAdd();
            }
        });

        display.getCloseButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getDeleteButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                askToDelete();
            }
        });

        display.getRemoteGrid().addSelectionHandler(new SelectionHandler<Remote>() {

            @Override
            public void onSelection(SelectionEvent<Remote> event) {
                boolean selected = event.getSelectedItem() != null;
                display.enableDeleteButton(selected);
            }
        });
    }

    /**
     * @see org.exoplatform.ide.git.client.remote.ShowRemotesHandler#onShowRemotes(org.exoplatform.ide.git.client.remote .ShowRemotesEvent)
     */
    @Override
    public void onShowRemotes(ShowRemotesEvent event) {
        if (makeSelectionCheck()) {
            // String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
            String projectId = getSelectedProject().getId();
            getRemotes(projectId);
        }
    }

    /**
     * Get the list of remote repositories for local one. If remote repositories are found, then get the list of branches (remote and
     * local).
     * 
     * @param workDir
     */
    public void getRemotes(final String projectId) {
        try {
            GitClientService.getInstance()
                            .remoteList(vfs.getId(), projectId, null, true,
                                        new AsyncRequestCallback<List<Remote>>(
                                                                               new RemoteListUnmarshaller(new ArrayList<Remote>())) {
                                            @Override
                                            protected void onSuccess(List<Remote> result) {
                                                if (display == null) {
                                                    Display d = GWT.create(Display.class);
                                                    IDE.getInstance().openView(d.asView());
                                                    bindDisplay(d);
                                                }

                                                display.getRemoteGrid().setValue(result);
                                                display.enableDeleteButton(false);
                                            }

                                            @Override
                                            protected void onFailure(Throwable exception) {
                                                String errorMessage =
                                                                      (exception.getMessage() != null) ? exception.getMessage()
                                                                          : GitExtension.MESSAGES
                                                                                                 .remoteListFailed();
                                                Dialogs.getInstance().showError(errorMessage);
                                            }
                                        });
        } catch (RequestException e) {
            String errorMessage =
                                  (e.getMessage() != null) ? e.getMessage() : GitExtension.MESSAGES.remoteListFailed();
            Dialogs.getInstance().showError(errorMessage);
        }
    }

    /** Show UI for adding remote repository. */
    private void doAdd() {
        new AddRemoteRepositoryPresenter(null, "Add remote repository") {

            @Override
            public void onSubmit() {
                String name = getDisplay().getName().getValue();
                String url = getDisplay().getUrl().getValue();
                addRemoteRepository(name, url);
            }
        };
    }

    /**
     * Add new remote repository to the list of remote repositories.
     * 
     * @param name name
     * @param url url
     */
    private void addRemoteRepository(String name, String url) {
        // final String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
        final String projectId = getSelectedProject().getId();

        try {
            GitClientService.getInstance().remoteAdd(vfs.getId(), projectId, name, url, new AsyncRequestCallback<String>() {

                @Override
                protected void onSuccess(String result) {
                    getRemotes(projectId);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    String errorMessage =
                                          (exception.getMessage() != null) ? exception.getMessage()
                                              : GitExtension.MESSAGES.remoteAddFailed();
                    IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
                }
            });
        } catch (RequestException e) {
            String errorMessage =
                                  (e.getMessage() != null) ? e.getMessage() : GitExtension.MESSAGES.remoteAddFailed();
            IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
        }
    }

    /** Ask the user to confirm the deletion of the remote repository. */
    private void askToDelete() {
        final Remote selectedRemote = display.getSelectedRemote();
        if (selectedRemote == null) {
            Dialogs.getInstance().showInfo(GitExtension.MESSAGES.selectRemoteRepositoryFail());
            return;
        }

        Dialogs.getInstance().ask(GitExtension.MESSAGES.deleteRemoteRepositoryTitle(),
                                  GitExtension.MESSAGES.deleteRemoteRepositoryQuestion(selectedRemote.getName()),
                                  new BooleanValueReceivedHandler() {

                                      @Override
                                      public void booleanValueReceived(Boolean value) {
                                          if (value != null && value)
                                              doDelete(selectedRemote.getName());
                                      }
                                  });
    }

    /**
     * Delete remote repository from the list of the remote repositories.
     * 
     * @param name name of the remote repository to delete
     */
    private void doDelete(String name) {
        // final String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
        final String projectId = getSelectedProject().getId();

        try {
            GitClientService.getInstance().remoteDelete(vfs.getId(), projectId, name, new AsyncRequestCallback<String>() {

                @Override
                protected void onSuccess(String result) {
                    getRemotes(projectId);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    String errorMessage =
                                          (exception.getMessage() != null) ? exception.getMessage()
                                              : GitExtension.MESSAGES.remoteDeleteFailed();
                    IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
                }
            });
        } catch (RequestException e) {
            String errorMessage =
                                  (e.getMessage() != null) ? e.getMessage() : GitExtension.MESSAGES.remoteDeleteFailed();
            IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
        }
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }
}
