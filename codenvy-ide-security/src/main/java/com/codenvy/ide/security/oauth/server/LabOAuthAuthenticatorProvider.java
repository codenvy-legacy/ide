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
package com.codenvy.ide.security.oauth.server;

import com.google.api.client.auth.oauth2.MemoryCredentialStore;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.json.jackson.JacksonFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class LabOAuthAuthenticatorProvider implements OAuthAuthenticatorProvider {

    private static final Logger LOG = LoggerFactory.getLogger(LabOAuthAuthenticatorProvider.class);

    private final Map<String, OAuthAuthenticator> authenticators;

    private final String keyFolder;

    @Inject
    public LabOAuthAuthenticatorProvider(@Named("security.local.oauth-keyFolder") String keyFolder) {
        this.keyFolder = keyFolder;
        authenticators = new HashMap<>();

        GitHubOAuthAuthenticator gitHubOAuthAuthenticator =
                new GitHubOAuthAuthenticator(new MemoryCredentialStore(),
                                             getClientSecrets("github_client_secrets.json"));

        authenticators.put(gitHubOAuthAuthenticator.getOAuthProvider(), gitHubOAuthAuthenticator);

        try {
            WSO2OAuthAuthenticator wso2OAuthAuthenticator =
                    new WSO2OAuthAuthenticator(new MemoryCredentialStore(),
                                               getClientSecrets("wso2_client_secrets.json"));
            authenticators.put(wso2OAuthAuthenticator.getOAuthProvider(), wso2OAuthAuthenticator);
        } catch (Exception ignored) {
            // ignore wso2 configuration
        }
    }

    @Override
    public OAuthAuthenticator getAuthenticator(String oauthProviderName) {
        return authenticators.get(oauthProviderName);
    }

    private GoogleClientSecrets getClientSecrets(String fileName) {
        File clientSecrets = new File(keyFolder, fileName);
        if (!clientSecrets.exists() || clientSecrets.isDirectory()) {
            LOG.warn("Client secrets file " + clientSecrets.getAbsolutePath() + " not found or is a directory", fileName);
            throw new RuntimeException("Client secrets file " + clientSecrets.getAbsolutePath() + " not found or is a "
                                       + "directory");
        }
        try (InputStream is = new FileInputStream(clientSecrets)) {
            return GoogleClientSecrets.load(new JacksonFactory(), is);
        } catch (IOException e) {
            LOG.error(e.getLocalizedMessage(), e);
            throw new RuntimeException(e.getLocalizedMessage(), e);
        }
    }

}
