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
package com.codenvy.ide.ext.java.client.projectmodel;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.api.resources.model.Folder;
import com.codenvy.ide.api.resources.model.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

/** @author Evgen Vidolob */
@RunWith(MockitoJUnitRunner.class)
public class JavaProjectTest {

    private static final String[] packages = new String[]{
            "org",//
            "org.exo",//
            "org.codenvy.ide",
            "org.codenvy.ide.cli"
    };
    @Mock
    private SourceFolder sourceFolder;
    private JavaProject project = new JavaProject(null, null, null, null);

    @Before
    public void setUp() {
        Array<Resource> children = Collections.createArray();
        for (String pack : packages) {
            Package p = Mockito.mock(Package.class);
            Mockito.when(p.getName()).thenReturn(pack);
            children.add(p);
        }
        Mockito.when(sourceFolder.getChildren()).thenReturn(children);
        when(sourceFolder.getName()).thenReturn("src/main/java");
    }

    @Test
    public void findParentForNewPackage() {
        Folder parentForPackage = project.findFolderParent(sourceFolder, "org.ide");
        assertThat(parentForPackage.getName()).isEqualTo("org");
    }

    @Test
    public void findParentForNewPackageWithNameConflict() {
        Folder parentForPackage = project.findFolderParent(sourceFolder, "org.codenvy.ide.client");
        assertThat(parentForPackage.getName()).isEqualTo("org.codenvy.ide");
    }

    @Test
    public void parentNotExist() {
        Folder parentForPackage = project.findFolderParent(sourceFolder, "com.codenvy.ide.client");
        assertThat(parentForPackage).isNull();
    }

    @Test
    public void findParentPartNameMatch() {
        Folder parentForPackage = project.findFolderParent(sourceFolder, "org.codenvy.idetest");
        assertThat(parentForPackage.getName()).isEqualTo("org.exo");
    }

}
