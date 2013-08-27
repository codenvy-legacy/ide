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
package org.exoplatform.ide.extension.aws.client.beanstalk.versions;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.extension.aws.client.AWSExtension;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ApplicationVersionInfo;

import java.sql.Date;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 20, 2012 12:28:23 PM anya $
 */
public class VersionsGrid extends ListGrid<ApplicationVersionInfo> implements HasVersionActions {
    private final String ID = "ideVersionsGrid";

    private final String LABEL = AWSExtension.LOCALIZATION_CONSTANT.versionsGridLabel();

    private final String DESCRIPTION = AWSExtension.LOCALIZATION_CONSTANT.versionsGridDescription();

    private final String CREATED = AWSExtension.LOCALIZATION_CONSTANT.versionsGridCreated();

    private final String UPDATED = AWSExtension.LOCALIZATION_CONSTANT.versionsGridUpdated();

    private final String DEPLOY = AWSExtension.LOCALIZATION_CONSTANT.deployButton();

    private final String DELETE = AWSExtension.LOCALIZATION_CONSTANT.deleteButton();

    private Column<ApplicationVersionInfo, String> deployColumn;

    private Column<ApplicationVersionInfo, String> deleteColumn;

    public VersionsGrid() {
        setID(ID);

        Column<ApplicationVersionInfo, String> labelColumn = new Column<ApplicationVersionInfo, String>(new TextCell()) {

            @Override
            public String getValue(ApplicationVersionInfo application) {
                return application.getVersionLabel();
            }
        };

        Column<ApplicationVersionInfo, String> descriptionColumn =
                new Column<ApplicationVersionInfo, String>(new TextCell()) {

                    @Override
                    public String getValue(ApplicationVersionInfo application) {
                        return application.getDescription();
                    }
                };

        Column<ApplicationVersionInfo, String> createdColumn = new Column<ApplicationVersionInfo, String>(new TextCell()) {

            @Override
            public String getValue(ApplicationVersionInfo application) {
                return new Date(application.getCreated()).toString();
            }
        };

        Column<ApplicationVersionInfo, String> updatedColumn = new Column<ApplicationVersionInfo, String>(new TextCell()) {

            @Override
            public String getValue(ApplicationVersionInfo application) {
                return new Date(application.getUpdated()).toString();
            }
        };

        deployColumn = new Column<ApplicationVersionInfo, String>(new ButtonCell()) {

            @Override
            public String getValue(ApplicationVersionInfo application) {
                return DEPLOY;
            }
        };

        deleteColumn = new Column<ApplicationVersionInfo, String>(new ButtonCell()) {

            @Override
            public String getValue(ApplicationVersionInfo application) {
                return DELETE;
            }
        };

        getCellTable().addColumn(labelColumn, LABEL);
        getCellTable().addColumn(descriptionColumn, DESCRIPTION);
        getCellTable().addColumn(createdColumn, CREATED);
        getCellTable().addColumn(updatedColumn, UPDATED);
        getCellTable().addColumn(deployColumn, DEPLOY);
        getCellTable().addColumn(deleteColumn, DELETE);
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.versions.HasVersionActions#addDeployHandler(com.google.gwt.event.logical
     * .shared.SelectionHandler) */
    @Override
    public void addDeployHandler(final SelectionHandler<ApplicationVersionInfo> handler) {
        deployColumn.setFieldUpdater(new FieldUpdater<ApplicationVersionInfo, String>() {
            @Override
            public void update(int index, ApplicationVersionInfo object, String value) {
                handler.onSelection(new SelectionEventImpl(object));
            }
        });
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.versions.HasVersionActions#addDeleteHandler(com.google.gwt.event.logical
     * .shared.SelectionHandler) */
    @Override
    public void addDeleteHandler(final SelectionHandler<ApplicationVersionInfo> handler) {
        deleteColumn.setFieldUpdater(new FieldUpdater<ApplicationVersionInfo, String>() {
            @Override
            public void update(int index, ApplicationVersionInfo object, String value) {
                handler.onSelection(new SelectionEventImpl(object));
            }
        });
    }

    private class SelectionEventImpl extends SelectionEvent<ApplicationVersionInfo> {
        /** @param selectedItem */
        protected SelectionEventImpl(ApplicationVersionInfo selectedItem) {
            super(selectedItem);
        }
    }

}
