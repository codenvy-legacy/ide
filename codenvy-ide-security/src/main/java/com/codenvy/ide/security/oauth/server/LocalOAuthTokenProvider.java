/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 * [2012] - [$today.year] Codenvy, S.A. 
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

import com.codenvy.api.auth.oauth.OAuthTokenProvider;
import com.codenvy.api.auth.shared.dto.OAuthToken;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Vitaly Parfonov
 */
@Singleton
public class LocalOAuthTokenProvider implements OAuthTokenProvider {

    private final Map<String, OAuthAuthenticator> authenticators = new ConcurrentHashMap<>();

    @Inject
    public LocalOAuthTokenProvider(Set<OAuthAuthenticatorProvider> providers) {
        for(OAuthAuthenticatorProvider authenticatorProvider : providers) {
            this.authenticators.put(authenticatorProvider.getId(), authenticatorProvider.getAuthenticator());
        }
    }

    @Override
    public OAuthToken getToken(String oauthProviderName, String userId) throws IOException {
        OAuthAuthenticator authenticator = authenticators.get(oauthProviderName);
        return authenticator != null ? authenticator.getToken(userId) : null;
    }
}
