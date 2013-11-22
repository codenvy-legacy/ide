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
 * Event, occurs after pressing Show/Hide syntax error button.
 *
 * @author <a href="mailto:vsvydenko@codenvy.com">Valeriy Svydenko</a>
 */
public class DisableSyntaxErrorHighlightingEvent extends GwtEvent<DisableSyntaxErrorHighlightingHandler> {
    /** Type used to register this event. */
    public static GwtEvent.Type<DisableSyntaxErrorHighlightingHandler> TYPE = new GwtEvent.Type<DisableSyntaxErrorHighlightingHandler>();

    private boolean enable;

    /** @param enable */
    public DisableSyntaxErrorHighlightingEvent(boolean enable) {
        this.enable = enable;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    public Type<DisableSyntaxErrorHighlightingHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    protected void dispatch(DisableSyntaxErrorHighlightingHandler handler) {
        handler.onDisableSyntaxErrorHighlighting(this);
    }

    public boolean isEnable() {
        return enable;
    }
}
