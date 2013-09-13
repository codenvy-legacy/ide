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
import org.exoplatform.ide.vfs.shared.ExitCodes;
import org.exoplatform.ide.vfs.shared.Principal;
import org.exoplatform.ide.vfs.shared.PrincipalImpl;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfoImpl;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.HashSet;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: CopyTest.java 75032 2011-10-13 15:24:34Z andrew00x $
 */
public class CopyTest extends MemoryFileSystemTest {
    private MemoryFolder copyTestDestinationFolder;
    private MemoryFolder copyTestDestinationProject;
    private MemoryFile   fileForCopy;
    private MemoryFolder folderForCopy;
    private MemoryFolder projectForCopy;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        String name = getClass().getName();
        MemoryFolder copyTestSourceFolder = new MemoryFolder(name);
        testRoot.addChild(copyTestSourceFolder);

        folderForCopy = new MemoryFolder("CopyTest_FOLDER");
        // add child in folder
        MemoryFile file = fileForCopy = new MemoryFile("file", "text/plain",
                                                       new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
        folderForCopy.addChild(file);
        copyTestSourceFolder.addChild(folderForCopy);

        projectForCopy = new MemoryFolder("CopyTest_PROJECT");
        projectForCopy.setMediaType("text/vnd.ideproject+directory");
        assertTrue(projectForCopy.isProject());
        copyTestSourceFolder.addChild(projectForCopy);

        copyTestDestinationFolder = new MemoryFolder(name + "_CopyTest_DESTINATION_FOLDER");
        testRoot.addChild(copyTestDestinationFolder);

        copyTestDestinationProject = new MemoryFolder(name + "_CopyTest_DESTINATION_PROJECT");
        copyTestDestinationProject.setMediaType("text/vnd.ideproject+directory");
        assertTrue(copyTestDestinationProject.isProject());
        testRoot.addChild(copyTestDestinationProject);

        fileForCopy = new MemoryFile("CopyTest_FILE", "text/plain",
                                     new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
        copyTestSourceFolder.addChild(fileForCopy);

        memoryContext.putItem(copyTestSourceFolder);
        memoryContext.putItem(copyTestDestinationFolder);
        memoryContext.putItem(copyTestDestinationProject);
    }

    public void testCopyFile() throws Exception {
        String path = SERVICE_URI + "copy/" + fileForCopy.getId() + '?' + "parentId=" + copyTestDestinationFolder.getId();
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
        assertEquals(200, response.getStatus());
        String expectedPath = copyTestDestinationFolder.getPath() + '/' + fileForCopy.getName();
        assertNotNull("Source file not found. ", memoryContext.getItemByPath(fileForCopy.getPath()));
        assertNotNull("Not found file in destination location. ", memoryContext.getItemByPath(expectedPath));
    }

    public void testCopyFileAlreadyExist() throws Exception {
        MemoryFile existed = new MemoryFile("CopyTest_FILE", "text/plain",
                                            new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
        copyTestDestinationFolder.addChild(existed);
        memoryContext.putItem(existed);
        String path = SERVICE_URI + "copy/" + fileForCopy.getId() + '?' + "parentId=" + copyTestDestinationFolder.getId();
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
        assertEquals(400, response.getStatus());
        assertEquals(ExitCodes.ITEM_EXISTS, Integer.parseInt((String)response.getHttpHeaders().getFirst("X-Exit-Code")));
        assertNotNull("Source file not found. ", memoryContext.getItemByPath(fileForCopy.getPath()));
    }

    public void testCopyFileDestination_NoPermissions() throws Exception {
        AccessControlEntry adminACE = new AccessControlEntryImpl();
        adminACE.setPrincipal(new PrincipalImpl("admin", Principal.Type.USER));
        adminACE.setPermissions(new HashSet<String>(Arrays.asList(VirtualFileSystemInfoImpl.BasicPermissions.ALL.value())));

        AccessControlEntry userACE = new AccessControlEntryImpl();
        userACE.setPrincipal(new PrincipalImpl("john", Principal.Type.USER));
        userACE.setPermissions(new HashSet<String>(Arrays.asList(VirtualFileSystemInfoImpl.BasicPermissions.READ.value())));

        copyTestDestinationFolder.updateACL(Arrays.asList(adminACE, userACE), true);

        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "copy/" + fileForCopy.getId() + '?' + "parentId=" + copyTestDestinationFolder.getId();
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals(403, response.getStatus());
        assertNotNull("Source file not found. ", memoryContext.getItemByPath(fileForCopy.getPath()));
        assertNull("File must not be copied since destination accessible for reading only. ",
                   memoryContext.getItemByPath(copyTestDestinationFolder.getPath() + "/CopyTest_FILE"));
    }

    public void testCopyFolder() throws Exception {
        String path = SERVICE_URI + "copy/" + folderForCopy.getId() + '?' + "parentId=" + copyTestDestinationFolder.getId();
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
        assertEquals(200, response.getStatus());
        String expectedPath = copyTestDestinationFolder.getPath() + "/" + folderForCopy.getName();
        assertNotNull("Source folder not found. ", memoryContext.getItemByPath(folderForCopy.getPath()));
        assertNotNull("Not found folder in destination location. ", memoryContext.getItemByPath(expectedPath));
        assertNotNull("Child of folder missing after coping. ", memoryContext.getItemByPath(expectedPath + "/file"));
    }

    public void testCopyFolderAlreadyExist() throws Exception {
        MemoryFolder existed = new MemoryFolder("CopyTest_FOLDER");
        copyTestDestinationFolder.addChild(existed);
        memoryContext.putItem(existed);
        String path = SERVICE_URI + "copy/" + folderForCopy.getId() + "?" + "parentId=" + copyTestDestinationFolder.getId();
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
        assertEquals(400, response.getStatus());
        assertEquals(ExitCodes.ITEM_EXISTS, Integer.parseInt((String)response.getHttpHeaders().getFirst("X-Exit-Code")));
        assertNotNull("Source folder not found. ", memoryContext.getItemByPath(folderForCopy.getPath()));
    }

    public void testCopyProjectToProject() throws Exception {
        String path = SERVICE_URI + "copy/" + projectForCopy.getId() + '?' +
                      "parentId=" + copyTestDestinationProject.getId();
        final String originPath = projectForCopy.getPath();

        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
        log.info(response.getEntity());
        assertEquals("Unexpected status " + response.getStatus(), 200, response.getStatus());
        String expectedPath = copyTestDestinationProject.getPath() + '/' + projectForCopy.getName();
        assertNotNull("Source project is missed. ", memoryContext.getItemByPath(originPath));
        assertNotNull("Not found project in destination location. ", memoryContext.getItemByPath(expectedPath));
    }
}
