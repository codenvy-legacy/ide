/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.project.properties;

import elemental.html.TableElement;

import com.codenvy.ide.Resources;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.util.dom.Elements;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

/**
 * UI for project's properties listing and edititng.
 * 
 * @author <a href="mailto:ashumilova@codenvy.com">Ann Shumilova</a>
 * @version $Id:
 */
public class ProjectPropertiesViewImpl extends DialogBox implements ProjectPropertiesView {
    interface ProjectPropertiesViewImplUiBinder extends UiBinder<Widget, ProjectPropertiesViewImpl> {
    }

    private static ProjectPropertiesViewImplUiBinder uiBinder = GWT.create(ProjectPropertiesViewImplUiBinder.class);

    @UiField
    com.codenvy.ide.ui.Button                        btnCancel;
    @UiField
    com.codenvy.ide.ui.Button                        btnSave;
    @UiField
    com.codenvy.ide.ui.Button                        btnDelete;
    @UiField
    com.codenvy.ide.ui.Button                        btnEdit;

    @UiField(provided = true)
    CellTable<Property>                              propertiesTable;

    @UiField(provided = true)
    Resources                                        res;
    private ActionDelegate                           delegate;
    private ProjectPropertiesLocalizationConstant    localization;


    @Inject
    protected ProjectPropertiesViewImpl(Resources resources, ProjectPropertiesLocalizationConstant localization) {
        this.res = resources;
        this.localization = localization;
        initPropertiesTable();

        Widget widget = uiBinder.createAndBindUi(this);

        TableElement tableElement = Elements.createTableElement();
        tableElement.setAttribute("style", "width: 100%");

        this.setText(localization.projectPropertiesViewTitle());
        this.setWidget(widget);

    }

    /**
     * Initialize the properties table.
     */
    private void initPropertiesTable() {
        propertiesTable = new CellTable<Property>();
        Column<Property, String> nameColumn = new Column<Property, String>(new TextCell()) {
            @Override
            public String getValue(Property object) {
                return PropertyUtil.getHumanReadableName(object.getName());
            }
        };
        nameColumn.setSortable(true);

        Column<Property, SafeHtml> valueColumn = new Column<Property, SafeHtml>(new SafeHtmlCell()) {
            @Override
            public SafeHtml getValue(Property object) {
                SafeHtmlBuilder builder = new SafeHtmlBuilder();
                for (String v : object.getValue().asIterable()) {
                    builder.appendEscaped(v).appendHtmlConstant("</br>");
                }
                return builder.toSafeHtml();
            }
        };

        propertiesTable.addColumn(nameColumn, localization.propertyNameTitle());
        propertiesTable.addColumn(valueColumn, localization.propertyValueTitle());
        propertiesTable.setColumnWidth(nameColumn, 40, Style.Unit.PCT);
        propertiesTable.setColumnWidth(valueColumn, 60, Style.Unit.PCT);

        final SingleSelectionModel<Property> selectionModel = new SingleSelectionModel<Property>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                Property selectedObject = selectionModel.getSelectedObject();
                delegate.selectedProperty(selectedObject);
            }
        });
        propertiesTable.setSelectionModel(selectionModel);
    }


    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void setEditButtonEnabled(boolean isEnabled) {
        btnEdit.setEnabled(isEnabled);
    }

    /** {@inheritDoc} */
    @Override
    public void setDeleteButtonEnabled(boolean isEnabled) {
        btnDelete.setEnabled(isEnabled);
    }

    /** {@inheritDoc} */
    @Override
    public void setSaveButtonEnabled(boolean isEnabled) {
        btnSave.setEnabled(isEnabled);
    }

    /** {@inheritDoc} */
    @Override
    public void setProperties(Array<Property> properties) {
        // Wraps Array in java.util.List
        List<Property> list = new ArrayList<Property>();
        for (Property property : properties.asIterable()) {
            list.add(property);
        }
        this.propertiesTable.setRowData(list);
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

    @UiHandler("btnCancel")
    void onBtnCancelClick(ClickEvent event) {
        delegate.onCancelClicked();
    }

    @UiHandler("btnSave")
    void onBtnOpenClick(ClickEvent event) {
        delegate.onSaveClicked();
    }

    @UiHandler("btnEdit")
    void onBtnEditClick(ClickEvent event) {
        delegate.onEditClicked();
    }

    @UiHandler("btnDelete")
    void onBtnDeleteClick(ClickEvent event) {
        delegate.onDeleteClicked();
    }
}
