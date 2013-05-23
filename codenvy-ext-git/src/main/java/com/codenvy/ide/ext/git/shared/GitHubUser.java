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
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: GitHubUser.java Aug 6, 2012
 */
public interface GitHubUser {
    /** @return the type */
    String getType();

    /**
     * @param type
     *         the type to set
     */
    void setType(String type);

    /** @return the email */
    String getEmail();

    /**
     * @param email
     *         the email to set
     */
    void setEmail(String email);

    /** @return the company */
    String getCompany();

    /**
     * @param company
     *         the company to set
     */
    void setCompany(String company);

    /** @return the followers */
    int getFollowers();

    /**
     * @param followers
     *         the followers to set
     */
    void setFollowers(int followers);

    /** @return the avatar_url */
    String getAvatarUrl();

    /**
     * @param avatarUrl
     *         the avatar_url to set
     */
    void setAvatarUrl(String avatarUrl);

    /** @return the html_url */
    String getHtmlUrl();

    /**
     * @param htmlUrl
     *         the html_url to set
     */
    void setHtmlUrl(String htmlUrl);

    /** @return the bio */
    String getBio();

    /**
     * @param bio
     *         the bio to set
     */
    void setBio(String bio);

    /** @return the public_repos */
    int getPublicRepos();

    /**
     * @param publicRepos
     *         the public_repos to set
     */
    void setPublicRepos(int publicRepos);

    /** @return the public_gists */
    int getPublicGists();

    /**
     * @param publicGists
     *         the public_gists to set
     */
    void setPublicGists(int publicGists);

    /** @return the following */
    int getFollowing();

    /**
     * @param following
     *         the following to set
     */
    void setFollowing(int following);

    /** @return the location */
    String getLocation();

    /**
     * @param location
     *         the location to set
     */
    void setLocation(String location);

    /** @return the name */
    String getName();

    /**
     * @param name
     *         the name to set
     */
    void setName(String name);

    /** @return the url */
    String getUrl();

    /**
     * @param url
     *         the url to set
     */
    void setUrl(String url);

    /** @return the gravatar_id */
    String getGravatarId();

    /**
     * @param gravatarId
     *         the gravatar_id to set
     */
    void setGravatarId(String gravatarId);

    /** @return the id */
    String getId();

    /**
     * @param id
     *         the id to set
     */
    void setId(String id);

    /** @return the login */
    String getLogin();

    /**
     * @param login
     *         the login to set
     */
    void setLogin(String login);
}