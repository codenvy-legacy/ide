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