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
package org.exoplatform.ide.extension.samples.client.inviting.google;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class InviteGoogleDevelopersEvent extends GwtEvent<InviteGoogleDevelopersHandler> {

    public static final GwtEvent.Type<InviteGoogleDevelopersHandler> TYPE = new GwtEvent.Type<InviteGoogleDevelopersHandler>();

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<InviteGoogleDevelopersHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(InviteGoogleDevelopersHandler handler) {
        handler.onInviteGoogleDevelopers(this);
    }

}
