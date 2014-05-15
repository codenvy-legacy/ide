/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2014] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.runner.docker;

import com.codenvy.api.core.notification.EventService;
import com.codenvy.api.core.util.CustomPortService;
import com.codenvy.api.runner.dto.RunRequest;
import com.codenvy.api.runner.internal.Constants;
import com.codenvy.api.runner.internal.ResourceAllocators;
import com.codenvy.commons.json.JsonHelper;
import com.codenvy.commons.json.JsonParseException;
import com.google.common.io.ByteStreams;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * @author andrew00x
 */
@Singleton
public class DockerRunner extends BaseDockerRunner {
    @Inject
    public DockerRunner(@Named(Constants.DEPLOY_DIRECTORY) java.io.File deployDirectoryRoot,
                        @Named(Constants.APP_CLEANUP_TIME) int cleanupTime,
                        @Named(HOST_NAME) String hostName,
                        ResourceAllocators allocators,
                        CustomPortService portService,
                        EventService eventService) {
        super(deployDirectoryRoot, cleanupTime, hostName, allocators, portService, eventService);
    }

    @Override
    protected DockerEnvironment getDockerEnvironment(RunRequest request) throws IOException {
        final List<String> scriptUrls = request.getRunnerScriptUrls();
        if (scriptUrls.isEmpty()) {
            return null;
        }
        String myEnv = null;
        for (int i = 0, size = scriptUrls.size(); i < size && myEnv == null; i++) {
            final String scriptUrl = scriptUrls.get(i);
            final int queryStart = scriptUrl.indexOf('?');
            final String scriptPath = queryStart > 0 ? scriptUrl.substring(0, queryStart) : scriptUrl;
            if (scriptPath.endsWith("dockerenv.c5y.json")) {
                LOG.debug("Use docker environment file '{}'", scriptPath);
                myEnv = scriptUrl;
            }
        }
        if (myEnv == null) {
            return null;
        }
        try (InputStream in = new URL(myEnv).openStream()) {
            return JsonHelper.fromJson(in, DockerEnvironment.class, null);
        } catch (JsonParseException e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public String getName() {
        return "docker";
    }

    @Override
    public String getDescription() {
        return "The linux container runtime";
    }

    @Override
    protected Dockerfile getDockerfile(DockerEnvironment dockerEnvironment, RunRequest request) throws IOException {
        final boolean debug = request.getDebugMode() != null;
        String dockerFile = null;
        if (dockerEnvironment != null) {
            dockerFile = debug ? dockerEnvironment.getDebugDockerfileName() : dockerEnvironment.getRunDockerfileName();
        }
        if (dockerFile == null) {
            dockerFile = debug ? "debug.dc5y" : "run.dc5y";
        }
        final List<String> scriptUrls = request.getRunnerScriptUrls();
        if (scriptUrls.isEmpty()) {
            return null;
        }
        String myScript = findDockerfileUrl(scriptUrls, dockerFile);
        // If there is no Dockerfile for simple run try to use Dockerfile for run under debug, if any.
        if (myScript == null && !debug) {
            if (dockerEnvironment != null) {
                dockerFile = dockerEnvironment.getDebugDockerfileName();
            }
            if (dockerFile == null) {
                dockerFile = "debug.dc5y";
            }
            myScript = findDockerfileUrl(scriptUrls, dockerFile);
        }
        if (myScript == null) {
            return null;
        }
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (InputStream in = new URL(myScript).openStream()) {
            ByteStreams.copy(in, output);
        }
        return DockerfileParser.parse(output.toString());
    }

    private String findDockerfileUrl(List<String> scriptUrls, String dockerFile) {
        for (final String scriptUrl : scriptUrls) {
            final int queryStart = scriptUrl.indexOf('?');
            final String scriptPath = queryStart > 0 ? scriptUrl.substring(0, queryStart) : scriptUrl;
            if (scriptPath.endsWith(dockerFile)) {
                LOG.debug("Use dockerfile '{}'", scriptPath);
                return scriptUrl;
            }
        }
        return null;
    }
}
