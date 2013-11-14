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
import com.codenvy.factory.FactoryServlet;

import org.exoplatform.ide.git.server.GitException;
import org.exoplatform.services.security.ConversationState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Used to store credentials when given url is WSO2.
 *
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class OAuthCredentialsProvider implements CredentialsProvider {

    private static final Pattern WSO_2_URL_PATTERN = Pattern.compile(FactoryServlet.WSO_2_URL_STRING);
    private static final Logger LOG = LoggerFactory.getLogger(OAuthCredentialsProvider.class);
    private final OAuthTokenProvider tokenProvider;

    public OAuthCredentialsProvider(OAuthTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public boolean get(String url, CredentialItem... items) throws GitException {
        if (!WSO_2_URL_PATTERN.matcher(url).matches()) {
            return false;
        }
        Token token;
        try {
            token = tokenProvider.getToken("wso2", ConversationState.getCurrent().getIdentity().getUserId());
        } catch (IOException e) {
            LOG.error("Can't get token", e);
            return false;
        }
        if (token != null) {
            for (CredentialItem item : items) {
                if (item instanceof CredentialItem.Password) {
                    ((CredentialItem.Password)item).setValue("x-oauth-basic");
                    continue;
                }
                if (item instanceof CredentialItem.Username) {
                    ((CredentialItem.Username)item).setValue(token.getToken());
                    continue;
                }
            }
        } else {
            LOG.error("Token is null");
            return false;
        }
        return true;
    }
}
