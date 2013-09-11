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
package org.exoplatform.ide.extension.cloudbees.client.list.ui;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesExtension;
import org.exoplatform.ide.extension.cloudbees.client.list.HasApplicationListActions;
import org.exoplatform.ide.extension.cloudbees.shared.ApplicationInfo;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Sep 22, 2011 evgen $
 */
public class ApplicationListGrid extends ListGrid<ApplicationInfo> implements HasApplicationListActions {

    private Column<ApplicationInfo, String> nameColumn;

    private Column<ApplicationInfo, String> statusColumn;

    private Column<ApplicationInfo, String> urlColumn;

    private Column<ApplicationInfo, String> instanceColumn;

    private Column<ApplicationInfo, String> infoColumn;

    private Column<ApplicationInfo, String> deleteColumn;

    /**
     *
     */
    public ApplicationListGrid() {
        nameColumn = new Column<ApplicationInfo, String>(new TextCell()) {

            @Override
            public String getValue(ApplicationInfo object) {
                return object.getId();
            }
        };

        statusColumn = new Column<ApplicationInfo, String>(new TextCell()) {

            @Override
            public String getValue(ApplicationInfo object) {
                return object.getStatus();
            }
        };

        urlColumn = new Column<ApplicationInfo, String>(new TextCell(new SafeHtmlRenderer<String>() {

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

        instanceColumn = new Column<ApplicationInfo, String>(new TextCell()) {

            @Override
            public String getValue(ApplicationInfo object) {
                return object.getClusterSize();
            }
        };

        deleteColumn = new Column<ApplicationInfo, String>(new ButtonCell()) {

            @Override
            public String getValue(ApplicationInfo object) {
                return CloudBeesExtension.LOCALIZATION_CONSTANT.appListDelete();
            }
        };

        infoColumn = new Column<ApplicationInfo, String>(new ButtonCell()) {

            @Override
            public String getValue(ApplicationInfo object) {
                return CloudBeesExtension.LOCALIZATION_CONSTANT.appListInfo();
            }
        };

        getCellTable().addColumn(nameColumn, CloudBeesExtension.LOCALIZATION_CONSTANT.appListName());
        getCellTable().addColumn(statusColumn, CloudBeesExtension.LOCALIZATION_CONSTANT.appListStatus());
        getCellTable().addColumn(urlColumn, CloudBeesExtension.LOCALIZATION_CONSTANT.appListUrl());
        getCellTable().addColumn(instanceColumn, CloudBeesExtension.LOCALIZATION_CONSTANT.appListInstance());
        getCellTable().addColumn(infoColumn, CloudBeesExtension.LOCALIZATION_CONSTANT.appListInfo());
        getCellTable().addColumn(deleteColumn, CloudBeesExtension.LOCALIZATION_CONSTANT.appListDelete());
    }

    /** @see org.exoplatform.ide.extension.cloudbees.client.list.HasApplicationListActions#addDeleteHandler(com.google.gwt.event.logical
     * .shared.SelectionHandler) */
    @Override
    public void addDeleteHandler(final SelectionHandler<ApplicationInfo> handler) {
        deleteColumn.setFieldUpdater(new FieldUpdater<ApplicationInfo, String>() {

            @Override
            public void update(int index, ApplicationInfo object, String value) {
                handler.onSelection(new SelectionEventImpl(object));
            }
        });
    }

    /** @see org.exoplatform.ide.extension.cloudbees.client.list.HasApplicationListActions#addInfoHandler(com.google.gwt.event.logical
     * .shared.SelectionHandler) */
    @Override
    public void addInfoHandler(final SelectionHandler<ApplicationInfo> handler) {
        infoColumn.setFieldUpdater(new FieldUpdater<ApplicationInfo, String>() {

            @Override
            public void update(int index, ApplicationInfo object, String value) {
                handler.onSelection(new SelectionEventImpl(object));
            }
        });
    }

    private class SelectionEventImpl extends SelectionEvent<ApplicationInfo> {
        /** @param selectedItem */
        protected SelectionEventImpl(ApplicationInfo selectedItem) {
            super(selectedItem);
        }

    }
}
