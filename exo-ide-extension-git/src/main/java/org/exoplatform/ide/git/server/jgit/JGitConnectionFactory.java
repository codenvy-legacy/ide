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
package org.exoplatform.ide.git.server.jgit;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepository;
import org.exoplatform.ide.git.server.GitConnection;
import org.exoplatform.ide.git.server.GitConnectionFactory;
import org.exoplatform.ide.git.server.GitException;
import org.exoplatform.ide.git.shared.GitUser;

import java.io.File;
import java.io.IOException;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: JGitConnectionFactory.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class JGitConnectionFactory extends GitConnectionFactory {
    /**
     * @see org.exoplatform.ide.git.server.GitConnectionFactory#getConnection(java.io.File, org.exoplatform.ide.git.shared.GitUser)
     */
    @Override
    public GitConnection getConnection(File workDir, GitUser user) throws GitException {
        return new JGitConnection(createRepository(workDir), user);
    }

    private static Repository createRepository(File workDir) throws GitException {
        try {
            return new FileRepository(new File(workDir, Constants.DOT_GIT));
        } catch (IOException e) {
            throw new GitException(e.getMessage(), e);
        }
    }
}
