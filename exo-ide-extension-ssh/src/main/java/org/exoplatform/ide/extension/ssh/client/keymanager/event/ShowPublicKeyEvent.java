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

import org.exoplatform.ide.extension.ssh.shared.KeyItem;

/**
 * Event to show Public key part.
 */
public class ShowPublicKeyEvent extends GwtEvent<ShowPublicKeyHandler> {

    public static final GwtEvent.Type<ShowPublicKeyHandler> TYPE = new Type<ShowPublicKeyHandler>();

    private KeyItem keyItem;

    public ShowPublicKeyEvent(KeyItem keyItem) {
        super();
        this.keyItem = keyItem;
    }

    /** {@inheritDoc} */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ShowPublicKeyHandler> getAssociatedType() {
        return TYPE;
    }

    /** {@inheritDoc} */
    @Override
    protected void dispatch(ShowPublicKeyHandler handler) {
        handler.onShowPublicSshKey(this);
    }

    /** @return the keyItem */
    public KeyItem getKeyItem() {
        return keyItem;
    }

}
