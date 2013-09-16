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
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: GitHubUser.java Aug 6, 2012
 */
public interface GitHubUser {

    /** @return the type */
    public abstract String getType();

    /**
     * @param type
     *         the type to set
     */
    public abstract void setType(String type);

    /** @return the email */
    public abstract String getEmail();

    /**
     * @param email
     *         the email to set
     */
    public abstract void setEmail(String email);

    /** @return the company */
    public abstract String getCompany();

    /**
     * @param company
     *         the company to set
     */
    public abstract void setCompany(String company);

    /** @return the followers */
    public abstract int getFollowers();

    /**
     * @param followers
     *         the followers to set
     */
    public abstract void setFollowers(int followers);

    /** @return the avatar_url */
    public abstract String getAvatarUrl();

    /**
     * @param avatarUrl
     *         the avatar_url to set
     */
    public abstract void setAvatarUrl(String avatarUrl);

    /** @return the html_url */
    public abstract String getHtmlUrl();

    /**
     * @param htmlUrl
     *         the html_url to set
     */
    public abstract void setHtmlUrl(String htmlUrl);

    /** @return the bio */
    public abstract String getBio();

    /**
     * @param bio
     *         the bio to set
     */
    public abstract void setBio(String bio);

    /** @return the public_repos */
    public abstract int getPublicRepos();

    /**
     * @param publicRepos
     *         the public_repos to set
     */
    public abstract void setPublicRepos(int publicRepos);

    /** @return the public_gists */
    public abstract int getPublicGists();

    /**
     * @param public_gists
     *         the public_gists to set
     */
    public abstract void setPublicGists(int publicGists);

    /** @return the following */
    public abstract int getFollowing();

    /**
     * @param following
     *         the following to set
     */
    public abstract void setFollowing(int following);

    /** @return the location */
    public abstract String getLocation();

    /**
     * @param location
     *         the location to set
     */
    public abstract void setLocation(String location);

    /** @return the name */
    public abstract String getName();

    /**
     * @param name
     *         the name to set
     */
    public abstract void setName(String name);

    /** @return the url */
    public abstract String getUrl();

    /**
     * @param url
     *         the url to set
     */
    public abstract void setUrl(String url);

    /** @return the gravatar_id */
    public abstract String getGravatarId();

    /**
     * @param gravatarId
     *         the gravatar_id to set
     */
    public abstract void setGravatarId(String gravatarId);

    /** @return the id */
    public abstract String getId();

    /**
     * @param id
     *         the id to set
     */
    public abstract void setId(String id);

    /** @return the login */
    public abstract String getLogin();

    /**
     * @param login
     *         the login to set
     */
    public abstract void setLogin(String login);

}
