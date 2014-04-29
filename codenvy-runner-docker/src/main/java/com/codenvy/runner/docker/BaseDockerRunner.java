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
import com.codenvy.api.core.util.LineConsumer;
import com.codenvy.api.core.util.Pair;
import com.codenvy.api.core.util.ValueHolder;
import com.codenvy.api.runner.RunnerException;
import com.codenvy.api.runner.dto.DebugMode;
import com.codenvy.api.runner.dto.RunRequest;
import com.codenvy.api.runner.internal.ApplicationLogger;
import com.codenvy.api.runner.internal.ApplicationProcess;
import com.codenvy.api.runner.internal.DeploymentSources;
import com.codenvy.api.runner.internal.Disposer;
import com.codenvy.api.runner.internal.ResourceAllocators;
import com.codenvy.api.runner.internal.Runner;
import com.codenvy.api.runner.internal.RunnerConfiguration;
import com.codenvy.api.runner.internal.RunnerConfigurationFactory;
import com.codenvy.commons.json.JsonHelper;
import com.codenvy.commons.json.JsonParseException;
import com.codenvy.commons.lang.NamedThreadFactory;
import com.codenvy.dto.server.DtoFactory;
import com.codenvy.runner.docker.dockerfile.DockerImage;
import com.codenvy.runner.docker.dockerfile.DockerfileParser;
import com.codenvy.runner.docker.json.BuildImageStatus;
import com.codenvy.runner.docker.json.ContainerConfig;
import com.codenvy.runner.docker.json.ContainerCreated;
import com.codenvy.runner.docker.json.HostConfig;
import com.codenvy.runner.docker.json.Image;
import com.codenvy.runner.docker.json.PortBinding;
import com.google.common.hash.Hashing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.Reader;
import java.net.ConnectException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

/** @author andrew00x */
public abstract class BaseDockerRunner extends Runner {
    static final Logger LOG = LoggerFactory.getLogger(BaseDockerRunner.class);

    public static final String HOST_NAME = "runner.docker.host_name";

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

    private final Map<String, ImageStats> dockerImages;
    private final String                  hostName;
    private final CustomPortService       portService;
    private final long                    maxImageIdleTime;

    private ScheduledExecutorService imageCleaner;

    protected BaseDockerRunner(java.io.File deployDirectoryRoot,
                               int cleanupDelay,
                               String hostName,
                               ResourceAllocators allocators,
                               CustomPortService portService,
                               EventService eventService) {
        super(deployDirectoryRoot, cleanupDelay, allocators, eventService);
        this.hostName = hostName;
        this.portService = portService;
        this.dockerImages = new HashMap<>();
        // TODO: configurable
        this.maxImageIdleTime = TimeUnit.MINUTES.toMillis(15);
    }

    @PostConstruct
    @Override
    public final void start() {
        super.start();
        imageCleaner = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory(getName() + "-ImageCleaner-", true));
        imageCleaner.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                synchronized (BaseDockerRunner.this) {
                    final DockerConnector connector = DockerConnector.getInstance();
                    final List<String> remove = new LinkedList<>();
                    for (Map.Entry<String, ImageStats> e : dockerImages.entrySet()) {
                        final ImageStats stats = e.getValue();
                        if (stats.idle() > maxImageIdleTime) {
                            try {
                                connector.removeImage(stats.image);
                                LOG.debug("Remove docker image: {}", stats.image);
                                remove.add(e.getKey());
                            } catch (IOException err) {
                                LOG.error(String.format("Failed remove docker image %s", stats.image), err);
                            }
                        }
                    }
                    dockerImages.keySet().removeAll(remove);
                }
            }
        }, 1, 1, TimeUnit.MINUTES);
        doStart();
    }

    protected void doStart() {
    }

    @Override
    public final void stop() {
        super.stop();
        imageCleaner.shutdownNow();
        final DockerConnector connector = DockerConnector.getInstance();
        for (ImageStats stats : dockerImages.values()) {
            try {
                connector.removeImage(stats.image);
                LOG.debug("Remove docker image: {}", stats.image);
            } catch (IOException e) {
                LOG.error(String.format("Failed remove docker image %s", stats.image), e);
            }
        }
        dockerImages.clear();
        doStop();
    }

    protected void doStop() {
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
                                                                     .withRel(com.codenvy.api.runner.internal.Constants.LINK_REL_WEB_URL)
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
            final ImageStats imageStats = createImageIfNeed(connector, dockerRepoName, fileHash, dockerfile, applicationFile);
            final ContainerConfig containerConfig =
                    new ContainerConfig().withImage(imageStats.image).withMemory(runnerCfg.getMemory() * 1024 * 1024).withCpuShares(1);
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
            final DockerProcess docker = new DockerProcess(connector, containerConfig, hostConfig, new ApplicationProcess.Callback() {
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
            imageStats.inc();
            registerDisposer(docker, new Disposer() {
                @Override
                public void dispose() {
                    try {
                        if (docker.isRunning()) {
                            docker.stop();
                        }
                        connector.removeContainer(docker.container, true);
                        LOG.debug("Remove docker container: {}", docker.container);
                        imageStats.dec();
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

    private synchronized ImageStats createImageIfNeed(DockerConnector connector,
                                                      String dockerRepoName,
                                                      String tag,
                                                      java.io.File dockerFile,
                                                      java.io.File applicationFile) throws IOException, RunnerException {
        final String dockerImageName = dockerRepoName + ':' + tag;
        ImageStats imageStats = dockerImages.get(dockerImageName);
        if (imageStats != null) {
            Image image = null;
            final Image[] images = connector.listImages();
            for (int i = 0, l = images.length; i < l && image == null; i++) {
                // While create new image we get short form of image id.
                // Unfortunately while get list of existed images we get long form of id.
                // That's why use 'startsWith' instead of 'equals'.
                if (images[i].getId().startsWith(imageStats.image)) {
                    image = images[i];
                }
            }
            if (image == null) {
                dockerImages.remove(dockerImageName);
                imageStats = null;
            }
        }
        final ValueHolder<String> imageIdH = new ValueHolder<>();
        final ValueHolder<String> errorH = new ValueHolder<>();
        if (imageStats == null) {
            final LineConsumer output = new LineConsumer() {
                @Override
                public void writeLine(String line) throws IOException {
                    if (!(line == null || line.isEmpty())) {
                        LOG.debug(line);
                        try {
                            final BuildImageStatus status = JsonHelper.fromJson(line, BuildImageStatus.class, null);
                            final String stream = status.getStream();
                            final String error = status.getError();
                            if (stream != null && stream.startsWith("Successfully built ")) {
                                int endSize = 19;
                                while (endSize < stream.length() && Character.digit(stream.charAt(endSize), 16) != -1) {
                                    endSize++;
                                }
                                imageIdH.set(stream.substring(19, endSize));
                            } else if (error != null && errorH.get() == null) {
                                errorH.set(error);
                            }
                        } catch (JsonParseException e) {
                            LOG.error(e.getMessage(), e);
                        }
                    }
                }

                @Override
                public void close() throws IOException {
                    // noop
                }
            };
            connector.createImage(dockerFile, applicationFile, dockerRepoName, tag, output);
            final String error = errorH.get();
            if (error != null) {
                throw new RunnerException(error);
            }
            final String imageId = imageIdH.get();
            if (imageId == null) {
                throw new RunnerException("Invalid response from Docker API, can't get ID of newly created image");
            }
            LOG.debug("Create new image {}, id {}", dockerImageName, imageId);
            dockerImages.put(dockerImageName, imageStats = new ImageStats(imageId));
        } else {
            LOG.debug("Image {} exists, id {}", dockerImageName, imageStats.image);
        }
        return imageStats;
    }

    public static class DockerRunnerConfiguration extends RunnerConfiguration {
        public DockerRunnerConfiguration(int memory, RunRequest request) {
            super(memory, request);
        }
    }

    private static class ImageStats {
        final String image;
        int  count;
        long unusedSince;

        ImageStats(String image) {
            this.image = image;
        }

        synchronized int inc() {
            unusedSince = 0;
            return ++count;
        }

        synchronized int dec() {
            if (--count == 0) {
                unusedSince = System.currentTimeMillis();
            }
            return count;
        }

        synchronized long idle() {
            return unusedSince == 0 ? 0 : System.currentTimeMillis() - unusedSince;
        }
    }

    private static class DockerProcess extends ApplicationProcess {
        final DockerConnector connector;
        final ContainerConfig containerCfg;
        final HostConfig      hostCfg;
        final Callback        callback;
        final AtomicBoolean   started;
        String       container;
        DockerLogger logger;

        DockerProcess(DockerConnector connector, ContainerConfig containerCfg, HostConfig hostCfg, Callback callback) {
            this.connector = connector;
            this.containerCfg = containerCfg;
            this.hostCfg = hostCfg;
            this.callback = callback;
            started = new AtomicBoolean(false);
        }

        @Override
        public void start() throws RunnerException {
            if (started.compareAndSet(false, true)) {
                try {
                    final ContainerCreated response = connector.createContainer(containerCfg);
                    connector.startContainer(response.getId(), hostCfg);
                    container = response.getId();
                    logger = new DockerLogger(connector, container);
                    if (callback != null) {
                        callback.started();
                    }
                } catch (IOException e) {
                    throw new RunnerException(e);
                }
            } else {
                throw new IllegalStateException("Process is already started");
            }
        }

        @Override
        public void stop() throws RunnerException {
            if (started.get()) {
                try {
                    connector.stopContainer(container, 3, TimeUnit.SECONDS);
                    if (callback != null) {
                        callback.stopped();
                    }
                } catch (IOException e) {
                    throw new RunnerException(e);
                }
            } else {
                throw new IllegalStateException("Process is not started yet");
            }
        }

        @Override
        public int waitFor() throws RunnerException {
            if (started.get()) {
                try {
                    return connector.waitContainer(container).getStatusCode();
                } catch (IOException e) {
                    throw new RunnerException(e);
                }
            }
            throw new IllegalStateException("Process is not started yet");
        }

        @Override
        public int exitCode() throws RunnerException {
            if (started.get()) {
                try {
                    return connector.inspectContainer(container).getState().getExitCode();
                } catch (IOException e) {
                    throw new RunnerException(e);
                }
            }
            return -1;
        }

        @Override
        public boolean isRunning() throws RunnerException {
            if (started.get()) {
                try {
                    return connector.inspectContainer(container).getState().isRunning();
                } catch (ConnectException e) {
                    // If connection to docker daemon is lost.
                    LOG.error(e.getMessage(), e);
                } catch (IOException e) {
                    throw new RunnerException(e);
                }
            }
            return false;
        }

        @Override
        public ApplicationLogger getLogger() throws RunnerException {
            if (started.get()) {
                return logger;
            }
            return ApplicationLogger.DUMMY;
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
