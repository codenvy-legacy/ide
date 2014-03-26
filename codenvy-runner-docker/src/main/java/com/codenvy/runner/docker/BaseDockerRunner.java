/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
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
import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.api.core.util.CustomPortService;
import com.codenvy.api.core.util.Pair;
import com.codenvy.api.runner.RunnerException;
import com.codenvy.api.runner.internal.ApplicationLogger;
import com.codenvy.api.runner.internal.ApplicationProcess;
import com.codenvy.api.runner.internal.DeploymentSources;
import com.codenvy.api.runner.internal.Disposer;
import com.codenvy.api.runner.internal.ResourceAllocators;
import com.codenvy.api.runner.internal.Runner;
import com.codenvy.api.runner.internal.RunnerConfiguration;
import com.codenvy.api.runner.internal.RunnerConfigurationFactory;
import com.codenvy.api.runner.internal.dto.DebugMode;
import com.codenvy.api.runner.internal.dto.RunRequest;
import com.codenvy.dto.server.DtoFactory;
import com.codenvy.runner.docker.dockerfile.DockerImage;
import com.codenvy.runner.docker.dockerfile.DockerfileParser;
import com.codenvy.runner.docker.json.ContainerConfig;
import com.codenvy.runner.docker.json.ContainerCreated;
import com.codenvy.runner.docker.json.HostConfig;
import com.codenvy.runner.docker.json.Image;
import com.codenvy.runner.docker.json.PortBinding;
import com.google.common.hash.Hashing;
import com.google.common.io.CharStreams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** @author andrew00x */
public abstract class BaseDockerRunner extends Runner {
    private static final Logger LOG = LoggerFactory.getLogger(BaseDockerRunner.class);

    // Pattern for ports in range [0;65535]
    // Public HTTP ports of application should be defined as environment variables in dockerfile in following format:
    // ENV APPLICATION_PORT_{NUMBER}_HTTP {NUMBER}
    // For example, propagate port 8080:
    // ENV APPLICATION_PORT_8080_HTTP 8080
    // See docker docs for details about format of dockerfile.
    private static final Pattern HTTP_PORT_PATTERN  =
            Pattern.compile("APPLICATION_PORT_([0-9]|[1-9][0-9]|[1-9][0-9][0-9]|[1-9][0-9][0-9][0-9]|[1-6][0-5][0-5][0-3][0-5])_HTTP");
    // Pattern for debug ports in range  [0;65535]
    // Example:
    // ENV APPLICATION_PORT_8000_DEBUG 8000
    private static final Pattern DEBUG_PORT_PATTERN =
            Pattern.compile("APPLICATION_PORT_([0-9]|[1-9][0-9]|[1-9][0-9][0-9]|[1-9][0-9][0-9][0-9]|[1-6][0-5][0-5][0-3][0-5])_DEBUG");

    private final Map<String, ImageUsage> dockerImageUsage;
    private final String                  hostName;
    private final CustomPortService       portService;

    protected BaseDockerRunner(java.io.File deployDirectoryRoot,
                               int cleanupDelay,
                               String hostName,
                               ResourceAllocators allocators,
                               CustomPortService portService,
                               EventService eventService) {
        super(deployDirectoryRoot, cleanupDelay, allocators, eventService);
        this.hostName = hostName;
        this.portService = portService;
        this.dockerImageUsage = new HashMap<>();
    }

    @Override
    public RunnerConfigurationFactory getRunnerConfigurationFactory() {
        return new RunnerConfigurationFactory() {
            @Override
            public RunnerConfiguration createRunnerConfiguration(RunRequest request) throws RunnerException {
                return new DockerRunnerConfiguration(request.getMemorySize(), request);
            }
        };
    }

    @Override
    protected ApplicationProcess newApplicationProcess(DeploymentSources toDeploy, RunnerConfiguration runnerCfg) throws RunnerException {
        try {
            final java.io.File applicationFile = toDeploy.getFile();
            // It always should be DockerRunnerConfiguration.
            final DockerRunnerConfiguration dockerRunnerCfg = (DockerRunnerConfiguration)runnerCfg;
            final DockerfileTemplate dockerfileTemplate = getDockerfileTemplate(dockerRunnerCfg.getRequest());
            if (dockerfileTemplate == null) {
                throw new RunnerException("Unable create environment for starting application. Acceptable dockerfile isn't found.");
            }
            // Apply parameters and write Dockerfile based on it's template.
            dockerfileTemplate.setParameters(runnerCfg.getOptions()).setParameter("app", applicationFile.getName());
            final java.io.File dockerfile = new java.io.File(applicationFile.getParentFile(), "Dockerfile");
            dockerfileTemplate.writeDockerfile(dockerfile);
            // Find port mapping specified in Dockerfile.
            final List<Pair<Integer, Integer>> portMapping = new LinkedList<>();
            int debugPort = -1;
            try (Reader reader = Files.newBufferedReader(dockerfile.toPath(), Charset.forName("UTF-8"))) {
                for (DockerImage dockerImage : DockerfileParser.parse(reader)) {
                    for (Map.Entry<String, String> entry : dockerImage.getEnv().entrySet()) {
                        final String name = entry.getKey();
                        if (HTTP_PORT_PATTERN.matcher(name).matches()) {
                            final int privatePort = Integer.parseInt(entry.getValue());
                            final int publicPort = portService.acquire();
                            portMapping.add(Pair.of(publicPort, privatePort));
                            // Web link for application.
                            // TODO: need something more flexible to avoid showing port in URL.
                            final String webUrl = String.format("http://%s:%d", hostName, publicPort);
                            dockerRunnerCfg.getLinks().add(DtoFactory.getInstance().createDto(Link.class)
                                                                     .withRel("web url")
                                                                     .withHref(webUrl));
                        } else if (DEBUG_PORT_PATTERN.matcher(name).matches()) {
                            final int privatePort = Integer.parseInt(entry.getValue());
                            debugPort = portService.acquire();
                            portMapping.add(Pair.of(debugPort, privatePort));
                        }
                    }
                }
            } catch (IOException e) {
                throw new RunnerException(e);
            }
            final DebugMode debugMode = dockerRunnerCfg.getRequest().getDebugMode();
            if (debugMode != null) {
                dockerRunnerCfg.setDebugHost(hostName);
                dockerRunnerCfg.setDebugPort(debugPort);
                dockerRunnerCfg.setDebugSuspend("suspend".equals(debugMode.getMode()));
            }

            final String workspace = runnerCfg.getRequest().getWorkspace();
            final String project = runnerCfg.getRequest().getProject();
            final String dockerRepoName = workspace + (project.startsWith("/") ? project : ('/' + runnerCfg.getRequest().getProject()));
            final String fileHash = com.google.common.io.Files.hash(applicationFile, Hashing.sha1()).toString();
            final DockerConnector connector = DockerConnector.getInstance();
            final ImageUsage imageUsage = createImageIfNeed(connector, dockerRepoName, fileHash, dockerfile, applicationFile);
            final ContainerConfig containerConfig =
                    new ContainerConfig().withImage(imageUsage.image).withMemory(runnerCfg.getMemory() * 1024 * 1024).withCpuShares(1);
            HostConfig hostConfig = null;
            if (!portMapping.isEmpty()) {
                hostConfig = new HostConfig();
                final Map<String, PortBinding[]> portBinding = new HashMap<>(portMapping.size());
                for (Pair<Integer, Integer> p : portMapping) {
                    portBinding.put(String.format("%d/tcp", p.second),
                                    new PortBinding[]{new PortBinding().withHostPort(Integer.toString(p.first))});
                }
                hostConfig.setPortBindings(portBinding);
            }
            final DockerProcess docker = new DockerProcess(connector, containerConfig, hostConfig, new DockerProcess.Callback() {
                @Override
                public void started() {
                }

                @Override
                public void stopped() {
                    for (Pair<Integer, Integer> p : portMapping) {
                        portService.release(p.first);
                    }
                }
            });
            imageUsage.inc();
            registerDisposer(docker, new Disposer() {
                @Override
                public void dispose() {
                    try {
                        if (docker.isRunning()) {
                            docker.stop();
                        }
                        connector.removeContainer(docker.container, true);
                        LOG.debug("Remove docker container: {}", docker.container);
                    } catch (Exception e) {
                        LOG.error(e.getMessage(), e);
                    }
                    try {
                        maybeDeleteImage(connector, imageUsage);
                    } catch (Exception e) {
                        LOG.error(e.getMessage(), e);
                    }
                }
            });
            return docker;
        } catch (IOException e) {
            throw new RunnerException(e);
        }
    }

    protected abstract DockerfileTemplate getDockerfileTemplate(RunRequest request) throws IOException;

    private static final Pattern BUILD_LOG_PATTERN = Pattern.compile("\\{[^\\}^\\{]+\\}");

    private synchronized ImageUsage createImageIfNeed(DockerConnector connector,
                                                      String dockerRepoName,
                                                      String tag,
                                                      java.io.File dockerFile,
                                                      java.io.File applicationFile) throws IOException, RunnerException {
        String dockerImageId = null;
        final Image[] images = connector.listImages();
        for (int i = 0, l = images.length; i < l && dockerImageId == null; i++) {
            Image image = images[i];
            if (dockerRepoName.equals(image.getRepository()) && tag.equals(image.getTag())) {
                dockerImageId = image.getId();
            }
        }
        final String dockerImageName = dockerRepoName + ':' + tag;
        if (dockerImageId == null) {
            final StringBuilder output = new StringBuilder();
            connector.createImage(dockerFile, applicationFile, dockerRepoName, tag, output);
            final String buildLog = output.toString();
            LOG.debug(buildLog);
            for (String line : CharStreams.readLines(new StringReader(buildLog))) {
                if (line.startsWith("Error build:")) {
                    throw new RunnerException(String.format("Error building Docker container, response from Docker API:\n%s\n", buildLog));
                }
            }
            final Matcher matcher = BUILD_LOG_PATTERN.matcher(buildLog);
            if (matcher.find()) {
                do {
                    final String msg = buildLog.substring(matcher.start() + 1, matcher.end() - 1);
                    if (msg.startsWith("\"stream\":\"Successfully built ")) {
                        int endSize = 29; // length of '"stream":"Successfully built '
                        while (endSize < msg.length() && Character.digit(msg.charAt(endSize), 16) != -1) {
                            endSize++;
                        }
                        dockerImageId = msg.substring(29, endSize);
                    }
                } while (matcher.find());
            }
            if (dockerImageId == null) {
                throw new RunnerException("Invalid response from Docker API, can't get ID of newly created image");
            }
            LOG.debug("Create new image {}, id {}", dockerImageName, dockerImageId);
        } else {
            LOG.debug("Image {} exists, id {}", dockerImageName, dockerImageId);
        }
        ImageUsage imageUsage = dockerImageUsage.get(dockerImageName);
        if (imageUsage == null) {
            dockerImageUsage.put(dockerImageName, imageUsage = new ImageUsage(dockerImageId));
        }
        return imageUsage;
    }

    private void maybeDeleteImage(DockerConnector connector, ImageUsage imageUsage) throws IOException {
        if (imageUsage.dec() == 0) {
            connector.removeImage(imageUsage.image);
            LOG.debug("Remove docker image: {}", imageUsage.image);
        }
    }

    public static class DockerRunnerConfiguration extends RunnerConfiguration {
        public DockerRunnerConfiguration(int memory, RunRequest request) {
            super(memory, request);
        }
    }

    private static class ImageUsage {
        final String image;
        int count;

        ImageUsage(String image) {
            this.image = image;
        }

        synchronized int inc() {
            return ++count;
        }

        synchronized int dec() {
            return --count;
        }

        synchronized int count() {
            return count;
        }

        @Override
        public String toString() {
            return "ImageUsage{" +
                   "image='" + image + '\'' +
                   ", count=" + count +
                   '}';
        }
    }

    private static class DockerProcess extends ApplicationProcess {
        final DockerConnector connector;
        final ContainerConfig containerCfg;
        final HostConfig      hostCfg;
        final Callback        callback;
        String       container;
        DockerLogger logger;

        DockerProcess(DockerConnector connector, ContainerConfig containerCfg, HostConfig hostCfg, Callback callback) {
            this.connector = connector;
            this.containerCfg = containerCfg;
            this.hostCfg = hostCfg;
            this.callback = callback;
        }

        @Override
        public synchronized void start() throws RunnerException {
            if (container != null) {
                throw new IllegalStateException("Process is already started");
            }
            try {
                final ContainerCreated response = connector.createContainer(containerCfg);
                connector.startContainer(response.getId(), hostCfg);
                container = response.getId();
                logger = new DockerLogger(connector, container);
            } catch (IOException e) {
                throw new RunnerException(e);
            }
            if (callback != null) {
                callback.started();
            }
        }

        @Override
        public synchronized void stop() throws RunnerException {
            if (container == null) {
                throw new IllegalStateException("Process is not started yet");
            }
            try {
                connector.stopContainer(container, 3, TimeUnit.SECONDS);
            } catch (IOException e) {
                throw new RunnerException(e);
            }
            if (callback != null) {
                callback.stopped();
            }
        }

        @Override
        public int waitFor() throws RunnerException {
            synchronized (this) {
                if (container == null) {
                    throw new IllegalStateException("Process is not started yet");
                }
            }
            try {
                return connector.waitContainer(container).getStatusCode();
            } catch (IOException e) {
                throw new RunnerException(e);
            }
        }

        @Override
        public synchronized int exitCode() throws RunnerException {
            if (container == null) {
                return -1;
            }
            try {
                return connector.inspectContainer(container).getState().getExitCode();
            } catch (IOException e) {
                throw new RunnerException(e.getMessage(), e);
            }
        }

        @Override
        public synchronized boolean isRunning() throws RunnerException {
            if (container == null) {
                return false;
            }
            try {
                return connector.inspectContainer(container).getState().isRunning();
            } catch (IOException e) {
                throw new RunnerException(e.getMessage(), e);
            }
        }

        @Override
        public synchronized ApplicationLogger getLogger() throws RunnerException {
            if (logger == null) {
                // is not started yet
                return ApplicationLogger.DUMMY;
            }
            return logger;
        }

        private static interface Callback {
            void started();

            void stopped();
        }

        private static class DockerLogger implements ApplicationLogger {
            final DockerConnector connector;
            final String          container;

            DockerLogger(DockerConnector connector, String container) {
                this.connector = connector;
                this.container = container;
            }

            @Override
            public void getLogs(Appendable output) throws IOException {
                connector.getContainerLogs(container, output);
            }

            @Override
            public String getContentType() {
                return "text/plain";
            }

            @Override
            public void writeLine(String line) throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public void close() throws IOException {
            }
        }
    }
}
