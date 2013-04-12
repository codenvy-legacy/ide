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
package org.exoplatform.ide.extension.aws.client.beanstalk.versions.delete;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.extension.aws.shared.beanstalk.ApplicationVersionInfo;

/**
 * Event occurs, when user tries to delete application's version.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 20, 2012 4:52:19 PM anya $
 */
public class DeleteVersionEvent extends GwtEvent<DeleteVersionHandler> {

    /** Type, used to register event. */
    public static final GwtEvent.Type<DeleteVersionHandler> TYPE = new GwtEvent.Type<DeleteVersionHandler>();

    private String vfsId;

    private String projectId;

    private ApplicationVersionInfo version;

    private VersionDeletedHandler versionDeletedHandler;

    public DeleteVersionEvent(String vfsId, String projectId, ApplicationVersionInfo version,
                              VersionDeletedHandler versionDeletedHandler) {
        this.projectId = projectId;
        this.vfsId = vfsId;
        this.version = version;
        this.versionDeletedHandler = versionDeletedHandler;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<DeleteVersionHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(DeleteVersionHandler handler) {
        handler.onDeleteVersion(this);
    }

    /** @return the vfsId */
    public String getVfsId() {
        return vfsId;
    }

    /** @return the projectId */
    public String getProjectId() {
        return projectId;
    }

    /** @return the version */
    public ApplicationVersionInfo getVersion() {
        return version;
    }

    /** @return the versionDeletedHandler */
    public VersionDeletedHandler getVersionDeletedHandler() {
        return versionDeletedHandler;
    }
}
