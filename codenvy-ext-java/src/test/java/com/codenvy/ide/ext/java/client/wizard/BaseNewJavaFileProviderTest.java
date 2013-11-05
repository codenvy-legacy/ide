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

import com.codenvy.ide.ext.java.client.projectmodel.CompilationUnit;
import com.codenvy.ide.resources.model.File;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

/**
 * The base test for testing new java file providers.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public abstract class BaseNewJavaFileProviderTest extends BaseNewJavaResourceProviderTest {
    protected String content;

    @Override
    public void testCreateWhenRequestIsSuccessful() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] arguments = invocationOnMock.getArguments();
                AsyncCallback<File> callback = (AsyncCallback<File>)arguments[3];
                callback.onSuccess(javaFile);
                return null;
            }
        }).when(project).createCompilationUnit(eq(testPackage), anyString(), anyString(), (AsyncCallback<CompilationUnit>)anyObject());

        super.testCreateWhenRequestIsSuccessful();

        verify(project).createCompilationUnit(eq(testPackage), eq(resourceName), eq(content), (AsyncCallback<CompilationUnit>)anyObject());
        verify(callback).onSuccess(eq(javaFile));
    }

    @Override
    public void testCreateWhenRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] arguments = invocationOnMock.getArguments();
                AsyncCallback<File> callback = (AsyncCallback<File>)arguments[3];
                callback.onFailure(throwable);
                return null;
            }
        }).when(project).createCompilationUnit(eq(testPackage), anyString(), anyString(), (AsyncCallback<CompilationUnit>)anyObject());

        super.testCreateWhenRequestIsFailed();
    }
}