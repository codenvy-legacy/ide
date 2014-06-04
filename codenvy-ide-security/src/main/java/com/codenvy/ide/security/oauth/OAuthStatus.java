/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.security.oauth;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public enum OAuthStatus {
    /** If OAuth window manually closed by user. */
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