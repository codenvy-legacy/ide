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

import com.codenvy.api.core.util.ComponentLoader;
import com.codenvy.api.core.util.CustomPortService;
import com.codenvy.api.runner.RunnerException;
import com.codenvy.api.runner.internal.*;
import com.codenvy.api.runner.internal.dto.RunRequest;
import com.codenvy.commons.lang.IoUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * //
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 */
public class DeployToApplicationServerRunner extends Runner {
    public static final  String DEFAULT_SERVER_NAME      = "Tomcat";
    public static final  String DEBUG_TRANSPORT_PROTOCOL = "dt_socket";
    private static final Logger LOG                      =
            LoggerFactory.getLogger(DeployToApplicationServerRunner.class);
    private final Map<String, ApplicationServer> applicationServers;

    public DeployToApplicationServerRunner() {
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
        for (ApplicationServer server : ComponentLoader.all(ApplicationServer.class)) {
            applicationServers.put(server.getName(), server);
        }
    }

    @Override
    public RunnerConfigurationFactory getRunnerConfigurationFactory() {
        return new RunnerConfigurationFactory() {
            @Override
            public RunnerConfiguration createRunnerConfiguration(RunRequest request) throws RunnerException {
                return new ApplicationServerRunnerConfiguration(DEFAULT_SERVER_NAME,
                                                                CustomPortService.getInstance().acquire(),
                                                                request.getMemorySize(), 0, false,
                                                                DEBUG_TRANSPORT_PROTOCOL, request);
            }
        };
    }

    @Override
    protected ApplicationProcess newApplicationProcess(final DeploymentSources toDeploy,
                                                       final RunnerConfiguration configuration) throws RunnerException {
        // It always should be ApplicationServerRunnerConfiguration.
        final ApplicationServerRunnerConfiguration webAppsRunnerCfg =
                (ApplicationServerRunnerConfiguration)configuration;
        final java.io.File appDir;
        try {
            appDir = Files.createTempDirectory(getDeployDirectory().toPath(), ("app_" + getName() + '_')).toFile();
        } catch (IOException e) {
            throw new RunnerException(e);
        }
        final ApplicationServer server = applicationServers.get(webAppsRunnerCfg.getServer());
        if (server == null) {
            throw new RunnerException(String.format("Server %s not found", webAppsRunnerCfg.getServer()));
        }

        final StopCallback stopCallback = new StopCallback() {
            @Override
            public void stopped() {
                CustomPortService.getInstance().release(webAppsRunnerCfg.getPort());
                final int debugPort = webAppsRunnerCfg.getDebugPort();
                if (debugPort > 0) {
                    CustomPortService.getInstance().release(debugPort);
                }
                IoUtil.deleteRecursive(appDir);
                LOG.debug("stop {} at port {}, application {}",
                          new Object[]{webAppsRunnerCfg.getServer(), webAppsRunnerCfg.getPort(), appDir});
            }
        };

        final ApplicationProcess process = server.deploy(appDir, toDeploy, webAppsRunnerCfg, stopCallback);

        registerDisposer(process, new Disposer() {
            @Override
            public void dispose() {
                CustomPortService.getInstance().release(webAppsRunnerCfg.getPort());
                final int debugPort = webAppsRunnerCfg.getDebugPort();
                if (debugPort > 0) {
                    CustomPortService.getInstance().release(debugPort);
                }
                IoUtil.deleteRecursive(appDir);
                LOG.debug("stop {} at port {}, application {}",
                          new Object[]{webAppsRunnerCfg.getServer(), webAppsRunnerCfg.getPort(), appDir});
            }
        });
        return process;
    }
}