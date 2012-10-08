/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.aws.client.ec2;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.extension.aws.shared.ec2.InstanceInfo;

import java.util.Date;
import java.util.List;

/**
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: EC2InstancesGrid.java Sep 21, 2012 3:07:04 PM azatsarynnyy $
 *
 */
public class EC2InstancesGrid extends ListGrid<InstanceInfo>
{
   private static final String ID = "ideEC2IntancesGrid";

   public EC2InstancesGrid()
   {
      setID(ID);
      initColumns();
   }

   /**
    * Initialize columns.
    */
   private void initColumns()
   {
      Column<InstanceInfo, String> instanceIdCol = new Column<InstanceInfo, String>(new TextCell())
      {
         @Override
         public String getValue(InstanceInfo ec2Intance)
         {
            return ec2Intance.getId();
         }
      };

      Column<InstanceInfo, String> imageIdCol = new Column<InstanceInfo, String>(new TextCell())
      {
         @Override
         public String getValue(InstanceInfo ec2Intance)
         {
            return ec2Intance.getImageId();
         }
      };

      Column<InstanceInfo, String> rootDeviceCol = new Column<InstanceInfo, String>(new TextCell())
      {
         @Override
         public String getValue(InstanceInfo ec2Intance)
         {
            return ec2Intance.getRootDeviceType();
         }
      };

      Column<InstanceInfo, String> imageTypeCol = new Column<InstanceInfo, String>(new TextCell())
      {
         @Override
         public String getValue(InstanceInfo ec2Intance)
         {
            return ec2Intance.getImageType();
         }
      };

      Column<InstanceInfo, String> stateCol = new Column<InstanceInfo, String>(new TextCell())
      {
         @Override
         public String getValue(InstanceInfo ec2Intance)
         {
            return ec2Intance.getState().toString();
         }
      };

      Column<InstanceInfo, String> securityGroupsCol = new Column<InstanceInfo, String>(new TextCell())
      {
         @Override
         public String getValue(InstanceInfo ec2Intance)
         {
            StringBuffer securityGroups = new StringBuffer();
            for (String securityGroup : ec2Intance.getSetSecurityGroupsNames())
            {
               securityGroups.append(securityGroup).append(", ");
            }
            return securityGroups.substring(0, securityGroups.length()-2);
         }
      };

      Column<InstanceInfo, String> keyPairNameCol = new Column<InstanceInfo, String>(new TextCell())
      {
         @Override
         public String getValue(InstanceInfo ec2Intance)
         {
            return ec2Intance.getKeyName();
         }
      };

      Column<InstanceInfo, String> publicDNSNameCol = new Column<InstanceInfo, String>(new TextCell())
      {
         @Override
         public String getValue(InstanceInfo ec2Intance)
         {
            return ec2Intance.getPublicDNSName();
         }
      };

      Column<InstanceInfo, String> availabilityZoneCol = new Column<InstanceInfo, String>(new TextCell())
      {
         @Override
         public String getValue(InstanceInfo ec2Intance)
         {
            return ec2Intance.getAvailabilityZone();
         }
      };

      Column<InstanceInfo, String> launchTimeCol = new Column<InstanceInfo, String>(new TextCell())
      {
         @Override
         public String getValue(InstanceInfo ec2Intance)
         {
            return new Date(ec2Intance.getLaunchTime()).toString();
         }
      };

      getCellTable().addColumn(instanceIdCol, "Instance");
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

   /**
    * @see org.exoplatform.gwtframework.ui.client.component.ListGrid#setValue(java.util.List)
    */
   @Override
   public void setValue(List<InstanceInfo> value)
   {
      super.setValue(value);
      if (value != null && value.size() > 0)
      {
         selectItem(value.get(0));
      }
      getCellTable().redraw();
   }

}
