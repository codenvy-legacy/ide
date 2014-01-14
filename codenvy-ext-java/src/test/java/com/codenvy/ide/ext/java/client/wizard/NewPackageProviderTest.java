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

import com.google.gwt.user.client.rpc.AsyncCallback;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

/**
 * Testing {@link NewPackageProvider} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class NewPackageProviderTest extends BaseNewJavaResourceProviderTest {

    @Override
    public void setUp() {
        provider = new NewPackageProvider(selectionAgent);
        super.setUp();
    }

    @Override
    public void testCreateWhenRequestIsSuccessful() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] arguments = invocationOnMock.getArguments();
                AsyncCallback<com.codenvy.ide.ext.java.client.projectmodel.Package> callback =
                        (AsyncCallback<com.codenvy.ide.ext.java.client.projectmodel.Package>)arguments[2];
                callback.onSuccess(testPackage);
                return null;
            }
        }).when(project).createPackage(eq(testPackage), anyString(),
                                       (AsyncCallback<com.codenvy.ide.ext.java.client.projectmodel.Package>)anyObject());

        super.testCreateWhenRequestIsSuccessful();

        verify(callback).onSuccess(eq(testPackage));
    }

    @Override
    public void testCreateWhenRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] arguments = invocationOnMock.getArguments();
                AsyncCallback<com.codenvy.ide.ext.java.client.projectmodel.Package> callback =
                        (AsyncCallback<com.codenvy.ide.ext.java.client.projectmodel.Package>)arguments[2];
                callback.onFailure(throwable);
                return null;
            }
        }).when(project).createPackage(eq(testPackage), anyString(),
                                       (AsyncCallback<com.codenvy.ide.ext.java.client.projectmodel.Package>)anyObject());

        super.testCreateWhenRequestIsFailed();
    }
}