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
package com.codenvy.ide.core.editor;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.api.editor.DocumentProvider.DocumentCallback;
import com.codenvy.ide.api.editor.EditorInput;
import com.codenvy.ide.text.DocumentFactoryImpl;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.when;

/** @author Evgen Vidolob */
@RunWith(MockitoJUnitRunner.class)
public class ResourceDocumentProviderTest {
    @Mock
    private DocumentCallback     callback;
    @Mock
    private EditorInput          input;
    @Mock
    private ItemReference        file;
    @Mock
    private EventBus             eventBus;
    @Mock
    private ProjectServiceClient projectServiceClient;

    @Before
    public void setUp() {
        when(input.getFile()).thenReturn(file);
//        when(file.getProject()).thenReturn(project);
//        when(file.getContent()).thenReturn("test");
    }

    @Test
    public void shouldCallProjectGetContent() {
        ResourceDocumentProvider provider = new ResourceDocumentProvider(new DocumentFactoryImpl(), eventBus, projectServiceClient);
//        provider.getDocument(input, callback);
//        verify(project).getContent(eq(file), Mockito.<AsyncCallback<ItemReference>>any());
    }

    @Test
    public void shouldCallCallback() {
        ResourceDocumentProvider provider = new ResourceDocumentProvider(new DocumentFactoryImpl(), eventBus, projectServiceClient);
//        doAnswer(createServerResponse()).when(project).getContent((File)any(), (AsyncCallback<File>)any());
//        provider.getDocument(input, callback);
//        verify(callback).onDocument((Document)any());
    }

    @SuppressWarnings("unchecked")
    private Answer<?> createServerResponse() {
        Answer<?> responseEmulator = new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                AsyncCallback<ItemReference> callback = (AsyncCallback<ItemReference>)invocation.getArguments()[1];
                callback.onSuccess(file);
                return null;
            }
        };
        return responseEmulator;
    }
}
