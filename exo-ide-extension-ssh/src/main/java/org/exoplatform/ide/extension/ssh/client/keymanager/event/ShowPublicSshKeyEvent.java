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
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: ShowPublicSshKeyEvent May 19, 2011 12:23:55 PM evgen $
 */
public class ShowPublicSshKeyEvent extends GwtEvent<ShowPublicSshKeyHandler> {

    public static final GwtEvent.Type<ShowPublicSshKeyHandler> TYPE = new Type<ShowPublicSshKeyHandler>();

    private KeyItem keyItem;

    /** @param keyItem */
    public ShowPublicSshKeyEvent(KeyItem keyItem) {
        super();
        this.keyItem = keyItem;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ShowPublicSshKeyHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(ShowPublicSshKeyHandler handler) {
        handler.onShowPublicSshKey(this);
    }

    /** @return the keyItem */
    public KeyItem getKeyItem() {
        return keyItem;
    }

}
