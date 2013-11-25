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
package com.codenvy.ide.ext.github.shared;

import com.codenvy.dto.shared.DTO;

/**
 * GitHub repository, taken from API v3.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Mar 29, 2012 12:41:47 PM anya $
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