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
 * Describe information about specified S3 key version.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface S3ObjectVersion
{
   /**
    * Get S3 bucket name
    *
    * @return
    *    name of the bucket
    */
   String getS3Bucket();

   /**
    * Set S3 bucket name
    *
    * @param s3Bucket
    *    name of the bucket
    */
   void setS3Bucket(String s3Bucket);

   /**
    * Get S3 key name
    *
    * @return
    *    name of the key where object is stored
    */
   String getS3Key();

   /**
    * Set S3 key name
    *
    * @param s3Key
    *    name of the key where object is stored
    */
   void setS3Key(String s3Key);

   /**
    * Get version ID of current S3 key
    *
    * @return
    *    ID of version
    */
   String getVersionId();

   /**
    * Set version ID for current S3 key
    *
    * @param versionId
    *    ID of version
    */
   void setVersionId(String versionId);

   /**
    * Get S3 key owner
    *
    * @return
    *    owner of the S3 key
    */
   S3Owner getOwner();

   /**
    * Set S3 key owner
    *
    * @param owner
    *    owner of the S3 key
    */
   void setOwner(S3Owner owner);

   /**
    * Get last modified date
    *
    * @return
    *    timestamp of the last modified date
    */
   long getLastModifiedDate();

   /**
    * Set last modified date
    *
    * @param lastModifiedDate
    *    timestamp of the last modified date
    */
   void setLastModifiedDate(long lastModifiedDate);

   /**
    * Get size of the S3 key
    *
    * @return
    *    size in bytes
    */
   long getSize();

   /**
    * Set size of the S3 key
    *
    * @param size
    *    size in bytes
    */
   void setSize(long size);
}
