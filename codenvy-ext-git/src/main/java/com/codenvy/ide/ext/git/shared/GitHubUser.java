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

    /** @return the email */
    String getEmail();

    /** @return the company */
    String getCompany();

    /** @return the followers */
    int getFollowers();

    /** @return the avatar_url */
    String getAvatarUrl();

    /** @return the html_url */
    String getHtmlUrl();

    /** @return the bio */
    String getBio();

    /** @return the public_repos */
    int getPublicRepos();

    /** @return the public_gists */
    int getPublicGists();

    /** @return the following */
    int getFollowing();

    /** @return the location */
    String getLocation();

    /** @return the name */
    String getName();

    /** @return the url */
    String getUrl();

    /** @return the gravatar_id */
    String getGravatarId();

    /** @return the id */
    String getId();

    /** @return the login */
    String getLogin();
}