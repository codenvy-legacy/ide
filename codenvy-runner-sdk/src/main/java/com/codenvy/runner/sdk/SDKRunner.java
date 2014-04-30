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
import com.codenvy.api.project.server.ProjectEventService;
import com.codenvy.api.runner.RunnerException;
import com.codenvy.api.runner.dto.DebugMode;
import com.codenvy.api.runner.dto.RunRequest;
import com.codenvy.api.runner.dto.RunnerEnvironment;
import com.codenvy.api.runner.internal.ApplicationProcess;
import com.codenvy.api.runner.internal.Constants;
import com.codenvy.api.runner.internal.DeploymentSources;
import com.codenvy.api.runner.internal.Disposer;
import com.codenvy.api.runner.internal.ResourceAllocators;
import com.codenvy.api.runner.internal.Runner;
import com.codenvy.api.runner.internal.RunnerConfiguration;
import com.codenvy.api.runner.internal.RunnerConfigurationFactory;
import com.codenvy.commons.lang.IoUtil;
import com.codenvy.commons.lang.ZipUtils;
import com.codenvy.dto.server.DtoFactory;
import com.codenvy.ide.commons.GwtXmlUtils;
import com.codenvy.ide.maven.tools.MavenUtils;

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
    public static final String DEFAULT_SERVER_NAME      = "Tomcat7";
    public static final String DEBUG_TRANSPORT_PROTOCOL = "dt_socket";
    /** Rel for code server link. */
    public static final String LINK_REL_CODE_SERVER     = "code server";
    /** Name of configuration parameter that specifies the domain name or IP address of the code server. */
    public static final String CODE_SERVER_BIND_ADDRESS = "runner.sdk.code_server_bind_address";
    public static final String HOST_NAME                = "runner.sdk.host_name";

    private final Map<String, ApplicationServer> servers;
    private final Map<String, RunnerEnvironment> environments;
    private final String                         codeServerAddress;
    private final String                         hostName;
    private final CustomPortService              portService;
    private final CodeServer                     codeServer;
    private final ProjectEventService            projectEventService;

    @Inject
    public SDKRunner(@Named(Constants.DEPLOY_DIRECTORY) java.io.File deployDirectoryRoot,
                     @Named(Constants.APP_CLEANUP_TIME) int cleanupTime,
                     @Named(CODE_SERVER_BIND_ADDRESS) String codeServerAddress,
                     @Named(HOST_NAME) String hostName,
                     CustomPortService portService,
                     Set<ApplicationServer> appServers,
                     CodeServer codeServer,
                     ResourceAllocators allocators,
                     EventService eventService,
                     ProjectEventService projectEventService) {
        super(deployDirectoryRoot, cleanupTime, allocators, eventService);
        this.codeServerAddress = codeServerAddress;
        this.hostName = hostName;
        this.portService = portService;
        this.codeServer = codeServer;
        this.projectEventService = projectEventService;
        this.servers = new HashMap<>();
        //available application servers should be already injected
        for (ApplicationServer appServer : appServers) {
            servers.put(appServer.getName(), appServer);
        }
        this.environments = new HashMap<>(servers.size());
        final DtoFactory dtoFactory = DtoFactory.getInstance();
        for (ApplicationServer server : appServers) {
            final RunnerEnvironment runnerEnvironment = dtoFactory.createDto(RunnerEnvironment.class)
                                                                  .withId(server.getName())
                                                                  .withDescription(server.getDescription())
                                                                  .withIsDefault(DEFAULT_SERVER_NAME.equals(server.getName()));
            this.environments.put(runnerEnvironment.getId(), runnerEnvironment);
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
    public Map<String, RunnerEnvironment> getEnvironments() {
        final Map<String, RunnerEnvironment> copy = new HashMap<>(environments.size());
        final DtoFactory dtoFactory = DtoFactory.getInstance();
        for (Map.Entry<String, RunnerEnvironment> entry : environments.entrySet()) {
            copy.put(entry.getKey(), dtoFactory.clone(entry.getValue()));
        }
        return copy;
    }

    @Override
    public RunnerConfigurationFactory getRunnerConfigurationFactory() {
        return new RunnerConfigurationFactory() {
            @Override
            public RunnerConfiguration createRunnerConfiguration(RunRequest request) throws RunnerException {
                final int httpPort = portService.acquire();
                final int codeServerPort = portService.acquire();
                String server = request.getEnvironmentId();
                if (server == null) {
                    server = DEFAULT_SERVER_NAME;
                }
                final SDKRunnerConfiguration configuration =
                        new SDKRunnerConfiguration(server, request.getMemorySize(), httpPort, codeServerAddress, codeServerPort, request);
                configuration.getLinks().add(DtoFactory.getInstance().createDto(Link.class)
                                                       .withRel(com.codenvy.api.runner.internal.Constants.LINK_REL_WEB_URL)
                                                       .withHref(String.format("http://%s:%d/%s", hostName, httpPort, "ide/default")));
                configuration.getLinks().add(DtoFactory.getInstance().createDto(Link.class)
                                                       .withRel(LINK_REL_CODE_SERVER)
                                                       .withHref(String.format("%s:%d", codeServerAddress, codeServerPort)));
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

        final ApplicationServer server = servers.get(sdkRunnerCfg.getServer());
        if (server == null) {
            throw new RunnerException(String.format("Server %s not found", sdkRunnerCfg.getServer()));
        }

        final java.io.File appDir;
        final Path codeServerWorkDirPath;
        final Utils.ExtensionDescriptor extensionDescriptor;
        try {
            appDir = Files.createTempDirectory(getDeployDirectory().toPath(), (server.getName() + '_' + getName() + '_')).toFile();
            codeServerWorkDirPath = Files.createTempDirectory(getDeployDirectory().toPath(), ("codeServer_" + getName() + '_'));
            extensionDescriptor = Utils.getExtensionFromJarFile(new ZipFile(toDeploy.getFile()));
        } catch (IOException | IllegalArgumentException e) {
            throw new RunnerException(e);
        }

        final CodeServer.CodeServerProcess codeServerProcess = codeServer.prepare(codeServerWorkDirPath,
                                                                                  sdkRunnerCfg,
                                                                                  extensionDescriptor,
                                                                                  getExecutor());
        final String workspace = sdkRunnerCfg.getRequest().getWorkspace();
        final String project = sdkRunnerCfg.getRequest().getProject();
        // Register an appropriate ProjectEventListener in order
        // to provide mirror of a remote project for GWT code server.
        projectEventService.addListener(workspace, project, codeServerProcess);

        final ZipFile warFile = buildCodenvyWebAppWithExtension(extensionDescriptor);

        final ApplicationProcess process =
                server.deploy(appDir, warFile, toDeploy.getFile(), sdkRunnerCfg, codeServerProcess,
                              new ApplicationProcess.Callback() {
                                  @Override
                                  public void started() {
                                  }

                                  @Override
                                  public void stopped() {
                                      // stop tracking changes in remote project since code server is stopped
                                      projectEventService.removeListener(workspace, project, codeServerProcess);
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
                              }
                             );

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

            warPath = Utils.buildProjectFromSources(workDirPath, "*.war");
        } catch (Exception e) {
            throw new RunnerException(e);
        }
        return warPath;
    }

}