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
package com.codenvy.ide.wizard.newresource;

import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.resources.model.Folder;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static com.codenvy.ide.resources.model.Folder.TYPE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Testing {@link NewFolderProvider} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class NewFolderProviderTest extends BaseNewResourceProviderTest {
    @Before
    public void setUp() {
        newResourceProvider = new NewFolderProvider(resources, selectionAgent);
        when(folder.getResourceType()).thenReturn(TYPE);
    }

    @Override
    public void testCreateWhenRequestIsSuccessful() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] arguments = invocationOnMock.getArguments();
                AsyncCallback<Folder> callback = (AsyncCallback<Folder>)arguments[2];
                callback.onSuccess(parent);
                return null;
            }
        }).when(project).createFolder((Folder)anyObject(), anyString(), (AsyncCallback<Folder>)anyObject());

        super.testCreateWhenRequestIsSuccessful();

        verify(project).createFolder(eq(parent), eq(RESOURCE_NAME), (AsyncCallback<Folder>)anyObject());
        verify(callback).onSuccess(eq(parent));
    }

    @Override
    public void testCreateWhenRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] arguments = invocationOnMock.getArguments();
                AsyncCallback<Folder> callback = (AsyncCallback<Folder>)arguments[2];
                callback.onFailure(throwable);
                return null;
            }
        }).when(project).createFolder((Folder)anyObject(), anyString(), (AsyncCallback<Folder>)anyObject());

        super.testCreateWhenRequestIsFailed();

        verify(project).createFolder(eq(parent), eq(RESOURCE_NAME), (AsyncCallback<Folder>)anyObject());
    }

    @Test
    public void testInContextWhenSelectionIsEmpty() throws Exception {
        assertEquals(newResourceProvider.inContext(), !IN_CONTEXT);
    }

    @Test
    public void testInContextWhenFileIsSelected() throws Exception {
        // Needs custom implementation of File class because getParent() method is final. That's why mockito can't override its method.
        TestFile file = new TestFile();
        file.setParent(folder);

        Selection selection = mock(Selection.class);
        when(selection.getFirstElement()).thenReturn(file);
        when(selectionAgent.getSelection()).thenReturn(selection);

        assertEquals(newResourceProvider.inContext(), IN_CONTEXT);
    }

    @Test
    public void testInContextWhenFileIsSelectedAndParentIsProject() throws Exception {
        // Needs custom implementation of File class because getParent() method is final. That's why mockito can't override its method.
        TestFile file = new TestFile();
        file.setParent(project);

        Selection selection = mock(Selection.class);
        when(selection.getFirstElement()).thenReturn(file);
        when(selectionAgent.getSelection()).thenReturn(selection);

        assertEquals(newResourceProvider.inContext(), IN_CONTEXT);
    }

    @Test
    public void testInContextWhenFolderIsSelected() throws Exception {
        Selection selection = mock(Selection.class);
        when(selection.getFirstElement()).thenReturn(folder);
        when(selectionAgent.getSelection()).thenReturn(selection);

        assertEquals(newResourceProvider.inContext(), IN_CONTEXT);
    }

    @Test
    public void testInContextWhenFolderIsSelectedAndFolderHasNotFolderType() throws Exception {
        Folder folder = mock(Folder.class);
        when(folder.getResourceType()).thenReturn("Package");

        Selection selection = mock(Selection.class);
        when(selection.getFirstElement()).thenReturn(folder);
        when(selectionAgent.getSelection()).thenReturn(selection);

        assertEquals(newResourceProvider.inContext(), !IN_CONTEXT);
    }

    @Test
    public void testInContextWhenProjectIsSelected() throws Exception {
        Selection selection = mock(Selection.class);
        when(selection.getFirstElement()).thenReturn(project);
        when(selectionAgent.getSelection()).thenReturn(selection);

        assertEquals(newResourceProvider.inContext(), IN_CONTEXT);
    }
}