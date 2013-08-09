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
package com.google.collide.client.disable;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class DisableEnableCollaborationEvent extends GwtEvent<DisableEnableCollaborationHandler> {
    public static Type<DisableEnableCollaborationHandler> TYPE = new Type<DisableEnableCollaborationHandler>();

    private boolean enable;
    private boolean fromMenu;

    public DisableEnableCollaborationEvent(boolean enable, boolean fromMenu) {
        this.enable = enable;
        this.fromMenu = fromMenu;
    }

    public Type<DisableEnableCollaborationHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(DisableEnableCollaborationHandler handler) {
        handler.onDisableEnableCollaboration(this);
    }

    public boolean isEnable() {
        return enable;
    }

    public boolean isFromMenu() {
        return fromMenu;
    }
}
