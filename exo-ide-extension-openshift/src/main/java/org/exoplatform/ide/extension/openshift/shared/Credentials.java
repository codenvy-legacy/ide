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
package org.exoplatform.ide.extension.openshift.shared;

/**
 * Authentication credentials.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: Credentials.java Mar 20, 2012 9:32:20 AM azatsarynnyy $
 */
public interface Credentials {
    /**
     * Returns the RedHat login.
     *
     * @return e-mail.
     */
    public String getRhlogin();

    /**
     * Set the RedHat login.
     *
     * @param email
     */
    public void setRhlogin(String email);

    /**
     * Returns the password.
     *
     * @return password.
     */
    public String getPassword();

    /**
     * Set the password.
     *
     * @param password
     *         password.
     */
    public void setPassword(String password);
}
