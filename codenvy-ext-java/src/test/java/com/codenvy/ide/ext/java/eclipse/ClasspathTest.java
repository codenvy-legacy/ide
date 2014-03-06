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

import com.codenvy.api.project.server.ProjectTypeRegistry;
import com.codenvy.api.project.shared.ProjectType;
import com.codenvy.ide.ext.java.server.BinaryTypeConvector;
import com.codenvy.ide.ext.java.server.internal.core.JavaProject;
import com.codenvy.ide.ext.java.server.internal.core.search.indexing.IndexManager;
import com.codenvy.ide.ext.java.server.internal.core.search.matching.JavaSearchNameEnvironment;
import com.codenvy.ide.ext.java.server.internal.core.search.processing.JobManager;
import com.codenvy.vfs.impl.fs.LocalFileSystemTest;
import com.codenvy.vfs.impl.fs.VirtualFileImpl;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Evgen Vidolob
 */
public class ClasspathTest extends LocalFileSystemTest{
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
    private String folderPath;
    private String folderId;


    private byte[]                    zipProject;


    @Before
    public void init() throws Exception {
        folderPath = createDirectory("/", "project");
        URL testproject = Thread.currentThread().getContextClassLoader().getResource("projects/testproject");
        zipProject = zipFolder(testproject.getFile());
        folderId = pathToId(folderPath);
        String path = SERVICE_URI + "import/" + folderId;
        Map<String, List<String>> headers = new HashMap<>(1);
        headers.put("Content-Type", Arrays.asList("application/zip"));
        launcher.service("POST", path, BASE_URI, headers, zipProject, null, null);
        ProjectTypeRegistry typeRegistry = new ProjectTypeRegistry();
        typeRegistry.registerProjectType(new ProjectType("test_type", "test type"));

    }

    /**
     * zip the folders
     */
    private byte[] zipFolder(String srcFolder) throws Exception {
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
    private void addFileToZip(String path, String srcFile, ZipOutputStream zip, boolean flag) throws Exception {
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
    private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip) throws Exception {
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

    @Test
    public void testRawClasspath() throws Exception {
        VirtualFileImpl file = mountPoint.getVirtualFile("/project");
        JavaProject project = new JavaProject(file, TEMP_DIR);
        IClasspathEntry[] rawClasspath = project.getRawClasspath();
        Assert.assertEquals(3, rawClasspath.length);
    }

    @Test
    public void testResolvedClasspath() throws Exception {
        VirtualFileImpl file = mountPoint.getVirtualFile("/project");
        JavaProject project = new JavaProject(file, TEMP_DIR);
        IClasspathEntry[] classpath = project.getResolvedClasspath();
        assertTrue(classpath.length > 3);
    }

    @Test
    public void testIndexProject() throws Exception {
        IndexManager indexManager = new IndexManager();
        JobManager.VERBOSE = true;
        Thread thread = new Thread(indexManager,"index thread");
        indexManager.reset();
        thread.start();
        VirtualFileImpl file = mountPoint.getVirtualFile("/project");
        JavaProject project = new JavaProject(file, TEMP_DIR);
        indexManager.indexAll(project);
        indexManager.saveIndexes();
        assertTrue(indexManager.indexLocations.elementSize >0);
    }

    @Test
    public void testNameEnviroment() throws Exception {
        VirtualFileImpl file = mountPoint.getVirtualFile("/project");
        JavaProject project = new JavaProject(file, TEMP_DIR);
        JavaSearchNameEnvironment environment = new JavaSearchNameEnvironment(project, null);
        char[][] packages = new char[][]{TypeConstants.JAVA, TypeConstants.LANG};
        NameEnvironmentAnswer answer = environment.findType("String".toCharArray(), packages);
        assertNotNull(answer);
        IBinaryType binaryType = answer.getBinaryType();
        assertNotNull(binaryType);
        assertEquals("String", String.valueOf(binaryType.getSourceName()));
        String type = BinaryTypeConvector.toJsonBinaryType(binaryType);
        System.out.println(type);
    }

    @Test
    public void testEnviromentSearchSource() throws Exception {
        VirtualFileImpl file = mountPoint.getVirtualFile("/project");
        JavaProject project = new JavaProject(file, TEMP_DIR);
        JavaSearchNameEnvironment environment = new JavaSearchNameEnvironment(project, null);
        char[][] packages = new char[][]{"com".toCharArray(), "codenvy".toCharArray(),"test".toCharArray()};
        NameEnvironmentAnswer answer = environment.findType("MyClass".toCharArray(), packages);
        assertNotNull(answer);
        ICompilationUnit compilationUnit = answer.getCompilationUnit();
        assertNotNull(compilationUnit);
        assertEquals("MyClass", new String(compilationUnit.getMainTypeName()));
    }

    @Test
    public void testDoubleConstant() throws Exception {
        VirtualFileImpl file = mountPoint.getVirtualFile("/project");
        JavaProject project = new JavaProject(file, TEMP_DIR);
        JavaSearchNameEnvironment environment = new JavaSearchNameEnvironment(project, null);
        char[][] packages = new char[][]{"java".toCharArray(), "util".toCharArray()};
        NameEnvironmentAnswer answer = environment.findType("HashMap".toCharArray(), packages);
        assertNotNull(answer);
        IBinaryType type = answer.getBinaryType();
        System.out.println(BinaryTypeConvector.toJsonBinaryType(type));
    }
}
