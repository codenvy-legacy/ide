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
package com.codenvy.ide.ext.git.client.reset.commit;

import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.GitResources;
import com.codenvy.ide.ext.git.shared.Revision;
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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.RadioButton;
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
    RadioButton         soft;
    @UiField
    RadioButton         mixed;
    @UiField
    RadioButton         hard;
    @UiField
    RadioButton         keep;
    @UiField
    RadioButton         merge;
    @UiField
    Button              btnReset;
    @UiField
    Button              btnCancel;
    @UiField(provided = true)
    CellTable<Revision> commits;
    @UiField(provided = true)
    final   GitResources            res;
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
    protected ResetToCommitViewImpl(GitResources resources, GitLocalizationConstant locale) {
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
    public void setRevisions(@NotNull List<Revision> revisions) {
        // Wraps Array in java.util.List
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