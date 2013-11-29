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

import com.codenvy.api.core.rest.FileAdapter;
import com.codenvy.api.core.util.CommandLine;
import com.codenvy.api.core.util.CustomPortService;
import com.codenvy.api.core.util.LineConsumer;
import com.codenvy.api.core.util.ProcessUtil;
import com.codenvy.api.runner.RunnerException;
import com.codenvy.api.runner.internal.*;
import com.codenvy.api.runner.internal.dto.RunRequest;
import com.codenvy.commons.lang.IoUtil;
import com.codenvy.commons.lang.NamedThreadFactory;
import com.codenvy.ide.commons.ZipUtils;
import com.google.common.io.CharStreams;

import org.apache.maven.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Runner implementation to test Codenvy plug-ins by launching
 * a separate Codenvy web-application in Tomcat server.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 */
public class SDKRunner extends Runner {
    private static final Logger LOG                       = LoggerFactory.getLogger(SDKRunner.class);
    /** String in JSON format to register builder service. */
    private static final String BUILDER_REGISTRATION_JSON =
            "[{\"builderServiceLocation\":{\"url\":\"http://localhost:${PORT}/api/internal/builder\"}}]";
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
        return "Codenvy plug-ins runtime";
    }

    @Override
    public RunnerConfigurationFactory getRunnerConfigurationFactory() {
        return new RunnerConfigurationFactory() {
            @Override
            public RunnerConfiguration createRunnerConfiguration(RunRequest request) throws RunnerException {
                return new RunnerConfiguration(request.getMemorySize(), CustomPortService.getInstance().acquire(), 0,
                                               request);
            }
        };
    }

    @Override
    protected ApplicationProcess newApplicationProcess(DeploymentSources toDeploy,
                                                       RunnerConfiguration runnerCfg) throws RunnerException {
        final File appDir;
        try {
            appDir = Files.createTempDirectory(getDeployDirectory().toPath(), ("app_" + getName() + '_')).toFile();

            final Path tomcatPath = Files.createDirectory(appDir.toPath().resolve("tomcat"));
            ZipUtils.unzip(Utils.getTomcatBinaryDistribution().openStream(), tomcatPath.toFile());

            final Path webappsPath = tomcatPath.resolve("webapps");
            final File warFile = buildCodenvyWebApp(toDeploy.getFile()).toFile();
            ZipUtils.unzip(warFile, webappsPath.resolve("ide").toFile());

            configureBuilderService(webappsPath, runnerCfg);
            setEnvVariables(tomcatPath, runnerCfg);
            generateServerXml(tomcatPath.toFile(), runnerCfg);
        } catch (IOException e) {
            throw new RunnerException(e);
        }

        File startUpScriptFile = genStartUpScriptUnix(appDir, runnerCfg);
        if (!startUpScriptFile.setExecutable(true, false)) {
            throw new RunnerException("Unable update attributes of the startup script");
        }
        final File logsDir = new File(appDir, "logs");
        if (!logsDir.mkdir()) {
            throw new RunnerException("Unable create logs directory");
        }
        final List<FileAdapter> logFiles = new ArrayList<>(2);
        logFiles.add(new FileAdapter(new java.io.File(logsDir, "stdout.log"), "logs/stdout.log", "text/plain"));
        logFiles.add(new FileAdapter(new java.io.File(logsDir, "stderr.log"), "logs/stderr.log", "text/plain"));

        final TomcatProcess process =
                new TomcatProcess(runnerCfg.getPort(), logFiles, runnerCfg.getDebugPort(), startUpScriptFile, appDir);
        registerDisposer(process, new Disposer() {
            @Override
            public void dispose() {
                if (ProcessUtil.isAlive(process.pid)) {
                    ProcessUtil.kill(process.pid);
                }

                CustomPortService.getInstance().release(process.httpPort);
                if (process.debugPort > 0) {
                    CustomPortService.getInstance().release(process.debugPort);
                }
                IoUtil.deleteRecursive(process.workDir);
                LOG.debug("stop tomcat at port {}, application {}", process.httpPort, process.workDir);
            }
        });
        return process;
    }

    private Path buildCodenvyWebApp(File jarFile) throws RunnerException {
        Path warPath;
        try {
            // prepare Codenvy Platform sources
            final Path appDirPath =
                    Files.createTempDirectory(getDeployDirectory().toPath(), ("war_" + getName() + '_'));
            ZipUtils.unzip(Utils.getCodenvyPlatformBinaryDistribution().openStream(), appDirPath.toFile());

            // add extension to Codenvy Platform
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

    private void configureBuilderService(Path webappsPath, RunnerConfiguration runnerCfg)
            throws RunnerException, IOException {
        final Path apiAppPath = webappsPath.resolve("api");
        ZipUtils.unzip(webappsPath.resolve("api.war").toFile(), apiAppPath.toFile());

        String cfg = BUILDER_REGISTRATION_JSON.replace("${PORT}", Integer.toString(runnerCfg.getPort()));
        final Path builderRegistrationJsonPath =
                apiAppPath.resolve("WEB-INF/classes/conf/builder_service_registrations.json");
        try {
            Files.write(builderRegistrationJsonPath, cfg.getBytes());
        } catch (IOException e) {
            throw new RunnerException(e);
        }
    }

    private void setEnvVariables(Path tomcatPath, RunnerConfiguration runnerCfg) throws IOException {
        final Path setenvShPath = tomcatPath.resolve("bin/setenv.sh");
        final byte[] bytes = Files.readAllBytes(setenvShPath);
        final String setenvShContent = new String(bytes);
        Files.write(setenvShPath, setenvShContent.replace("${PORT}", Integer.toString(runnerCfg.getPort())).getBytes());
    }

    private void generateServerXml(File tomcatDir, RunnerConfiguration runnerConfiguration)
            throws RunnerException {
        String cfg = SERVER_XML.replace("${PORT}", Integer.toString(runnerConfiguration.getPort()));
        final File serverXmlFile = new File(new File(tomcatDir, "conf"), "server.xml");
        try {
            Files.write(serverXmlFile.toPath(), cfg.getBytes());
        } catch (IOException e) {
            throw new RunnerException(e);
        }
    }

    private File genStartUpScriptUnix(File appDir, RunnerConfiguration runnerConfiguration) throws RunnerException {
        final String startupScript = "#!/bin/sh\n" +
                                     exportEnvVariablesUnix(runnerConfiguration) +
                                     "cd tomcat\n" +
                                     "chmod +x bin/*.sh\n" +
                                     catalinaUnix(runnerConfiguration) +
                                     "PID=$!\n" +
                                     "echo \"$PID\" >> ../run.pid\n" +
                                     "wait $PID";
        final File startUpScriptFile = new File(appDir, "startup.sh");
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

    private String exportEnvVariablesUnix(RunnerConfiguration runnerConfiguration) {
        int memory = runnerConfiguration.getMemory();
        if (memory <= 0) {
            memory = 256;
        }
        final String catalinaOpts = String.format("export CATALINA_OPTS=\"-Xms%dm -Xmx%dm\"%n", memory, memory);
        final int debugPort = runnerConfiguration.getDebugPort();
        if (debugPort <= 0) {
            return catalinaOpts;
        }
        final StringBuilder export = new StringBuilder();
        export.append(catalinaOpts);
        return export.toString();
    }

    private String catalinaUnix(RunnerConfiguration runnerConfiguration) {
        final boolean debug = runnerConfiguration.getDebugPort() > 0;
        if (debug) {
            return "./bin/catalina.sh jpda run > ../logs/stdout.log 2> ../logs/stderr.log &\n";
        }
        return "./bin/catalina.sh run > ../logs/stdout.log 2> ../logs/stderr.log &\n";
    }

    private static class TomcatProcess extends ApplicationProcess {
        final int               httpPort;
        final List<FileAdapter> logFiles;
        final int               debugPort;
        final ExecutorService   pidTaskExecutor;
        final File              startUpScriptFile;
        final File              workDir;
        int pid = -1;
        TomcatLogger logger;
        Process      process;

        TomcatProcess(int httpPort, List<FileAdapter> logFiles, int debugPort, File startUpScriptFile, File workDir) {
            this.httpPort = httpPort;
            this.logFiles = logFiles;
            this.debugPort = debugPort;
            this.startUpScriptFile = startUpScriptFile;
            this.workDir = workDir;
            pidTaskExecutor = Executors.newCachedThreadPool(new NamedThreadFactory("TomcatServer-", true));
        }

        @Override
        public synchronized void start() throws RunnerException {
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
        public synchronized void stop() throws RunnerException {
            if (pid == -1) {
                throw new IllegalStateException("Process is not started yet.");
            }
            ProcessUtil.kill(pid);

            CustomPortService.getInstance().release(httpPort);
            if (debugPort > 0) {
                CustomPortService.getInstance().release(debugPort);
            }
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
