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

import org.exoplatform.ide.git.server.GitConnection;
import org.exoplatform.ide.git.server.GitException;
import org.exoplatform.ide.git.shared.CloneRequest;
import org.exoplatform.ide.git.shared.Remote;
import org.exoplatform.ide.git.shared.RemoteListRequest;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @author <a href="maito:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class RemoteListTest extends BaseTest {

    private File repository2;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        repository2 = new File(getTarget().getAbsolutePath(), "repository2");
        forClean.add(repository2);
    }

    @Test
    public void testRemoteList() throws GitException, URISyntaxException {
        GitConnection connection = getDefaultConnection()
                .clone(new CloneRequest(getDefaultRepository().getAbsolutePath(), repository2.getAbsolutePath()));
        assertEquals(1, connection.remoteList(new RemoteListRequest()).size());
        //create new remote
        NativeGit nativeGit = new NativeGit(repository2);
        nativeGit.createRemoteAddCommand()
                .setName("newremote")
                .setUrl("newremote.url")
                .execute();
        assertEquals(2, connection.remoteList(new RemoteListRequest()).size());
        RemoteListRequest request = new RemoteListRequest();
        request.setRemote("newremote");
        List<Remote> one = connection.remoteList(request);
        assertEquals("newremote.url", one.get(0).getUrl());
        assertEquals(1 , one.size());
    }
}
