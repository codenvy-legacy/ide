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
package org.exoplatform.ide.extension.netvibes.client.model;

/**
 * Keeps data for deploying UWA widget to Netvibes Ecosystem.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 1, 2010 $
 *
 */
public class DeployWidget
{
   /**
    * Widget location.
    */
   private String url;

   /**
    * Widget's title.
    */
   private String title;

   /**
    * Description of widget.
    */
   private String description;

   /**
    * Widget's version (optional).
    */
   private String version;

   /**
    * The main language of the widget.
    */
   private String mainLanguage;

   /**
    * Descriptive keywords of the widget(optional).
    */
   private String keywords;

   /**
    * Thumbnail location(optional).
    */
   private String thumbnail;

   /**
    * Most appropriate region.
    */
   private String region;

   /**
    * Most appropriate category name.
    */
   private String categoryName;

   /**
    * Most appropriate category id.
    */
   private String categoryId;

   /**
    * User's API key.
    */
   private String apiKey;

   /**
    * User's secrete key.
    */
   private String secretKey;

   /**
    * @return the url
    */
   public String getUrl()
   {
      return url;
   }

   /**
    * @param url the url to set
    */
   public void setUrl(String url)
   {
      this.url = url;
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
    * @return the version
    */
   public String getVersion()
   {
      return version;
   }

   /**
    * @param version the version to set
    */
   public void setVersion(String version)
   {
      this.version = version;
   }

   /**
    * @return the mainLanguage
    */
   public String getMainLanguage()
   {
      return mainLanguage;
   }

   /**
    * @param mainLanguage the mainLanguage to set
    */
   public void setMainLanguage(String mainLanguage)
   {
      this.mainLanguage = mainLanguage;
   }

   /**
    * @return the keywords
    */
   public String getKeywords()
   {
      return keywords;
   }

   /**
    * @param keywords the keywords to set
    */
   public void setKeywords(String keywords)
   {
      this.keywords = keywords;
   }

   /**
    * @return the thumbnail
    */
   public String getThumbnail()
   {
      return thumbnail;
   }

   /**
    * @param thumbnail the thumbnail to set
    */
   public void setThumbnail(String thumbnail)
   {
      this.thumbnail = thumbnail;
   }

   /**
    * @return the region
    */
   public String getRegion()
   {
      return region;
   }

   /**
    * @param region the region to set
    */
   public void setRegion(String region)
   {
      this.region = region;
   }

   /**
    * @return the categoryName
    */
   public String getCategoryName()
   {
      return categoryName;
   }

   /**
    * @param categoryName the categoryName to set
    */
   public void setCategoryName(String categoryName)
   {
      this.categoryName = categoryName;
   }

   /**
    * @return the categoryId
    */
   public String getCategoryId()
   {
      return categoryId;
   }

   /**
    * @param categoryId the categoryId to set
    */
   public void setCategoryId(String categoryId)
   {
      this.categoryId = categoryId;
   }

   /**
    * @return the apiKey
    */
   public String getApiKey()
   {
      return apiKey;
   }

   /**
    * @param apiKey the apiKey to set
    */
   public void setApiKey(String apiKey)
   {
      this.apiKey = apiKey;
   }

   /**
    * @return the secretKey
    */
   public String getSecretKey()
   {
      return secretKey;
   }

   /**
    * @param secretKey the secretKey to set
    */
   public void setSecretKey(String secretKey)
   {
      this.secretKey = secretKey;
   }
}
