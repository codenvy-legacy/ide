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
package org.exoplatform.ide.extension.gadget.server.opensocial.model;

/**
 * Describes an account held by this Person, which MAY be on the Service Provider's service, or MAY be on a different service.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Nov 19, 2010 $
 */
public class Account {
    /** The top-most authoritative domain for this account. */
    private String domain;

    /** An alphanumeric user name, usually chosen by the user. */
    private String userName;

    /** A user ID associated with this account. */
    private String userId;

    /** @return the domain */
    public String getDomain() {
        return domain;
    }

    /**
     * @param domain
     *         the domain to set
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /** @return the userName */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName
     *         the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /** @return the userId */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId
     *         the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }
}
