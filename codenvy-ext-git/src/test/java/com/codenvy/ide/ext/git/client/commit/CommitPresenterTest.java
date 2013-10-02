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
package com.codenvy.ide.ext.git.client.commit;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.ext.git.client.BaseTest;
import com.codenvy.ide.ext.git.shared.RepoInfo;
import com.codenvy.ide.ext.git.shared.Revision;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Testing {@link CommitPresenter} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class CommitPresenterTest extends BaseTest {
    public static final boolean ALL_FILE_INCLUDES = true;
    public static final boolean IS_OVERWRITTEN    = true;
    public static final String  COMMIT_TEXT       = "commit text";
    @Mock
    private CommitView      view;
    @Mock
    private Revision        revision;
    private CommitPresenter presenter;

    @Override
    public void disarm() {
        super.disarm();

        presenter = new CommitPresenter(view, service, resourceProvider, constant, eventBus, notificationManager);

        when(revision.fake()).thenReturn(false);
    }

    @Test
    public void testShowDialog() throws Exception {
        presenter.showDialog();

        verify(view).setAmend(eq(!IS_OVERWRITTEN));
        verify(view).setAllFilesInclude(eq(!ALL_FILE_INCLUDES));
        verify(view).setMessage(EMPTY_TEXT);
        verify(view).focusInMessageField();
        verify(view).setEnableCommitButton(eq(DISABLE_BUTTON));
        verify(view).showDialog();
    }

    @Test
    public void testOnCommitClickedWhenCommitWSRequestIsSuccessful() throws Exception {
        when(view.getMessage()).thenReturn(COMMIT_TEXT);
        when(view.isAllFilesInclued()).thenReturn(ALL_FILE_INCLUDES);
        when(view.isAmend()).thenReturn(IS_OVERWRITTEN);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                RequestCallback<RepoInfo> callback = (RequestCallback<RepoInfo>)arguments[5];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, revision);
                return callback;
            }
        }).when(service).commitWS(anyString(), (Project)anyObject(), anyString(), anyBoolean(), anyBoolean(),
                                  (RequestCallback<Revision>)anyObject());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<Project> callback = (AsyncCallback<Project>)arguments[1];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, project);
                return callback;
            }
        }).when(resourceProvider).getProject(anyString(), (AsyncCallback<Project>)anyObject());

        presenter.showDialog();
        presenter.onCommitClicked();

        verify(view).getMessage();
        verify(view).isAllFilesInclued();
        verify(view).isAmend();
        verify(view).close();

        verify(service).commitWS(eq(VFS_ID), eq(project), eq(COMMIT_TEXT), eq(ALL_FILE_INCLUDES), eq(IS_OVERWRITTEN),
                                 (RequestCallback<Revision>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
    }

    @Test
    public void testOnCommitClickedWhenCommitWSRequestIsFailed() throws Exception {
        when(view.getMessage()).thenReturn(COMMIT_TEXT);
        when(view.isAllFilesInclued()).thenReturn(ALL_FILE_INCLUDES);
        when(view.isAmend()).thenReturn(IS_OVERWRITTEN);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                RequestCallback<RepoInfo> callback = (RequestCallback<RepoInfo>)arguments[5];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).commitWS(anyString(), (Project)anyObject(), anyString(), anyBoolean(), anyBoolean(),
                                  (RequestCallback<Revision>)anyObject());

        presenter.showDialog();
        presenter.onCommitClicked();

        verify(view).getMessage();
        verify(view).isAllFilesInclued();
        verify(view).isAmend();
        verify(view).close();

        verify(service).commitWS(eq(VFS_ID), eq(project), eq(COMMIT_TEXT), eq(ALL_FILE_INCLUDES), eq(IS_OVERWRITTEN),
                                 (RequestCallback<Revision>)anyObject());
        verify(constant).commitFailed();
        verify(notificationManager).showNotification((Notification)anyObject());
    }

    @Test
    public void testOnCommitClickedWhenCommitRequestIsSuccessful() throws Exception {
        when(view.getMessage()).thenReturn(COMMIT_TEXT);
        when(view.isAllFilesInclued()).thenReturn(ALL_FILE_INCLUDES);
        when(view.isAmend()).thenReturn(IS_OVERWRITTEN);
        doThrow(WebSocketException.class).when(service).commitWS(anyString(), (Project)anyObject(), anyString(), anyBoolean(), anyBoolean(),
                                                                 (RequestCallback<Revision>)anyObject());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<RepoInfo> callback = (AsyncRequestCallback<RepoInfo>)arguments[5];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, revision);
                return callback;
            }
        }).when(service).commit(anyString(), (Project)anyObject(), anyString(), anyBoolean(), anyBoolean(),
                                (AsyncRequestCallback<Revision>)anyObject());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<Project> callback = (AsyncCallback<Project>)arguments[1];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, project);
                return callback;
            }
        }).when(resourceProvider).getProject(anyString(), (AsyncCallback<Project>)anyObject());

        presenter.showDialog();
        presenter.onCommitClicked();

        verify(view).getMessage();
        verify(view).isAllFilesInclued();
        verify(view).isAmend();
        verify(view).close();

        verify(service).commitWS(eq(VFS_ID), eq(project), eq(COMMIT_TEXT), eq(ALL_FILE_INCLUDES), eq(IS_OVERWRITTEN),
                                 (RequestCallback<Revision>)anyObject());
        verify(service).commit(eq(VFS_ID), eq(project), eq(COMMIT_TEXT), eq(ALL_FILE_INCLUDES), eq(IS_OVERWRITTEN),
                               (AsyncRequestCallback<Revision>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
    }

    @Test
    public void testOnCommitClickedWhenCommitRequestIsFailed() throws Exception {
        when(view.getMessage()).thenReturn(COMMIT_TEXT);
        when(view.isAllFilesInclued()).thenReturn(ALL_FILE_INCLUDES);
        when(view.isAmend()).thenReturn(IS_OVERWRITTEN);
        doThrow(WebSocketException.class).when(service).commitWS(anyString(), (Project)anyObject(), anyString(), anyBoolean(), anyBoolean(),
                                                                 (RequestCallback<Revision>)anyObject());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<RepoInfo> callback = (AsyncRequestCallback<RepoInfo>)arguments[5];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).commit(anyString(), (Project)anyObject(), anyString(), anyBoolean(), anyBoolean(),
                                (AsyncRequestCallback<Revision>)anyObject());

        presenter.showDialog();
        presenter.onCommitClicked();

        verify(view).getMessage();
        verify(view).isAllFilesInclued();
        verify(view).isAmend();
        verify(view).close();

        verify(service).commitWS(eq(VFS_ID), eq(project), eq(COMMIT_TEXT), eq(ALL_FILE_INCLUDES), eq(IS_OVERWRITTEN),
                                 (RequestCallback<Revision>)anyObject());
        verify(service).commit(eq(VFS_ID), eq(project), eq(COMMIT_TEXT), eq(ALL_FILE_INCLUDES), eq(IS_OVERWRITTEN),
                               (AsyncRequestCallback<Revision>)anyObject());
        verify(constant).commitFailed();
        verify(notificationManager).showNotification((Notification)anyObject());
    }

    @Test
    public void testOnCommitClickedWhenRequestExceptionHappened() throws Exception {
        when(view.getMessage()).thenReturn(COMMIT_TEXT);
        when(view.isAllFilesInclued()).thenReturn(ALL_FILE_INCLUDES);
        when(view.isAmend()).thenReturn(IS_OVERWRITTEN);
        doThrow(WebSocketException.class).when(service).commitWS(anyString(), (Project)anyObject(), anyString(), anyBoolean(), anyBoolean(),
                                                                 (RequestCallback<Revision>)anyObject());
        doThrow(RequestException.class).when(service).commit(anyString(), (Project)anyObject(), anyString(), anyBoolean(), anyBoolean(),
                                                             (AsyncRequestCallback<Revision>)anyObject());

        presenter.showDialog();
        presenter.onCommitClicked();

        verify(view).getMessage();
        verify(view).isAllFilesInclued();
        verify(view).isAmend();
        verify(view).close();

        verify(service).commitWS(eq(VFS_ID), eq(project), eq(COMMIT_TEXT), eq(ALL_FILE_INCLUDES), eq(IS_OVERWRITTEN),
                                 (RequestCallback<Revision>)anyObject());
        verify(service).commit(eq(VFS_ID), eq(project), eq(COMMIT_TEXT), eq(ALL_FILE_INCLUDES), eq(IS_OVERWRITTEN),
                               (AsyncRequestCallback<Revision>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
    }

    @Test
    public void testOnCancelClicked() throws Exception {
        presenter.onCancelClicked();

        verify(view).close();
    }

    @Test
    public void testOnValueChangedWhenCommitMessageEmpty() throws Exception {
        when(view.getMessage()).thenReturn(EMPTY_TEXT);

        presenter.onValueChanged();

        verify(view).setEnableCommitButton(eq(DISABLE_BUTTON));
    }

    @Test
    public void testOnValueChanged() throws Exception {
        when(view.getMessage()).thenReturn(COMMIT_TEXT);

        presenter.onValueChanged();

        verify(view).setEnableCommitButton(eq(!DISABLE_BUTTON));
    }
}