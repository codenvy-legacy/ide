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
package com.codenvy.ide.ext.github.server;

import com.codenvy.api.auth.oauth.OAuthTokenProvider;
import com.codenvy.api.auth.shared.dto.OAuthToken;
import com.codenvy.commons.env.EnvironmentContext;
import com.codenvy.commons.json.JsonHelper;
import com.codenvy.dto.server.DtoFactory;
import com.codenvy.ide.MimeType;
import com.codenvy.ide.ext.git.server.GitException;
import com.codenvy.ide.ext.git.server.commons.Util;
import com.codenvy.ide.ext.git.server.NotAuthorizedException;
import com.codenvy.ide.ext.git.server.nativegit.SshKeyUploaderProvider;
import com.codenvy.ide.ext.github.shared.GitHubKey;
import com.codenvy.ide.ext.ssh.server.SshKey;
import com.codenvy.ide.ext.ssh.server.SshKeyStore;
import com.codenvy.ide.ext.ssh.server.SshKeyStoreException;
import com.codenvy.ide.rest.HTTPHeader;
import com.codenvy.ide.rest.HTTPMethod;
import com.codenvy.ide.rest.HTTPStatus;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * @author Vladyslav Zhukovskii
 */
@Singleton
public class GitHubKeyUploaderProvider implements SshKeyUploaderProvider {

    private SshKeyStore        keyStore;
    private OAuthTokenProvider tokenProvider;

    private static final String SSH_KEY_STORE_HOST = "github.com";

    private static final Logger LOG = LoggerFactory.getLogger(GitHubKeyUploaderProvider.class);

    @Inject
    public GitHubKeyUploaderProvider(SshKeyStore keyStore, OAuthTokenProvider tokenProvider) {
        this.keyStore = keyStore;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public boolean match(String url) {
        return Util.isSSH(url) && Util.isGitHub(url);
    }

    @Override
    public boolean uploadKey() throws GitException {
        try {
            final OAuthToken token =
                    tokenProvider.getToken("github", EnvironmentContext.getCurrent().getUser().getId());

            if (token == null || token.getToken() == null) {
                LOG.info("Token not found, user need to authorize to upload key.");
                throw new NotAuthorizedException("To upload public SSH public key you need to authorize.");
            }

            if (keyStore.getPrivateKey(SSH_KEY_STORE_HOST) != null) {
                String ideStoredPublicKeyPart = new String(keyStore.getPublicKey(SSH_KEY_STORE_HOST).getBytes());

                List<GitHubKey> gitHubUserPublicKeys = getUserPublicKeys(token);
                for (GitHubKey gitHubUserPublicKey : gitHubUserPublicKeys) {
                    if (ideStoredPublicKeyPart.startsWith(gitHubUserPublicKey.getKey())) {
                        return false;
                    }
                }

                keyStore.removeKeys(SSH_KEY_STORE_HOST);
                LOG.info("Deleting exist private key.");
            }

            keyStore.genKeyPair(SSH_KEY_STORE_HOST, null, null);
            final SshKey publicKey = keyStore.getPublicKey(SSH_KEY_STORE_HOST);

            Map<String, String> postParams = new HashMap<>(2);
            postParams.put("title", Util.getCodenvyTimeStamptKeyLabel());
            postParams.put("key", new String(publicKey.getBytes()));

            final String postBody = JsonHelper.toJson(postParams);

            LOG.info("Upload public key: {}", postBody);

            final String url = String.format("https://api.github.com/user/keys?access_token=%s", token.getToken());

            HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod(HTTPMethod.POST);
            conn.setRequestProperty(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON);
            conn.setRequestProperty(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON);
            conn.setRequestProperty(HTTPHeader.CONTENT_LENGTH, String.valueOf(postBody.length()));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postBody.getBytes());
            conn.connect();

            final int responseCode = conn.getResponseCode();

            LOG.info("Upload key response code: {}", responseCode);

            if (responseCode == HTTPStatus.CREATED) { //according to github api - its OK
                return true;
            } else {
                throw new GitException(String.format("%d: Failed to upload public key to http://github.com/", responseCode));
            }

        } catch (SshKeyStoreException | IOException e) {
            return false;
        }
    }

    private List<GitHubKey> getUserPublicKeys(OAuthToken token) {
        try {
            final String url = String.format("https://api.github.com/user/keys?access_token=%s", token.getToken());

            HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod(HTTPMethod.GET);
            conn.setRequestProperty(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON);
            conn.connect();

            final int responseCode = conn.getResponseCode();

            if (responseCode != HTTPStatus.OK) {
                return Collections.emptyList();
            }

            StringBuilder answer = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    answer.append(line).append("\n");
                }
            }

            return DtoFactory.getInstance().createListDtoFromJson(answer.toString(), GitHubKey.class);
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }
}
