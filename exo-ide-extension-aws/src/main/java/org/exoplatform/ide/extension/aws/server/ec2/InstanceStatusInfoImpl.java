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

import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.InstanceStatusEvent;
import com.amazonaws.services.ec2.model.InstanceStatusSummary;
import org.exoplatform.ide.extension.aws.shared.ec2.InstanceStatusInfo;
import java.util.List;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class InstanceStatusInfoImpl implements InstanceStatusInfo
{
   private String instanceId;
   private String availabilityZone;
   private List<InstanceStatusEvent> events;
   private InstanceState instanceState;
   private InstanceStatusSummary instanceStatus;
   private InstanceStatusSummary systemStatus;

   public InstanceStatusInfoImpl()
   {
   }

   public InstanceStatusInfoImpl(String instanceId,
                                 String availabilityZone,
                                 List<InstanceStatusEvent> events,
                                 InstanceState instanceState,
                                 InstanceStatusSummary instanceStatus,
                                 InstanceStatusSummary systemStatus)
   {
      this.instanceId = instanceId;
      this.availabilityZone = availabilityZone;
      this.events = events;
      this.instanceState = instanceState;
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
   public List<InstanceStatusEvent> getEvents()
   {
      return events;
   }

   @Override
   public void setEvents(List<InstanceStatusEvent> events)
   {
      this.events = events;
   }

   @Override
   public InstanceState getInstanceState()
   {
      return instanceState;
   }

   @Override
   public void setInstanceState(InstanceState instanceState)
   {
      this.instanceState = instanceState;
   }

   @Override
   public InstanceStatusSummary getInstanceStatus()
   {
      return instanceStatus;
   }

   @Override
   public void setInstanceStatus(InstanceStatusSummary instanceStatus)
   {
      this.instanceStatus = instanceStatus;
   }

   @Override
   public InstanceStatusSummary getSystemStatus()
   {
      return systemStatus;
   }

   @Override
   public void setSystemStatus(InstanceStatusSummary systemStatus)
   {
      this.systemStatus = systemStatus;
   }

   @Override
   public String toString()
   {
      return "InstanceStatusInfoImpl{" +
         "instanceId='" + instanceId + '\'' +
         ", availabilityZone='" + availabilityZone + '\'' +
         ", events=" + events +
         ", instanceState=" + instanceState +
         ", instanceStatus=" + instanceStatus +
         ", systemStatus=" + systemStatus +
         '}';
   }
}
