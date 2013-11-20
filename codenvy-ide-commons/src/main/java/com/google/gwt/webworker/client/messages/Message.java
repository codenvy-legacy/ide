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
package com.google.gwt.webworker.client.messages;

/**
 *  Base interface for all DTOs that adds a type tag for routing messages.
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface Message {
    public static final int    NON_ROUTABLE_TYPE = -2;
    public static final String TYPE_FIELD = "_type";

    /**
     * Every DTO needs to report a type for the purposes of routing messages on
     * the client.
     */
    public int getType();
}
