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
package org.exoplatform.ide.eclipse.resources;

import com.codenvy.eclipse.core.resources.IFile;
import com.codenvy.eclipse.core.resources.IResource;
import com.codenvy.eclipse.core.runtime.CoreException;
import com.codenvy.eclipse.core.runtime.NullProgressMonitor;
import com.codenvy.eclipse.core.runtime.Path;
import com.codenvy.commons.lang.IoUtil;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Tests for getting, setting and updating contents of file resources.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: FileContentsTest.java Jan 3, 2013 11:10:48 AM azatsarynnyy $
 */
public class FileContentsTest extends ResourcesBaseTest {
    private IFile fileResourceWithoutContent;

    private IFile fileResourceWithContent;

    private IFile fileResourceNonExist;

    private static final String DEFAULT_CONTENT = "test_content";

    @Override
    public void setUp() throws Exception {
        super.setUp();
        fileResourceWithoutContent = (IFile)ws.newResource(new Path("/project/folder/file_without_content"),
                                                           IResource.FILE);
        fileResourceWithoutContent.create(null, true, new NullProgressMonitor());

        fileResourceWithContent = (IFile)ws.newResource(new Path("/project/folder/file_with_content"), IResource.FILE);
        InputStream contentsStream = new ByteArrayInputStream(DEFAULT_CONTENT.getBytes());
        fileResourceWithContent.create(contentsStream, true, new NullProgressMonitor());

        fileResourceNonExist = (IFile)ws.newResource(new Path("/project/folder/file_non_exist"), IResource.FILE);
    }

    @Test
    public void testGetContents() throws Exception {
        String actualContents = IoUtil.readStream(fileResourceWithContent.getContents());
        assertEquals(DEFAULT_CONTENT, actualContents);
    }

    @Test(expected = CoreException.class)
    public void testGetContentsFileNotExist() throws Exception {
        fileResourceNonExist.getContents();
    }

    @Test
    public void testSetContents() throws Exception {
        InputStream contentsStream = new ByteArrayInputStream(DEFAULT_CONTENT.getBytes());
        fileResourceWithoutContent.setContents(contentsStream, true, true, new NullProgressMonitor());

        String actualContents = IoUtil.readStream(fileResourceWithoutContent.getContents());
        assertEquals(DEFAULT_CONTENT, actualContents);
    }

    @Test(expected = CoreException.class)
    public void testSetContentsFileNotExist() throws Exception {
        InputStream contentsStream = new ByteArrayInputStream(DEFAULT_CONTENT.getBytes());
        fileResourceNonExist.setContents(contentsStream, true, true, new NullProgressMonitor());
    }

    @Test
    public void testUpdateContents() throws Exception {
        String existingContents = IoUtil.readStream(fileResourceWithContent.getContents());
        String expectedContents = "test_content_origin";
        assertFalse(existingContents.equals(expectedContents));

        InputStream contentStream = new ByteArrayInputStream(expectedContents.getBytes());
        fileResourceWithContent.setContents(contentStream, true, true, new NullProgressMonitor());

        String actualContents = IoUtil.readStream(fileResourceWithContent.getContents());
        assertEquals(expectedContents, actualContents);
    }

    @Test
    public void testAppendContents() throws Exception {
        String existingContents = IoUtil.readStream(fileResourceWithContent.getContents());
        String contentsToAppend = "test_append_content";

        InputStream contentStream = new ByteArrayInputStream(contentsToAppend.getBytes());
        fileResourceWithContent.appendContents(contentStream, true, true, new NullProgressMonitor());

        String actualContents = IoUtil.readStream(fileResourceWithContent.getContents());
        assertEquals(existingContents + contentsToAppend, actualContents);
    }

}
