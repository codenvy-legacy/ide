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
