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
package com.codenvy.ide.ext.git.client.reset.commit;

import com.codenvy.ide.ext.git.client.BaseTest;
import com.codenvy.ide.ext.git.shared.LogResponse;
import com.codenvy.ide.ext.git.shared.ResetRequest;
import com.codenvy.ide.ext.git.shared.Revision;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static com.codenvy.ide.ext.git.shared.ResetRequest.ResetType.MIXED;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Testing {@link ResetToCommitPresenter} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class ResetToCommitPresenterTest extends BaseTest {
    public static final boolean IS_TEXT_FORMATTED = true;
    public static final boolean IS_MIXED          = true;
    public static final String  REVISION_ID       = "revisionId";
    @Mock
    private ResetToCommitView      view;
    @Mock
    private Revision               selectedRevision;
    @InjectMocks
    private ResetToCommitPresenter presenter;

    @Test
    public void testShowDialog() throws Exception {
        presenter.showDialog();

        verify(resourceProvider).getActiveProject();
        verify(service).log(eq(VFS_ID), eq(PROJECT_ID), eq(!IS_TEXT_FORMATTED), (AsyncRequestCallback<LogResponse>)anyObject());
        verify(console, never()).print(anyString());
    }

    @Test
    public void testShowDialogWhenExceptionHappened() throws Exception {
        doThrow(RequestException.class).when(service)
                .log(anyString(), anyString(), anyBoolean(), (AsyncRequestCallback<LogResponse>)anyObject());

        presenter.showDialog();

        verify(resourceProvider).getActiveProject();
        verify(service).log(eq(VFS_ID), eq(PROJECT_ID), eq(!IS_TEXT_FORMATTED), (AsyncRequestCallback<LogResponse>)anyObject());
        verify(console).print(anyString());
    }

    @Test
    public void testOnResetClicked() throws Exception {
        when(view.isMixMode()).thenReturn(IS_MIXED);
        when(selectedRevision.getId()).thenReturn(PROJECT_ID);

        presenter.onRevisionSelected(selectedRevision);
        presenter.onResetClicked();

        verify(service).reset(eq(VFS_ID), anyString(), eq(PROJECT_ID), eq(MIXED), (AsyncRequestCallback<String>)anyObject());
        verify(console, never()).print(anyString());
    }

    @Test
    public void testOnResetClickedWhenExceptionHappened() throws Exception {
        when(view.isMixMode()).thenReturn(IS_MIXED);
        when(selectedRevision.getId()).thenReturn(REVISION_ID);
        doThrow(RequestException.class).when(service).reset(anyString(), anyString(), anyString(), (ResetRequest.ResetType)anyObject(),
                                                            (AsyncRequestCallback<String>)anyObject());

        presenter.onRevisionSelected(selectedRevision);
        presenter.onResetClicked();

        verify(service).reset(eq(VFS_ID), anyString(), eq(REVISION_ID), eq(MIXED), (AsyncRequestCallback<String>)anyObject());
        verify(console).print(anyString());
    }

    @Test
    public void testOnCancelClicked() throws Exception {
        presenter.onCancelClicked();

        verify(view).close();
    }

    @Test
    public void testOnRevisionSelected() throws Exception {
        presenter.onRevisionSelected(selectedRevision);

        verify(view).setEnableResetButton(eq(ENABLE_BUTTON));
    }
}