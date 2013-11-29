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

import com.codenvy.api.core.config.Configuration;
import com.codenvy.api.core.rest.FileAdapter;
import com.codenvy.api.core.util.CommandLine;
import com.codenvy.api.core.util.ProcessUtil;
import com.codenvy.api.core.util.SystemInfo;
import com.codenvy.api.runner.RunnerException;
import com.codenvy.api.runner.internal.ApplicationLogger;
import com.codenvy.api.runner.internal.ApplicationProcess;
import com.codenvy.api.runner.internal.DeploymentSources;
import com.codenvy.api.runner.internal.DeploymentSourcesValidator;
import com.codenvy.commons.lang.IoUtil;
import com.codenvy.commons.lang.NamedThreadFactory;
import com.codenvy.commons.lang.ZipUtils;
import com.google.common.io.CharStreams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * {@code ApplicationServer} implementation to deploy application to Apache Tomcat servlet container.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 */
public class TomcatServer implements ApplicationServer {
    public static final  String TOMCAT_HOME_PARAMETER       = "runner.tomcat.tomcat_home";
    public static final  String MEM_SIZE_PARAMETER          = "runner.tomcat.memory";
    public static final  int    DEFAULT_MEM_SIZE            = 256;
    private static final Logger LOG                         = LoggerFactory.getLogger(TomcatServer.class);
    private static final String SERVER_XML                  =
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
    private static final String TOMCAT_HOME_SYSTEM_PROPERTY = "codenvy.runner.tomcat.home";
    /** Validator for deployment sources. */
    protected final DeploymentSourcesValidator appValidator;
    protected final ExecutorService            pidTaskExecutor;
    private         java.io.File               tomcatHome;
    private         int                        defaultMemSize;

    public TomcatServer() {
        appValidator = new JavaWebApplicationValidator();
        pidTaskExecutor = Executors.newCachedThreadPool(new NamedThreadFactory("TomcatServer-", true));
        defaultMemSize = DEFAULT_MEM_SIZE;

        Configuration configuration = new Configuration();
        final String tomcatHomeDir = System.getProperty(TOMCAT_HOME_SYSTEM_PROPERTY);
        if (tomcatHomeDir != null && Files.exists(Paths.get(tomcatHomeDir))) {
            configuration.setFile(TomcatServer.TOMCAT_HOME_PARAMETER, new java.io.File(tomcatHomeDir));
        }
        setConfiguration(configuration);
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
        if (myTomcatHome == null) {
            throw new RunnerException("System property " + TOMCAT_HOME_SYSTEM_PROPERTY +
                                      " is not set or Tomcat home directory does not exist.");
        }
        validate(toDeploy);
        try {
            final Path tomcatPath = Files.createDirectory(appDir.toPath().resolve("tomcat"));
            IoUtil.copy(myTomcatHome, tomcatPath.toFile(), null);
            final Path webappsPath = Files.createDirectory(tomcatPath.resolve("webapps"));
            final Path rootPath = Files.createDirectory(webappsPath.resolve("ROOT"));
            if (toDeploy.isArchive()) {
                ZipUtils.unzip(toDeploy.getFile(), rootPath.toFile());
            } else {
                IoUtil.copy(toDeploy.getFile(), rootPath.toFile(), null);
            }
            genServerXml(tomcatPath.toFile(), runnerConfiguration);
            if (SystemInfo.isUnix()) {
                return startUnix(appDir, runnerConfiguration, stopCallback);
            } else {
                return startWindows(appDir, runnerConfiguration);
            }
        } catch (IOException e) {
            throw new RunnerException(e);
        }
    }

    public java.io.File getTomcatHome() {
        return tomcatHome;
    }

    public int getDefaultMemSize() {
        return defaultMemSize;
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
        configuration.setInt(MEM_SIZE_PARAMETER, getDefaultMemSize());
        if (tomcatHome != null) {
            configuration.setFile(TOMCAT_HOME_PARAMETER, tomcatHome);
        }
        return configuration;
    }

    @Override
    public void setConfiguration(Configuration configuration) {
        defaultMemSize = configuration.getInt(MEM_SIZE_PARAMETER, DEFAULT_MEM_SIZE);
        tomcatHome = configuration.getFile(TOMCAT_HOME_PARAMETER, null);
    }

    @Override
    public String toString() {
        return "Tomcat Server";
    }

    protected void validate(DeploymentSources toDeploy) throws RunnerException {
        if (!appValidator.isValid(toDeploy)) {
            throw new RunnerException(
                    String.format("Invalid deployment. Cannot deploy this application in %s server", getName()));
        }
    }

    protected void genServerXml(java.io.File tomcatDir, ApplicationServerRunnerConfiguration runnerConfiguration)
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
                                           final ApplicationServerRunnerConfiguration runnerConfiguration,
                                           StopCallback stopCallback)
            throws RunnerException {
        java.io.File startUpScriptFile = genStartUpScriptUnix(appDir, runnerConfiguration);
        if (!startUpScriptFile.setExecutable(true, false)) {
            throw new RunnerException("Unable update attributes of the startup script");
        }

        final java.io.File logsDir = new java.io.File(appDir, "logs");
        if (!logsDir.mkdir()) {
            throw new RunnerException("Unable create logs directory");
        }
        final List<FileAdapter> logFiles = new ArrayList<>(2);
        logFiles.add(new FileAdapter(new java.io.File(logsDir, "stdout.log"), "logs/stdout.log", "text/plain"));
        logFiles.add(new FileAdapter(new java.io.File(logsDir, "stderr.log"), "logs/stderr.log", "text/plain"));

        return new TomcatProcess(runnerConfiguration.getPort(), logFiles, runnerConfiguration.getDebugPort(),
                                 startUpScriptFile, appDir, stopCallback, pidTaskExecutor);
    }

    private java.io.File genStartUpScriptUnix(java.io.File appDir,
                                              ApplicationServerRunnerConfiguration runnerConfiguration)
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

    private String exportEnvVariablesUnix(ApplicationServerRunnerConfiguration runnerConfiguration) {
        int memory = runnerConfiguration.getMemory();
        if (memory <= 0) {
            memory = getDefaultMemSize();
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

    // TODO: implement
    protected ApplicationProcess startWindows(java.io.File appDir,
                                              ApplicationServerRunnerConfiguration runnerConfiguration) {
        throw new UnsupportedOperationException();
    }

    private static class TomcatProcess extends ApplicationProcess {
        final int               httpPort;
        final List<FileAdapter> logFiles;
        final int               debugPort;
        final ExecutorService   pidTaskExecutor;
        final File              startUpScriptFile;
        final File              workDir;
        final StopCallback      stopCallback;
        int pid = -1;
        TomcatLogger logger;
        Process      process;

        TomcatProcess(int httpPort, List<FileAdapter> logFiles, int debugPort, File startUpScriptFile, File workDir,
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
            if (ProcessUtil.isAlive(pid)) {
                throw new IllegalStateException("Process is already started.");
            }

            try {
                process = Runtime.getRuntime()
                                 .exec(new CommandLine(startUpScriptFile.getAbsolutePath()).toShellCommand(), null,
                                       workDir);
                pid = pidTaskExecutor.submit(new Callable<Integer>() {
                    @Override
                    public Integer call() throws Exception {
                        final File pidFile = new File(workDir, "run.pid");
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
                LOG.debug("start tomcat at port {}, application {}", httpPort, workDir);
            } catch (IOException | InterruptedException | TimeoutException e) {
                throw new RunnerException(e);
            } catch (ExecutionException e) {
                throw new RunnerException(e.getCause());
            }
        }

        @Override
        public void stop() throws RunnerException {
            if (pid == -1) {
                throw new IllegalStateException("Process is not started yet.");
            }
            ProcessUtil.kill(pid);

//            CustomPortService.getInstance().release(httpPort);
//            if (debugPort > 0) {
//                CustomPortService.getInstance().release(debugPort);
//            }

            stopCallback.stopped();

            IoUtil.deleteRecursive(workDir);
            LOG.debug("stop tomcat at port {}, application {}", httpPort, workDir);
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

            final List<FileAdapter> logFiles;

            TomcatLogger(List<FileAdapter> logFiles) {
                this.logFiles = logFiles;
            }

            @Override
            public void getLogs(Appendable output) throws IOException {
                for (FileAdapter logFile : logFiles) {
                    if (logFile.getIoFile().getTotalSpace() > 0) {
                        output.append("\n====> ").append(logFile.getName()).append(" <====\n\n");
                        CharStreams.copy(new InputStreamReader(new FileInputStream(logFile.getIoFile())), output);
                        output.append("\n");
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