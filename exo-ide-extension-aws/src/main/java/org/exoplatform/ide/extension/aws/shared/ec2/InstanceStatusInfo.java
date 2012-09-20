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
package org.exoplatform.ide.extension.aws.shared.ec2;

import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.InstanceStatusEvent;
import com.amazonaws.services.ec2.model.InstanceStatusSummary;
import java.util.List;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface InstanceStatusInfo
{
   String getInstanceId();

   void setInstanceId(String instanceId);

   String getAvailabilityZone();

   void setAvailabilityZone(String availabilityZone);

   List<InstanceStatusEvent> getEvents();

   void setEvents(List<InstanceStatusEvent> events);

   InstanceState getInstanceState();

   void setInstanceState(InstanceState instanceState);

   InstanceStatusSummary getInstanceStatus();

   void setInstanceStatus(InstanceStatusSummary instanceStatus);

   InstanceStatusSummary getSystemStatus();

   void setSystemStatus(InstanceStatusSummary systemStatus);
}
