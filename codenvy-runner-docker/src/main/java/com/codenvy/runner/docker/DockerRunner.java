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

import com.codenvy.api.runner.RunnerException;
import com.codenvy.api.runner.internal.ApplicationLogger;
import com.codenvy.api.runner.internal.ApplicationProcess;
import com.codenvy.api.runner.internal.Constants;
import com.codenvy.api.runner.internal.DeploymentSources;
import com.codenvy.api.runner.internal.Disposer;
import com.codenvy.api.runner.internal.Runner;
import com.codenvy.api.runner.internal.RunnerConfiguration;
import com.codenvy.api.runner.internal.RunnerConfigurationFactory;
import com.codenvy.api.runner.internal.dto.RunRequest;
import com.codenvy.runner.docker.json.ContainerConfig;
import com.codenvy.runner.docker.json.ContainerCreated;
import com.codenvy.runner.docker.json.HostConfig;
import com.codenvy.runner.docker.json.Image;
import com.google.common.hash.Hashing;
import com.google.common.io.CharStreams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/** @author <a href="mailto:aparfonov@codenvy.com">Andrey Parfonov</a> */
public class DockerRunner extends Runner {
    private static final Logger LOG = LoggerFactory.getLogger(DockerRunner.class);

    private final Map<String, java.io.File> dockerFileTemplates;
    private final Map<String, ImageUsage>   dockerImageUsage;

    DockerRunner(Map<String, java.io.File> dockerFileTemplates) {
        this.dockerFileTemplates = new HashMap<>(dockerFileTemplates);
        dockerImageUsage = new HashMap<>();
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
    public RunnerConfigurationFactory getRunnerConfigurationFactory() {
        return new RunnerConfigurationFactory() {
            @Override
            public RunnerConfiguration createRunnerConfiguration(RunRequest request) throws RunnerException {
                return new DockerRunnerConfiguration(request.getMemorySize(), 0, request);
            }
        };
    }

    @Override
    protected ApplicationProcess newApplicationProcess(DeploymentSources toDeploy,
                                                       RunnerConfiguration runnerCfg) throws RunnerException {
        try {
            final java.io.File applicationFile = toDeploy.getFile();
            final java.io.File dockerFile = new java.io.File(applicationFile.getParentFile(), "Dockerfile");
            // It always should be DockerRunnerConfiguration.
            final DockerRunnerConfiguration dockerRunnerCfg = (DockerRunnerConfiguration)runnerCfg;
            getDockerfileBuilder(dockerRunnerCfg)
                    .setParameters(runnerCfg.getOptions())
                    .setParameter("app", applicationFile.getName())
                    .writeDockerfile(dockerFile);
            final String dockerRepoName = runnerCfg.getRequest().getWorkspace() + '/' + runnerCfg.getRequest().getProject();
            final String fileHash = com.google.common.io.Files.hash(applicationFile, Hashing.sha1()).toString();
            final DockerConnector connector = DockerConnector.getInstance();
            final ImageUsage imageUsage = createImageIfNeed(connector, dockerRepoName, fileHash, dockerFile, applicationFile);
            final ContainerConfig dockerCfg =
                    new ContainerConfig().withImage(imageUsage.image).withMemory(runnerCfg.getMemory() * 1024 * 1024).withCpuShares(1);
            final DockerProcess docker = new DockerProcess(connector, dockerCfg, null); // TODO: host config
            imageUsage.inc();
            registerDisposer(docker, new Disposer() {
                @Override
                public void dispose() {
                    try {
                        connector.removeContainer(docker.container, true);
                        LOG.info("Remove docker container: {}", docker.container); // TODO: debug
                    } catch (IOException e) {
                        LOG.error(e.getMessage(), e);
                    }
                    try {
                        maybeDeleteImage(connector, imageUsage);
                    } catch (IOException e) {
                        LOG.error(e.getMessage(), e);
                    }
                }
            });
            return docker;
        } catch (IOException e) {
            throw new RunnerException(e);
        }
    }

    private synchronized ImageUsage createImageIfNeed(DockerConnector connector,
                                                      String dockerRepoName,
                                                      String tag,
                                                      java.io.File dockerFile,
                                                      java.io.File applicationFile) throws IOException, RunnerException {
        boolean exists = false;
        for (Image image : connector.listImages()) {
            if (dockerRepoName.equals(image.getRepository()) && tag.equals(image.getTag())) {
                exists = true;
                break;
            }
        }
        final String dockerImageName = dockerRepoName + ':' + tag;
        if (exists) {
            LOG.info("Image {} exists", dockerImageName); // TODO: debug
        } else {
            LOG.info("Create new image {}", dockerImageName); // TODO: debug
            final StringBuilder output = new StringBuilder();
            connector.createImage(dockerFile, applicationFile, dockerImageName, output);
            final String buildLog = output.toString();
            LOG.debug(buildLog);
            for (String line : CharStreams.readLines(new StringReader(buildLog))) {
                if (line.startsWith("Error build:")) {
                    throw new RunnerException(String.format("Error building Docker container, response from Docker API:\n%s\n", buildLog));
                }
            }
        }
        ImageUsage imageUsage = dockerImageUsage.get(dockerImageName);
        if (imageUsage == null) {
            dockerImageUsage.put(dockerImageName, imageUsage = new ImageUsage(dockerImageName));
        }
        return imageUsage;
    }

    private void maybeDeleteImage(DockerConnector connector, ImageUsage imageUsage) throws IOException {
        if (imageUsage.dec() == 0) {
            connector.removeImage(imageUsage.image);
            LOG.info("Remove docker image: {}", imageUsage.image); // TODO: debug
        }
    }

    protected DockerfileBuilder getDockerfileBuilder(DockerRunnerConfiguration configuration) throws RunnerException {
        final String dockerfileName = configuration.getDockerfileName();
        final java.io.File template = dockerFileTemplates.get(dockerfileName);
        if (template == null) {
            throw new RunnerException(String.format("Dockerfile %s not found", dockerfileName));
        }
        return DockerfileBuilder.of(template);
    }

    public static class DockerRunnerConfiguration extends RunnerConfiguration {
        public DockerRunnerConfiguration(int memory, int debugPort, RunRequest request) {
            super(memory, debugPort, request);
        }

        public String getDockerfileName() {
            return getOptions().get("docker_file");
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
        String       container;
        DockerLogger logger;

        DockerProcess(DockerConnector connector, ContainerConfig containerCfg, HostConfig hostCfg) {
            this.connector = connector;
            this.containerCfg = containerCfg;
            this.hostCfg = hostCfg;
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
        }

        @Override
        public synchronized void stop() throws RunnerException {
            if (container == null) {
                throw new IllegalStateException("Process is not started yet");
            }
            try {
                connector.stopContainer(container, 5, TimeUnit.SECONDS);
            } catch (IOException e) {
                throw new RunnerException(e);
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
