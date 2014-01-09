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
package org.exoplatform.ide.extension.ssh.client.keymanager.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event to show user management keys window.
 */
public class ShowKeyManagerEvent extends GwtEvent<ShowKeyManagerHandler> {

    public static final GwtEvent.Type<ShowKeyManagerHandler> TYPE = new Type<ShowKeyManagerHandler>();

    /** {@inheritDoc} */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ShowKeyManagerHandler> getAssociatedType() {
        return TYPE;
    }

    /** {@inheritDoc} */
    @Override
    protected void dispatch(ShowKeyManagerHandler handler) {
        handler.onShowSshKeyManager(this);
    }

}
