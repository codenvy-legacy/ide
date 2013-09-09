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
package org.exoplatform.ide.extension.aws.client.beanstalk.environments.launch;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 21, 2012 2:57:38 PM anya $
 */
public class LaunchEnvironmentEvent extends GwtEvent<LaunchEnvironmentHandler> {
    /** Type used to register the event. */
    public static final GwtEvent.Type<LaunchEnvironmentHandler> TYPE = new GwtEvent.Type<LaunchEnvironmentHandler>();

    private String applicationName;

    private String versionLabel;

    private String vfsId;

    private String projectId;

    private LaunchEnvironmentStartedHandler environmentLaunchedHandler;

    public LaunchEnvironmentEvent(String vfsId, String projectId, String applicationName, String versionLabel,
                                  LaunchEnvironmentStartedHandler environmentLaunchedHandler) {
        this.vfsId = vfsId;
        this.projectId = projectId;
        this.applicationName = applicationName;
        this.versionLabel = versionLabel;
        this.environmentLaunchedHandler = environmentLaunchedHandler;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(LaunchEnvironmentHandler handler) {
        handler.onLaunchEnvironment(this);
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<LaunchEnvironmentHandler> getAssociatedType() {
        return TYPE;
    }

    /** @return the applicationName */
    public String getApplicationName() {
        return applicationName;
    }

    /** @return the versionLabel */
    public String getVersionLabel() {
        return versionLabel;
    }

    /** @return the vfsId */
    public String getVfsId() {
        return vfsId;
    }

    /** @return the projectId */
    public String getProjectId() {
        return projectId;
    }

    /** @return the environmentLaunchedHandler */
    public LaunchEnvironmentStartedHandler getEnvironmentCreatedHandler() {
        return environmentLaunchedHandler;
    }
}
