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
package org.exoplatform.ide.client.module.gadget.service;

import com.google.gwt.json.client.JSONObject;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class GadgetMetadata
{
   
   private String source; 
   
   private JSONObject userPrefs;

   private String author;

   private String title;

   private String titleUrl;

   private double height;

   private String authorLocation;

   private String[] features;

   private boolean showStats;

   private String screenshot;

   private double moduleId;

   private String authorEmail;

   private boolean singleton;

   private double width;

   private JSONObject links;

   private String authorLink;

   private String url;
   
   private String iframeUrl;

   private boolean scaling;

   private String directoryTitle;

   private String authorAffiliation;

   private String thumbnail;

   private boolean scrolling;

   private JSONObject views;

   private String[] categories;

   private String authorPhoto;

   private boolean showInDirectory;
   
   private String securityToken;
   
   public static final String GADGETS = "gadgets";

   public static final String USER_PREFS = "userPrefs";

   public static final String AUTHOR = "author";

   public static final String TITLE = "title";

   public static final String TITLE_URL = "titleUrl";

   public static final String HEIGHT = "height";

   public static final String AUTHOR_LOCATION = "authorLocation";

   public static final String FEATURES = "features";

   public static final String SHOWSTATS = "showStats";

   public static final String SCREENSHOT = "screenshot";

   public static final String MODULE_ID = "moduleId";

   public static final String AUTHOR_EMAIL = "authorEmail";

   public static final String SINGLETON = "singleton";

   public static final String WIDTH = "width";

   public static final String LINKS = "links";

   public static final String AUTHOR_LINK = "authorLink";

   public static final String URL = "url";
   
   public static final String IFRAME_URL = "iframeUrl";

   public static final String SCALING = "scaling";

   public static final String DIRECTORY_TITLE = "directoryTitle";

   public static final String AUTHOR_AFFILIATION = "authorAffiliation";

   public static final String THUMBNAIL = "thumbnail";

   public static final String SCROLLING = "scrolling";

   public static final String VIEWS = "views";

   public static final String CATEGORIES = "categories";

   public static final String AUTHOR_PHOTO = "authorPhoto";

   public static final String SHOW_IN_DIRECTORY = "showInDirectory";

   /**
    * @return the userPrefs
    */
   public JSONObject getUserPrefs()
   {
      return userPrefs;
   }

   /**
    * @param userPrefs the userPrefs to set
    */
   public void setUserPrefs(JSONObject userPrefs)
   {
      this.userPrefs = userPrefs;
   }

   /**
    * @return the author
    */
   public String getAuthor()
   {
      return author;
   }

   /**
    * @param author the author to set
    */
   public void setAuthor(String author)
   {
      this.author = author;
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
    * @return the titleUrl
    */
   public String getTitleUrl()
   {
      return titleUrl;
   }

   /**
    * @param titleUrl the titleUrl to set
    */
   public void setTitleUrl(String titleUrl)
   {
      this.titleUrl = titleUrl;
   }

   /**
    * @return the height
    */
   public double getHeight()
   {
      return height;
   }

   /**
    * @param height the height to set
    */
   public void setHeight(double height)
   {
      this.height = height;
   }

   /**
    * @return the authorLocation
    */
   public String getAuthorLocation()
   {
      return authorLocation;
   }

   /**
    * @param authorLocation the authorLocation to set
    */
   public void setAuthorLocation(String authorLocation)
   {
      this.authorLocation = authorLocation;
   }

   /**
    * @return the features
    */
   public String[] getFeatures()
   {
      return features;
   }

   /**
    * @param features the features to set
    */
   public void setFeatures(String[] features)
   {
      this.features = features;
   }

   /**
    * @return the showStats
    */
   public boolean isShowStats()
   {
      return showStats;
   }

   /**
    * @param showStats the showStats to set
    */
   public void setShowStats(boolean showStats)
   {
      this.showStats = showStats;
   }

   /**
    * @return the screenshot
    */
   public String getScreenshot()
   {
      return screenshot;
   }

   /**
    * @param screenshot the screenshot to set
    */
   public void setScreenshot(String screenshot)
   {
      this.screenshot = screenshot;
   }

   /**
    * @return the moduleId
    */
   public double getModuleId()
   {
      return moduleId;
   }

   /**
    * @param moduleId the moduleId to set
    */
   public void setModuleId(double moduleId)
   {
      this.moduleId = moduleId;
   }

   /**
    * @return the authorEmail
    */
   public String getAuthorEmail()
   {
      return authorEmail;
   }

   /**
    * @param authorEmail the authorEmail to set
    */
   public void setAuthorEmail(String authorEmail)
   {
      this.authorEmail = authorEmail;
   }

   /**
    * @return the singleton
    */
   public boolean isSingleton()
   {
      return singleton;
   }

   /**
    * @param singleton the singleton to set
    */
   public void setSingleton(boolean singleton)
   {
      this.singleton = singleton;
   }

   /**
    * @return the width
    */
   public double getWidth()
   {
      return width;
   }

   /**
    * @param width the width to set
    */
   public void setWidth(double width)
   {
      this.width = width;
   }

   /**
    * @return the links
    */
   public JSONObject getLinks()
   {
      return links;
   }

   /**
    * @param links the links to set
    */
   public void setLinks(JSONObject links)
   {
      this.links = links;
   }

   /**
    * @return the authorLink
    */
   public String getAuthorLink()
   {
      return authorLink;
   }

   /**
    * @param authorLink the authorLink to set
    */
   public void setAuthorLink(String authorLink)
   {
      this.authorLink = authorLink;
   }

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
    * @return the iframeUrl
    */
   public String getIframeUrl()
   {
      return iframeUrl;
   }

   /**
    * @param iframeUrl the iframeUrl to set
    */
   public void setIframeUrl(String iframeUrl)
   {
      this.iframeUrl = iframeUrl;
   }

   /**
    * @return the scaling
    */
   public boolean isScaling()
   {
      return scaling;
   }

   /**
    * @param scaling the scaling to set
    */
   public void setScaling(boolean scaling)
   {
      this.scaling = scaling;
   }

   /**
    * @return the directoryTitle
    */
   public String getDirectoryTitle()
   {
      return directoryTitle;
   }

   /**
    * @param directoryTitle the directoryTitle to set
    */
   public void setDirectoryTitle(String directoryTitle)
   {
      this.directoryTitle = directoryTitle;
   }

   /**
    * @return the authorAffiliation
    */
   public String getAuthorAffiliation()
   {
      return authorAffiliation;
   }

   /**
    * @param authorAffiliation the authorAffiliation to set
    */
   public void setAuthorAffiliation(String authorAffiliation)
   {
      this.authorAffiliation = authorAffiliation;
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
    * @return the scrolling
    */
   public boolean isScrolling()
   {
      return scrolling;
   }

   /**
    * @param scrolling the scrolling to set
    */
   public void setScrolling(boolean scrolling)
   {
      this.scrolling = scrolling;
   }

   /**
    * @return the views
    */
   public JSONObject getViews()
   {
      return views;
   }

   /**
    * @param views the views to set
    */
   public void setViews(JSONObject views)
   {
      this.views = views;
   }

   /**
    * @return the categories
    */
   public String[] getCategories()
   {
      return categories;
   }

   /**
    * @param categories the categories to set
    */
   public void setCategories(String[] categories)
   {
      this.categories = categories;
   }

   /**
    * @return the authorPhoto
    */
   public String getAuthorPhoto()
   {
      return authorPhoto;
   }

   /**
    * @param authorPhoto the authorPhoto to set
    */
   public void setAuthorPhoto(String authorPhoto)
   {
      this.authorPhoto = authorPhoto;
   }

   /**
    * @return the showInDirectory
    */
   public boolean isShowInDirectory()
   {
      return showInDirectory;
   }

   /**
    * @param showInDirectory the showInDirectory to set
    */
   public void setShowInDirectory(boolean showInDirectory)
   {
      this.showInDirectory = showInDirectory;
   }
   
   
   /**
    * @return the securityToken
    */
   public String getSecurityToken()
   {
      return securityToken;
   }

   /**
    * @param securityToken the securityToken to set
    */
   public void setSecurityToken(String securityToken)
   {
      this.securityToken = securityToken;
   }

   public String getSource()
   {
      return source;
   }
   
   public void setSource(String source)
   {
      this.source = source;
   }
   

}
