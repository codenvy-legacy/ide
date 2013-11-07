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
package com.codenvy.ide.extension.maven.client.template;

import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;

import static com.codenvy.ide.extension.maven.client.BuilderExtension.JAR_PROJECT_ID;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Testing {@link com.codenvy.ide.extension.maven.client.template.CreateJavaProjectPage} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class CreateJavaProjectPageTest extends BaseCreateProjectTest {

    @Override
    public void setUp() {
        super.setUp();
        page = new CreateJavaProjectPage(service, resourceProvider);
        page.setContext(wizardContext);
    }

    @Override
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
                .createJavaProject(anyString(), (JsonArray<Property>)anyObject(), (AsyncRequestCallback<Void>)anyObject());

        super.testCreateWhenGetProjectRequestIsSuccessful();
    }

    @Override
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
                .createJavaProject(anyString(), (JsonArray<Property>)anyObject(), (AsyncRequestCallback<Void>)anyObject());

        super.testCreateWhenCreateTutorialRequestIsFailed();
    }

    @Override
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
                .createJavaProject(anyString(), (JsonArray<Property>)anyObject(), (AsyncRequestCallback<Void>)anyObject());

        super.testCreateWhenGetProjectRequestIsFailed();
    }

    @Override
    public void testCreateWhenRequestExceptionHappened() throws Exception {
        doThrow(RequestException.class).when(service)
                .createJavaProject(anyString(), (JsonArray<Property>)anyObject(), (AsyncRequestCallback<Void>)anyObject());

        super.testCreateWhenRequestExceptionHappened();
    }

    @Override
    public void testInContext() {
        when(template.getId()).thenReturn(JAR_PROJECT_ID);

        super.testInContext();
    }
}