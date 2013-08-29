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
package org.exoplatform.ide.client.framework.event;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class CollaborationChangedEvent extends GwtEvent<CollaborationChangedHandler> {
    public static Type<CollaborationChangedHandler> TYPE = new Type<CollaborationChangedHandler>();

    private boolean enabled;

    private ProjectModel project;

    public CollaborationChangedEvent(boolean enabled, ProjectModel project) {
        this.enabled = enabled;
        this.project = project;
    }

    public Type<CollaborationChangedHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(CollaborationChangedHandler handler) {
        handler.onCollaborationChanged(this);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public ProjectModel getProject() {
        return project;
    }
}
