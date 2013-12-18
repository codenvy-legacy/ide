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

import com.codenvy.api.core.config.Configuration;
import com.codenvy.api.core.util.CommandLine;
import com.codenvy.api.core.util.ProcessUtil;
import com.codenvy.api.core.util.SystemInfo;
import com.codenvy.api.runner.RunnerException;
import com.codenvy.api.runner.internal.ApplicationLogger;
import com.codenvy.api.runner.internal.ApplicationProcess;
import com.codenvy.commons.lang.NamedThreadFactory;
import com.codenvy.commons.lang.ZipUtils;
import com.google.common.io.CharStreams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.zip.ZipFile;

/**
 * {@link ApplicationServer} implementation to deploy application to Apache Tomcat servlet container.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 */
public class TomcatServer implements ApplicationServer {
    public static final  String MEM_SIZE_PARAMETER        = "runner.tomcat.memory";
    public static final  int    DEFAULT_MEM_SIZE          = 256;
    private static final Logger LOG                       = LoggerFactory.getLogger(TomcatServer.class);
    /** String in JSON format to register builder service. */
    private static final String BUILDER_REGISTRATION_JSON =
            "[{\"builderServiceLocation\":{\"url\":\"http://localhost:${PORT}/api/internal/builder\"}}]";
    /** String in JSON format to register runner service. */
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
    /** Validator to validate deployment sources. */
    protected final JavaWebApplicationValidator appValidator;
    protected final ExecutorService             pidTaskExecutor;
    private         int                         memSize;

    public TomcatServer() {
        appValidator = new JavaWebApplicationValidator();
        pidTaskExecutor = Executors.newCachedThreadPool(new NamedThreadFactory("TomcatServer-", true));

        Configuration configuration = new Configuration();
        setConfiguration(configuration);
    }

    @Override
    public final String getName() {
        return "Tomcat";
    }

    @Override
    public ApplicationProcess deploy(java.io.File appDir,
                                     ZipFile webApp,
                                     SDKRunnerConfiguration runnerConfiguration,
                                     CodeServer.CodeServerProcess codeServerProcess,
                                     StopCallback stopCallback) throws RunnerException {
        validate(webApp);
        try {
            final Path tomcatPath = Files.createDirectory(appDir.toPath().resolve("tomcat"));
            ZipUtils.unzip(Utils.getTomcatBinaryDistribution().openStream(), tomcatPath.toFile());

            final Path webappsPath = tomcatPath.resolve("webapps");
            ZipUtils.unzip(new File(webApp.getName()), webappsPath.resolve("ide").toFile());

            generateServerXml(tomcatPath.toFile(), runnerConfiguration);
            configureApiServices(webappsPath, runnerConfiguration);
            setEnvVariables(tomcatPath, runnerConfiguration);

            if (SystemInfo.isUnix()) {
                return startUnix(appDir, runnerConfiguration, codeServerProcess, stopCallback);
            } else {
                return startWindows(appDir, runnerConfiguration, codeServerProcess, stopCallback);
            }
        } catch (IOException e) {
            throw new RunnerException(e);
        }
    }

    protected void validate(ZipFile toDeploy) throws RunnerException {
        if (!appValidator.isValid(toDeploy)) {
            throw new RunnerException(
                    String.format("Invalid deployment. Cannot deploy this application in %s server", getName()));
        }
    }

    protected void generateServerXml(java.io.File tomcatDir, SDKRunnerConfiguration runnerConfiguration)
            throws RunnerException {
        String cfg = SERVER_XML.replace("${PORT}", Integer.toString(runnerConfiguration.getPort()));
        final java.io.File serverXmlFile = new java.io.File(new java.io.File(tomcatDir, "conf"), "server.xml");
        try {
            Files.write(serverXmlFile.toPath(), cfg.getBytes());
        } catch (IOException e) {
            throw new RunnerException(e);
        }
    }

    protected void configureApiServices(Path webappsPath, SDKRunnerConfiguration runnerCfg)
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

    protected void setEnvVariables(Path tomcatPath, SDKRunnerConfiguration runnerCfg) throws IOException {
        final Path setenvShPath = tomcatPath.resolve("bin/setenv.sh");
        final byte[] bytes = Files.readAllBytes(setenvShPath);
        final String setenvShContent = new String(bytes);
        Files.write(setenvShPath, setenvShContent.replace("${PORT}", Integer.toString(runnerCfg.getPort())).getBytes());
    }

    // *nix

    protected ApplicationProcess startUnix(final File appDir, final SDKRunnerConfiguration runnerConfiguration,
                                           CodeServer.CodeServerProcess codeServerProcess, StopCallback stopCallback)
            throws RunnerException {
        java.io.File startUpScriptFile = genStartUpScriptUnix(appDir, runnerConfiguration);

        final java.io.File logsDir = new java.io.File(appDir, "logs");
        if (!logsDir.mkdir()) {
            throw new RunnerException("Unable to create logs directory");
        }
        final List<java.io.File> logFiles = new ArrayList<>(2);
        logFiles.add(new java.io.File(logsDir, "stdout.log"));
        logFiles.add(new java.io.File(logsDir, "stderr.log"));

        return new TomcatProcess(runnerConfiguration.getPort(), logFiles, runnerConfiguration.getDebugPort(),
                                 startUpScriptFile, appDir, codeServerProcess, stopCallback, pidTaskExecutor);
    }

    private java.io.File genStartUpScriptUnix(java.io.File appDir,
                                              SDKRunnerConfiguration runnerConfiguration)
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
            throw new RunnerException("Unable to update attributes of the startup script");
        }
        return startUpScriptFile;
    }

    private String exportEnvVariablesUnix(SDKRunnerConfiguration runnerConfiguration) {
        int memory = runnerConfiguration.getMemory();
        if (memory <= 0) {
            memory = getMemSize();
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

    protected ApplicationProcess startWindows(File appDir, SDKRunnerConfiguration runnerConfiguration,
                                              CodeServer.CodeServerProcess codeServerProcess,
                                              StopCallback stopCallback) {
        throw new UnsupportedOperationException();
    }

    public int getMemSize() {
        return memSize;
    }

    @Override
    public Configuration getDefaultConfiguration() {
        final Configuration defaultConfiguration = new Configuration();
        defaultConfiguration.setInt(MEM_SIZE_PARAMETER, DEFAULT_MEM_SIZE);
        return defaultConfiguration;
    }

    @Override
    public Configuration getConfiguration() {
        final Configuration configuration = new Configuration();
        configuration.setInt(MEM_SIZE_PARAMETER, getMemSize());
        return configuration;
    }

    @Override
    public void setConfiguration(Configuration configuration) {
        memSize = configuration.getInt(MEM_SIZE_PARAMETER, DEFAULT_MEM_SIZE);
    }

    @Override
    public String toString() {
        return "Tomcat Server";
    }

    private static class TomcatProcess extends ApplicationProcess {
        final int                          httpPort;
        final List<java.io.File>           logFiles;
        final int                          debugPort;
        final ExecutorService              pidTaskExecutor;
        final java.io.File                 startUpScriptFile;
        final java.io.File                 workDir;
        final CodeServer.CodeServerProcess codeServerProcess;
        final StopCallback                 stopCallback;
        int pid = -1;
        TomcatLogger logger;
        Process      process;

        TomcatProcess(int httpPort, List<File> logFiles, int debugPort, File startUpScriptFile, File workDir,
                      CodeServer.CodeServerProcess codeServerProcess, StopCallback stopCallback,
                      ExecutorService pidTaskExecutor) {
            this.httpPort = httpPort;
            this.logFiles = logFiles;
            this.debugPort = debugPort;
            this.startUpScriptFile = startUpScriptFile;
            this.workDir = workDir;
            this.codeServerProcess = codeServerProcess;
            this.stopCallback = stopCallback;
            this.pidTaskExecutor = pidTaskExecutor;
        }

        @Override
        public void start() throws RunnerException {
            if (pid != -1) {
                throw new IllegalStateException("Process is already started");
            }

            try {
                process = Runtime.getRuntime()
                                 .exec(new CommandLine(startUpScriptFile.getAbsolutePath()).toShellCommand(), null,
                                       workDir);
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

                if (codeServerProcess != null) {
                    try {
                        codeServerProcess.start();
                    } catch (Exception ignore) {
                    }
                }

                logger = new TomcatLogger(logFiles, codeServerProcess);
                LOG.debug("Start Tomcat at port {}, application {}", httpPort, workDir);
            } catch (IOException | InterruptedException | TimeoutException e) {
                throw new RunnerException(e);
            } catch (ExecutionException e) {
                throw new RunnerException(e.getCause());
            }
        }

        @Override
        public void stop() throws RunnerException {
            if (pid == -1) {
                throw new IllegalStateException("Process is not started yet");
            }
            // Use ProcessUtil.kill(pid) because java.lang.Process.destroy() method doesn't
            // kill all child processes (see http://bugs.sun.com/view_bug.do?bug_id=4770092).
            ProcessUtil.kill(pid);
            if (codeServerProcess != null) {
                try {
                    codeServerProcess.stop();
                } catch (Exception ignore) {
                }
            }
            stopCallback.stopped();
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
            } catch (InterruptedException e) {
            }
            return process.exitValue();
        }

        @Override
        public int exitCode() throws RunnerException {
            if (pid == -1 || ProcessUtil.isAlive(pid)) {
                return -1;
            }
            return process.exitValue();
        }

        @Override
        public boolean isRunning() throws RunnerException {
            return ProcessUtil.isAlive(pid);
        }

        @Override
        public ApplicationLogger getLogger() throws RunnerException {
            if (logger == null) {
                // is not started yet
                return ApplicationLogger.DUMMY;
            }
            return logger;
        }

        private static class TomcatLogger implements ApplicationLogger {
            final List<java.io.File>           logFiles;
            final CodeServer.CodeServerProcess codeServerProcess;

            TomcatLogger(List<java.io.File> logFiles, CodeServer.CodeServerProcess codeServerProcess) {
                this.logFiles = logFiles;
                this.codeServerProcess = codeServerProcess;
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

                if (codeServerProcess != null) {
                    try {
                        codeServerProcess.getLogs(output);
                    } catch (Exception ignore) {
                    }
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

}