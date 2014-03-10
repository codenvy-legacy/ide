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
package com.codenvy.ide.security.oauth.shared;

public interface Token {

    /** Get OAuth token */
    String getToken();

    /** Set OAuth token */
    void setToken(String token);

    /** Get OAuth 1.0 secret token */
    String getSecret();

    /** Get OAuth 1.0 secret token */
    void setSecret(String secret);

    /** Get OAuth scope */
    String getScope();

    /** Set OAuth scope */
    void setScope(String scope);

    /** Get OAuth version of token */
    String getVersion();

    /** Set OAuth version of token */
    void setVersion(String version);

    /** Get value of authenticator header for signing OAuth 1.0 request */
    String getAuthHeader();

    /** Set value of authenticator header for signing OAuth 1.0 request */
    void setAuthHeader(String headerValue);
}
