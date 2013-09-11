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
