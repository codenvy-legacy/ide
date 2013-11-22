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
package org.exoplatform.ide.git.server.provider.github;

import org.exoplatform.ide.git.shared.GitHubUser;


/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: GitHubUser.java Aug 6, 2012
 */
public class GitHubUserImpl implements GitHubUser {
    private String type;

    private String email;

    private String company;

    private int followers;

    private String avatarUrl;

    private String htmlUrl;

    private String bio;

    private int publicRepos;

    private int publicGists;

    private int following;

    private String location;

    private String name;

    private String url;

    private String gravatarId;

    private String id;

    private String login;

    /** @see org.exoplatform.ide.extension.samples.shared.GitHubUser#getType() */
    @Override
    public String getType() {
        return type;
    }

    /** @see org.exoplatform.ide.extension.samples.shared.GitHubUser#setType(java.lang.String) */
    @Override
    public void setType(String type) {
        this.type = type;
    }

    /** @see org.exoplatform.ide.extension.samples.shared.GitHubUser#getEmail() */
    @Override
    public String getEmail() {
        return email;
    }

    /** @see org.exoplatform.ide.extension.samples.shared.GitHubUser#setEmail(java.lang.String) */
    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    /** @see org.exoplatform.ide.extension.samples.shared.GitHubUser#getCompany() */
    @Override
    public String getCompany() {
        return company;
    }

    /** @see org.exoplatform.ide.extension.samples.shared.GitHubUser#setCompany(java.lang.String) */
    @Override
    public void setCompany(String company) {
        this.company = company;
    }

    /** @see org.exoplatform.ide.extension.samples.shared.GitHubUser#getFollowers() */
    @Override
    public int getFollowers() {
        return followers;
    }

    /** @see org.exoplatform.ide.extension.samples.shared.GitHubUser#setFollowers(int) */
    @Override
    public void setFollowers(int followers) {
        this.followers = followers;
    }

    /** @see org.exoplatform.ide.extension.samples.shared.GitHubUser#getAvatarUrl() */
    @Override
    public String getAvatarUrl() {
        return avatarUrl;
    }

    /** @see org.exoplatform.ide.extension.samples.shared.GitHubUser#setAvatarUrl(java.lang.String) */
    @Override
    public void setAvatarUrl(String avatar_url) {
        this.avatarUrl = avatar_url;
    }

    /** @see org.exoplatform.ide.extension.samples.shared.GitHubUser#getHtmlUrl() */
    @Override
    public String getHtmlUrl() {
        return htmlUrl;
    }

    /** @see org.exoplatform.ide.extension.samples.shared.GitHubUser#setHtmlUrl(java.lang.String) */
    @Override
    public void setHtmlUrl(String html_url) {
        this.htmlUrl = html_url;
    }

    /** @see org.exoplatform.ide.extension.samples.shared.GitHubUser#getBio() */
    @Override
    public String getBio() {
        return bio;
    }

    /** @see org.exoplatform.ide.extension.samples.shared.GitHubUser#setBio(java.lang.String) */
    @Override
    public void setBio(String bio) {
        this.bio = bio;
    }

    /** @see org.exoplatform.ide.extension.samples.shared.GitHubUser#getPublicRepos() */
    @Override
    public int getPublicRepos() {
        return publicRepos;
    }

    /** @see org.exoplatform.ide.extension.samples.shared.GitHubUser#setPublicRepos(int) */
    @Override
    public void setPublicRepos(int public_repos) {
        this.publicRepos = public_repos;
    }

    /** @see org.exoplatform.ide.extension.samples.shared.GitHubUser#getPublicGists() */
    @Override
    public int getPublicGists() {
        return publicGists;
    }

    /** @see org.exoplatform.ide.extension.samples.shared.GitHubUser#setPublicGists(int) */
    @Override
    public void setPublicGists(int public_gists) {
        this.publicGists = public_gists;
    }

    /** @see org.exoplatform.ide.extension.samples.shared.GitHubUser#getFollowing() */
    @Override
    public int getFollowing() {
        return following;
    }

    /** @see org.exoplatform.ide.extension.samples.shared.GitHubUser#setFollowing(int) */
    @Override
    public void setFollowing(int following) {
        this.following = following;
    }

    /** @see org.exoplatform.ide.extension.samples.shared.GitHubUser#getLocation() */
    @Override
    public String getLocation() {
        return location;
    }

    /** @see org.exoplatform.ide.extension.samples.shared.GitHubUser#setLocation(java.lang.String) */
    @Override
    public void setLocation(String location) {
        this.location = location;
    }

    /** @see org.exoplatform.ide.extension.samples.shared.GitHubUser#getName() */
    @Override
    public String getName() {
        return name;
    }

    /** @see org.exoplatform.ide.extension.samples.shared.GitHubUser#setName(java.lang.String) */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /** @see org.exoplatform.ide.extension.samples.shared.GitHubUser#getUrl() */
    @Override
    public String getUrl() {
        return url;
    }

    /** @see org.exoplatform.ide.extension.samples.shared.GitHubUser#setUrl(java.lang.String) */
    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    /** @see org.exoplatform.ide.extension.samples.shared.GitHubUser#getGravatarId() */
    @Override
    public String getGravatarId() {
        return gravatarId;
    }

    /** @see org.exoplatform.ide.extension.samples.shared.GitHubUser#setGravatarId(java.lang.String) */
    @Override
    public void setGravatarId(String gravatar_id) {
        this.gravatarId = gravatar_id;
    }

    /** @see org.exoplatform.ide.extension.samples.shared.GitHubUser#getId() */
    @Override
    public String getId() {
        return id;
    }

    /** @see org.exoplatform.ide.extension.samples.shared.GitHubUser#setId(java.lang.String) */
    @Override
    public void setId(String id) {
        this.id = id;
    }

    /** @see org.exoplatform.ide.extension.samples.shared.GitHubUser#getLogin() */
    @Override
    public String getLogin() {
        return login;
    }

    /** @see org.exoplatform.ide.extension.samples.shared.GitHubUser#setLogin(java.lang.String) */
    @Override
    public void setLogin(String login) {
        this.login = login;
    }


}
