/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2013] Codenvy, S.A.
 *  All Rights Reserved.
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
package com.codenvy.ide.ext.github.server.oauth;


import com.codenvy.ide.security.oauth.shared.User;

/** Represents GitHub user. */
public class GitHubUser implements User {
    private String name;
    private String company;
    private String email;

    @Override
    public final String getId() {
        return email;
    }

    @Override
    public final void setId(String id) {
        // JSON response from Github API contains key 'id' but it has different purpose.
        // Ignore calls of this method. Email address is used as user identifier.
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "GitHubUser{" +
               "id='" + getId() + '\'' +
               ", name='" + name + '\'' +
               ", company='" + company + '\'' +
               ", email='" + email + '\'' +
               '}';
    }
}
