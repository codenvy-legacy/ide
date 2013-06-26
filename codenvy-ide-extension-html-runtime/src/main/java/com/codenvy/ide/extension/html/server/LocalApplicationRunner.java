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

import com.codenvy.ide.extension.html.shared.ApplicationInstance;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.codenvy.commons.lang.NameGenerator.generate;
import static com.codenvy.ide.commons.server.ContainerUtils.readValueParam;

/**
 * {@link ApplicationRunner} for run HTML applications at ...
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: LocalApplicationRunner.java Jun 26, 2013 1:12:02 PM azatsarynnyy $
 */
public class LocalApplicationRunner implements ApplicationRunner {
    /** Default application lifetime (in minutes). After this time application may be stopped automatically. */
    private static final int               DEFAULT_APPLICATION_LIFETIME = 10;

    private static final Log               LOG                          = ExoLogger.getLogger(LocalApplicationRunner.class);

    private static final String            DEFAULT_SERVER               = "https://www.codenvy.com/ide/";

    private final int                      applicationLifetime;
    private final long                     applicationLifetimeMillis;

    private final Map<String, Application> applications;
    private final ScheduledExecutorService applicationTerminator;

    public LocalApplicationRunner(InitParams initParams) {
        this(parseApplicationLifeTime(readValueParam(initParams, "html-application-lifetime")));
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

    protected LocalApplicationRunner(int applicationLifetime) {
        if (applicationLifetime < 1) {
            throw new IllegalArgumentException("Invalid application lifetime: " + 1);
        }
        this.applicationLifetime = applicationLifetime;
        this.applicationLifetimeMillis = applicationLifetime * 60 * 1000;

        this.applications = new ConcurrentHashMap<String, Application>();
        this.applicationTerminator = Executors.newSingleThreadScheduledExecutor();
        this.applicationTerminator.scheduleAtFixedRate(new TerminateApplicationTask(), 1, 1, TimeUnit.MINUTES);
    }

    @Override
    public ApplicationInstance runApplication(String wsName, VirtualFileSystem vfs, String projectId) throws ApplicationRunnerException,
                                                                                      VirtualFileSystemException {
        Item project = vfs.getItem(projectId, false, PropertyFilter.NONE_FILTER);
        if (project.getItemType() != ItemType.PROJECT) {
            throw new ApplicationRunnerException("Item '" + project.getPath() + "' is not a project. ");
        }

        final String name = generate("app-", 16);
        final String host = DEFAULT_SERVER + wsName + "/_appruner/" + name;
        final long expired = System.currentTimeMillis() + applicationLifetimeMillis;

        applications.put(name, new Application(name, expired, project.getName(), vfs.getInfo().getId(), projectId));
        LOG.info("EVENT#run-started# PROJECT#" + project.getName() + "# TYPE#HTML#");
        LOG.info("EVENT#project-deployed# PROJECT#" + project.getName() + "# TYPE#HTML# PAAS#LOCAL#");
        return new ApplicationInstanceImpl(name, host, null, applicationLifetime);
    }

    @Override
    public void stopApplication(String name) throws ApplicationRunnerException {
        Application application = applications.get(name);
        if (application != null) {
            try {
                LOG.debug("Stop application {}.", name);
                LOG.info("EVENT#run-finished# PROJECT#" + applications.get(name).projectName + "# TYPE#HTML#");
                applications.remove(name);
            } catch (Exception e) {
                throw new ApplicationRunnerException(e.getMessage(), e);
            }
        } else {
            throw new ApplicationRunnerException("Unable to stop application. Application '" + name + "' not found. ");
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
        final String projectName;
        final long  expirationTime;
        final String vfsId;
        final String projectId;

        Application(String name, long expirationTime, String projectName, String vfsId, String projectId) {
            this.name = name;
            this.expirationTime = expirationTime;
            this.projectName = projectName;
            this.vfsId = vfsId;
            this.projectId = projectId;
        }

        boolean isExpired() {
            return expirationTime < System.currentTimeMillis();
        }
    }
}
