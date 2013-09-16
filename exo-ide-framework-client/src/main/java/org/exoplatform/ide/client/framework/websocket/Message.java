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
package org.exoplatform.ide.client.framework.websocket;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: Message.java Dec 4, 2012 3:07:48 PM azatsarynnyy $
 */
public interface Message {
    /**
     * Get message UUID. If specified for request message then response message gets the same UUID.
     *
     * @return message unique identifier
     */
    String getUuid();

    /**
     * Set message UUID. If specified for request message then response message gets the same UUID.
     *
     * @param uuid
     *         message unique identifier
     */
    void setUuid(String uuid);

    /**
     * Get message body.
     *
     * @return message body
     */
    String getBody();

    /**
     * Set message body.
     *
     * @param body
     *         message body
     */
    void setBody(String body);
}
