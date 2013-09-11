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
package org.exoplatform.ide.client.menu;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class RefreshMenuEvent extends GwtEvent<RefreshMenuHandler> {

    public static final GwtEvent.Type<RefreshMenuHandler> TYPE = new GwtEvent.Type<RefreshMenuHandler>();

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<RefreshMenuHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(RefreshMenuHandler handler) {
        handler.onRefreshMenu(this);
    }

}
