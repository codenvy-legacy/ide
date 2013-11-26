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
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: GitHubUser.java Aug 6, 2012
 */
@DTO
public interface GitHubUser {
    /** @return the type */
    String getType();
    
    void setType(String type);

    /** @return the email */
    String getEmail();
    
    void setEmail(String email);

    /** @return the company */
    String getCompany();
    
    void setCompany(String company);

    /** @return the followers */
    int getFollowers();
    
    void setFollowers(int followers);

    /** @return the avatar_url */
    String getAvatarUrl();
    
    void setAvatarUrl(String avatarUrl);

    /** @return the html_url */
    String getHtmlUrl();
    
    void setHtmlUrl(String htmlUrl);    

    /** @return the bio */
    String getBio();
    
    void setBio(String bio);

    /** @return the public_repos */
    int getPublicRepos();
    
    void setPublicRepos(int publicRepos);

    /** @return the public_gists */
    int getPublicGists();
    
    void setPublicGists(int publicGists);

    /** @return the following */
    int getFollowing();
    
    void setFollowing(int following);

    /** @return the location */
    String getLocation();
    
    void setLocation(String location);

    /** @return the name */
    String getName();
    
    void setName(String name);

    /** @return the url */
    String getUrl();
    
    void setUrl(String url);

    /** @return the gravatar_id */
    String getGravatarId();
    
    void setGravatarId(String gravatarId);

    /** @return the id */
    String getId();
    
    void setId(String id);

    /** @return the login */
    String getLogin();
    
    void setLogin(String login);
}