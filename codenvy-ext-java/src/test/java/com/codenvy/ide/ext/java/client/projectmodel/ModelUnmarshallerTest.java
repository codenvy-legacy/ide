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
package com.codenvy.ide.ext.java.client.projectmodel;

import com.codenvy.ide.ext.java.client.BaseTest;

import com.codenvy.ide.ext.java.client.projectmodel.CompilationUnit;
import com.codenvy.ide.ext.java.client.projectmodel.JavaModelUnmarshaller;
import com.codenvy.ide.ext.java.client.projectmodel.JavaProject;
import com.codenvy.ide.ext.java.client.projectmodel.JavaProjectDesctiprion;
import com.codenvy.ide.ext.java.client.projectmodel.Package;
import com.codenvy.ide.ext.java.client.projectmodel.SourceFolder;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.resources.model.Folder;
import com.codenvy.ide.resources.model.Resource;
import com.google.common.collect.Lists;
import com.google.gwt.http.client.Response;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class ModelUnmarshallerTest extends BaseTest {

    private static String projectJs;

    @Spy
    private JavaProject project = new JavaProject(null);

    @Mock
    private JavaProjectDesctiprion projectDescription;

    @Mock
    private Response response;

    @BeforeClass
    public static void init() {
        InputStream stream =
                Thread.currentThread().getContextClassLoader()
                      .getResourceAsStream("com/codenvy/ide/ext/java/client/projectmodel/project.js");
        try {
            projectJs = IOUtils.toString(stream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Before
    public void setUp() {
        when(project.getDescription()).thenReturn(projectDescription);
        when(project.getPath()).thenReturn("/SpringProject");
        when(projectDescription.getSourceFolders()).thenReturn(
                JsonCollections.createStringSet("src/main/java", "src/main/resources", "src/test/java", "src/test/resources"));
        when(response.getText()).thenReturn(projectJs);
    }

    @Test
    public void sourceFoldersParse() throws UnmarshallerException {
        List<Resource> allValues = parseProject();
        assertThat(allValues).onProperty("name").containsOnly("src/main/java", "src/test/java", "src/main/resources",
                                                              "src/test/java", "src/test/resources", "src", "pom.xml", ".project");
    }

    @Test
    public void packageParse() throws UnmarshallerException {
        List<Resource> allValues = parseProject();
        SourceFolder sourceFolder = (SourceFolder)allValues.get(3);
        ArrayList<Resource> packages = Lists.newArrayList(sourceFolder.getChildren().asIterable());
        assertThat(packages).onProperty("name"). //
                containsOnly("org", //
                             "org.springframework", //
                             "org.springframework.samples", //
                             "org.springframework.samples.mvc",//
                             "org.springframework.samples.mvc.ajax", //
                             "org.springframework.samples.mvc.ajax.json", //
                             "org.springframework.samples.mvc.ajax.account");

    }

    @Test
    public void addNotValidFolderNameAsChildrenToPackage() throws UnmarshallerException {
        List<Resource> allValues = parseProject();
        SourceFolder sourceFolder = (SourceFolder)allValues.get(3);
        ArrayList<Resource> packages = Lists.newArrayList(sourceFolder.getChildren().asIterable());
        Package pack = (Package)packages.get(5);
        ArrayList<Resource> childrens = Lists.newArrayList(pack.getChildren().asIterable());
        assertThat(childrens).onProperty("name").contains("void");
    }

    @Test
    public void addCompilationUnit() throws UnmarshallerException {
        List<Resource> allValues = parseProject();
        SourceFolder sourceFolder = (SourceFolder)allValues.get(3);
        ArrayList<Resource> packages = Lists.newArrayList(sourceFolder.getChildren().asIterable());
        Package pack = (Package)packages.get(5);
        ArrayList<Resource> childrens = Lists.newArrayList(pack.getChildren().asIterable());
        assertThat(childrens).onProperty("resourceType").containsOnly(File.TYPE, CompilationUnit.TYPE, Folder.TYPE);
    }

    /**
     * @return
     * @throws UnmarshallerException
     */
    private List<Resource> parseProject() throws UnmarshallerException {
        JavaModelUnmarshaller unmarshaller = new JavaModelUnmarshaller(project, project);
        unmarshaller.unmarshal(response);
        ArgumentCaptor<Resource> childrens = ArgumentCaptor.forClass(Resource.class);
        verify(project, times(7)).addChild(childrens.capture());
        List<Resource> allValues = childrens.getAllValues();
        return allValues;
    }
}
