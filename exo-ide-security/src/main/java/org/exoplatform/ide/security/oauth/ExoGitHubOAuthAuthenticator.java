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
package org.exoplatform.ide.security.oauth;

import com.codenvy.commons.security.oauth.GitHubOAuthAuthenticator;
import com.google.api.client.auth.oauth2.CredentialStore;
import com.google.api.client.auth.oauth2.MemoryCredentialStore;

import org.exoplatform.container.xml.InitParams;

import static org.exoplatform.ide.security.oauth.ExoGoogleOAuthAuthenticator.createClientSecrets;

/** GitHubOAuthAuthenticator configured over eXo container. */
public class ExoGitHubOAuthAuthenticator extends GitHubOAuthAuthenticator {
    public ExoGitHubOAuthAuthenticator(InitParams initParams) {
        super(new MemoryCredentialStore(), createClientSecrets(initParams));
    }

    public ExoGitHubOAuthAuthenticator(CredentialStore credentialStore, InitParams initParams) {
        super(credentialStore, createClientSecrets(initParams));
    }
}
