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
package com.codenvy.ide.ext.git.client.reset.files;

import com.codenvy.ide.ext.git.client.BaseTest;
import com.codenvy.ide.ext.git.shared.ResetRequest;
import com.codenvy.ide.ext.git.shared.Status;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Testing {@link ResetFilesPresenter} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class ResetFilesPresenterTest extends BaseTest {
    @Mock
    private ResetFilesView      view;
    @InjectMocks
    private ResetFilesPresenter presenter;

    @Test
    public void testShowDialog() throws Exception {
        presenter.showDialog();

        verify(resourceProvider).getActiveProject();
        verify(service).status(eq(VFS_ID), eq(PROJECT_ID), (AsyncRequestCallback<Status>)anyObject());
        verify(console, never()).print(anyString());
    }

    @Test
    public void testShowDialogWhenExceptionHappened() throws Exception {
        doThrow(RequestException.class).when(service).status(anyString(), anyString(), (AsyncRequestCallback<Status>)anyObject());

        presenter.showDialog();

        verify(resourceProvider).getActiveProject();
        verify(service).status(eq(VFS_ID), eq(PROJECT_ID), (AsyncRequestCallback<Status>)anyObject());
        verify(console).print(anyString());
    }

    @Test
    @Ignore
    public void testOnResetClicked() throws Exception {
        // TODO not possible to check because need to change AsyncRequestCallback
        presenter.onResetClicked();

        verify(service).reset(eq(VFS_ID), eq(PROJECT_ID), anyString(), (ResetRequest.ResetType)anyObject(),
                              (AsyncRequestCallback<String>)anyObject());
    }

    @Test
    public void testOnCancelClicked() throws Exception {
        presenter.onCancelClicked();

        verify(view).close();
    }
}