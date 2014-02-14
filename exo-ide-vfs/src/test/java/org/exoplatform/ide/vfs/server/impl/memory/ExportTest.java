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
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyImpl;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ExportTest extends MemoryFileSystemTest {
    private String exportTestRootId;
    private String exportFolderId;
    private String exportFolderName;
    private String exportProjectId;

    private Set<String> expectedExportFolderZipItems   = new HashSet<String>();
    private Set<String> expectedExportProjectZipItems  = new HashSet<String>();
    private Set<String> expectedExportTestRootZipItems = new HashSet<String>();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        String name = getClass().getName();
        MemoryFolder exportTestRoot = new MemoryFolder(name);
        testRoot.addChild(exportTestRoot);

        MemoryFolder exportTestFolder = new MemoryFolder("ExportTest_FOLDER");
        exportTestRoot.addChild(exportTestFolder);

//      Create in exportTestFolder folder next files and folders:
//      ----------------------------
//         folder1/
//         folder2/
//         folder3/
//         folder1/file1.txt
//         folder1/folder12/
//         folder2/file2.txt
//         folder2/folder22/
//         folder3/file3.txt
//         folder3/folder32/
//         folder1/folder12/file12.txt
//         folder2/folder22/file22.txt
//         folder3/folder32/file32.txt
//      ----------------------------

        MemoryFolder folder1 = new MemoryFolder("folder1");
        exportTestFolder.addChild(folder1);
        MemoryFolder folder2 = new MemoryFolder("folder2");
        exportTestFolder.addChild(folder2);
        MemoryFolder folder3 = new MemoryFolder("folder3");
        exportTestFolder.addChild(folder3);

        MemoryFile file1 = new MemoryFile("file1.txt", "text/plain", new ByteArrayInputStream(DEFAULT_CONTENT_BYTES));
        folder1.addChild(file1);
        MemoryFile file2 = new MemoryFile("file2.txt", "text/plain", new ByteArrayInputStream(DEFAULT_CONTENT_BYTES));
        folder2.addChild(file2);
        MemoryFile file3 = new MemoryFile("file3.txt", "text/plain", new ByteArrayInputStream(DEFAULT_CONTENT_BYTES));
        folder3.addChild(file3);

        MemoryFolder folder12 = new MemoryFolder("folder12");
        folder1.addChild(folder12);
        MemoryFolder folder22 = new MemoryFolder("folder22");
        folder2.addChild(folder22);
        MemoryFolder folder32 = new MemoryFolder("folder32");
        folder3.addChild(folder32);

        MemoryFile file12 = new MemoryFile("file12.txt", "text/plain", new ByteArrayInputStream(DEFAULT_CONTENT_BYTES));
        folder12.addChild(file12);
        MemoryFile file22 = new MemoryFile("file22.txt", "text/plain", new ByteArrayInputStream(DEFAULT_CONTENT_BYTES));
        folder22.addChild(file22);
        MemoryFile file32 = new MemoryFile("file32.txt", "text/plain", new ByteArrayInputStream(DEFAULT_CONTENT_BYTES));
        folder32.addChild(file32);

        expectedExportFolderZipItems.add("folder1/");
        expectedExportFolderZipItems.add("folder2/");
        expectedExportFolderZipItems.add("folder3/");
        expectedExportFolderZipItems.add("folder1/file1.txt");
        expectedExportFolderZipItems.add("folder1/folder12/");
        expectedExportFolderZipItems.add("folder2/file2.txt");
        expectedExportFolderZipItems.add("folder2/folder22/");
        expectedExportFolderZipItems.add("folder3/file3.txt");
        expectedExportFolderZipItems.add("folder3/folder32/");
        expectedExportFolderZipItems.add("folder1/folder12/file12.txt");
        expectedExportFolderZipItems.add("folder2/folder22/file22.txt");
        expectedExportFolderZipItems.add("folder3/folder32/file32.txt");

        // Project for export that contain one file only.
        MemoryFolder exportTestProject = new MemoryFolder("ExportTest_PROJECT");
        exportTestProject.setMediaType("text/vnd.ideproject+directory");
        exportTestProject.updateProperties(Arrays.<Property>asList(new PropertyImpl("vfs:projectType", "java"),
                                                                   new PropertyImpl("prop1", "val1")));
        assertTrue(exportTestProject.isProject());
        exportTestRoot.addChild(exportTestProject);

        MemoryFile projectFile = new MemoryFile("readme.txt", "text/plain", new ByteArrayInputStream(DEFAULT_CONTENT_BYTES));
        exportTestProject.addChild(projectFile);

        expectedExportProjectZipItems.add(".codenvy");
        expectedExportProjectZipItems.add("readme.txt");

        memoryContext.putItem(exportTestRoot);

        exportFolderId = exportTestFolder.getId();
        exportFolderName = exportTestFolder.getName();
        exportProjectId = exportTestProject.getId();
        exportTestRootId = exportTestRoot.getId();

        expectedExportTestRootZipItems.add("ExportTest_FOLDER/");
        expectedExportTestRootZipItems.add("ExportTest_FOLDER/folder1/");
        expectedExportTestRootZipItems.add("ExportTest_FOLDER/folder2/");
        expectedExportTestRootZipItems.add("ExportTest_FOLDER/folder3/");
        expectedExportTestRootZipItems.add("ExportTest_FOLDER/folder1/file1.txt");
        expectedExportTestRootZipItems.add("ExportTest_FOLDER/folder1/folder12/");
        expectedExportTestRootZipItems.add("ExportTest_FOLDER/folder2/file2.txt");
        expectedExportTestRootZipItems.add("ExportTest_FOLDER/folder2/folder22/");
        expectedExportTestRootZipItems.add("ExportTest_FOLDER/folder3/file3.txt");
        expectedExportTestRootZipItems.add("ExportTest_FOLDER/folder3/folder32/");
        expectedExportTestRootZipItems.add("ExportTest_FOLDER/folder1/folder12/file12.txt");
        expectedExportTestRootZipItems.add("ExportTest_FOLDER/folder2/folder22/file22.txt");
        expectedExportTestRootZipItems.add("ExportTest_FOLDER/folder3/folder32/file32.txt");
        expectedExportTestRootZipItems.add("ExportTest_PROJECT/");
        expectedExportTestRootZipItems.add("ExportTest_PROJECT/.codenvy");
        expectedExportTestRootZipItems.add("ExportTest_PROJECT/readme.txt");
    }

    public void testExportFolder() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "export/" + exportFolderId;
        ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
        assertEquals(200, response.getStatus());
        assertEquals("application/zip", writer.getHeaders().getFirst("Content-Type"));
        checkZipItems(expectedExportFolderZipItems, new ZipInputStream(new ByteArrayInputStream(writer.getBody())));
    }

    public void testExportProject() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "export/" + exportProjectId;
        ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
        assertEquals(200, response.getStatus());
        assertEquals("application/zip", writer.getHeaders().getFirst("Content-Type"));
        checkZipItems(expectedExportProjectZipItems, new ZipInputStream(new ByteArrayInputStream(writer.getBody())));
    }

    public void testExportRootFolder() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "export/" + exportTestRootId;
        ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
        assertEquals(200, response.getStatus());
        assertEquals("application/zip", writer.getHeaders().getFirst("Content-Type"));
        checkZipItems(expectedExportTestRootZipItems, new ZipInputStream(new ByteArrayInputStream(writer.getBody())));
    }

    public void testDownloadZip() throws Exception {
        // Expect the same as 'export in zip' plus header "Content-Disposition".
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "downloadzip/" + exportFolderId;
        ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
        assertEquals(200, response.getStatus());
        assertEquals("application/zip", writer.getHeaders().getFirst("Content-Type"));
        assertEquals("attachment; filename=\"" + exportFolderName + ".zip" + '"',
                     writer.getHeaders().getFirst("Content-Disposition"));
        checkZipItems(expectedExportFolderZipItems, new ZipInputStream(new ByteArrayInputStream(writer.getBody())));
    }

    private void checkZipItems(Set<String> expected, ZipInputStream zip) throws Exception {
        ZipEntry zipEntry;
        while ((zipEntry = zip.getNextEntry()) != null) {
            String name = zipEntry.getName();
         /*if (!zipEntry.isDirectory())
         {
            byte[] buf = new byte[1024];
            int i = zip.read(buf);
            System.out.println(new String(buf, 0, i));
         }*/
            zip.closeEntry();
            assertTrue("Not found " + name + " entry in zip. ", expected.remove(name));
        }
        assertTrue(expected.isEmpty());
    }

}
