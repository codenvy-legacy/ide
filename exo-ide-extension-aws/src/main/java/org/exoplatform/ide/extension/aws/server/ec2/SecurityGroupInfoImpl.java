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

import org.exoplatform.ide.extension.aws.shared.ec2.SecurityGroupInfo;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class SecurityGroupInfoImpl implements SecurityGroupInfo
{
   private String id;
   private String name;
   private String ownerId;
   private String description;

   public SecurityGroupInfoImpl(String id, String name, String ownerId, String description)
   {
      this.id = id;
      this.name = name;
      this.ownerId = ownerId;
      this.description = description;
   }

   public SecurityGroupInfoImpl()
   {
   }

   @Override
   public String getId()
   {
      return id;
   }

   @Override
   public void setId(String id)
   {
      this.id = id;
   }

   @Override
   public String getName()
   {
      return name;
   }

   @Override
   public void setName(String name)
   {
      this.name = name;
   }

   @Override
   public String getOwnerId()
   {
      return ownerId;
   }

   @Override
   public void setOwnerId(String ownerId)
   {
      this.ownerId = ownerId;
   }

   @Override
   public String getDescription()
   {
      return description;
   }

   @Override
   public void setDescription(String description)
   {
      this.description = description;
   }

   @Override
   public String toString()
   {
      return "SecurityGroupInfoImpl{" +
         "id='" + id + '\'' +
         ", name='" + name + '\'' +
         ", ownerId='" + ownerId + '\'' +
         ", description='" + description + '\'' +
         '}';
   }
}
