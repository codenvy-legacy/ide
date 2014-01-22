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
package org.exoplatform.ide.git.client.merge;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.git.client.editor.UpdateOpenedFilesEvent;
import org.exoplatform.ide.git.client.marshaller.BranchListUnmarshaller;
import org.exoplatform.ide.git.client.marshaller.Merge;
import org.exoplatform.ide.git.client.marshaller.MergeUnmarshaller;
import org.exoplatform.ide.git.client.merge.Reference.RefType;
import org.exoplatform.ide.git.shared.Branch;
import org.exoplatform.ide.git.shared.BranchListRequest;
import org.exoplatform.ide.git.shared.MergeResult;
import org.exoplatform.ide.vfs.client.model.FileModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Presenter to perform merge reference with current HEAD commit.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jul 20, 2011 12:38:39 PM anya $
 */
public class MergePresenter extends GitPresenter implements MergeHandler, ViewClosedHandler, EditorFileOpenedHandler,
                                                            EditorFileClosedHandler {

    interface Display extends IsView {
        HasClickHandlers getMergeButton();

        HasClickHandlers getCancelButton();

        TreeGridItem<Reference> getRefTree();

        void enableMergeButton(boolean enable);

        Reference getSelectedReference();
    }

    private Display display;

    private Map<String, FileModel> openedFiles = new HashMap<String, FileModel>();

    /**
     *
     */
    public MergePresenter() {
        IDE.addHandler(MergeEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(EditorFileOpenedEvent.TYPE, this);
        IDE.addHandler(EditorFileClosedEvent.TYPE, this);
    }

    @Override
    public void onEditorFileClosed(EditorFileClosedEvent event) {
        this.openedFiles = event.getOpenedFiles();
    }

    @Override
    public void onEditorFileOpened(EditorFileOpenedEvent event) {
        this.openedFiles = event.getOpenedFiles();
    }

    /** Bind display with presenter. */
    public void bindDisplay() {
        display.getCancelButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getMergeButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                for (Map.Entry<String, FileModel> openedFile : openedFiles.entrySet()) {
                    if (openedFile.getValue().isContentChanged()) {
                        Dialogs.getInstance().showInfo(GitExtension.MESSAGES.fileUnsaved(openedFile.getValue().getName()));
                        return;
                    }
                }

                doMerge();
            }
        });

        display.getRefTree().addSelectionHandler(new SelectionHandler<Reference>() {

            @Override
            public void onSelection(SelectionEvent<Reference> event) {
                boolean enabled = (event.getSelectedItem() != null && event.getSelectedItem().getRefType() != null);
                display.enableMergeButton(enabled);
            }
        });
    }

    /** @see org.exoplatform.ide.git.client.merge.MergeHandler#onMerge(org.exoplatform.ide.git.client.merge.MergeEvent) */
    @Override
    public void onMerge(MergeEvent event) {
        if (makeSelectionCheck()) {
            String projectId = getSelectedProject().getId();
            if (display == null) {
                display = GWT.create(Display.class);
                bindDisplay();
                IDE.getInstance().openView(display.asView());
                display.enableMergeButton(false);
            }

            try {
                GitClientService.getInstance()
                                .branchList(vfs.getId(), projectId, BranchListRequest.LIST_LOCAL,
                                            new AsyncRequestCallback<List<Branch>>(
                                                    new BranchListUnmarshaller(new ArrayList<Branch>())) {

                                                @Override
                                                protected void onSuccess(List<Branch> result) {
                                                    if (result == null || result.size() == 0)
                                                        return;
                                                    setReferences(result, true);
                                                }

                                                @Override
                                                protected void onFailure(Throwable exception) {
                                                    IDE.fireEvent(new ExceptionThrownEvent(exception));
                                                }
                                            });
            } catch (RequestException e) {
                IDE.fireEvent(new ExceptionThrownEvent(e));
            }

            try {
                GitClientService.getInstance()
                                .branchList(vfs.getId(), projectId, BranchListRequest.LIST_REMOTE,
                                            new AsyncRequestCallback<List<Branch>>(
                                                    new BranchListUnmarshaller(new ArrayList<Branch>())) {

                                                @Override
                                                protected void onSuccess(List<Branch> result) {
                                                    if (result == null || result.size() == 0)
                                                        return;
                                                    setReferences(result, false);
                                                }

                                                @Override
                                                protected void onFailure(Throwable exception) {
                                                    IDE.fireEvent(new ExceptionThrownEvent(exception));
                                                }
                                            });
            } catch (RequestException e) {
                IDE.fireEvent(new ExceptionThrownEvent(e));
            }
        }
    }

    /**
     * Set references values.
     * 
     * @param branches list of branches
     * @param isLocal if <code>true</code> then list of local branches is provided
     */
    public void setReferences(List<Branch> branches, boolean isLocal) {
        for (Branch branch : branches) {
            if (!branch.isActive()) {
                if (isLocal) {
                    display.getRefTree().setValue(
                                                  new Reference(branch.getName(), branch.getDisplayName(), RefType.LOCAL_BRANCH));
                } else {
                    display.getRefTree().setValue(
                                                  new Reference(branch.getName(), branch.getDisplayName(), RefType.REMOTE_BRANCH));
                }
            }
        }
    }

    /**
     * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     *      .event.ViewClosedEvent)
     */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    /** Perform merge. */
    public void doMerge() {
        Reference reference = display.getSelectedReference();
        if (reference == null) {
            return;
        }

        String projectId = getSelectedProject().getId();

        try {
            GitClientService.getInstance().merge(vfs.getId(), projectId, reference.getDisplayName(),
                                                 new AsyncRequestCallback<MergeResult>(new MergeUnmarshaller(new Merge())) {

                                                     @Override
                                                     protected void onSuccess(MergeResult result) {
                                                         IDE.fireEvent(new OutputEvent(formMergeMessage(result), Type.GIT));
                                                         IDE.getInstance().closeView(display.asView().getId());
                                                         IDE.fireEvent(new RefreshBrowserEvent());
                                                         IDE.fireEvent(new UpdateOpenedFilesEvent());
                                                     }

                                                     @Override
                                                     protected void onFailure(Throwable exception) {
                                                         IDE.fireEvent(new ExceptionThrownEvent(exception));
                                                     }
                                                 });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /**
     * Form the result message of the merge operation.
     * 
     * @param mergeResult
     * @return {@link String} merge result message
     */
    private String formMergeMessage(MergeResult mergeResult) {
        if (mergeResult.getMergeStatus().equals(MergeResult.MergeStatus.ALREADY_UP_TO_DATE)) {
            return mergeResult.getMergeStatus().toString();
        }
        String conflicts = "";
        if (mergeResult.getConflicts() != null && mergeResult.getConflicts().length > 0) {
            for (String conflict : mergeResult.getConflicts()) {
                conflicts += "- " + conflict + "<br>";
            }
        }
        String commits = "";
        if (mergeResult.getMergedCommits() != null && mergeResult.getMergedCommits().length > 0) {
            for (String commit : mergeResult.getMergedCommits()) {
                commits += "- " + commit + "<br>";
            }
        }

        String message = "<b>" + mergeResult.getMergeStatus().toString() + "</b><br/>";
        message += (conflicts.length() > 0) ? GitExtension.MESSAGES.mergedConflicts(conflicts) : "";
        message += (commits.length() > 0) ? GitExtension.MESSAGES.mergedCommits(commits) : "";
        message +=
                   (mergeResult.getNewHead() != null) ? GitExtension.MESSAGES.mergedNewHead(mergeResult.getNewHead()) : "";
        return message;
    }
}