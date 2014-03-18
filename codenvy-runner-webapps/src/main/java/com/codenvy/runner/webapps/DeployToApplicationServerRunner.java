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

import com.codenvy.api.core.notification.EventService;
import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.api.core.util.CustomPortService;
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
import com.codenvy.commons.lang.IoUtil;
import com.codenvy.dto.server.DtoFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Runner implementation to run Java web applications by deploying it to application server.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class DeployToApplicationServerRunner extends Runner {
    private static final Logger LOG = LoggerFactory.getLogger(DeployToApplicationServerRunner.class);

    public static final String DEFAULT_SERVER_NAME      = "Tomcat";
    public static final String DEBUG_TRANSPORT_PROTOCOL = "dt_socket";

    private final Map<String, ApplicationServer> servers;
    private final String                         hostName;
    private final CustomPortService              portService;

    @Inject
    public DeployToApplicationServerRunner(@Named(DEPLOY_DIRECTORY) java.io.File deployDirectoryRoot,
                                           @Named(CLEANUP_DELAY_TIME) int cleanupDelay,
                                           @Named("runner.java_webapp.host_name") String hostName,
                                           ResourceAllocators allocators,
                                           CustomPortService portService,
                                           Set<ApplicationServer> serverSet,
                                           EventService eventService) {
        super(deployDirectoryRoot, cleanupDelay, allocators, eventService);
        this.hostName = hostName;
        this.portService = portService;
        this.servers = new HashMap<>();
        for (ApplicationServer server : serverSet) {
            this.servers.put(server.getName(), server);
        }
    }

    @Override
    public String getName() {
        return "webapps";
    }

    @Override
    public String getDescription() {
        return "Deploy to application server runner";
    }

    @Override
    public RunnerConfigurationFactory getRunnerConfigurationFactory() {
        return new RunnerConfigurationFactory() {
            @Override
            public RunnerConfiguration createRunnerConfiguration(RunRequest request) throws RunnerException {
                final int httpPort = portService.acquire();
                final ApplicationServerRunnerConfiguration configuration =
                        new ApplicationServerRunnerConfiguration(DEFAULT_SERVER_NAME, request.getMemorySize(), httpPort, request);
                configuration.getLinks().add(DtoFactory.getInstance().createDto(Link.class).withRel("web url")
                                                       .withHref(String.format("http://%s:%d", hostName, httpPort)));
                final DebugMode debugMode = request.getDebugMode();
                if (debugMode != null) {
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
        // It always should be ApplicationServerRunnerConfiguration.
        final ApplicationServerRunnerConfiguration webAppsRunnerCfg = (ApplicationServerRunnerConfiguration)configuration;
        final ApplicationServer server = servers.get(webAppsRunnerCfg.getServer());
        if (server == null) {
            throw new RunnerException(String.format("Server %s not found", webAppsRunnerCfg.getServer()));
        }

        final java.io.File appDir;
        try {
            appDir = Files.createTempDirectory(getDeployDirectory().toPath(), (server.getName() + '_' + getName() + '_')).toFile();
        } catch (IOException e) {
            throw new RunnerException(e);
        }

        final ApplicationProcess process =
                server.deploy(appDir, toDeploy, webAppsRunnerCfg, new ApplicationServer.StopCallback() {
                    @Override
                    public void stopped() {
                        portService.release(webAppsRunnerCfg.getHttpPort());
                        final int debugPort = webAppsRunnerCfg.getDebugPort();
                        if (debugPort > 0) {
                            portService.release(debugPort);
                        }
                    }
                });

        registerDisposer(process, new Disposer() {
            @Override
            public void dispose() {
                if (!IoUtil.deleteRecursive(appDir)) {
                    LOG.error("Unable to remove app: {}", appDir);
                }
            }
        });

        return process;
    }
}