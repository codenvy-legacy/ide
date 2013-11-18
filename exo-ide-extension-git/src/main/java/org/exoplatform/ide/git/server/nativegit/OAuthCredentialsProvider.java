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
package org.exoplatform.ide.git.server.nativegit;

import com.codenvy.commons.security.oauth.OAuthTokenProvider;
import com.codenvy.commons.security.shared.Token;

import org.exoplatform.ide.git.server.GitException;
import org.exoplatform.ide.git.server.provider.GitVendorService;
import org.exoplatform.ide.git.server.provider.GitVendorServiceProvider;
import org.exoplatform.services.security.ConversationState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Used to store credentials when we found Git service which support OAuth 2.0.
 */
public class OAuthCredentialsProvider implements CredentialsProvider {

    private static final Logger LOG = LoggerFactory.getLogger(OAuthCredentialsProvider.class);
    private final OAuthTokenProvider       tokenProvider;
    private       GitVendorServiceProvider gitVendorServiceProvider;

    public OAuthCredentialsProvider(OAuthTokenProvider tokenProvider, GitVendorServiceProvider gitVendorServiceProvider) {
        this.tokenProvider = tokenProvider;
        this.gitVendorServiceProvider = gitVendorServiceProvider;
    }

    /** {@inheritDoc} */
    @Override
    public boolean get(String url, CredentialItem... items) throws GitException {

        GitVendorService gitVendorService = gitVendorServiceProvider.getGitServiceByUrlMatch(url);

        if (gitVendorService.isOAuth2()) {
            try {
                Token token =
                        tokenProvider.getToken(gitVendorService.getVendorName(), ConversationState.getCurrent().getIdentity().getUserId());
                if (token != null) {
                    for (CredentialItem item : items) {
                        if (item instanceof CredentialItem.Password) {
                            ((CredentialItem.Password)item).setValue("x-oauth-basic");
                            continue;
                        }
                        if (item instanceof CredentialItem.Username) {
                            ((CredentialItem.Username)item).setValue(token.getToken());
                        }
                    }
                    return true;
                }
            } catch (IOException e) {
                LOG.error("Can't get token", e);
                return false;
            }
        }

        return false;
    }
}
