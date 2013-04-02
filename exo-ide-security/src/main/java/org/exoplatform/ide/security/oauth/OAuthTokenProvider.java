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

import java.io.IOException;

/** Retrieves user token from OAuth providers. */
public interface OAuthTokenProvider {
    /**
     * Get oauth token.
     *
     * @param oauthProviderName
     *         - name of provider.
     * @param userId
     *         user
     * @return oauth token or <code>null</code>
     * @throws java.io.IOException
     *         if i/o error occurs when try to refresh expired oauth token
     */
    String getToken(String oauthProviderName, String userId) throws IOException;
}
