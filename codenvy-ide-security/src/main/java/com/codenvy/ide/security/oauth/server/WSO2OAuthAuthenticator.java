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

package com.codenvy.ide.security.oauth.server;

import com.codenvy.ide.security.oauth.shared.Token;
import com.codenvy.ide.security.oauth.shared.User;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.CredentialStore;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;

/** OAuth authentication for wso2 account. */
public class WSO2OAuthAuthenticator extends OAuthAuthenticator {
    private static final Logger LOG = LoggerFactory.getLogger(WSO2OAuthAuthenticator.class);

    public WSO2OAuthAuthenticator(CredentialStore credentialStore, GoogleClientSecrets clientSecrets) {
        super(new AuthorizationCodeFlow.Builder(BearerToken.authorizationHeaderAccessMethod(), new NetHttpTransport(), new JacksonFactory(),
                                                new GenericUrl(clientSecrets.getDetails().getTokenUri()),
                                                new ClientParametersAuthentication(clientSecrets.getDetails().getClientId(),
                                                                                   clientSecrets.getDetails().getClientSecret()),
                                                clientSecrets.getDetails().getClientId(), clientSecrets.getDetails().getAuthUri())
                      .setScopes(Collections.<String>emptyList()).setCredentialStore(credentialStore).build(),
              new HashSet<String>(clientSecrets.getDetails().getRedirectUris()));

    }

    /** {@inheritDoc} */
    @Override
    public User getUser(Token accessToken) throws OAuthAuthenticationException {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String getOAuthProvider() {
        return "wso2";
    }
}
