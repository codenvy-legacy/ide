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
package com.codenvy.ide.ext.appfog.client.apps;

import com.codenvy.ide.ext.appfog.client.AppfogLocalizationConstant;
import com.codenvy.ide.ext.appfog.client.AppfogResources;
import com.codenvy.ide.ext.appfog.shared.AppfogApplication;
import com.codenvy.ide.json.JsonArray;
import com.google.gwt.cell.client.*;
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
import com.google.gwt.user.client.ui.TextBox;
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

    /** Special cell for displaying links into CellTable. */
    private class ListLink extends AbstractSafeHtmlCell<List<String>> {
        /** Create Link list. */
        public ListLink() {
            super(new SafeHtmlListRenderer());
        }

        /** {@inheritDoc} */
        @Override
        protected void render(com.google.gwt.cell.client.Cell.Context context, SafeHtml data, SafeHtmlBuilder sb) {
            sb.append(data);
        }
    }

    /** Renderer for displaying links into CellTable. */
    private class SafeHtmlListRenderer implements SafeHtmlRenderer<List<String>> {
        /** {@inheritDoc} */
        @Override
        public SafeHtml render(List<String> object) {
            String string = createLinks(object);
            return new SafeHtmlBuilder().appendHtmlConstant(string).toSafeHtml();
        }

        /** {@inheritDoc} */
        @Override
        public void render(List<String> object, SafeHtmlBuilder builder) {
            String string = createLinks(object);
            builder.appendHtmlConstant(string);
        }

        /**
         * Formats list of links to String.
         *
         * @param object
         * @return links in String format
         */
        private String createLinks(List<String> object) {
            StringBuilder b = new StringBuilder();
            for (String s : object) {
                b.append(
                        "<a style=\"cursor: pointer; color:#2039f8\" href=http://" + s + " target=\"_blank\">" + s + "</a>")
                 .append("<br>");
            }

            String string = b.toString();
            if (string.endsWith("<br>")) {
                string = string.substring(0, string.lastIndexOf("<br>"));
            }

            return string;
        }
    }

    private static ApplicationsViewImplUiBinder ourUiBinder = GWT.create(ApplicationsViewImplUiBinder.class);

    @UiField
    com.codenvy.ide.ui.Button btnClose;
    @UiField
    com.codenvy.ide.ui.Button btnShow;
    @UiField
    TextBox                   target;
    @UiField(provided = true)
    final AppfogResources            res;
    @UiField(provided = true)
    final AppfogLocalizationConstant locale;
    @UiField(provided = true)
    CellTable<AppfogApplication> appsTable = new CellTable<AppfogApplication>();
    private ActionDelegate delegate;
    private boolean        isShown;

    /**
     * Create view.
     *
     * @param resources
     * @param constant
     */
    @Inject
    protected ApplicationsViewImpl(AppfogResources resources, AppfogLocalizationConstant constant) {
        this.res = resources;
        this.locale = constant;

        createAppsTable();

        Widget widget = ourUiBinder.createAndBindUi(this);

        this.setText("Applications");
        this.setWidget(widget);
    }

    /** Creates table what contains list of available applications. */
    private void createAppsTable() {
        Column<AppfogApplication, String> nameColumn = new Column<AppfogApplication, String>(new TextCell()) {
            @Override
            public String getValue(AppfogApplication object) {
                return object.getName();
            }
        };

        Column<AppfogApplication, Number> instancesColumn = new Column<AppfogApplication, Number>(new NumberCell()) {
            @Override
            public Integer getValue(AppfogApplication object) {
                return object.getInstances();
            }
        };

        Column<AppfogApplication, String> stateColumn = new Column<AppfogApplication, String>(new TextCell()) {
            @Override
            public String getValue(AppfogApplication object) {
                return object.getState();
            }
        };

        Column<AppfogApplication, List<String>> urlColumn = new Column<AppfogApplication, List<String>>(new ListLink()) {
            @Override
            public List<String> getValue(AppfogApplication object) {
                ArrayList<String> list = new ArrayList<String>();
                JsonArray<String> uris = object.getUris();
                for (int i = 0; i < uris.size(); i++) {
                    String s = uris.get(i);
                    list.add(s);
                }
                return list;
            }
        };

        Column<AppfogApplication, String> servicesColumn = new Column<AppfogApplication, String>(new TextCell()) {
            @Override
            public String getValue(AppfogApplication object) {
                StringBuilder b = new StringBuilder();
                JsonArray<String> services = object.getServices();
                for (int i = 0; i < services.size(); i++) {
                    String s = services.get(i);
                    b.append(s).append(";");
                }
                return b.toString();
            }
        };

        Column<AppfogApplication, String> startColumn = new Column<AppfogApplication, String>(new ButtonCell()) {
            @Override
            public String getValue(AppfogApplication object) {
                return "Start";
            }
        };

        // Creates handler on button clicked
        startColumn.setFieldUpdater(new FieldUpdater<AppfogApplication, String>() {
            @Override
            public void update(int index, AppfogApplication object, String value) {
                delegate.onStartClicked(object);
            }
        });


        Column<AppfogApplication, String> stopColumn = new Column<AppfogApplication, String>(new ButtonCell()) {
            @Override
            public String getValue(AppfogApplication object) {
                return "Stop";
            }
        };

        // Creates handler on button clicked
        stopColumn.setFieldUpdater(new FieldUpdater<AppfogApplication, String>() {
            @Override
            public void update(int index, AppfogApplication object, String value) {
                delegate.onStopClicked(object);
            }
        });

        Column<AppfogApplication, String> restartColumn = new Column<AppfogApplication, String>(new ButtonCell()) {
            @Override
            public String getValue(AppfogApplication object) {
                return "Restart";
            }
        };

        // Creates handler on button clicked
        restartColumn.setFieldUpdater(new FieldUpdater<AppfogApplication, String>() {
            @Override
            public void update(int index, AppfogApplication object, String value) {
                delegate.onRestartClicked(object);
            }
        });

        Column<AppfogApplication, String> deleteColumn = new Column<AppfogApplication, String>(new ButtonCell()) {
            @Override
            public String getValue(AppfogApplication object) {
                return "Delete";
            }
        };

        // Creates handler on button clicked
        deleteColumn.setFieldUpdater(new FieldUpdater<AppfogApplication, String>() {
            @Override
            public void update(int index, AppfogApplication object, String value) {
                delegate.onDeleteClicked(object);
            }
        });

        // Adds headers and size of column
        appsTable.addColumn(nameColumn, "Application");
        appsTable.addColumn(instancesColumn, "#");
        appsTable.setColumnWidth(instancesColumn, "8px");
        appsTable.addColumn(stateColumn, "Health");
        appsTable.setColumnWidth(stateColumn, "50px");
        appsTable.addColumn(urlColumn, "URLS");
        appsTable.addColumn(servicesColumn, "Services");
        appsTable.setColumnWidth(servicesColumn, "60px");

        appsTable.addColumn(startColumn, "Start");
        appsTable.setColumnWidth(startColumn, "60px");
        appsTable.addColumn(stopColumn, "Stop");
        appsTable.setColumnWidth(stopColumn, "60px");
        appsTable.addColumn(restartColumn, "Restart");
        appsTable.setColumnWidth(restartColumn, "60px");
        appsTable.addColumn(deleteColumn, "Delete");
        appsTable.setColumnWidth(deleteColumn, "60px");

        // don't show loading indicator
        appsTable.setLoadingIndicator(null);
    }

    /** {@inheritDoc} */
    @Override
    public void setApplications(JsonArray<AppfogApplication> apps) {
        // Wraps JsonArray in java.util.List
        List<AppfogApplication> appList = new ArrayList<AppfogApplication>();
        for (int i = 0; i < apps.size(); i++) {
            appList.add(apps.get(i));
        }

        appsTable.setRowData(appList);
    }

    /** {@inheritDoc} */
    @Override
    public String getTarget() {
        return target.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setTarget(String target) {
        this.target.setText(target);
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

    @UiHandler("btnShow")
    public void onShowButtonClicked(ClickEvent event) {
        delegate.onShowClicked();
    }

    @UiHandler("btnClose")
    public void onCloseClicked(ClickEvent event) {
        delegate.onCloseClicked();
    }
}