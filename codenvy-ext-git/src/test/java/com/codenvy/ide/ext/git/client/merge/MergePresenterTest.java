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
package com.codenvy.ide.ext.git.client.merge;

import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.git.client.BaseTest;
import com.codenvy.ide.ext.git.shared.Branch;
import com.codenvy.ide.ext.git.shared.MergeResult;
import com.codenvy.ide.ext.git.shared.Reference;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static com.codenvy.ide.ext.git.shared.BranchListRequest.LIST_LOCAL;
import static com.codenvy.ide.ext.git.shared.BranchListRequest.LIST_REMOTE;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Testing {@link MergePresenter} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class MergePresenterTest extends BaseTest {
    public static final String DISPLAY_NAME = "displayName";
    @Mock
    private MergeView      view;
    @Mock
    private Reference      selectedReference;
    @InjectMocks
    private MergePresenter presenter;

    @Test
    public void testShowDialog() throws Exception {
        presenter.showDialog();

        verify(resourceProvider).getActiveProject();
        verify(view).setEnableMergeButton(eq(DISABLE_BUTTON));
        verify(view).showDialog();

        verify(service).branchList(eq(VFS_ID), eq(PROJECT_ID), eq(LIST_LOCAL), (AsyncRequestCallback<JsonArray<Branch>>)anyObject());
        verify(service).branchList(eq(VFS_ID), eq(PROJECT_ID), eq(LIST_REMOTE), (AsyncRequestCallback<JsonArray<Branch>>)anyObject());

        verify(eventBus, never()).fireEvent((ExceptionThrownEvent)anyObject());
        verify(console, never()).print(anyString());
    }

    @Test
    public void testShowDialogWhenLocalBranchCantGet() throws Exception {
        doThrow(RequestException.class).when(service)
                .branchList(anyString(), anyString(), eq(LIST_LOCAL), (AsyncRequestCallback<JsonArray<Branch>>)anyObject());

        presenter.showDialog();

        verify(service).branchList(eq(VFS_ID), eq(PROJECT_ID), eq(LIST_LOCAL), (AsyncRequestCallback<JsonArray<Branch>>)anyObject());
        verify(service).branchList(eq(VFS_ID), eq(PROJECT_ID), eq(LIST_REMOTE), (AsyncRequestCallback<JsonArray<Branch>>)anyObject());

        verify(eventBus).fireEvent((ExceptionThrownEvent)anyObject());
        verify(console).print(anyString());
    }

    @Test
    public void testShowDialogWhenRemoteBranchCantGet() throws Exception {
        doThrow(RequestException.class).when(service)
                .branchList(anyString(), anyString(), eq(LIST_REMOTE), (AsyncRequestCallback<JsonArray<Branch>>)anyObject());

        presenter.showDialog();

        verify(service).branchList(eq(VFS_ID), eq(PROJECT_ID), eq(LIST_LOCAL), (AsyncRequestCallback<JsonArray<Branch>>)anyObject());
        verify(service).branchList(eq(VFS_ID), eq(PROJECT_ID), eq(LIST_REMOTE), (AsyncRequestCallback<JsonArray<Branch>>)anyObject());

        verify(eventBus).fireEvent((ExceptionThrownEvent)anyObject());
        verify(console).print(anyString());
    }

    @Test
    public void testOnCancelClicked() throws Exception {
        presenter.onCancelClicked();

        verify(view).close();
    }

    @Test
    public void testOnMergeClicked() throws Exception {
        when(selectedReference.getDisplayName()).thenReturn(DISPLAY_NAME);

        presenter.onReferenceSelected(selectedReference);
        presenter.onMergeClicked();

        verify(service).merge(eq(VFS_ID), anyString(), eq(DISPLAY_NAME), (AsyncRequestCallback<MergeResult>)anyObject());
    }

    @Test
    public void testOnMergeClickedWhenExceptionHappened() throws Exception {
        when(selectedReference.getDisplayName()).thenReturn(DISPLAY_NAME);
        doThrow(RequestException.class).when(service)
                .merge(anyString(), anyString(), anyString(), (AsyncRequestCallback<MergeResult>)anyObject());

        presenter.onReferenceSelected(selectedReference);
        presenter.onMergeClicked();

        verify(service).merge(eq(VFS_ID), anyString(), eq(DISPLAY_NAME), (AsyncRequestCallback<MergeResult>)anyObject());

        verify(eventBus).fireEvent((ExceptionThrownEvent)anyObject());
        verify(console).print(anyString());
    }

    @Test
    public void testOnReferenceSelected() throws Exception {
        when(selectedReference.getDisplayName()).thenReturn(DISPLAY_NAME);

        presenter.onReferenceSelected(selectedReference);
        presenter.onMergeClicked();

        verify(service).merge(anyString(), anyString(), eq(DISPLAY_NAME), (AsyncRequestCallback<MergeResult>)anyObject());
    }
}