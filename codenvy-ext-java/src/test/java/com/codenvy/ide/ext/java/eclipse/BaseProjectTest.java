/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2014] Codenvy, S.A.
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
package com.codenvy.ide.ext.java.eclipse;

import com.codenvy.api.project.server.ProjectManager;
import com.codenvy.api.project.server.ProjectTypeDescriptionRegistry;
import com.codenvy.api.project.server.ProjectTypeRegistry;
import com.codenvy.api.project.server.ValueProviderFactory;
import com.codenvy.api.project.shared.ProjectType;
import com.codenvy.vfs.impl.fs.LocalFileSystemTest;

import org.junit.BeforeClass;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Evgen Vidolob
 */
public class BaseProjectTest extends LocalFileSystemTest {
    protected static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
    protected static String folderPath;
    protected static String folderId;


    protected static byte[]         zipProject;
    protected static   ProjectManager pm;

    @BeforeClass
    public static void init() throws Exception {
        ProjectTypeRegistry typeRegistry = new ProjectTypeRegistry();
        typeRegistry.registerProjectType(new ProjectType("test_type", "test type"));
        ProjectTypeDescriptionRegistry ptdr = new ProjectTypeDescriptionRegistry(typeRegistry);
        Set<ValueProviderFactory> vpf = Collections.EMPTY_SET;
        pm = new ProjectManager(typeRegistry, ptdr, vpf, virtualFileSystemRegistry);
        folderPath = createDirectory("/", "project");
        URL testproject = Thread.currentThread().getContextClassLoader().getResource("projects/testproject");
        zipProject = zipFolder(testproject.getFile());
        folderId = pathToId(folderPath);
        String path = SERVICE_URI + "import/" + folderId;
        Map<String, List<String>> headers = new HashMap<>(1);
        headers.put("Content-Type", Arrays.asList("application/zip"));
        launcher.service("POST", path, BASE_URI, headers, zipProject, null, null);

    }

    /**
     * zip the folders
     */
    private static byte[] zipFolder(String srcFolder) throws Exception {
        ZipOutputStream zip = null;
        /*
         * create the output stream to zip file result
         */
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        zip = new ZipOutputStream(bout);
        File folder = new File(srcFolder + "/");
        zip.putNextEntry(new ZipEntry(".project"));
        zip.write(("[{\"name\":\"vfs:projectType\",\"value\":[\"jar\"]}, {\"name\":\"language\",\"value\":[\"java\"]}, " +
                   "{\"name\":\"builder.name\",\"value\":[\"maven\"]}]").getBytes());

        for (String fileName : folder.list()) {
            addFileToZip("", srcFolder + "/" + fileName, zip, false);

        }
        /*
         * close the zip objects
         */
        zip.flush();
        zip.close();
        return  bout.toByteArray();
    }

    /*
     * recursively add files to the zip files
     */
    private static void addFileToZip(String path, String srcFile, ZipOutputStream zip, boolean flag) throws Exception {
        /*
         * create the file object for inputs
         */
        File folder = new File(srcFile);

        /*
         * if the folder is empty add empty folder to the Zip file
         */
        if (flag) {
            zip.putNextEntry(new ZipEntry(path + "/" + folder.getName() + "/"));
        } else { /*
                 * if the current name is directory, recursively traverse it
                 * to get the files
                 */
            if (folder.isDirectory()) {
                /*
                 * if folder is not empty
                 */
                addFolderToZip(path, srcFile, zip);
            } else {
                /*
                 * write the file to the output
                 */
                byte[] buf = new byte[1024];
                int len;
                FileInputStream in = new FileInputStream(srcFile);
                zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
                while ((len = in.read(buf)) > 0) {
                    /*
                     * Write the Result
                     */
                    zip.write(buf, 0, len);
                }
            }
        }
    }

    /*
     * add folder to the zip file
     */
    private static void addFolderToZip(String path, String srcFolder, ZipOutputStream zip) throws Exception {
        File folder = new File(srcFolder);

        /*
         * check the empty folder
         */
        if (folder.list().length == 0) {
            System.out.println(folder.getName());
            addFileToZip(path, srcFolder, zip, true);
        } else {
            /*
             * list the files in the folder
             */
            for (String fileName : folder.list()) {
                if (path.equals("")) {
                    addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip, false);
                } else {
                    addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip, false);
                }
            }
        }
    }
}
