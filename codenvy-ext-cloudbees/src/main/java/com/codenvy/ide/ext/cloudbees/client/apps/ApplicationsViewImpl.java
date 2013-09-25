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
package com.codenvy.ide.ext.cloudbees.client.apps;

import com.codenvy.ide.ext.cloudbees.client.CloudBeesLocalizationConstant;
import com.codenvy.ide.ext.cloudbees.shared.ApplicationInfo;
import com.codenvy.ide.json.JsonArray;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

/**
 * The implementation of {@link ApplicationsView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class ApplicationsViewImpl extends DialogBox implements ApplicationsView {
    interface ApplicationsViewImplUiBinder extends UiBinder<Widget, ApplicationsViewImpl> {
    }

    private static ApplicationsViewImplUiBinder ourUiBinder = GWT.create(ApplicationsViewImplUiBinder.class);

    @UiField
    com.codenvy.ide.ui.Button btnOk;
    @UiField(provided = true)
    final CloudBeesLocalizationConstant locale;
    @UiField(provided = true)
    CellTable<ApplicationInfo> appsTable = new CellTable<ApplicationInfo>();
    private ActionDelegate delegate;
    private boolean        isShown;

    /**
     * Create view.
     *
     * @param locale
     */
    @Inject
    protected ApplicationsViewImpl(CloudBeesLocalizationConstant locale) {
        this.locale = locale;

        createAppsTable();

        Widget widget = ourUiBinder.createAndBindUi(this);

        this.setText("Applications");
        this.setWidget(widget);
    }

    /** Creates table what contains list of available applications. */
    private void createAppsTable() {
        Column<ApplicationInfo, String> nameColumn = new Column<ApplicationInfo, String>(new TextCell()) {
            @Override
            public String getValue(ApplicationInfo object) {
                return object.getId();
            }
        };

        Column<ApplicationInfo, String> statusColumn = new Column<ApplicationInfo, String>(new TextCell()) {

            @Override
            public String getValue(ApplicationInfo object) {
                return object.getStatus();
            }
        };

        Column<ApplicationInfo, String> urlColumn = new Column<ApplicationInfo, String>(new TextCell(new SafeHtmlRenderer<String>() {
            @Override
            public void render(String object, SafeHtmlBuilder builder) {
                builder.appendHtmlConstant(createLink(object));
            }

            @Override
            public SafeHtml render(String object) {
                return new SafeHtmlBuilder().appendHtmlConstant(createLink(object)).toSafeHtml();
            }

            private String createLink(String s) {
                return "<a style=\"cursor: pointer; color:#2039f8\" href=" + s + " target=\"_blank\">" + s + "</a>";
            }
        })) {

            @Override
            public String getValue(ApplicationInfo object) {
                return object.getUrl();
            }
        };

        Column<ApplicationInfo, String> instanceColumn = new Column<ApplicationInfo, String>(new TextCell()) {

            @Override
            public String getValue(ApplicationInfo object) {
                return object.getClusterSize();
            }
        };

        Column<ApplicationInfo, String> deleteColumn = new Column<ApplicationInfo, String>(new ButtonCell()) {
            @Override
            public String getValue(ApplicationInfo object) {
                return locale.appListDelete();
            }
        };

        // Creates handler on button clicked
        deleteColumn.setFieldUpdater(new FieldUpdater<ApplicationInfo, String>() {
            @Override
            public void update(int index, ApplicationInfo object, String value) {
                delegate.onDeleteClicked(object);
            }
        });

        Column<ApplicationInfo, String> infoColumn = new Column<ApplicationInfo, String>(new ButtonCell()) {
            @Override
            public String getValue(ApplicationInfo object) {
                return locale.appListInfo();
            }
        };

        // Creates handler on button clicked
        infoColumn.setFieldUpdater(new FieldUpdater<ApplicationInfo, String>() {
            @Override
            public void update(int index, ApplicationInfo object, String value) {
                delegate.onInfoClicked(object);
            }
        });

        // Adds headers and size of column
        appsTable.addColumn(nameColumn, locale.appListName());

        appsTable.addColumn(statusColumn, locale.appListStatus());
        appsTable.setColumnWidth(statusColumn, "40px");

        appsTable.addColumn(urlColumn, locale.appListUrl());

        appsTable.addColumn(instanceColumn, locale.appListInstance());
        appsTable.setColumnWidth(instanceColumn, "60px");

        appsTable.addColumn(infoColumn, locale.appListInfo());
        appsTable.setColumnWidth(infoColumn, "60px");
        appsTable.addColumn(deleteColumn, locale.appListDelete());
        appsTable.setColumnWidth(deleteColumn, "60px");

        // don't show loading indicator
        appsTable.setLoadingIndicator(null);
    }

    /** {@inheritDoc} */
    @Override
    public void setApplications(JsonArray<ApplicationInfo> apps) {
        // Wraps JsonArray in java.util.List
        List<ApplicationInfo> appList = new ArrayList<ApplicationInfo>();
        for (int i = 0; i < apps.size(); i++) {
            appList.add(apps.get(i));
        }

        appsTable.setRowData(appList);
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
        this.isShown = true;
        this.center();
        this.show();
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @UiHandler("btnOk")
    public void onShowButtonClicked(ClickEvent event) {
        delegate.onOkClicked();
    }
}