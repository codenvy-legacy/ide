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
package org.exoplatform.ide.extension.aws.client.ec2;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.extension.aws.shared.ec2.InstanceInfo;

import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: EC2InstancesGrid.java Sep 21, 2012 3:07:04 PM azatsarynnyy $
 */
public class EC2InstancesGrid extends ListGrid<InstanceInfo> {
    private static final String ID = "ideEC2IntancesGrid";

    public EC2InstancesGrid() {
        setID(ID);
        initColumns();
    }

    /** Initialize columns. */
    private void initColumns() {
        Column<InstanceInfo, String> instanceIdCol = new Column<InstanceInfo, String>(new TextCell()) {
            @Override
            public String getValue(InstanceInfo ec2Intance) {
                return ec2Intance.getId();
            }
        };

        Column<InstanceInfo, String> imageIdCol = new Column<InstanceInfo, String>(new TextCell()) {
            @Override
            public String getValue(InstanceInfo ec2Intance) {
                return ec2Intance.getImageId();
            }
        };

        Column<InstanceInfo, String> rootDeviceCol = new Column<InstanceInfo, String>(new TextCell()) {
            @Override
            public String getValue(InstanceInfo ec2Intance) {
                return ec2Intance.getRootDeviceType();
            }
        };

        Column<InstanceInfo, String> imageTypeCol = new Column<InstanceInfo, String>(new TextCell()) {
            @Override
            public String getValue(InstanceInfo ec2Intance) {
                return ec2Intance.getImageType();
            }
        };

        Column<InstanceInfo, String> stateCol = new Column<InstanceInfo, String>(new TextCell()) {
            @Override
            public String getValue(InstanceInfo ec2Intance) {
                return ec2Intance.getState().toString();
            }
        };

        Column<InstanceInfo, String> securityGroupsCol = new Column<InstanceInfo, String>(new TextCell()) {
            @Override
            public String getValue(InstanceInfo ec2Intance) {
                StringBuffer securityGroups = new StringBuffer();
                for (String securityGroup : ec2Intance.getSetSecurityGroupsNames()) {
                    securityGroups.append(securityGroup).append(", ");
                }
                return securityGroups.substring(0, securityGroups.length() - 2);
            }
        };

        Column<InstanceInfo, String> keyPairNameCol = new Column<InstanceInfo, String>(new TextCell()) {
            @Override
            public String getValue(InstanceInfo ec2Intance) {
                return ec2Intance.getKeyName();
            }
        };

        Column<InstanceInfo, String> publicDNSNameCol = new Column<InstanceInfo, String>(new TextCell()) {
            @Override
            public String getValue(InstanceInfo ec2Intance) {
                return ec2Intance.getPublicDNSName();
            }
        };

        Column<InstanceInfo, String> availabilityZoneCol = new Column<InstanceInfo, String>(new TextCell()) {
            @Override
            public String getValue(InstanceInfo ec2Intance) {
                return ec2Intance.getAvailabilityZone();
            }
        };

        Column<InstanceInfo, String> launchTimeCol = new Column<InstanceInfo, String>(new TextCell()) {
            @Override
            public String getValue(InstanceInfo ec2Intance) {
                return new Date(ec2Intance.getLaunchTime()).toString();
            }
        };

        getCellTable().addColumn(instanceIdCol, "Instance");
        getCellTable().setColumnWidth(launchTimeCol, 30, Unit.PCT);
        getCellTable().addColumn(imageIdCol, "AMI ID");
        getCellTable().addColumn(rootDeviceCol, "Root Device");
        getCellTable().addColumn(imageTypeCol, "Type");
        getCellTable().addColumn(stateCol, "State");
        getCellTable().addColumn(securityGroupsCol, "Security Groups");
        getCellTable().addColumn(keyPairNameCol, "Key Pair Name");
        getCellTable().addColumn(publicDNSNameCol, "Public DNS");
        getCellTable().addColumn(availabilityZoneCol, "Availability Zone");
        getCellTable().addColumn(launchTimeCol, "Launch Time");
        getCellTable().setColumnWidth(launchTimeCol, 30, Unit.PCT);
    }

    /** @see org.exoplatform.gwtframework.ui.client.component.ListGrid#setValue(java.util.List) */
    @Override
    public void setValue(List<InstanceInfo> value) {
        super.setValue(value);
        if (value != null && value.size() > 0) {
            selectItem(value.get(0));
        }
        getCellTable().redraw();
    }

}
