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
import com.codenvy.api.runner.internal.Constants;
import com.codenvy.api.runner.internal.ResourceAllocators;
import com.codenvy.api.runner.internal.RunnerRegistry;
import com.codenvy.commons.json.JsonHelper;
import com.codenvy.commons.json.JsonParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * The following directory structure is required:
 * <pre>
 *     ${runner.docker.dockerfiles_repo}/
 *        JavaWeb/
 *            Tomcat7/
 *                run.dc5y
 *                debug.dc5y
 *                dockerenv.c5y.json
 *            default/
 *                run.dc5y
 *                debug.dc5y
 *                dockerenv.c5y.json
 * </pre>
 * <ul>
 * <li><b>${runner.docker.dockerfiles_repo}</b> - configuration parameter that points to the root directory where docker files for all
 * supported runners and environments are located</li>
 * <li><b>JavaWeb</b> - directory that contains description of environments for running java web application</li>
 * <li><b>Tomcat7</b> - directory that contains description of environment that uses tomcat 7. This directory must contains file
 * <i>run.dc5y</i> and might contain files <i>debug.dc5y</i> and <i>dockerenv.c5y.json</i>. Docker based runner uses a <i>run.dc5y</i> to
 * create a Docker image that contains user's application and instruction how to start it. Docker based runner uses a <i>debug.dc5y</i> to
 * create a Docker image that contains user's application and instruction how to start application under debug. Need to have this file only
 * is
 * support debug for this type of application. File <i>dockerenv.c5y.json</i> contains additional information. It is possible to override
 * default names <i>run.dc5y</i> and <i>debug.dc5y</i> with <i>dockerenv.c5y.json</i> file. See {@link
 * DockerEnvironment#getRunDockerfileName()} and {@link DockerEnvironment#getDebugDockerfileName()}.</li>
 * </ul>
 * Typically each type of application might have default environment. In example above such environment is located in directory
 * <i>default</i>.
 *
 * @author andrew00x
 */
@Singleton
public class EmbeddedDockerRunnerRegistryPlugin {
    private static final Logger LOG = LoggerFactory.getLogger(EmbeddedDockerRunnerRegistryPlugin.class);

    public static final String DOCKERFILES_REPO = "runner.docker.dockerfiles_repo";

    private final List<EmbeddedDockerRunner> myRunners;
    private final RunnerRegistry             registry;

    @Inject
    public EmbeddedDockerRunnerRegistryPlugin(RunnerRegistry registry,
                                              @Named(Constants.DEPLOY_DIRECTORY) java.io.File deployDirectoryRoot,
                                              @Named(Constants.APP_CLEANUP_TIME) int cleanupTime,
                                              @Named(BaseDockerRunner.HOST_NAME) String hostName,
                                              ResourceAllocators allocators,
                                              CustomPortService portService,
                                              EventService eventService,
                                              @Nullable @Named(DOCKERFILES_REPO) String dockerfilesRepository) {
        this.registry = registry;
        this.myRunners = new LinkedList<>();
        java.io.File dockerFilesDir = null;
        if (!(dockerfilesRepository == null || dockerfilesRepository.isEmpty())) {
            dockerFilesDir = new java.io.File(dockerfilesRepository);
        }
        if (dockerFilesDir == null) {
            final URL dockerFilesUrl = Thread.currentThread().getContextClassLoader().getResource("codenvy/runner/docker");
            if (dockerFilesUrl != null) {
                try {
                    dockerFilesDir = new java.io.File(dockerFilesUrl.toURI());
                } catch (URISyntaxException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
        if (dockerFilesDir != null && dockerFilesDir.isDirectory()) {
            for (Map.Entry<String, List<DockerEnvironment>> entry : findEnvironments(dockerFilesDir).entrySet()) {
                myRunners.add(new EmbeddedDockerRunner(deployDirectoryRoot,
                                                       cleanupTime,
                                                       hostName,
                                                       allocators,
                                                       portService,
                                                       eventService,
                                                       entry.getKey(),
                                                       entry.getValue(),
                                                       dockerFilesDir));
            }
        }
    }

    @PostConstruct
    private void start() {
        for (EmbeddedDockerRunner runner : myRunners) {
            runner.start();
            registry.add(runner);
        }
    }

    @PreDestroy
    private void stop() {
        for (EmbeddedDockerRunner runner : myRunners) {
            registry.remove(runner.getName());
            runner.stop();
        }
    }

    private final FileFilter dirFilter = new FileFilter() {
        @Override
        public boolean accept(java.io.File pathname) {
            return pathname.isDirectory();
        }
    };

    private Map<String, List<DockerEnvironment>> findEnvironments(java.io.File dockerFilesDir) {
        final Map<String, List<DockerEnvironment>> environments = new HashMap<>();
        for (java.io.File runnerDir : dockerFilesDir.listFiles(dirFilter)) {
            final String runnerName = runnerDir.getName();
            final LinkedList<DockerEnvironment> envList = new LinkedList<>();
            environments.put(runnerName, envList);
            for (java.io.File envDir : runnerDir.listFiles(dirFilter)) {
                final java.io.File envFile = new java.io.File(envDir, "dockerenv.c5y.json");
                if (envFile.exists()) {
                    try {
                        try (FileReader r = new FileReader(envFile)) {
                            final DockerEnvironment environment = JsonHelper.fromJson(r, DockerEnvironment.class, null);
                            if (environment.getId() == null) {
                                environment.setId(envDir.getName());
                            }
                            envList.add(environment);
                        }
                    } catch (IOException | JsonParseException e) {
                        LOG.error(e.getMessage(), e);
                    }
                } else {
                    final DockerEnvironment environment = new DockerEnvironment();
                    environment.setId(envDir.getName());
                    envList.add(environment);
                }
            }
        }
        return environments;
    }
}
