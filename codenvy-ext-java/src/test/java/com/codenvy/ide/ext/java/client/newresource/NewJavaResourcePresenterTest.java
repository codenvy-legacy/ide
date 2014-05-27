/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
 *  All Rights Reserved.
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
