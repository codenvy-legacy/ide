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
package com.codenvy.ide.ext.git.server.commons;

import com.codenvy.dto.server.DtoFactory;
import com.codenvy.ide.ext.git.server.GitConnection;
import com.codenvy.ide.ext.git.server.GitConnectionFactory;
import com.codenvy.ide.ext.git.server.GitException;
import com.codenvy.ide.ext.git.shared.LsRemoteRequest;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.ws.rs.core.UriBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Check if repository is public and set private permission to vfs otherwise.
 *
 * @author Vladyslav Zhukovskii
 */
@Singleton
public class GitRepositoryPrivacyChecker {
    private static final Pattern SSH_URL = Pattern.compile("^(?:ssh://)?(?:\\w+@)([a-zA-Z-.]+)(?::(\\d+))?+(?:/|:)(.+)$");
    private final GitConnectionFactory gitConnectionFactory;

    @Inject
    public GitRepositoryPrivacyChecker(GitConnectionFactory gitConnectionFactory) {
        this.gitConnectionFactory = gitConnectionFactory;
    }

    /**
     * Check repository public or not with calling git ls-remote.
     * If url is SSH, it will be converted to https or http, because ssh can't be checked in such way.
     *
     * @param gitUrl
     *         repository url
     * @return <code>true</code> when repository is public
     */
    public boolean isRepositoryPublic(String gitUrl) {
        Matcher matcher = SSH_URL.matcher(gitUrl);
        if (!matcher.matches()) {
            // call git ls-remote
            try {
                GitConnection gitConnection = gitConnectionFactory.getConnection("/tmp");
                gitConnection.lsRemote(DtoFactory.getInstance().createDto(LsRemoteRequest.class).withRemoteUrl(gitUrl));
                return true;
            } catch (GitException e) {
                return false;
            }
        } else {
            // if url is ssh, use special check
            // order is important
            String host = matcher.group(1);
            String port = matcher.group(2);
            String path = matcher.group(3);
            List<String> gitUrls = new ArrayList<>();
            gitUrls.add(createHttpsUrl(host, port, path));
            gitUrls.add(createHttpUrl(host, port, path));
            gitUrls.add(createHttpsWithoutPortUrl(host, path));
            gitUrls.add(createHttpWithoutPortUrl(host, path));
            for (String repoUrl : gitUrls) {
                try {
                    GitConnection gitConnection = gitConnectionFactory.getConnection("/tmp", null);
                    gitConnection.lsRemote(DtoFactory.getInstance().createDto(LsRemoteRequest.class)
                                                     .withRemoteUrl(repoUrl)
                                                     .withUseAuthorization(false));
                    return true;
                } catch (GitException ignored) {
                    // try another url to check for
                }
            }
            return false;
        }
    }

    private String createHttpsUrl(String host, String port, String path) {
        UriBuilder ub = UriBuilder.fromPath(path);
        ub.scheme("https").host(host);
        if (port != null && port.isEmpty()) {
            ub.port(Integer.parseInt(port));
        }
        return ub.build().toString();
    }

    private String createHttpUrl(String host, String port, String path) {
        UriBuilder ub = UriBuilder.fromPath(path);
        ub.scheme("http").host(host);
        if (port != null && port.isEmpty()) {
            ub.port(Integer.parseInt(port));
        }
        return ub.build().toString();
    }

    private String createHttpsWithoutPortUrl(String host, String path) {
        return UriBuilder.fromPath(path).host(host).scheme("https").build().toString();
    }

    private String createHttpWithoutPortUrl(String host, String path) {
        return UriBuilder.fromPath(path).host(host).scheme("http").build().toString();
    }
}
