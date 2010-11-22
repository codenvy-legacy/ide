/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.opensocial.data.model;

import java.util.List;

/**
 * Albums support collections of media items (video, image, sound).
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Nov 19, 2010 $
 *
 */
public class Album
{
   /**
    * Description of the album.
    */
   private String description;
   
   /**
    * Unique identifier for the album.
    */
   private String id;
   
   /**
    * Location corresponding to the album.
    */
   private Address location;
   
   /**
    * Number of items in the album.
    */
   private Integer mediaItemCount;
   
   /**
    * Array of strings identifying the mime-types of media items in the Album.
    */
   private List<String> mediaMimeType;
   
   /**
    * Array of MediaItem types, types are one of: audio, image, video.
    */
   private List<String> mediaType;
   
   /**
    * ID of the owner of the album.
    */
   private String ownerId;
   
   
   /**
    * URL to a thumbnail cover of the album.
    */
   private String thumbnailUrl;
   
   /**
    * The title of the album. 
    */
   private String title;

   /**
    * @return the description
    */
   public String getDescription()
   {
      return description;
   }

   /**
    * @param description the description to set
    */
   public void setDescription(String description)
   {
      this.description = description;
   }

   /**
    * @return the id
    */
   public String getId()
   {
      return id;
   }

   /**
    * @param id the id to set
    */
   public void setId(String id)
   {
      this.id = id;
   }

   /**
    * @return the location
    */
   public Address getLocation()
   {
      return location;
   }

   /**
    * @param location the location to set
    */
   public void setLocation(Address location)
   {
      this.location = location;
   }

   /**
    * @return the mediaItemCount
    */
   public Integer getMediaItemCount()
   {
      return mediaItemCount;
   }

   /**
    * @param mediaItemCount the mediaItemCount to set
    */
   public void setMediaItemCount(Integer mediaItemCount)
   {
      this.mediaItemCount = mediaItemCount;
   }

   /**
    * @return the mediaMimeType
    */
   public List<String> getMediaMimeType()
   {
      return mediaMimeType;
   }

   /**
    * @param mediaMimeType the mediaMimeType to set
    */
   public void setMediaMimeType(List<String> mediaMimeType)
   {
      this.mediaMimeType = mediaMimeType;
   }

   /**
    * @return the mediaType
    */
   public List<String> getMediaType()
   {
      return mediaType;
   }

   /**
    * @param mediaType the mediaType to set
    */
   public void setMediaType(List<String> mediaType)
   {
      this.mediaType = mediaType;
   }

   /**
    * @return the ownerId
    */
   public String getOwnerId()
   {
      return ownerId;
   }

   /**
    * @param ownerId the ownerId to set
    */
   public void setOwnerId(String ownerId)
   {
      this.ownerId = ownerId;
   }

   /**
    * @return the thumbnailUrl
    */
   public String getThumbnailUrl()
   {
      return thumbnailUrl;
   }

   /**
    * @param thumbnailUrl the thumbnailUrl to set
    */
   public void setThumbnailUrl(String thumbnailUrl)
   {
      this.thumbnailUrl = thumbnailUrl;
   }

   /**
    * @return the title
    */
   public String getTitle()
   {
      return title;
   }

   /**
    * @param title the title to set
    */
   public void setTitle(String title)
   {
      this.title = title;
   }
}
