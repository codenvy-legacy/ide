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

import org.exoplatform.ide.extension.aws.shared.s3.S3ObjectVersion;
import org.exoplatform.ide.extension.aws.shared.s3.S3Owner;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class S3ObjectVersionImpl implements S3ObjectVersion
{
   private String s3Bucket;
   private String s3Key;
   private String versionId;
   private S3Owner owner;
   private long lastModifiedDate;
   private long size;

   public S3ObjectVersionImpl()
   {
   }

   public static class Builder
   {
      private String s3Bucket;
      private String s3Key;
      private String versionId;
      private S3Owner owner;
      private long lastModifiedDate;
      private long size;

      public Builder withS3Bucket(String s3Bucket)
      {
         this.s3Bucket = s3Bucket;
         return this;
      }

      public Builder withS3Key(String s3Key)
      {
         this.s3Key = s3Key;
         return this;
      }

      public Builder withVersionId(String versionId)
      {
         this.versionId = versionId;
         return this;
      }

      public Builder withOwner(S3Owner owner)
      {
         this.owner = owner;
         return this;
      }

      public Builder withLastModifiedDate(long lastModifiedDate)
      {
         this.lastModifiedDate = lastModifiedDate;
         return this;
      }

      public Builder withSize(long size)
      {
         this.size = size;
         return this;
      }

      public S3ObjectVersion build()
      {
         return new S3ObjectVersionImpl(this);
      }
   }

   private S3ObjectVersionImpl(Builder builder)
   {
      builder.s3Bucket = s3Bucket;
      builder.s3Key = s3Key;
      builder.versionId = versionId;
      builder.owner = owner;
      builder.lastModifiedDate = lastModifiedDate;
      builder.size = size;
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
   public String getVersionId()
   {
      return versionId;
   }

   @Override
   public void setVersionId(String versionId)
   {
      this.versionId = versionId;
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
   public long getLastModifiedDate()
   {
      return lastModifiedDate;
   }

   @Override
   public void setLastModifiedDate(long lastModifiedDate)
   {
      this.lastModifiedDate = lastModifiedDate;
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
   public String toString()
   {
      return "S3ObjectVersionImpl{" +
         "s3Bucket='" + s3Bucket + '\'' +
         ", s3Key='" + s3Key + '\'' +
         ", versionId='" + versionId + '\'' +
         ", owner=" + owner +
         ", lastModifiedDate=" + lastModifiedDate +
         ", size=" + size +
         '}';
   }
}
