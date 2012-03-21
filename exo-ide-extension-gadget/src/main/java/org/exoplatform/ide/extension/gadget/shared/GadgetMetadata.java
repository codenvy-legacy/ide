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
package org.exoplatform.ide.extension.gadget.shared;

import java.util.List;

/**
 * Metadata of OpenSocial gadget.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: GadgetMetadata.java Mar 20, 2012 16:12:11 PM azatsarynnyy $
 *
 */
public interface GadgetMetadata
{

   /**
    * @return the userPrefs
    */
   public UserPrefs getUserPrefs();

   /**
    * @param userPrefs the userPrefs to set
    */
   public void setUserPrefs(UserPrefs userPrefs);

   /**
    * @return the title
    */
   public String getTitle();

   /**
    * @param title the title to set
    */
   public void setTitle(String title);

   /**
    * @return the titleUrl
    */
   public String getTitleUrl();

   /**
    * @param titleUrl the titleUrl to set
    */
   public void setTitleUrl(String titleUrl);

   /**
    * @return the height
    */
   public double getHeight();

   /**
    * @param height the height to set
    */
   public void setHeight(double height);

   /**
    * @return the features
    */
   public List<String> getFeatures();

   /**
    * @param features the features to set
    */
   public void setFeatures(List<String> features);

   /**
    * @return the showStats
    */
   public boolean isShowStats();

   /**
    * @param showStats the showStats to set
    */
   public void setShowStats(boolean showStats);

   /**
    * @return the screenshot
    */
   public String getScreenshot();

   /**
    * @param screenshot the screenshot to set
    */
   public void setScreenshot(String screenshot);

   /**
    * @return the moduleId
    */
   public double getModuleId();

   /**
    * @param moduleId the moduleId to set
    */
   public void setModuleId(double moduleId);

   /**
    * @return the singleton
    */
   public boolean isSingleton();

   /**
    * @param singleton the singleton to set
    */
   public void setSingleton(boolean singleton);

   /**
    * @return the width
    */
   public double getWidth();

   /**
    * @param width the width to set
    */
   public void setWidth(double width);

   /**
    * @return the links
    */
   public Links getLinks();

   /**
    * @param links the links to set
    */
   public void setLinks(Links links);

   /**
    * @return the authorLink
    */
   public String getAuthorLink();

   /**
    * @param authorLink the authorLink to set
    */
   public void setAuthorLink(String authorLink);

   /**
    * @return the url
    */
   public String getUrl();

   /**
    * @param url the url to set
    */
   public void setUrl(String url);

   /**
    * @return the iframeUrl
    */
   public String getIframeUrl();

   /**
    * @param iframeUrl the iframeUrl to set
    */
   public void setIframeUrl(String iframeUrl);

   /**
    * @return the scaling
    */
   public boolean isScaling();

   /**
    * @param scaling the scaling to set
    */
   public void setScaling(boolean scaling);

   /**
    * @return the thumbnail
    */
   public String getThumbnail();

   /**
    * @param thumbnail the thumbnail to set
    */
   public void setThumbnail(String thumbnail);

   /**
    * @return the scrolling
    */
   public boolean isScrolling();

   /**
    * @param scrolling the scrolling to set
    */
   public void setScrolling(boolean scrolling);

   /**
    * @return the views
    */
   public Views getViews();

   /**
    * @param views the views to set
    */
   public void setViews(Views views);

   /**
    * @return the categories
    */
   public List<String> getCategories();

   /**
    * @param categories the categories to set
    */
   public void setCategories(List<String> categories);

   /**
    * @return the authorPhoto
    */
   public String getAuthorPhoto();

   /**
    * @param authorPhoto the authorPhoto to set
    */
   public void setAuthorPhoto(String authorPhoto);

   /**
    * @return the showInDirectory
    */
   public boolean isShowInDirectory();

   /**
    * @param showInDirectory the showInDirectory to set
    */
   public void setShowInDirectory(boolean showInDirectory);

}