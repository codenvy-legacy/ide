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
package com.codenvy.runner.sdk;

import com.codenvy.api.core.util.CustomPortService;
import com.codenvy.api.core.util.ProcessUtil;
import com.codenvy.api.runner.RunnerException;
import com.codenvy.api.runner.internal.*;
import com.codenvy.api.runner.internal.dto.RunRequest;
import com.codenvy.api.vfs.server.VirtualFile;

import org.apache.maven.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import static com.codenvy.ide.commons.FileUtils.createTempDirectory;
import static com.codenvy.runner.sdk.Utils.readPom;

/**
 * Runner implementation to testing Codenvy plug-ins by launching
 * a separate Codenvy web-application in Tomcat container.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 */
public class SDKRunner extends Runner {
    private static final Logger LOG = LoggerFactory.getLogger(SDKRunner.class);

    @Override
    public String getName() {
        return "sdk";
    }

    @Override
    public String getDescription() {
        return "Codenvy plug-ins runtime";
    }

    @Override
    public RunnerConfigurationFactory getRunnerConfigurationFactory() {
        return new RunnerConfigurationFactory() {
            @Override
            public RunnerConfiguration createRunnerConfiguration(RunRequest request) throws RunnerException {
                final int mem;
                final String memStr = request.getOptions().get(Constants.MEMORY);
                if (memStr != null) {
                    mem = Integer.parseInt(memStr);
                } else {
                    mem = DEFAULT_MEMORY_SIZE;
                }
                return new SDKRunnerConfiguration(portService.acquire(), mem, 0, request);
            }
        };
    }

    @Override
    protected ApplicationProcess newApplicationProcess(DeploymentSources toDeploy,
                                                       RunnerConfiguration runnerCfg) throws RunnerException {
        final java.io.File applicationFile = toDeploy.getFile();
        // It always should be SDKRunnerConfiguration.
        final SDKRunnerConfiguration sdkRunnerCfg = (SDKRunnerConfiguration)runnerCfg;

//        buildWar();

//            getDockerfileBuilder(sdkRunnerCfg).setParameters(runnerCfg.getOptions()).setParameter("app", applicationFile.getName());
//            final String dockerRepoName = runnerCfg.getRequest().getWorkspace() + '/' + runnerCfg.getRequest().getProject();
//            final String fileHash = com.google.common.io.Files.hash(applicationFile, Hashing.sha1()).toString();
        final TomcatProcess tomcatProcess = new TomcatProcess(sdkRunnerCfg.port, portService);
//        registerDisposer(tomcatProcess, new Disposer() {
//            @Override
//            public void dispose() {
//            }
//        });
        return tomcatProcess;
    }

//    private static void buildWar() throws RunnerException {
//        try {
//            File tempDir = createTempDirectory("sdk-war-");
//            final Path buildDirPath = createTempDirectory(tempDir, "build-").toPath();
//            final Path clientModuleDirPath = buildDirPath.resolve("codenvy-ide-client");
//            final Path clientModulePomPath = clientModuleDirPath.resolve("pom.xml");
//
//            VirtualFile pomFile = vfsMountPoint.getVirtualFile(project.getName() + "/pom.xml");
//            InputStream extPomContent = pomFile.getContent().getStream();
//            Model extensionPom = readPom(extPomContent);
//
//            if (extensionPom.getGroupId() == null || extensionPom.getArtifactId() == null ||
//                extensionPom.getVersion() == null) {
//                throw new RunnerException("Missing Maven artifact coordinates.");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public static class SDKRunnerConfiguration extends RunnerConfiguration {
        private final int port;

        public SDKRunnerConfiguration(int port, int memory, int debugPort, RunRequest request) {
            super(memory, debugPort, request);
            this.port = port;
        }
    }

    private static class TomcatProcess extends ApplicationProcess {
        Process   process;
        SDKLogger logger;
        int       port;
        CustomPortService portService;

        TomcatProcess(int port, CustomPortService portService) {
            this.port = port;
            this.portService = portService;
        }

        @Override
        public synchronized void start() throws RunnerException {
//            if (container != null) {
//                throw new IllegalStateException("Process is already started.");
//            }
            logger = new SDKLogger();
        }

        @Override
        public synchronized void stop() throws RunnerException {
            if (!ProcessUtil.isAlive(process)) {
//            if (container == null) {
                throw new IllegalStateException("Process is not started yet.");
            }
            ProcessUtil.kill(process);
            portService.release(port);
        }

        @Override
        public int waitFor() throws RunnerException {
            return 0;
        }

        @Override
        public synchronized int exitCode() throws RunnerException {
            //if (container == null) {
            return -1;
//            }
//            try {
//                return connector.inspectContainer(container).getState().getExitCode();
//            } catch (IOException e) {
//                throw new RunnerException(e.getMessage(), e);
//            }
        }

        @Override
        public synchronized boolean isRunning() throws RunnerException {
            return ProcessUtil.isAlive(process);
        }

        @Override
        public synchronized ApplicationLogger getLogger() throws RunnerException {
            if (logger == null) {
                // is not started yet
                return ApplicationLogger.DUMMY;
            }
            return logger;
        }

        private static class SDKLogger implements ApplicationLogger {

            SDKLogger() {
            }

            @Override
            public void getLogs(Appendable output) throws IOException {
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
