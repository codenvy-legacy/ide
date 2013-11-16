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

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.ui.workspace.PartPresenter;
import com.codenvy.ide.api.ui.workspace.PartStack;
import com.codenvy.ide.api.ui.workspace.PartStackType;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ext.git.client.BaseTest;
import com.codenvy.ide.ext.git.shared.DiffRequest;
import com.codenvy.ide.ext.git.shared.LogResponse;
import com.codenvy.ide.ext.git.shared.Revision;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.codenvy.ide.ext.git.shared.DiffRequest.DiffType.RAW;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link HistoryPresenter} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class HistoryPresenterTest extends BaseTest {
    public static final boolean TEXT_NOT_FORMATTED = false;
    public static final String  REVISION_ID        = "revisionId";
    public static final boolean NO_RENAMES         = false;
    public static final int     RENAME_LIMIT       = 0;
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
    @Mock
    private PartPresenter    partPresenter;
    @Mock
    private LogResponse      logResponse;
    private HistoryPresenter presenter;

    @Override
    public void disarm() {
        super.disarm();

        presenter = new HistoryPresenter(view, service, constant, resources, resourceProvider, workspaceAgent, selectionAgent,
                                         notificationManager, dtoFactory);
        presenter.setPartStack(partStack);

        when(partStack.getActivePart()).thenReturn(partPresenter);
        when(selectedRevision.getId()).thenReturn(REVISION_ID);
    }

    @Test
    public void testShowDialogWhenLogRequestIsSuccessful() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[3];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, logResponse);
                return callback;
            }
        }).when(service).log(anyString(), anyString(), anyBoolean(), (AsyncRequestCallback<String>)anyObject());

        presenter.showDialog();

        verify(resourceProvider).getActiveProject();
        verify(service).log(eq(VFS_ID), eq(PROJECT_ID), eq(TEXT_NOT_FORMATTED), (AsyncRequestCallback<String>)anyObject());
        verify(view).selectProjectChangesButton(eq(SELECTED_ITEM));
        verify(view).selectDiffWithPrevVersionButton(eq(SELECTED_ITEM));
        verify(view).setCommitADate(eq(EMPTY_TEXT));
        verify(view).setCommitARevision(eq(EMPTY_TEXT));
        verify(view).setCommitBDate(eq(EMPTY_TEXT));
        verify(view).setCommitBRevision(eq(EMPTY_TEXT));
        verify(view).setDiffContext(eq(EMPTY_TEXT));
        verify(view).setCompareType(anyString());
        verify(view).setRevisions((JsonArray<Revision>)anyObject());
        verify(workspaceAgent).openPart(eq(presenter), eq(PartStackType.TOOLING));
        verify(partStack).getActivePart();
        verify(partStack).setActivePart(eq(presenter));
    }

    @Test
    public void testShowDialogWhenLogRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[3];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).log(anyString(), anyString(), anyBoolean(), (AsyncRequestCallback<String>)anyObject());

        presenter.showDialog();

        verify(resourceProvider).getActiveProject();
        verify(service).log(eq(VFS_ID), eq(PROJECT_ID), eq(TEXT_NOT_FORMATTED), (AsyncRequestCallback<String>)anyObject());
        verify(view).selectProjectChangesButton(eq(SELECTED_ITEM));
        verify(view).selectDiffWithPrevVersionButton(eq(SELECTED_ITEM));
        verify(view, times(2)).setCommitADate(eq(EMPTY_TEXT));
        verify(view, times(2)).setCommitARevision(eq(EMPTY_TEXT));
        verify(view, times(2)).setCommitBDate(eq(EMPTY_TEXT));
        verify(view, times(2)).setCommitBRevision(eq(EMPTY_TEXT));
        verify(view, times(2)).setDiffContext(eq(EMPTY_TEXT));
        verify(view, times(2)).setCompareType(anyString());
        verify(workspaceAgent).openPart(eq(presenter), eq(PartStackType.TOOLING));
        verify(partStack).getActivePart();
        verify(partStack).setActivePart(eq(presenter));
        verify(constant, times(2)).historyNothingToDisplay();
        verify(constant).logFailed();
        verify(notificationManager).showNotification((Notification)anyObject());
    }

    @Test
    public void testShowDialogWhenRequestExceptionHappened() throws Exception {
        doThrow(RequestException.class).when(service)
                .log(anyString(), anyString(), anyBoolean(), (AsyncRequestCallback<String>)anyObject());

        presenter.showDialog();

        verify(resourceProvider).getActiveProject();
        verify(service).log(eq(VFS_ID), eq(PROJECT_ID), eq(TEXT_NOT_FORMATTED), (AsyncRequestCallback<String>)anyObject());
        verify(view).selectProjectChangesButton(eq(SELECTED_ITEM));
        verify(view).selectDiffWithPrevVersionButton(eq(SELECTED_ITEM));
        verify(view, times(2)).setCommitADate(eq(EMPTY_TEXT));
        verify(view, times(2)).setCommitARevision(eq(EMPTY_TEXT));
        verify(view, times(2)).setCommitBDate(eq(EMPTY_TEXT));
        verify(view, times(2)).setCommitBRevision(eq(EMPTY_TEXT));
        verify(view, times(2)).setDiffContext(eq(EMPTY_TEXT));
        verify(view, times(2)).setCompareType(anyString());
        verify(workspaceAgent).openPart(eq(presenter), eq(PartStackType.TOOLING));
        verify(partStack).getActivePart();
        verify(partStack).setActivePart(eq(presenter));
        verify(constant, times(2)).historyNothingToDisplay();
        verify(constant).logFailed();
        verify(notificationManager).showNotification((Notification)anyObject());
    }

    @Test
    public void testOnRefreshClicked() throws Exception {
        presenter.onRefreshClicked();

        verify(resourceProvider).getActiveProject();
        verify(service).log(eq(VFS_ID), eq(PROJECT_ID), eq(TEXT_NOT_FORMATTED), (AsyncRequestCallback<String>)anyObject());
    }

    @Test
    public void testOnProjectChangesClickedWhenDiffRequestIsSuccessful() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[8];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, EMPTY_TEXT);
                return callback;
            }
        }).when(service)
                .diff(anyString(), anyString(), (List<String>)anyObject(), (DiffRequest.DiffType)anyObject(), anyBoolean(), anyInt(),
                      anyString(), anyBoolean(), (AsyncRequestCallback<String>)anyObject());

        presenter.onDiffWithIndexClicked();
        presenter.onRevisionSelected(selectedRevision);
        reset(view);
        presenter.onProjectChangesClicked();

        verify(view).selectProjectChangesButton(eq(DISABLE_BUTTON));
        verify(view).selectResourceChangesButton(eq(ENABLE_BUTTON));
        verify(service)
                .diff(eq(VFS_ID), eq(PROJECT_ID), (List<String>)anyObject(), eq(RAW), eq(NO_RENAMES), eq(RENAME_LIMIT),
                      eq(REVISION_ID), anyBoolean(), (AsyncRequestCallback<String>)anyObject());

        verify(view).setDiffContext(eq(EMPTY_TEXT));
        verify(constant).historyDiffIndexState();
        verify(view).setCommitADate(anyString());
        verify(view).setCommitARevision(anyString());
        verify(view).setCompareType(anyString());
    }

    @Test
    public void testOnProjectChangesClickedWhenDiffRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[8];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service)
                .diff(anyString(), anyString(), (List<String>)anyObject(), (DiffRequest.DiffType)anyObject(), anyBoolean(), anyInt(),
                      anyString(), anyBoolean(), (AsyncRequestCallback<String>)anyObject());

        presenter.onDiffWithIndexClicked();
        presenter.onRevisionSelected(selectedRevision);
        reset(view);
        presenter.onProjectChangesClicked();

        verify(view).selectProjectChangesButton(eq(DISABLE_BUTTON));
        verify(view).selectResourceChangesButton(eq(ENABLE_BUTTON));
        verify(service)
                .diff(eq(VFS_ID), eq(PROJECT_ID), (List<String>)anyObject(), eq(RAW), eq(NO_RENAMES), eq(RENAME_LIMIT),
                      eq(REVISION_ID), anyBoolean(), (AsyncRequestCallback<String>)anyObject());
        verify(constant).diffFailed();
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(view).setCommitADate(anyString());
        verify(view).setCommitARevision(anyString());
        verify(view).setCommitBDate(eq(EMPTY_TEXT));
        verify(view).setCommitBRevision(eq(EMPTY_TEXT));
        verify(view).setDiffContext(eq(EMPTY_TEXT));
        verify(view).setCompareType(anyString());
        verify(constant).historyNothingToDisplay();
    }

    @Test
    public void testOnProjectChangesClickedWhenRequestExceptionHappened() throws Exception {
        doThrow(RequestException.class).when(service).diff(anyString(), anyString(), (List<String>)anyObject(),
                                                           (DiffRequest.DiffType)anyObject(), anyBoolean(), anyInt(),
                                                           anyString(), anyBoolean(), (AsyncRequestCallback<String>)anyObject());

        presenter.onDiffWithIndexClicked();
        presenter.onRevisionSelected(selectedRevision);
        reset(view);
        presenter.onProjectChangesClicked();

        verify(view).selectProjectChangesButton(eq(DISABLE_BUTTON));
        verify(view).selectResourceChangesButton(eq(ENABLE_BUTTON));
        verify(service)
                .diff(eq(VFS_ID), eq(PROJECT_ID), (List<String>)anyObject(), eq(RAW), eq(NO_RENAMES), eq(RENAME_LIMIT),
                      eq(REVISION_ID), anyBoolean(), (AsyncRequestCallback<String>)anyObject());
        verify(view).setCommitADate(anyString());
        verify(view).setCommitARevision(anyString());
        verify(view).setCommitBDate(eq(EMPTY_TEXT));
        verify(view).setCommitBRevision(eq(EMPTY_TEXT));
        verify(view).setDiffContext(eq(EMPTY_TEXT));
        verify(view).setCompareType(anyString());
        verify(constant).historyNothingToDisplay();
    }

    @Test
    public void testOnResourceChangesClicked() throws Exception {
        presenter.onProjectChangesClicked();
        reset(view);
        presenter.onResourceChangesClicked();

        verify(view).selectProjectChangesButton(eq(DISABLE_BUTTON));
        verify(view).selectResourceChangesButton(eq(ENABLE_BUTTON));
        verify(view).setDiffContext(eq(EMPTY_TEXT));
    }

    @Test
    public void testOnDiffWithIndexClicked() throws Exception {
        presenter.onDiffWithIndexClicked();

        verify(view).selectDiffWithIndexButton(eq(SELECTED_ITEM));
        verify(view).selectDiffWithPrevVersionButton(eq(UNSELECTED_ITEM));
        verify(view).selectDiffWithWorkingTreeButton(eq(UNSELECTED_ITEM));
        verify(view).setDiffContext(eq(EMPTY_TEXT));
        verify(service).log(eq(VFS_ID), eq(PROJECT_ID), eq(TEXT_NOT_FORMATTED), (AsyncRequestCallback<String>)anyObject());
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
        verify(service).log(eq(VFS_ID), eq(PROJECT_ID), eq(TEXT_NOT_FORMATTED), (AsyncRequestCallback<String>)anyObject());
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
        verify(service).log(eq(VFS_ID), eq(PROJECT_ID), eq(TEXT_NOT_FORMATTED), (AsyncRequestCallback<String>)anyObject());
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
    public void testOnRevisionSelectedWhenDiffRequestIsSuccessful() throws Exception {
        List<Revision> revisions = new ArrayList<Revision>();
        revisions.add(selectedRevision);
        revisions.add(selectedRevision);
        when(logResponse.getCommits()).thenReturn(revisions);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[3];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, logResponse);
                return callback;
            }
        }).when(service).log(anyString(), anyString(), anyBoolean(), (AsyncRequestCallback<String>)anyObject());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[8];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, EMPTY_TEXT);
                return callback;
            }
        }).when(service)
                .diff(anyString(), anyString(), (List<String>)anyObject(), (DiffRequest.DiffType)anyObject(), anyBoolean(), anyInt(),
                      anyString(), anyString(), (AsyncRequestCallback<String>)anyObject());

        presenter.showDialog();
        reset(view);
        presenter.onRevisionSelected(selectedRevision);

        verify(service)
                .diff(eq(VFS_ID), eq(PROJECT_ID), (List<String>)anyObject(), eq(DiffRequest.DiffType.RAW), anyBoolean(), anyInt(),
                      eq(REVISION_ID), eq(REVISION_ID), (AsyncRequestCallback<String>)anyObject());
        verify(view).setDiffContext(eq(EMPTY_TEXT));
        verify(view).setCommitADate(anyString());
        verify(view).setCommitARevision(anyString());
        verify(view).setCommitBDate(anyString());
        verify(view).setCommitBRevision(anyString());
    }

    @Test
    public void testOnRevisionSelectedWhenDiffRequestIsFailed() throws Exception {
        List<Revision> revisions = new ArrayList<Revision>();
        revisions.add(selectedRevision);
        revisions.add(selectedRevision);
        when(logResponse.getCommits()).thenReturn(revisions);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[3];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, logResponse);
                return callback;
            }
        }).when(service).log(anyString(), anyString(), anyBoolean(), (AsyncRequestCallback<String>)anyObject());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[8];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service)
                .diff(anyString(), anyString(), (List<String>)anyObject(), (DiffRequest.DiffType)anyObject(), anyBoolean(), anyInt(),
                      anyString(), anyString(), (AsyncRequestCallback<String>)anyObject());

        presenter.showDialog();
        reset(view);
        presenter.onRevisionSelected(selectedRevision);

        verify(service)
                .diff(eq(VFS_ID), eq(PROJECT_ID), (List<String>)anyObject(), eq(DiffRequest.DiffType.RAW), anyBoolean(), anyInt(),
                      eq(REVISION_ID), eq(REVISION_ID), (AsyncRequestCallback<String>)anyObject());
        verify(view).setDiffContext(eq(EMPTY_TEXT));
        verify(view).setCommitADate(anyString());
        verify(view).setCommitARevision(anyString());
        verify(view).setCommitBDate(anyString());
        verify(view).setCommitBRevision(anyString());
    }

    @Test
    public void testOnRevisionSelectedWhenRequestExceptionHappened() throws Exception {
        List<Revision> revisions = new ArrayList<Revision>();
        revisions.add(selectedRevision);
        revisions.add(selectedRevision);
        when(logResponse.getCommits()).thenReturn(revisions);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[3];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, logResponse);
                return callback;
            }
        }).when(service).log(anyString(), anyString(), anyBoolean(), (AsyncRequestCallback<String>)anyObject());
        doThrow(RequestException.class).when(service)
                .diff(anyString(), anyString(), (List<String>)anyObject(), (DiffRequest.DiffType)anyObject(), anyBoolean(), anyInt(),
                      anyString(), anyString(), (AsyncRequestCallback<String>)anyObject());

        presenter.showDialog();
        reset(view);
        presenter.onRevisionSelected(selectedRevision);

        verify(service)
                .diff(eq(VFS_ID), eq(PROJECT_ID), (List<String>)anyObject(), eq(DiffRequest.DiffType.RAW), anyBoolean(), anyInt(),
                      eq(REVISION_ID), eq(REVISION_ID), (AsyncRequestCallback<String>)anyObject());
        verify(view).setDiffContext(eq(EMPTY_TEXT));
        verify(view).setCommitADate(anyString());
        verify(view).setCommitARevision(anyString());
        verify(view).setCommitBDate(anyString());
        verify(view).setCommitBRevision(anyString());
        verify(constant).diffFailed();
        verify(notificationManager).showNotification((Notification)anyObject());
    }

    @Test
    public void testGo() throws Exception {
        AcceptsOneWidget container = mock(AcceptsOneWidget.class);

        presenter.go(container);

        verify(container).setWidget(eq(view));
    }
}