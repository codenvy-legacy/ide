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

import org.exoplatform.ide.extension.aws.shared.s3.S3Object;
import org.exoplatform.ide.extension.aws.shared.s3.S3Owner;

import java.util.Date;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class S3ObjectImpl implements S3Object
{
   private String s3Bucket;
   private String s3Key;
   private String eTag;
   private long size;
   private long updated;
   private String storageClass;
   private S3Owner owner;

   public static class Builder
   {
      private String s3Bucket;
      private String s3Key;
      private String eTag;
      private long size;
      private long updated;
      private String storageClass;
      private S3Owner owner;

      public Builder s3Bucket(String s3Bucket)
      {
         this.s3Bucket = s3Bucket;
         return this;
      }

      public Builder s3Key(String s3Key)
      {
         this.s3Key = s3Key;
         return this;
      }

      public Builder eTag(String eTag)
      {
         this.eTag = eTag;
         return this;
      }

      public Builder size(long size)
      {
         this.size = size;
         return this;
      }

      public Builder updated(Date updated)
      {
         if (updated != null)
         {
            this.updated = updated.getTime();
         }
         return this;
      }

      public Builder storageClass(String storageClass)
      {
         this.storageClass = storageClass;
         return this;
      }

      public Builder owner(String id, String displayName)
      {
         this.owner = new S3OwnerImpl(id, displayName);
         return this;
      }

      public S3Object build()
      {
         return new S3ObjectImpl(this);
      }
   }

   private S3ObjectImpl(Builder builder)
   {
      this.s3Bucket = builder.s3Bucket;
      this.s3Key = builder.s3Key;
      this.eTag = builder.eTag;
      this.size = builder.size;
      this.updated = builder.updated;
      this.storageClass = builder.storageClass;
      this.owner = builder.owner;
   }

   public S3ObjectImpl()
   {
   }

   @Override
   public String getS3Bucket()
   {
      return s3Bucket;
   }

   @Override
   public void setS3Bucket(String s3Bucket)
   {
      this.s3Bucket = s3Bucket;
   }

   @Override
   public String getS3Key()
   {
      return s3Key;
   }

   @Override
   public void setS3Key(String s3Key)
   {
      this.s3Key = s3Key;
   }

   @Override
   public String geteTag()
   {
      return eTag;
   }

   @Override
   public void seteTag(String eTag)
   {
      this.eTag = eTag;
   }

   @Override
   public long getSize()
   {
      return size;
   }

   @Override
   public void setSize(long size)
   {
      this.size = size;
   }

   @Override
   public long getUpdated()
   {
      return updated;
   }

   @Override
   public void setUpdated(long updated)
   {
      this.updated = updated;
   }

   @Override
   public String getStorageClass()
   {
      return storageClass;
   }

   @Override
   public void setStorageClass(String storageClass)
   {
      this.storageClass = storageClass;
   }

   @Override
   public S3Owner getOwner()
   {
      return owner;
   }

   @Override
   public void setOwner(S3Owner owner)
   {
      this.owner = owner;
   }

   @Override
   public String toString()
   {
      return "S3ObjectImpl{" +
         "s3Bucket='" + s3Bucket + '\'' +
         ", s3Key='" + s3Key + '\'' +
         ", eTag='" + eTag + '\'' +
         ", size=" + size +
         ", updated=" + updated +
         ", storageClass='" + storageClass + '\'' +
         ", owner=" + owner +
         '}';
   }
}
