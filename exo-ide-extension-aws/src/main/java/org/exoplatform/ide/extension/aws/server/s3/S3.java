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

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.DeleteBucketRequest;
import org.exoplatform.ide.extension.aws.server.AWSAuthenticator;
import org.exoplatform.ide.extension.aws.server.AWSException;
import org.exoplatform.ide.extension.aws.shared.s3.S3Bucket;
import org.exoplatform.ide.extension.aws.shared.s3.S3ObjectInfo;
import org.exoplatform.ide.extension.aws.shared.s3.S3ObjectsList;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class S3
{
   private final AWSAuthenticator authenticator;

   public S3(AWSAuthenticator authenticator)
   {
      this.authenticator = authenticator;
   }

   public S3Bucket createBucket(String name, String region) throws AWSException
   {
      AmazonS3 s3 = getS3Client();

      try
      {
         return createBucket(s3, name, region);
      }
      catch (AmazonClientException e)
      {
         throw new AWSException(e);
      }
   }

   private S3Bucket createBucket(AmazonS3 s3, String name, String region)
   {
      Bucket bucket = s3.createBucket(new CreateBucketRequest(name, region));
      S3Bucket s3Bucket = new S3BucketImpl(
         bucket.getName(),
         bucket.getCreationDate().getTime(),
         new S3OwnerImpl(
            bucket.getOwner().getId(),
            bucket.getOwner().getDisplayName()
         )
      );

      return s3Bucket;
   }

   public List<S3Bucket> listBuckets() throws AWSException
   {
      AmazonS3 s3 = getS3Client();

      try
      {
         return listBuckets(s3);
      }
      catch (AmazonClientException e)
      {
         throw new AWSException(e);
      }
   }

   private List<S3Bucket> listBuckets(AmazonS3 s3)
   {
      List<Bucket> buckets = s3.listBuckets();
      List<S3Bucket> s3Buckets = new ArrayList<S3Bucket>();

      for (Bucket bucket : buckets)
      {
         s3Buckets.add(
            new S3BucketImpl(
               bucket.getName(),
               bucket.getCreationDate().getTime(),
               new S3OwnerImpl(
                  bucket.getOwner().getId(),
                  bucket.getOwner().getDisplayName()
               )
            )
         );
      }

      return s3Buckets;
   }

   public void deleteBucket(String name) throws AWSException
   {
      AmazonS3 s3 = getS3Client();

      try
      {
         deleteBucket(s3, name);
      }
      catch (AmazonClientException e)
      {
         throw new AWSException(e);
      }
   }

   private void deleteBucket(AmazonS3 s3, String name)
   {
      s3.deleteBucket(new DeleteBucketRequest(name));
   }

   //

   /**
    * Upload content from specified URL to Amazon S3 storage.
    *
    * @param s3Bucket
    *    bucket name
    * @param s3Key
    *    key
    * @param data
    *    data location
    * @return S3 object description
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    * @throws java.io.IOException
    *    if any i/o error occurs
    */
   public S3ObjectInfo putObject(String s3Bucket, String s3Key, URL data) throws AWSException, IOException
   {
      return null;
   }

   /**
    * Upload specified eXo IDE project to Amazon S3 storage. Project is zipped before uploading to S3.
    *
    * @param s3Bucket
    *    bucket name
    * @param s3Key
    *    key
    * @param vfs
    *    instance of Virtual File system
    * @param projectId
    *    id of project
    * @return S3 object description
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    * @throws org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException
    *    if any VirtualFileSystem error occurs
    * @throws IOException
    *    if any i/o error occurs
    */
   public S3ObjectInfo uploadProject(String s3Bucket, String s3Key, VirtualFileSystem vfs, String projectId)
      throws AWSException, VirtualFileSystemException, IOException
   {
      return null;
   }

   public S3ObjectsList listObjects(String s3Bucket, String prefix, String nextMarker, int maxKeys)
   {
      return null;
   }

   //

   protected AmazonS3 getS3Client() throws AWSException
   {
      final AWSCredentials credentials = authenticator.getCredentials();
      if (credentials == null)
      {
         throw new AWSException("Authentication required.");
      }
      return new AmazonS3Client(credentials);
   }
}
