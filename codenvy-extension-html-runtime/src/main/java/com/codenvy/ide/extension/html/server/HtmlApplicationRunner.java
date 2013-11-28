/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
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
 * {@link ApplicationRunner} for running HTML applications.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: HtmlApplicationRunner.java Jun 26, 2013 1:12:02 PM azatsarynnyy $
 */
public class HtmlApplicationRunner implements ApplicationRunner {
    /** Default application lifetime (in minutes). After this time application may be stopped automatically. */
    private static final int                     DEFAULT_APPLICATION_LIFETIME = 10;

    private static final Log                     LOG                          = ExoLogger.getLogger(HtmlApplicationRunner.class);

    private final int                            applicationLifetime;
    private final long                           applicationLifetimeMillis;

    private final String                         applicationURL;

    private final Map<String, RunnedApplication> applications;
    private final ScheduledExecutorService       applicationTerminator;

    public HtmlApplicationRunner(InitParams initParams) {
        this(parseApplicationLifeTime(readValueParam(initParams, "html-application-lifetime")),
             readValueParam(initParams, "html-application-url"));
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

    protected HtmlApplicationRunner(int applicationLifetime, String applicationURL) {
        if (applicationLifetime < 1) {
            throw new IllegalArgumentException("Invalid application lifetime: " + 1);
        }
        this.applicationLifetime = applicationLifetime;
        this.applicationLifetimeMillis = applicationLifetime * 60 * 1000;

        this.applications = new ConcurrentHashMap<String, RunnedApplication>();
        this.applicationURL = applicationURL;

        this.applicationTerminator = Executors.newSingleThreadScheduledExecutor();
        this.applicationTerminator.scheduleAtFixedRate(new TerminateApplicationTask(), 1, 1, TimeUnit.MINUTES);
    }

    /**
     * @see com.codenvy.ide.extension.html.server.ApplicationRunner#runApplication(org.exoplatform.ide.vfs.server.VirtualFileSystem,
     *      java.lang.String, java.lang.String)
     */
    @Override
    public ApplicationInstance runApplication(VirtualFileSystem vfs, String projectId, String wsMountPath) throws ApplicationRunnerException,
                                                                                                          VirtualFileSystemException {
        Item project = vfs.getItem(projectId, false, PropertyFilter.NONE_FILTER);
        if (project.getItemType() != ItemType.PROJECT) {
            throw new ApplicationRunnerException("Item '" + project.getPath() + "' is not a project. ");
        }

        final String name = generate("app-", 16);
        final long expired = System.currentTimeMillis() + applicationLifetimeMillis;

        applications.put(name, new RunnedApplication(name, expired, project.getName(), wsMountPath + project.getPath()));
        LOG.info("EVENT#run-started# PROJECT#" + project.getName() + "# TYPE#HTML#");

        return new ApplicationInstanceImpl(name, applicationLifetime, applicationURL);
    }

    /** @see com.codenvy.ide.extension.html.server.ApplicationRunner#stopApplication(java.lang.String) */
    @Override
    public void stopApplication(String name) throws ApplicationRunnerException {
        RunnedApplication application = applications.get(name);
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

    /** @see com.codenvy.ide.extension.html.server.ApplicationRunner#getApplicationByName(java.lang.String) */
    @Override
    public RunnedApplication getApplicationByName(String name) throws ApplicationRunnerException {
        RunnedApplication application = applications.get(name);
        if (application != null) {
            return application;
        } else {
            throw new ApplicationRunnerException("Application '" + name + "' not found. ");
        }
    }

    private class TerminateApplicationTask implements Runnable {
        @Override
        public void run() {
            List<String> stopped = new ArrayList<String>();
            for (RunnedApplication app : applications.values()) {
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
}
