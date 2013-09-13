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
