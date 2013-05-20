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

    @UiHandler("btnOk")
    public void onShowButtonClicked(ClickEvent event) {
        delegate.onOkClicked();
    }
}