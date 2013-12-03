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
package org.exoplatform.ide.git.client.branch;

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
import org.exoplatform.gwtframework.ui.client.dialog.StringValueReceivedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.git.client.editor.UpdateOpenedFilesEvent;
import org.exoplatform.ide.git.client.marshaller.BranchListUnmarshaller;
import org.exoplatform.ide.git.shared.Branch;
import org.exoplatform.ide.git.shared.BranchListRequest;
import org.exoplatform.ide.vfs.client.model.FileModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Presenter of view for displaying and work with branches. The view must be pointed in Views.gwt.xml file.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 8, 2011 12:02:49 PM anya $
 */
public class BranchPresenter extends GitPresenter implements ShowBranchesHandler, EditorFileOpenedHandler, EditorFileClosedHandler {
    interface Display extends IsView {
        /**
         * Click handler for create branch button.
         *
         * @return {@link HasClickHandlers}
         */
        HasClickHandlers getCreateBranchButton();

        /**
         * Click handler for checkout branch button.
         *
         * @return {@link HasClickHandlers}
         */
        HasClickHandlers getCheckoutBranchButton();

        /**
         * Click handler for delete branch button.
         *
         * @return {@link HasClickHandlers}
         */
        HasClickHandlers getDeleteBranchButton();

        /**
         * Click handler for rename branch button.
         *
         * @return {@link HasClickHandlers}
         */
        HasClickHandlers getRenameBranchButton();

        /**
         * Click handler for close button.
         *
         * @return {@link HasClickHandlers}
         */
        HasClickHandlers getCloseButton();

        /**
         * Returns the grid, responsible for displaying branches.
         *
         * @return {@link ListGridItem}
         */
        ListGridItem<Branch> getBranchesGrid();

        /**
         * Get selected branch in grid.
         *
         * @return {@link Branch} selected branch
         */
        Branch getSelectedBranch();

        /**
         * Change the enable state of the delete button.
         *
         * @param enabled is enabled
         */
        void enableDeleteButton(boolean enabled);

        /**
         * Change the enable state of the checkout button.
         *
         * @param enabled is enabled
         */
        void enableCheckoutButton(boolean enabled);

        /**
         * Change the enable state of the rename button.
         *
         * @param enabled is enabled
         */
        void enableRenameButton(boolean enabled);
    }

    /** Presenter's display. */
    private Display display;

    private Map<String, FileModel> openedFiles = new HashMap<String, FileModel>();

    public BranchPresenter() {
        IDE.addHandler(ShowBranchesEvent.TYPE, this);
        IDE.addHandler(EditorFileOpenedEvent.TYPE, this);
        IDE.addHandler(EditorFileClosedEvent.TYPE, this);
    }

    /**
     * Bind display with presenter.
     *
     * @param d display
     */
    public void bindDisplay(Display d) {
        this.display = d;

        display.getBranchesGrid().addSelectionHandler(new SelectionHandler<Branch>() {

            @Override
            public void onSelection(SelectionEvent<Branch> event) {
                boolean enabled = (event.getSelectedItem() != null && !event.getSelectedItem().isActive());
                display.enableDeleteButton(enabled);
                display.enableCheckoutButton(enabled);

                boolean renameEnabled = (event.getSelectedItem() != null && !event.getSelectedItem().isRemote());
                boolean deleteEnabled = (event.getSelectedItem() != null && !event.getSelectedItem().isRemote());
                display.enableRenameButton(renameEnabled);
                display.enableDeleteButton(deleteEnabled);
            }
        });

        display.getCheckoutBranchButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                for (Map.Entry<String, FileModel> openedFile : openedFiles.entrySet()) {
                    if (openedFile.getValue().isContentChanged()) {
                        Dialogs.getInstance().showInfo(GitExtension.MESSAGES.fileUnsaved(openedFile.getValue().getName()));
                        return;
                    }
                }

                doCheckoutBranch();
            }
        });

        display.getCreateBranchButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                askNewBranchName();
            }
        });

        display.getDeleteBranchButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                askDeleteBranch();
            }
        });

        display.getRenameBranchButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                askRenameBranch();
            }
        });

        display.getCloseButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

    }

    /**
     * Get the list of branches.
     *
     * @param workDir Git repository work tree location
     * @param remote get remote branches if <code>true</code>
     */
    public void getBranches(String projectId) {
        try {
            GitClientService.getInstance()
                            .branchList(vfs.getId(), projectId, BranchListRequest.LIST_ALL,
                                        new AsyncRequestCallback<List<Branch>>(
                                                                               new BranchListUnmarshaller(new ArrayList<Branch>())) {

                                            @Override
                                            protected void onSuccess(List<Branch> result) {
                                                display.getBranchesGrid().setValue(result);
                                            }

                                            @Override
                                            protected void onFailure(Throwable exception) {
                                                String errorMessage =
                                                                      (exception.getMessage() != null) ? exception.getMessage()
                                                                          : GitExtension.MESSAGES
                                                                                                 .branchesListFailed();
                                                IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
                                            }
                                        });
        } catch (RequestException e) {
            String errorMessage = (e.getMessage() != null) ? e.getMessage() : GitExtension.MESSAGES.branchesListFailed();
            IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
        }
    }

    /**
     * @see org.exoplatform.ide.git.client.branch.ShowBranchesHandler#onShowBranches(org.exoplatform.ide.git.client.branch
     *      .ShowBranchesEvent)
     */
    @Override
    public void onShowBranches(ShowBranchesEvent event) {
        if (makeSelectionCheck()) {
            Display d = GWT.create(Display.class);
            IDE.getInstance().openView(d.asView());
            bindDisplay(d);

            display.enableCheckoutButton(false);
            display.enableDeleteButton(false);
            getBranches(getSelectedProject().getId());
        }
    }

    private void askNewBranchName() {
        Dialogs.getInstance().askForValue(GitExtension.MESSAGES.branchCreateNew(), GitExtension.MESSAGES.branchTypeNew(),
                                          "", new StringValueReceivedHandler() {

                                              @Override
                                              public void stringValueReceived(String value) {
                                                  if (value != null) {
                                                      doCreateBranch(value);
                                                  }
                                              }
                                          });
    }

    /**
     * Create branch with pointed name.
     *
     * @param name new branch's name
     */
    private void doCreateBranch(String name) {
        final String projectId = getSelectedProject().getId();

        try {
            GitClientService.getInstance().branchCreate(vfs.getId(), projectId, name, null,
                                                        new AsyncRequestCallback<Branch>() {

                                                            @Override
                                                            protected void onSuccess(Branch result) {
                                                                getBranches(projectId);
                                                            }

                                                            @Override
                                                            protected void onFailure(Throwable exception) {
                                                                String errorMessage =
                                                                                      (exception.getMessage() != null)
                                                                                          ? exception.getMessage()
                                                                                          : GitExtension.MESSAGES


                                                                                                                 .branchCreateFailed();
                                                                IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
                                                            }
                                                        });
        } catch (RequestException e) {
            String errorMessage = (e.getMessage() != null) ? e.getMessage() : GitExtension.MESSAGES.branchCreateFailed();
            IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
        }
    }

    /** Checkout the branch. */
    private void doCheckoutBranch() {
        String name = display.getSelectedBranch().getDisplayName();
        String startingPoint = null;
        if (display.getSelectedBranch().isRemote()) {
            startingPoint = display.getSelectedBranch().getDisplayName();
        }
        final String projectId = getSelectedProject().getId();
        if (name == null) {
            return;
        }

        try {
            GitClientService.getInstance().branchCheckout(vfs.getId(), projectId, name, startingPoint,
                                                          display.getSelectedBranch().isRemote(),
                                                          new AsyncRequestCallback<String>() {
                                                              @Override
                                                              protected void onSuccess(String result) {
                                                                  getBranches(projectId);
                                                                  IDE.fireEvent(new RefreshBrowserEvent());
                                                                  IDE.fireEvent(new UpdateOpenedFilesEvent());
                                                              }

                                                              @Override
                                                              protected void onFailure(Throwable exception) {
                                                                  String errorMessage =
                                                                                        (exception.getMessage() != null)
                                                                                            ? exception.getMessage()
                                                                                            : GitExtension.MESSAGES
                                                                                                                   .branchCheckoutFailed();
                                                                  IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
                                                              }
                                                          });
        } catch (RequestException e) {
            String errorMessage = (e.getMessage() != null) ? e.getMessage() : GitExtension.MESSAGES.branchCheckoutFailed();
            IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
        }

    }

    /** Show ask dialog for deleting branch, if user confirms - delete branch. */
    private void askDeleteBranch() {
        final String name = display.getSelectedBranch().getName();

        Dialogs.getInstance().ask(GitExtension.MESSAGES.branchDelete(), GitExtension.MESSAGES.branchDeleteAsk(name),
                                  new BooleanValueReceivedHandler() {

                                      @Override
                                      public void booleanValueReceived(Boolean value) {
                                          if (value != null && value) {
                                              doDeleteBranch(name);
                                          }
                                      }
                                  });
    }

    private void askRenameBranch() {
        final String currentBranchName = display.getSelectedBranch().getDisplayName();
        Dialogs.getInstance().askForValue(GitExtension.MESSAGES.branchRename(), GitExtension.MESSAGES.branchRenameDescription(),
                                          currentBranchName, new StringValueReceivedHandler() {

                                              @Override
                                              public void stringValueReceived(String value) {
                                                  if (value != null) {
                                                      doRenameBranch(currentBranchName, value);
                                                  }
                                              }
                                          });
    }

    /**
     * Rename branch with pointed name.
     *
     * @param name name of branch to delete
     */
    private void doRenameBranch(String oldName, String newName) {
        final String projectId = getSelectedProject().getId();
        try {
            GitClientService.getInstance().branchRename(vfs.getId(), projectId, oldName, newName,
                                                        new AsyncRequestCallback<String>() {
                                                            @Override
                                                            protected void onSuccess(String result) {
                                                                getBranches(projectId);
                                                            }

                                                            @Override
                                                            protected void onFailure(Throwable exception) {
                                                                String errorMessage = (exception.getMessage() != null)
                                                                    ? exception.getMessage()
                                                                    : GitExtension.MESSAGES.branchRenameFailed();
                                                                IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
                                                            }
                                                        });
        } catch (RequestException e) {
            String errorMessage = (e.getMessage() != null) ? e.getMessage() : GitExtension.MESSAGES.branchRenameFailed();
            IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
        }
    }

    /**
     * Delete branch with pointed name.
     *
     * @param name name of branch to delete
     */
    private void doDeleteBranch(String name) {
        final String projectId = getSelectedProject().getId();
        try {
            GitClientService.getInstance().branchDelete(vfs.getId(), projectId, name, true,
                                                        new AsyncRequestCallback<String>() {
                                                            @Override
                                                            protected void onSuccess(String result) {
                                                                getBranches(projectId);
                                                            }

                                                            @Override
                                                            protected void onFailure(Throwable exception) {
                                                                String errorMessage =
                                                                                      (exception.getMessage() != null)
                                                                                          ? exception.getMessage()
                                                                                          : GitExtension.MESSAGES
                                                                                                                 .branchDeleteFailed();
                                                                IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
                                                            }
                                                        });
        } catch (RequestException e) {
            String errorMessage = (e.getMessage() != null) ? e.getMessage() : GitExtension.MESSAGES.branchDeleteFailed();
            IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
        }
    }

    @Override
    public void onEditorFileClosed(EditorFileClosedEvent event) {
        this.openedFiles = event.getOpenedFiles();
    }

    @Override
    public void onEditorFileOpened(EditorFileOpenedEvent event) {
        this.openedFiles = event.getOpenedFiles();
    }
}
