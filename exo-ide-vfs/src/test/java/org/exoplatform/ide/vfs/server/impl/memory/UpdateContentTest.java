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
package org.exoplatform.ide.vfs.server.impl.memory;

import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.tools.ByteArrayContainerResponseWriter;
import org.exoplatform.ide.vfs.server.impl.memory.context.MemoryFile;
import org.exoplatform.ide.vfs.server.impl.memory.context.MemoryFolder;
import org.exoplatform.ide.vfs.shared.AccessControlEntry;
import org.exoplatform.ide.vfs.shared.AccessControlEntryImpl;
import org.exoplatform.ide.vfs.shared.Principal;
import org.exoplatform.ide.vfs.shared.PrincipalImpl;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfoImpl;

import java.io.ByteArrayInputStream;
import java.util.*;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: UpdateContentTest.java 75032 2011-10-13 15:24:34Z andrew00x $
 */
public class UpdateContentTest extends MemoryFileSystemTest {
    private String fileId;
    private String folderId;
    private String content = "__UpdateContentTest__";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        String name = getClass().getName();
        MemoryFolder updateContentTestFolder = new MemoryFolder(name);
        testRoot.addChild(updateContentTestFolder);

        MemoryFile file = new MemoryFile("UpdateContentTest_FILE", "text/plain",
                                         new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
        updateContentTestFolder.addChild(file);
        fileId = file.getId();

        MemoryFolder folder = new MemoryFolder("UpdateContentTest_FOLDER");
        updateContentTestFolder.addChild(folder);
        folderId = folder.getId();

        memoryContext.putItem(updateContentTestFolder);
    }

    public void testUpdateContent() throws Exception {
        String path = SERVICE_URI + "content/" + fileId;

        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        List<String> contentType = new ArrayList<String>();
        contentType.add("text/plain;charset=utf8");
        headers.put("Content-Type", contentType);

        ContainerResponse response = launcher.service("POST", path, BASE_URI, headers, content.getBytes(), null);
        assertEquals(204, response.getStatus());

        MemoryFile file = (MemoryFile)memoryContext.getItem(fileId);
        checkFileContext(content, "text/plain;charset=utf8", file);
    }

    public void testUpdateContentFolder() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "content/" + folderId;
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, content.getBytes(), writer, null);
        assertEquals(400, response.getStatus());
        log.info(new String(writer.getBody()));
    }

    public void testUpdateContentNoPermissions() throws Exception {
        AccessControlEntry adminACE = new AccessControlEntryImpl();
        adminACE.setPrincipal(new PrincipalImpl("admin", Principal.Type.USER));
        adminACE.setPermissions(new HashSet<String>(Arrays.asList(VirtualFileSystemInfoImpl.BasicPermissions.ALL.value())));
        AccessControlEntry userACE = new AccessControlEntryImpl();
        userACE.setPrincipal(new PrincipalImpl("john", Principal.Type.USER));
        userACE.setPermissions(new HashSet<String>(Arrays.asList(VirtualFileSystemInfoImpl.BasicPermissions.READ.value())));
        memoryContext.getItem(fileId).updateACL(Arrays.asList(adminACE, userACE), true);

        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "content/" + fileId;
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
        assertEquals(403, response.getStatus());
        log.info(new String(writer.getBody()));
    }

    public void testUpdateContentLocked() throws Exception {
        MemoryFile file = (MemoryFile)memoryContext.getItem(fileId);
        String lockToken = file.lock();

        String path = SERVICE_URI + "content/" + fileId + '?' + "lockToken=" + lockToken;

        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        List<String> contentType = new ArrayList<String>();
        contentType.add("text/plain;charset=utf8");
        headers.put("Content-Type", contentType);

        ContainerResponse response = launcher.service("POST", path, BASE_URI, headers, content.getBytes(), null);
        assertEquals(204, response.getStatus());

        file = (MemoryFile)memoryContext.getItem(fileId);
        checkFileContext(content, "text/plain;charset=utf8", file);
    }

    public void testUpdateContentLocked_NoLockTokens() throws Exception {
        MemoryFile file = (MemoryFile)memoryContext.getItem(fileId);
        file.lock();
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "content/" + fileId;
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
        assertEquals(423, response.getStatus());
        log.info(new String(writer.getBody()));
    }
}
