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

import static org.junit.Assert.*;

import com.codenvy.eclipse.core.resources.IFile;
import com.codenvy.eclipse.core.resources.IFolder;
import com.codenvy.eclipse.core.resources.IProject;
import com.codenvy.eclipse.core.resources.IResource;
import com.codenvy.eclipse.core.runtime.CoreException;
import com.codenvy.eclipse.core.runtime.NullProgressMonitor;
import com.codenvy.eclipse.core.runtime.Path;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests getting members of container resource.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: MembersTest.java Jan 3, 2013 11:10:48 AM azatsarynnyy $
 */
public class MembersTest extends ResourcesBaseTest {
    private IProject projectResource;

    private IProject projectResourceNotExist;

    private IFolder children1;

    private IFile children2;

    private IFile children3;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        projectResource = (IProject)ws.newResource(new Path("/project"), IResource.PROJECT);
        projectResource.create(new NullProgressMonitor());

        projectResourceNotExist = (IProject)ws.newResource(new Path("/project_not_exist"), IResource.PROJECT);

        children1 = (IFolder)ws.newResource(projectResource.getFullPath().append("folder"), IResource.FOLDER);
        children1.create(true, true, new NullProgressMonitor());
        children2 = (IFile)ws.newResource(projectResource.getFullPath().append("file"), IResource.FILE);
        children2.create(null, true, new NullProgressMonitor());
        children3 = (IFile)ws.newResource(projectResource.getFullPath().append("folder/file"), IResource.FILE);
        children3.create(null, true, new NullProgressMonitor());
    }

    @Test
    public void testGetMembers() throws Exception {
        IResource[] members = projectResource.members();
        List<String> memberPathList = new ArrayList<String>(members.length);
        for (IResource member : members) {
            memberPathList.add(member.getFullPath().toString());
        }

        assertTrue(memberPathList.contains(children1.getFullPath().toString()));
        assertTrue(memberPathList.contains(children2.getFullPath().toString()));
        assertFalse("Members of a project or folder must be are the files and folders immediately contained within it.",
                    memberPathList.contains(children3.getFullPath().toString()));
    }

    @Test
    public void testFindMemberByPath() throws Exception {
        IResource foundMember1 = projectResource.findMember(children1.getFullPath());
        assertEquals(children1.getFullPath(), foundMember1.getFullPath());

        IResource foundMember2 = projectResource.findMember(children2.getFullPath());
        assertEquals(children2.getFullPath(), foundMember2.getFullPath());

        IResource foundMember3 = projectResource.findMember(children3.getFullPath());
        assertNotNull(foundMember3);
    }

    @Test
    public void testFindMemberByStringPath() throws Exception {
        IResource foundMember1 = projectResource.findMember(children1.getFullPath().toString());
        assertEquals(children1.getFullPath(), foundMember1.getFullPath());

        IResource foundMember2 = projectResource.findMember(children2.getFullPath().toString());
        assertEquals(children2.getFullPath(), foundMember2.getFullPath());

        IResource foundMember3 = projectResource.findMember(children3.getFullPath().toString());
        assertNotNull(foundMember3);
    }

    @Test(expected = CoreException.class)
    public void testGetMembers_ContainerNotExist() throws Exception {
        projectResourceNotExist.members();
    }

}
