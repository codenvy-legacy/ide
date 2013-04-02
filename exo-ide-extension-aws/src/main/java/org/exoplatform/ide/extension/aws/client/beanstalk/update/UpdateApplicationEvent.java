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
package org.exoplatform.ide.extension.aws.client.beanstalk.update;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.extension.aws.shared.beanstalk.ApplicationInfo;

/**
 * Event occurs when user tries to update application.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 19, 2012 4:31:32 PM anya $
 */
public class UpdateApplicationEvent extends GwtEvent<UpdateApplicationHandler> {
    /** Type, used to register the event. */
    public static final GwtEvent.Type<UpdateApplicationHandler> TYPE = new GwtEvent.Type<UpdateApplicationHandler>();

    private ApplicationUpdatedHandler applicationUpdatedHandler;

    private String vfsId;

    private String projectId;

    private ApplicationInfo applicationInfo;

    public UpdateApplicationEvent(String vfsId, String projectId, ApplicationInfo applicationInfo,
                                  ApplicationUpdatedHandler applicationUpdatedHandler) {
        this.vfsId = vfsId;
        this.projectId = projectId;
        this.applicationInfo = applicationInfo;
        this.applicationUpdatedHandler = applicationUpdatedHandler;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<UpdateApplicationHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(UpdateApplicationHandler handler) {
        handler.onUpdateApplication(this);
    }

    /** @return the applicationUpdatedHandler */
    public ApplicationUpdatedHandler getApplicationUpdatedHandler() {
        return applicationUpdatedHandler;
    }

    /** @return the vfsId */
    public String getVfsId() {
        return vfsId;
    }

    /** @return the projectId */
    public String getProjectId() {
        return projectId;
    }

    /** @return the applicationInfo */
    public ApplicationInfo getApplicationInfo() {
        return applicationInfo;
    }
}
