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
