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
package com.codenvy.runner.webapps;

import com.codenvy.api.core.util.CommandLine;
import com.codenvy.api.core.util.ProcessUtil;
import com.codenvy.api.core.util.SystemInfo;
import com.codenvy.api.runner.RunnerException;
import com.codenvy.api.runner.internal.ApplicationLogger;
import com.codenvy.api.runner.internal.ApplicationProcess;
import com.codenvy.api.runner.internal.DeploymentSources;
import com.codenvy.commons.lang.IoUtil;
import com.codenvy.commons.lang.NamedThreadFactory;
import com.codenvy.commons.lang.ZipUtils;
import com.google.common.io.CharStreams;
import com.google.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
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
 * {@code ApplicationServer} implementation to deploy application to Apache Tomcat servlet container.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class TomcatServer implements ApplicationServer {
    public static final  String TOMCAT_HOME_PARAMETER = "runner.tomcat.tomcat_home";
    public static final  String MEM_SIZE_PARAMETER    = "runner.tomcat.memory";
    private static final Logger LOG                   = LoggerFactory.getLogger(TomcatServer.class);
    private static final String SERVER_XML            =
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
    private final ExecutorService pidTaskExecutor;
    private final int             memSize;
    private final java.io.File    tomcatHome;

    @Inject
    public TomcatServer(@Named(MEM_SIZE_PARAMETER) int memSize,
                        @Named(TOMCAT_HOME_PARAMETER) java.io.File tomcatHome) {
        this.memSize = memSize;
        this.tomcatHome = tomcatHome;
        this.pidTaskExecutor = Executors.newCachedThreadPool(new NamedThreadFactory("TomcatServer-", true));
    }

    @Override
    public final String getName() {
        return "Tomcat";
    }

    @Override
    public ApplicationProcess deploy(java.io.File appDir,
                                     DeploymentSources toDeploy,
                                     ApplicationServerRunnerConfiguration runnerConfiguration,
                                     StopCallback stopCallback) throws RunnerException {
        final java.io.File myTomcatHome = getTomcatHome();
        try {
            final Path tomcatPath = Files.createDirectory(appDir.toPath().resolve("tomcat"));
            IoUtil.copy(myTomcatHome, tomcatPath.toFile(), null);
            final Path webappsPath = tomcatPath.resolve("webapps");
            if (Files.exists(webappsPath)) {
                IoUtil.deleteRecursive(webappsPath.toFile());
            }
            Files.createDirectory(webappsPath);
            final Path rootPath = Files.createDirectory(webappsPath.resolve("ROOT"));
            if (toDeploy.isArchive()) {
                ZipUtils.unzip(toDeploy.getFile(), rootPath.toFile());
            } else {
                IoUtil.copy(toDeploy.getFile(), rootPath.toFile(), null);
            }
            generateServerXml(tomcatPath.toFile(), runnerConfiguration);
        } catch (IOException e) {
            throw new RunnerException(e);
        }

        if (SystemInfo.isUnix()) {
            return startUnix(appDir, runnerConfiguration, stopCallback);
        } else {
            return startWindows(appDir, runnerConfiguration, stopCallback);
        }
    }

    protected void generateServerXml(java.io.File tomcatDir, ApplicationServerRunnerConfiguration runnerConfiguration) throws IOException {
        final String cfg = SERVER_XML.replace("${PORT}", Integer.toString(runnerConfiguration.getHttpPort()));
        final java.io.File serverXmlFile = new java.io.File(new java.io.File(tomcatDir, "conf"), "server.xml");
        Files.write(serverXmlFile.toPath(), cfg.getBytes());
    }

    public java.io.File getTomcatHome() {
        return tomcatHome;
    }

    public int getMemSize() {
        return memSize;
    }

    @Override
    public String toString() {
        return "Tomcat Server";
    }

    // *nix

    protected ApplicationProcess startUnix(final java.io.File appDir,
                                           final ApplicationServerRunnerConfiguration runnerConfiguration,
                                           StopCallback stopCallback) throws RunnerException {
        final java.io.File logsDir = new java.io.File(appDir, "logs");
        final java.io.File startUpScriptFile;
        try {
            startUpScriptFile = genStartUpScriptUnix(appDir, runnerConfiguration);
            Files.createDirectory(logsDir.toPath());
        } catch (IOException e) {
            throw new RunnerException(e);
        }
        final List<java.io.File> logFiles = new ArrayList<>(2);
        logFiles.add(new java.io.File(logsDir, "stdout.log"));
        logFiles.add(new java.io.File(logsDir, "stderr.log"));

        return new TomcatProcess(runnerConfiguration.getHttpPort(), logFiles, runnerConfiguration.getDebugPort(),
                                 startUpScriptFile, appDir, stopCallback, pidTaskExecutor);
    }

    private java.io.File genStartUpScriptUnix(java.io.File appDir, ApplicationServerRunnerConfiguration runnerConfiguration)
            throws IOException {
        final String startupScript = "#!/bin/sh\n" +
                                     exportEnvVariablesUnix(runnerConfiguration) +
                                     "cd tomcat\n" +
                                     "chmod +x bin/*.sh\n" +
                                     catalinaUnix(runnerConfiguration) +
                                     "PID=$!\n" +
                                     "echo \"$PID\" >> ../run.pid\n" +
                                     "wait $PID";
        final java.io.File startUpScriptFile = new java.io.File(appDir, "startup.sh");
        Files.write(startUpScriptFile.toPath(), startupScript.getBytes());
        if (!startUpScriptFile.setExecutable(true, false)) {
            throw new IOException("Unable to update attributes of the startup script");
        }
        return startUpScriptFile;
    }

    private String exportEnvVariablesUnix(ApplicationServerRunnerConfiguration runnerConfiguration) {
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

    private String catalinaUnix(ApplicationServerRunnerConfiguration runnerConfiguration) {
        final boolean debug = runnerConfiguration.getDebugPort() > 0;
        if (debug) {
            return "./bin/catalina.sh jpda run > ../logs/stdout.log 2> ../logs/stderr.log &\n";
        }
        return "./bin/catalina.sh run > ../logs/stdout.log 2> ../logs/stderr.log &\n";
    }

    // Windows

    protected ApplicationProcess startWindows(java.io.File appDir,
                                              ApplicationServerRunnerConfiguration runnerConfiguration,
                                              StopCallback stopCallback) {
        throw new UnsupportedOperationException();
    }

    private static class TomcatProcess extends ApplicationProcess {
        final int                httpPort;
        final List<java.io.File> logFiles;
        final int                debugPort;
        final ExecutorService    pidTaskExecutor;
        final java.io.File       startUpScriptFile;
        final java.io.File       workDir;
        final StopCallback       stopCallback;

        int pid = -1;
        TomcatLogger logger;
        Process      process;

        TomcatProcess(int httpPort, List<java.io.File> logFiles, int debugPort, java.io.File startUpScriptFile, java.io.File workDir,
                      StopCallback stopCallback, ExecutorService pidTaskExecutor) {
            this.httpPort = httpPort;
            this.logFiles = logFiles;
            this.debugPort = debugPort;
            this.startUpScriptFile = startUpScriptFile;
            this.workDir = workDir;
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

                logger = new TomcatLogger(logFiles);
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
            } catch (InterruptedException ignored) {
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
}