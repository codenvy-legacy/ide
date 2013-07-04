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
package com.codenvy.ide.ext.aws.client.beanstalk.versions;

import com.codenvy.ide.ext.aws.client.AWSLocalizationConstant;
import com.codenvy.ide.ext.aws.shared.beanstalk.ApplicationVersionInfo;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Date;
import java.util.List;

/**
 * The implementation of {@link VersionTabPainView}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class VersionTabPainViewImpl extends Composite implements VersionTabPainView {
    interface VersionTabPainViewImplUiBinder extends UiBinder<Widget, VersionTabPainViewImpl> {
    }

    private static VersionTabPainViewImplUiBinder uiBinder = GWT.create(VersionTabPainViewImplUiBinder.class);

    @UiField(provided = true)
    CellTable<ApplicationVersionInfo> versions = new CellTable<ApplicationVersionInfo>();

    @UiField(provided = true)
    AWSLocalizationConstant constant;

    private ActionDelegate delegate;

    /**
     * Create view.
     *
     * @param constant
     */
    @Inject
    protected VersionTabPainViewImpl(AWSLocalizationConstant constant) {
        this.constant = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        initVersionTable();

        initWidget(widget);
    }

    /** Init version table. */
    private void initVersionTable() {
        versions.setWidth("100%", true);
        versions.setAutoHeaderRefreshDisabled(true);
        versions.setAutoFooterRefreshDisabled(true);

        HTMLPanel emptyPanel = new HTMLPanel("No versions.");
        versions.setEmptyTableWidget(emptyPanel);

        final SelectionModel<ApplicationVersionInfo> selectionModel = new SingleSelectionModel<ApplicationVersionInfo>();
        versions.setSelectionModel(selectionModel);

        Column<ApplicationVersionInfo, String> versionLabelColumn = new Column<ApplicationVersionInfo, String>(new TextCell()) {
            @Override
            public String getValue(ApplicationVersionInfo object) {
                return object.getVersionLabel();
            }
        };

        Column<ApplicationVersionInfo, String> descriptionColumn = new Column<ApplicationVersionInfo, String>(new TextCell()) {
            @Override
            public String getValue(ApplicationVersionInfo object) {
                return object.getDescription();
            }
        };

        Column<ApplicationVersionInfo, String> creationDateColumn = new Column<ApplicationVersionInfo, String>(new TextCell()) {
            @Override
            public String getValue(ApplicationVersionInfo object) {
                String createdTime =
                        DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_SHORT)
                                      .format(new Date((long)object.getCreated()));
                return createdTime;
            }
        };

        Column<ApplicationVersionInfo, String> updateDateColumn = new Column<ApplicationVersionInfo, String>(new TextCell()) {
            @Override
            public String getValue(ApplicationVersionInfo object) {
                String updatedTime =
                        DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_SHORT)
                                      .format(new Date((long)object.getCreated()));
                return updatedTime;
            }
        };

        Column<ApplicationVersionInfo, String> deployButtonColumn = new Column<ApplicationVersionInfo, String>(new ButtonCell()) {
            @Override
            public String getValue(ApplicationVersionInfo object) {
                return "Deploy";
            }
        };

        Column<ApplicationVersionInfo, String> deleteButtonColumn = new Column<ApplicationVersionInfo, String>(new ButtonCell()) {
            @Override
            public String getValue(ApplicationVersionInfo object) {
                return "Delete";
            }
        };

        deployButtonColumn.setFieldUpdater(new FieldUpdater<ApplicationVersionInfo, String>() {
            @Override
            public void update(int index, ApplicationVersionInfo object, String value) {
                delegate.onDeployVersionClicked(object);
            }
        });

        deleteButtonColumn.setFieldUpdater(new FieldUpdater<ApplicationVersionInfo, String>() {
            @Override
            public void update(int index, ApplicationVersionInfo object, String value) {
                delegate.onDeleteVersionClicked(object);
            }
        });

        versions.getRowContainer().getStyle().setHeight(100, Style.Unit.PX);

        versions.addColumn(versionLabelColumn, constant.versionsGridLabel());
        versions.addColumn(descriptionColumn, constant.versionsGridDescription());
        versions.addColumn(creationDateColumn, constant.versionsGridCreated());
        versions.addColumn(updateDateColumn, constant.versionsGridUpdated());
        versions.addColumn(deployButtonColumn, constant.deployButton());
        versions.addColumn(deleteButtonColumn, constant.deleteButton());
    }

    /** {@inheritDoc} */
    @Override
    public void setVersions(List<ApplicationVersionInfo> versions) {
        this.versions.setRowData(versions);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }
}
