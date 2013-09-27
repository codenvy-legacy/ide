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
package org.exoplatform.ide.extension.heroku.client.apps;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.extension.heroku.client.HerokuExtension;

/**
 * Grid for listing Heroku applications.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Mar 15, 2012 4:33:30 PM anya $
 */
public class ApplicationsListGrid extends ListGrid<String> implements HasApplicationsActions {

    /** Column to display application's name. */
    private Column<String, String> nameColumn;

    /** Column to delete application. */
    private Column<String, String> deleteColumn;

    /** Column to rename application. */
    private Column<String, String> renameColumn;

    /** Column to change application's environment. */
    private Column<String, String> environmentChangeColumn;

    /** Column to application's properties. */
    private Column<String, String> infoColumn;

    /** Column to import application. */
    private Column<String, String> importColumn;

    /**
     *
     */
    public ApplicationsListGrid() {
        setID("herokuApplicationsListGrid");

        TextCell textCell = new TextCell();
        nameColumn = new Column<String, String>(textCell) {

            @Override
            public String getValue(String object) {
                return object;
            }
        };

        renameColumn = new Column<String, String>(new ButtonCell()) {

            @Override
            public String getValue(String object) {
                return HerokuExtension.LOCALIZATION_CONSTANT.applicationsListGridButtonRename();
            }
        };

        infoColumn = new Column<String, String>(new ButtonCell()) {

            @Override
            public String getValue(String object) {
                return HerokuExtension.LOCALIZATION_CONSTANT.applicationsListGridButtonInfo();
            }
        };

        importColumn = new Column<String, String>(new ButtonCell()) {

            @Override
            public String getValue(String object) {
                return HerokuExtension.LOCALIZATION_CONSTANT.applicationsListGridButtonImport();
            }
        };

        environmentChangeColumn = new Column<String, String>(new ButtonCell()) {

            @Override
            public String getValue(String object) {
                return HerokuExtension.LOCALIZATION_CONSTANT.applicationsListGridButtonChange();
            }
        };

        deleteColumn = new Column<String, String>(new ButtonCell()) {

            @Override
            public String getValue(String object) {
                return HerokuExtension.LOCALIZATION_CONSTANT.applicationsListGridButtonDelete();
            }
        };

        getCellTable().addColumn(nameColumn, HerokuExtension.LOCALIZATION_CONSTANT.applicationsListGridFieldName());

        getCellTable().addColumn(deleteColumn, HerokuExtension.LOCALIZATION_CONSTANT.applicationsListGridButtonDelete());
        deleteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        getCellTable().setColumnWidth(deleteColumn, "80px");

        getCellTable().addColumn(renameColumn, HerokuExtension.LOCALIZATION_CONSTANT.applicationsListGridButtonRename());
        renameColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        getCellTable().setColumnWidth(renameColumn, "80px");

        getCellTable().addColumn(environmentChangeColumn,
                                 HerokuExtension.LOCALIZATION_CONSTANT.applicationsListGridFieldEnvironment());
        environmentChangeColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        getCellTable().setColumnWidth(environmentChangeColumn, "80px");

        getCellTable().addColumn(infoColumn, HerokuExtension.LOCALIZATION_CONSTANT.applicationsListGridButtonInfo());
        infoColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        getCellTable().setColumnWidth(infoColumn, "80px");

        getCellTable().addColumn(importColumn, HerokuExtension.LOCALIZATION_CONSTANT.applicationsListGridButtonImport());
        importColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        getCellTable().setColumnWidth(importColumn, "80px");
    }

    private class SelectionEventImpl extends SelectionEvent<String> {
        /** @param selectedItem */
        protected SelectionEventImpl(String selectedItem) {
            super(selectedItem);
        }

    }

    /** @see org.exoplatform.ide.extension.heroku.client.apps.HasApplicationsActions#addDeleteApplicationHandler(com.google.gwt.event
     * .logical.shared.SelectionHandler) */
    @Override
    public void addDeleteApplicationHandler(final SelectionHandler<String> handler) {
        deleteColumn.setFieldUpdater(new FieldUpdater<String, String>() {

            @Override
            public void update(int index, String object, String value) {
                handler.onSelection(new SelectionEventImpl(object));
            }
        });
    }

    /** @see org.exoplatform.ide.extension.heroku.client.apps.HasApplicationsActions#addRenameApplicationHandler(com.google.gwt.event
     * .logical.shared.SelectionHandler) */
    @Override
    public void addRenameApplicationHandler(final SelectionHandler<String> handler) {
        renameColumn.setFieldUpdater(new FieldUpdater<String, String>() {

            @Override
            public void update(int index, String object, String value) {
                handler.onSelection(new SelectionEventImpl(object));
            }
        });
    }

    /** @see org.exoplatform.ide.extension.heroku.client.apps.HasApplicationsActions#addChangeEnvironmentHandler(com.google.gwt.event
     * .logical.shared.SelectionHandler) */
    @Override
    public void addChangeEnvironmentHandler(final SelectionHandler<String> handler) {
        environmentChangeColumn.setFieldUpdater(new FieldUpdater<String, String>() {

            @Override
            public void update(int index, String object, String value) {
                handler.onSelection(new SelectionEventImpl(object));
            }
        });
    }

    /** @see org.exoplatform.ide.extension.heroku.client.apps.HasApplicationsActions#addApplicationInfoHandler(com.google.gwt.event
     * .logical.shared.SelectionHandler) */
    @Override
    public void addApplicationInfoHandler(final SelectionHandler<String> handler) {
        infoColumn.setFieldUpdater(new FieldUpdater<String, String>() {

            @Override
            public void update(int index, String object, String value) {
                handler.onSelection(new SelectionEventImpl(object));
            }
        });
    }

    /** @see org.exoplatform.ide.extension.heroku.client.apps.HasApplicationsActions#addImportApplicationHandler(com.google.gwt.event
     * .logical.shared.SelectionHandler) */
    @Override
    public void addImportApplicationHandler(final SelectionHandler<String> handler) {
        importColumn.setFieldUpdater(new FieldUpdater<String, String>() {

            @Override
            public void update(int index, String object, String value) {
                handler.onSelection(new SelectionEventImpl(object));
            }
        });
    }

}
