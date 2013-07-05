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
package com.codenvy.ide.ext.aws.client.beanstalk.environments;

import com.codenvy.ide.ext.aws.client.AWSLocalizationConstant;
import com.codenvy.ide.ext.aws.shared.beanstalk.EnvironmentInfo;
import com.codenvy.ide.ui.Button;
import com.google.gwt.cell.client.AbstractSafeHtmlCell;
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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.List;

/**
 * The implementation of {@link EnvironmentTabPainView}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class EnvironmentTabPainViewImpl extends Composite implements EnvironmentTabPainView {
    interface EnvironmentTabPainViewImplUiBinder extends UiBinder<Widget, EnvironmentTabPainViewImpl> {
    }

    private static EnvironmentTabPainViewImplUiBinder uiBinder = GWT.create(EnvironmentTabPainViewImplUiBinder.class);

    @UiField(provided = true)
    CellTable<EnvironmentInfo> environmentInfoCellTable = new CellTable<EnvironmentInfo>();

    @UiField
    Button editConfigurationButton;

    @UiField
    Button restartEnvironmentButton;

    @UiField
    Button rebuildEnvironmentButton;

    @UiField
    Button terminateEnvironmentButton;

    @UiField
    Button logsEnvironmentButton;

    @UiField(provided = true)
    AWSLocalizationConstant constant;

    private ActionDelegate delegate;

    private EnvironmentInfo selectedEnvironment;

    /**
     * Create view.
     *
     * @param constant
     */
    @Inject
    public EnvironmentTabPainViewImpl(AWSLocalizationConstant constant) {
        this.constant = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        initEnvironmentCellTable();

        initWidget(widget);

        editConfigurationButton.setEnabled(false);
        restartEnvironmentButton.setEnabled(false);
        rebuildEnvironmentButton.setEnabled(false);
        terminateEnvironmentButton.setEnabled(false);
        logsEnvironmentButton.setEnabled(false);
    }

    /** Link representation of table cell. */
    public class LinkCell extends AbstractSafeHtmlCell<String> {
        public LinkCell() {
            super(new SafeHtmlListRenderer());
        }

        /** {@inheritDoc} */
        @Override
        protected void render(com.google.gwt.cell.client.Cell.Context context, SafeHtml data, SafeHtmlBuilder sb) {
            sb.append(data);
        }

    }


    /** Renderer for HTML cells. */
    private class SafeHtmlListRenderer implements SafeHtmlRenderer<String> {
        /** {@inheritDoc} */
        @Override
        public SafeHtml render(String object) {
            String string = createLinks(object);
            return new SafeHtmlBuilder().appendHtmlConstant(string).toSafeHtml();
        }

        /** {@inheritDoc} */
        @Override
        public void render(String object, SafeHtmlBuilder builder) {
            String string = createLinks(object);
            builder.appendHtmlConstant(string);
        }
    }

    /**
     * Create html href link for specified url.
     *
     * @param s
     *         url.
     * @return html href with specified url.
     */
    private String createLinks(String s) {
        if (s.isEmpty()) {
            return "n/a";
        }
        return "<a style=\"cursor: pointer; color:#2039f8\" href=http://" + s
               + " target=\"_blank\">View Running Version</a><br>";
    }

    /** Init environment cell table. */
    private void initEnvironmentCellTable() {
        environmentInfoCellTable.setWidth("100%", true);
        environmentInfoCellTable.setAutoHeaderRefreshDisabled(true);
        environmentInfoCellTable.setAutoFooterRefreshDisabled(true);

        HTMLPanel emptyPanel = new HTMLPanel("No environments.");
        environmentInfoCellTable.setEmptyTableWidget(emptyPanel);

        final SelectionModel<EnvironmentInfo> selectionModel = new SingleSelectionModel<EnvironmentInfo>();
        environmentInfoCellTable.setSelectionModel(selectionModel);

        Column<EnvironmentInfo, String> nameColumn = new Column<EnvironmentInfo, String>(new TextCell()) {
            @Override
            public String getValue(EnvironmentInfo object) {
                return object.getName();
            }
        };

        Column<EnvironmentInfo, String> solutionStackColumn = new Column<EnvironmentInfo, String>(new TextCell()) {
            @Override
            public String getValue(EnvironmentInfo object) {
                return object.getSolutionStackName();
            }
        };

        Column<EnvironmentInfo, String> runingVersionColumn = new Column<EnvironmentInfo, String>(new TextCell()) {
            @Override
            public String getValue(EnvironmentInfo object) {
                return object.getVersionLabel();
            }
        };

        Column<EnvironmentInfo, String> statusColumn = new Column<EnvironmentInfo, String>(new TextCell()) {
            @Override
            public String getValue(EnvironmentInfo object) {
                return object.getStatus().toString();
            }
        };

        Column<EnvironmentInfo, String> healthColumn = new Column<EnvironmentInfo, String>(new TextCell()) {
            @Override
            public String getValue(EnvironmentInfo object) {
                return object.getHealth().toString();
            }
        };

        Column<EnvironmentInfo, String> urlColumn = new Column<EnvironmentInfo, String>(new LinkCell()) {
            @Override
            public String getValue(EnvironmentInfo object) {
                if (object.getCname() != null) {
                    return object.getCname();
                }
                if (object.getEndpointUrl() != null) {
                    return object.getEndpointUrl();
                }
                return "";
            }
        };

        environmentInfoCellTable.getSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                selectedEnvironment = ((SingleSelectionModel<EnvironmentInfo>)selectionModel).getSelectedObject();
                editConfigurationButton.setEnabled(true);
                restartEnvironmentButton.setEnabled(true);
                rebuildEnvironmentButton.setEnabled(true);
                terminateEnvironmentButton.setEnabled(true);
                logsEnvironmentButton.setEnabled(true);
            }
        });

        environmentInfoCellTable.addColumn(nameColumn, constant.environmentsGridName());
        environmentInfoCellTable.addColumn(solutionStackColumn, constant.environmentsGridStack());
        environmentInfoCellTable.addColumn(runingVersionColumn, constant.environmentsGridVersion());
        environmentInfoCellTable.addColumn(statusColumn, constant.environmentsGridStatus());
        environmentInfoCellTable.addColumn(healthColumn, constant.environmentsGridHealth());
        environmentInfoCellTable.addColumn(urlColumn, constant.environmentsGridUrl());
    }

    /** {@inheritDoc} */
    @Override
    public void setEnvironments(List<EnvironmentInfo> environments) {
        environmentInfoCellTable.setRowData(environments);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @UiHandler("editConfigurationButton")
    public void onEditConfigurationButtonClicked(ClickEvent event) {
        delegate.onEditConfigurationButtonClicked(selectedEnvironment);
    }

    @UiHandler("restartEnvironmentButton")
    public void onRestartButtonClicked(ClickEvent event) {
        delegate.onRestartButtonClicked(selectedEnvironment);
    }

    @UiHandler("rebuildEnvironmentButton")
    public void onRebuildButtonClicked(ClickEvent event) {
        delegate.onRebuildButtonClicked(selectedEnvironment);
    }

    @UiHandler("terminateEnvironmentButton")
    public void onTerminateButtonClicked(ClickEvent event) {
        delegate.onTerminateButtonClicked(selectedEnvironment);
    }

    @UiHandler("logsEnvironmentButton")
    public void onGetLogsButtonClicked(ClickEvent event) {
        delegate.onGetLogsButtonCLicked(selectedEnvironment);
    }
}
