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

import com.codenvy.commons.security.oauth.OAuthAuthenticator;
import com.codenvy.commons.security.oauth.OAuthAuthenticatorProvider;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Grub all implementations of OAuthAuthenticator from eXo container. */
public class ExoOAuthAuthenticatorProvider implements OAuthAuthenticatorProvider {
    private final Map<String, OAuthAuthenticator> authenticators;

    public ExoOAuthAuthenticatorProvider(ExoContainerContext containerContext) {
        authenticators = new HashMap<String, OAuthAuthenticator>();
        ExoContainer container = containerContext.getContainer();
        List allAuth = container.getComponentInstancesOfType(OAuthAuthenticator.class);
        if (!(allAuth == null || allAuth.isEmpty())) {
            for (Object o : allAuth) {
                OAuthAuthenticator auth = (OAuthAuthenticator)o;
                authenticators.put(auth.getOAuthProvider(), auth);
            }
        }
    }

    @Override
    public OAuthAuthenticator getAuthenticator(String oauthProviderName) {
        return authenticators.get(oauthProviderName);
    }
}
