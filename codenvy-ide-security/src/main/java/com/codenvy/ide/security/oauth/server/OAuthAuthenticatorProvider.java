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

/** Allow store and provide services which implementations of OAuthAuthenticator. */
public interface OAuthAuthenticatorProvider {

    /**
     * Get authentication service by name.
     *
     * @return OAuthAuthenticator instance or <code>null</code> if specified OAuth provider is not supported
     */
    OAuthAuthenticator getAuthenticator();

    String getId();
}
