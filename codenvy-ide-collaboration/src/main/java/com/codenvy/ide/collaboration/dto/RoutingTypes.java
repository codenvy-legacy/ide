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
package com.codenvy.ide.collaboration.dto;


/**
 * Routing types for all DTOs.
 * NOTE: If you add a new DTO, ONLY add to the bottom of the list.
 *
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class RoutingTypes {
    private RoutingTypes() {
    }

    public static final int PROJECT_OPENED = 1;

    public static final int PROJECT_CLOSED = 2;

    public static final int ITEM_DELETED = 3;

    public static final int ITEM_CREATED = 4;

    public static final int ITEM_MOVED = 5;

    public static final int ITEM_RENAMED = 6;

    public static final int CHAT_MESSAGE = 7;

    public static final int CHAT_CODE_POINT = 8;

    public static final int PROJECT_PARTICIPANTS = 9;

    public static final int CHAT_PARTISIPANT_ADD = 10;

    public static final int CHAT_PARTISIPANT_REMOVE = 11;

    public static final int PROJECT_OPERATION_NOTIFICATION = 12;

    public static final int DISABLE_ENABLE_COLLABORATION = 13;

}
