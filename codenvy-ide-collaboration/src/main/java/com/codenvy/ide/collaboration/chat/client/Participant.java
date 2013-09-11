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
package com.codenvy.ide.collaboration.chat.client;


import com.codenvy.ide.collaboration.dto.client.DtoClientImpls.UserDetailsImpl;

/**
 * Model object for a participant. This extends the
 * {@link UserDetailsImpl} class used for data transfer.
 *
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class Participant extends UserDetailsImpl {

    private static final String COLOR_KEY = "__color";

    private static final String CLIENT_ID_KEY = "__client_id";

    protected Participant() {
    }

    public final String getColor() {
        return getStringField(COLOR_KEY);
    }

    public final void setColor(String color) {
        addField(COLOR_KEY, color);
    }

    public final String getClientId() {
        return getStringField(CLIENT_ID_KEY);
    }

    public final void setClientId(String clientId) {
        addField(CLIENT_ID_KEY, clientId);
    }

}
