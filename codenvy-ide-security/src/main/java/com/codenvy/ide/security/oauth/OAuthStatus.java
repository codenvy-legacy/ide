/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.security.oauth;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public enum OAuthStatus {
    /** If OAuth window manualy closed by user. */
    NOT_PERFORMED(1),

    /** If some problem according while user try to login. */
    FAILED(2),

    /** If user has successfully logged in. */
    LOGGED_IN(3),

    /** If user has successfully logged out. */
    LOGGED_OUT(4);

    private final int value;

    private OAuthStatus(int value) {
        this.value = value;
    }

    public static OAuthStatus fromValue(int value) {
        for (OAuthStatus v : OAuthStatus.values()) {
            if (v.value == value) {
                return v;
            }
        }
        throw new IllegalArgumentException("Invalid value '" + value + "' ");
    }
}