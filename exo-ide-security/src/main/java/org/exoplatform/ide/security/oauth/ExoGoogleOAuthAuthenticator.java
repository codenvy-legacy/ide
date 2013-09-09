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

import com.codenvy.commons.security.oauth.GoogleOAuthAuthenticator;
import com.codenvy.ide.commons.server.ContainerUtils;
import com.google.api.client.auth.oauth2.CredentialStore;
import com.google.api.client.auth.oauth2.MemoryCredentialStore;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.json.jackson.JacksonFactory;

import org.exoplatform.container.xml.InitParams;

import java.io.IOException;
import java.io.InputStream;

/** Implementation of GoogleOAuthAuthenticator configured throw eXo container. */
public class ExoGoogleOAuthAuthenticator extends GoogleOAuthAuthenticator {
    public ExoGoogleOAuthAuthenticator(InitParams initParams) {
        super(new MemoryCredentialStore(), createClientSecrets(initParams));
    }

    public ExoGoogleOAuthAuthenticator(CredentialStore credentialStore, InitParams initParams) {
        super(credentialStore, createClientSecrets(initParams));
    }

    public static GoogleClientSecrets loadClientSecrets(String configName) throws IOException {
        InputStream secrets = Thread.currentThread().getContextClassLoader().getResourceAsStream(configName);
        if (secrets != null) {
            // stream closed after parsing by JsonFactory.
            return GoogleClientSecrets.load(new JacksonFactory(), secrets);
        }
        throw new IOException("Cannot load client secrets. File '" + configName + "' not found. ");
    }

    public static GoogleClientSecrets createClientSecrets(InitParams initParams) {
        final String type = ContainerUtils.readValueParam(initParams, "type");
        if (!("installed".equals(type) || "web".equals(type))) {
            throw new IllegalArgumentException("Invalid credentials type " + type + " .Must be 'web' or 'installed'. ");
        }
        GoogleClientSecrets.Details cfg = new GoogleClientSecrets.Details();
        cfg.setClientId(ContainerUtils.readValueParam(initParams, "client-id"))
           .setClientSecret(ContainerUtils.readValueParam(initParams, "client-secret"))
           .setAuthUri(ContainerUtils.readValueParam(initParams, "auth-uri"))
           .setTokenUri(ContainerUtils.readValueParam(initParams, "token-uri"))
           .setRedirectUris(ContainerUtils.readValuesParam(initParams, "redirect-uris"));
        return "web".equals(type) ? new GoogleClientSecrets().setWeb(cfg) : new GoogleClientSecrets().setInstalled(cfg);
    }
}
