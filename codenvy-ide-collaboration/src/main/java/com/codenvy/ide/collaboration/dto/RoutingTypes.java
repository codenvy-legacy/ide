/*
 * Copyright (C) 2011 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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

    public static final int CHAT_PARTISIPANTS = 9;

    public static final int CHAT_PARTISIPANTS_RESPONSE = 10;

    public static final int CHAT_PARTISIPANT_ADD = 11;

    public static final int CHAT_PARTISIPANT_REMOVE = 12;

    public static final int PROJECT_OPERATION_NOTIFICATION = 14;

    public static final int DISABLE_ENABLE_COLLABORATION = 15;

}
