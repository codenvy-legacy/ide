/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package com.codenvy.ide.java.client.projectmodel;

import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
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

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
@RunWith(MockitoJUnitRunner.class)
public class JavaProjectTest {

    @Mock
    private SourceFolder sourceFolder;

    private static final String[] packages = new String[]{
            "org",//
            "org.exo",//
            "org.exo.ide",
            "org.exo.ide.cli"
    };

    private JavaProject project = new JavaProject(null);

    @Before
    public void setUp() {
        JsonArray<Resource> childrens = JsonCollections.createArray();
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
