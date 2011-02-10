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
package org.exoplatform.ide.extension.gadget.server.opensocial.model;

import java.util.ArrayList;

/**
 * Represents images, movies, and audio. 
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Nov 19, 2010 $
 *
 */
public class MediaItem
{
   /**
    * Album to which the media item belongs.
    */
   private String album_id;

   /**
    * Creation datetime associated with the media item - assigned by container in UTC. 
    */
   private String created;

   /**
    * Description of the media item.
    */
   private String description;

   /**
    * Playtime length in seconds.
    * Set to -1/not defined if unknown.
    */
   private Integer duration;

   /**
    * Number of bytes (set to -1/undefined if unknown).
    */
   private Long file_size;

   /**
    * Id Associated with the media item.
    */
   private String id;

   /**
    * Language associated with the media item in ISO 639-3 format.
    */
   private String language;

   /**
    * Update datetime associated with the media item - assigned by container in UTC.
    */
   private String lastUpdated;

   /**
    * Location corresponding to the media item.
    */
   private Address location;

   /**
    * The MIME type of media, specified as a string.
    */
   private String mimeType;

   /**
    * Number of comments on the media item.
    */
   private Integer numComments;

   /**
    * Number of views for the media item.
    */
   private Integer numViews;

   /**
    * Number of votes received for voting.
    */
   private Integer numVotes;

   /**
    * Average rating of the media item on a scale of 0-10.
    */
   private Integer rating;

   /**
    * For streaming/live content, datetime when the content is available.
    */
   private String startTime;

   /**
    * Array of string (IDs) of people tagged in the media item. 
    */
   private ArrayList<String> taggedPeople;

   /**
    * Tags associated with this media item.
    */
   private ArrayList<String> tags;

   /**
    * URL to a thumbnail image of the media item. 
    */
   private String thumbnailURL;

   /**
    * Describing the media item. 
    */
   private String title;

   /**
    * The type of media, specified as a MediaItem.Type object.
    */
   private String type;

   /**
    *  URL where the media can be found. 
    */
   private String URL;

   /**
    * @return the album_id
    */
   public String getAlbum_id()
   {
      return album_id;
   }

   /**
    * @param album_id the album_id to set
    */
   public void setAlbum_id(String album_id)
   {
      this.album_id = album_id;
   }

   /**
    * @return the created
    */
   public String getCreated()
   {
      return created;
   }

   /**
    * @param created the created to set
    */
   public void setCreated(String created)
   {
      this.created = created;
   }

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
    * @return the duration
    */
   public Integer getDuration()
   {
      return duration;
   }

   /**
    * @param duration the duration to set
    */
   public void setDuration(Integer duration)
   {
      this.duration = duration;
   }

   /**
    * @return the file_size
    */
   public Long getFile_size()
   {
      return file_size;
   }

   /**
    * @param file_size the file_size to set
    */
   public void setFile_size(Long file_size)
   {
      this.file_size = file_size;
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
    * @return the language
    */
   public String getLanguage()
   {
      return language;
   }

   /**
    * @param language the language to set
    */
   public void setLanguage(String language)
   {
      this.language = language;
   }

   /**
    * @return the lastUpdated
    */
   public String getLastUpdated()
   {
      return lastUpdated;
   }

   /**
    * @param lastUpdated the lastUpdated to set
    */
   public void setLastUpdated(String lastUpdated)
   {
      this.lastUpdated = lastUpdated;
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
    * @return the mimeType
    */
   public String getMimeType()
   {
      return mimeType;
   }

   /**
    * @param mimeType the mimeType to set
    */
   public void setMimeType(String mimeType)
   {
      this.mimeType = mimeType;
   }

   /**
    * @return the numComments
    */
   public Integer getNumComments()
   {
      return numComments;
   }

   /**
    * @param numComments the numComments to set
    */
   public void setNumComments(Integer numComments)
   {
      this.numComments = numComments;
   }

   /**
    * @return the numViews
    */
   public Integer getNumViews()
   {
      return numViews;
   }

   /**
    * @param numViews the numViews to set
    */
   public void setNumViews(Integer numViews)
   {
      this.numViews = numViews;
   }

   /**
    * @return the numVotes
    */
   public Integer getNumVotes()
   {
      return numVotes;
   }

   /**
    * @param numVotes the numVotes to set
    */
   public void setNumVotes(Integer numVotes)
   {
      this.numVotes = numVotes;
   }

   /**
    * @return the rating
    */
   public Integer getRating()
   {
      return rating;
   }

   /**
    * @param rating the rating to set
    */
   public void setRating(Integer rating)
   {
      this.rating = rating;
   }

   /**
    * @return the startTime
    */
   public String getStartTime()
   {
      return startTime;
   }

   /**
    * @param startTime the startTime to set
    */
   public void setStartTime(String startTime)
   {
      this.startTime = startTime;
   }

   /**
    * @return the taggedPeople
    */
   public ArrayList<String> getTaggedPeople()
   {
      if (taggedPeople == null)
      {
         taggedPeople = new ArrayList<String>();
      }
      return taggedPeople;
   }

   /**
    * @param taggedPeople the taggedPeople to set
    */
   public void setTaggedPeople(ArrayList<String> taggedPeople)
   {
      this.taggedPeople = taggedPeople;
   }

   /**
    * @return the tags
    */
   public ArrayList<String> getTags()
   {
      if (tags == null)
      {
         tags = new ArrayList<String>();
      }
      return tags;
   }

   /**
    * @param tags the tags to set
    */
   public void setTags(ArrayList<String> tags)
   {
      this.tags = tags;
   }

   /**
    * @return the thumbnailURL
    */
   public String getThumbnailURL()
   {
      return thumbnailURL;
   }

   /**
    * @param thumbnailURL the thumbnailURL to set
    */
   public void setThumbnailURL(String thumbnailURL)
   {
      this.thumbnailURL = thumbnailURL;
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

   /**
    * @return the type
    */
   public String getType()
   {
      return type;
   }

   /**
    * @param type the type to set
    */
   public void setType(String type)
   {
      this.type = type;
   }

   /**
    * @return the uRL
    */
   public String getURL()
   {
      return URL;
   }

   /**
    * @param uRL the uRL to set
    */
   public void setURL(String uRL)
   {
      URL = uRL;
   }

}
