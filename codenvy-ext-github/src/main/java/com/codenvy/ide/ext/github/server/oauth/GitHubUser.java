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
