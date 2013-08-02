/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.ext.git.client.history;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.ext.git.client.GitResources;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.shared.Revision;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.part.PartStackUIResources;
import com.codenvy.ide.part.base.BaseView;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;
import com.google.inject.Singleton;

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
    com.codenvy.ide.ui.Button btnRefresh;
    @UiField
    com.codenvy.ide.ui.Button btnProjectChanges;
    @UiField
    com.codenvy.ide.ui.Button btnResourceChanges;
    @UiField
    com.codenvy.ide.ui.Button btnDiffWithIndex;
    @UiField
    com.codenvy.ide.ui.Button btnDiffWithWorkTree;
    @UiField
    com.codenvy.ide.ui.Button btnDiffWithPrevCommit;
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
    protected HistoryViewImpl(GitResources resources, GitLocalizationConstant locale, PartStackUIResources partStackUIResources) {
        super(partStackUIResources);

        this.res = resources;
        this.locale = locale;

        createCommitsTable();

        container.add(ourUiBinder.createAndBindUi(this));
    }

    /** Creates table what contains list of available commits. */
    private void createCommitsTable() {
        commits = new CellTable<Revision>();

        Column<Revision, String> dateColumn = new Column<Revision, String>(new TextCell()) {
            @Override
            public String getValue(Revision revision) {
                return DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM).format(
                        new Date((long)revision.getCommitTime()));
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
    public void setRevisions(@NotNull JsonArray<Revision> revisions) {
        // Wraps JsonArray in java.util.List
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