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
package com.codenvy.ide.extension.css.wizard;

import com.codenvy.ide.ext.web.WebExtensionResource;
import com.codenvy.ide.ext.web.css.CssFileProvider;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.resources.model.Folder;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static com.codenvy.ide.MimeType.TEXT_CSS;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Testing {@link com.codenvy.ide.ext.web.css.CssFileProvider} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class NewCSSFileProviderTest {
    public static final String CONTENT       = "@CHARSET \"UTF-8\";";
    public static final String RESOURCE_NAME = "resourceName";
    @Mock
    private Folder                  parent;
    @Mock
    private Project                 project;
    @Mock
    private Folder                  folder;
    @Mock
    private AsyncCallback<Resource> callback;
    @Mock
    private Throwable               throwable;
    @Mock
    private WebExtensionResource    resource;
    private CssFileProvider         newCSSFileProvider;
    private String                  cssFileName;

    @Before
    public void setUp() {
        newCSSFileProvider = new CssFileProvider(resource);
        cssFileName = RESOURCE_NAME + '.' + newCSSFileProvider.getExtension();
    }

    @Test
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

        newCSSFileProvider.create(RESOURCE_NAME, parent, project, callback);

        verify(project).createFile(eq(parent), eq(cssFileName), eq(CONTENT), eq(TEXT_CSS),
                                   (AsyncCallback<File>)anyObject());
        verify(callback).onSuccess(eq(file));
    }

    @Test
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

        newCSSFileProvider.create(RESOURCE_NAME, parent, project, callback);

        verify(callback).onFailure(eq(throwable));
        verify(project).createFile(eq(parent), eq(cssFileName), eq(CONTENT), eq(TEXT_CSS), (AsyncCallback<File>)anyObject());
    }
}