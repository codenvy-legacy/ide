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
package com.codenvy.ide.api.projecttree.generic;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link ProjectRootNode} functionality.
 *
 * @author Artem Zatsarynnyy
 */
@RunWith(MockitoJUnitRunner.class)
public class ProjectNodeTest {
    private static final String ITEM_PATH       = "/project_name";
    private static final String ITEM_NAME       = "project_name";
    private static final String PROJECT_TYPE_ID = "project_type";
    @Mock
    private EventBus               eventBus;
    @Mock
    private EditorAgent            editorAgent;
    @Mock
    private ProjectServiceClient   projectServiceClient;
    @Mock
    private DtoUnmarshallerFactory dtoUnmarshallerFactory;
    @Mock
    private ProjectDescriptor      projectDescriptor;
    @InjectMocks
    private ProjectRootNode        projectNode;

    @Before
    public void setUp() {
        when(projectDescriptor.getPath()).thenReturn(ITEM_PATH);
        when(projectDescriptor.getName()).thenReturn(ITEM_NAME);
        when(projectDescriptor.getProjectTypeId()).thenReturn(PROJECT_TYPE_ID);
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals(ITEM_NAME, projectNode.getName());
    }

    @Test
    public void testGetPath() throws Exception {
        assertEquals(ITEM_PATH, projectNode.getPath());
    }

    @Test
    public void testGetProject() throws Exception {
        assertEquals(projectNode, projectNode.getProject());
    }

    @Test
    public void shouldNotBeLeaf() throws Exception {
        assertFalse(projectNode.isLeaf());
    }

    @Test
    public void shouldBeRenemable() throws Exception {
        assertTrue(projectNode.isRenemable());
    }

    @Test
    public void shouldBeDeletable() throws Exception {
        assertTrue(projectNode.isDeletable());
    }

    @Test
    public void shouldInvokeCallbackWhenDeleteIsSuccessful() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Void> callback = (AsyncRequestCallback<Void>)arguments[1];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, (Void)null);
                return callback;
            }
        }).when(projectServiceClient).delete(anyString(), (AsyncRequestCallback<Void>)anyObject());
        AsyncCallback<Void> callback = mock(AsyncCallback.class);

        projectNode.delete(callback);

        verify(projectServiceClient).delete(eq(ITEM_PATH), Matchers.<AsyncRequestCallback<Void>>anyObject());
        verify(callback).onSuccess(Matchers.<Void>anyObject());
    }

    @Test
    public void shouldInvokeCallbackWhenDeleteIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Void> callback = (AsyncRequestCallback<Void>)arguments[1];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(projectServiceClient).delete(anyString(), (AsyncRequestCallback<Void>)anyObject());
        AsyncCallback<Void> callback = mock(AsyncCallback.class);

        projectNode.delete(callback);

        verify(projectServiceClient).delete(eq(ITEM_PATH), Matchers.<AsyncRequestCallback<Void>>anyObject());
        verify(callback).onFailure(Matchers.<Throwable>anyObject());
    }

    @Test
    public void shouldReturnProjectTypeId() throws Exception {
        assertEquals(projectDescriptor.getProjectTypeId(), projectNode.getProjectTypeId());
    }
}
