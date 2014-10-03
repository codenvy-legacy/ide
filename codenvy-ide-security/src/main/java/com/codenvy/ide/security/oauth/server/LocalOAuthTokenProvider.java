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
        for (OAuthAuthenticatorProvider authenticatorProvider : providers) {
            this.authenticators.put(authenticatorProvider.getId(), authenticatorProvider.getAuthenticator());
        }
    }

    @Override
    public OAuthToken getToken(String oauthProviderName, String userId) throws IOException {
        OAuthAuthenticator authenticator = authenticators.get(oauthProviderName);
        return authenticator != null ? authenticator.getToken(userId) : null;
    }
}
