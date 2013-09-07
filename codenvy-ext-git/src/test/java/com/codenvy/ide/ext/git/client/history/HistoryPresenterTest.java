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
package com.codenvy.ide.ext.git.client.history;

import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.ui.workspace.PartPresenter;
import com.codenvy.ide.api.ui.workspace.PartStack;
import com.codenvy.ide.api.ui.workspace.PartStackType;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.ext.git.client.BaseTest;
import com.codenvy.ide.ext.git.shared.DiffRequest;
import com.codenvy.ide.ext.git.shared.LogResponse;
import com.codenvy.ide.ext.git.shared.Revision;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.*;

/**
 * Testing {@link HistoryPresenter} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class HistoryPresenterTest extends BaseTest {
    private static final boolean TEXT_NOT_FORMATTED = false;
    @Mock
    private HistoryView      view;
    @Mock
    private WorkspaceAgent   workspaceAgent;
    @Mock
    private Revision         selectedRevision;
    @Mock
    private SelectionAgent   selectionAgent;
    @Mock
    private PartStack        partStack;
    @InjectMocks
    private HistoryPresenter presenter;

    @Before
    public void disarm() {
        super.disarm();

        presenter.setPartStack(partStack);
    }

    @Test
    public void testShowDialog() throws Exception {
        PartPresenter partPresenter = mock(PartPresenter.class);
        when(partStack.getActivePart()).thenReturn(partPresenter);

        presenter.showDialog();

        verify(resourceProvider).getActiveProject();
        verify(service).log(eq(VFS_ID), eq(PROJECT_ID), eq(TEXT_NOT_FORMATTED), (AsyncRequestCallback<LogResponse>)anyObject());
        verify(view).selectProjectChangesButton(eq(SELECTED_ITEM));
        verify(view).selectDiffWithPrevVersionButton(eq(SELECTED_ITEM));
        verify(view).setCommitADate(eq(EMPTY_TEXT));
        verify(view).setCommitARevision(eq(EMPTY_TEXT));
        verify(view).setCommitBDate(eq(EMPTY_TEXT));
        verify(view).setCommitBRevision(eq(EMPTY_TEXT));
        verify(view).setDiffContext(eq(EMPTY_TEXT));
        verify(view).setCompareType(anyString());
        verify(workspaceAgent).openPart(eq(presenter), eq(PartStackType.TOOLING));
        verify(partStack).getActivePart();
        verify(partStack).setActivePart(eq(presenter));
    }

    @Test
    public void testOnRefreshClicked() throws Exception {
        presenter.onRefreshClicked();

        verify(resourceProvider).getActiveProject();
        verify(service).log(eq(VFS_ID), eq(PROJECT_ID), eq(TEXT_NOT_FORMATTED), (AsyncRequestCallback<LogResponse>)anyObject());
        verify(console, never()).print(anyString());
    }

    @Test
    public void testOnRefreshClickedWhenExceptionHappened() throws Exception {
        doThrow(RequestException.class).when(service)
                .log(anyString(), anyString(), anyBoolean(), (AsyncRequestCallback<LogResponse>)anyObject());

        presenter.onRefreshClicked();

        verify(resourceProvider).getActiveProject();
        verify(service).log(eq(VFS_ID), eq(PROJECT_ID), eq(TEXT_NOT_FORMATTED), (AsyncRequestCallback<LogResponse>)anyObject());

        verify(console).print(anyString());
        verify(view).setCompareType(anyString());
        verify(view).setDiffContext(eq(EMPTY_TEXT));
        verify(view).setCommitADate(anyString());
        verify(view).setCommitARevision(anyString());
        verify(view).setCommitBDate(eq(EMPTY_TEXT));
        verify(view).setCommitBRevision(eq(EMPTY_TEXT));
    }

    @Test
    @Ignore
    public void testOnProjectChangesClicked() throws Exception {
        // TODO not possible to check because need to change AsyncRequestCallback
        presenter.onProjectChangesClicked();

        verify(view).selectProjectChangesButton(eq(DISABLE_BUTTON));
        verify(view).selectResourceChangesButton(eq(ENABLE_BUTTON));
    }

    @Test
    @Ignore
    public void testOnResourceChangesClicked() throws Exception {
        // TODO not possible to test because need to use method OnProjectChangesClicked
        presenter.onProjectChangesClicked();

        presenter.onResourceChangesClicked();

        verify(view).selectProjectChangesButton(eq(DISABLE_BUTTON));
        verify(view).selectResourceChangesButton(eq(ENABLE_BUTTON));
    }

    @Test
    public void testOnDiffWithIndexClicked() throws Exception {
        presenter.onDiffWithIndexClicked();

        verify(view).selectDiffWithIndexButton(eq(SELECTED_ITEM));
        verify(view).selectDiffWithPrevVersionButton(eq(UNSELECTED_ITEM));
        verify(view).selectDiffWithWorkingTreeButton(eq(UNSELECTED_ITEM));

        verify(view).setDiffContext(eq(EMPTY_TEXT));
        verify(service).log(eq(VFS_ID), eq(PROJECT_ID), eq(TEXT_NOT_FORMATTED), (AsyncRequestCallback<LogResponse>)anyObject());
    }

    @Test
    public void testOnDiffWithIndexTwiceClicked() throws Exception {
        presenter.onDiffWithIndexClicked();
        reset(view);

        presenter.onDiffWithIndexClicked();

        verify(view, never()).selectDiffWithIndexButton(anyBoolean());
        verify(view, never()).selectDiffWithPrevVersionButton(anyBoolean());
        verify(view, never()).selectDiffWithWorkingTreeButton(anyBoolean());
    }

    @Test
    public void testOnDiffWithWorkTreeClicked() throws Exception {
        presenter.onDiffWithWorkTreeClicked();

        verify(view).selectDiffWithWorkingTreeButton(eq(SELECTED_ITEM));
        verify(view).selectDiffWithIndexButton(eq(UNSELECTED_ITEM));
        verify(view).selectDiffWithPrevVersionButton(eq(UNSELECTED_ITEM));

        verify(view).setDiffContext(eq(EMPTY_TEXT));
        verify(service).log(eq(VFS_ID), eq(PROJECT_ID), eq(TEXT_NOT_FORMATTED), (AsyncRequestCallback<LogResponse>)anyObject());
    }

    @Test
    public void testOnDiffWithWorkTreeTwiceClicked() throws Exception {
        presenter.onDiffWithWorkTreeClicked();
        reset(view);

        presenter.onDiffWithWorkTreeClicked();

        verify(view, never()).selectDiffWithIndexButton(anyBoolean());
        verify(view, never()).selectDiffWithPrevVersionButton(anyBoolean());
        verify(view, never()).selectDiffWithWorkingTreeButton(anyBoolean());
    }

    @Test
    public void testOnDiffWithPrevCommitClicked() throws Exception {
        presenter.onDiffWithPrevCommitClicked();

        verify(view).selectDiffWithPrevVersionButton(eq(SELECTED_ITEM));
        verify(view).selectDiffWithIndexButton(eq(UNSELECTED_ITEM));
        verify(view).selectDiffWithWorkingTreeButton(eq(UNSELECTED_ITEM));

        verify(view).setDiffContext(eq(EMPTY_TEXT));
        verify(service).log(eq(VFS_ID), eq(PROJECT_ID), eq(TEXT_NOT_FORMATTED), (AsyncRequestCallback<LogResponse>)anyObject());
    }

    @Test
    public void testOnDiffWithPrevCommitTwiceClicked() throws Exception {
        presenter.onDiffWithPrevCommitClicked();
        reset(view);

        presenter.onDiffWithPrevCommitClicked();

        verify(view, never()).selectDiffWithIndexButton(anyBoolean());
        verify(view, never()).selectDiffWithPrevVersionButton(anyBoolean());
        verify(view, never()).selectDiffWithWorkingTreeButton(anyBoolean());
    }

    @Test
    public void testOnRevisionSelected() throws Exception {
        presenter.showDialog();
        presenter.onDiffWithIndexClicked();
        reset(view);

        String revisionId = "revisonId";
        when(selectedRevision.getId()).thenReturn(revisionId);
        presenter.onRevisionSelected(selectedRevision);

        verify(service)
                .diff(eq(VFS_ID), eq(PROJECT_ID), (JsonArray<String>)anyObject(), eq(DiffRequest.DiffType.RAW), anyBoolean(), anyInt(),
                      eq(revisionId), anyBoolean(), (AsyncRequestCallback<String>)anyObject());
    }

    @Test
    public void testGo() throws Exception {
        AcceptsOneWidget container = mock(AcceptsOneWidget.class);

        presenter.go(container);

        verify(container).setWidget(eq(view));
    }
}