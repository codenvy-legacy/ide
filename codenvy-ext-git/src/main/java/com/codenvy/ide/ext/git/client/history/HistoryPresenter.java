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
package com.codenvy.ide.ext.git.client.history;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.annotations.Nullable;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.parts.base.BasePresenter;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.ui.workspace.PartPresenter;
import com.codenvy.ide.api.ui.workspace.PartStackType;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.ext.git.client.GitClientService;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.GitResources;
import com.codenvy.ide.ext.git.client.marshaller.DiffResponseUnmarshaller;
import com.codenvy.ide.ext.git.client.marshaller.LogResponseUnmarshaller;
import com.codenvy.ide.ext.git.shared.LogResponse;
import com.codenvy.ide.ext.git.shared.Revision;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.js.JsoArray;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Date;

import static com.codenvy.ide.ext.git.shared.DiffRequest.DiffType.RAW;

/**
 * Presenter for showing git history.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 29, 2011 2:57:17 PM anya $
 */
@Singleton
public class HistoryPresenter extends BasePresenter implements HistoryView.ActionDelegate {
    protected enum DiffWith {
        DIFF_WITH_INDEX,
        DIFF_WITH_WORK_TREE,
        DIFF_WITH_PREV_VERSION
    }

    private HistoryView             view;
    private GitClientService        service;
    private GitLocalizationConstant constant;
    private GitResources            resources;
    private ResourceProvider        resourceProvider;
    private ConsolePart             console;
    private WorkspaceAgent          workspaceAgent;
    /** If <code>true</code> then show all changes in project, if <code>false</code> then show changes of the selected resource. */
    private boolean                 showChangesInProject;
    private DiffWith                diffType;
    private boolean isViewClosed = true;
    private JsonArray<Revision> revisions;
    private Revision            selectedRevision;
    private SelectionAgent      selectionAgent;

    /**
     * Create presenter.
     *
     * @param view
     * @param service
     * @param constant
     * @param resources
     * @param resourceProvider
     * @param console
     * @param workspaceAgent
     */
    @Inject
    public HistoryPresenter(HistoryView view, GitClientService service, GitLocalizationConstant constant, GitResources resources,
                            ResourceProvider resourceProvider, ConsolePart console, WorkspaceAgent workspaceAgent,
                            SelectionAgent selectionAgent) {
        this.view = view;
        this.view.setDelegate(this);
        this.view.setTitle(constant.historyTitle());
        this.service = service;
        this.constant = constant;
        this.resources = resources;
        this.resourceProvider = resourceProvider;
        this.console = console;
        this.workspaceAgent = workspaceAgent;
        this.selectionAgent = selectionAgent;
    }

    /** Show dialog. */
    public void showDialog() {
        Project project = resourceProvider.getActiveProject();
        getCommitsLog(project.getId());
        selectedRevision = null;

        view.selectProjectChangesButton(true);
        showChangesInProject = true;
        view.selectDiffWithPrevVersionButton(true);
        diffType = DiffWith.DIFF_WITH_PREV_VERSION;

        displayCommitA(null);
        displayCommitB(null);
        view.setDiffContext("");
        view.setCompareType(constant.historyNothingToDisplay());

        if (isViewClosed) {
            workspaceAgent.openPart(this, PartStackType.TOOLING);
            isViewClosed = false;
        }

        PartPresenter activePart = partStack.getActivePart();
        if (activePart == null || !activePart.equals(this)) {
            partStack.setActivePart(this);
        }
    }

    /** Get the log of the commits. If successfully received, then display in revision grid, otherwise - show error in output panel. */
    private void getCommitsLog(@NotNull String projectId) {
        LogResponseUnmarshaller unmarshaller = new LogResponseUnmarshaller();

        try {
            service.log(resourceProvider.getVfsId(), projectId, false, new AsyncRequestCallback<LogResponse>(unmarshaller) {
                @Override
                protected void onSuccess(LogResponse result) {
                    revisions = result.getCommits();
                    view.setRevisions(revisions);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    nothingToDisplay(null);
                    String errorMessage = exception.getMessage() != null ? exception.getMessage() : constant.logFailed();
                    console.print(errorMessage);
                }
            });
        } catch (RequestException e) {
            nothingToDisplay(null);
            String errorMessage = e.getMessage() != null ? e.getMessage() : constant.logFailed();
            console.print(errorMessage);
        }
    }

    /**
     * Clear the comparance result, when there is nothing to compare.
     *
     * @param revision
     */
    private void nothingToDisplay(@Nullable Revision revision) {
        displayCommitA(revision);
        displayCommitB(null);
        view.setCompareType(constant.historyNothingToDisplay());
        view.setDiffContext("");
    }

    /**
     * Display information about commit A.
     *
     * @param revision
     *         revision what need to display
     */
    private void displayCommitA(@Nullable Revision revision) {
        if (revision == null) {
            view.setCommitADate("");
            view.setCommitARevision("");
        } else {
            DateTimeFormat formatter = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM);
            view.setCommitADate(formatter.format(new Date((long)revision.getCommitTime())));
            view.setCommitARevision(revision.getId());
        }
    }

    /**
     * Display information about commit B.
     *
     * @param revision
     *         revision what need to display
     */
    private void displayCommitB(@Nullable Revision revision) {
        boolean isEmpty = revision == null;
        if (isEmpty) {
            view.setCommitBDate("");
            view.setCommitBRevision("");
        } else {
            DateTimeFormat formatter = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM);
            view.setCommitBDate(formatter.format(new Date((long)revision.getCommitTime())));
            view.setCommitBRevision(revision.getId());
        }
        view.setCommitBPanelVisible(!isEmpty);
    }

    /** {@inheritDoc} */
    @Override
    public void onRefreshClicked() {
        Project project = resourceProvider.getActiveProject();
        getCommitsLog(project.getId());
    }

    /** {@inheritDoc} */
    @Override
    public void onProjectChangesClicked() {
        if (showChangesInProject) {
            return;
        }
        showChangesInProject = true;
        view.selectProjectChangesButton(false);
        view.selectResourceChangesButton(true);
        update();
    }

    /** {@inheritDoc} */
    @Override
    public void onResourceChangesClicked() {
        if (!showChangesInProject) {
            return;
        }
        showChangesInProject = false;
        view.selectProjectChangesButton(false);
        view.selectResourceChangesButton(true);
        update();
    }

    /** {@inheritDoc} */
    @Override
    public void onDiffWithIndexClicked() {
        if (DiffWith.DIFF_WITH_INDEX.equals(diffType)) {
            return;
        }
        diffType = DiffWith.DIFF_WITH_INDEX;
        view.selectDiffWithIndexButton(true);
        view.selectDiffWithPrevVersionButton(false);
        view.selectDiffWithWorkingTreeButton(false);
        update();
    }

    /** {@inheritDoc} */
    @Override
    public void onDiffWithWorkTreeClicked() {
        if (DiffWith.DIFF_WITH_WORK_TREE.equals(diffType)) {
            return;
        }
        diffType = DiffWith.DIFF_WITH_WORK_TREE;
        view.selectDiffWithIndexButton(false);
        view.selectDiffWithPrevVersionButton(false);
        view.selectDiffWithWorkingTreeButton(true);
        update();
    }

    /** {@inheritDoc} */
    @Override
    public void onDiffWithPrevCommitClicked() {
        if (DiffWith.DIFF_WITH_PREV_VERSION.equals(diffType)) {
            return;
        }
        diffType = DiffWith.DIFF_WITH_PREV_VERSION;
        view.selectDiffWithIndexButton(false);
        view.selectDiffWithPrevVersionButton(true);
        view.selectDiffWithWorkingTreeButton(false);
        update();
    }

    /** {@inheritDoc} */
    @Override
    public void onRevisionSelected(@NotNull Revision revision) {
        selectedRevision = revision;
        update();
    }

    /** Update content. */
    private void update() {
        if (showChangesInProject) {
            getDiff();
        } else {
            view.setDiffContext("");
        }
        Project project = resourceProvider.getActiveProject();
        getCommitsLog(project.getId());
    }

    /** Get the changes between revisions. On success - display diff in text format, otherwise - show the error message in output panel. */
    private void getDiff() {
        JsonArray<String> filePatterns = JsoArray.create();

        Project project = resourceProvider.getActiveProject();
        if (!showChangesInProject && project != null) {
            Selection<Resource> selection = (Selection<Resource>)selectionAgent.getSelection();
            Resource element;

            if (selection == null) {
                element = project;
            } else {
                element = selection.getFirstElement();
            }

            String pattern = element.getPath().replaceFirst(project.getPath(), "");
            pattern = (pattern.startsWith("/")) ? pattern.replaceFirst("/", "") : pattern;
            if (pattern.length() > 0) {
                filePatterns.add(pattern);
            }
        }

        if (DiffWith.DIFF_WITH_INDEX.equals(diffType) || DiffWith.DIFF_WITH_WORK_TREE.equals(diffType)) {
            boolean isCached = DiffWith.DIFF_WITH_INDEX.equals(diffType);
            doDiffWithNotCommited(filePatterns, selectedRevision, isCached);
        } else {
            doDiffWithPrevVersion(filePatterns, selectedRevision);
        }
    }

    /**
     * Perform diff between pointed revision and index or working tree.
     *
     * @param filePatterns
     *         patterns for which to show diff
     * @param revision
     *         revision to compare with
     * @param isCached
     *         if <code>true</code> compare with index, else - with working tree
     */
    private void doDiffWithNotCommited(@NotNull JsonArray<String> filePatterns, @Nullable final Revision revision, final boolean isCached) {
        if (revision == null) {
            return;
        }

        String projectId = resourceProvider.getActiveProject().getId();
        DiffResponseUnmarshaller unmarshaller = new DiffResponseUnmarshaller();

        try {
            service.diff(resourceProvider.getVfsId(), projectId, filePatterns, RAW, false, 0, revision.getId(), isCached,
                         new AsyncRequestCallback<String>(unmarshaller) {
                             @Override
                             protected void onSuccess(String result) {
                                 view.setDiffContext(result);
                                 String text = isCached ? constant.historyDiffIndexState() : constant.historyDiffTreeState();
                                 displayCommitA(revision);
                                 view.setCompareType(text);
                             }

                             @Override
                             protected void onFailure(Throwable exception) {
                                 nothingToDisplay(revision);
                                 String errorMessage = exception.getMessage() != null ? exception.getMessage() : constant.diffFailed();
                                 console.print(errorMessage);
                             }
                         });
        } catch (RequestException e) {
            nothingToDisplay(revision);
            String errorMessage = e.getMessage() != null ? e.getMessage() : constant.diffFailed();
            console.print(errorMessage);
        }
    }

    /**
     * Perform diff between selected commit and previous one.
     *
     * @param filePatterns
     *         patterns for which to show diff
     * @param revisionB
     *         selected commit
     */
    private void doDiffWithPrevVersion(@NotNull JsonArray<String> filePatterns, @Nullable final Revision revisionB) {
        if (revisionB == null) {
            return;
        }

        int index = revisions.indexOf(revisionB);
        if (index + 1 < revisions.size()) {
            final Revision revisionA = revisions.get(index + 1);
            String projectId = resourceProvider.getActiveProject().getId();
            DiffResponseUnmarshaller unmarshaller = new DiffResponseUnmarshaller();

            try {
                service.diff(resourceProvider.getVfsId(), projectId, filePatterns, RAW, false, 0, revisionA.getId(), revisionB.getId(),
                             new AsyncRequestCallback<String>(unmarshaller) {
                                 @Override
                                 protected void onSuccess(String result) {
                                     view.setDiffContext(result);
                                     displayCommitA(revisionA);
                                     displayCommitB(revisionB);
                                 }

                                 @Override
                                 protected void onFailure(Throwable exception) {
                                     nothingToDisplay(revisionB);
                                     String errorMessage = exception.getMessage() != null ? exception.getMessage() : constant.diffFailed();
                                     console.print(errorMessage);
                                 }
                             });
            } catch (RequestException e) {
                nothingToDisplay(revisionB);
                String errorMessage = e.getMessage() != null ? e.getMessage() : constant.diffFailed();
                console.print(errorMessage);
            }
        } else {
            nothingToDisplay(revisionB);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getTitle() {
        return constant.historyTitle();
    }

    /** {@inheritDoc} */
    @Override
    public ImageResource getTitleImage() {
        return resources.history();
    }

    /** {@inheritDoc} */
    @Override
    public String getTitleToolTip() {
        return constant.historyTitle();
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

    /** {@inheritDoc} */
    @Override
    public int getSize() {
        return 450;
    }
}