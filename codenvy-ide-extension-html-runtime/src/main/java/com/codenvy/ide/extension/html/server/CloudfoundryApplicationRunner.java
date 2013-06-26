/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.extension.html.server;

import com.codenvy.ide.commons.server.ParsingResponseException;
import com.codenvy.ide.extension.html.shared.ApplicationInstance;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.ide.extension.cloudfoundry.server.Cloudfoundry;
import org.exoplatform.ide.extension.cloudfoundry.server.CloudfoundryException;
import org.exoplatform.ide.extension.cloudfoundry.server.ext.CloudfoundryPool;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import org.exoplatform.ide.security.paas.CredentialStoreException;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.picocontainer.Startable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.codenvy.ide.commons.server.ContainerUtils.readValueParam;
import static com.codenvy.commons.lang.IoUtil.createTempDirectory;
import static com.codenvy.commons.lang.IoUtil.deleteRecursive;
import static com.codenvy.commons.lang.NameGenerator.generate;
import static com.codenvy.commons.lang.ZipUtils.unzip;

/**
 * ApplicationRunner for deploy HTML applications at CloudFoundry.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: CloudfoundryApplicationRunner.java Jun 26, 2013 11:13:53 AM azatsarynnyy $
 *
 */
public class CloudfoundryApplicationRunner implements ApplicationRunner, Startable {
    /** Default application lifetime (in minutes). After this time application may be stopped automatically. */
    private static final int               DEFAULT_APPLICATION_LIFETIME = 10;

    private static final Log               LOG                          = ExoLogger.getLogger(CloudfoundryApplicationRunner.class);

    private final int                      applicationLifetime;
    private final long                     applicationLifetimeMillis;

    private final CloudfoundryPool         cfServers;

    private final Map<String, Application> applications;
    private final ScheduledExecutorService applicationTerminator;

    public CloudfoundryApplicationRunner(CloudfoundryPool cfServers, InitParams initParams) {
        this(cfServers, parseApplicationLifeTime(readValueParam(initParams, "cloudfoundry-application-lifetime")));
    }

    private static int parseApplicationLifeTime(String str) {
        if (str != null) {
            try {
                return Integer.parseInt(str);
            } catch (NumberFormatException ignored) {
            }
        }
        return DEFAULT_APPLICATION_LIFETIME;
    }

    protected CloudfoundryApplicationRunner(CloudfoundryPool cfServers, int applicationLifetime) {
        if (applicationLifetime < 1) {
            throw new IllegalArgumentException("Invalid application lifetime: " + 1);
        }
        this.applicationLifetime = applicationLifetime;
        this.applicationLifetimeMillis = applicationLifetime * 60 * 1000;
        this.cfServers = cfServers;

        this.applications = new ConcurrentHashMap<String, Application>();
        this.applicationTerminator = Executors.newSingleThreadScheduledExecutor();
        this.applicationTerminator.scheduleAtFixedRate(new TerminateApplicationTask(), 1, 1, TimeUnit.MINUTES);
    }

    @Override
    public ApplicationInstance runApplication(VirtualFileSystem vfs, String projectId) throws ApplicationRunnerException,
                                                                                      VirtualFileSystemException {
        java.io.File path = null;
        try {
            Item project = vfs.getItem(projectId, false, PropertyFilter.NONE_FILTER);
            if (project.getItemType() != ItemType.PROJECT) {
                throw new ApplicationRunnerException("Item '" + project.getPath() + "' is not a project. ");
            }
            path = createTempDirectory(null, "app-html-");
            unzip(vfs.exportZip(projectId).getStream(), path);
            java.io.File projectFile = new java.io.File(path, ".project");
            if (projectFile.exists()) {
                projectFile.delete(); // Do not send .project file to CF.
            }

            final Cloudfoundry cloudfoundry = cfServers.next();
            final String name = generate("app-", 16);
            try {
                return doRunApplication(cloudfoundry, name, path, project.getName());
            } catch (ApplicationRunnerException e) {
                Throwable cause = e.getCause();
                if (cause instanceof CloudfoundryException) {
                    if (200 == ((CloudfoundryException)cause).getExitCode()) {
                        // Login and try again.
                        login(cloudfoundry);
                        return doRunApplication(cloudfoundry, name, path, project.getName());
                    }
                }
                throw e;
            }
        } catch (IOException e) {
            throw new ApplicationRunnerException(e.getMessage(), e);
        } finally {
            if (path != null && path.exists()) {
                deleteRecursive(path);
            }
        }
    }

    private ApplicationInstance doRunApplication(Cloudfoundry cloudfoundry,
                                                 String name,
                                                 java.io.File appDir,
                                                 String projectName) throws ApplicationRunnerException {
        try {
            final String target = cloudfoundry.getTarget();
            final CloudFoundryApplication cfApp = createApplication(cloudfoundry, target, name, appDir);
            final long expired = System.currentTimeMillis() + applicationLifetimeMillis;

            applications.put(name, new Application(name, target, expired, projectName));
            LOG.debug("Start application {} at CF server {}", name, target);
            LOG.info("EVENT#run-started# PROJECT#" + projectName + "# TYPE#HTML#");
            LOG.info("EVENT#project-deployed# PROJECT#" + projectName + "# TYPE#HTML# PAAS#LOCAL#");
            return new ApplicationInstanceImpl(name, cfApp.getUris().get(0), null, applicationLifetime);
        } catch (Exception e) {

            String logs = safeGetLogs(cloudfoundry, name);

            // try to remove application.
            try {
                LOG.warn("Application {} failed to start, cause: {}", name, e.getMessage());
                cloudfoundry.deleteApplication(cloudfoundry.getTarget(), name, null, null, "cloudfoundry", true);
            } catch (Exception e1) {
                LOG.warn("Unable delete failed application {}, cause: {}", name, e.getMessage());
            }

            throw new ApplicationRunnerException(e.getMessage(), e, logs);
        }
    }

    /**
     * Get applications logs and hide any errors. This method is used for getting logs of failed application to help user understand what is
     * going wrong.
     */
    private String safeGetLogs(Cloudfoundry cloudfoundry, String name) {
        try {
            return cloudfoundry.getLogs(cloudfoundry.getTarget(), name, "0", null, null);
        } catch (Exception e) {
            // Not able show log if any errors occurs.
            return null;
        }
    }

    @Override
    public String getLogs(String name) throws ApplicationRunnerException {
        Application application = applications.get(name);
        if (application != null) {
            Cloudfoundry cloudfoundry = cfServers.byTargetName(application.server);
            if (cloudfoundry != null) {
                try {
                    return doGetLogs(cloudfoundry, name);
                } catch (ApplicationRunnerException e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof CloudfoundryException) {
                        if (200 == ((CloudfoundryException)cause).getExitCode()) {
                            login(cloudfoundry);
                            return doGetLogs(cloudfoundry, name);
                        }
                    }
                    throw e;
                }
            } else {
                throw new ApplicationRunnerException("Unable get logs. Server not available. ");
            }
        } else {
            throw new ApplicationRunnerException("Unable get logs. Application '" + name + "' not found. ");
        }
    }

    private String doGetLogs(Cloudfoundry cloudfoundry, String name) throws ApplicationRunnerException {
        try {
            return cloudfoundry.getLogs(cloudfoundry.getTarget(), name, "0", null, null);
        } catch (Exception e) {
            throw new ApplicationRunnerException(e.getMessage(), e);
        }
    }

    @Override
    public void stopApplication(String name) throws ApplicationRunnerException {
        Application application = applications.get(name);
        if (application != null) {
            Cloudfoundry cloudfoundry = cfServers.byTargetName(application.server);
            if (cloudfoundry != null) {
                try {
                    doStopApplication(cloudfoundry, name);
                } catch (ApplicationRunnerException e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof CloudfoundryException) {
                        if (200 == ((CloudfoundryException)cause).getExitCode()) {
                            login(cloudfoundry);
                            doStopApplication(cloudfoundry, name);
                        }
                    }
                    throw e;
                }
            } else {
                throw new ApplicationRunnerException("Unable stop application. Server not available. ");
            }
        } else {
            throw new ApplicationRunnerException("Unable stop application. Application '" + name + "' not found. ");
        }
    }

    private void doStopApplication(Cloudfoundry cloudfoundry, String name) throws ApplicationRunnerException {
        try {
            String target = cloudfoundry.getTarget();
            cloudfoundry.stopApplication(target, name, null, null, "cloudfoundry");
            cloudfoundry.deleteApplication(target, name, null, null, "cloudfoundry", true);
            LOG.debug("Stop application {}.", name);
            LOG.info("EVENT#run-finished# PROJECT#" + applications.get(name).projectName + "# TYPE#HTML#");
            applications.remove(name);
        } catch (Exception e) {
            throw new ApplicationRunnerException(e.getMessage(), e);
        }
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
        applicationTerminator.shutdownNow();
        for (Application app : applications.values()) {
            try {
                stopApplication(app.name);
            } catch (ApplicationRunnerException e) {
                LOG.error("Failed to stop application {}.", app.name, e);
            }
        }
        applications.clear();
    }

    private CloudFoundryApplication createApplication(Cloudfoundry cloudfoundry,
                                                      String target,
                                                      String name,
                                                      java.io.File path)
                                                                        throws CloudfoundryException,
                                                                        IOException,
                                                                        ParsingResponseException,
                                                                        VirtualFileSystemException,
                                                                        CredentialStoreException {
        return cloudfoundry.createApplication(target, name, "html", null, 1, 128, false, "html", null, null, null,
                                              null, path.toURI().toURL(), null);
    }

    private void login(Cloudfoundry cloudfoundry) throws ApplicationRunnerException {
        try {
            cloudfoundry.login();
        } catch (Exception e) {
            throw new ApplicationRunnerException(e.getMessage(), e);
        }
    }

    private class TerminateApplicationTask implements Runnable {
        @Override
        public void run() {
            List<String> stopped = new ArrayList<String>();
            for (Application app : applications.values()) {
                if (app.isExpired()) {
                    try {
                        stopApplication(app.name);
                    } catch (ApplicationRunnerException e) {
                        LOG.error("Failed to stop application {}.", app.name, e);
                    }
                    // Do not try to stop application twice.
                    stopped.add(app.name);
                }
            }
            applications.keySet().removeAll(stopped);
            LOG.debug("{} applications removed. ", stopped.size());
        }
    }

    private static class Application {
        final String name;
        final String server;
        final String projectName;
        final long   expirationTime;

        Application(String name, String server, long expirationTime, String projectName) {
            this.name = name;
            this.server = server;
            this.expirationTime = expirationTime;
            this.projectName = projectName;
        }

        boolean isExpired() {
            return expirationTime < System.currentTimeMillis();
        }
    }
}
