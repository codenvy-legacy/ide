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
package com.codenvy.ide.ext.extensions.client.template;

import com.codenvy.ide.api.ui.wizard.Wizard;
import com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard;
import com.codenvy.ide.ext.extensions.client.ExtRuntimeResources;
import com.codenvy.ide.ext.extensions.client.template.sample.CreateSampleCodenvyExtensionPage;
import com.codenvy.ide.ext.extensions.client.template.sample.CreateSampleCodenvyExtensionPageView;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;

import static com.codenvy.ide.ext.extensions.client.ExtRuntimeExtension.SAMPLE_EXTENSION_ID;
import static com.codenvy.ide.ext.extensions.client.template.sample.CreateSampleCodenvyExtensionPage.DEFAULT_VERSION;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Testing {@link com.codenvy.ide.ext.extensions.client.template.sample.CreateSampleCodenvyExtensionPage} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class CreateSampleCodenvyExtensionPageTest extends BaseCreateExtensionTest {
    public static final String  EMPTY_TEXT = "";
    public static final boolean COMPLETED  = true;
    @Mock
    private CreateSampleCodenvyExtensionPageView view;
    @Mock
    private Wizard.UpdateDelegate                updateDelegate;

    @Override
    public void setUp() {
        super.setUp();
        page = new CreateSampleCodenvyExtensionPage(view, service, resourceProvider, mock(ExtRuntimeResources.class));
        page.setContext(wizardContext);
        page.setUpdateDelegate(updateDelegate);
    }

    @Test
    public void testOnValueChanged() throws Exception {
        ((CreateSampleCodenvyExtensionPage)page).onValueChanged();

        verify(updateDelegate).updateControls();
    }

    @Test
    public void testIsCompletedWhenGroupIDEmpty() throws Exception {
        when(view.getGroupId()).thenReturn(EMPTY_TEXT);
        when(view.getArtifactId()).thenReturn(EMPTY_TEXT);
        when(view.getVersion()).thenReturn(EMPTY_TEXT);
        assertEquals(page.isCompleted(), !COMPLETED);
    }

    @Test
    public void testIsCompletedWhenArtifactIDEmpty() throws Exception {
        when(view.getGroupId()).thenReturn(PROJECT_NAME);
        when(view.getArtifactId()).thenReturn(EMPTY_TEXT);
        when(view.getVersion()).thenReturn(EMPTY_TEXT);
        assertEquals(page.isCompleted(), !COMPLETED);
    }

    @Test
    public void testIsCompletedWhenVersionEmpty() throws Exception {
        when(view.getGroupId()).thenReturn(PROJECT_NAME);
        when(view.getArtifactId()).thenReturn(PROJECT_NAME);
        when(view.getVersion()).thenReturn(EMPTY_TEXT);
        assertEquals(page.isCompleted(), !COMPLETED);
    }

    @Test
    public void testIsCompleted() throws Exception {
        when(view.getGroupId()).thenReturn(PROJECT_NAME);
        when(view.getArtifactId()).thenReturn(PROJECT_NAME);
        when(view.getVersion()).thenReturn(PROJECT_NAME);
        assertEquals(page.isCompleted(), COMPLETED);
    }

    @Test
    public void testGetNoticeWhenGroupIDEmpty() throws Exception {
        when(view.getGroupId()).thenReturn(EMPTY_TEXT);

        assertEquals(page.getNotice(), "Please, specify groupId.");
    }

    @Test
    public void testGetNoticeWhenArtifactIDEmpty() throws Exception {
        when(view.getGroupId()).thenReturn(PROJECT_NAME);
        when(view.getArtifactId()).thenReturn(EMPTY_TEXT);

        assertEquals(page.getNotice(), "Please, specify artifactId.");
    }

    @Test
    public void testGetNoticeWhenVersionEmpty() throws Exception {
        when(view.getGroupId()).thenReturn(PROJECT_NAME);
        when(view.getArtifactId()).thenReturn(PROJECT_NAME);
        when(view.getVersion()).thenReturn(EMPTY_TEXT);

        assertEquals(page.getNotice(), "Please, specify version.");
    }

    @Test
    public void testGetNotice() throws Exception {
        when(view.getGroupId()).thenReturn(PROJECT_NAME);
        when(view.getArtifactId()).thenReturn(PROJECT_NAME);
        when(view.getVersion()).thenReturn(PROJECT_NAME);

        assertNull(page.getNotice());
    }

    @Test
    public void testGo() throws Exception {
        AcceptsOneWidget container = mock(AcceptsOneWidget.class);

        page.go(container);

        verify(wizardContext).getData(eq(NewProjectWizard.PROJECT_NAME));
        verify(view).setGroupId(eq(PROJECT_NAME));
        verify(view).setArtifactId(eq(PROJECT_NAME));
        verify(view).setVersion(eq(DEFAULT_VERSION));
        verify(container).setWidget(eq(view));
        verify(updateDelegate).updateControls();
    }

    @Override
    public void testCreateWhenGetProjectRequestIsSuccessful() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Void> callback = (AsyncRequestCallback<Void>)arguments[5];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, (Void)null);
                return callback;
            }
        }).when(service)
                .createSampleCodenvyExtensionProject(anyString(), (JsonArray<Property>)anyObject(), anyString(), anyString(), anyString(),
                                                     (AsyncRequestCallback<Void>)anyObject());

        super.testCreateWhenGetProjectRequestIsSuccessful();

        verify(view).getGroupId();
        verify(view).getArtifactId();
        verify(view).getVersion();
    }

    @Override
    public void testCreateWhenCreateTutorialRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Void> callback = (AsyncRequestCallback<Void>)arguments[5];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, throwable);
                return callback;
            }
        }).when(service)
                .createSampleCodenvyExtensionProject(anyString(), (JsonArray<Property>)anyObject(), anyString(), anyString(), anyString(),
                                                     (AsyncRequestCallback<Void>)anyObject());

        super.testCreateWhenCreateTutorialRequestIsFailed();

        verify(view).getGroupId();
        verify(view).getArtifactId();
        verify(view).getVersion();
    }

    @Override
    public void testCreateWhenGetProjectRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Void> callback = (AsyncRequestCallback<Void>)arguments[5];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, (Void)null);
                return callback;
            }
        }).when(service)
                .createSampleCodenvyExtensionProject(anyString(), (JsonArray<Property>)anyObject(), anyString(), anyString(), anyString(),
                                                     (AsyncRequestCallback<Void>)anyObject());

        super.testCreateWhenGetProjectRequestIsFailed();

        verify(view).getGroupId();
        verify(view).getArtifactId();
        verify(view).getVersion();
    }

    @Override
    public void testCreateWhenRequestExceptionHappened() throws Exception {
        doThrow(RequestException.class).when(service)
                .createSampleCodenvyExtensionProject(anyString(), (JsonArray<Property>)anyObject(), anyString(), anyString(), anyString(),
                                                     (AsyncRequestCallback<Void>)anyObject());

        super.testCreateWhenRequestExceptionHappened();

        verify(view).getGroupId();
        verify(view).getArtifactId();
        verify(view).getVersion();
    }

    @Override
    public void testInContext() {
        when(template.getId()).thenReturn(SAMPLE_EXTENSION_ID);

        super.testInContext();
    }
}