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
package org.exoplatform.ide.vfs.impl.fs;

import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.JsonValue;
import org.everrest.core.impl.provider.json.ObjectBuilder;
import org.everrest.core.tools.ByteArrayContainerResponseWriter;
import org.exoplatform.ide.vfs.shared.Principal;
import org.exoplatform.ide.vfs.shared.PrincipalImpl;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.BasicPermissions;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static com.codenvy.commons.lang.IoUtil.deleteRecursive;
import static com.codenvy.commons.lang.ZipUtils.unzip;

public class ExportTest extends LocalFileSystemTest {
    private String fileId;
    private String filePath;

    private String folderId;
    private String folderPath;

    private String protectedFolderId;
    private String protectedFolderPath;

    private String projectId;
    private String projectPath;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        folderPath = createDirectory(testRootPath, "ExportTest_Folder");
        createTree(folderPath, 6, 4, null);
        projectPath = createDirectory(testRootPath, "ExportTest_Project");
        createTree(projectPath, 6, 4, null);
        Map<String, String[]> projectProperties = new HashMap<String, String[]>(2);
        projectProperties.put("vfs:mimeType", new String[]{Project.PROJECT_MIME_TYPE});
        projectProperties.put("vfs:projectType", new String[]{"java"});
        writeProperties(projectPath, projectProperties);
        filePath = createFile(testRootPath, "ExportTest_File", null);
        protectedFolderPath = createDirectory(testRootPath, "ExportTest_ProtectedFolder");
        createTree(protectedFolderPath, 6, 4, null);

        Map<Principal, Set<BasicPermissions>> permissions = new HashMap<Principal, Set<BasicPermissions>>(1);
        permissions.put(new PrincipalImpl("andrew", Principal.Type.USER), EnumSet.of(BasicPermissions.ALL));
        writePermissions(protectedFolderPath, permissions);

        folderId = pathToId(folderPath);
        projectId = pathToId(projectPath);
        fileId = pathToId(filePath);
        protectedFolderId = pathToId(protectedFolderPath);
    }

    public void testExportFolder() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "export/" + folderId;
        ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        assertEquals("application/zip", writer.getHeaders().getFirst("Content-Type"));

        java.io.File unzip = getIoFile(createDirectory(testRootPath, "__unzip__"));
        unzip(new ByteArrayInputStream(writer.getBody()), unzip);
        compareDirectories(getIoFile(folderPath), unzip);
    }

    public void testExportFolderHavePermissions() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "export/" + protectedFolderId;
        // Replace default principal by principal who has read permission.
        ConversationState user = new ConversationState(new Identity("andrew"));
        ConversationState.setCurrent(user);
        ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        assertEquals("application/zip", writer.getHeaders().getFirst("Content-Type"));

        java.io.File unzip = getIoFile(createDirectory(testRootPath, "__unzip__"));
        unzip(new ByteArrayInputStream(writer.getBody()), unzip);
        compareDirectories(getIoFile(protectedFolderPath), unzip);
    }

    public void testExportFolderNoPermissions() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "export/" + protectedFolderId;
        ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
        assertEquals(403, response.getStatus());
        log.info(new String(writer.getBody()));
    }

    public void testExportFolderNoPermissions2() throws Exception {
        Map<Principal, Set<BasicPermissions>> permissions = new HashMap<Principal, Set<BasicPermissions>>(1);
        permissions.put(new PrincipalImpl("andrew", Principal.Type.USER), EnumSet.of(BasicPermissions.ALL));
        List<String> l = flattenDirectory(folderPath);
        // Find one child in the list and remove write permission for 'admin'.
        String myProtectedItemPath = folderPath + '/' + l.get(new Random().nextInt(l.size()));
        writePermissions(myProtectedItemPath, permissions);

        // From now have permission to read folder but have not permission to read 'myProtectedItemPath' .
        // It should not be in result zip.
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "export/" + folderId;
        ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        assertEquals("application/zip", writer.getHeaders().getFirst("Content-Type"));

        java.io.File unzip = getIoFile(createDirectory(testRootPath, "__unzip__"));
        unzip(new ByteArrayInputStream(writer.getBody()), unzip);

        // Remove file from source folder and compare directories
        assertTrue(deleteRecursive(getIoFile(myProtectedItemPath)));
        compareDirectories(getIoFile(folderPath), unzip);
    }

    public void testExportProject() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "export/" + projectId;
        ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        assertEquals("application/zip", writer.getHeaders().getFirst("Content-Type"));

        java.io.File unzip = getIoFile(createDirectory(testRootPath, "__unzip__"));
        unzip(new ByteArrayInputStream(writer.getBody()), unzip);
        java.io.File dotProject = new java.io.File(unzip, ".project");
        Property[] properties = parseDotProjectFile(dotProject);
        assertEquals(2, properties.length);
        for (Property property : properties) {
            if ("vfs:mimeType".equals(property.getName())) {
                assertEquals(1, property.getValue().size());
                assertEquals(Project.PROJECT_MIME_TYPE, property.getValue().get(0));
            } else if ("vfs:projectType".equals(property.getName())) {
                assertEquals(1, property.getValue().size());
                assertEquals("java", property.getValue().get(0));
            } else {
                fail("Unexpected property " + property);
            }
        }


        assertTrue(dotProject.delete());
        // now compare directories as usual
        compareDirectories(getIoFile(projectPath), unzip);
    }

    public void testExportMultiProject() throws Exception {
        // create one more project inside existed one.
        String nestedProjectPath = createDirectory(projectPath, "Nested_Project");
        createTree(nestedProjectPath, 6, 4, null);
        Map<String, String[]> props = new HashMap<String, String[]>(2);
        props.put("vfs:mimeType", new String[]{Project.PROJECT_MIME_TYPE});
        props.put("vfs:projectType", new String[]{"my_project"});
        writeProperties(nestedProjectPath, props);

        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "export/" + projectId;
        ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        assertEquals("application/zip", writer.getHeaders().getFirst("Content-Type"));

        java.io.File unzip = getIoFile(createDirectory(testRootPath, "__unzip__"));
        unzip(new ByteArrayInputStream(writer.getBody()), unzip);

        // properties of parent project
        java.io.File dotProject = new java.io.File(unzip, ".project");
        Property[] properties = parseDotProjectFile(dotProject);
        assertEquals(2, properties.length);
        for (Property property : properties) {
            if ("vfs:mimeType".equals(property.getName())) {
                assertEquals(1, property.getValue().size());
                assertEquals(Project.PROJECT_MIME_TYPE, property.getValue().get(0));
            } else if ("vfs:projectType".equals(property.getName())) {
                assertEquals(1, property.getValue().size());
                assertEquals("java", property.getValue().get(0));
            } else {
                fail("Unexpected property " + property);
            }
        }


        // properties of nested project
        java.io.File nestedDotProject = new java.io.File(new java.io.File(unzip, "Nested_Project"), ".project");
        properties = parseDotProjectFile(nestedDotProject);
        assertEquals(2, properties.length);
        for (Property property : properties) {
            if ("vfs:mimeType".equals(property.getName())) {
                assertEquals(1, property.getValue().size());
                assertEquals(Project.PROJECT_MIME_TYPE, property.getValue().get(0));
            } else if ("vfs:projectType".equals(property.getName())) {
                assertEquals(1, property.getValue().size());
                assertEquals("my_project", property.getValue().get(0));
            } else {
                fail("Unexpected property " + property);
            }
        }

        assertTrue(dotProject.delete());
        assertTrue(nestedDotProject.delete());
        // now compare directories as usual
        compareDirectories(getIoFile(projectPath), unzip);
    }

    private Property[] parseDotProjectFile(java.io.File dotProject) throws Exception {
        FileInputStream fIn = new FileInputStream(dotProject);
        try {
            JsonParser parser = new JsonParser();
            parser.parse(fIn);
            JsonValue json = parser.getJsonObject();
            return (Property[])ObjectBuilder.createArray(Property[].class, json);
        } finally {
            fIn.close();
        }
    }

    public void testDownloadZip() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "downloadzip/" + folderId;
        ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        assertEquals("application/zip", writer.getHeaders().getFirst("Content-Type"));
        assertEquals("attachment; filename=\"" + getIoFile(folderPath).getName() + ".zip" + '"',
                     writer.getHeaders().getFirst("Content-Disposition"));
    }

    public void testExportFile() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "export/" + fileId;
        ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
        assertEquals(400, response.getStatus());
        log.info(new String(writer.getBody()));
        assertTrue(exists(filePath));
    }
}
