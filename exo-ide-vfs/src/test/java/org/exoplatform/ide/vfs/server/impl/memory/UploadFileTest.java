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
import org.everrest.core.impl.EnvironmentContext;
import org.everrest.test.mock.MockHttpServletRequest;
import org.exoplatform.ide.vfs.server.impl.memory.context.MemoryFile;
import org.exoplatform.ide.vfs.server.impl.memory.context.MemoryFolder;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class UploadFileTest extends MemoryFileSystemTest {
    private String uploadTestFolderId;
    private String uploadTestFolderPath;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        String name = getClass().getName();

        MemoryFolder uploadTestFolder = new MemoryFolder(name);
        testRoot.addChild(uploadTestFolder);
        uploadTestFolderId = uploadTestFolder.getId();
        uploadTestFolderPath = uploadTestFolder.getPath();
        memoryContext.putItem(uploadTestFolder);
    }

    public void testUploadNewFile() throws Exception {
        // Passed by browser.
        String fileName = "testUploadNewFile";
        // File content.
        String fileContent = "test upload file";
        // Passed by browser.
        String fileMediaType = "text/plain; charset=utf8";
        ContainerResponse response = doUploadFile(fileName, fileMediaType, fileContent, "", "", false);
        assertEquals(200, response.getStatus());
        String expectedPath = uploadTestFolderPath + "/" + fileName;
        MemoryFile file = (MemoryFile)memoryContext.getItemByPath(expectedPath);
        assertNotNull("File was not created in expected location. ", file);
        checkFileContext(fileContent, "text/plain;charset=utf8", file);
    }

    public void testUploadNewFileInRootFolder() throws Exception {
        // Passed by browser.
        String fileName = "testUploadNewFile";
        // File content.
        String fileContent = "test upload file";
        // Passed by browser.
        String fileMediaType = "text/plain; charset=utf8";
        uploadTestFolderId = memoryContext.getRoot().getId();
        ContainerResponse response = doUploadFile(fileName, fileMediaType, fileContent, "", "", false);
        assertEquals(200, response.getStatus());
        String expectedPath = "/" + fileName;
        MemoryFile file = (MemoryFile)memoryContext.getItemByPath(expectedPath);
        assertNotNull("File was not created in expected location. ", file);
        checkFileContext(fileContent, "text/plain;charset=utf8", file);
    }

    public void testUploadNewFileCustomizeName() throws Exception {
        // Passed by browser.
        String fileName = "testUploadNewFileCustomizeName";
        // File content.
        String fileContent = "test upload file with custom name";
        // Passed by browser.
        String fileMediaType = "text/plain; charset=utf8";
        // Name of file passed in HTML form. If present it should be used instead of original file name.
        String formFileName = fileName + ".txt";
        ContainerResponse response = doUploadFile(fileName, fileMediaType, fileContent, "", formFileName, false);
        assertEquals(200, response.getStatus());
        String expectedPath = uploadTestFolderPath + "/" + formFileName;
        MemoryFile file = (MemoryFile)memoryContext.getItemByPath(expectedPath);
        assertNotNull("File was not created in expected location. ", file);
        checkFileContext(fileContent, "text/plain;charset=utf8", file);
    }

    public void testUploadNewFileCustomizeMediaType() throws Exception {
        // Passed by browser.
        String fileName = "testUploadNewFileCustomizeMediaType";
        // File content.
        String fileContent = "test upload file with custom media type";
        // Passed by browser.
        String fileMediaType = "application/octet-stream";
        // Name of file passed in HTML form. If present it should be used instead of original file name.
        String formFileName = fileName + ".txt";
        // Media type of file passed in HTML form. If present it should be used instead of original file media type.
        String formMediaType = "text/plain; charset=utf8";
        ContainerResponse response =
                doUploadFile(fileName, fileMediaType, fileContent, formMediaType, formFileName, false);
        assertEquals(200, response.getStatus());
        String expectedPath = uploadTestFolderPath + "/" + formFileName;
        MemoryFile file = (MemoryFile)memoryContext.getItemByPath(expectedPath);
        assertNotNull("File was not created in expected location. ", file);
        checkFileContext(fileContent, "text/plain;charset=utf8", file);
    }

    public void testUploadFileAlreadyExists() throws Exception {
        String fileName = "existedFile";
        String fileMediaType = "application/octet-stream";
        MemoryFolder folder = (MemoryFolder)memoryContext.getItem(uploadTestFolderId);
        MemoryFile file = new MemoryFile(fileName, fileMediaType,
                                         new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
        folder.addChild(file);
        memoryContext.putItem(file);

        ContainerResponse response = doUploadFile(fileName, fileMediaType, DEFAULT_CONTENT, "", "", false);
        assertEquals(200, response.getStatus());
        String entity = (String)response.getEntity();
        assertTrue(entity.contains("File with the same name exists"));
        assertTrue(entity.contains("Code: 102"));
        log.info(entity);
    }

    public void testUploadFileAlreadyExistsOverwrite() throws Exception {
        String fileName = "existedFileOverwrite";
        String fileMediaType = "application/octet-stream";
        MemoryFolder folder = (MemoryFolder)memoryContext.getItem(uploadTestFolderId);
        MemoryFile file = new MemoryFile(fileName, fileMediaType,
                                         new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
        folder.addChild(file);
        memoryContext.putItem(file);

        String fileContent = "test upload and overwrite existed file";
        fileMediaType = "text/plain; charset=utf8";
        ContainerResponse response = doUploadFile(fileName, fileMediaType, fileContent, "", "", true);
        assertEquals(200, response.getStatus());
        String expectedPath = uploadTestFolderPath + "/" + fileName;
        file = (MemoryFile)memoryContext.getItemByPath(expectedPath);
        assertNotNull("File was not created in expected location. ", file);
        checkFileContext(fileContent, "text/plain;charset=utf8", file);
    }

    private ContainerResponse doUploadFile(String fileName, String fileMediaType, String fileContent,
                                           String formMediaType, String formFileName, boolean formOverwrite) throws Exception {
        String path = SERVICE_URI + "uploadfile/" + uploadTestFolderId; //

        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        List<String> contentType = new ArrayList<String>();
        contentType.add("multipart/form-data; boundary=abcdef");
        headers.put("Content-Type", contentType);

        String uploadBodyPattern = "--abcdef\r\n"
                                   + "Content-Disposition: form-data; name=\"file\"; filename=\"%1$s\"\r\nContent-Type: %2$s\r\n\r\n"
                                   + "%3$s\r\n--abcdef\r\nContent-Disposition: form-data; name=\"mimeType\"\r\n\r\n%4$s"
                                   + "\r\n--abcdef\r\nContent-Disposition: form-data; name=\"name\"\r\n\r\n%5$s\r\n"
                                   + "--abcdef\r\nContent-Disposition: form-data; name=\"overwrite\"\r\n\r\n%6$b\r\n--abcdef--\r\n";
        byte[] formData =
                String.format(uploadBodyPattern, fileName, fileMediaType, fileContent, formMediaType, formFileName,
                              formOverwrite).getBytes();
        EnvironmentContext env = new EnvironmentContext();
        env.put(HttpServletRequest.class, new MockHttpServletRequest("", new ByteArrayInputStream(formData),
                                                                     formData.length, "POST", headers));

        return launcher.service("POST", path, BASE_URI, headers, formData, env);
    }
}
