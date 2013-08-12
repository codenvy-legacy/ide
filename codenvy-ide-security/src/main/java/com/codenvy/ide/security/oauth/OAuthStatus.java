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