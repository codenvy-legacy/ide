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
package com.codenvy.ide.ext.java.client.wizard;

import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.ui.IconRegistry;
import com.codenvy.ide.ext.java.client.BaseTest;
import com.codenvy.ide.ext.java.client.projectmodel.CompilationUnit;
import com.codenvy.ide.ext.java.client.projectmodel.JavaProject;
import com.codenvy.ide.ext.java.client.projectmodel.SourceFolder;
import com.codenvy.ide.resources.model.Folder;
import com.codenvy.ide.resources.model.Resource;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The base test for testing new java resource providers.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public abstract class BaseNewJavaResourceProviderTest extends BaseTest {
    public static final String  RESOURCE_NAME = "resourceName";
    public static final String  PACKAGE_NAME  = "mypackage";
    public static final boolean IN_CONTEXT    = true;
    @Mock
    protected SelectionAgent                                       selectionAgent;
    @Mock
    protected Throwable                                            throwable;
    @Mock
    protected AsyncCallback<Resource>                              callback;
    @Mock
    protected CompilationUnit                                      javaFile;
    @Mock
    protected JavaProject                                          project;
    @Mock
    protected SourceFolder                                         sourceFolder;
    @Mock
    protected com.codenvy.ide.ext.java.client.projectmodel.Package javaPackage;
    @Mock
    protected Folder                                               folder;
    @Mock
    protected IconRegistry                                         iconRegistry;
    protected TestPackage                                          testPackage;
    protected String                                               resourceName;
    protected AbstractNewJavaResourceProvider                      provider;

    @Before
    public void setUp() {
        // Needs custom implementation of Package class because getParent() method is final. That's why mockito can't override its method.
        testPackage = new TestPackage();
        testPackage.setParent(sourceFolder);
        testPackage.setName(PACKAGE_NAME);

        resourceName = RESOURCE_NAME + '.' + provider.getExtension();
    }

    @Test
    public void testCreateWhenRequestIsSuccessful() throws Exception {
        provider.create(RESOURCE_NAME, testPackage, project, callback);
    }

    @Test
    public void testCreateWhenRequestIsFailed() throws Exception {
        provider.create(RESOURCE_NAME, testPackage, project, callback);

        verify(callback).onFailure(eq(throwable));
    }

    @Test
    public void testInContextWhenSelectionIsEmpty() throws Exception {
        assertEquals(provider.inContext(), !IN_CONTEXT);
    }

    @Test
    public void testInContextWhenFileIsSelected() throws Exception {
        // Needs custom implementation of File class because getParent() method is final. That's why mockito can't override its method.
        TestFile file = new TestFile();
        file.setParent(folder);

        Selection selection = mock(Selection.class);
        when(selection.getFirstElement()).thenReturn(file);
        when(selectionAgent.getSelection()).thenReturn(selection);

        assertEquals(provider.inContext(), !IN_CONTEXT);
    }

    @Test
    public void testInContextWhenFolderIsSelected() throws Exception {
        Selection selection = mock(Selection.class);
        when(selection.getFirstElement()).thenReturn(folder);
        when(selectionAgent.getSelection()).thenReturn(selection);

        assertEquals(provider.inContext(), !IN_CONTEXT);
    }

    @Test
    public void testInContextWhenJavaFileIsSelected() throws Exception {
        // Needs custom implementation of File class because getParent() method is final. That's why mockito can't override its method.
        TestFile file = new TestFile();
        file.setParent(javaPackage);

        Selection selection = mock(Selection.class);
        when(selection.getFirstElement()).thenReturn(file);
        when(selectionAgent.getSelection()).thenReturn(selection);

        assertEquals(provider.inContext(), IN_CONTEXT);
    }

    @Test
    public void testInContextWhenSourceFolderIsSelected() throws Exception {
        Selection selection = mock(Selection.class);
        when(selection.getFirstElement()).thenReturn(sourceFolder);
        when(selectionAgent.getSelection()).thenReturn(selection);

        assertEquals(provider.inContext(), IN_CONTEXT);
    }

    @Test
    public void testInContextWhenPackageIsSelected() throws Exception {
        Selection selection = mock(Selection.class);
        when(selection.getFirstElement()).thenReturn(javaPackage);
        when(selectionAgent.getSelection()).thenReturn(selection);

        assertEquals(provider.inContext(), IN_CONTEXT);
    }

    @Test
    public void testInContextWhenProjectIsSelected() throws Exception {
        Selection selection = mock(Selection.class);
        when(selection.getFirstElement()).thenReturn(project);
        when(selectionAgent.getSelection()).thenReturn(selection);

        assertEquals(provider.inContext(), !IN_CONTEXT);
    }
}