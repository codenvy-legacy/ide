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

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.ext.git.client.BaseTest;
import com.codenvy.ide.ext.git.shared.LogResponse;
import com.codenvy.ide.ext.git.shared.ResetRequest;
import com.codenvy.ide.ext.git.shared.Revision;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;
import java.util.ArrayList;

import static com.codenvy.ide.ext.git.shared.ResetRequest.ResetType.MIXED;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link ResetToCommitPresenter} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class ResetToCommitPresenterTest extends BaseTest {
    public static final boolean IS_TEXT_FORMATTED = true;
    public static final boolean IS_MIXED          = true;
    @Mock
    private ResetToCommitView      view;
    @Mock
    private Revision               selectedRevision;
    @InjectMocks
    private ResetToCommitPresenter presenter;

    @Override
    public void disarm() {
        super.disarm();

        presenter = new ResetToCommitPresenter(view, service, resourceProvider, constant, notificationManager, dtoUnmarshallerFactory);
    }

    @Test
    public void testShowDialogWhenLogRequestIsSuccessful() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[3];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, mock(LogResponse.class));
                return callback;

            }
        }).when(service).log(anyString(), anyString(), anyBoolean(), (AsyncRequestCallback<LogResponse>)anyObject());

        presenter.showDialog();

        verify(resourceProvider).getActiveProject();
        verify(service).log(eq(VFS_ID), eq(PROJECT_ID), eq(!IS_TEXT_FORMATTED), (AsyncRequestCallback<LogResponse>)anyObject());
        verify(view).setRevisions((ArrayList<Revision>)anyObject());
        verify(view).setMixMode(eq(IS_MIXED));
        verify(view).setEnableResetButton(eq(DISABLE_BUTTON));
        verify(view).showDialog();
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
        }).when(service).log(anyString(), anyString(), anyBoolean(), (AsyncRequestCallback<LogResponse>)anyObject());

        presenter.showDialog();

        verify(resourceProvider).getActiveProject();
        verify(service).log(eq(VFS_ID), eq(PROJECT_ID), eq(!IS_TEXT_FORMATTED), (AsyncRequestCallback<LogResponse>)anyObject());
        verify(constant).logFailed();
        verify(notificationManager).showNotification((Notification)anyObject());
    }

    @Test
    public void testOnResetClickedWhenResetRequestIsSuccessful() throws Exception {
        when(view.isMixMode()).thenReturn(IS_MIXED);
        when(selectedRevision.getId()).thenReturn(PROJECT_ID);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Void> callback = (AsyncRequestCallback<Void>)arguments[4];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, (Void)null);
                return callback;
            }
        }).when(service).reset(anyString(), anyString(), anyString(), (ResetRequest.ResetType)anyObject(),
                               (AsyncRequestCallback<Void>)anyObject());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<Project> callback = (AsyncCallback<Project>)arguments[0];
                callback.onSuccess(project);
                return callback;
            }
        }).when(project).refreshProperties((AsyncCallback<Project>)anyObject());

        presenter.onRevisionSelected(selectedRevision);
        presenter.onResetClicked();

        verify(service).reset(eq(VFS_ID), anyString(), eq(PROJECT_ID), eq(MIXED), (AsyncRequestCallback<Void>)anyObject());
        verify(notificationManager, never()).showNotification((Notification)anyObject());
    }

    @Test
    public void testOnResetClickedWhenResetRequestIsFailed() throws Exception {
        when(view.isMixMode()).thenReturn(IS_MIXED);
        when(selectedRevision.getId()).thenReturn(PROJECT_ID);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Void> callback = (AsyncRequestCallback<Void>)arguments[4];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).reset(anyString(), anyString(), anyString(), (ResetRequest.ResetType)anyObject(),
                               (AsyncRequestCallback<Void>)anyObject());

        presenter.onRevisionSelected(selectedRevision);
        presenter.onResetClicked();

        verify(service).reset(eq(VFS_ID), anyString(), eq(PROJECT_ID), eq(MIXED), (AsyncRequestCallback<Void>)anyObject());
        verify(constant).resetFail();
        verify(notificationManager).showNotification((Notification)anyObject());
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