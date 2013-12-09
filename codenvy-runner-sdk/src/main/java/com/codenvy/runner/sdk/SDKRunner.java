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

import com.codenvy.api.core.util.CommandLine;
import com.codenvy.api.core.util.CustomPortService;
import com.codenvy.api.core.util.LineConsumer;
import com.codenvy.api.core.util.ProcessUtil;
import com.codenvy.api.core.util.SystemInfo;
import com.codenvy.api.runner.RunnerException;
import com.codenvy.api.runner.internal.ApplicationLogger;
import com.codenvy.api.runner.internal.ApplicationProcess;
import com.codenvy.api.runner.internal.DeploymentSources;
import com.codenvy.api.runner.internal.Disposer;
import com.codenvy.api.runner.internal.Runner;
import com.codenvy.api.runner.internal.RunnerConfiguration;
import com.codenvy.api.runner.internal.RunnerConfigurationFactory;
import com.codenvy.api.runner.internal.dto.RunRequest;
import com.codenvy.commons.lang.IoUtil;
import com.codenvy.commons.lang.NamedThreadFactory;
import com.codenvy.ide.commons.ZipUtils;
import com.google.common.io.CharStreams;

import org.apache.maven.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Runner implementation to test Codenvy plug-ins by launching
 * a separate Codenvy web-application in Tomcat server.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 */
public class SDKRunner extends Runner {
    public static final  int    DEFAULT_MEM_SIZE          = 256;
    public static final  String DEBUG_TRANSPORT_PROTOCOL  = "dt_socket";
    private static final Logger LOG                       = LoggerFactory.getLogger(SDKRunner.class);
    /** String in JSON format to register builder service. */
    private static final String BUILDER_REGISTRATION_JSON =
            "[{\"builderServiceLocation\":{\"url\":\"http://localhost:${PORT}/api/internal/builder\"}}]";
    private static final String RUNNER_REGISTRATION_JSON  =
            "[{\"runnerServiceLocation\":{\"url\":\"http://localhost:${PORT}/api/internal/runner\"}}]";
    private static final String SERVER_XML                =
            "<?xml version='1.0' encoding='utf-8'?>\n" +
            "<Server port=\"-1\">\n" +
            "  <Listener className=\"org.apache.catalina.core.AprLifecycleListener\" SSLEngine=\"on\" />\n" +
            "  <Listener className=\"org.apache.catalina.core.JasperListener\" />\n" +
            "  <Listener className=\"org.apache.catalina.core.JreMemoryLeakPreventionListener\" />\n" +
            "  <Listener className=\"org.apache.catalina.mbeans.GlobalResourcesLifecycleListener\" />\n" +
            "  <Listener className=\"org.apache.catalina.core.ThreadLocalLeakPreventionListener\" />\n" +
            "  <Service name=\"Catalina\">\n" +
            "    <Connector port=\"${PORT}\" protocol=\"HTTP/1.1\"\n" +
            "               connectionTimeout=\"20000\" />\n" +
            "    <Engine name=\"Catalina\" defaultHost=\"localhost\">\n" +
            "      <Host name=\"localhost\"  appBase=\"webapps\"\n" +
            "            unpackWARs=\"true\" autoDeploy=\"true\">\n" +
            "      </Host>\n" +
            "    </Engine>\n" +
            "  </Service>\n" +
            "</Server>\n";

    @Override
    public String getName() {
        return "sdk";
    }

    @Override
    public String getDescription() {
        return "Codenvy plug-ins runner";
    }

    @Override
    public RunnerConfigurationFactory getRunnerConfigurationFactory() {
        return new RunnerConfigurationFactory() {
            @Override
            public RunnerConfiguration createRunnerConfiguration(RunRequest request) throws RunnerException {
                return new SDKRunnerConfiguration(CustomPortService.getInstance().acquire(),
                                                  request.getMemorySize(), -1, false,
                                                  DEBUG_TRANSPORT_PROTOCOL,
                                                  request);
            }
        };
    }

    @Override
    protected ApplicationProcess newApplicationProcess(DeploymentSources toDeploy,
                                                       RunnerConfiguration configuration) throws RunnerException {
        // It always should be SDKRunnerConfiguration.
        final SDKRunnerConfiguration runnerCfg = (SDKRunnerConfiguration)configuration;

        final java.io.File appDir;
        try {
            appDir = Files.createTempDirectory(getDeployDirectory().toPath(), ("app_" + getName() + '_')).toFile();

            final Path tomcatPath = Files.createDirectory(appDir.toPath().resolve("tomcat"));
            ZipUtils.unzip(Utils.getTomcatBinaryDistribution().openStream(), tomcatPath.toFile());

            final Path webappsPath = tomcatPath.resolve("webapps");
            final java.io.File warFile = buildCodenvyWebApp(toDeploy.getFile()).toFile();
            ZipUtils.unzip(warFile, webappsPath.resolve("ide").toFile());

            configureApiServices(webappsPath, runnerCfg);
            setEnvVariables(tomcatPath, runnerCfg);
            generateServerXml(tomcatPath.toFile(), runnerCfg);
        } catch (IOException e) {
            throw new RunnerException(e);
        }

        final ApplicationProcess applicationProcess;
        if (SystemInfo.isUnix()) {
            applicationProcess = startUnix(appDir, runnerCfg);
        } else {
            applicationProcess = startWindows(appDir, runnerCfg);
        }

        registerDisposer(applicationProcess, new Disposer() {
            @Override
            public void dispose() {
                if (!IoUtil.deleteRecursive(appDir)) {
                    LOG.error("Unable to remove app: {}", appDir);
                }
            }
        });

        return applicationProcess;
    }

    private Path buildCodenvyWebApp(java.io.File jarFile) throws RunnerException {
        Path warPath;
        try {
            // prepare Codenvy Platform sources
            final Path appDirPath =
                    Files.createTempDirectory(getDeployDirectory().toPath(), ("war_" + getName() + '_'));
            ZipUtils.unzip(Utils.getCodenvyPlatformBinaryDistribution().openStream(), appDirPath.toFile());

            // add extension to Codenvy Platform
            // TODO avoid unzipping jar-file, use java.util.zip.*
            final Path jarUnzipped =
                    Files.createTempDirectory(getDeployDirectory().toPath(), ("jar_" + getName() + '_'));
            ZipUtils.unzip(jarFile, jarUnzipped.toFile());
            final Path pomXmlExt = Utils.findFile("pom.xml", jarUnzipped);
            Model pomExt = Utils.readPom(pomXmlExt);

            Utils.addDependencyToPom(appDirPath.resolve("pom.xml"), pomExt);
            final Path mainGwtModuleDescriptor = Utils.findFile("*.gwt.xml", appDirPath);
            Utils.inheritGwtModule(mainGwtModuleDescriptor, Utils.detectGwtModuleLogicalName(jarUnzipped));

            // build WAR by invoking Maven directly
            warPath = buildWar(appDirPath);
        } catch (IOException e) {
            throw new RunnerException(e);
        }
        return warPath;
    }

    private Path buildWar(Path appDirPath) throws RunnerException {
        final String[] command = new String[]{Utils.getMavenExecCommand(), "package"};

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command).directory(appDirPath.toFile());
            Process process = processBuilder.start();
            ProcessLineConsumer consumer = new ProcessLineConsumer();
            ProcessUtil.process(process, consumer, consumer);
            process.waitFor();
            if (process.exitValue() != 0) {
                throw new RunnerException(consumer.getOutput().toString());
            }
            return Utils.findFile("*.war", appDirPath.resolve("target"));
        } catch (IOException | InterruptedException e) {
            throw new RunnerException(e);
        }
    }

    private void configureApiServices(Path webappsPath, SDKRunnerConfiguration runnerCfg)
            throws RunnerException, IOException {
        final Path apiAppPath = webappsPath.resolve("api");
        ZipUtils.unzip(webappsPath.resolve("api.war").toFile(), apiAppPath.toFile());

        final String builderServiceCfg =
                BUILDER_REGISTRATION_JSON.replace("${PORT}", Integer.toString(runnerCfg.getPort()));
        final Path builderRegistrationJsonPath =
                apiAppPath.resolve("WEB-INF/classes/conf/builder_service_registrations.json");

        final String runnerServiceCfg =
                RUNNER_REGISTRATION_JSON.replace("${PORT}", Integer.toString(runnerCfg.getPort()));
        final Path runnerRegistrationJsonPath =
                apiAppPath.resolve("WEB-INF/classes/conf/runner_service_registrations.json");
        try {
            Files.write(builderRegistrationJsonPath, builderServiceCfg.getBytes());
            Files.write(runnerRegistrationJsonPath, runnerServiceCfg.getBytes());
        } catch (IOException e) {
            throw new RunnerException(e);
        }
    }

    private void setEnvVariables(Path tomcatPath, SDKRunnerConfiguration runnerCfg) throws IOException {
        final Path setenvShPath = tomcatPath.resolve("bin/setenv.sh");
        final byte[] bytes = Files.readAllBytes(setenvShPath);
        final String setenvShContent = new String(bytes);
        Files.write(setenvShPath, setenvShContent.replace("${PORT}", Integer.toString(runnerCfg.getPort())).getBytes());
    }

    private void generateServerXml(java.io.File tomcatDir, SDKRunnerConfiguration runnerConfiguration)
            throws RunnerException {
        String cfg = SERVER_XML.replace("${PORT}", Integer.toString(runnerConfiguration.getPort()));
        final java.io.File serverXmlFile = new java.io.File(new java.io.File(tomcatDir, "conf"), "server.xml");
        try {
            Files.write(serverXmlFile.toPath(), cfg.getBytes());
        } catch (IOException e) {
            throw new RunnerException(e);
        }
    }

    // *nix

    protected ApplicationProcess startUnix(final java.io.File appDir,
                                           final SDKRunnerConfiguration runnerConfiguration)
            throws RunnerException {
        java.io.File startUpScriptFile = genStartUpScriptUnix(appDir, runnerConfiguration);
        if (!startUpScriptFile.setExecutable(true, false)) {
            throw new RunnerException("Unable update attributes of the startup script");
        }

        final java.io.File logsDir = new java.io.File(appDir, "logs");
        if (!logsDir.mkdir()) {
            throw new RunnerException("Unable create logs directory");
        }
        final List<java.io.File> logFiles = new ArrayList<>(2);
        logFiles.add(new java.io.File(logsDir, "stdout.log"));
        logFiles.add(new java.io.File(logsDir, "stderr.log"));

        return new TomcatProcess(runnerConfiguration.getPort(), logFiles, runnerConfiguration.getDebugPort(),
                                 startUpScriptFile, appDir);
    }

    private java.io.File genStartUpScriptUnix(java.io.File appDir, SDKRunnerConfiguration runnerConfiguration)
            throws RunnerException {
        final String startupScript = "#!/bin/sh\n" +
                                     exportEnvVariablesUnix(runnerConfiguration) +
                                     "cd tomcat\n" +
                                     "chmod +x bin/*.sh\n" +
                                     catalinaUnix(runnerConfiguration) +
                                     "PID=$!\n" +
                                     "echo \"$PID\" >> ../run.pid\n" +
                                     "wait $PID";
        final java.io.File startUpScriptFile = new java.io.File(appDir, "startup.sh");
        try {
            Files.write(startUpScriptFile.toPath(), startupScript.getBytes());
        } catch (IOException e) {
            throw new RunnerException(e);
        }
        if (!startUpScriptFile.setExecutable(true, false)) {
            throw new RunnerException("Unable update attributes of the startup script");
        }
        return startUpScriptFile;
    }

    private String exportEnvVariablesUnix(SDKRunnerConfiguration runnerConfiguration) {
        int memory = runnerConfiguration.getMemory();
        if (memory <= 0) {
            memory = DEFAULT_MEM_SIZE;
        }
        final String catalinaOpts = String.format("export CATALINA_OPTS=\"-Xms%dm -Xmx%dm\"%n", memory, memory);
        final int debugPort = runnerConfiguration.getDebugPort();
        if (debugPort <= 0) {
            return catalinaOpts;
        }
        final StringBuilder export = new StringBuilder();
        export.append(catalinaOpts);
        /*
        From catalina.sh:
        -agentlib:jdwp=transport=$JPDA_TRANSPORT,address=$JPDA_ADDRESS,server=y,suspend=$JPDA_SUSPEND
         */
        export.append(String.format("export JPDA_ADDRESS=%d%n", debugPort));
        export.append(String.format("export JPDA_TRANSPORT=%s%n", runnerConfiguration.getDebugTransport()));
        export.append(String.format("export JPDA_SUSPEND=%s%n", runnerConfiguration.isDebugSuspend() ? "y" : "n"));
        return export.toString();
    }

    private String catalinaUnix(SDKRunnerConfiguration runnerConfiguration) {
        final boolean debug = runnerConfiguration.getDebugPort() > 0;
        if (debug) {
            return "./bin/catalina.sh jpda run > ../logs/stdout.log 2> ../logs/stderr.log &\n";
        }
        return "./bin/catalina.sh run > ../logs/stdout.log 2> ../logs/stderr.log &\n";
    }

    // Windows

    // TODO: implement
    protected ApplicationProcess startWindows(java.io.File appDir, SDKRunnerConfiguration runnerConfiguration) {
        throw new UnsupportedOperationException();
    }

    private static class TomcatProcess extends ApplicationProcess {
        final int                httpPort;
        final List<java.io.File> logFiles;
        final int                debugPort;
        final ExecutorService    pidTaskExecutor;
        final java.io.File       startUpScriptFile;
        final java.io.File       workDir;
        int pid = -1;
        TomcatLogger logger;
        Process      process;

        TomcatProcess(int httpPort, List<java.io.File> logFiles, int debugPort, java.io.File startUpScriptFile, java.io.File workDir) {
            this.httpPort = httpPort;
            this.logFiles = logFiles;
            this.debugPort = debugPort;
            this.startUpScriptFile = startUpScriptFile;
            this.workDir = workDir;
            pidTaskExecutor = Executors.newCachedThreadPool(new NamedThreadFactory("TomcatServer-", true));
        }

        @Override
        public synchronized void start() throws RunnerException {
            if (pid != -1) {
                throw new IllegalStateException("Process is already started");
            }

            try {
                process = Runtime.getRuntime()
                                 .exec(new CommandLine(startUpScriptFile.getAbsolutePath()).toShellCommand(), null, workDir);

                pid = pidTaskExecutor.submit(new Callable<Integer>() {
                    @Override
                    public Integer call() throws Exception {
                        final java.io.File pidFile = new java.io.File(workDir, "run.pid");
                        final Path pidPath = pidFile.toPath();
                        synchronized (this) {
                            while (!Files.isReadable(pidPath)) {
                                wait(100);
                            }
                        }
                        final BufferedReader pidReader = new BufferedReader(new FileReader(pidFile));
                        try {
                            return Integer.valueOf(pidReader.readLine());
                        } finally {
                            try {
                                pidReader.close();
                            } catch (IOException ignored) {
                            }
                        }
                    }
                }).get(5, TimeUnit.SECONDS);

                logger = new TomcatLogger(logFiles);
                LOG.debug("Start Tomcat at port {}, application {}", httpPort, workDir);
            } catch (IOException | InterruptedException | TimeoutException e) {
                throw new RunnerException(e);
            } catch (ExecutionException e) {
                throw new RunnerException(e.getCause());
            }
        }

        @Override
        public synchronized void stop() throws RunnerException {
            if (pid == -1) {
                throw new IllegalStateException("Process is not started yet");
            }
            ProcessUtil.kill(pid);

            CustomPortService.getInstance().release(httpPort);
            if (debugPort > 0) {
                CustomPortService.getInstance().release(debugPort);
            }
            LOG.debug("Stop Tomcat at port {}, application {}", httpPort, workDir);
        }

        @Override
        public int waitFor() throws RunnerException {
            synchronized (this) {
                if (pid == -1) {
                    throw new IllegalStateException("Process is not started yet");
                }
            }
            try {
                process.waitFor();
            } catch (InterruptedException ignored) {
            }
            return process.exitValue();
        }

        @Override
        public synchronized int exitCode() throws RunnerException {
            if (pid == -1 || ProcessUtil.isAlive(pid)) {
                return -1;
            }
            return process.exitValue();
        }

        @Override
        public synchronized boolean isRunning() throws RunnerException {
            return ProcessUtil.isAlive(pid);
        }

        @Override
        public synchronized ApplicationLogger getLogger() throws RunnerException {
            if (logger == null) {
                // is not started yet
                return ApplicationLogger.DUMMY;
            }
            return logger;
        }

        private static class TomcatLogger implements ApplicationLogger {

            final List<java.io.File> logFiles;

            TomcatLogger(List<java.io.File> logFiles) {
                this.logFiles = logFiles;
            }

            @Override
            public void getLogs(Appendable output) throws IOException {
                for (java.io.File logFile : logFiles) {
                    output.append(String.format("%n====> %1$s <====%n%n", logFile.getName()));
                    try (FileReader r = new FileReader(logFile)) {
                        CharStreams.copy(r, output);
                    }
                    output.append(System.lineSeparator());
                }
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

    private static class ProcessLineConsumer implements LineConsumer {
        final StringBuilder output = new StringBuilder();

        @Override
        public void writeLine(String line) throws IOException {
            output.append('\n').append(line);
        }

        @Override
        public void close() throws IOException {
            //nothing to close
        }

        StringBuilder getOutput() {
            return output;
        }
    }
}
