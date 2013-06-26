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
package com.codenvy.ide.ext.git.client.reset;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.ext.git.client.GitClientResources;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.shared.Revision;
import com.codenvy.ide.json.JsonArray;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The implementation of {@link ResetToCommitView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class ResetToCommitViewImpl extends DialogBox implements ResetToCommitView {
    interface ResetToCommitViewImplUiBinder extends UiBinder<Widget, ResetToCommitViewImpl> {
    }

    private static ResetToCommitViewImplUiBinder ourUiBinder = GWT.create(ResetToCommitViewImplUiBinder.class);

    @UiField
    RadioButton               soft;
    @UiField
    RadioButton               mixed;
    @UiField
    RadioButton               hard;
    @UiField
    RadioButton               keep;
    @UiField
    RadioButton               merge;
    @UiField
    com.codenvy.ide.ui.Button btnReset;
    @UiField
    com.codenvy.ide.ui.Button btnCancel;
    @UiField(provided = true)
    CellTable<Revision>       commits;
    @UiField(provided = true)
    final   GitClientResources      res;
    @UiField(provided = true)
    final   GitLocalizationConstant locale;
    private ActionDelegate          delegate;

    /**
     * Create view.
     *
     * @param resources
     * @param locale
     */
    @Inject
    protected ResetToCommitViewImpl(GitClientResources resources, GitLocalizationConstant locale) {
        this.res = resources;
        this.locale = locale;

        createCommitsTable();

        Widget widget = ourUiBinder.createAndBindUi(this);

        this.setText(locale.resetCommitViewTitle());
        this.setWidget(widget);

        prepareRadioButtons();
    }

    /** Add description to buttons. */
    private void prepareRadioButtons() {
        addDescription(soft, locale.resetSoftTypeDescription());
        addDescription(mixed, locale.resetMixedTypeDescription());
        addDescription(hard, locale.resetHardTypeDescription());
        addDescription(keep, locale.resetKeepTypeDescription());
        addDescription(merge, locale.resetMergeTypeDescription());
    }

    /**
     * Add description to radio button title.
     *
     * @param radioItem
     *         radio button
     * @param description
     *         description to add
     */
    private void addDescription(RadioButton radioItem, String description) {
        Element descElement = DOM.createSpan();
        descElement.setInnerText(description);
        DOM.setStyleAttribute(descElement, "color", "#555");
        radioItem.getElement().appendChild(descElement);
    }

    /** Creates table what contains list of available commits. */
    private void createCommitsTable() {
        commits = new CellTable<Revision>();

        Column<Revision, String> dateColumn = new Column<Revision, String>(new TextCell()) {
            @Override
            public String getValue(Revision revision) {
                return DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM).format(
                        new Date(revision.getCommitTime()));
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
    public boolean isSoftMode() {
        return soft.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public void setSoftMode(boolean isSoft) {
        soft.setValue(isSoft);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isMixMode() {
        return mixed.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public void setMixMode(boolean isMix) {
        mixed.setValue(isMix);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isHardMode() {
        return hard.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public void setHardMode(boolean isHard) {
        hard.setValue(isHard);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isKeepMode() {
        return keep.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public void setKeepMode(boolean isKeep) {
        keep.setValue(isKeep);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isMergeMode() {
        return merge.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public void setMergeMode(boolean isMerge) {
        merge.setValue(isMerge);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableResetButton(boolean enabled) {
        btnReset.setEnabled(enabled);
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        this.center();
        this.show();
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @UiHandler("btnReset")
    public void onResetClicked(ClickEvent event) {
        delegate.onResetClicked();
    }

    @UiHandler("btnCancel")
    public void onCancelClicked(ClickEvent event) {
        delegate.onCancelClicked();
    }
}