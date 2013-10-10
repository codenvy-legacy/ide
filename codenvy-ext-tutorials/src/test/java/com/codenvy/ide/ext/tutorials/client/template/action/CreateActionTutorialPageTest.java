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
package com.codenvy.ide.ext.tutorials.client.template.action;

import com.codenvy.ide.ext.tutorials.client.BaseCreateTutorialTest;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Testing {@link CreateActionTutorialPage} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class CreateActionTutorialPageTest extends BaseCreateTutorialTest {
    private CreateActionTutorialPage page;

    @Override
    public void setUp() {
        super.setUp();
        page = new CreateActionTutorialPage(service, resourceProvider);
        page.setContext(wizardContext);
    }

    @Test
    public void testCreateWhenGetProjectRequestIsSuccessful() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Void> callback = (AsyncRequestCallback<Void>)arguments[2];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, (Void)null);
                return callback;
            }
        }).when(service)
                .createActionTutorialProject(anyString(), (JsonArray<Property>)anyObject(), (AsyncRequestCallback<Void>)anyObject());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<Project> callback = (AsyncCallback<Project>)arguments[1];
                callback.onSuccess(project);
                return callback;
            }
        }).when(resourceProvider)
                .getProject(anyString(), (AsyncCallback<Project>)anyObject());

        page.commit(callback);

        verify(resourceProvider).getProject(eq(PROJECT_NAME), (AsyncCallback<Project>)anyObject());
        verify(callback).onSuccess();
    }

    @Test
    public void testCreateWhenCreateTutorialRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Void> callback = (AsyncRequestCallback<Void>)arguments[2];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, throwable);
                return callback;
            }
        }).when(service)
                .createActionTutorialProject(anyString(), (JsonArray<Property>)anyObject(), (AsyncRequestCallback<Void>)anyObject());

        page.commit(callback);

        verify(callback).onFailure(eq(throwable));
    }

    @Test
    public void testCreateWhenGetProjectRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Void> callback = (AsyncRequestCallback<Void>)arguments[2];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, (Void)null);
                return callback;
            }
        }).when(service)
                .createActionTutorialProject(anyString(), (JsonArray<Property>)anyObject(), (AsyncRequestCallback<Void>)anyObject());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<Project> callback = (AsyncCallback<Project>)arguments[1];
                callback.onFailure(throwable);
                return callback;
            }
        }).when(resourceProvider)
                .getProject(anyString(), (AsyncCallback<Project>)anyObject());

        page.commit(callback);

        verify(resourceProvider).getProject(eq(PROJECT_NAME), (AsyncCallback<Project>)anyObject());
        verify(callback).onFailure(eq(throwable));
    }

    @Test
    public void testCreateWhenREquestExceptionHappened() throws Exception {
        doThrow(RequestException.class).when(service)
                .createActionTutorialProject(anyString(), (JsonArray<Property>)anyObject(), (AsyncRequestCallback<Void>)anyObject());

        page.commit(callback);

        verify(callback).onFailure((Throwable)anyObject());
    }
}