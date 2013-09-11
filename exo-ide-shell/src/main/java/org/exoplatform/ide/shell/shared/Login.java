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
 * Interface describe login command.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: Login.java Mar 28, 2012 11:11:07 AM azatsarynnyy $
 */
public interface Login {
    /**
     * Returns the login command.
     *
     * @return the login command
     */
    String getCmd();

    /**
     * Sets the login command.
     *
     * @param cmd
     *         the login command
     */
    void setCmd(String cmd);
}
