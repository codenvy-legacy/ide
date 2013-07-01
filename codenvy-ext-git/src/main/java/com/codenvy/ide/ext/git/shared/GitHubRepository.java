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
package com.codenvy.ide.ext.git.shared;

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
     * Get repository's location.
     *
     * @return {@link String} url
     */
    String getUrl();

    /**
     * Get repository's home page.
     *
     * @return {@link String} home page
     */
    String getHomepage();

    /**
     * Get the number of repository's forks.
     *
     * @return forks
     */
    int getForks();

    /**
     * Get repository's language.
     *
     * @return {@link String} language
     */
    String getLanguage();

    /**
     * Get fork state.
     *
     * @return {@link Boolean} <code>true</code> id forked
     */
    boolean isFork();

    /**
     * Get the number of repository's watchers.
     *
     * @return {@link Integer} the number of watchers
     */
    int getWatchers();

    /**
     * Get private state of the repository.
     *
     * @return {@link Boolean} private state of the repository
     */
    boolean isPrivate();

    /**
     * Get repository's size.
     *
     * @return {@link Integer} size
     */
    int getSize();

    /**
     * Get repository's description.
     *
     * @return {@link String} description
     */
    String getDescription();

    /**
     * Get SSH URL.
     *
     * @return {@link String} SSH URL
     */
    String getSshUrl();

    /**
     * Get HTML URL.
     *
     * @return {@link String} HTML URL
     */
    String getHtmlUrl();

    /**
     * Get updated date.
     *
     * @return {@link String}
     */
    String getUpdatedAt();

    /**
     * Get Git URL.
     *
     * @return {@link String} Git URL
     */
    String getGitUrl();

    /**
     * Get whether repository has wiki.
     *
     * @return {@link Boolean} <code> true</code> - has wiki
     */
    boolean hasWiki();

    /**
     * Get clone URL.
     *
     * @return {@link String} clone URL
     */
    String getCloneUrl();

    /**
     * Get SVN URL.
     *
     * @return {@link String} SVN URL
     */
    String getSvnUrl();

    /**
     * Get the number of opened issues.
     *
     * @return {@link Integer} number of opened issues
     */
    int getOpenedIssues();

    /**
     * Get repository's created date.
     *
     * @return {@link String} created date
     */
    String getCreatedAt();

    /**
     * Get repository's pushed date.
     *
     * @return {@link String} pushed date
     */
    String getPushedAt();

    /**
     * Get whether repository has downloads.
     *
     * @return {@link Boolean} <code> true</code> - has downloads
     */
    boolean hasDownloads();

    /**
     * Get whether repository has issues.
     *
     * @return {@link Boolean} <code> true</code> - has issues
     */
    boolean hasIssues();
}