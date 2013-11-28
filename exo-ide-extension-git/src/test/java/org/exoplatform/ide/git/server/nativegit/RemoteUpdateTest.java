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
package org.exoplatform.ide.git.server.nativegit;

import org.exoplatform.ide.git.server.GitException;
import org.exoplatform.ide.git.server.nativegit.commands.GetConfigCommand;
import org.exoplatform.ide.git.shared.RemoteUpdateRequest;
import org.junit.Test;

import java.io.File;
import java.util.*;

/**
 * @author <a href="maito:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class RemoteUpdateTest extends BaseTest {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        NativeGit nGitDefault = new NativeGit(getDefaultRepository());
        nGitDefault.createRemoteAddCommand()
                .setName("newRemote")
                .setUrl("newRemote.url")
                .setBranches(new String[]{"branch1"})
                .execute();
    }

    @Test
    public void testUpdateBranches() throws GitException {
        //change branch1 to branch2
        RemoteUpdateRequest request = new RemoteUpdateRequest();
        request.setName("newRemote");
        request.setBranches(new String[]{"branch2"});
        getDefaultConnection().remoteUpdate(request);
        assertEquals(parseAllConfig(getDefaultRepository())
                .get("remote.newRemote.fetch").get(0), "+refs/heads/branch2:refs/remotes/newRemote/branch2");
    }

    @Test
    public void testAddBranchesUpdate() throws GitException {
        RemoteUpdateRequest request = new RemoteUpdateRequest();
        request.setName("newRemote");
        request.setBranches(new String[]{"branch2"});
        request.setAddBranches(true);
        getDefaultConnection().remoteUpdate(request);
        assertTrue(parseAllConfig(getDefaultRepository()).get("remote.newRemote.fetch")
                .containsAll(Arrays.asList(new String[]{"+refs/heads/branch2:refs/remotes/newRemote/branch2",
                        "+refs/heads/branch1:refs/remotes/newRemote/branch1"})));
    }

    @Test
    public void testAddUrl() throws GitException {
        RemoteUpdateRequest request = new RemoteUpdateRequest();
        request.setName("newRemote");
        request.setAddUrl(new String[]{"new.com"});
        getDefaultConnection().remoteUpdate(request);
        assertTrue(parseAllConfig(getDefaultRepository()).get("remote.newRemote.url").contains("new.com"));
    }

    @Test
    public void testAddPushUrl() throws GitException {
        RemoteUpdateRequest request = new RemoteUpdateRequest();
        request.setName("newRemote");
        request.setAddPushUrl(new String[]{"pushurl1"});
        getDefaultConnection().remoteUpdate(request);
        assertTrue(parseAllConfig(getDefaultRepository()).get("remote.newRemote.pushurl").contains("pushurl1"));
    }

    @Test
    public void testDeleteUrl() throws GitException {
        //add url
        new NativeGit(getDefaultRepository()).createRemoteUpdateCommand()
                .setRemoteName("newRemote")
                .setAddUrl(new String[]{"newurl"})
                .execute();
        RemoteUpdateRequest request = new RemoteUpdateRequest();
        request.setName("newRemote");
        request.setRemoveUrl(new String[]{"newurl"});
        getDefaultConnection().remoteUpdate(request);
        assertFalse(parseAllConfig(getDefaultRepository()).get("remote.newRemote.url").contains("newurl"));
    }

    @Test
    public void testDeletePushUrl() throws GitException {
        //add push url
        new NativeGit(getDefaultRepository()).createRemoteUpdateCommand()
                .setRemoteName("newRemote")
                .setAddUrl(new String[]{"pusurl"})
                .execute();
        RemoteUpdateRequest request = new RemoteUpdateRequest();
        request.setName("newRemote");
        request.setRemovePushUrl(new String[]{"pushurl"});
        assertNull(parseAllConfig(getDefaultRepository()).get("remote.newRemote.pushurl"));
    }

    private Map<String, List<String>> parseAllConfig(File repo) throws GitException {
        Map<String, List<String>> config = new HashMap<>();
        GetConfigCommand getConf = new GetConfigCommand(repo).setGetList(true);
        getConf.execute();
        for (String outLine : getConf.getOutput()) {
            String[] pair = outLine.split("=");
            List<String> list = config.get(pair[0]);
            if (list == null) {
                list = new LinkedList<>();
            }
            list.add(pair[1]);
            config.put(pair[0], list);
        }
        return config;
    }
}
