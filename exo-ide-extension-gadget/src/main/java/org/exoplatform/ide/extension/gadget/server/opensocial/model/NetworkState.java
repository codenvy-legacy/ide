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
package org.exoplatform.ide.extension.gadget.server.opensocial.model;

/**
 * Person network state.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Nov 19, 2010 $
 */
public enum NetworkState {
    /** Currently Online. */
    ONLINE("ONLINE", "Online"),
    /** Currently Offline. */
    OFFLINE("OFFLINE", "Offline"),
    /** Currently online but away. */
    AWAY("AWAY", "Away"),
    /** In a chat or available to chat. */
    CHAT("CHAT", "Chat"),
    /** Online, but don't disturb. */
    DND("DND", "Do Not Disturb"),
    /** Gone away for a longer period of time. */
    XA("XA", "Extended Away");

    /** Status. */
    private final String status;

    /** The value used for display purposes. */
    private final String displayValue;

    /**
     * @param status
     *         status
     * @param displayValue
     *         display value
     */
    private NetworkState(String status, String displayValue) {
        this.status = status;
        this.displayValue = displayValue;
    }

    /** @return {@link String} display name */
    public String getDisplayValue() {
        return displayValue;
    }

    /** @return the status */
    public String getStatus() {
        return status;
    }
}
