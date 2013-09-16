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
package org.exoplatform.ide.client.framework.userinfo.event;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.gwtframework.commons.exception.ServerExceptionEvent;
import org.exoplatform.ide.client.framework.userinfo.UserInfo;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class UserInfoReceivedEvent extends ServerExceptionEvent<UserInfoReceivedHandler> {

    public static final GwtEvent.Type<UserInfoReceivedHandler> TYPE = new GwtEvent.Type<UserInfoReceivedHandler>();

    private UserInfo userInfo;

    public UserInfoReceivedEvent(UserInfo userInfo) {
        super(null);
        this.userInfo = userInfo;
    }

    @Override
    protected void dispatch(UserInfoReceivedHandler handler) {
        handler.onUserInfoReceived(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<UserInfoReceivedHandler> getAssociatedType() {
        return TYPE;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

}
