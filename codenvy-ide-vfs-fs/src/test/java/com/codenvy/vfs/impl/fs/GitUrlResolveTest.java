/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.vfs.impl.fs;

import java.net.URI;

/** @author andrew00x */
public class GitUrlResolveTest extends LocalFileSystemTest {
    private String file;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        final String testFolderPath = createDirectory(testRootPath, "GitUrlResolveTest_Folder");
        file = createFile(testFolderPath, "test.txt", DEFAULT_CONTENT_BYTES);
    }

    public void testResolveGitUrlWithPort() throws Exception {
        String expectedUrl = String.format("http://localhost:9000/git/%s", root.toPath().relativize(getIoFile(file).toPath()));
        GitUrlResolver resolver = new GitUrlResolver(root, new LocalPathResolver());
        final String url = resolver.resolve(URI.create("http://localhost:9000/some/path"), mountPoint.getVirtualFile(file));
        assertEquals(expectedUrl, url);
    }

    public void testResolveGitUrlWithoutPort() throws Exception {
        String expectedUrl = String.format("http://localhost/git/%s", root.toPath().relativize(getIoFile(file).toPath()));
        GitUrlResolver resolver = new GitUrlResolver(root, new LocalPathResolver());
        final String url = resolver.resolve(URI.create("http://localhost/some/path"), mountPoint.getVirtualFile(file));
        assertEquals(expectedUrl, url);
    }
}
