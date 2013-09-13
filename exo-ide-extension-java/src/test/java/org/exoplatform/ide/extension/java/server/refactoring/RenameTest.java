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
