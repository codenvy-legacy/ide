/*
 * Copyright (C) 2012 eXo Platform SAS.
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
