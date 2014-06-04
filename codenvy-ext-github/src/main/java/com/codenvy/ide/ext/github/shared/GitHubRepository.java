/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.github.shared;

import com.codenvy.dto.shared.DTO;

/**
 * GitHub repository, taken from API v3.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 */
@DTO
public interface GitHubRepository {
    /**
     * Get repository's name.
     *
     * @return {@link String} name
     */
    String getName();
    
    void setName(String name);

    /**
     * Get repository's location.
     *
     * @return {@link String} url
     */
    String getUrl();
    
    void setUrl(String url);

    /**
     * Get repository's home page.
     *
     * @return {@link String} home page
     */
    String getHomepage();
    
    void setHomepage(String homePage);

    /**
     * Get the number of repository's forks.
     *
     * @return forks
     */
    int getForks();
    
    void setForks(int forks);

    /**
     * Get repository's language.
     *
     * @return {@link String} language
     */
    String getLanguage();
    
    void setLanguage(String language);

    /**
     * Get fork state.
     *
     * @return {@link Boolean} <code>true</code> id forked
     */
    boolean isFork();
    
    void setFork(boolean isFork);

    /**
     * Get the number of repository's watchers.
     *
     * @return {@link Integer} the number of watchers
     */
    int getWatchers();
    
    void setWatchers(int watchers);

    /**
     * Get private state of the repository.
     *
     * @return {@link Boolean} private state of the repository
     */
    boolean isPrivateRepo();
    
    void setPrivateRepo(boolean isPrivateRepo);

    /**
     * Get repository's size.
     *
     * @return {@link Integer} size
     */
    int getSize();
    
    void setSize(int size);
    
    /**
     * Get repository's description.
     *
     * @return {@link String} description
     */
    String getDescription();
    
    void setDescription(String description);

    /**
     * Get SSH URL.
     *
     * @return {@link String} SSH URL
     */
    String getSshUrl();
    
    void setSshUrl(String sshUrl);

    /**
     * Get HTML URL.
     *
     * @return {@link String} HTML URL
     */
    String getHtmlUrl();
    
    void setHtmlUrl(String htmlUrl);

    /**
     * Get updated date.
     *
     * @return {@link String}
     */
    String getUpdatedAt();
    
    void setUpdatedAt(String updatedAt);

    /**
     * Get Git URL.
     *
     * @return {@link String} Git URL
     */
    String getGitUrl();
    
    void setGitUrl(String gitUrl);

    /**
     * Get whether repository has wiki.
     *
     * @return {@link Boolean} <code> true</code> - has wiki
     */
    boolean isHasWiki();
    
    void setHasWiki(boolean isHasWiki);

    /**
     * Get clone URL.
     *
     * @return {@link String} clone URL
     */
    String getCloneUrl();

    void setCloneUrl(String cloneUrl);
    
    /**
     * Get SVN URL.
     *
     * @return {@link String} SVN URL
     */
    String getSvnUrl();
    
    void setSvnUrl(String svnUrl);

    /**
     * Get the number of opened issues.
     *
     * @return {@link Integer} number of opened issues
     */
    int getOpenedIssues();
    
    void setOpenedIssues(int openedIssues);

    /**
     * Get repository's created date.
     *
     * @return {@link String} created date
     */
    String getCreatedAt();
    
    void setCreatedAt(String createdAt);

    /**
     * Get repository's pushed date.
     *
     * @return {@link String} pushed date
     */
    String getPushedAt();
    
    void setPushedAt(String pushedAt);

    /**
     * Get whether repository has downloads.
     *
     * @return {@link Boolean} <code> true</code> - has downloads
     */
    boolean isHasDownloads();
    
    void setHasDownloads(boolean isHasDownloads);

    /**
     * Get whether repository has issues.
     *
     * @return {@link Boolean} <code> true</code> - has issues
     */
    boolean isHasIssues();
    
    void setHasIssues(boolean isHasIssues);
}