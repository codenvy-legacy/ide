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

import com.codenvy.commons.security.oauth.GitHubOAuthAuthenticator;
import com.codenvy.commons.security.oauth.GoogleOAuthAuthenticator;
import com.codenvy.commons.security.oauth.OAuthAuthenticator;
import com.codenvy.commons.security.oauth.OAuthAuthenticatorProvider;
import com.google.api.client.auth.oauth2.MemoryCredentialStore;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.json.jackson.JacksonFactory;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/** Grub all implementations of OAuthAuthenticator from eXo container. */
public class LabOAuthAuthenticatorProvider implements OAuthAuthenticatorProvider {

    private static final Logger LOG = LoggerFactory.getLogger(LabOAuthAuthenticatorProvider.class);

    private final Map<String, OAuthAuthenticator> authenticators;

    public LabOAuthAuthenticatorProvider() {
        authenticators = new HashMap<String, OAuthAuthenticator>();

        GitHubOAuthAuthenticator gitHubOAuthAuthenticator =
                new GitHubOAuthAuthenticator(new MemoryCredentialStore(),
                                             getClientSecrets("github_client_secrets.json"));
        GoogleOAuthAuthenticator googleOAuthAuthenticator =
                new GoogleOAuthAuthenticator(new MemoryCredentialStore(),
                                             getClientSecrets("google_client_secrets.json"));

        authenticators.put(gitHubOAuthAuthenticator.getOAuthProvider(), gitHubOAuthAuthenticator);
        authenticators.put(googleOAuthAuthenticator.getOAuthProvider(), googleOAuthAuthenticator);


    }

    @Override
    public OAuthAuthenticator getAuthenticator(String oauthProviderName) {
        return authenticators.get(oauthProviderName);
    }

    private GoogleClientSecrets getClientSecrets(String fileName) {
        File clientSecrets = new File(System.getProperty("codenvy.local.conf.dir"), fileName);
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
