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
package org.exoplatform.ide.extension.aws.server.s3;

import org.exoplatform.ide.extension.aws.shared.s3.S3Owner;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class S3OwnerImpl implements S3Owner
{
   private String id;
   private String name;

   public S3OwnerImpl(String id, String name)
   {
      this.id = id;
      this.name = name;
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
}
