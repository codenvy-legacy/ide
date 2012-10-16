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
package org.exoplatform.ide.extension.aws.shared.s3;

/**
 * Information about S3 bucket owner
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface S3Owner
{
   /**
    * Get owner ID
    *
    * @return
    *    id of S3 bucket owner
    */
   String getId();

   /**
    * Set owner ID
    *
    * @param id
    *    id of S3 bucket owner
    */
   void setId(String id);

   /**
    * Get S3 bucket owner display name
    *
    * @return
    *    display name of S3 bucket owner
    */
   String getName();


   /**
    * Set S3 bucket owner display name
    *
    * @param name
    *    display name of S3 bucket owner
    */
   void setName(String name);
}
