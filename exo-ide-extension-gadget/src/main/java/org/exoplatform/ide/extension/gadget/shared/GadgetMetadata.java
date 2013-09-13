/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.extension.gadget.shared;

import java.util.List;

/**
 * Metadata of OpenSocial gadget.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: GadgetMetadata.java Mar 20, 2012 16:12:11 PM azatsarynnyy $
 */
public interface GadgetMetadata {

    /** @return the userPrefs */
    public UserPrefs getUserPrefs();

    /**
     * @param userPrefs
     *         the userPrefs to set
     */
    public void setUserPrefs(UserPrefs userPrefs);

    /** @return the title */
    public String getTitle();

    /**
     * @param title
     *         the title to set
     */
    public void setTitle(String title);

    /** @return the titleUrl */
    public String getTitleUrl();

    /**
     * @param titleUrl
     *         the titleUrl to set
     */
    public void setTitleUrl(String titleUrl);

    /** @return the height */
    public double getHeight();

    /**
     * @param height
     *         the height to set
     */
    public void setHeight(double height);

    /** @return the features */
    public List<String> getFeatures();

    /**
     * @param features
     *         the features to set
     */
    public void setFeatures(List<String> features);

    /** @return the showStats */
    public boolean isShowStats();

    /**
     * @param showStats
     *         the showStats to set
     */
    public void setShowStats(boolean showStats);

    /** @return the screenshot */
    public String getScreenshot();

    /**
     * @param screenshot
     *         the screenshot to set
     */
    public void setScreenshot(String screenshot);

    /** @return the moduleId */
    public double getModuleId();

    /**
     * @param moduleId
     *         the moduleId to set
     */
    public void setModuleId(double moduleId);

    /** @return the singleton */
    public boolean isSingleton();

    /**
     * @param singleton
     *         the singleton to set
     */
    public void setSingleton(boolean singleton);

    /** @return the width */
    public double getWidth();

    /**
     * @param width
     *         the width to set
     */
    public void setWidth(double width);

    /** @return the links */
    public Links getLinks();

    /**
     * @param links
     *         the links to set
     */
    public void setLinks(Links links);

    /** @return the authorLink */
    public String getAuthorLink();

    /**
     * @param authorLink
     *         the authorLink to set
     */
    public void setAuthorLink(String authorLink);

    /** @return the url */
    public String getUrl();

    /**
     * @param url
     *         the url to set
     */
    public void setUrl(String url);

    /** @return the iframeUrl */
    public String getIframeUrl();

    /**
     * @param iframeUrl
     *         the iframeUrl to set
     */
    public void setIframeUrl(String iframeUrl);

    /** @return the scaling */
    public boolean isScaling();

    /**
     * @param scaling
     *         the scaling to set
     */
    public void setScaling(boolean scaling);

    /** @return the thumbnail */
    public String getThumbnail();

    /**
     * @param thumbnail
     *         the thumbnail to set
     */
    public void setThumbnail(String thumbnail);

    /** @return the scrolling */
    public boolean isScrolling();

    /**
     * @param scrolling
     *         the scrolling to set
     */
    public void setScrolling(boolean scrolling);

    /** @return the views */
    public Views getViews();

    /**
     * @param views
     *         the views to set
     */
    public void setViews(Views views);

    /** @return the categories */
    public List<String> getCategories();

    /**
     * @param categories
     *         the categories to set
     */
    public void setCategories(List<String> categories);

    /** @return the authorPhoto */
    public String getAuthorPhoto();

    /**
     * @param authorPhoto
     *         the authorPhoto to set
     */
    public void setAuthorPhoto(String authorPhoto);

    /** @return the showInDirectory */
    public boolean isShowInDirectory();

    /**
     * @param showInDirectory
     *         the showInDirectory to set
     */
    public void setShowInDirectory(boolean showInDirectory);

}