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
package com.codenvy.ide.extension.cloudfoundry.client.apps;

import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryLocalizationConstant;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryResources;
import com.codenvy.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

/**
 * The implementation of {@link ApplicationsView}.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class ApplicationsViewImpl extends DialogBox implements ApplicationsView {
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

    interface ApplicationsViewImplUiBinder extends UiBinder<Widget, ApplicationsViewImpl> {
    }

    private static ApplicationsViewImplUiBinder uiBinder = GWT.create(ApplicationsViewImplUiBinder.class);

    @UiField
    com.codenvy.ide.ui.Button btnClose;
    @UiField
    com.codenvy.ide.ui.Button btnShow;
    @UiField
    ListBox                   target;
    @UiField(provided = true)
    CellTable<CloudFoundryApplication> appsTable = new CellTable<CloudFoundryApplication>();
    @UiField(provided = true)
    final   CloudFoundryResources            res;
    @UiField(provided = true)
    final   CloudFoundryLocalizationConstant locale;
    private ActionDelegate                   delegate;
    private boolean                          isShown;

    /**
     * Create view.
     *
     * @param resources
     * @param constant
     */
    @Inject
    protected ApplicationsViewImpl(CloudFoundryResources resources, CloudFoundryLocalizationConstant constant) {
        this.res = resources;
        this.locale = constant;

        createAppsTable();

        Widget widget = uiBinder.createAndBindUi(this);

        this.setText("Applications");
        this.setWidget(widget);
    }

    /** Creates table what contains list of available applications. */
    private void createAppsTable() {
        Column<CloudFoundryApplication, String> nameColumn = new Column<CloudFoundryApplication, String>(new TextCell()) {
            @Override
            public String getValue(CloudFoundryApplication object) {
                return object.getName();
            }
        };

        Column<CloudFoundryApplication, Number> instancesColumn =
                new Column<CloudFoundryApplication, Number>(new NumberCell()) {
                    @Override
                    public Integer getValue(CloudFoundryApplication object) {
                        return object.getInstances();
                    }
                };

        Column<CloudFoundryApplication, String> stateColumn = new Column<CloudFoundryApplication, String>(new TextCell()) {
            @Override
            public String getValue(CloudFoundryApplication object) {
                return object.getState();
            }
        };

        Column<CloudFoundryApplication, List<String>> urlColumn =
                new Column<CloudFoundryApplication, List<String>>(new ListLink()) {
                    @Override
                    public List<String> getValue(CloudFoundryApplication object) {
                        ArrayList<String> list = new ArrayList<String>();
                        JsonArray<String> uris = object.getUris();
                        for (int i = 0; i < uris.size(); i++) {
                            String s = uris.get(i);
                            list.add(s);
                        }
                        return list;
                    }
                };

        Column<CloudFoundryApplication, String> servicesColumn =
                new Column<CloudFoundryApplication, String>(new TextCell()) {
                    @Override
                    public String getValue(CloudFoundryApplication object) {
                        StringBuilder b = new StringBuilder();
                        JsonArray<String> services = object.getServices();
                        for (int i = 0; i < services.size(); i++) {
                            String s = services.get(i);
                            b.append(s).append(";");
                        }
                        return b.toString();
                    }
                };

        Column<CloudFoundryApplication, String> startColumn =
                new Column<CloudFoundryApplication, String>(new ButtonCell()) {

                    @Override
                    public String getValue(CloudFoundryApplication object) {
                        return "Start";
                    }
                };

        // Creates handler on button clicked
        startColumn.setFieldUpdater(new FieldUpdater<CloudFoundryApplication, String>() {
            @Override
            public void update(int index, CloudFoundryApplication object, String value) {
                delegate.onStartClicked(object);
            }
        });


        Column<CloudFoundryApplication, String> stopColumn =
                new Column<CloudFoundryApplication, String>(new ButtonCell()) {

                    @Override
                    public String getValue(CloudFoundryApplication object) {
                        return "Stop";
                    }
                };

        // Creates handler on button clicked
        stopColumn.setFieldUpdater(new FieldUpdater<CloudFoundryApplication, String>() {
            @Override
            public void update(int index, CloudFoundryApplication object, String value) {
                delegate.onStopClicked(object);
            }
        });

        Column<CloudFoundryApplication, String> restartColumn =
                new Column<CloudFoundryApplication, String>(new ButtonCell()) {

                    @Override
                    public String getValue(CloudFoundryApplication object) {
                        return "Restart";
                    }
                };

        // Creates handler on button clicked
        restartColumn.setFieldUpdater(new FieldUpdater<CloudFoundryApplication, String>() {
            @Override
            public void update(int index, CloudFoundryApplication object, String value) {
                delegate.onRestartClicked(object);
            }
        });

        Column<CloudFoundryApplication, String> deleteColumn =
                new Column<CloudFoundryApplication, String>(new ButtonCell()) {
                    @Override
                    public String getValue(CloudFoundryApplication object) {
                        return "Delete";
                    }
                };

        // Creates handler on button clicked
        deleteColumn.setFieldUpdater(new FieldUpdater<CloudFoundryApplication, String>() {
            @Override
            public void update(int index, CloudFoundryApplication object, String value) {
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
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void setApplications(JsonArray<CloudFoundryApplication> apps) {
        // Wraps JsonArray in java.util.List
        List<CloudFoundryApplication> appList = new ArrayList<CloudFoundryApplication>();
        for (int i = 0; i < apps.size(); i++) {
            appList.add(apps.get(i));
        }

        appsTable.setRowData(appList);
    }

    /** {@inheritDoc} */
    @Override
    public String getServer() {
        int serverIndex = target.getSelectedIndex();
        return serverIndex != -1 ? target.getItemText(serverIndex) : "";
    }

    /** {@inheritDoc} */
    @Override
    public void setServer(String server) {
        int count = this.target.getItemCount();
        boolean isItemFound = false;

        // Looks up entered server into available list of servers
        int i = 0;
        while (i < count && !isItemFound) {
            String item = this.target.getItemText(i);
            isItemFound = item.equals(server);

            i++;
        }

        // If item was found then it will be shown otherwise do nothing
        if (isItemFound) {
            this.target.setSelectedIndex(i - 1);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setServers(JsonArray<String> servers) {
        target.clear();
        for (int i = 0; i < servers.size(); i++) {
            target.addItem(servers.get(i));
        }
    }

    @UiHandler("btnShow")
    void onBtnShowClick(ClickEvent event) {
        delegate.onShowClicked();
    }

    @UiHandler("btnClose")
    void onBtnCloseClick(ClickEvent event) {
        delegate.onCloseClicked();
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
    public boolean isShown() {
        return isShown;
    }
}