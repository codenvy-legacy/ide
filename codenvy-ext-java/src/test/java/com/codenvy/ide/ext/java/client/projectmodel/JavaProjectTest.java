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
package com.codenvy.ide.ext.java.client.projectmodel;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.resources.model.Folder;
import com.codenvy.ide.resources.model.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

/** @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a> */
@RunWith(MockitoJUnitRunner.class)
public class JavaProjectTest {

    private static final String[] packages = new String[]{
            "org",//
            "org.exo",//
            "org.exo.ide",
            "org.exo.ide.cli"
    };
    @Mock
    private SourceFolder sourceFolder;
    private JavaProject project = new JavaProject(null, null);

    @Before
    public void setUp() {
        Array<Resource> childrens = Collections.createArray();
        for (String pack : packages) {
            Package p = Mockito.mock(Package.class);
            Mockito.when(p.getName()).thenReturn(pack);
            childrens.add(p);
        }
        Mockito.when(sourceFolder.getChildren()).thenReturn(childrens);
        when(sourceFolder.getName()).thenReturn("src/main/java");
    }

    @Test
    public void findParentForNewPackage() {
        Folder parentForPackage = project.findFolderParent(sourceFolder, "org.ide");
        assertThat(parentForPackage.getName()).isEqualTo("org");
    }

    @Test
    public void findParentForNewPackageWithNameConflict() {
        Folder parentForPackage = project.findFolderParent(sourceFolder, "org.exo.ide.client");
        assertThat(parentForPackage.getName()).isEqualTo("org.exo.ide");
    }

    @Test
    public void parentNotExist() {
        Folder parentForPackage = project.findFolderParent(sourceFolder, "com.exo.ide.client");
        assertThat(parentForPackage).isNull();
    }

    @Test
    public void findParentPartNameMathch() {
        Folder parentForPackage = project.findFolderParent(sourceFolder, "org.exo.idetest");
        assertThat(parentForPackage.getName()).isEqualTo("org.exo");
    }

}
