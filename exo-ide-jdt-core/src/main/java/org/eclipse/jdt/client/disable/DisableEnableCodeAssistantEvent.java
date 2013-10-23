/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package org.eclipse.jdt.client.disable;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author <a href="mailto:vsvydenko@codenvy.com">Valeriy Svydenko</a>
 */
public class DisableEnableCodeAssistantEvent extends GwtEvent<DisableEnableCodeAssistantHandler> {
    public static GwtEvent.Type<DisableEnableCodeAssistantHandler> TYPE = new GwtEvent.Type<DisableEnableCodeAssistantHandler>();

    private boolean enable;

    public DisableEnableCodeAssistantEvent(boolean enable) {
        this.enable = enable;
    }

    public Type<DisableEnableCodeAssistantHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(DisableEnableCodeAssistantHandler handler) {
        handler.onDisableEnableCodeAssistant(this);
    }

    public boolean isEnable() {
        return enable;
    }
}
