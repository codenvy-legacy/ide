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
package com.codenvy.ide.ext.git.server.nativegit;

import com.codenvy.api.auth.oauth.OAuthTokenProvider;
import com.codenvy.api.auth.shared.dto.OAuthToken;
import com.codenvy.commons.env.EnvironmentContext;
import com.codenvy.ide.ext.git.server.GitException;

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
public class WSO2OAuthCredentialsProvider implements CredentialsProvider {
    public static final String WSO_2_URL_STRING =
            "(http|https)://((([0-9a-fA-F]{32}(:x-oauth-basic)?)|([0-9a-zA-Z-_.]+))@)?git\\.cloudpreview\\.wso2\\.com" +
            "(:[0-9]{1,5})?/.+\\.git";

    public static final Pattern WSO_2_URL_PATTERN = Pattern.compile(WSO_2_URL_STRING);

    private static final Logger LOG = LoggerFactory.getLogger(WSO2OAuthCredentialsProvider.class);
    private final OAuthTokenProvider tokenProvider;

    @Inject
    public WSO2OAuthCredentialsProvider(OAuthTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public boolean get(String url, CredentialItem... items) throws GitException {
        if (!WSO_2_URL_PATTERN.matcher(url).matches()) {
            return false;
        }
        OAuthToken token;
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
