package com.codenvy.ide.ext.gae.client.project.backend;

import com.codenvy.ide.ext.gae.client.GAELocalization;
import com.codenvy.ide.ext.gae.shared.Backend;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.ui.Button;
import com.google.gwt.cell.client.*;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladyslav Zhukovskii</a>
 * @version $Id: 05.08.13 vlad $
 */
@Singleton
public class BackendTabPaneViewImpl extends Composite implements BackendTabPaneView {
    interface BackendTabPaneViewImplUiBinder extends UiBinder<Widget, BackendTabPaneViewImpl> {
    }

    private static BackendTabPaneViewImplUiBinder uiBinder = GWT.create(BackendTabPaneViewImplUiBinder.class);

    @UiField(provided = true)
    CellTable<Backend> backendsTable = new CellTable<Backend>();

    @UiField
    Button btnConfigure;

    @UiField
    Button btnDelete;

    @UiField
    Button btnUpdate;

    @UiField
    Button btnRollBack;

    @UiField
    Button btnUpdateAll;

    @UiField
    Button btnRollBackAll;

    @UiField(provided = true)
    GAELocalization constant;

    private ActionDelegate delegate;

    private Backend backend;

    @Inject
    public BackendTabPaneViewImpl(GAELocalization constant) {
        this.constant = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        initBackendsTable();

        initWidget(widget);
    }

    private void initBackendsTable() {
        backendsTable.setWidth("100%", true);
        backendsTable.setAutoHeaderRefreshDisabled(true);
        backendsTable.setAutoFooterRefreshDisabled(true);

        HTMLPanel emptyPanel = new HTMLPanel("No backends found.");
        backendsTable.setEmptyTableWidget(emptyPanel);

        final SelectionModel<Backend> selectionModel = new SingleSelectionModel<Backend>();
        backendsTable.setSelectionModel(selectionModel);

        Column<Backend, String> nameColumn = new Column<Backend, String>(new TextCell()) {
            @Override
            public String getValue(Backend backend) {
                return backend.getName();
            }
        };

        Column<Backend, String> stateColumn = new Column<Backend, String>(new ButtonCell()) {
            @Override
            public String getValue(Backend object) {
                return Backend.State.START.equals(object.getState()) ? "Stop" : "Start";
            }
        };

        Column<Backend, String> classColumn = new Column<Backend, String>(new TextCell()) {
            @Override
            public String getValue(Backend object) {
                return object.getInstanceClass();
            }
        };

        Column<Backend, Number> instanceColumn = new Column<Backend, Number>(new NumberCell()) {
            @Override
            public Number getValue(Backend object) {
                return object.getInstances();
            }
        };

        Column<Backend, SafeHtml> dynamicColumn = new Column<Backend, SafeHtml>(new SafeHtmlCell()) {

            @Override
            public SafeHtml getValue(final Backend object) {
                return new SafeHtml() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public String asString() {
                        if (object.isDynamic()) {
                            return "<input type=\"checkbox\" checked=checked readonly=\"true\"/>";
                        } else {
                            return "<input type=\"checkbox\" readonly=\"true\"/>";
                        }
                    }
                };
            }
        };

        Column<Backend, SafeHtml> publicColumn = new Column<Backend, SafeHtml>(new SafeHtmlCell()) {

            @Override
            public SafeHtml getValue(final Backend object) {
                return new SafeHtml() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public String asString() {
                        if (object.isPublic()) {
                            return "<input type=\"checkbox\" checked=checked readonly=\"true\"/>";
                        } else {
                            return "<input type=\"checkbox\" readonly=\"true\"/>";
                        }
                    }
                };
            }
        };

        backendsTable.addColumn(nameColumn, constant.backendNameTitle());
        backendsTable.setColumnWidth(nameColumn, "25%");

        backendsTable.addColumn(stateColumn, constant.backendStateTitle());
        backendsTable.setColumnWidth(stateColumn, "15%");
        stateColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        backendsTable.addColumn(classColumn, constant.backendClassTitle());
        backendsTable.setColumnWidth(classColumn, "15%");
        classColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        backendsTable.addColumn(instanceColumn, constant.backendInstancesTitle());
        backendsTable.setColumnWidth(instanceColumn, "15%");
        instanceColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        backendsTable.addColumn(dynamicColumn, constant.backendDynamicTitle());
        backendsTable.setColumnWidth(dynamicColumn, "15%");
        dynamicColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        backendsTable.addColumn(publicColumn, constant.backendPublicTitle());
        backendsTable.setColumnWidth(publicColumn, "15%");
        publicColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                backend = ((SingleSelectionModel<Backend>)selectionModel).getSelectedObject();
                setEnableUpdateButtons(backend != null);
            }
        });

        stateColumn.setFieldUpdater(new FieldUpdater<Backend, String>() {
            @Override
            public void update(int index, Backend object, String value) {
                delegate.onUpdateBackendState(object.getName(),
                                              Backend.State.START.equals(object.getState()) ? Backend.State.STOP
                                                                                            : Backend.State.START);
            }
        });
    }

    @Override
    public void setEnableUpdateButtons(boolean enable) {
        btnConfigure.setEnabled(enable);
        btnDelete.setEnabled(enable);
        btnUpdate.setEnabled(enable);
        btnRollBack.setEnabled(enable);
    }

    @Override
    public void setBackendsList(JsonArray<Backend> backends) {
        List<Backend> backendList = new ArrayList<Backend>(backends.size());
        for (int i = 0; i < backends.size(); i++) {
            backendList.add(backends.get(i));
        }

        backendsTable.setRowData(backendList);
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public Backend getSelectedBackend() {
        return backend;
    }

    @UiHandler("btnConfigure")
    public void onConfigureBackendClicked(ClickEvent event) {
        delegate.onConfigureBackendClicked();
    }

    @UiHandler("btnDelete")
    public void onDeleteBackendClicked(ClickEvent event) {
        delegate.onDeleteBackendClicked();
    }

    @UiHandler("btnUpdate")
    public void onUpdateBackendClicked(ClickEvent event) {
        delegate.onUpdateBackendClicked();
    }

    @UiHandler("btnRollBack")
    public void onRollBackBackendClicked(ClickEvent event) {
        delegate.onRollBackBackendClicked();
    }

    @UiHandler("btnUpdateAll")
    public void onUpdateAllBackendsClicked(ClickEvent event) {
        delegate.onUpdateAllBackendsClicked();
    }

    @UiHandler("btnRollBackAll")
    public void onRollBackAllBackendsClicked(ClickEvent event) {
        delegate.onRollBackAllBackendsClicked();
    }
}
