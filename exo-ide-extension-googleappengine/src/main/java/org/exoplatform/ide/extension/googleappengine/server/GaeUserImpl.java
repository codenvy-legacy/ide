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
package org.exoplatform.ide.extension.googleappengine.server;

import com.codenvy.commons.security.shared.Token;

import org.exoplatform.ide.extension.googleappengine.shared.GaeUser;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class GaeUserImpl implements GaeUser {
    private String id;
    private Token  token;

    public GaeUserImpl(String id, Token token) {
        this.id = id;
        this.token = token;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getEmail() {
        // Not need email address in this case.
        return null;
    }

    @Override
    public void setEmail(String id) {
        // Not need email address in this case.
    }

    @Override
    public String getName() {
        // Not need name in this case.
        return null;
    }

    @Override
    public void setName(String name) {
        // Not need name in this case.
    }

    @Override
    public Token getToken() {
        return token;
    }

    @Override
    public void setToken(Token token) {
        this.token = token;
    }
}
