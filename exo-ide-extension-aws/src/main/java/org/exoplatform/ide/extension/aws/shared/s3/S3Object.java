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
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface S3Object
{
   /**
    * Get name of S3 bucket where object stored.
    *
    * @return name of S3 bucket
    */
   String getS3Bucket();

   /**
    * Set name of S3 bucket where object stored.
    *
    * @param s3Bucket
    *    name of S3 bucket
    */
   void setS3Bucket(String s3Bucket);

   /**
    * Get S3 key under which this object is stored.
    *
    * @return S3 key under which this object is stored
    */
   String getS3Key();

   /**
    * Set S3 key under which this object is stored.
    *
    * @param s3Key
    *    S3 key under which this object is stored
    */
   void setS3Key(String s3Key);

   /**
    * Get hex encoded MD5 hash of this object's contents, as computed by Amazon S3.
    *
    * @return hex encoded MD5 hash of this object's contents
    */
   String getETag();

   /**
    * Set hex encoded MD5 hash of this object's contents, as computed by Amazon S3.
    *
    * @param eTag
    *    hex encoded MD5 hash of this object's contents
    */
   void setETag(String eTag);

   /**
    * Get size of object in bytes.
    *
    * @return size of object in bytes
    */
   long getSize();

   /**
    * Set size of object in bytes.
    *
    * @param size
    *    size of object in bytes
    */
   void setSize(long size);

   /**
    * Get time when this object was last modified.
    *
    * @return time when this object was last modified
    */
   long getUpdated();

   /**
    * Get time when this object was last modified.
    *
    * @param updated
    *    time when this object was last modified
    */
   void setUpdated(long updated);

   /**
    * Get class of storage used by Amazon S3 to store this object.
    *
    * @return class of storage used by Amazon S3 to store this object
    */
   String getStorageClass();

   /**
    * Set class of storage used by Amazon S3 to store this object.
    *
    * @param storageClass
    *    class of storage used by Amazon S3 to store this object
    */
   void setStorageClass(String storageClass);

   /**
    * Get owner of this object.
    *
    * @return owner of this object
    */
   S3Owner getOwner();

   /**
    * Set owner of this object.
    *
    * @param owner
    *    owner of this object
    */
   void setOwner(S3Owner owner);
}
