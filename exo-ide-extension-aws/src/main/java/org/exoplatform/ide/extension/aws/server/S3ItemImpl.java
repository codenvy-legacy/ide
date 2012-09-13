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
package org.exoplatform.ide.extension.aws.server;

import org.exoplatform.ide.extension.aws.shared.S3Item;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class S3ItemImpl implements S3Item
{
   private String s3Bucket;
   private String s3Key;

   public S3ItemImpl(String s3Bucket, String s3Key)
   {
      this.s3Bucket = s3Bucket;
      this.s3Key = s3Key;
   }

   public S3ItemImpl()
   {
   }

   public String getS3Bucket()
   {
      return s3Bucket;
   }

   public void setS3Bucket(String s3Bucket)
   {
      this.s3Bucket = s3Bucket;
   }

   public String getS3Key()
   {
      return s3Key;
   }

   public void setS3Key(String s3Key)
   {
      this.s3Key = s3Key;
   }

   @Override
   public String toString()
   {
      return "S3ItemImpl{" +
         "s3Bucket='" + s3Bucket + '\'' +
         ", s3Key='" + s3Key + '\'' +
         '}';
   }
}
