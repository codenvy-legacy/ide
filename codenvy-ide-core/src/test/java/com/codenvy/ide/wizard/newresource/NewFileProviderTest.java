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

import com.codenvy.ide.api.resources.model.File;
import com.codenvy.ide.api.resources.model.Folder;
import com.codenvy.ide.api.ui.Icon;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.junit.Before;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static com.codenvy.ide.MimeType.TEXT_PLAIN;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link NewFileProvider} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class NewFileProviderTest extends BaseNewResourceProviderTest {
    private String textFileName;

    @Before
    public void setUp() {
        when(iconRegistry.getIcon(anyString())).thenReturn(mock(Icon.class));
        newResourceProvider = new NewFileProvider(iconRegistry);
        textFileName = RESOURCE_NAME;
    }

    @Override
    public void testCreateWhenRequestIsSuccessful() throws Exception {
        final File file = mock(File.class);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] arguments = invocationOnMock.getArguments();
                AsyncCallback<File> callback = (AsyncCallback<File>)arguments[4];
                callback.onSuccess(file);
                return null;
            }
        }).when(project).createFile((Folder)anyObject(), anyString(), anyString(), (String)anyObject(), (AsyncCallback<File>)anyObject());

        super.testCreateWhenRequestIsSuccessful();

        verify(project).createFile(eq(parent), eq(textFileName), eq(EMPTY_STRING), eq(TEXT_PLAIN),
                                   (AsyncCallback<File>)anyObject());
        verify(callback).onSuccess(eq(file));
    }

    @Override
    public void testCreateWhenRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] arguments = invocationOnMock.getArguments();
                AsyncCallback<File> callback = (AsyncCallback<File>)arguments[4];
                callback.onFailure(throwable);
                return null;
            }
        }).when(project).createFile((Folder)anyObject(), anyString(), anyString(), (String)anyObject(), (AsyncCallback<File>)anyObject());

        super.testCreateWhenRequestIsFailed();

        verify(project).createFile(eq(parent), eq(textFileName), eq(EMPTY_STRING), eq(TEXT_PLAIN), (AsyncCallback<File>)anyObject());
    }
}