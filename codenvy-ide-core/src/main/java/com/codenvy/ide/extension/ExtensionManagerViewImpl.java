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
package com.codenvy.ide.extension;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.toolbar.ToolbarPresenter;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.List;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
@Singleton
public class ExtensionManagerViewImpl implements ExtensionManagerView {
    private static ExtensionManagerViewImplUiBinder ourUiBinder = GWT.create(ExtensionManagerViewImplUiBinder.class);
    private final DockLayoutPanel rootElement;
    @UiField
    DataGrid<ExtensionDescription> dataGrid;
    @UiField
    TextAreaElement                descriptionArea;
    @UiField
    SimplePanel                    toolBarPanel;
    private ActionDelegate delegate;

    @Inject
    public ExtensionManagerViewImpl(ToolbarPresenter toolbarPresenter, ActionManager actionManager, Resources resources) {
        rootElement = ourUiBinder.createAndBindUi(this);
        toolbarPresenter.go(toolBarPanel);
        DefaultActionGroup actionGroup = new DefaultActionGroup("extensionManager", false, actionManager);
        actionManager.registerAction("extensionManagerGroup", actionGroup);
        SortByStatusAction sortByStatusAction = new SortByStatusAction(this, resources);
        actionManager.registerAction("extensionManagerSortByStatus", sortByStatusAction);
        actionGroup.add(sortByStatusAction);
        toolbarPresenter.bindMainGroup(actionGroup);

        CheckboxCell checkboxCell = new CheckboxCell(false, false);
        Column<ExtensionDescription, Boolean> enabledColumn = new Column<ExtensionDescription, Boolean>(checkboxCell) {
            @Override
            public Boolean getValue(ExtensionDescription object) {
                return object.isEnabled();
            }
        };

        enabledColumn.setFieldUpdater(new FieldUpdater<ExtensionDescription, Boolean>() {
            @Override
            public void update(int index, ExtensionDescription object, Boolean value) {
                object.setEnabled(value);
                delegate.setDirty();
            }
        });
        dataGrid.addColumn(enabledColumn);
        dataGrid.setColumnWidth(enabledColumn, 40, Style.Unit.PX);

        Column<ExtensionDescription, String> titleColumn = new Column<ExtensionDescription, String>(new TextCell()) {
            @Override
            public String getValue(ExtensionDescription object) {
                return object.getTitle();
            }
        };
        dataGrid.addColumn(titleColumn);
        SingleSelectionModel<ExtensionDescription> selectionModel = new SingleSelectionModel<ExtensionDescription>();
        dataGrid.setSelectionModel(selectionModel);
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {

            }
        });

    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public Widget asWidget() {
        return rootElement;
    }

    public void sortByStatus(boolean state) {
        Log.info(getClass(), "Not implemented yet!");
    }

    /** {@inheritDoc} */
    @Override
    public void setExtensions(List<ExtensionDescription> extensions) {
        dataGrid.setRowData(extensions);
        dataGrid.redraw();
    }

    interface ExtensionManagerViewImplUiBinder
            extends UiBinder<DockLayoutPanel, ExtensionManagerViewImpl> {
    }
}