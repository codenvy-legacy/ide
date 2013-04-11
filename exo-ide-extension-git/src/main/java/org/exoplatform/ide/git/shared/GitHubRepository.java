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
package org.exoplatform.ide.git.shared;

/**
 * GitHub repository, taken from API v3.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Mar 29, 2012 12:41:47 PM anya $
 */
public interface GitHubRepository {
    /**
     * Get repository's name.
     * 
     * @return {@link String} name
     */
    String getName();

    /**
     * Set repository's name.
     * 
     * @param name repository's name
     */
    void setName(String name);

    /**
     * Get repository's location.
     * 
     * @return {@link String} url
     */
    String getUrl();

    /**
     * Set repository's URL.
     * 
     * @param url repository's URL
     */
    void setUrl(String url);

    /**
     * Get repository's home page.
     * 
     * @return {@link String} home page
     */
    String getHomepage();

    /**
     * Set repository's home page.
     * 
     * @param homepage home page
     */
    void setHomepage(String homepage);

    /**
     * Get the number of repository's forks.
     * 
     * @return forks
     */
    int getForks();

    /**
     * Set the number of repository's forks.
     * 
     * @param forks number of forks
     */
    void setForks(int forks);

    /**
     * Get repository's language.
     * 
     * @return {@link String} language
     */
    String getLanguage();

    /**
     * Set repository's language.
     * 
     * @param language language
     */
    void setLanguage(String language);

    /**
     * Get fork state.
     * 
     * @return {@link Boolean} <code>true</code> id forked
     */
    boolean isFork();

    /**
     * Set fork state.
     * 
     * @param fork fork
     */
    void setFork(boolean fork);

    /**
     * Get the number of repository's watchers.
     * 
     * @return {@link Integer} the number of watchers
     */
    int getWatchers();

    /**
     * Set the number of repository's watchers.
     * 
     * @param watchers wathers
     */
    void setWatchers(int watchers);

    /**
     * Get private state of the repository.
     * 
     * @return {@link Boolean} private state of the repository
     */
    boolean isPrivate();

    /**
     * Set private state of the repository.
     * 
     * @param isPrivate private
     */
    void setPrivate(boolean isPrivate);

    /**
     * Get repository's size.
     * 
     * @return {@link Integer} size
     */
    int getSize();

    /**
     * Set repository's size.
     * 
     * @param size size
     */
    void setSize(int size);

    /**
     * Get repository's description.
     * 
     * @return {@link String} description
     */
    String getDescription();

    /**
     * Set repository's description.
     * 
     * @param description
     */
    void setDescription(String description);

    /**
     * Get SSH URL.
     * 
     * @return {@link String} SSH URL
     */
    String getSshUrl();

    /**
     * Set SSH URL.
     * 
     * @param sshUrl
     */
    void setSshUrl(String sshUrl);

    /**
     * Get HTML URL.
     * 
     * @return {@link String} HTML URL
     */
    String getHtmlUrl();

    /**
     * Set HTML URL.
     * 
     * @param htmlUrl
     */
    void setHtmlUrl(String htmlUrl);

    /**
     * Get updated date.
     * 
     * @return {@link String}
     */
    String getUpdatedAt();

    /**
     * Set updated date.
     * 
     * @param updatedAt
     */
    void setUpdatedAt(String updatedAt);

    /**
     * Get Git URL.
     * 
     * @return {@link String} Git URL
     */
    String getGitUrl();

    /**
     * Set Git URL.
     * 
     * @param gitUrl
     */
    void setGitUrl(String gitUrl);

    /**
     * Get whether repository has wiki.
     * 
     * @return {@link Boolean} <code> true</code> - has wiki
     */
    boolean isHasWiki();

    /**
     * Set whether repository has wiki.
     * 
     * @param hasWiki
     */
    void setHasWiki(boolean hasWiki);

    /**
     * Get clone URL.
     * 
     * @return {@link String} clone URL
     */
    String getCloneUrl();

    /**
     * Set clone URL.
     * 
     * @param cloneUrl
     */
    void setCloneUrl(String cloneUrl);

    /**
     * Get SVN URL.
     * 
     * @return {@link String} SVN URL
     */
    String getSvnUrl();

    /**
     * Set SVN URL.
     * 
     * @param svnUrl
     */
    void setSvnUrl(String svnUrl);

    /**
     * Get the number of opened issues.
     * 
     * @return {@link Integer} number of opened issues
     */
    int getOpenedIssues();

    /**
     * Set the number of opened issues.
     * 
     * @param openedIssues
     */
    void setOpenedIssues(int openedIssues);

    /**
     * Get repository's created date.
     * 
     * @return {@link String} created date
     */
    String getCreatedAt();

    /**
     * Set repository's created date.
     * 
     * @param createdAt
     */
    void setCreatedAt(String createdAt);

    /**
     * Get repository's pushed date.
     * 
     * @return {@link String} pushed date
     */
    String getPushedAt();

    /**
     * Set repository's pushed date.
     * 
     * @param pushedAt
     */
    void setPushedAt(String pushedAt);

    /**
     * Get whether repository has downloads.
     * 
     * @return {@link Boolean} <code> true</code> - has downloads
     */
    boolean isHasDownloads();

    /**
     * Set whether repository has downloads.
     * 
     * @param hasDownloads
     */
    void setHasDownloads(boolean hasDownloads);

    /**
     * Get whether repository has issues.
     * 
     * @return {@link Boolean} <code> true</code> - has issues
     */
    boolean isHasIssues();

    /**
     * Set whether repository has issues.
     * 
     * @param hasIssues
     */
    void setHasIssues(boolean hasIssues);
}
