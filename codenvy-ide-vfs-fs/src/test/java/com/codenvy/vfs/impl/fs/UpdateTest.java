/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.vfs.impl.fs;

import com.codenvy.api.vfs.shared.dto.Item;
import com.codenvy.api.vfs.shared.dto.Principal;
import com.codenvy.commons.env.EnvironmentContext;
import com.codenvy.commons.user.UserImpl;
import com.codenvy.dto.server.DtoFactory;

import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.tools.ByteArrayContainerResponseWriter;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.codenvy.api.vfs.shared.dto.VirtualFileSystemInfo.BasicPermissions;

public class UpdateTest extends LocalFileSystemTest {
    private final String lockToken = "01234567890abcdef";

    private String fileId;
    private String filePath;

    private String folderId;
    private String folderPath;

    private String lockedFilePath;
    private String lockedFileId;

    private String protectedFileId;
    private String protectedFilePath;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        filePath = createFile(testRootPath, "UpdateTest_File", DEFAULT_CONTENT_BYTES);
        folderPath = createDirectory(testRootPath, "UpdateTest_Folder");
        lockedFilePath = createFile(testRootPath, "UpdateTest_LockedFile", DEFAULT_CONTENT_BYTES);
        protectedFilePath = createFile(testRootPath, "UpdateTest_ProtectedFile", DEFAULT_CONTENT_BYTES);

        Map<Principal, Set<BasicPermissions>> permissions = new HashMap<>(2);
        Principal user = DtoFactory.getInstance().createDto(Principal.class).withName("andrew").withType(Principal.Type.USER);
        Principal admin = DtoFactory.getInstance().createDto(Principal.class).withName("admin").withType(Principal.Type.USER);
        permissions.put(user, EnumSet.of(BasicPermissions.ALL));
        permissions.put(admin, EnumSet.of(BasicPermissions.READ));

        writePermissions(protectedFilePath, permissions);
        createLock(lockedFilePath, lockToken, Long.MAX_VALUE);

        fileId = pathToId(filePath);
        lockedFileId = pathToId(lockedFilePath);
        protectedFileId = pathToId(protectedFilePath);
        folderId = pathToId(folderPath);
    }

    public void testUpdatePropertiesFile() throws Exception {
        String update = "[{\"name\":\"MyProperty\", \"value\":[\"MyValue\"]}]";

        String requestPath = SERVICE_URI + "item/" + fileId;
        Map<String, List<String>> h = new HashMap<>(1);
        h.put("Content-Type", Arrays.asList("application/json"));
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, h, update.getBytes(), null);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());

        Map<String, String[]> expectedProperties = new HashMap<>(1);
        expectedProperties.put("MyProperty", new String[]{"MyValue"});
        validateProperties(filePath, expectedProperties);

        Item item = getItem(fileId);
        assertEquals("MyValue", getPropertyValue(item, "MyProperty"));
    }

    public void testUpdatePropertiesFile2() throws Exception {
        Map<String, String[]> props = new HashMap<>(3);
        props.put("a", new String[]{"to be or not to be"});
        props.put("b", new String[]{"hello world"});
        props.put("c", new String[]{"test"});
        writeProperties(filePath, props);

        String update = "[{\"name\":\"b\", \"value\":[\"TEST\"]}," //
                        + "{\"name\":\"c\", \"value\":[\"TEST\"]}," //
                        + "{\"name\":\"d\", \"value\":[\"TEST\", \"TEST\"]}]";

        String requestPath = SERVICE_URI + "item/" + fileId;
        Map<String, List<String>> h = new HashMap<>(1);
        h.put("Content-Type", Arrays.asList("application/json"));
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, h, update.getBytes(), null);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());

        Map<String, String[]> expectedProperties = new HashMap<>(4);
        expectedProperties.put("a", new String[]{"to be or not to be"});
        expectedProperties.put("b", new String[]{"TEST"});
        expectedProperties.put("c", new String[]{"TEST"});
        expectedProperties.put("d", new String[]{"TEST", "TEST"});
        validateProperties(filePath, expectedProperties);

        Item item = getItem(fileId);
        assertEquals("to be or not to be", getPropertyValue(item, "a")); // not updated
        assertEquals("TEST", getPropertyValue(item, "b"));
        assertEquals("TEST", getPropertyValue(item, "c"));
        assertEquals(Arrays.asList("TEST", "TEST"), getPropertyValues(item, "d"));
    }

    public void testUpdateLockedFile() throws Exception {
        String properties = "[{\"name\":\"MyProperty\", \"value\":[\"MyValue\"]}]";

        String requestPath = SERVICE_URI + "item/" + lockedFileId + '?' + "lockToken=" + lockToken;
        Map<String, List<String>> h = new HashMap<>(1);
        h.put("Content-Type", Arrays.asList("application/json"));
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, h, properties.getBytes(), null);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());

        Map<String, String[]> expectedProperties = new HashMap<>(1);
        expectedProperties.put("MyProperty", new String[]{"MyValue"});
        validateProperties(lockedFilePath, expectedProperties);

        Item item = getItem(lockedFileId);
        assertEquals("MyValue", getPropertyValue(item, "MyProperty"));
    }

    public void testUpdateLockedFileNoLockToken() throws Exception {
        String properties = "[{\"name\":\"MyProperty\", \"value\":[\"MyValue\"]}]";

        String requestPath = SERVICE_URI + "item/" + lockedFileId;
        Map<String, List<String>> h = new HashMap<>(1);
        h.put("Content-Type", Arrays.asList("application/json"));
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, h, properties.getBytes(), writer, null);
        assertEquals(423, response.getStatus());
        log.info(new String(writer.getBody()));

        assertNull("Properties must not be updated. ", readProperties(lockedFilePath));

        Item item = getItem(lockedFileId);
        assertNull(getPropertyValue(item, "MyProperty"));
    }

    public void testUpdateFileHavePermissions() throws Exception {
        String properties = "[{\"name\":\"MyProperty\", \"value\":[\"MyValue\"]}]";

        String requestPath = SERVICE_URI + "item/" + protectedFileId;
        Map<String, List<String>> h = new HashMap<>(1);
        h.put("Content-Type", Arrays.asList("application/json"));
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        // File is protected and default principal 'andrew' has not write permission.
        // Replace default principal by principal who has write permission.
        EnvironmentContext.getCurrent().setUser(new UserImpl("andrew"));
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, h, properties.getBytes(), writer, null);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());

        Map<String, String[]> expectedProperties = new HashMap<>(1);
        expectedProperties.put("MyProperty", new String[]{"MyValue"});
        validateProperties(protectedFilePath, expectedProperties);

        Item item = getItem(protectedFileId);
        assertEquals("MyValue", getPropertyValue(item, "MyProperty"));
    }

    public void testUpdateFileNoPermissions() throws Exception {
        String properties = "[{\"name\":\"MyProperty\", \"value\":[\"MyValue\"]}]";

        String requestPath = SERVICE_URI + "item/" + protectedFileId;
        Map<String, List<String>> h = new HashMap<>(1);
        h.put("Content-Type", Arrays.asList("application/json"));
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, h, properties.getBytes(), writer, null);
        assertEquals(403, response.getStatus());
        log.info(new String(writer.getBody()));

        assertNull("Properties must not be updated.", readProperties(protectedFilePath));

        Item item = getItem(protectedFileId);
        assertNull(getPropertyValue(item, "MyProperty"));
    }
}
