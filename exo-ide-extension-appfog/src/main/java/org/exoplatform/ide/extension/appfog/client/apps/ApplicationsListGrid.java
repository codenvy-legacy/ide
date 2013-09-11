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
package org.exoplatform.ide.extension.appfog.client.apps;

import com.google.gwt.cell.client.*;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.extension.appfog.shared.AppfogApplication;

import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Aug 18, 2011 evgen $
 */
public class ApplicationsListGrid extends ListGrid<AppfogApplication> implements HasApplicationsActions {
    private Column<AppfogApplication, String> nameColumn;

    private Column<AppfogApplication, Number> instancesColumn;

    private Column<AppfogApplication, String> stateColumn;

    private Column<AppfogApplication, List<String>> urlColumn;

    private Column<AppfogApplication, String> servicesColumn;

    private Column<AppfogApplication, String> startColumn;

    private Column<AppfogApplication, String> stopColumn;

    private Column<AppfogApplication, String> restartColumn;

    private Column<AppfogApplication, String> deleteColumn;

    /**
     *
     */
    public ApplicationsListGrid() {
        setID("applicationsListGrid");

        TextCell textCell = new TextCell();
        nameColumn = new Column<AppfogApplication, String>(textCell) {

            @Override
            public String getValue(AppfogApplication object) {
                return object.getName();
            }
        };

        instancesColumn = new Column<AppfogApplication, Number>(new NumberCell()) {

            @Override
            public Integer getValue(AppfogApplication object) {
                return object.getInstances();
            }
        };

        stateColumn = new Column<AppfogApplication, String>(new TextCell()) {

            @Override
            public String getValue(AppfogApplication object) {
                return object.getState();
            }
        };

        urlColumn = new Column<AppfogApplication, List<String>>(new ListLink()) {

            @Override
            public List<String> getValue(AppfogApplication object) {
                return object.getUris();
            }
        };

        servicesColumn = new Column<AppfogApplication, String>(new TextCell()) {

            @Override
            public String getValue(AppfogApplication object) {
                StringBuilder b = new StringBuilder();
                for (String s : object.getServices()) {
                    b.append(s).append(";");
                }
                return b.toString();
            }
        };

        startColumn = new Column<AppfogApplication, String>(new ButtonCell()) {

            @Override
            public String getValue(AppfogApplication object) {
                return "Start";
            }
        };

        stopColumn = new Column<AppfogApplication, String>(new ButtonCell()) {

            @Override
            public String getValue(AppfogApplication object) {
                return "Stop";
            }
        };

        restartColumn = new Column<AppfogApplication, String>(new ButtonCell()) {

            @Override
            public String getValue(AppfogApplication object) {
                return "Restart";
            }
        };

        deleteColumn = new Column<AppfogApplication, String>(new ButtonCell()) {

            @Override
            public String getValue(AppfogApplication object) {
                return "Delete";
            }
        };

        getCellTable().addColumn(nameColumn, "Application");
        getCellTable().addColumn(instancesColumn, "#");
        getCellTable().setColumnWidth(instancesColumn, "8px");
        getCellTable().addColumn(stateColumn, "Health");
        getCellTable().setColumnWidth(stateColumn, "50px");
        getCellTable().addColumn(urlColumn, "URLS");
        getCellTable().addColumn(servicesColumn, "Services");
        getCellTable().setColumnWidth(servicesColumn, "60px");

        getCellTable().addColumn(startColumn, "Start");
        getCellTable().setColumnWidth(startColumn, "60px");
        getCellTable().addColumn(stopColumn, "Stop");
        getCellTable().setColumnWidth(stopColumn, "60px");
        getCellTable().addColumn(restartColumn, "Restart");
        getCellTable().setColumnWidth(restartColumn, "60px");
        getCellTable().addColumn(deleteColumn, "Delete");
        getCellTable().setColumnWidth(deleteColumn, "60px");
    }

    private class ListLink extends AbstractSafeHtmlCell<List<String>> {

        /**
         *
         */
        public ListLink() {
            super(new SafeHtmlListRenderer());
        }

        /**
         * @see com.google.gwt.cell.client.AbstractSafeHtmlCell#render(com.google.gwt.cell.client.Cell.Context,
         *      com.google.gwt.safehtml.shared.SafeHtml, com.google.gwt.safehtml.shared.SafeHtmlBuilder)
         */
        @Override
        protected void render(com.google.gwt.cell.client.Cell.Context context, SafeHtml data, SafeHtmlBuilder sb) {
            sb.append(data);
        }

    }

    private class SafeHtmlListRenderer implements SafeHtmlRenderer<List<String>> {

        /** @see com.google.gwt.text.shared.SafeHtmlRenderer#render(java.lang.Object) */
        @Override
        public SafeHtml render(List<String> object) {
            String string = createLinks(object);
            return new SafeHtmlBuilder().appendHtmlConstant(string).toSafeHtml();
        }

        /** @see com.google.gwt.text.shared.SafeHtmlRenderer#render(java.lang.Object, com.google.gwt.safehtml.shared.SafeHtmlBuilder) */
        @Override
        public void render(List<String> object, SafeHtmlBuilder builder) {
            String string = createLinks(object);
            builder.appendHtmlConstant(string);
        }

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

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.apps.HasApplicationsActions#addStartApplicationHandler(com.google.gwt.event
     * .logical.shared.SelectionHandler) */
    @Override
    public void addStartApplicationHandler(final SelectionHandler<AppfogApplication> handler) {
        startColumn.setFieldUpdater(new FieldUpdater<AppfogApplication, String>() {

            @Override
            public void update(int index, AppfogApplication object, String value) {
                handler.onSelection(new SelectionEventImpl(object));
            }
        });
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.apps.HasApplicationsActions#addStopApplicationHandler(com.google.gwt.event
     * .logical.shared.SelectionHandler) */
    @Override
    public void addStopApplicationHandler(final SelectionHandler<AppfogApplication> handler) {
        stopColumn.setFieldUpdater(new FieldUpdater<AppfogApplication, String>() {

            @Override
            public void update(int index, AppfogApplication object, String value) {
                handler.onSelection(new SelectionEventImpl(object));
            }
        });
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.apps.HasApplicationsActions#addRestartApplicationHandler(com.google.gwt
     * .event.logical.shared.SelectionHandler) */
    @Override
    public void addRestartApplicationHandler(final SelectionHandler<AppfogApplication> handler) {
        restartColumn.setFieldUpdater(new FieldUpdater<AppfogApplication, String>() {

            @Override
            public void update(int index, AppfogApplication object, String value) {
                handler.onSelection(new SelectionEventImpl(object));
            }
        });
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.apps.HasApplicationsActions#addDeleteApplicationHandler(com.google.gwt
     * .event.logical.shared.SelectionHandler) */
    @Override
    public void addDeleteApplicationHandler(final SelectionHandler<AppfogApplication> handler) {
        deleteColumn.setFieldUpdater(new FieldUpdater<AppfogApplication, String>() {

            @Override
            public void update(int index, AppfogApplication object, String value) {
                handler.onSelection(new SelectionEventImpl(object));
            }
        });
    }

    private class SelectionEventImpl extends SelectionEvent<AppfogApplication> {
        protected SelectionEventImpl(AppfogApplication selectedItem) {
            super(selectedItem);
        }

    }

}
