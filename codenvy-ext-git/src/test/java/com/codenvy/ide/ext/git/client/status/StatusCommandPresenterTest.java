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
package com.codenvy.ide.ext.git.client.status;

import com.codenvy.ide.ext.git.client.BaseTest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;

import org.junit.Test;
import org.mockito.InjectMocks;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Testing {@link StatusCommandPresenter} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class StatusCommandPresenterTest extends BaseTest {
    public static final boolean IS_NOT_FORMATTED = false;
    @InjectMocks
    private StatusCommandPresenter presenter;

    @Test
    public void testShowStatus() throws RequestException {
        presenter.showStatus();

        verify(resourceProvider).getActiveProject();
        verify(service).statusText(eq(VFS_ID), eq(PROJECT_ID), eq(IS_NOT_FORMATTED), (AsyncRequestCallback<String>)anyObject());
        verify(console, never()).print(anyString());
    }

    @Test
    public void testShowStatusWhenExceptionHappened() throws RequestException {
        doThrow(RequestException.class).when(service)
                .statusText(anyString(), anyString(), anyBoolean(), (AsyncRequestCallback<String>)anyObject());

        presenter.showStatus();

        verify(resourceProvider).getActiveProject();
        verify(service).statusText(eq(VFS_ID), eq(PROJECT_ID), eq(IS_NOT_FORMATTED), (AsyncRequestCallback<String>)anyObject());
        verify(console).print(anyString());
    }
}