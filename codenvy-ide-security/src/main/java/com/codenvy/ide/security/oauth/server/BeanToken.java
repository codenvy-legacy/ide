/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2013] Codenvy, S.A.
 *  All Rights Reserved.
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
package com.codenvy.ide.security.oauth.server;


import com.codenvy.ide.security.oauth.shared.Token;

public class BeanToken implements Token {
    private String version;
    private String scope;
    private String token;
    private String secret;
    private String authHeader;

    public BeanToken(String token) {
        this(token, null, null, null, null);
    }

    public BeanToken(String token, String scope) {
        this(token, scope, null, null, null);
    }

    public BeanToken(String token, String scope, String version, String secret, String authHeader) {
        this.token = token;
        this.scope = scope;
        this.secret = secret;
        this.version = version;
        this.authHeader = authHeader;
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public void setToken(String token) {
        this.token = token;

    }

    @Override
    public String getSecret() {
        return secret;
    }

    @Override
    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Override
    public String getScope() {
        return scope;
    }

    @Override
    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String getAuthHeader() {
        return authHeader;
    }

    @Override
    public void setAuthHeader(String headerValue) {
        this.authHeader = headerValue;
    }
}
