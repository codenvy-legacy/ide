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

import org.exoplatform.ide.security.shared.Token;

import java.io.IOException;

/** Retrieves oAuth token with help of OAuthAuthenticatorProvider. */
public class OAuthAuthenticatorTokenProvider implements OAuthTokenProvider {
    private final OAuthAuthenticatorProvider oAuthAuthenticatorProvider;

    public OAuthAuthenticatorTokenProvider(OAuthAuthenticatorProvider oAuthAuthenticatorProvider) {
        this.oAuthAuthenticatorProvider = oAuthAuthenticatorProvider;
    }

    @Override
    public Token getToken(String oauthProviderName, String userId) throws IOException {
        OAuthAuthenticator oAuthAuthenticator = oAuthAuthenticatorProvider.getAuthenticator(oauthProviderName);
        if (oAuthAuthenticator != null && oAuthAuthenticator.getToken(userId) != null) {
            return oAuthAuthenticator.getToken(userId);
        }
        return null;
    }
}
