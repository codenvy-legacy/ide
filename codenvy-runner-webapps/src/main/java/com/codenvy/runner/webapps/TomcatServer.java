/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.runner.webapps;

import com.codenvy.api.core.notification.EventService;
import com.codenvy.api.core.util.CommandLine;
import com.codenvy.api.core.util.ProcessUtil;
import com.codenvy.api.core.util.StreamPump;
import com.codenvy.api.core.util.SystemInfo;
import com.codenvy.api.runner.RunnerException;
import com.codenvy.api.runner.internal.ApplicationLogger;
import com.codenvy.api.runner.internal.ApplicationLogsPublisher;
import com.codenvy.api.runner.internal.ApplicationProcess;
import com.codenvy.api.runner.internal.DeploymentSources;
import com.codenvy.commons.lang.IoUtil;
import com.codenvy.commons.lang.ZipUtils;
import com.google.common.io.CharStreams;
import com.google.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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

    private final int          memSize;
    private final java.io.File tomcatHome;
    private final EventService eventService;

    @Inject
    public TomcatServer(@Named(MEM_SIZE_PARAMETER) int memSize,
                        @Named(TOMCAT_HOME_PARAMETER) java.io.File tomcatHome,
                        EventService eventService) {
        this.memSize = memSize;
        this.tomcatHome = tomcatHome;
        this.eventService = eventService;
    }

    @Override
    public final String getName() {
        return "Tomcat7";
    }

    @Override
    public String getDescription() {
        return "Apache Tomcat 7.0 is an implementation of the Java Servlet and JavaServer Pages technologies.\n" +
               "Home page: http://tomcat.apache.org/";
    }

    @Override
    public ApplicationProcess deploy(java.io.File appDir,
                                     DeploymentSources toDeploy,
                                     ApplicationServerRunnerConfiguration runnerConfiguration,
                                     ApplicationProcess.Callback callback) throws RunnerException {
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
            return startUnix(appDir, runnerConfiguration, callback);
        } else {
            return startWindows(appDir, runnerConfiguration, callback);
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
                                           ApplicationProcess.Callback callback) throws RunnerException {
        final java.io.File logsDir = new java.io.File(appDir, "logs");
        final java.io.File startUpScriptFile;
        try {
            startUpScriptFile = genStartUpScriptUnix(appDir, runnerConfiguration);
            Files.createDirectory(logsDir.toPath());
        } catch (IOException e) {
            throw new RunnerException(e);
        }
        final List<java.io.File> logFiles = new ArrayList<>(1);
        logFiles.add(new java.io.File(logsDir, "output.log"));

        return new TomcatProcess(appDir, startUpScriptFile, logFiles, runnerConfiguration, callback, eventService);
    }

    private java.io.File genStartUpScriptUnix(java.io.File appDir, ApplicationServerRunnerConfiguration runnerConfiguration)
            throws IOException {
        final String startupScript = "#!/bin/sh\n" +
                                     exportEnvVariablesUnix(runnerConfiguration) +
                                     "cd tomcat\n" +
                                     "chmod +x bin/*.sh\n" +
                                     catalinaUnix(runnerConfiguration) +
                                     "PID=$!\n" +
                                     "echo \"$PID\" > ../run.pid\n" +
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
        /*
        From catalina.sh:
        -agentlib:jdwp=transport=$JPDA_TRANSPORT,address=$JPDA_ADDRESS,server=y,suspend=$JPDA_SUSPEND
         */
        return catalinaOpts +
               String.format("export JPDA_ADDRESS=%d%n", debugPort) +
               String.format("export JPDA_TRANSPORT=%s%n", runnerConfiguration.getDebugTransport()) +
               String.format("export JPDA_SUSPEND=%s%n", runnerConfiguration.isDebugSuspend() ? "y" : "n");
    }

    private String catalinaUnix(ApplicationServerRunnerConfiguration runnerConfiguration) {
        final boolean debug = runnerConfiguration.getDebugPort() > 0;
        if (debug) {
            return "./bin/catalina.sh jpda run 2>&1 | tee ../logs/output.log &\n";
        }
        return "./bin/catalina.sh run 2>&1 | tee ../logs/output.log &\n";
    }

    // Windows

    protected ApplicationProcess startWindows(java.io.File appDir,
                                              ApplicationServerRunnerConfiguration runnerConfiguration,
                                              ApplicationProcess.Callback callback) {
        throw new UnsupportedOperationException();
    }

    private static class TomcatProcess extends ApplicationProcess {
        final int                httpPort;
        final List<java.io.File> logFiles;
        final int                debugPort;
        final java.io.File       startUpScriptFile;
        final java.io.File       workDir;
        final Callback           callback;
        final EventService       eventService;
        final String             workspace;
        final String             project;
        final long               id;

        ApplicationLogger logger;
        Process           process;
        StreamPump        output;

        TomcatProcess(java.io.File appDir, java.io.File startUpScriptFile, List<java.io.File> logFiles,
                      ApplicationServerRunnerConfiguration runnerConfiguration, Callback callback, EventService eventService) {
            this.httpPort = runnerConfiguration.getHttpPort();
            this.logFiles = logFiles;
            this.debugPort = runnerConfiguration.getDebugPort();
            this.startUpScriptFile = startUpScriptFile;
            this.workDir = appDir;
            this.callback = callback;
            this.eventService = eventService;
            this.workspace = runnerConfiguration.getRequest().getWorkspace();
            this.project = runnerConfiguration.getRequest().getProject();
            this.id = runnerConfiguration.getRequest().getId();
        }

        @Override
        public synchronized void start() throws RunnerException {
            if (process != null && ProcessUtil.isAlive(process)) {
                throw new IllegalStateException("Process is already started");
            }
            try {
                process = Runtime.getRuntime().exec(new CommandLine(startUpScriptFile.getAbsolutePath()).toShellCommand(), null, workDir);
                logger = new ApplicationLogsPublisher(new TomcatLogger(logFiles), eventService, id, workspace, project);
                output = new StreamPump();
                output.start(process, logger);
                LOG.debug("Start Tomcat at port {}, application {}", httpPort, workDir);
            } catch (IOException e) {
                throw new RunnerException(e);
            }
        }

        @Override
        public synchronized void stop() throws RunnerException {
            if (process == null) {
                throw new IllegalStateException("Process is not started yet");
            }
            // Use ProcessUtil.kill(process) because java.lang.Process.destroy() method doesn't
            // kill all child processes (see http://bugs.sun.com/view_bug.do?bug_id=4770092).
            ProcessUtil.kill(process);
            if (output != null) {
                output.stop();
            }
            callback.stopped();
            LOG.debug("Stop Tomcat at port {}, application {}", httpPort, workDir);
        }

        @Override
        public int waitFor() throws RunnerException {
            synchronized (this) {
                if (process == null) {
                    throw new IllegalStateException("Process is not started yet");
                }
            }
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                Thread.interrupted();
                ProcessUtil.kill(process);
            } finally {
                if (output != null) {
                    output.stop();
                }
            }
            return process.exitValue();
        }

        @Override
        public synchronized int exitCode() throws RunnerException {
            if (process == null || ProcessUtil.isAlive(process)) {
                return -1;
            }
            return process.exitValue();
        }

        @Override
        public synchronized boolean isRunning() throws RunnerException {
            return process != null && ProcessUtil.isAlive(process);
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
                // noop since logs already redirected to the file
            }

            @Override
            public void close() throws IOException {
            }
        }
    }
}