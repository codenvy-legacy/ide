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
package com.codenvy.ide.ext.openshift.client.list;

import com.codenvy.ide.ext.openshift.client.OpenShiftLocalizationConstant;
import com.codenvy.ide.ext.openshift.client.info.ApplicationProperty;
import com.codenvy.ide.ext.openshift.shared.AppInfo;
import com.codenvy.ide.ext.openshift.shared.OpenShiftEmbeddableCartridge;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.ui.Button;
import com.google.gwt.cell.client.AbstractSafeHtmlCell;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.HasDirection;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

/**
 * The implementation of {@link ApplicationListView}.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class ApplicationListViewImpl extends DialogBox implements ApplicationListView {

    interface ApplicationListViewImplUiBinder extends UiBinder<Widget, ApplicationListViewImpl> {
    }

    private static ApplicationListViewImplUiBinder uiBinder = GWT.create(ApplicationListViewImplUiBinder.class);

    @UiField(provided = true)
    CellTable<AppInfo> applicationList = new CellTable<AppInfo>();

    @UiField(provided = true)
    CellTable<ApplicationProperty> applicationProperties = new CellTable<ApplicationProperty>();

    @UiField(provided = true)
    CellTable<OpenShiftEmbeddableCartridge> applicationCartridges = new CellTable<OpenShiftEmbeddableCartridge>();

    @UiField
    TextBox userLoginField;

    @UiField
    TextBox userDomainFiled;

    @UiField
    Button btnChangeAccount;

    @UiField
    Button btnChangeDomain;

    @UiField
    Button btnCreateCartridge;

    @UiField
    Button btnClose;

    @UiField(provided = true)
    final OpenShiftLocalizationConstant constant;

    private ActionDelegate delegate;

    private boolean isShown;

    private AppInfo selectedApplication;

    /**
     * Create presenter.
     *
     * @param constant
     *         locale constants
     */
    @Inject
    protected ApplicationListViewImpl(OpenShiftLocalizationConstant constant) {
        this.constant = constant;

        initApplicationListTable();
        initApplicationPropertiesTable();
        initCartridgesTable();

        Widget widget = uiBinder.createAndBindUi(this);

        this.setText(constant.applicationListViewTitle());
        this.setWidget(widget);
    }

    /** Simple cell which can display html code. */
    private class SimpleHtmlCell extends AbstractSafeHtmlCell<String> {
        /** Create Link list. */
        public SimpleHtmlCell() {
            super(new SafeHtmlListRenderer());
        }

        /** {@inheritDoc} */
        @Override
        protected void render(com.google.gwt.cell.client.Cell.Context context, SafeHtml data, SafeHtmlBuilder sb) {
            sb.append(data);
        }
    }

    /** Renderer for {@link SimpleHtmlCell}. */
    private class SafeHtmlListRenderer implements SafeHtmlRenderer<String> {
        /** {@inheritDoc} */
        @Override
        public SafeHtml render(String object) {
            return new SafeHtmlBuilder().appendHtmlConstant(object).toSafeHtml();
        }

        /** {@inheritDoc} */
        @Override
        public void render(String object, SafeHtmlBuilder builder) {
            builder.appendHtmlConstant(object);
        }
    }

    /** Initialization of application's table. */
    private void initApplicationListTable() {
        applicationList.setWidth("100%", true);
        applicationList.setAutoHeaderRefreshDisabled(true);
        applicationList.setAutoFooterRefreshDisabled(true);

        HTMLPanel panel = new HTMLPanel("No applications.");
        applicationList.setEmptyTableWidget(panel);

        final SelectionModel<AppInfo> selectionModel = new SingleSelectionModel<AppInfo>();

        applicationList.setSelectionModel(selectionModel);

        Column<AppInfo, String> appNameColumn = new TextColumn<AppInfo>() {
            @Override
            public String getValue(AppInfo object) {
                return object.getName();
            }
        };

        Column<AppInfo, String> appDeleteColumn = new Column<AppInfo, String>(new ButtonCell()) {
            @Override
            public String getValue(AppInfo object) {
                return "Delete";
            }
        };

        appDeleteColumn.setHorizontalAlignment(HasHorizontalAlignment.HorizontalAlignmentConstant.startOf(HasDirection.Direction.RTL));
        this.applicationList.getSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                selectedApplication = ((SingleSelectionModel<AppInfo>)selectionModel).getSelectedObject();
            }
        });

        appDeleteColumn.setFieldUpdater(new FieldUpdater<AppInfo, String>() {
            @Override
            public void update(int index, AppInfo object, String value) {
                delegate.onApplicationDeleteClicked(object);
            }
        });

        applicationList.addColumn(appNameColumn, "Application");
        applicationList.addColumn(appDeleteColumn, "Delete");
    }

    /** Initialization of application properties table. */
    private void initApplicationPropertiesTable() {
        applicationProperties.setWidth("100%", true);
        applicationProperties.setAutoHeaderRefreshDisabled(true);
        applicationProperties.setAutoFooterRefreshDisabled(true);

        HTMLPanel panel = new HTMLPanel("No properties.");
        applicationProperties.setEmptyTableWidget(panel);

        final SelectionModel<ApplicationProperty> selectionModel = new NoSelectionModel<ApplicationProperty>();

        applicationProperties.setSelectionModel(selectionModel);

        Column<ApplicationProperty, String> propertyKeyColumn = new TextColumn<ApplicationProperty>() {
            @Override
            public String getValue(ApplicationProperty object) {
                return object.getPropertyName();
            }
        };

        Column<ApplicationProperty, String> propertyValueColumn = new Column<ApplicationProperty, String>(new SimpleHtmlCell()) {
            @Override
            public String getValue(ApplicationProperty object) {
                return object.getPropertyValue();
            }
        };

        applicationProperties.setColumnWidth(propertyKeyColumn, "100px");

        applicationProperties.addColumn(propertyKeyColumn, "Property");
        applicationProperties.addColumn(propertyValueColumn, "Value");
    }

    /** Initialization of application's cartridges table. */
    private void initCartridgesTable() {
        applicationCartridges.setWidth("100%", true);
        applicationCartridges.setAutoHeaderRefreshDisabled(true);
        applicationCartridges.setAutoFooterRefreshDisabled(true);

        HTMLPanel panel = new HTMLPanel("No cartridges.");
        applicationCartridges.setEmptyTableWidget(panel);

        final SelectionModel<OpenShiftEmbeddableCartridge> selectionModel = new NoSelectionModel<OpenShiftEmbeddableCartridge>();

        applicationCartridges.setSelectionModel(selectionModel);

        Column<OpenShiftEmbeddableCartridge, String> cartridgeNameColumn = new TextColumn<OpenShiftEmbeddableCartridge>() {
            @Override
            public String getValue(OpenShiftEmbeddableCartridge object) {
                return object.getName();
            }
        };

        Column<OpenShiftEmbeddableCartridge, String> cartridgeStartColumn =
                new Column<OpenShiftEmbeddableCartridge, String>(new ButtonCell()) {
                    @Override
                    public String getValue(OpenShiftEmbeddableCartridge object) {
                        return "Start";
                    }
                };

        Column<OpenShiftEmbeddableCartridge, String> cartridgeStopColumn =
                new Column<OpenShiftEmbeddableCartridge, String>(new ButtonCell()) {
                    @Override
                    public String getValue(OpenShiftEmbeddableCartridge object) {
                        return "Stop";
                    }
                };

        Column<OpenShiftEmbeddableCartridge, String> cartridgeRestartColumn =
                new Column<OpenShiftEmbeddableCartridge, String>(new ButtonCell()) {
                    @Override
                    public String getValue(OpenShiftEmbeddableCartridge object) {
                        return "Restart";
                    }
                };

        Column<OpenShiftEmbeddableCartridge, String> cartridgeReloadColumn =
                new Column<OpenShiftEmbeddableCartridge, String>(new ButtonCell()) {
                    @Override
                    public String getValue(OpenShiftEmbeddableCartridge object) {
                        return "Reload";
                    }
                };

        Column<OpenShiftEmbeddableCartridge, String> cartridgeDeleteColumn =
                new Column<OpenShiftEmbeddableCartridge, String>(new ButtonCell()) {
                    @Override
                    public String getValue(OpenShiftEmbeddableCartridge object) {
                        return "Delete";
                    }
                };

        cartridgeStartColumn.setFieldUpdater(new FieldUpdater<OpenShiftEmbeddableCartridge, String>() {
            @Override
            public void update(int index, OpenShiftEmbeddableCartridge object, String value) {
                delegate.onCartridgeStartClicked(object);
            }
        });

        cartridgeStopColumn.setFieldUpdater(new FieldUpdater<OpenShiftEmbeddableCartridge, String>() {
            @Override
            public void update(int index, OpenShiftEmbeddableCartridge object, String value) {
                delegate.onCartridgeStopClicked(object);
            }
        });

        cartridgeRestartColumn.setFieldUpdater(new FieldUpdater<OpenShiftEmbeddableCartridge, String>() {
            @Override
            public void update(int index, OpenShiftEmbeddableCartridge object, String value) {
                delegate.onCartridgeRestartClicked(object);
            }
        });

        cartridgeReloadColumn.setFieldUpdater(new FieldUpdater<OpenShiftEmbeddableCartridge, String>() {
            @Override
            public void update(int index, OpenShiftEmbeddableCartridge object, String value) {
                delegate.onCartridgeReloadClicked(object);
            }
        });

        cartridgeDeleteColumn.setFieldUpdater(new FieldUpdater<OpenShiftEmbeddableCartridge, String>() {
            @Override
            public void update(int index, OpenShiftEmbeddableCartridge object, String value) {
                delegate.onCartridgeDeleteClicked(object);
            }
        });

        applicationCartridges.addColumn(cartridgeNameColumn, "Cartridge");
        applicationCartridges.addColumn(cartridgeStartColumn, "Start");
        applicationCartridges.addColumn(cartridgeStopColumn, "Stop");
        applicationCartridges.addColumn(cartridgeRestartColumn, "Restart");
        applicationCartridges.addColumn(cartridgeReloadColumn, "Reload");
        applicationCartridges.addColumn(cartridgeDeleteColumn, "Delete");
    }

    /** {@inheritDoc} */
    @Override
    public void setApplications(JsonArray<AppInfo> applications) {
        List<AppInfo> list = new ArrayList<AppInfo>();
        for (int i = 0; i < applications.size(); i++) {
            list.add(applications.get(i));
        }
        this.applicationList.setRowData(list);
        this.applicationList.getSelectionModel().setSelected(applications.get(0), true);
    }

    /** {@inheritDoc} */
    @Override
    public void setCartridges(JsonArray<OpenShiftEmbeddableCartridge> cartridges) {
        List<OpenShiftEmbeddableCartridge> list = new ArrayList<OpenShiftEmbeddableCartridge>();
        for (int i = 0; i < cartridges.size(); i++) {
            list.add(cartridges.get(i));
        }
        this.applicationCartridges.setRowData(list);
    }

    /** {@inheritDoc} */
    @Override
    public void setApplicationInfo(JsonArray<ApplicationProperty> properties) {
        List<ApplicationProperty> list = new ArrayList<ApplicationProperty>();
        for (int i = 0; i < properties.size(); i++) {
            list.add(properties.get(i));
        }
        this.applicationProperties.setRowData(list);
    }

    /** {@inheritDoc} */
    @Override
    public void setUserLogin(String userLogin) {
        this.userLoginField.setText(userLogin);
    }

    /** {@inheritDoc} */
    @Override
    public void setUserDomain(String userDomain) {
        this.userDomainFiled.setText(userDomain);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isShown() {
        return isShown;
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.isShown = false;
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        this.selectedApplication = null;
        this.isShown = true;
        this.center();
        this.show();
    }

    /** {@inheritDoc} */
    @Override
    public AppInfo getSelectedApplication() {
        return applicationList.getKeyboardSelectedRow() != -1 ? selectedApplication : null;
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /**
     * Handler for Close button.
     *
     * @param event
     */
    @UiHandler("btnClose")
    public void onCloseButtonClick(ClickEvent event) {
        delegate.onCloseClicked();
    }

    /**
     * Handler for Create cartridge button.
     *
     * @param event
     */
    @UiHandler("btnCreateCartridge")
    public void onCreateCartridgeClicked(ClickEvent event) {
        delegate.onCreateCartridgeClicked();
    }

    /**
     * Handler for Change account button.
     *
     * @param event
     */
    @UiHandler("btnChangeAccount")
    public void onChangeAccountClicked(ClickEvent event) {
        delegate.onChangeAccountClicked();
    }

    /**
     * Handler for Change domain button.
     *
     * @param event
     */
    @UiHandler("btnChangeDomain")
    public void onChangeDomainClicked(ClickEvent event) {
        delegate.onChangeDomainNameClicked();
    }
}
