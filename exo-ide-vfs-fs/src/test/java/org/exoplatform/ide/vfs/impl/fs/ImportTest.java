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
import org.everrest.core.impl.EnvironmentContext;
import org.everrest.test.mock.MockHttpServletRequest;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Principal;
import org.exoplatform.ide.vfs.shared.PrincipalImpl;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.BasicPermissions;

import javax.servlet.http.HttpServletRequest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static com.codenvy.commons.lang.IoUtil.copy;
import static com.codenvy.commons.lang.ZipUtils.zipDir;

public class ImportTest extends LocalFileSystemTest {
    private final byte[] existedFileContent = "existed file".getBytes();

    private String folderPath;
    private String folderId;

    private String protectedFolderPath;
    private String protectedFolderId;

    private String folderWithFilesPath;
    private String folderWithFilesId;
    private String existedFile;

    private byte[] zipFolder;
    private byte[] zipProject;

    private String srcFolderPath;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        folderPath = createDirectory(testRootPath, "ImportTest");
        folderWithFilesPath = createDirectory(testRootPath, "ImportTest_WithFiles");
        protectedFolderPath = createDirectory(testRootPath, "ImportTest_Protected");

        Map<Principal, Set<BasicPermissions>> permissions = new HashMap<Principal, Set<BasicPermissions>>(2);
        permissions.put(new PrincipalImpl("admin", Principal.Type.USER), EnumSet.of(BasicPermissions.READ));
        permissions.put(new PrincipalImpl("andrew", Principal.Type.USER), EnumSet.of(BasicPermissions.READ, BasicPermissions.WRITE));
        writePermissions(protectedFolderPath, permissions);

        srcFolderPath = createDirectory(testRootPath, "ImportTestFolderSource");
        createTree(srcFolderPath, 6, 4, null);

        // Destination folder for import contains file with name which cause conflict with imported structure.
        List<String> l = flattenDirectory(srcFolderPath);
        Random random = new Random();
        // select any file
        String file;
        for (; ; ) {
            file = l.get(random.nextInt(l.size()));
            if (getIoFile(srcFolderPath + '/' + file).isFile()) {
                break;
            }
        }
        // copy it to import destination.
        copy(getIoFile(srcFolderPath + '/' + file), getIoFile(folderWithFilesPath + '/' + file), null);
        // Update content, will check it later.
        writeFile(folderWithFilesPath + '/' + file, existedFileContent);
        existedFile = folderWithFilesPath + '/' + file;

        java.io.File srcIoDir = getIoFile(srcFolderPath);
        java.io.File zipped = getIoFile(createFile(testRootPath, "__file__.zip", null));
        zipDir(srcIoDir.getAbsolutePath(), srcIoDir, zipped, null);
        FileInputStream in = new FileInputStream(zipped);
        zipFolder = new byte[(int)zipped.length()];
        in.read(zipFolder);
        in.close();
        zipped.delete();

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ZipOutputStream zbout = new ZipOutputStream(bout);
        zbout.putNextEntry(new ZipEntry(".project"));
        zbout.write(("[{\"name\":\"vfs:mimeType\",\"value\":[\"text/vnd.ideproject+directory\"]}," +
                     "{\"name\":\"vfs:projectType\",\"value\":[\"test\"]}]").getBytes());
        zbout.closeEntry();
        ByteArrayInputStream bin = new ByteArrayInputStream(zipFolder);
        ZipInputStream zbin = new ZipInputStream(bin);
        ZipEntry copyEntry;
        byte[] buf = new byte[32];
        while ((copyEntry = zbin.getNextEntry()) != null) {
            zbout.putNextEntry(new ZipEntry(copyEntry));
            if (!copyEntry.isDirectory()) {
                int r;
                while ((r = zbin.read(buf)) != -1) {
                    zbout.write(buf, 0, r);
                }
            }
            zbin.closeEntry();
            zbout.closeEntry();
        }
        zbin.close();
        zbout.close();
        zipProject = bout.toByteArray();

        folderId = pathToId(folderPath);
        folderWithFilesId = pathToId(folderWithFilesPath);
        protectedFolderId = pathToId(protectedFolderPath);
    }

    public void testImport() throws Exception {
        String path = SERVICE_URI + "import/" + folderId;
        Map<String, List<String>> headers = new HashMap<String, List<String>>(1);
        headers.put("Content-Type", Arrays.asList("application/zip"));
        ContainerResponse response = launcher.service("POST", path, BASE_URI, headers, zipFolder, null, null);
        assertEquals(204, response.getStatus());

        // Check imported structure.
        compareDirectories(srcFolderPath, folderPath);
    }

    public void testImportNoPermissions() throws Exception {
        String path = SERVICE_URI + "import/" + protectedFolderId;
        Map<String, List<String>> headers = new HashMap<String, List<String>>(1);
        headers.put("Content-Type", Arrays.asList("application/zip"));
        ContainerResponse response = launcher.service("POST", path, BASE_URI, headers, zipFolder, null, null);
        assertEquals(403, response.getStatus());
        assertTrue(flattenDirectory(protectedFolderPath).isEmpty()); // any file must not be created
    }

    public void testImportFileExists() throws Exception {
        String path = SERVICE_URI + "import/" + folderWithFilesId;
        Map<String, List<String>> headers = new HashMap<String, List<String>>(1);
        headers.put("Content-Type", Arrays.asList("application/zip"));
        ContainerResponse response = launcher.service("POST", path, BASE_URI, headers, zipFolder, null, null);
        assertEquals(400, response.getStatus());

        // File which prevents import must not be updated.
        assertTrue(Arrays.equals(existedFileContent, readFile(existedFile)));
    }

    public void testImportFileExists_Overwrite() throws Exception {
        String path = SERVICE_URI + "import/" + folderWithFilesId + '?' + "overwrite=" + true;
        Map<String, List<String>> headers = new HashMap<String, List<String>>(1);
        headers.put("Content-Type", Arrays.asList("application/zip"));
        ContainerResponse response = launcher.service("POST", path, BASE_URI, headers, zipFolder, null, null);
        assertEquals(204, response.getStatus()); // content of existed file overwritten

        // Check imported structure.
        compareDirectories(srcFolderPath, folderWithFilesPath);
    }

    public void testImportFileExistsAndLocked() throws Exception {
        createLock(existedFile, "1234567890");
        String path = SERVICE_URI + "import/" + folderWithFilesId + '?' + "overwrite=" + true;
        Map<String, List<String>> headers = new HashMap<String, List<String>>(1);
        headers.put("Content-Type", Arrays.asList("application/zip"));
        ContainerResponse response = launcher.service("POST", path, BASE_URI, headers, zipFolder, null, null);
        assertEquals(423, response.getStatus());

        // File which prevents import must not be updated.
        // File is locked so 'overwrite' parameters does not help.
        assertTrue(Arrays.equals(existedFileContent, readFile(existedFile)));
    }

    public void testImportFileExistsAndProtected() throws Exception {
        Map<Principal, Set<BasicPermissions>> permissions = new HashMap<Principal, Set<BasicPermissions>>(2);
        permissions.put(new PrincipalImpl("admin", Principal.Type.USER), EnumSet.of(BasicPermissions.READ));
        permissions.put(new PrincipalImpl("andrew", Principal.Type.USER), EnumSet.of(BasicPermissions.READ, BasicPermissions.WRITE));
        writePermissions(existedFile, permissions);

        String path = SERVICE_URI + "import/" + folderWithFilesId + '?' + "overwrite=" + true;
        Map<String, List<String>> headers = new HashMap<String, List<String>>(1);
        headers.put("Content-Type", Arrays.asList("application/zip"));
        ContainerResponse response = launcher.service("POST", path, BASE_URI, headers, zipFolder, null, null);
        assertEquals(403, response.getStatus());

        // File which prevents import must not be updated.
        // File is protected by ACL so 'overwrite' parameters does not help.
        assertTrue(Arrays.equals(existedFileContent, readFile(existedFile)));
    }

    public void testImportProject() throws Exception {
        String path = SERVICE_URI + "import/" + folderId;
        Map<String, List<String>> headers = new HashMap<String, List<String>>(1);
        headers.put("Content-Type", Arrays.asList("application/zip"));
        ContainerResponse response = launcher.service("POST", path, BASE_URI, headers, zipProject, null, null);
        assertEquals(204, response.getStatus());

        // Check imported structure.
        compareDirectories(srcFolderPath, folderPath);

        // Check properties. Regular folder must become to project.
        // 1. check backend
        Map<String, String[]> expectedProperties = new HashMap<String, String[]>(2);
        expectedProperties.put("vfs:mimeType", new String[]{Project.PROJECT_MIME_TYPE});
        expectedProperties.put("vfs:projectType", new String[]{"test"});
        validateProperties(folderPath, expectedProperties);

        // 2. check API
        Item item = getItem(folderId);
        assertEquals(ItemType.PROJECT, item.getItemType());
        assertEquals(Project.PROJECT_MIME_TYPE, item.getMimeType());
        assertEquals("test", ((Project)item).getProjectType());
    }

    public void testUploadZipFolder() throws Exception {
        // Do the same as 'import' but send content in HTML form.
        String path = SERVICE_URI + "uploadzip/" + folderId;
        Map<String, List<String>> headers = new HashMap<String, List<String>>(1);
        headers.put("Content-Type", Arrays.asList("multipart/form-data; boundary=abcdef"));

        // Build multipart request.
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        buf.write(("--abcdef\r\nContent-Disposition: form-data; name=\"file\"; filename=\"zippedfolder\"\r\n" +
                   "Content-Type: application/zip\r\n\r\n").getBytes());
        buf.write(zipFolder);
        buf.write("\r\n--abcdef--".getBytes());
        byte[] body = buf.toByteArray();

        // Need set EnvironmentContext.  HttpServletRequest used to obtain HTML form data.
        EnvironmentContext env = new EnvironmentContext();
        env.put(HttpServletRequest.class, new MockHttpServletRequest("", new ByteArrayInputStream(body), body.length,
                                                                     "POST", headers));

        ContainerResponse response = launcher.service("POST", path, BASE_URI, headers, body, null, env);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());

        // Check imported structure.
        compareDirectories(srcFolderPath, folderPath);
    }

    public void testZipBomb() throws Exception {
        final int uncompressedSize = 1000001;
        // Uncompressed size bigger then 1000000 (~1M).
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(bout);
        zip.putNextEntry(new ZipEntry("null"));
        for (int i = 0; i < uncompressedSize; i++) {
            zip.write(0);
        }
        zip.closeEntry();
        zip.close();
        byte b[] = bout.toByteArray();
        // Be sure source data for test is correct. Zero data should be compressed with very high ratio.
        assertTrue((uncompressedSize / b.length) > 100);
        String path = SERVICE_URI + "import/" + folderId;
        Map<String, List<String>> headers = new HashMap<String, List<String>>(1);
        headers.put("Content-Type", Arrays.asList("application/zip"));
        ContainerResponse response = launcher.service("POST", path, BASE_URI, headers, b, null, null);
        // Exception must be thrown.
        assertEquals(500, response.getStatus());
        log.info(response.getEntity());
    }
}
