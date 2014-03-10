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
package com.codenvy.ide.security.oauth.server;


import com.codenvy.ide.security.oauth.shared.Token;

import java.io.IOException;

/** Retrieves user token from OAuth providers. */
public interface OAuthTokenProvider {
    /**
     * Get oauth token.
     *
     * @param oauthProviderName
     *         - name of provider.
     * @param userId
     *         - user
     * @return oauth token or <code>null</code>
     * @throws java.io.IOException
     *         if i/o error occurs when try to refresh expired oauth token
     */
    Token getToken(String oauthProviderName, String userId) throws IOException;

//    /**
//     * Get oauth token.
//     *
//     * @param oauthProviderName
//     *         - name of provider.
//     * @param userId
//     *         - user
//     * @param urlInfo
//     *         - information needed to sign request with authorization header
//     * @return oauth token or <code>null</code>
//     * @throws java.io.IOException
//     *         if i/o error occurs when try to refresh expired oauth token
//     */
//    Token getToken(String oauthProviderName, String userId, OAuth1UrlInfo urlInfo) throws IOException;
}
