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

import junit.framework.Assert;

import org.eclipse.jgit.api.Git;
import org.exoplatform.ide.git.shared.GitUser;

import java.io.File;
import java.util.List;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: Aug 3, 2012
 */
public class GetCommitersTest extends BaseTest {
    public void testGetCommiters() throws Exception {
        Git git = new Git(getDefaultRepository());
        File workDir = git.getRepository().getWorkTree();
        addFile(workDir, "t-log1", "AAA\n");
        git.add().addFilepattern(".").call();
        git.commit().setMessage("log\ntest").setCommitter("Chuck Norris", "gmail@chucknorris.com").call();

        List<GitUser> commiters = getDefaultConnection().getCommiters();
        Assert.assertNotNull("No commiters", commiters);
        Assert.assertEquals("Must be to comitters one of them owner of repository, other commiter from test", 2, commiters.size());
        Assert.assertEquals("gmail@chucknorris.com", commiters.get(0).getEmail());
        Assert.assertEquals("Chuck Norris", commiters.get(0).getName());
    }

}
