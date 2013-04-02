/*
 * Copyright (C) 2011 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.git.server.jgit;

import junit.framework.Assert;

import org.eclipse.jgit.api.Git;
import org.exoplatform.ide.git.shared.GitUser;

import java.io.File;
import java.util.List;

/**
 * Created by The eXo Platform SAS.
 *
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
