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

import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.ext.java.client.BaseTest;
import com.codenvy.ide.api.resources.model.File;
import com.codenvy.ide.api.resources.model.Folder;
import com.codenvy.ide.api.resources.model.Resource;
import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** @author Evgen Vidolob */
public class ModelUnmarshallerTest extends BaseTest {

    @Spy
    private JavaProject project = new JavaProject(null, null, null, null);
    @Mock
    private JavaProjectDescription projectDescription;
    private Array<ItemReference>   itemReferenceArray;

    @Before
    public void setUp() {
        when(project.getDescription()).thenReturn(projectDescription);
        when(project.getPath()).thenReturn("/SpringProject");
        when(projectDescription.getSourceFolders()).thenReturn(
                Collections.createStringSet("src/main/java", "src/main/resources", "src/test/java", "src/test/resources"));
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
        ArrayList<Resource> children = Lists.newArrayList(pack.getChildren().asIterable());
        assertThat(children).onProperty("name").contains("void");
    }

    @Test
    public void addCompilationUnit() throws UnmarshallerException {
        List<Resource> allValues = parseProject();
        SourceFolder sourceFolder = (SourceFolder)allValues.get(3);
        ArrayList<Resource> packages = Lists.newArrayList(sourceFolder.getChildren().asIterable());
        Package pack = (Package)packages.get(5);
        ArrayList<Resource> children = Lists.newArrayList(pack.getChildren().asIterable());
        assertThat(children).onProperty("resourceType").containsOnly(File.TYPE, CompilationUnit.TYPE, Folder.TYPE);
    }

    private List<Resource> parseProject() throws UnmarshallerException {
        JavaModelUnmarshaller unmarshaller = new JavaModelUnmarshaller(project, project, null, null, null, null);
        unmarshaller.unmarshalChildren(Collections.<ItemReference>createArray(), Collections.<ProjectDescriptor>createArray());
        ArgumentCaptor<Resource> children = ArgumentCaptor.forClass(Resource.class);
        verify(project, times(7)).addChild(children.capture());
        List<Resource> allValues = children.getAllValues();
        return allValues;
    }
}
