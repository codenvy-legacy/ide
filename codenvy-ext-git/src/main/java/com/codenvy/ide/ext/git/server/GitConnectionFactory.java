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
package com.codenvy.ide.ext.git.server;


import com.codenvy.ide.ext.git.shared.GitUser;

import java.io.File;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: GitConnectionFactory.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public abstract class GitConnectionFactory {
    /**
     * Get connection to Git repository located in <code>workDir</code>.
     *
     * @param workDir repository directory
     * @param user user
     * @return connection to Git repository
     * @throws GitException if can't initialize connection
     */
    public final GitConnection getConnection(String workDir, GitUser user) throws GitException {
        return getConnection(new File(workDir), user);
    }

    /**
     * Get connection to Git repository located in <code>workDir</code>.
     *
     * @param workDir repository directory
     * @param user user
     * @return connection to Git repository
     * @throws GitException if can't initialize connection
     */
    public abstract GitConnection getConnection(File workDir, GitUser user) throws GitException;
}