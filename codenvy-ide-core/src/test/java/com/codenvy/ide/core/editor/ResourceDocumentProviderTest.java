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
package com.codenvy.ide.core.editor;

import com.codenvy.ide.api.editor.DocumentProvider.DocumentCallback;
import com.codenvy.ide.api.editor.EditorInput;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.DocumentFactoryImpl;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
@RunWith(MockitoJUnitRunner.class)
public class ResourceDocumentProviderTest {
    @Mock
    private DocumentCallback callback;
    @Mock
    private EditorInput      input;
    @Mock
    private File             file;
    @Mock
    private Project          project;
    @Mock
    private EventBus         eventBus;

    @Before
    public void setUp() {
        when(input.getFile()).thenReturn(file);
        when(file.getProject()).thenReturn(project);
        when(file.getContent()).thenReturn("test");
    }

    @Test
    public void shuldCallProjectGetContent() {
        ResourceDocumentProvider provider = new ResourceDocumentProvider(new DocumentFactoryImpl(), eventBus);
        provider.getDocument(input, callback);
        verify(project).getContent(eq(file), Mockito.<AsyncCallback<File>>any());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shuldCallCallback() {
        ResourceDocumentProvider provider = new ResourceDocumentProvider(new DocumentFactoryImpl(), eventBus);
        doAnswer(createServerResponse()).when(project).getContent((File)any(), (AsyncCallback<File>)any());
        provider.getDocument(input, callback);
        verify(callback).onDocument((Document)any());
    }

    /** @return  */
    @SuppressWarnings("unchecked")
    private Answer<?> createServerResponse() {
        Answer<?> responseEmulator = new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                AsyncCallback<File> callback = (AsyncCallback<File>)invocation.getArguments()[1];
                callback.onSuccess(file);
                return null;
            }
        };
        return responseEmulator;
    }
}
