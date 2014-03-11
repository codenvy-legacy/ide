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
package com.codenvy.ide.ext.git.server.nativegit;

import com.codenvy.commons.env.EnvironmentContext;
import com.codenvy.ide.ext.git.server.GitException;
import com.codenvy.ide.security.oauth.server.OAuthTokenProvider;
import com.codenvy.ide.security.oauth.shared.Token;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Used to store credentials when given url is WSO2.
 *
 * @author Eugene Voevodin
 */
@Singleton
public class OAuthCredentialsProvider implements CredentialsProvider {
    public static final String WSO_2_URL_STRING =
            "(http|https)://((([0-9a-fA-F]{32}(:x-oauth-basic)?)|([0-9a-zA-Z-_.]+))@)?git\\.cloudpreview\\.wso2\\.com" +
            "(:[0-9]{1,5})?/.+\\.git";

    public static final Pattern WSO_2_URL_PATTERN = Pattern.compile(WSO_2_URL_STRING);

    private static final Logger LOG = LoggerFactory.getLogger(OAuthCredentialsProvider.class);
    private final OAuthTokenProvider tokenProvider;

    @Inject
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
            token = tokenProvider.getToken("wso2", EnvironmentContext.getCurrent().getUser().getName());
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
                }
            }
        } else {
            LOG.error("Token is null");
            return false;
        }
        return true;
    }
}
