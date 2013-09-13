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
package org.exoplatform.ide.git.shared;

/**
 * Represents user's credentials.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Mar 28, 2012 12:44:37 PM anya $
 * @deprecated not need it any more, we use oauth for GItHub
 */
public interface Credentials {
    /**
     * Get login.
     * 
     * @return {@link String} login
     */
    public String getLogin();

    /**
     * Set login.
     * 
     * @param login
     */
    public void setLogin(String login);

    /**
     * Get password.
     * 
     * @return {@link String} password
     */
    public String getPassword();

    /**
     * Set the password.
     * 
     * @param password password.
     */
    public void setPassword(String password);
}
