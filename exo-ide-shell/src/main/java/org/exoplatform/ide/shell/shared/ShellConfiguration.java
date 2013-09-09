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
package org.exoplatform.ide.shell.shared;


/**
 * Interface describe shell configuration.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: ShellConfiguration.java Mar 28, 2012 11:29:43 AM azatsarynnyy $
 */
public interface ShellConfiguration {

    /**
     * Returns information about user.
     *
     * @return the user information
     */
    public UserInfo getUser();

    /**
     * Sets information about user.
     *
     * @param userInfo
     *         the user information
     */
    public void setUser(UserInfo userInfo);

    /**
     * Returns entry point.
     *
     * @return the entry point
     */
    public String getVfsBaseUrl();

    /**
     * Change the entry point.
     *
     * @param entryPoint
     *         the entry point
     */
    public void setVfsBaseUrl(String vfsBaseUrl);

}