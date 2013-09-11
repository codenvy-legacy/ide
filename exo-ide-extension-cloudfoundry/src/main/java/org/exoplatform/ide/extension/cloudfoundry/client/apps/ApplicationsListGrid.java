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
package org.exoplatform.ide.extension.cloudfoundry.client.apps;

import com.google.gwt.cell.client.*;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudFoundryApplication;

import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Aug 18, 2011 evgen $
 */
public class ApplicationsListGrid extends ListGrid<CloudFoundryApplication> implements HasApplicationsActions {
    private Column<CloudFoundryApplication, String> nameColumn;

    private Column<CloudFoundryApplication, Number> instancesColumn;

    private Column<CloudFoundryApplication, String> stateColumn;

    private Column<CloudFoundryApplication, List<String>> urlColumn;

    private Column<CloudFoundryApplication, String> servicesColumn;

    private Column<CloudFoundryApplication, String> startColumn;

    private Column<CloudFoundryApplication, String> stopColumn;

    private Column<CloudFoundryApplication, String> restartColumn;

    private Column<CloudFoundryApplication, String> deleteColumn;

    /**
     *
     */
    public ApplicationsListGrid() {
        setID("applicationsListGrid");

        TextCell textCell = new TextCell();
        nameColumn = new Column<CloudFoundryApplication, String>(textCell) {

            @Override
            public String getValue(CloudFoundryApplication object) {
                return object.getName();
            }
        };

        instancesColumn = new Column<CloudFoundryApplication, Number>(new NumberCell()) {

            @Override
            public Integer getValue(CloudFoundryApplication object) {
                return object.getInstances();
            }
        };

        stateColumn = new Column<CloudFoundryApplication, String>(new TextCell()) {

            @Override
            public String getValue(CloudFoundryApplication object) {
                return object.getState();
            }
        };

        urlColumn = new Column<CloudFoundryApplication, List<String>>(new ListLink()) {

            @Override
            public List<String> getValue(CloudFoundryApplication object) {
                return object.getUris();
            }
        };

        servicesColumn = new Column<CloudFoundryApplication, String>(new TextCell()) {

            @Override
            public String getValue(CloudFoundryApplication object) {
                StringBuilder b = new StringBuilder();
                for (String s : object.getServices()) {
                    b.append(s).append(";");
                }
                return b.toString();
            }
        };

        startColumn = new Column<CloudFoundryApplication, String>(new ButtonCell()) {

            @Override
            public String getValue(CloudFoundryApplication object) {
                return "Start";
            }
        };

        stopColumn = new Column<CloudFoundryApplication, String>(new ButtonCell()) {

            @Override
            public String getValue(CloudFoundryApplication object) {
                return "Stop";
            }
        };

        restartColumn = new Column<CloudFoundryApplication, String>(new ButtonCell()) {

            @Override
            public String getValue(CloudFoundryApplication object) {
                return "Restart";
            }
        };

        deleteColumn = new Column<CloudFoundryApplication, String>(new ButtonCell()) {

            @Override
            public String getValue(CloudFoundryApplication object) {
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

        /**
         * @param object
         * @return
         */
        private String createLinks(List<String> object) {
            StringBuilder b = new StringBuilder();
            for (String s : object) {
                b.append(
                        "<a style=\"cursor: pointer; color:#2039f8\" href=http://" + s + " target=\"_blank\">" + s + "</a>")
                 .append("<br>");
            }
            String string = b.toString();
            if (string.endsWith("<br>"))
                string = string.substring(0, string.lastIndexOf("<br>"));
            return string;
        }
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.apps.HasApplicationsActions#addStartApplicationHandler(com.google.gwt.event
     * .logical.shared.SelectionHandler) */
    @Override
    public void addStartApplicationHandler(final SelectionHandler<CloudFoundryApplication> handler) {
        startColumn.setFieldUpdater(new FieldUpdater<CloudFoundryApplication, String>() {

            @Override
            public void update(int index, CloudFoundryApplication object, String value) {
                handler.onSelection(new SelectionEventImpl(object));
            }
        });
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.apps.HasApplicationsActions#addStopApplicationHandler(com.google.gwt.event
     * .logical.shared.SelectionHandler) */
    @Override
    public void addStopApplicationHandler(final SelectionHandler<CloudFoundryApplication> handler) {
        stopColumn.setFieldUpdater(new FieldUpdater<CloudFoundryApplication, String>() {

            @Override
            public void update(int index, CloudFoundryApplication object, String value) {
                handler.onSelection(new SelectionEventImpl(object));
            }
        });
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.apps.HasApplicationsActions#addRestartApplicationHandler(com.google.gwt
     * .event.logical.shared.SelectionHandler) */
    @Override
    public void addRestartApplicationHandler(final SelectionHandler<CloudFoundryApplication> handler) {
        restartColumn.setFieldUpdater(new FieldUpdater<CloudFoundryApplication, String>() {

            @Override
            public void update(int index, CloudFoundryApplication object, String value) {
                handler.onSelection(new SelectionEventImpl(object));
            }
        });
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.apps.HasApplicationsActions#addDeleteApplicationHandler(com.google.gwt
     * .event.logical.shared.SelectionHandler) */
    @Override
    public void addDeleteApplicationHandler(final SelectionHandler<CloudFoundryApplication> handler) {
        deleteColumn.setFieldUpdater(new FieldUpdater<CloudFoundryApplication, String>() {

            @Override
            public void update(int index, CloudFoundryApplication object, String value) {
                handler.onSelection(new SelectionEventImpl(object));
            }
        });
    }

    private class SelectionEventImpl extends SelectionEvent<CloudFoundryApplication> {
        /** @param selectedItem */
        protected SelectionEventImpl(CloudFoundryApplication selectedItem) {
            super(selectedItem);
        }

    }

}
