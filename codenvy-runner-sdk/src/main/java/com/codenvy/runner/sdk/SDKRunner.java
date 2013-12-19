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
import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.api.core.util.ComponentLoader;
import com.codenvy.api.core.util.CustomPortService;
import com.codenvy.api.core.util.LineConsumer;
import com.codenvy.api.core.util.ProcessUtil;
import com.codenvy.api.runner.RunnerException;
import com.codenvy.api.runner.internal.*;
import com.codenvy.api.runner.internal.dto.RunRequest;
import com.codenvy.dto.server.DtoFactory;
import com.codenvy.ide.commons.FileUtils;
import com.codenvy.ide.commons.GwtXmlUtils;
import com.codenvy.ide.commons.MavenUtils;
import com.codenvy.ide.commons.ZipUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipFile;

/**
 * Runner implementation to run Codenvy extensions by deploying it to application server.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 */
public class SDKRunner extends Runner {
    public static final  String IDE_GWT_XML_FILE_NAME    = "IDEPlatform.gwt.xml";
    public static final  String DEFAULT_SERVER_NAME      = "Tomcat";
    public static final  String DEBUG_TRANSPORT_PROTOCOL = "dt_socket";
    /** Rel for code server link. */
    public static final  String LINK_REL_CODE_SERVER     = "code server";
    /**
     * Name of configuration parameter that specifies the domain name or IP address of the code server.
     * If such parameter is not specified then <code>DEFAULT_BIND_ADDRESS</code> constant value will be used.
     */
    public static final  String CODE_SERVER_BIND_ADDRESS = "runner.sdk.code_server_bind_address";
    /** Specifies the default bind address for the code server. */
    private static final String DEFAULT_BIND_ADDRESS     = "localhost";
    private static final Logger LOG                      = LoggerFactory.getLogger(SDKRunner.class);
    private final Map<String, ApplicationServer> applicationServers;
    private final Path                           fsMountPointPath;

    public SDKRunner(Path fsMountPointPath) {
        applicationServers = new HashMap<>();
        this.fsMountPointPath = fsMountPointPath;
    }

    @Override
    public String getName() {
        return "sdk";
    }

    @Override
    public String getDescription() {
        return "Codenvy extensions runner";
    }

    @Override
    public void start() {
        super.start();
        for (ApplicationServer server : ComponentLoader.all(ApplicationServer.class)) {
            applicationServers.put(server.getName(), server);
        }
    }

    @Override
    public RunnerConfigurationFactory getRunnerConfigurationFactory() {
        return new RunnerConfigurationFactory() {
            @Override
            public RunnerConfiguration createRunnerConfiguration(RunRequest request) throws RunnerException {
                final Configuration myConfiguration = getConfiguration();
                final String codeServerBindAddress =
                        myConfiguration.get(CODE_SERVER_BIND_ADDRESS, DEFAULT_BIND_ADDRESS);

                final int codeServerPort = CustomPortService.getInstance().acquire();
                List<Link> links = new ArrayList<>(1);
                links.add(DtoFactory.getInstance().createDto(Link.class)
                                    .withRel(LINK_REL_CODE_SERVER)
                                    .withHref(codeServerBindAddress + ":" + codeServerPort));

                return new SDKRunnerConfiguration(DEFAULT_SERVER_NAME,
                                                  CustomPortService.getInstance().acquire(),
                                                  request.getMemorySize(), -1, false,
                                                  DEBUG_TRANSPORT_PROTOCOL, codeServerBindAddress, codeServerPort,
                                                  links, request);
            }
        };
    }

    @Override
    protected ApplicationProcess newApplicationProcess(final DeploymentSources toDeploy,
                                                       final RunnerConfiguration configuration) throws RunnerException {
        // It always should be SDKRunnerConfiguration.
        final SDKRunnerConfiguration sdkRunnerCfg = (SDKRunnerConfiguration)configuration;

        final ApplicationServer server = applicationServers.get(sdkRunnerCfg.getServer());
        if (server == null) {
            throw new RunnerException(String.format("Server %s not found", sdkRunnerCfg.getServer()));
        }

        Path projectSourcesPath = fsMountPointPath.resolve(sdkRunnerCfg.getRequest().getProject());
        if (!projectSourcesPath.isAbsolute()) {
            projectSourcesPath = projectSourcesPath.toAbsolutePath();
        }
        projectSourcesPath = projectSourcesPath.normalize();

        final java.io.File appDir;
        final Path codeServerWorkDirPath;
        final Utils.ExtensionDescriptor extension;
        try {
            appDir =
                    Files.createTempDirectory(getDeployDirectory().toPath(), (server.getName() + "_" + getName() + '_'))
                         .toFile();
            codeServerWorkDirPath =
                    Files.createTempDirectory(getDeployDirectory().toPath(), ("codeServer_" + getName() + '_'));
            extension = Utils.getExtensionFromJarFile(new ZipFile(toDeploy.getFile()));
        } catch (IOException e) {
            throw new RunnerException(e);
        }

        final ZipFile warFile = buildCodenvyWebAppWithExtension(extension);

        final CodeServer codeServer = new CodeServer();
        CodeServer.CodeServerProcess codeServerProcess =
                codeServer.prepare(codeServerWorkDirPath, sdkRunnerCfg, extension, projectSourcesPath, warFile);

        final ApplicationProcess process =
                server.deploy(appDir, warFile, sdkRunnerCfg, codeServerProcess,
                              new ApplicationServer.StopCallback() {
                                  @Override
                                  public void stopped() {
                                      CustomPortService.getInstance().release(sdkRunnerCfg.getPort());

                                      final int debugPort = sdkRunnerCfg.getDebugPort();
                                      if (debugPort > 0) {
                                          CustomPortService.getInstance().release(debugPort);
                                      }

                                      final int codeServerPort = sdkRunnerCfg.getCodeServerPort();
                                      if (codeServerPort > 0) {
                                          CustomPortService.getInstance().release(codeServerPort);
                                      }
                                  }
                              });

        registerDisposer(process, new Disposer() {
            @Override
            public void dispose() {
                if (!FileUtils.deleteRecursive(appDir)) {
                    LOG.error("Unable to remove app: {}", appDir);
                }

                if (!FileUtils.deleteRecursive(codeServerWorkDirPath.toFile(), false)) {
                    LOG.error("Unable to remove code server working directory: {}", codeServerWorkDirPath);
                }
            }
        });

        return process;
    }

    private ZipFile buildCodenvyWebAppWithExtension(Utils.ExtensionDescriptor extension) throws RunnerException {
        final ZipFile warPath;
        try {
            // prepare Codenvy Platform sources
            final Path workDirPath =
                    Files.createTempDirectory(getDeployDirectory().toPath(), ("war_" + getName() + '_'));
            ZipUtils.unzip(Utils.getCodenvyPlatformBinaryDistribution().openStream(), workDirPath.toFile());

            // integrate extension to Codenvy Platform
            MavenUtils.addDependencyToPom(workDirPath.resolve("pom.xml"), extension.groupId, extension.artifactId,
                                          extension.version);
            GwtXmlUtils.inheritGwtModule(MavenUtils.findFile(SDKRunner.IDE_GWT_XML_FILE_NAME, workDirPath),
                                         extension.gwtModuleName);

            warPath = buildWebAppAndGetWar(workDirPath);
        } catch (IOException e) {
            throw new RunnerException(e);
        }
        return warPath;
    }

    private ZipFile buildWebAppAndGetWar(Path appDirPath) throws RunnerException {
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

            return new ZipFile(MavenUtils.findFile("*.war", appDirPath.resolve("target")).toFile());
        } catch (IOException | InterruptedException e) {
            throw new RunnerException(e);
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