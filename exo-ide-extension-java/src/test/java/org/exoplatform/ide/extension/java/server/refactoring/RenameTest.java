/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.extension.java.server.refactoring;

import com.codenvy.eclipse.core.resources.ResourcesPlugin;
import com.codenvy.eclipse.core.runtime.CoreException;
import com.codenvy.eclipse.resources.WorkspaceResource;

import org.apache.commons.io.IOUtils;
import org.exoplatform.ide.extension.java.server.JavaDocBuilderVfsTest;
import org.exoplatform.ide.extension.java.server.RefactoringService;
import org.exoplatform.ide.vfs.server.ContentStream;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class RenameTest extends ResourcesBaseTest {


    private Folder project;

    @Before
    public void before() throws VirtualFileSystemException, IOException {
        if (ResourcesPlugin.getDefaultWorkspace() == null) {
            ResourcesPlugin.setDefaultWorkspace(new WorkspaceResource(vfs));
        }
        try {
            project = (Folder)vfs.getItemByPath(JavaDocBuilderVfsTest.class.getSimpleName(), null, false, PropertyFilter.NONE_FILTER);
            vfs.delete(project.getId(), null);
            project = vfs.createFolder(vfs.getInfo().getRoot().getId(), JavaDocBuilderVfsTest.class.getSimpleName());
        } catch (ItemNotFoundException e) {
            project = vfs.createFolder(vfs.getInfo().getRoot().getId(), JavaDocBuilderVfsTest.class.getSimpleName());
        }
        vfs.importZip(project.getId(),
                      Thread.currentThread().getContextClassLoader().getResourceAsStream("exo-ide-client.zip"), true);
    }

    @Test
    @Ignore
    public void renameTypeTest() throws VirtualFileSystemException, IOException, CoreException {
        RefactoringService r = new RefactoringService();
        r.rename(ID, project.getId(), "org.exoplatform.ide.client.IDE", 1046, "MyIde");
        ContentStream content = vfs.getContent(
                project.getPath() + "/src/main/java2/org/exoplatform/ide/client/MyIde.java", null);
        String c = IOUtils.toString(content.getStream());
        System.out.println(c);
    }
}
