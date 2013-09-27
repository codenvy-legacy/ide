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

import javax.ws.rs.core.HttpHeaders;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.HashSet;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: GetContentTest.java 77587 2011-12-13 10:42:02Z andrew00x $
 */
public class GetContentTest extends MemoryFileSystemTest {
    private MemoryFolder getContentTestFolder;
    private String       fileId;
    private String       fileName;
    private String       folderId;
    private String content = "__GetContentTest__";
    private String filePath;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        String name = getClass().getName();
        getContentTestFolder = new MemoryFolder(name);
        testRoot.addChild(getContentTestFolder);

        MemoryFile file = new MemoryFile("GetContentTest_FILE", "text/plain",
                                         new ByteArrayInputStream(content.getBytes()));
        getContentTestFolder.addChild(file);
        fileId = file.getId();
        fileName = file.getName();
        filePath = file.getPath();

        MemoryFolder folder = new MemoryFolder("GetContentTest_FOLDER");
        getContentTestFolder.addChild(folder);
        folderId = folder.getId();

        memoryContext.putItem(getContentTestFolder);
    }

    public void testGetContent() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "content/" + fileId;
        ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
        assertEquals(200, response.getStatus());
        //log.info(new String(writer.getBody()));
        assertEquals(content, new String(writer.getBody()));
        assertEquals("text/plain", writer.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE));
    }

    public void testDownloadFile() throws Exception {
        // Expect the same as 'get content' plus header "Content-Disposition".
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "downloadfile/" + fileId;
        ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
        assertEquals(200, response.getStatus());
        //log.info(new String(writer.getBody()));
        assertEquals(content, new String(writer.getBody()));
        assertEquals("text/plain", writer.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE));
        assertEquals("attachment; filename=\"" + fileName + "\"", writer.getHeaders().getFirst("Content-Disposition"));
    }

    public void testGetContentFolder() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "content/" + folderId;
        ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
        assertEquals(400, response.getStatus());
        log.info(new String(writer.getBody()));
    }

    public void testGetContentNoPermissions() throws Exception {
        AccessControlEntry ace = new AccessControlEntryImpl();
        ace.setPrincipal(new PrincipalImpl("admin", Principal.Type.USER));
        ace.setPermissions(new HashSet<String>(Arrays.asList(VirtualFileSystemInfoImpl.BasicPermissions.ALL.value())));
        memoryContext.getItem(fileId).updateACL(Arrays.asList(ace), true);

        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "content/" + fileId;
        ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
        assertEquals(403, response.getStatus());
        log.info(new String(writer.getBody()));
    }

    public void testGetContentByPath() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "contentbypath" + filePath;
        ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
        assertEquals(200, response.getStatus());
        //log.info(new String(writer.getBody()));
        assertEquals(content, new String(writer.getBody()));
        assertEquals("text/plain", writer.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE));
    }

    public void testGetContentByPathWithVersionID() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "contentbypath" + filePath + '?' + "versionId=" + "0";
        ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
        assertEquals(200, response.getStatus());
        //log.info(new String(writer.getBody()));
        assertEquals(content, new String(writer.getBody()));
        assertEquals("text/plain", writer.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE));
    }
}
