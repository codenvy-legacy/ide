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

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Image;
import com.amazonaws.services.ec2.model.Placement;
import com.amazonaws.services.ec2.model.Region;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.SecurityGroup;
import org.exoplatform.ide.extension.aws.server.AWSAuthenticator;
import org.exoplatform.ide.extension.aws.server.AWSException;
import org.exoplatform.ide.extension.aws.shared.ec2.Architecture;
import org.exoplatform.ide.extension.aws.shared.ec2.ImageInfo;
import org.exoplatform.ide.extension.aws.shared.ec2.ImagesList;
import org.exoplatform.ide.extension.aws.shared.ec2.KeyPairInfo;
import org.exoplatform.ide.extension.aws.shared.ec2.RegionInfo;
import org.exoplatform.ide.extension.aws.shared.ec2.SecurityGroupInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class EC2
{
   private final AWSAuthenticator authenticator;

   public EC2(AWSAuthenticator authenticator)
   {
      this.authenticator = authenticator;
   }

   /**
    * Get available AMIs.
    *
    * @param owner
    *    owner of AMI. May be specified ID of amazon user or 'self' to get own images. If this parameter is not
    *    specified 'amazon' assumed
    * @param isPublic
    *    if <code>true</code> get public images and private otherwise
    * @param architecture
    *    image architecture i386|x86_64. If not specified get images of both architectures
    * @param skipCount
    *    how may items skip in original result. Must be equals or greater then 0
    * @param maxItems
    *    how may items include result. If -1 then no limit of max images in result set
    * @return list of available images
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    * @throws IllegalArgumentException
    *    if skipCount parameter is negative or greater then total number of images
    */
   public ImagesList listImages(String owner,
                                boolean isPublic,
                                Architecture architecture,
                                int skipCount,
                                int maxItems) throws AWSException
   {
      AmazonEC2 ec2Client = getEC2Client();
      try
      {
         return listImages(ec2Client, owner, isPublic, architecture, skipCount, maxItems);
      }
      catch (AmazonClientException e)
      {
         throw new AWSException(e);
      }
      finally
      {
         ec2Client.shutdown();
      }
   }

   private ImagesList listImages(AmazonEC2 ec2Client,
                                 String owner,
                                 boolean isPublic,
                                 Architecture architecture,
                                 int skipCount,
                                 int maxItems) throws AWSException
   {
      if (skipCount < 0)
      {
         throw new IllegalArgumentException("'skipCount' parameter may not be negative. ");
      }
      if (owner == null)
      {
         owner = "amazon";
      }
      DescribeImagesRequest request = new DescribeImagesRequest()
         .withOwners(owner)
         .withFilters(new Filter().withName("is-public").withValues(Boolean.toString(isPublic)),
            new Filter().withName("image-type").withValues("machine"));
      if (architecture != null)
      {
         request.withFilters(new Filter().withName("architecture").withValues(architecture.toString()));
      }
      List<Image> ec2ImagesList = ec2Client.describeImages(request).getImages();
      final int totalNumber = ec2ImagesList.size();
      Iterator<Image> ec2ImagesIterator = ec2ImagesList.iterator();
      try
      {
         if (skipCount > 0)
         {
            int skip = skipCount;
            while (skip-- > 0)
            {
               ec2ImagesIterator.next();
            }
         }
      }
      catch (NoSuchElementException nse)
      {
         throw new IllegalArgumentException("'skipCount' parameter: '"
            + skipCount + "' is greater then total number of images. ");
      }

      List<ImageInfo> images = new ArrayList<ImageInfo>();
      for (int count = 0; ec2ImagesIterator.hasNext() && (maxItems < 0 || count < maxItems); count++)
      {
         Image ec2Image = ec2ImagesIterator.next();
         images.add(new ImageInfoImpl.Builder()
            .amiId(ec2Image.getImageId())
            .manifest(ec2Image.getImageLocation())
            .state(ec2Image.getState())
            .ownerId(ec2Image.getOwnerId())
            .ownerAlias(ec2Image.getImageOwnerAlias())
            .tags(ec2Image.getTags()).build());
      }

      ImagesList imagesList = new ImagesListImpl();
      imagesList.setImages(images);
      imagesList.setTotal(totalNumber);
      imagesList.setHasMore(ec2ImagesIterator.hasNext());
      if (ec2ImagesIterator.hasNext())
      {
         imagesList.setNextSkip(skipCount + images.size());
         imagesList.setMaxItems(maxItems);
      }
      return imagesList;
   }

   //

   public List<KeyPairInfo> listKeyPairs() throws AWSException
   {
      AmazonEC2 ec2Client = getEC2Client();
      try
      {
         return listKeyPairs(ec2Client);
      }
      catch (AmazonClientException e)
      {
         throw new AWSException(e);
      }
      finally
      {
         ec2Client.shutdown();
      }
   }

   private List<KeyPairInfo> listKeyPairs(AmazonEC2 ec2Client) throws AWSException
   {
      List<com.amazonaws.services.ec2.model.KeyPairInfo> ec2KeyPair = ec2Client.describeKeyPairs().getKeyPairs();
      List<KeyPairInfo> keyPair = new ArrayList<KeyPairInfo>(ec2KeyPair.size());
      for (com.amazonaws.services.ec2.model.KeyPairInfo ec2KeyPairInfo : ec2KeyPair)
      {
         keyPair.add(new KeyPairInfoImpl(ec2KeyPairInfo.getKeyName(), ec2KeyPairInfo.getKeyFingerprint()));
      }
      return keyPair;
   }

   //

   public List<SecurityGroupInfo> listSecurityGroups() throws AWSException
   {
      AmazonEC2 ec2Client = getEC2Client();
      try
      {
         return listSecurityGroups(ec2Client);
      }
      catch (AmazonClientException e)
      {
         throw new AWSException(e);
      }
      finally
      {
         ec2Client.shutdown();
      }
   }

   private List<SecurityGroupInfo> listSecurityGroups(AmazonEC2 ec2Client) throws AWSException
   {
      List<SecurityGroup> ec2SecurityGroups = ec2Client.describeSecurityGroups().getSecurityGroups();
      List<SecurityGroupInfo> securityGroup = new ArrayList<SecurityGroupInfo>(ec2SecurityGroups.size());
      for (SecurityGroup ec2SecurityGroup : ec2SecurityGroups)
      {
         securityGroup.add(new SecurityGroupInfoImpl(ec2SecurityGroup.getGroupId(), ec2SecurityGroup.getGroupName(),
            ec2SecurityGroup.getOwnerId(), ec2SecurityGroup.getDescription()));
      }
      return securityGroup;
   }

   //

   public List<RegionInfo> listRegions() throws AWSException
   {
      AmazonEC2 ec2Client = getEC2Client();
      try
      {
         return listRegions(ec2Client);
      }
      catch (AmazonClientException e)
      {
         throw new AWSException(e);
      }
      finally
      {
         ec2Client.shutdown();
      }
   }

   private List<RegionInfo> listRegions(AmazonEC2 ec2Client) throws AWSException
   {
      List<Region> ec2Regions = ec2Client.describeRegions().getRegions();
      List<RegionInfo> regions = new ArrayList<RegionInfo>(ec2Regions.size());
      for (Region region : ec2Regions)
      {
         regions.add(new RegionInfoImpl(region.getRegionName(), region.getEndpoint()));
      }
      return regions;
   }

   //

   public List<String> listAvailabilityZones() throws AWSException
   {
      AmazonEC2 ec2Client = getEC2Client();
      try
      {
         return listAvailabilityZones(ec2Client);
      }
      catch (AmazonClientException e)
      {
         throw new AWSException(e);
      }
      finally
      {
         ec2Client.shutdown();
      }
   }

   private List<String> listAvailabilityZones(AmazonEC2 ec2Client) throws AWSException
   {
      List<AvailabilityZone> ec2AvailabilityZones = ec2Client.describeAvailabilityZones().getAvailabilityZones();
      List<String> availabilityZones = new ArrayList<String>(ec2AvailabilityZones.size());
      for (AvailabilityZone ec2AvailabilityZone : ec2AvailabilityZones)
      {
         availabilityZones.add(ec2AvailabilityZone.getZoneName());
      }
      return availabilityZones;
   }

   //

   public void runInstance(String imageId,
                           String instanceType,
                           int numberOfInstances,
                           String keyName,
                           List<String> securityGroupsIds,
                           String availabilityZone) throws AWSException
   {
      AmazonEC2 ec2Client = getEC2Client();
      try
      {
         runInstance(ec2Client, imageId, instanceType, numberOfInstances, keyName, securityGroupsIds, availabilityZone);
      }
      catch (AmazonClientException e)
      {
         throw new AWSException(e);
      }
      finally
      {
         ec2Client.shutdown();
      }
   }

   private void runInstance(AmazonEC2 ec2Client,
                            String imageId,
                            String instanceType,
                            int numberOfInstances,
                            String keyName,
                            List<String> securityGroupsIds,
                            String availabilityZone)
   {
      ec2Client.runInstances(new RunInstancesRequest()
         .withImageId(imageId)
         .withInstanceType(instanceType)
         .withMinCount(numberOfInstances)
         .withMaxCount(numberOfInstances)
         .withKeyName(keyName)
         .withSecurityGroupIds(securityGroupsIds)
         .withPlacement(new Placement().withAvailabilityZone(availabilityZone)));
   }

   //

   protected AmazonEC2 getEC2Client() throws AWSException
   {
      final AWSCredentials credentials = authenticator.getCredentials();
      if (credentials == null)
      {
         throw new AWSException("Authentication required.");
      }
      return new AmazonEC2Client(credentials);
   }
}
