/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.git.client.history;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.parts.PartStackUIResources;
import com.codenvy.ide.api.parts.base.BaseView;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.GitResources;
import com.codenvy.ide.ext.git.shared.Revision;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The implementation of {@link HistoryView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class HistoryViewImpl extends BaseView<HistoryView.ActionDelegate> implements HistoryView {
    interface HistoryViewImplUiBinder extends UiBinder<Widget, HistoryViewImpl> {
    }

    private static HistoryViewImplUiBinder ourUiBinder = GWT.create(HistoryViewImplUiBinder.class);

    @UiField
    DockLayoutPanel           dataCommitBPanel;
    @UiField
    DockLayoutPanel           revisionCommitBPanel;
    @UiField
    HTML                      compareType;
    @UiField
    TextBox                   commitARevision;
    @UiField
    TextBox                   commitADate;
    @UiField
    TextBox                   commitBRevision;
    @UiField
    TextBox                   commitBDate;
    @UiField
    TextArea                  editor;
    @UiField(provided = true)
    CellTable<Revision>       commits;
    @UiField
    Button                    btnRefresh;
    @UiField
    Button btnProjectChanges;
    @UiField
    Button btnResourceChanges;
    @UiField
    Button btnDiffWithIndex;
    @UiField
    Button btnDiffWithWorkTree;
    @UiField
    Button btnDiffWithPrevCommit;
    @UiField(provided = true)
    final GitResources            res;
    @UiField(provided = true)
    final GitLocalizationConstant locale;

    /**
     * Create view.
     *
     * @param resources
     * @param locale
     * @param partStackUIResources
     */
    @Inject
    protected HistoryViewImpl(GitResources resources, GitLocalizationConstant locale, PartStackUIResources partStackUIResources, Resources res) {
        super(partStackUIResources);

        this.res = resources;
        this.locale = locale;

        createCommitsTable(res);

        container.add(ourUiBinder.createAndBindUi(this));
        minimizeButton.ensureDebugId("git-showHistory-minimizeBut");

        btnProjectChanges.getElement().appendChild(new Image(resources.projectLevel()).getElement());
        btnResourceChanges.getElement().appendChild(new Image(resources.resourceLevel()).getElement());
        btnDiffWithIndex.getElement().appendChild(new Image(resources.diffIndex()).getElement());
        btnDiffWithWorkTree.getElement().appendChild(new Image(resources.diffWorkTree()).getElement());
        btnDiffWithPrevCommit.getElement().appendChild(new Image(resources.diffPrevVersion()).getElement());
        btnRefresh.getElement().appendChild(new Image(resources.refresh()).getElement());
    }

    /** Creates table what contains list of available commits.
     * @param res*/
    private void createCommitsTable(Resources res) {
        commits = new CellTable<Revision>(15, res);

        Column<Revision, String> dateColumn = new Column<Revision, String>(new TextCell()) {
            @Override
            public String getValue(Revision revision) {
                return DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM).format(
                        new Date((long)revision.getCommitTime()));
            }
            @Override
            public void render(Cell.Context context, Revision revision, SafeHtmlBuilder sb) {
                sb.appendHtmlConstant("<div id=\"" + UIObject.DEBUG_ID_PREFIX + "git-showHistory-table-" + context.getIndex() + "\">");
                super.render(context, revision, sb);
            }
        };
        Column<Revision, String> commiterColumn = new Column<Revision, String>(new TextCell()) {
            @Override
            public String getValue(Revision revision) {
                if (revision.getCommitter() == null) {
                    return "";
                }
                return revision.getCommitter().getName();
            }

        };
        Column<Revision, String> commentColumn = new Column<Revision, String>(new TextCell()) {
            @Override
            public String getValue(Revision revision) {
                return revision.getMessage();
            }
        };

        commits.addColumn(dateColumn, locale.commitGridDate());
        commits.setColumnWidth(dateColumn, "20%");
        commits.addColumn(commiterColumn, locale.commitGridCommiter());
        commits.setColumnWidth(commiterColumn, "30%");
        commits.addColumn(commentColumn, locale.commitGridComment());
        commits.setColumnWidth(commentColumn, "50%");

        final SingleSelectionModel<Revision> selectionModel = new SingleSelectionModel<Revision>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                Revision selectedObject = selectionModel.getSelectedObject();
                delegate.onRevisionSelected(selectedObject);
            }
        });
        commits.setSelectionModel(selectionModel);
    }

    /** {@inheritDoc} */
    @Override
    public void setRevisions(@NotNull Array<Revision> revisions) {
        // Wraps Array in java.util.List
        List<Revision> list = new ArrayList<Revision>();
        for (int i = 0; i < revisions.size(); i++) {
            list.add(revisions.get(i));
        }
        this.commits.setRowData(list);
    }

    /** {@inheritDoc} */
    @Override
    public void selectProjectChangesButton(boolean selected) {
        btnProjectChanges.setEnabled(!selected);
    }

    /** {@inheritDoc} */
    @Override
    public void selectResourceChangesButton(boolean selected) {
        btnResourceChanges.setEnabled(!selected);
    }

    /** {@inheritDoc} */
    @Override
    public void selectDiffWithIndexButton(boolean selected) {
        btnDiffWithIndex.setEnabled(!selected);
    }

    /** {@inheritDoc} */
    @Override
    public void selectDiffWithWorkingTreeButton(boolean selected) {
        btnDiffWithWorkTree.setEnabled(!selected);
    }

    /** {@inheritDoc} */
    @Override
    public void selectDiffWithPrevVersionButton(boolean selected) {
        btnDiffWithPrevCommit.setEnabled(!selected);
    }

    /** {@inheritDoc} */
    @Override
    public void setCommitADate(@NotNull String date) {
        commitADate.setText(date);
    }

    /** {@inheritDoc} */
    @Override
    public void setCommitBDate(@NotNull String date) {
        commitBDate.setText(date);
    }

    /** {@inheritDoc} */
    @Override
    public void setCommitARevision(@NotNull String revision) {
        commitARevision.setText(revision);
    }

    /** {@inheritDoc} */
    @Override
    public void setCommitBRevision(@NotNull String revision) {
        commitBRevision.setText(revision);
    }

    /** {@inheritDoc} */
    @Override
    public void setCompareType(@NotNull String type) {
        compareType.setHTML(type);
    }

    /** {@inheritDoc} */
    @Override
    public void setDiffContext(@NotNull String diffContext) {
        editor.setText(diffContext);
    }

    /** {@inheritDoc} */
    @Override
    public void setCommitBPanelVisible(boolean visible) {
        revisionCommitBPanel.setVisible(visible);
        dataCommitBPanel.setVisible(visible);
    }

    /** {@inheritDoc} */
    @Override
    public void clear() {
        dataCommitBPanel.clear();
        revisionCommitBPanel.clear();
        commitARevision.setText("");
        commitBRevision.setText("");
        commitADate.setText("");
        commitBDate.setText("");
        compareType.setText("");
        editor.setText("");

        List<Revision> list = new ArrayList<Revision>();
        commits.setRowData(list);
    }

    @UiHandler("btnRefresh")
    public void onRefreshClicked(ClickEvent event) {
        delegate.onRefreshClicked();
    }

    @UiHandler("btnProjectChanges")
    public void onProjectChangesClick(ClickEvent event) {
        delegate.onProjectChangesClicked();
    }

    @UiHandler("btnResourceChanges")
    public void onResourceChangesClicked(ClickEvent event) {
        delegate.onResourceChangesClicked();
    }

    @UiHandler("btnDiffWithIndex")
    public void onDiffWithIndexClicked(ClickEvent event) {
        delegate.onDiffWithIndexClicked();
    }

    @UiHandler("btnDiffWithWorkTree")
    public void onDiffWithWorkTreeClicked(ClickEvent event) {
        delegate.onDiffWithWorkTreeClicked();
    }

    @UiHandler("btnDiffWithPrevCommit")
    public void onDiffWithPrevCommitClicked(ClickEvent event) {
        delegate.onDiffWithPrevCommitClicked();
    }
}