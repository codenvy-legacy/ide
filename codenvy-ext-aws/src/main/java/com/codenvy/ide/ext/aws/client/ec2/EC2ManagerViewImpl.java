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
package com.codenvy.ide.ext.aws.client.ec2;

import com.codenvy.ide.ext.aws.client.AWSLocalizationConstant;
import com.codenvy.ide.ext.aws.shared.ec2.InstanceInfo;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonStringMap;
import com.codenvy.ide.ui.Button;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The implementation of {@link EC2ManagerView}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class EC2ManagerViewImpl extends DialogBox implements EC2ManagerView {
    interface EC2ManagerViewImplUiBinder extends UiBinder<Widget, EC2ManagerViewImpl> {
    }

    private static EC2ManagerViewImplUiBinder uiBinder = GWT.create(EC2ManagerViewImplUiBinder.class);

    @UiField(provided = true)
    CellTable<InstanceInfo> ec2Instances = new CellTable<InstanceInfo>();

    @UiField(provided = true)
    CellTable<Ec2Tag> ec2Tags = new CellTable<Ec2Tag>();

    @UiField
    Button btnReboot;

    @UiField
    Button btnTerminate;

    @UiField
    Button btnStart;

    @UiField
    Button btnStop;

    @UiField
    Button btnClose;

    @UiField(provided = true)
    AWSLocalizationConstant constant;

    private ActionDelegate delegate;

    private boolean isShown;

    private InstanceInfo selectedInstanceInfo;

    /**
     * Create view.
     *
     * @param constant
     */
    @Inject
    protected EC2ManagerViewImpl(AWSLocalizationConstant constant) {
        this.constant = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        this.setText(constant.loginTitle());
        this.setWidget(widget);

        initInstancesTable();
        initTagsTable();
    }

    /** Init EC2 instances table. */
    private void initInstancesTable() {
        ec2Instances.setWidth("100%", true);
        ec2Instances.setAutoHeaderRefreshDisabled(true);
        ec2Instances.setAutoFooterRefreshDisabled(true);

        HTMLPanel panel = new HTMLPanel("No instances.");
        ec2Instances.setEmptyTableWidget(panel);
        ec2Instances.setLoadingIndicator(panel);

        final SelectionModel<InstanceInfo> selectionModel = new SingleSelectionModel<InstanceInfo>();

        ec2Instances.setSelectionModel(selectionModel);

        Column<InstanceInfo, String> instanceColumn = new Column<InstanceInfo, String>(new TextCell()) {
            @Override
            public String getValue(InstanceInfo object) {
                return object.getId();
            }
        };

        Column<InstanceInfo, String> amiIdColumn = new Column<InstanceInfo, String>(new TextCell()) {
            @Override
            public String getValue(InstanceInfo object) {
                return object.getImageId();
            }
        };

        Column<InstanceInfo, String> rootDeviceColumn = new Column<InstanceInfo, String>(new TextCell()) {
            @Override
            public String getValue(InstanceInfo object) {
                return object.getRootDeviceType();
            }
        };

        Column<InstanceInfo, String> typeColumn = new Column<InstanceInfo, String>(new TextCell()) {
            @Override
            public String getValue(InstanceInfo object) {
                return object.getImageType();
            }
        };

        Column<InstanceInfo, String> stateColumn = new Column<InstanceInfo, String>(new TextCell()) {
            @Override
            public String getValue(InstanceInfo object) {
                return object.getState().toString();
            }
        };

        Column<InstanceInfo, String> securityGroupsColumn = new Column<InstanceInfo, String>(new TextCell()) {
            @Override
            public String getValue(InstanceInfo object) {
                StringBuilder securityGroups = new StringBuilder();
                for (int i = 0; i < object.getSetSecurityGroupsNames().size(); i++) {
                    securityGroups.append(object.getSetSecurityGroupsNames().get(i)).append(", ");
                }
                return securityGroups.substring(0, securityGroups.length() - 2);
            }
        };

        Column<InstanceInfo, String> keyPairNameColumn = new Column<InstanceInfo, String>(new TextCell()) {
            @Override
            public String getValue(InstanceInfo object) {
                return object.getKeyName();
            }
        };

        Column<InstanceInfo, String> publicDNSColumn = new Column<InstanceInfo, String>(new TextCell()) {
            @Override
            public String getValue(InstanceInfo object) {
                return object.getPublicDNSName();
            }
        };

        Column<InstanceInfo, String> availabilityZoneColumn = new Column<InstanceInfo, String>(new TextCell()) {
            @Override
            public String getValue(InstanceInfo object) {
                return object.getAvailabilityZone();
            }
        };

        Column<InstanceInfo, String> launchTimeColumn = new Column<InstanceInfo, String>(new TextCell()) {
            @Override
            public String getValue(InstanceInfo object) {
                return new Date((long)object.getLaunchTime()).toString();
            }
        };

        ec2Instances.getSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                selectedInstanceInfo = ((SingleSelectionModel<InstanceInfo>)selectionModel).getSelectedObject();

                final List<Ec2Tag> instanceTags = new ArrayList<Ec2Tag>();

                if (selectedInstanceInfo != null) {
                    selectedInstanceInfo.getTags().iterate(new JsonStringMap.IterationCallback<String>() {
                        @Override
                        public void onIteration(String key, String value) {
                            instanceTags.add(new Ec2Tag(key, value));
                        }
                    });
                }

                ec2Tags.setRowData(instanceTags);
            }
        });

        ec2Instances.addColumn(instanceColumn, "Instance");
        ec2Instances.addColumn(amiIdColumn, "AMI ID");
        ec2Instances.addColumn(rootDeviceColumn, "Root Device");
        ec2Instances.addColumn(typeColumn, "Type");
        ec2Instances.addColumn(stateColumn, "State");
        ec2Instances.addColumn(securityGroupsColumn, "Security Groups");
        ec2Instances.addColumn(keyPairNameColumn, "Key Pair Name");
        ec2Instances.addColumn(publicDNSColumn, "Public DNS");
        ec2Instances.addColumn(availabilityZoneColumn, "Availability Zone");
        ec2Instances.addColumn(launchTimeColumn, "Launch Time");

        ec2Instances.setColumnWidth(instanceColumn, 100, Style.Unit.PX);
    }

    /** Init EC2 tags table. */
    private void initTagsTable() {
        ec2Tags.setWidth("100%", true);
        ec2Tags.setAutoHeaderRefreshDisabled(true);
        ec2Tags.setAutoFooterRefreshDisabled(true);

        HTMLPanel panel = new HTMLPanel("Select an instance.");
        ec2Tags.setLoadingIndicator(panel);

        final SelectionModel<Ec2Tag> selectionModel = new NoSelectionModel<Ec2Tag>();

        ec2Tags.setSelectionModel(selectionModel);

        Column<Ec2Tag, String> tagKeyColumn = new Column<Ec2Tag, String>(new TextCell()) {
            @Override
            public String getValue(Ec2Tag object) {
                return object.getTagName();
            }
        };

        Column<Ec2Tag, String> tagValueColumn = new Column<Ec2Tag, String>(new TextCell()) {
            @Override
            public String getValue(Ec2Tag object) {
                return object.getTagValue();
            }
        };

        ec2Tags.addColumn(tagKeyColumn, "Key");
        ec2Tags.addColumn(tagValueColumn, "Value");

        ec2Tags.setColumnWidth(tagKeyColumn, 200, Style.Unit.PX);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void setEC2Instances(JsonArray<InstanceInfo> instances) {
        List<InstanceInfo> instanceInfoList = new ArrayList<InstanceInfo>();

        for (int i = 0; i < instances.size(); i++) {
            instanceInfoList.add(instances.get(i));
        }

        ec2Instances.setRowData(instanceInfoList);
    }

    /** {@inheritDoc} */
    @Override
    public void setEC2Tags(JsonArray<Ec2Tag> tags) {
        List<Ec2Tag> ec2TagList = new ArrayList<Ec2Tag>();

        for (int i = 0; i < tags.size(); i++) {
            ec2TagList.add(tags.get(i));
        }

        ec2Tags.setRowData(ec2TagList);
    }

    /** {@inheritDoc} */
    @Override
    public void setAllButtonsEnableState(boolean enable) {
        btnReboot.setEnabled(enable);
        btnStart.setEnabled(enable);
        btnStop.setEnabled(enable);
        btnTerminate.setEnabled(enable);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isShown() {
        return isShown;
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
    public void close() {
        this.isShown = false;
        this.hide();
    }

    @UiHandler("btnStop")
    public void onStopClicked(ClickEvent event) {
        delegate.onStopClicked(selectedInstanceInfo);
    }

    @UiHandler("btnStart")
    public void onStartClicked(ClickEvent event) {
        delegate.onStartClicked(selectedInstanceInfo);
    }

    @UiHandler("btnTerminate")
    public void onTerminateClicked(ClickEvent event) {
        delegate.onTerminateClicked(selectedInstanceInfo);
    }

    @UiHandler("btnReboot")
    public void onRebootClicked(ClickEvent event) {
        delegate.onRebootClicked(selectedInstanceInfo);
    }

    @UiHandler("btnClose")
    public void onCloseClicked(ClickEvent event) {
        delegate.onCloseClicked();
    }
}
