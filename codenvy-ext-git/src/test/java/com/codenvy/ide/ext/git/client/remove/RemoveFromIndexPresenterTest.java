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
package com.codenvy.ide.ext.git.client.remove;

import com.codenvy.ide.ext.git.client.BaseTest;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.*;

/**
 * Testing {@link RemoveFromIndexPresenter} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class RemoveFromIndexPresenterTest extends BaseTest {
    public static final boolean REMOVED = true;
    @Mock
    private RemoveFromIndexView      view;
    @InjectMocks
    private RemoveFromIndexPresenter presenter;

    @Test
    public void testShowDialog() throws Exception {
        presenter.showDialog();

        verify(view).setMessage(anyString());
        verify(view).setRemoved(eq(!REMOVED));
        verify(view).showDialog();
    }

    @Test
    public void testOnRemoveClicked() throws Exception {
        when(view.isRemoved()).thenReturn(REMOVED);
        when(selectionAgent.getSelection()).thenReturn(null);

        presenter.showDialog();
        presenter.onRemoveClicked();

        verify(service)
                .remove(eq(VFS_ID), eq(PROJECT_ID), (JsonArray<String>)anyObject(), eq(REMOVED),
                        (AsyncRequestCallback<String>)anyObject());
        verify(view).close();
    }

    @Test
    public void testOnRemoveClickedWhenExceptionHappened() throws Exception {
        when(view.isRemoved()).thenReturn(REMOVED);
        when(selectionAgent.getSelection()).thenReturn(null);
        doThrow(RequestException.class).when(service).remove(anyString(), anyString(), (JsonArray<String>)anyObject(), anyBoolean(),
                                                             (AsyncRequestCallback<String>)anyObject());

        presenter.showDialog();
        presenter.onRemoveClicked();

        verify(service)
                .remove(eq(VFS_ID), eq(PROJECT_ID), (JsonArray<String>)anyObject(), eq(REMOVED),
                        (AsyncRequestCallback<String>)anyObject());
        verify(view).close();
        verify(console).print(anyString());
    }

    @Test
    public void testOnCancelClicked() throws Exception {
        presenter.onCancelClicked();

        verify(view).close();
    }
}