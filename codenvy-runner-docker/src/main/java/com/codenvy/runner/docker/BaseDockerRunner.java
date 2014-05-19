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
import com.codenvy.api.runner.RunnerException;
import com.codenvy.api.runner.dto.DebugMode;
import com.codenvy.api.runner.dto.RunRequest;
import com.codenvy.api.runner.internal.ApplicationLogger;
import com.codenvy.api.runner.internal.ApplicationLogsPublisher;
import com.codenvy.api.runner.internal.ApplicationProcess;
import com.codenvy.api.runner.internal.Constants;
import com.codenvy.api.runner.internal.DeploymentSources;
import com.codenvy.api.runner.internal.Disposer;
import com.codenvy.api.runner.internal.ResourceAllocators;
import com.codenvy.api.runner.internal.Runner;
import com.codenvy.api.runner.internal.RunnerConfiguration;
import com.codenvy.api.runner.internal.RunnerConfigurationFactory;
import com.codenvy.commons.json.JsonHelper;
import com.codenvy.commons.json.JsonParseException;
import com.codenvy.commons.lang.NamedThreadFactory;
import com.codenvy.commons.lang.ZipUtils;
import com.codenvy.dto.server.DtoFactory;
import com.codenvy.runner.docker.json.BuildImageStatus;
import com.codenvy.runner.docker.json.ContainerConfig;
import com.codenvy.runner.docker.json.ContainerCreated;
import com.codenvy.runner.docker.json.HostConfig;
import com.codenvy.runner.docker.json.Image;
import com.codenvy.runner.docker.json.PortBinding;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/** @author andrew00x */
public abstract class BaseDockerRunner extends Runner {
    static final Logger LOG = LoggerFactory.getLogger(BaseDockerRunner.class);

    public static final String HOST_NAME = "runner.docker.host_name";

    private final ConcurrentMap<String, Future<ImageStats>> buildImageTasks;
    private final Map<String, ImageStats>                   dockerImages;
    private final String                                    hostName;
    private final CustomPortService                         portService;
    private final long                                      maxImageIdleTime;

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
        this.dockerImages = new ConcurrentHashMap<>();
        // TODO: configurable
        this.maxImageIdleTime = TimeUnit.MINUTES.toMillis(15);
        buildImageTasks = new ConcurrentHashMap<>();
    }

    @PostConstruct
    @Override
    public final void start() {
        super.start();
        imageCleaner = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory(getName() + "-ImageCleaner-", true));
        imageCleaner.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                final DockerConnector connector = DockerConnector.getInstance();
                for (Iterator<Map.Entry<String, ImageStats>> itr = dockerImages.entrySet().iterator(); itr.hasNext(); ) {
                    if (Thread.currentThread().isInterrupted()) {
                        return;
                    }
                    final Map.Entry<String, ImageStats> e = itr.next();
                    final ImageStats stats = e.getValue();
                    if (stats.idle() > maxImageIdleTime) {
                        try {
                            connector.removeImage(stats.image);
                            LOG.debug("Remove docker image: {}", stats.image);
                            itr.remove();
                        } catch (IOException err) {
                            LOG.error(String.format("Failed remove docker image %s", stats.image), err);
                        }
                    }
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
        boolean interrupted = false;
        imageCleaner.shutdownNow();
        try {
            if (!imageCleaner.awaitTermination(15, TimeUnit.SECONDS)) {
                LOG.warn("Unable terminate image cleaner");
            }
        } catch (InterruptedException e) {
            interrupted = true;
        }
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
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
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
            // It always should be DockerRunnerConfiguration.
            final DockerRunnerConfiguration dockerRunnerCfg = (DockerRunnerConfiguration)runnerCfg;
            final RunRequest request = dockerRunnerCfg.getRequest();
            final DockerEnvironment dockerEnvironment = getDockerEnvironment(request);
            final Dockerfile dockerfile = getDockerfile(dockerEnvironment, request);
            if (dockerfile == null) {
                throw new RunnerException("Unable create environment for starting application. Acceptable dockerfile isn't found.");
            }
            // Apply parameters and write Dockerfile based on it's template.
            java.io.File applicationFile = toDeploy.getFile();
            String applicationFileName = applicationFile.getName();
            final java.io.File workDir = applicationFile.getParentFile();
            final List<DockerImage> images = dockerfile.getImages();
            if (images.isEmpty()) {
                throw new RunnerException(
                        "Unable create environment for starting application. Dockerfile exists but doesn't contains any images.");
            }
            // First image in the list is image for application.
            final DockerImage applicationImage = images.get(0);
            // check do we need unpack application file.
            // 1. request binding application directory to docker container instead of adding file inside container with 'ADD' instruction
            final String containerBindDir = dockerEnvironment != null ? dockerEnvironment.getBindAppDir() : null;
            boolean needUnpack = containerBindDir != null;
            if (!needUnpack) {
                // 2. request to add single file from application or if destination path ands with '/'
                // examples:
                // a. ADD $app$/some_file /opt/application
                // b. ADD $app$ /opt/application/
                for (Pair<String, String> add : applicationImage.getAdd()) {
                    if (add.first.contains("$app$/") || (add.first.equals("$app$") && add.second.endsWith("/"))) {
                        needUnpack = true;
                        break;
                    }
                }
            }

            if (needUnpack) {
                java.io.File applicationFileUnpack = new java.io.File(workDir, applicationFileName + "_unpack");
                ZipUtils.unzip(applicationFile, applicationFileUnpack);
                applicationFile = applicationFileUnpack;
                applicationFileName = applicationFileUnpack.getName();
            }
            dockerfile.getParameters().put("app", applicationFileName);
            dockerfile.getParameters().putAll(runnerCfg.getOptions());
            final java.io.File dockerfileIoFile = new java.io.File(workDir, "Dockerfile");
            dockerfile.writeDockerfile(dockerfileIoFile);

            final List<Pair<Integer, Integer>> portMapping = new LinkedList<>();
            int privateWebPort = -1;
            if (dockerEnvironment != null) {
                privateWebPort = dockerEnvironment.getWebPort();
            }
            if (privateWebPort <= 0) {
                // Use port that is set in EXPOSE instruction, if any. EXPOSE instruction may contains multiple container ports,
                // but Codenvy uses only the first one to connect the Docker container and route requests from the Internet.
                if (!images.isEmpty()) {
                    final List<String> expose = applicationImage.getExpose();
                    if (!expose.isEmpty()) {
                        try {
                            privateWebPort = Integer.parseInt(expose.get(0));
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }
            if (privateWebPort > 0) {
                final int publicWebPort = portService.acquire();
                portMapping.add(Pair.of(publicWebPort, privateWebPort));
                // Web link for application.
                // TODO: need something more flexible to avoid showing port in URL.
                final String webUrl = String.format("http://%s:%d", hostName, publicWebPort);
                dockerRunnerCfg.getLinks().add(DtoFactory.getInstance().createDto(Link.class)
                                                         .withRel(Constants.LINK_REL_WEB_URL)
                                                         .withHref(webUrl));
            }
            final DebugMode debugMode = dockerRunnerCfg.getRequest().getDebugMode();
            if (debugMode != null) {
                if (dockerEnvironment != null) {
                    int privateDebugPort = dockerEnvironment.getDebugPort();
                    if (privateDebugPort > 0) {
                        final int publicDebugPort = portService.acquire();
                        portMapping.add(Pair.of(publicDebugPort, privateDebugPort));
                        dockerRunnerCfg.setDebugHost(hostName);
                        dockerRunnerCfg.setDebugPort(publicDebugPort);
                        dockerRunnerCfg.setDebugSuspend("suspend".equals(debugMode.getMode()));
                    }
                }
            }
            final String workspace = runnerCfg.getRequest().getWorkspace();
            final String project = runnerCfg.getRequest().getProject();
            final String dockerRepoName = workspace + (project.startsWith("/") ? project : ('/' + runnerCfg.getRequest().getProject()));
            // count hash-sum from original file
            @SuppressWarnings("unchecked")
            final String hash = ByteStreams.hash(ByteStreams.join(Files.newInputStreamSupplier(toDeploy.getFile()),
                                                                  Files.newInputStreamSupplier(dockerfileIoFile)),
                                                 Hashing.sha1()
                                                ).toString();
            final DockerConnector connector = DockerConnector.getInstance();
            final ApplicationLogsPublisher logsPublisher = new ApplicationLogsPublisher(ApplicationLogger.DUMMY,
                                                                                        getEventService(),
                                                                                        request.getId(),
                                                                                        workspace,
                                                                                        project);
            final ImageStats imageStats = createImageIfNeed(connector, dockerRepoName, hash, dockerfileIoFile, applicationFile,
                                                            logsPublisher);
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
            if (containerBindDir != null) {
                if (hostConfig == null) {
                    hostConfig = new HostConfig();
                }
                hostConfig.setBinds(new String[]{String.format("%s:%s", applicationFile.getAbsolutePath(), containerBindDir)});
            }
            final DockerProcess docker = new DockerProcess(connector, containerConfig, hostConfig, logsPublisher,
                                                           new ApplicationProcess.Callback() {
                                                               @Override
                                                               public void started() {
                                                               }

                                                               @Override
                                                               public void stopped() {
                                                                   for (Pair<Integer, Integer> p : portMapping) {
                                                                       portService.release(p.first);
                                                                   }
                                                               }
                                                           }
            );
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

    /**
     * Get description of additional properties of docker environment. This method might return {@code null} is specified environment is
     * not configured for the project.
     *
     * @see DockerEnvironment
     */
    protected abstract DockerEnvironment getDockerEnvironment(RunRequest request) throws IOException, RunnerException;

    protected abstract Dockerfile getDockerfile(DockerEnvironment dockerEnvironment, RunRequest request)
            throws IOException, RunnerException;

    private ImageStats createImageIfNeed(final DockerConnector connector,
                                         final String dockerRepoName,
                                         final String tag,
                                         final java.io.File dockerFile,
                                         final java.io.File applicationFile,
                                         final ApplicationLogsPublisher logsPublisher) throws IOException, RunnerException {
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
        if (imageStats == null) {
            Future<ImageStats> future = buildImageTasks.get(dockerImageName);
            if (future == null) {
                final Callable<ImageStats> c = new Callable<ImageStats>() {
                    public ImageStats call() throws IOException, RunnerException {
                        final long startTime = System.currentTimeMillis();
                        final CreateImageLogger output = new CreateImageLogger(logsPublisher);
                        connector.createImage(dockerFile, applicationFile, dockerRepoName, tag, output);
                        final String error = output.getError();
                        if (error != null) {
                            throw new RunnerException(error);
                        }
                        final String imageId = output.getImageId();
                        if (imageId == null) {
                            throw new RunnerException("Invalid response from Docker API, can't get ID of newly created image");
                        }
                        final long endTime = System.currentTimeMillis();
                        LOG.debug("Create new image {}, id {} in {} ms", dockerImageName, imageId, (endTime - startTime));
                        ImageStats _imageStats;
                        dockerImages.put(dockerImageName, _imageStats = new ImageStats(imageId));
                        return _imageStats;
                    }
                };
                FutureTask<ImageStats> newFuture = new FutureTask<>(c);
                future = buildImageTasks.putIfAbsent(dockerImageName, newFuture);
                if (future == null) {
                    future = newFuture;
                    newFuture.run();
                }
            }
            try {
                return future.get();
            } catch (InterruptedException e) {
                Thread.interrupted();
                throw new RunnerException("Interrupted while waiting for creation of docker image. ");
            } catch (ExecutionException e) {
                final Throwable cause = e.getCause();
                if (cause instanceof Error) {
                    throw (Error)cause;
                } else if (cause instanceof RuntimeException) {
                    throw (RuntimeException)cause;
                } else if (cause instanceof IOException) {
                    throw (IOException)cause;
                } else if (cause instanceof RunnerException) {
                    throw (RunnerException)cause;
                } else {
                    throw new RunnerException(e);
                }
            } catch (CancellationException e) {
                throw new RunnerException("Cancelled while waiting for creation of docker image. ");
            } finally {
                buildImageTasks.remove(dockerImageName, future);
            }
        } else {
            LOG.debug("Image {} exists, id {}", dockerImageName, imageStats.image);
            return imageStats;
        }
    }

    private static class CreateImageLogger implements LineConsumer {
        final ApplicationLogsPublisher logsPublisher;

        String imageId;
        String error;

        CreateImageLogger(ApplicationLogsPublisher logsPublisher) {
            this.logsPublisher = logsPublisher;
        }

        String getImageId() {
            return imageId;
        }

        String getError() {
            return error;
        }

        @Override
        public void writeLine(String line) throws IOException {
            if (!(line == null || line.isEmpty())) {
                LOG.debug(line);
                try {
                    final BuildImageStatus status = JsonHelper.fromJson(line, BuildImageStatus.class, null);
                    final String stream = status.getStream();
                    final String error = status.getError();
                    if (error != null || stream != null) {
                        logsPublisher.writeLine(error != null ? String.format("ERROR: %s", error) : stream);
                    }
                    if (stream != null && stream.startsWith("Successfully built ")) {
                        int endSize = 19;
                        while (endSize < stream.length() && Character.digit(stream.charAt(endSize), 16) != -1) {
                            endSize++;
                        }
                        imageId = stream.substring(19, endSize);
                    } else if (error != null && this.error == null) {
                        this.error = error;
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

    private class DockerProcess extends ApplicationProcess {
        final DockerConnector          connector;
        final ContainerConfig          containerCfg;
        final HostConfig               hostCfg;
        final ApplicationLogsPublisher logsPublisher;
        final Callback                 callback;
        final AtomicBoolean            started;
        String       container;
        DockerLogger logger;

        DockerProcess(DockerConnector connector,
                      ContainerConfig containerCfg,
                      HostConfig hostCfg,
                      ApplicationLogsPublisher logsPublisher,
                      Callback callback) {
            this.connector = connector;
            this.containerCfg = containerCfg;
            this.hostCfg = hostCfg;
            this.logsPublisher = logsPublisher;
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
                    getExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                LOG.debug("Attach to container {}", container);
                                connector.attachContainer(container, logsPublisher, true);
                                LOG.debug("Detach from container {}", container);
                            } catch (Exception e) {
                                LOG.error(e.getMessage(), e);
                            }
                        }
                    });
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

        private class DockerLogger implements ApplicationLogger {
            final DockerConnector connector;
            final String          container;

            DockerLogger(DockerConnector connector, String container) {
                this.connector = connector;
                this.container = container;
            }

            @Override
            public void getLogs(final Appendable output) throws IOException {
                connector.attachContainer(container, new LineConsumer() {
                    @Override
                    public void writeLine(String s) throws IOException {
                        output.append(s);
                    }

                    @Override
                    public void close() throws IOException {
                        // noop
                    }
                }, false);
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
