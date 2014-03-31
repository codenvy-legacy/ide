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

import com.codenvy.api.core.notification.EventService;
import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.api.core.util.CustomPortService;
import com.codenvy.api.core.util.LineConsumer;
import com.codenvy.api.core.util.ProcessUtil;
import com.codenvy.api.runner.RunnerException;
import com.codenvy.api.runner.internal.ApplicationProcess;
import com.codenvy.api.runner.internal.DeploymentSources;
import com.codenvy.api.runner.internal.Disposer;
import com.codenvy.api.runner.internal.ResourceAllocators;
import com.codenvy.api.runner.internal.Runner;
import com.codenvy.api.runner.internal.RunnerConfiguration;
import com.codenvy.api.runner.internal.RunnerConfigurationFactory;
import com.codenvy.api.runner.internal.dto.DebugMode;
import com.codenvy.api.runner.internal.dto.RunRequest;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
import com.codenvy.commons.lang.IoUtil;
import com.codenvy.commons.lang.ZipUtils;
import com.codenvy.dto.server.DtoFactory;
import com.codenvy.ide.commons.GwtXmlUtils;
import com.codenvy.ide.maven.tools.MavenUtils;
import com.codenvy.vfs.impl.fs.LocalFSMountStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipFile;

/**
 * Runner implementation to run Codenvy extensions by deploying it to application server.
 *
 * @author Artem Zatsarynnyy
 * @author Eugene Voevodin
 */
@Singleton
public class SDKRunner extends Runner {
    private static final Logger LOG = LoggerFactory.getLogger(SDKRunner.class);

    public static final String IDE_GWT_XML_FILE_NAME    = "IDEPlatform.gwt.xml";
    public static final String DEFAULT_SERVER_NAME      = "Tomcat";
    public static final String DEBUG_TRANSPORT_PROTOCOL = "dt_socket";
    /** Rel for code server link. */
    public static final String LINK_REL_CODE_SERVER     = "code server";
    /** Name of configuration parameter that specifies the domain name or IP address of the code server. */
    public static final String CODE_SERVER_BIND_ADDRESS = "runner.sdk.code_server_bind_address";

    private final Map<String, ApplicationServer> applicationServers;
    private final String                         codeServerBindAddress;
    private final String                         hostName;
    private final LocalFSMountStrategy           mountStrategy;
    private final CustomPortService              portService;

    @Inject
    public SDKRunner(@Named(DEPLOY_DIRECTORY) java.io.File deployDirectoryRoot,
                     @Named(CLEANUP_DELAY_TIME) int cleanupDelay,
                     @Named(CODE_SERVER_BIND_ADDRESS) String codeServerBindAddress,
                     @Named("runner.sdk.host_name") String hostName,
                     LocalFSMountStrategy mountStrategy,
                     CustomPortService portService,
                     Set<ApplicationServer> appServers,
                     ResourceAllocators allocators,
                     EventService eventService) {
        super(deployDirectoryRoot, cleanupDelay, allocators, eventService);
        this.codeServerBindAddress = codeServerBindAddress;
        this.hostName = hostName;
        this.mountStrategy = mountStrategy;
        this.portService = portService;
        applicationServers = new HashMap<>();
        //available application servers should be already injected
        for (ApplicationServer appServer : appServers) {
            applicationServers.put(appServer.getName(), appServer);
        }
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
    public RunnerConfigurationFactory getRunnerConfigurationFactory() {
        return new RunnerConfigurationFactory() {
            @Override
            public RunnerConfiguration createRunnerConfiguration(RunRequest request) throws RunnerException {
                final int httpPort = portService.acquire();
                final int codeServerPort = portService.acquire();
                final SDKRunnerConfiguration configuration = new SDKRunnerConfiguration(DEFAULT_SERVER_NAME,
                                                                                        request.getMemorySize(),
                                                                                        httpPort,
                                                                                        codeServerBindAddress,
                                                                                        codeServerPort,
                                                                                        request);
                configuration.getLinks().add(DtoFactory.getInstance().createDto(Link.class).withRel("web url")
                                                       .withHref(String.format("http://%s:%d/%s", hostName, httpPort, "ide/default")));
                configuration.getLinks().add(DtoFactory.getInstance().createDto(Link.class)
                                                       .withRel(LINK_REL_CODE_SERVER)
                                                       .withHref(String.format("%s:%d", codeServerBindAddress, codeServerPort)));
                final DebugMode debugMode = request.getDebugMode();
                if (debugMode != null && debugMode.getMode() != null) {
                    configuration.setDebugHost(hostName);
                    configuration.setDebugPort(portService.acquire());
                    configuration.setDebugTransport(DEBUG_TRANSPORT_PROTOCOL);
                    configuration.setDebugSuspend("suspend".equals(debugMode.getMode()));
                }
                return configuration;
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

        final java.io.File appDir;
        final Path codeServerWorkDirPath;
        final Utils.ExtensionDescriptor extension;
        try {
            appDir = Files.createTempDirectory(getDeployDirectory().toPath(), (server.getName() + '_' + getName() + '_')).toFile();
            codeServerWorkDirPath = Files.createTempDirectory(getDeployDirectory().toPath(), ("codeServer_" + getName() + '_'));
            extension = Utils.getExtensionFromJarFile(new ZipFile(toDeploy.getFile()));
        } catch (IOException e) {
            throw new RunnerException(e);
        }

        final String workspace = sdkRunnerCfg.getRequest().getWorkspace();
        final String project = sdkRunnerCfg.getRequest().getProject().substring(1);
        final Path projectSourcesPath;
        try {
            projectSourcesPath = mountStrategy.getMountPath(workspace).toPath().resolve(project);
        } catch (VirtualFileSystemException e) {
            throw new RunnerException(e);
        }
        CodeServer codeServer = new CodeServer();
        CodeServer.CodeServerProcess codeServerProcess = codeServer.prepare(codeServerWorkDirPath,
                                                                            projectSourcesPath,
                                                                            sdkRunnerCfg,
                                                                            extension);
        final ZipFile warFile = buildCodenvyWebAppWithExtension(extension);
        final ApplicationProcess process =
                server.deploy(appDir, warFile, sdkRunnerCfg, codeServerProcess,
                              new ApplicationServer.StopCallback() {
                                  @Override
                                  public void stopped() {
                                      portService.release(sdkRunnerCfg.getHttpPort());

                                      final int debugPort = sdkRunnerCfg.getDebugPort();
                                      if (debugPort > 0) {
                                          portService.release(debugPort);
                                      }

                                      final int codeServerPort = sdkRunnerCfg.getCodeServerPort();
                                      if (codeServerPort > 0) {
                                          portService.release(codeServerPort);
                                      }
                                  }
                              });

        registerDisposer(process, new Disposer() {
            @Override
            public void dispose() {
                if (!IoUtil.deleteRecursive(appDir)) {
                    LOG.error("Unable to remove app: {}", appDir);
                }

                if (!IoUtil.deleteRecursive(codeServerWorkDirPath.toFile(), false)) {
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
            final Path workDirPath = Files.createTempDirectory(getDeployDirectory().toPath(), ("war_" + getName() + '_'));
            ZipUtils.unzip(Utils.getCodenvyPlatformBinaryDistribution().openStream(), workDirPath.toFile());

            // integrate extension to Codenvy Platform
            MavenUtils.addDependency(workDirPath.resolve("pom.xml").toFile(),
                                     extension.groupId,
                                     extension.artifactId,
                                     extension.version,
                                     null);
            GwtXmlUtils.inheritGwtModule(IoUtil.findFile(SDKRunner.IDE_GWT_XML_FILE_NAME, workDirPath.toFile()).toPath(),
                                         extension.gwtModuleName);

            warPath = buildWebAppAndGetWar(workDirPath);
        } catch (IOException e) {
            throw new RunnerException(e);
        }
        return warPath;
    }

    private ZipFile buildWebAppAndGetWar(Path appDirPath) throws RunnerException {
        final String[] command = new String[]{MavenUtils.getMavenExecCommand(), "package"};
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command).directory(appDirPath.toFile());
            Process process = processBuilder.start();
            ProcessLineConsumer consumer = new ProcessLineConsumer();
            ProcessUtil.process(process, consumer, consumer);
            process.waitFor();
            if (process.exitValue() != 0) {
                throw new RunnerException(consumer.getOutput().toString());
            }
            return new ZipFile(IoUtil.findFile("*.war", appDirPath.resolve("target").toFile()));
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