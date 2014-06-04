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
package com.codenvy.ide.ext.java.client.newresource;

import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.Folder;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.ext.java.client.projectmodel.CompilationUnit;
import com.codenvy.ide.ext.java.client.projectmodel.JavaProject;
import com.codenvy.ide.ext.java.client.projectmodel.SourceFolder;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link NewJavaResourcePresenter} functionality.
 *
 * @author Artem Zatsarynnyy
 */
@RunWith(MockitoJUnitRunner.class)
public class NewJavaResourcePresenterTest {
    private static String FILE_NAME = "my_file";
    @Mock
    private NewJavaResourceView      view;
    @Mock
    private EditorAgent              editorAgent;
    @Mock
    private SelectionAgent           selectionAgent;
    @Mock
    private ResourceProvider         resourceProvider;
    @Mock
    private JavaProject              activeProject;
    @InjectMocks
    private NewJavaResourcePresenter presenter;

    @Before
    public void setUp() {
        when(resourceProvider.getActiveProject()).thenReturn(activeProject);
        when(activeProject.getSourceFolders()).thenReturn(Collections.<SourceFolder>createArray());
    }

    @Test
    public void shouldShowDialog() {
        presenter.showDialog();

        verify(view).setTypes(Matchers.<Array<ResourceTypes>>anyObject());
        verify(view).showDialog();
    }

    @Test
    public void shouldCloseDialogOnOkClicked() throws Exception {
        when(view.getSelectedType()).thenReturn(ResourceTypes.CLASS);

        presenter.onOkClicked();

        verify(view).close();
    }

    @Test
    public void shouldCloseDialogOnCancelClicked() throws Exception {
        presenter.onCancelClicked();

        verify(view).close();
    }

    @Test
    public void testCreateClass() throws Exception {
        when(view.getName()).thenReturn(FILE_NAME);
        when(view.getSelectedType()).thenReturn(ResourceTypes.CLASS);

        presenter.onOkClicked();

        verify(activeProject).createCompilationUnit((Folder)anyObject(), eq(FILE_NAME + ".java"), anyString(),
                                                    Matchers.<AsyncCallback<CompilationUnit>>anyObject());
    }

    @Test
    public void testCreateInterface() throws Exception {
        when(view.getName()).thenReturn(FILE_NAME);
        when(view.getSelectedType()).thenReturn(ResourceTypes.INTERFACE);

        presenter.onOkClicked();

        verify(activeProject).createCompilationUnit((Folder)anyObject(), eq(FILE_NAME + ".java"), anyString(),
                                                    Matchers.<AsyncCallback<CompilationUnit>>anyObject());
    }

    @Test
    public void testCreateEnum() throws Exception {
        when(view.getName()).thenReturn(FILE_NAME);
        when(view.getSelectedType()).thenReturn(ResourceTypes.ENUM);

        presenter.onOkClicked();

        verify(activeProject).createCompilationUnit((Folder)anyObject(), eq(FILE_NAME + ".java"), anyString(),
                                                    Matchers.<AsyncCallback<CompilationUnit>>anyObject());
    }

}
