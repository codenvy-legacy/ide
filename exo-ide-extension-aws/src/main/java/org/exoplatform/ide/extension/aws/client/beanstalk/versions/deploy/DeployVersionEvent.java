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
package org.exoplatform.ide.extension.aws.client.beanstalk.versions.deploy;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs, when user tries to deploy an application's version.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: DeployVersionEvent.java Sep 27, 2012 6:04:07 PM azatsarynnyy $
 */
public class DeployVersionEvent extends GwtEvent<DeployVersionHandler> {
    public static final GwtEvent.Type<DeployVersionHandler> TYPE = new GwtEvent.Type<DeployVersionHandler>();

    private String applicationName;

    private String versionLabel;

    private DeployVersionStartedHandler deployVersionStartedHandler;

    public DeployVersionEvent(String applicationName, String versionLabel,
                              DeployVersionStartedHandler deployVersionStartedHandler) {
        this.applicationName = applicationName;
        this.versionLabel = versionLabel;
        this.deployVersionStartedHandler = deployVersionStartedHandler;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<DeployVersionHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(DeployVersionHandler handler) {
        handler.onDeployVersion(this);
    }

    /** @return the applicationName */
    public String getApplicationName() {
        return applicationName;
    }

    /** @return the versionLabel */
    public String getVersionLabel() {
        return versionLabel;
    }

    /** @return the deployVersionStartedHandler */
    public DeployVersionStartedHandler getDeployVersionStartedHandler() {
        return deployVersionStartedHandler;
    }
}
