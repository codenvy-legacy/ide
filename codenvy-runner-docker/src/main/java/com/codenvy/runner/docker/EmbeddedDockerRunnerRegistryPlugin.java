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
import com.codenvy.api.core.util.Pair;
import com.codenvy.api.runner.internal.Constants;
import com.codenvy.api.runner.internal.ResourceAllocators;
import com.codenvy.api.runner.internal.RunnerRegistry;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.File;
import java.io.FileFilter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author andrew00x
 */
@Singleton
public class EmbeddedDockerRunnerRegistryPlugin {
    public static final String DOCKERFILES_REPO = "runner.docker.dockerfiles_repo";


    private final List<EmbeddedDockerRunner> myRunners;
    private final RunnerRegistry             registry;

    @Inject
    public EmbeddedDockerRunnerRegistryPlugin(RunnerRegistry registry,
                                              @Named(Constants.DEPLOY_DIRECTORY) File deployDirectoryRoot,
                                              @Named(Constants.APP_CLEANUP_TIME) int cleanupTime,
                                              @Named(BaseDockerRunner.HOST_NAME) String hostName,
                                              ResourceAllocators allocators,
                                              CustomPortService portService,
                                              EventService eventService,
                                              @Nullable @Named(DOCKERFILES_REPO) String dockerfilesRepository) {
        this.registry = registry;
        this.myRunners = new LinkedList<>();
        for (Map.Entry<String, Map<String, List<Pair<String, File>>>> entry : findDockerfiles(dockerfilesRepository).entrySet()) {
            myRunners.add(new EmbeddedDockerRunner(deployDirectoryRoot,
                                                   cleanupTime,
                                                   hostName,
                                                   allocators,
                                                   portService,
                                                   eventService,
                                                   entry.getKey(),
                                                   entry.getValue()));
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

    /** Finds Dockerfiles. */
    private Map<String, Map<String, List<Pair<String, File>>>> findDockerfiles(String dockerfilesRepository) {
        File dockerFilesDir = null;
        if (!(dockerfilesRepository == null || dockerfilesRepository.isEmpty())) {
            dockerFilesDir = new File(dockerfilesRepository);
        }
        if (dockerFilesDir == null) {
            final URL dockerFilesUrl = Thread.currentThread().getContextClassLoader().getResource("codenvy/runner/docker");
            if (dockerFilesUrl != null) {
                try {
                    dockerFilesDir = new File(dockerFilesUrl.toURI());
                } catch (URISyntaxException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
        if (dockerFilesDir != null && dockerFilesDir.isDirectory()) {
            final Map<String, Map<String, List<Pair<String, File>>>> dockerFiles = new HashMap<>();
            final FileFilter dirFilter = new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            };
            final FileFilter fileFilter = new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    String name;
                    return pathname.isFile() && ((name = pathname.getName()).equals("Dockerfile") || name.equals("Dockerfile_Debug"));
                }
            };
            for (File file : dockerFilesDir.listFiles(dirFilter)) {
                final String runnerName = file.getName();
                dockerFiles.put(runnerName, new HashMap<String, List<Pair<String, File>>>());
                for (File _file : file.listFiles(dirFilter)) {
                    final String envName = _file.getName();
                    dockerFiles.get(runnerName).put(envName, new LinkedList<Pair<String, File>>());
                    for (File __file : _file.listFiles(fileFilter)) {
                        final String fName = __file.getName();
                        dockerFiles.get(runnerName).get(envName).add(Pair.of(fName, __file));
                    }
                }
            }
            return dockerFiles;
        }
        return Collections.emptyMap();
    }
}
