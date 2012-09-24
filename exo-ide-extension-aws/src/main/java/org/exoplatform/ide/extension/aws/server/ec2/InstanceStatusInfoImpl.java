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
package org.exoplatform.ide.extension.aws.server.ec2;

import org.exoplatform.ide.extension.aws.shared.ec2.InstanceStatusInfo;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class InstanceStatusInfoImpl implements InstanceStatusInfo
{
   private String instanceId;
   private String availabilityZone;
   private Integer instanceStateCode;
   private String instanceStateName;
   private String instanceStatus;
   private String systemStatus;

   public InstanceStatusInfoImpl()
   {
   }

   public InstanceStatusInfoImpl(String instanceId,
                                 String availabilityZone,
                                 Integer instanceStateCode,
                                 String instanceStateName,
                                 String instanceStatus,
                                 String systemStatus)
   {
      this.instanceId = instanceId;
      this.availabilityZone = availabilityZone;
      this.instanceStateCode = instanceStateCode;
      this.instanceStateName = instanceStateName;
      this.instanceStatus = instanceStatus;
      this.systemStatus = systemStatus;
   }

   @Override
   public String getInstanceId()
   {
      return instanceId;
   }

   @Override
   public void setInstanceId(String instanceId)
   {
      this.instanceId = instanceId;
   }

   @Override
   public String getAvailabilityZone()
   {
      return availabilityZone;
   }

   @Override
   public void setAvailabilityZone(String availabilityZone)
   {
      this.availabilityZone = availabilityZone;
   }

   @Override
   public Integer getInstanceStateCode()
   {
      return instanceStateCode;
   }

   @Override
   public void setInstanceStateCode(Integer instanceStateCode)
   {
      this.instanceStateCode = instanceStateCode;
   }

   @Override
   public String getInstanceStateName()
   {
      return instanceStateName;
   }

   @Override
   public void setInstanceStateName(String instanceStateName)
   {
      this.instanceStateName = instanceStateName;
   }

   @Override
   public String getInstanceStatus()
   {
      return instanceStatus;
   }

   @Override
   public void setInstanceStatus(String status)
   {
      this.instanceStatus = status;
   }

   @Override
   public String getSystemStatus()
   {
      return systemStatus;
   }

   @Override
   public void setSystemStatus(String status)
   {
      this.systemStatus = status;
   }

   @Override
   public String toString()
   {
      return "InstanceStatusInfoImpl{" +
         "instanceId='" + instanceId + '\'' +
         ", availabilityZone='" + availabilityZone + '\'' +
         ", instanceStateCode=" + instanceStateCode +
         ", instanceStateName='" + instanceStateName + '\'' +
         ", instanceStatus='" + instanceStatus + '\'' +
         ", systemStatus='" + systemStatus + '\'' +
         '}';
   }
}
