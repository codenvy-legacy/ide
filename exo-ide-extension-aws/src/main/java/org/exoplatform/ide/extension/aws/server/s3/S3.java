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
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketVersioningConfiguration;
import com.amazonaws.services.s3.model.CanonicalGrantee;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.DeleteBucketRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteVersionRequest;
import com.amazonaws.services.s3.model.EmailAddressGrantee;
import com.amazonaws.services.s3.model.GetBucketAclRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.Grant;
import com.amazonaws.services.s3.model.Grantee;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ListVersionsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.S3VersionSummary;
import com.amazonaws.services.s3.model.SetBucketAclRequest;
import com.amazonaws.services.s3.model.SetBucketVersioningConfigurationRequest;
import com.amazonaws.services.s3.model.VersionListing;
import org.exoplatform.ide.extension.aws.server.AWSAuthenticator;
import org.exoplatform.ide.extension.aws.server.AWSClient;
import org.exoplatform.ide.extension.aws.server.AWSException;
import org.exoplatform.ide.extension.aws.shared.s3.NewS3Object;
import org.exoplatform.ide.extension.aws.shared.s3.S3AccessControl;
import org.exoplatform.ide.extension.aws.shared.s3.S3Bucket;
import org.exoplatform.ide.extension.aws.shared.s3.S3IdentityGroupType;
import org.exoplatform.ide.extension.aws.shared.s3.S3IdentityType;
import org.exoplatform.ide.extension.aws.shared.s3.S3KeyVersions;
import org.exoplatform.ide.extension.aws.shared.s3.S3Object;
import org.exoplatform.ide.extension.aws.shared.s3.S3ObjectVersion;
import org.exoplatform.ide.extension.aws.shared.s3.S3ObjectsList;
import org.exoplatform.ide.extension.aws.shared.s3.S3Permission;
import org.exoplatform.ide.extension.aws.shared.s3.S3Region;
import org.exoplatform.ide.extension.aws.shared.s3.S3VersioningStatus;
import org.exoplatform.ide.extension.aws.shared.s3.UpdateAccessControlRequest;
import org.exoplatform.ide.vfs.server.ContentStream;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class S3 extends AWSClient
{
   public S3(AWSAuthenticator authenticator)
   {
      super(authenticator);
   }

   /**
    * Creates a new Amazon S3 bucket in the specified region. US region by default.
    * To confirm creating bucket there is some constrains:
    *    - Bucket names should not contain underscores
    *    - Bucket names should be between 3 and 63 characters long
    *    - Bucket names should not end with a dash
    *    - Bucket names cannot contain uppercase characters
    *
    * @param name
    *    name of the bucket
    * @param region
    *    region, where bucket must be created
    *    valid values: null | us-west-1 | us-west-2 | EU | ap-southeast-1 | ap-northeast-1 | sa-east-1
    * @return
    *    the newly created bucket with provided information
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    */
   public S3Bucket createBucket(String name, S3Region region) throws AWSException
   {
      try
      {
         return createBucket(getS3Client(), name, region.toString());
      }
      catch (AmazonClientException e)
      {
         throw new AWSException(e);
      }
   }

   private S3Bucket createBucket(AmazonS3 s3, String name, String region)
   {
      Bucket amazonS3Bucket = s3.createBucket(new CreateBucketRequest(name, region));
      S3Bucket bucket = new S3BucketImpl(amazonS3Bucket.getName());
      if (amazonS3Bucket.getCreationDate() != null)
      {
         bucket.setCreated(amazonS3Bucket.getCreationDate().getTime());
      }
      if (amazonS3Bucket.getOwner() != null)
      {
         bucket.setOwner(
            new S3OwnerImpl(amazonS3Bucket.getOwner().getId(), amazonS3Bucket.getOwner().getDisplayName()));
      }
      return bucket;
   }

   /**
    * Returns list of all Amazon S3 buckets that the authenticated user owns
    *
    * @return
    *    a list of all of the Amazon S3 buckets with provided information owned by the authenticated user
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    */
   public List<S3Bucket> listBuckets() throws AWSException
   {
      try
      {
         return listBuckets(getS3Client());
      }
      catch (AmazonClientException e)
      {
         throw new AWSException(e);
      }
   }

   private List<S3Bucket> listBuckets(AmazonS3 s3)
   {
      List<Bucket> amazonS3Buckets = s3.listBuckets();
      List<S3Bucket> buckets = new ArrayList<S3Bucket>(amazonS3Buckets.size());

      for (Bucket amazonS3Bucket : amazonS3Buckets)
      {
         S3Bucket bucket = new S3BucketImpl(amazonS3Bucket.getName());
         if (amazonS3Bucket.getCreationDate() != null)
         {
            bucket.setCreated(amazonS3Bucket.getCreationDate().getTime());
         }
         if (amazonS3Bucket.getOwner() != null)
         {
            bucket.setOwner(
               new S3OwnerImpl(amazonS3Bucket.getOwner().getId(), amazonS3Bucket.getOwner().getDisplayName()));
         }
         buckets.add(bucket);
      }
      return buckets;
   }

   /**
    * Delete the S3 bucket. All objects in the bucket must be deleted before the bucket itself can be deleted.
    *
    * @param name
    *    S3 bucket name which will deleted
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    */
   public void deleteBucket(String name) throws AWSException
   {
      try
      {
         deleteBucket(getS3Client(), name);
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

   /**
    * Upload content from specified URL into Amazon S3 storage.
    * If content with the same key already exist it may be rewritten with new content.
    *
    * @param s3Bucket
    *    S3 bucket name
    * @param s3Key
    *    key, where content(object) should be stored
    * @param data
    *    data location from which we take stream
    * @return S3 object description
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    * @throws java.io.IOException
    *    if any i/o error occurs
    */
   public NewS3Object putObject(String s3Bucket, String s3Key, URL data) throws AWSException, IOException
   {
      URLConnection conn = null;
      try
      {
         conn = data.openConnection();
         return putObject(
            getS3Client(),
            s3Bucket, s3Key,
            conn.getInputStream(),
            conn.getContentType(),
            conn.getContentLength()
         );
      }
      finally
      {
         if (conn != null)
         {
            if ("http".equals(data.getProtocol()) || "https".equals(data.getProtocol()))
            {
               ((HttpURLConnection)conn).disconnect();
            }
         }
      }
   }

   /**
    * Uploads a new object into to specified Amazon S3 bucket.
    * If object with the same key already exist it may be rewritten with new content.
    *
    * @param s3Bucket
    *    S3 bucket name
    * @param s3Key
    *    key, where object should be stored
    * @param stream
    *    input stream of given file to upload
    * @param mediaType
    *    media type of file to upload
    * @param length
    *    size in bytes for file to upload
    * @return
    *    a result object containing the information returned by Amazon S3 for the newly created object.
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    * @throws IOException
    *    if any i/o error occurs
    */
   public NewS3Object putObject(String s3Bucket, String s3Key, InputStream stream, String mediaType, long length)
      throws AWSException, IOException
   {
      try
      {
         return putObject(getS3Client(), s3Bucket, s3Key, stream, mediaType, length);
      }
      catch (AmazonClientException e)
      {
         throw new AWSException(e);
      }
   }

   /**
    * Upload specified eXo IDE project to Amazon S3 storage. Project is zipped before uploading to S3.
    * If project with the same key already exist it may be rewritten with new content.
    *
    * @param s3Bucket
    *    S3 bucket name
    * @param s3Key
    *    key, where project should be stored
    * @param vfs
    *    instance of Virtual File system which used by eXo IDE
    * @param projectId
    *    id of project in eXo IDE virtual file system
    * @return S3 object description
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    * @throws org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException
    *    if any VirtualFileSystem error occurs
    * @throws IOException
    *    if any i/o error occurs
    */
   public NewS3Object uploadProject(String s3Bucket, String s3Key, VirtualFileSystem vfs, String projectId)
      throws AWSException, VirtualFileSystemException, IOException
   {
      ContentStream zippedProject = vfs.exportZip(projectId);
      try
      {
         return putObject(
            getS3Client(),
            s3Bucket,
            s3Key,
            zippedProject.getStream(),
            zippedProject.getMimeType(),
            zippedProject.getLength()
         );
      }
      catch (AmazonClientException e)
      {
         throw new AWSException(e);
      }
   }

   private NewS3Object putObject(AmazonS3 s3,
                                 String s3Bucket,
                                 String s3Key,
                                 InputStream stream,
                                 String mediaType,
                                 long length)
      throws IOException
   {
      try
      {
         ObjectMetadata metadata = new ObjectMetadata();
         if (length != -1)
         {
            metadata.setContentLength(length);
         }

         metadata.setContentType(mediaType);

         PutObjectResult result = s3.putObject(new PutObjectRequest(s3Bucket, s3Key, stream, metadata));
         return new NewS3ObjectImpl(s3Bucket, s3Key, result.getVersionId());
      }
      finally
      {
         stream.close();
      }
   }

   /**
    * Returns object which contains information about S3 objects which stored in specified bucket.
    * List results are always returned in lexicographic (alphabetical) order.
    *
    * @param s3Bucket
    *    name of bucket
    * @param prefix
    *    the prefix restricting what keys will be listed
    * @param nextMarker
    *    the key marker indicating where listing results should begin, must be equals or greater 0
    * @param maxKeys
    *    the maximum number of results to return, if max keys -1 then no limit to show objects in result set
    * @return
    *    result object containing S3 bucket name and listing objects in this bucket
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    */
   public S3ObjectsList listObjects(String s3Bucket, String prefix, String nextMarker, int maxKeys) throws AWSException
   {
      try
      {
         return listObjects(getS3Client(), s3Bucket, prefix, nextMarker, maxKeys);
      }
      catch (AmazonClientException e)
      {
         throw new AWSException(e);
      }
   }

   private S3ObjectsList listObjects(AmazonS3 s3, String s3Bucket, String prefix, String nextMarker, int maxKeys)
   {
      ObjectListing objectListing = s3.listObjects(
         new ListObjectsRequest()
            .withBucketName(s3Bucket)
            .withPrefix(prefix)
            .withMarker(nextMarker)
            .withMaxKeys((maxKeys == -1) ? null : maxKeys)
      );
      S3ObjectsList s3ObjectsList = new S3ObjectsListImpl();

      List<S3Object> s3Objects = new ArrayList<S3Object>(objectListing.getObjectSummaries().size());

      for (S3ObjectSummary object : objectListing.getObjectSummaries())
      {
         s3Objects.add(
            new S3ObjectImpl.Builder()
               .eTag(object.getETag())
               .owner(object.getOwner().getId(), object.getOwner().getDisplayName())
               .s3Bucket(object.getBucketName())
               .s3Key(object.getKey())
               .size(object.getSize())
               .storageClass(object.getStorageClass())
               .updated(object.getLastModified())
               .build()
         );
      }

      s3ObjectsList.setMaxKeys(objectListing.getMaxKeys());
      s3ObjectsList.setObjects(s3Objects);
      s3ObjectsList.setPrefix(objectListing.getPrefix());
      s3ObjectsList.setS3Bucket(objectListing.getBucketName());
      s3ObjectsList.setNextMarker(objectListing.getNextMarker());

      return s3ObjectsList;
   }

   /**
    * Deletes the S3 object in the specified bucket.
    *
    * @param s3Bucket
    *    S3 bucket name
    * @param s3key
    *    key, where object is stored
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    */
   public void deleteObject(String s3Bucket, String s3key) throws AWSException
   {
      try
      {
         deleteObject(getS3Client(), s3Bucket, s3key);
      }
      catch (AmazonClientException e)
      {
         throw new AWSException(e);
      }
   }

   private void deleteObject(AmazonS3 s3, String s3Bucket, String s3Key)
   {
      s3.deleteObject(new DeleteObjectRequest(s3Bucket, s3Key));
   }

   /**
    * Gets the object stored in Amazon S3 under the specified bucket and key.
    *
    * @param s3Bucket
    *    S3 bucket name
    * @param s3Key
    *    the key of object to be read
    * @return
    *    result object containing stream, content type and last modification date for object to be read
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    */
   public S3Content getObjectContent(String s3Bucket, String s3Key) throws AWSException
   {
      try
      {
         return getObjectContent(getS3Client(), s3Bucket, s3Key);
      }
      catch (AmazonClientException e)
      {
         throw new AWSException(e);
      }
   }

   private S3Content getObjectContent(AmazonS3 s3, String s3Bucket, String s3Key)
   {
      com.amazonaws.services.s3.model.S3Object s3Object = s3.getObject(new GetObjectRequest(s3Bucket, s3Key));

      return new S3Content(
         s3Object.getObjectContent(),
         s3Object.getObjectMetadata().getContentType(),
         s3Object.getObjectMetadata().getLastModified(),
         s3Object.getObjectMetadata().getContentLength()
      );
   }

   /**
    * Set versioning status for specified S3 bucket.
    *
    * @param s3Bucket
    *    S3 bucket name
    * @param status
    *    current status of versioning.
    *    Valid values:
    *    {@link org.exoplatform.ide.extension.aws.shared.s3.S3VersioningStatus#OFF S3VersioningStatus.OFF}
    *    {@link org.exoplatform.ide.extension.aws.shared.s3.S3VersioningStatus#SUSPENDED S3VersioningStatus.SUSPENDED}
    *    {@see org.exoplatform.ide.extension.aws.shared.s3.S3VersioningStatus#ENABLED S3VersioningStatus.ENABLED}
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    */
   public void setVersioningStatus(String s3Bucket, S3VersioningStatus status) throws AWSException
   {
      try
      {
         setVersioningStatus(getS3Client(), s3Bucket, status.toString());
      }
      catch (AmazonClientException e)
      {
         throw new AWSException(e);
      }
   }

   private void setVersioningStatus(AmazonS3 s3Client, String s3Bucket, String status)
   {
      BucketVersioningConfiguration configuration = new BucketVersioningConfiguration(status);
      s3Client.setBucketVersioningConfiguration(new SetBucketVersioningConfigurationRequest(s3Bucket, configuration));
   }

   /**
    * Delete specified objects in S3 bucket.
    *
    * @param s3Bucket
    *    S3 bucket name
    * @param s3Keys
    *    list of objects which should be deleted
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    */
   public void deleteObjects(String s3Bucket, List<S3KeyVersions> s3Keys) throws AWSException
   {
      try
      {
         deleteObjects(getS3Client(), s3Bucket, s3Keys);
      }
      catch (AmazonClientException e)
      {
         throw new AWSException(e);
      }
   }

   private void deleteObjects(AmazonS3 s3Client, String s3Bucket, List<S3KeyVersions> s3Keys)
   {
      List<DeleteObjectsRequest.KeyVersion> keyVersions = new ArrayList<DeleteObjectsRequest.KeyVersion>(s3Keys.size());

      for (S3KeyVersions s3Key : s3Keys)
      {
         for (String version : s3Key.getVersions())
         {
            keyVersions.add(new DeleteObjectsRequest.KeyVersion(s3Key.getS3Key(), version));
         }
      }

      s3Client.deleteObjects(new DeleteObjectsRequest(s3Bucket).withKeys(keyVersions)).getDeletedObjects();
   }

   /**
    * Delete specified S3 key version in specified bucket.
    *
    * @param s3Bucket
    *    name of the S3 bucket
    * @param s3Key
    *    name of the S3 key in the bucket
    * @param versionId
    *    version ID to be deleted
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    */
   public void deleteVersion(String s3Bucket, String s3Key, String versionId) throws AWSException
   {
      try
      {
         deleteVersion(getS3Client(), s3Bucket, s3Key, versionId);
      }
      catch (AmazonClientException e)
      {
         throw new AWSException(e);
      }
   }

   private void deleteVersion(AmazonS3 s3Client, String s3Bucket, String s3Key, String versionId)
   {
      s3Client.deleteVersion(new DeleteVersionRequest(s3Bucket, s3Key, versionId));
   }

   /**
    * Get information about versions in specified S3 bucket.
    *
    * @param s3Bucket
    *    name of the S3 bucket
    * @param prefix
    *    (optional) prefix, which restricting what keys will be listed in result set
    * @param keyMarker
    *    (optional) marker from where results must begin to show (from specified key)
    * @param versionIdMarker
    *    (optional) marker from where results must begin to show (from specified version ID)
    * @param delimiter
    *    (optional) causes keys that contain the same string between the prefix and first occurrence of the delimiter
    * @param maxResults
    *    (optional) the maximum numbers of results to return
    * @return
    *    list of objects that describes key, containing info about version ID, owner, last modification date etc.
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    */
   public List<S3ObjectVersion> listVersions(String s3Bucket,
                                             String prefix,
                                             String keyMarker,
                                             String versionIdMarker,
                                             String delimiter,
                                             Integer maxResults) throws AWSException
   {
      try
      {
         return listVersions(getS3Client(), s3Bucket, prefix, keyMarker, versionIdMarker, delimiter, maxResults);
      }
      catch (AmazonClientException e)
      {
         throw new AWSException(e);
      }
   }

   private List<S3ObjectVersion> listVersions(AmazonS3 s3Client,
                                              String s3Bucket,
                                              String prefix,
                                              String keyMarker,
                                              String versionIdMarker,
                                              String delimiter,
                                              Integer maxResults)
   {
      VersionListing versionListing = s3Client.listVersions(
         new ListVersionsRequest()
            .withBucketName(s3Bucket)
            .withPrefix(prefix)
            .withKeyMarker(keyMarker)
            .withVersionIdMarker(versionIdMarker)
            .withDelimiter(delimiter)
            .withMaxResults(maxResults)
      );

      List<S3ObjectVersion> objectVersions =
         new ArrayList<S3ObjectVersion>(versionListing.getVersionSummaries().size());

      for (S3VersionSummary versionSummary : versionListing.getVersionSummaries())
      {
         objectVersions.add(
            new S3ObjectVersionImpl.Builder()
               .withS3Bucket(versionSummary.getBucketName())
               .withS3Key(versionSummary.getKey())
               .withVersionId(versionSummary.getVersionId())
               .withLastModifiedDate(versionSummary.getLastModified().getTime())
               .withOwner(
                  new S3OwnerImpl(
                     versionSummary.getOwner().getId(),
                     versionSummary.getOwner().getDisplayName()
                  )
               )
               .withSize(versionSummary.getSize())
               .build()
         );
      }

      return objectVersions;
   }

   /**
    * Update Amazon S3 Bucket ACL
    *
    * @param s3Bucket
    *    name of the S3 bucket
    * @param s3UpdateAccessControls
    *    object contains lists with user permissions which should be added and deleted from Access Control List
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    */
   public void updateBucketAcl(String s3Bucket,
                               UpdateAccessControlRequest s3UpdateAccessControls) throws AWSException
   {
      try
      {
         updateAcl(getS3Client(), s3Bucket, null, null, s3UpdateAccessControls);
      }
      catch (AmazonClientException e)
      {
         throw new AWSException(e);
      }
   }

   /**
    * Update Amazon S3 Key ACL
    *
    * @param s3Bucket
    *    name of the S3 bucket
    * @param s3Key
    *    name of the S3 key
    * @param versionId
    *    (optional) version ID of the S3 key, if not defined it uses the latest version of key
    * @param s3UpdateAccessControls
    *    object contains lists with user permissions which should be added and deleted from Access Control List
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    */
   public void updateObjectAcl(String s3Bucket,
                               String s3Key,
                               String versionId,
                               UpdateAccessControlRequest s3UpdateAccessControls) throws AWSException
   {
      try
      {
         updateAcl(getS3Client(), s3Bucket, s3Key, versionId, s3UpdateAccessControls);
      }
      catch (AmazonClientException e)
      {
         throw new AWSException(e);
      }
   }

   private void updateAcl(AmazonS3 s3Client,
                          String s3Bucket,
                          String s3Key,
                          String versionId,
                          UpdateAccessControlRequest s3UpdateAccessControls)
   {
      AccessControlList currentAcl;

      List<S3AccessControl> s3AccessControlsToAdd = s3UpdateAccessControls.getS3AccessControlsToAdd();
      List<S3AccessControl> s3AccessControlsToDelete = s3UpdateAccessControls.getS3AccessControlsToDelete();

      if (s3Key == null)
      {
         currentAcl = s3Client.getBucketAcl(new GetBucketAclRequest(s3Bucket));
         if (s3AccessControlsToAdd != null)
            currentAcl.getGrants().addAll(createGrants(s3AccessControlsToAdd));
         if (s3AccessControlsToDelete != null)
            currentAcl.getGrants().removeAll(createGrants(s3AccessControlsToDelete));
         s3Client.setBucketAcl(new SetBucketAclRequest(s3Bucket, currentAcl));
      }
      else
      {
         currentAcl = s3Client.getObjectAcl(s3Bucket, s3Key, versionId);
         if (s3AccessControlsToAdd != null)
            currentAcl.getGrants().addAll(createGrants(s3AccessControlsToAdd));
         if (s3AccessControlsToDelete != null)
            currentAcl.getGrants().removeAll(createGrants(s3AccessControlsToDelete));
         s3Client.setObjectAcl(s3Bucket, s3Key, versionId, currentAcl);
      }
   }

   /**
    * Get list of permissions for the S3 bucket
    *
    * @param s3Bucket
    *    name of the S3 bucket
    * @return
    *    list contains information about user, his identity and permission for this bucket
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    */
   public List<S3AccessControl> getBucketAcl(String s3Bucket) throws AWSException
   {
      try
      {
         return getAcl(getS3Client(), s3Bucket, null, null);
      }
      catch (AmazonClientException e)
      {
         throw new AWSException(e);
      }
   }

   /**
    * Get list of permissions for the S3 key
    *
    * @param s3Bucket
    *    name of the S3 bucket
    * @param s3Key
    *    name of the S3 key
    * @param versionId
    *    (optional) version ID of the S3 key, if not defined it uses the latest version of key
    * @return
    *    list contains information about user, his identity and permission for this key
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    */
   public List<S3AccessControl> getObjectAcl(String s3Bucket, String s3Key, String versionId) throws AWSException
   {
      try
      {
         return getAcl(getS3Client(), s3Bucket, s3Key, versionId);
      }
      catch (AmazonClientException e)
      {
         throw new AWSException(e);
      }
   }

   private List<S3AccessControl> getAcl(AmazonS3 s3Client, String s3Bucket, String s3key, String versionId)
   {
      if (s3key == null)
      {
         return createAccessControls(s3Client.getBucketAcl(new GetBucketAclRequest(s3Bucket)).getGrants());
      }

      return createAccessControls(s3Client.getObjectAcl(s3Bucket, s3key, versionId).getGrants());
   }
   //

   private List<S3AccessControl> createAccessControls(Set<Grant> grants)
   {
      List<S3AccessControl> s3AccessControls = new ArrayList<S3AccessControl>(grants.size());

      for (Grant grant : grants)
      {
         s3AccessControls.add(
            new S3AccessControlImpl(
               S3IdentityType.fromValue(grant.getGrantee().getTypeIdentifier()),
               S3Permission.fromValue(grant.getPermission().toString()),
               grant.getGrantee().getIdentifier()
            )
         );
      }

      return s3AccessControls;
   }

   private List<Grant> createGrants(List<S3AccessControl> s3AccessControls)
   {
      List<Grant> grants = new ArrayList<Grant>(s3AccessControls.size());

      for (S3AccessControl ac : s3AccessControls)
      {
         S3IdentityType identityType = ac.getIdentityType();
         String identifier = ac.getIdentifier();

         Grantee grantee;

         switch (identityType)
         {
            case GROUP:
               grantee = GroupGrantee.parseGroupGrantee(S3IdentityGroupType.fromValue(identifier).getUri());
               break;
            case CANONICAL:
               grantee = new CanonicalGrantee(identifier);
               break;
            case EMAIL:
               grantee = new EmailAddressGrantee(identifier);
               break;
            default:
               throw new IllegalArgumentException("Invalid identity type.");
         }

         grants.add(new Grant(grantee, Permission.parsePermission(ac.getPermission().toString())));
      }

      return grants;
   }

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
