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

import com.google.api.client.auth.oauth2.CredentialStore;
import com.google.api.client.auth.oauth2.MemoryCredentialStore;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.json.jackson.JacksonFactory;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.ide.commons.ContainerUtils;

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
