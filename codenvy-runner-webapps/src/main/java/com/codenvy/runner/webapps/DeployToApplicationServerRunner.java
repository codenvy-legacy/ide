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

import com.codenvy.api.core.util.CustomPortService;
import com.codenvy.api.runner.RunnerException;
import com.codenvy.api.runner.internal.ApplicationProcess;
import com.codenvy.api.runner.internal.DeploymentSources;
import com.codenvy.api.runner.internal.Disposer;
import com.codenvy.api.runner.internal.ResourceAllocators;
import com.codenvy.api.runner.internal.Runner;
import com.codenvy.api.runner.internal.RunnerConfiguration;
import com.codenvy.api.runner.internal.RunnerConfigurationFactory;
import com.codenvy.api.runner.internal.dto.RunRequest;
import com.codenvy.commons.lang.IoUtil;
import com.codenvy.inject.ConfigurationParameter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * Runner implementation to run Java web applications by deploying it to application server.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 */
@Singleton
public class DeployToApplicationServerRunner extends Runner {
    private static final Logger LOG = LoggerFactory.getLogger(DeployToApplicationServerRunner.class);

    public static final String DEFAULT_SERVER_NAME      = "Tomcat";
    public static final String DEBUG_TRANSPORT_PROTOCOL = "dt_socket";

    private final Map<String, ApplicationServer> applicationServers;
    private final CustomPortService              portService;

    @Inject
    public DeployToApplicationServerRunner(@Named(DEPLOY_DIRECTORY) ConfigurationParameter deployDirectoryPath,
                                           @Named(CLEANUP_DELAY_TIME) ConfigurationParameter cleanupDelay,
                                           ResourceAllocators allocators,
                                           CustomPortService portService) {
        this(deployDirectoryPath.asFile(), cleanupDelay.asInt(), allocators, portService);
    }

    public DeployToApplicationServerRunner(java.io.File deployDirectoryRoot,
                                           int cleanupDelay,
                                           ResourceAllocators allocators,
                                           CustomPortService portService) {
        super(deployDirectoryRoot, cleanupDelay, allocators);
        this.portService = portService;
        applicationServers = new HashMap<>();
    }

    @Override
    public String getName() {
        return "webapps";
    }

    @Override
    public String getDescription() {
        return "Java Web Applications Runner";
    }

    @Override
    public void start() {
        super.start();
        for (ApplicationServer server : ServiceLoader.load(ApplicationServer.class)) {
            applicationServers.put(server.getName(), server);
        }
    }

    @Override
    public RunnerConfigurationFactory getRunnerConfigurationFactory() {
        return new RunnerConfigurationFactory() {
            @Override
            public RunnerConfiguration createRunnerConfiguration(RunRequest request) throws RunnerException {
                return new ApplicationServerRunnerConfiguration(DEFAULT_SERVER_NAME,
                                                                portService.acquire(),
                                                                request.getMemorySize(),
                                                                -1,
                                                                false,
                                                                DEBUG_TRANSPORT_PROTOCOL,
                                                                request);
            }
        };
    }

    @Override
    protected ApplicationProcess newApplicationProcess(final DeploymentSources toDeploy,
                                                       final RunnerConfiguration configuration) throws RunnerException {
        // It always should be ApplicationServerRunnerConfiguration.
        final ApplicationServerRunnerConfiguration webAppsRunnerCfg =
                (ApplicationServerRunnerConfiguration)configuration;
        final ApplicationServer server = applicationServers.get(webAppsRunnerCfg.getServer());
        if (server == null) {
            throw new RunnerException(String.format("Server %s not found", webAppsRunnerCfg.getServer()));
        }

        final java.io.File appDir;
        try {
            appDir =
                    Files.createTempDirectory(getDeployDirectory().toPath(), (server.getName() + "_" + getName() + '_'))
                         .toFile();
        } catch (IOException e) {
            throw new RunnerException(e);
        }

        final ApplicationProcess process =
                server.deploy(appDir, toDeploy, webAppsRunnerCfg, new ApplicationServer.StopCallback() {
                    @Override
                    public void stopped() {
                        portService.release(webAppsRunnerCfg.getPort());
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