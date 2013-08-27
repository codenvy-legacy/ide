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
package org.exoplatform.ide.git.client.history;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.git.client.marshaller.DiffResponseUnmarshaller;
import org.exoplatform.ide.git.client.marshaller.LogResponse;
import org.exoplatform.ide.git.client.marshaller.LogResponseUnmarshaller;
import org.exoplatform.ide.git.shared.DiffRequest.DiffType;
import org.exoplatform.ide.git.shared.Revision;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.List;

/**
 * Presenter for history view. The view must be pointed in Views.gwt.xml.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 29, 2011 2:57:17 PM anya $
 */
public class HistoryPresenter extends GitPresenter implements ShowInHistoryHandler, ViewClosedHandler,
                                                  ProjectOpenedHandler {
    private enum DiffWith {
        DIFF_WITH_INDEX,
        DIFF_WITH_WORK_TREE,
        DIFF_WITH_PREV_VERSION;
    }

    interface Display extends IsView {
        /**
         * Grid with revisions (commits).
         * 
         * @return {@link ListGridItem}
         */
        ListGridItem<Revision> getRevisionGrid();

        /**
         * Get click handler of the refresh button.
         * 
         * @return {@link HasValue} click handler
         */
        HasClickHandlers getRefreshButton();

        /**
         * Get click handler of the changes in project button.
         * 
         * @return {@link HasValue} click handler
         */
        HasClickHandlers getProjectChangesButton();

        /**
         * Get click handler of the resource's changes button.
         * 
         * @return {@link HasValue} click handler
         */
        HasClickHandlers getResourceChangesButton();

        /**
         * Get click handler of the diff with index button.
         * 
         * @return {@link HasValue} click handler
         */
        HasClickHandlers getDiffWithIndexButton();

        /**
         * Get click handler of the diff with working tree button.
         * 
         * @return {@link HasValue} click handler
         */
        HasClickHandlers getDiffWithWorkingTreeButton();

        /**
         * Get click handler of the diff with previous version button.
         * 
         * @return {@link HasValue} click handler
         */
        HasClickHandlers getDiffWithPrevVersionButton();

        /**
         * Change the selected state of the changes in project button.
         * 
         * @param selected selected state
         */
        void selectProjectChangesButton(boolean selected);

        /**
         * Change the selected state of the resource changes button.
         * 
         * @param selected selected state
         */
        void selectResourceChangesButton(boolean selected);

        /**
         * Change the selected state of the diff with index button.
         * 
         * @param selected selected state
         */
        void selectDiffWithIndexButton(boolean selected);

        /**
         * Change the selected state of the diff with working tree button.
         * 
         * @param selected selected state
         */
        void selectDiffWithWorkingTreeButton(boolean selected);

        /**
         * Change the selected state of the diff with previous version button.
         * 
         * @param selected selected state
         */
        void selectDiffWithPrevVersionButton(boolean selected);

        void displayDiffContent(String diffContent);

        void displayCompareText(Revision revision, String text);

        void displayCompareVersion(Revision revisionA, Revision revisionB);
    }

    /** Presenter's display. */
    private Display  display;

    /** If <code>true</code> then show all changes in project, if <code>false</code> then show changes of the selected resource. */
    private boolean  showChangesInProject;

    private DiffWith diffType;

    /**
     * @param eventBus event handler
     */
    public HistoryPresenter() {
        IDE.addHandler(ShowInHistoryEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
    }

    /** Bind display with presenter. */
    public void bindDisplay() {
        display.getRevisionGrid().addSelectionHandler(new SelectionHandler<Revision>() {

            @Override
            public void onSelection(SelectionEvent<Revision> event) {
                if (event.getSelectedItem() != null) {
                    getDiff(event.getSelectedItem());
                }
            }
        });

        display.getRefreshButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                getCommitsLog(getSelectedProject().getId());
            }
        });

        display.getProjectChangesButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (showChangesInProject)
                    return;
                showChangesInProject = true;
                display.selectProjectChangesButton(true);
                display.selectResourceChangesButton(false);
                getCommitsLog(getSelectedProject().getId());
            }
        });

        display.getResourceChangesButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (!showChangesInProject)
                    return;
                showChangesInProject = false;
                display.selectProjectChangesButton(false);
                display.selectResourceChangesButton(true);
                getCommitsLog(getSelectedProject().getId());
            }
        });

        display.getDiffWithIndexButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (DiffWith.DIFF_WITH_INDEX.equals(diffType))
                    return;
                diffType = DiffWith.DIFF_WITH_INDEX;
                display.selectDiffWithIndexButton(true);
                display.selectDiffWithPrevVersionButton(false);
                display.selectDiffWithWorkingTreeButton(false);
                getCommitsLog(getSelectedProject().getId());
            }
        });

        display.getDiffWithWorkingTreeButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (DiffWith.DIFF_WITH_WORK_TREE.equals(diffType))
                    return;
                diffType = DiffWith.DIFF_WITH_WORK_TREE;
                display.selectDiffWithIndexButton(false);
                display.selectDiffWithPrevVersionButton(false);
                display.selectDiffWithWorkingTreeButton(true);
                getCommitsLog(getSelectedProject().getId());
            }
        });

        display.getDiffWithPrevVersionButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (DiffWith.DIFF_WITH_PREV_VERSION.equals(diffType))
                    return;
                diffType = DiffWith.DIFF_WITH_PREV_VERSION;
                display.selectDiffWithIndexButton(false);
                display.selectDiffWithPrevVersionButton(true);
                display.selectDiffWithWorkingTreeButton(false);
                getCommitsLog(getSelectedProject().getId());
            }
        });
    }

    /**
     * @see org.exoplatform.ide.git.client.history.ShowInHistoryHandler#onShowInHistory(org.exoplatform.ide.git.client.history
     *      .ShowInHistoryEvent)
     */
    @Override
    public void onShowInHistory(ShowInHistoryEvent event) {
        if (makeSelectionCheck()) {
            getCommitsLog(getSelectedProject().getId());
        }
    }

    /** Get the log of the commits. If successfully received, then display in revision grid, otherwise - show error in output panel. */
    private void getCommitsLog(String projectId) {
        try {
            GitClientService.getInstance()
                            .log(vfs.getId(), projectId, false,
                                 new AsyncRequestCallback<LogResponse>(
                                                                       new LogResponseUnmarshaller(new LogResponse(), false)) {

                                     @Override
                                     protected void onSuccess(LogResponse result) {
                                         if (display == null) {
                                             display = GWT.create(Display.class);

                                             IDE.getInstance().openView(display.asView());
                                             bindDisplay();
                                             display.selectProjectChangesButton(true);
                                             showChangesInProject = true;
                                             display.selectDiffWithPrevVersionButton(true);
                                             diffType = DiffWith.DIFF_WITH_PREV_VERSION;
                                         }
                                         display.getRevisionGrid().setValue(result.getCommits());
                                     }

                                     @Override
                                     protected void onFailure(Throwable exception) {
                                         nothingToDisplay(null);
                                         String errorMessage =
                                                               (exception.getMessage() != null) ? exception.getMessage()
                                                                   : GitExtension.MESSAGES.logFailed();
                                         IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
                                     }
                                 });
        } catch (RequestException e) {
            nothingToDisplay(null);
            String errorMessage = (e.getMessage() != null) ? e.getMessage() : GitExtension.MESSAGES.logFailed();
            IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
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

    /**
     * Get the changes between revisions. On success - display diff in text format, otherwise - show the error message in output panel.
     * 
     * @param revision revision
     */
    private void getDiff(Revision revision) {
        String[] filePatterns = null;

        ProjectModel project = getSelectedProject();
        if (!showChangesInProject && project != null && selectedItem != null) {
            String pattern = selectedItem.getPath().replaceFirst(project.getPath(), "");
            pattern = (pattern.startsWith("/")) ? pattern.replaceFirst("/", "") : pattern;
            if (pattern.length() > 0) {
                filePatterns = new String[]{pattern};
            }
        }

        if (DiffWith.DIFF_WITH_INDEX.equals(diffType) || DiffWith.DIFF_WITH_WORK_TREE.equals(diffType)) {
            boolean isCached = DiffWith.DIFF_WITH_INDEX.equals(diffType);
            doDiffWithNotCommited(filePatterns, revision, isCached);
        } else {
            doDiffWithPrevVersion(filePatterns, revision);
        }
    }

    /**
     * Perform diff between pointed revision and index or working tree.
     * 
     * @param filePatterns patterns for which to show diff
     * @param revision revision to compare with
     * @param isCached if <code>true</code> compare with index, else - with working tree
     */
    protected void doDiffWithNotCommited(String[] filePatterns, final Revision revision, final boolean isCached) {
        String projectId = getSelectedProject().getId();

        try {
            GitClientService.getInstance().diff(vfs.getId(), projectId, filePatterns, DiffType.RAW, false, 0,
                                                revision.getId(), isCached,
                                                new AsyncRequestCallback<StringBuilder>(new DiffResponseUnmarshaller(new StringBuilder())) {

                                                    @Override
                                                    protected void onSuccess(StringBuilder result) {
                                                        display.displayDiffContent(result.toString());
                                                        String text =
                                                                      (isCached) ? GitExtension.MESSAGES.historyDiffIndexState()
                                                                          : GitExtension.MESSAGES
                                                                                                 .historyDiffTreeState();
                                                        display.displayCompareText(revision, text);
                                                    }

                                                    @Override
                                                    protected void onFailure(Throwable exception) {
                                                        nothingToDisplay(revision);
                                                        String errorMessage =
                                                                              (exception.getMessage() != null) ? exception.getMessage()
                                                                                  : GitExtension.MESSAGES.diffFailed();
                                                        IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
                                                    }
                                                });
        } catch (RequestException e) {
            nothingToDisplay(revision);
            String errorMessage = (e.getMessage() != null) ? e.getMessage() : GitExtension.MESSAGES.diffFailed();
            IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
        }
    }

    /**
     * Perform diff between selected commit and previous one.
     * 
     * @param filePatterns patterns for which to show diff
     * @param revisionB selected commit
     */
    protected void doDiffWithPrevVersion(String[] filePatterns, final Revision revisionB) {
        List<Revision> revisions = display.getRevisionGrid().getValue();
        int index = revisions.indexOf(revisionB);
        if (index + 1 < revisions.size()) {
            final Revision revisionA = revisions.get(index + 1);
            String projectId = getSelectedProject().getId();

            try {
                GitClientService.getInstance()
                                .diff(vfs.getId(), projectId, filePatterns, DiffType.RAW, false, 0,
                                      revisionA.getId(), revisionB.getId(),
                                      new AsyncRequestCallback<StringBuilder>(
                                                                              new DiffResponseUnmarshaller(new StringBuilder())) {

                                          @Override
                                          protected void onSuccess(StringBuilder result) {
                                              display.displayDiffContent(result.toString());
                                              display.displayCompareVersion(revisionB, revisionA);
                                          }

                                          @Override
                                          protected void onFailure(Throwable exception) {
                                              nothingToDisplay(revisionB);
                                              String errorMessage =
                                                                    (exception.getMessage() != null) ? exception.getMessage()
                                                                        : GitExtension.MESSAGES.diffFailed();
                                              IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
                                          }
                                      });
            } catch (RequestException e) {
                nothingToDisplay(revisionB);
                String errorMessage = (e.getMessage() != null) ? e.getMessage() : GitExtension.MESSAGES.diffFailed();
                IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
            }
        } else {
            nothingToDisplay(revisionB);
        }
    }

    /**
     * Clear the comparance result, when there is nothing to compare.
     * 
     * @param revision
     */
    protected void nothingToDisplay(Revision revision) {
        display.displayCompareText(revision, GitExtension.MESSAGES.historyNothingToDisplay());
        display.displayDiffContent(" ");
    }

    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        if (display != null) {
            getCommitsLog(event.getProject().getId());
        }
    }
}
