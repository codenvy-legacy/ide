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
package com.codenvy.ide.ext.git.client.init;

import com.codenvy.ide.api.event.RefreshBrowserEvent;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.ext.git.client.BaseTest;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link InitRepositoryPresenter} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class InitRepositoryPresenterTest extends BaseTest {
    public static final boolean BARE = true;
    @Mock
    private InitRepositoryView      view;
    private InitRepositoryPresenter presenter;

    @Override
    public void disarm() {
        super.disarm();

        presenter = new InitRepositoryPresenter(view, service, resourceProvider, eventBus, constant, notificationManager);

        when(view.isBare()).thenReturn(BARE);
        when(project.getName()).thenReturn(PROJECT_NAME);
    }

    @Test
    public void testShowDialog() throws Exception {
        presenter.showDialog();

        verify(view).setWorkDir(eq(PROJECT_PATH));
        verify(view).setBare(eq(!BARE));
        verify(view).setEnableOkButton(eq(ENABLE_BUTTON));
        verify(view).showDialog();
    }

    @Test
    public void testOnOkClickedInitWSRequestIsSuccessful() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                RequestCallback<Void> callback = (RequestCallback<Void>)arguments[4];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, (Void)null);
                return callback;
            }
        }).when(service).initWS(anyString(), anyString(), anyString(), anyBoolean(), (RequestCallback<Void>)anyObject());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<Project> callback = (AsyncCallback<Project>)arguments[0];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, project);
                return callback;
            }
        }).when(project).refreshProperties((AsyncCallback<Project>)anyObject());

        presenter.showDialog();
        presenter.onOkClicked();

        verify(view).isBare();
        verify(view).close();
        verify(service).initWS(eq(VFS_ID), eq(PROJECT_ID), eq(PROJECT_NAME), eq(BARE), (RequestCallback<Void>)anyObject());
        verify(constant).initSuccess();
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(eventBus).fireEvent((RefreshBrowserEvent)anyObject());
        verify(project).refreshProperties((AsyncCallback<Project>)anyObject());
    }

    @Test
    public void testOnOkClickedInitWSRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                RequestCallback<String> callback = (RequestCallback<String>)arguments[4];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).initWS(anyString(), anyString(), anyString(), anyBoolean(), (RequestCallback<Void>)anyObject());

        presenter.showDialog();
        presenter.onOkClicked();

        verify(view).isBare();
        verify(view).close();
        verify(service).initWS(eq(VFS_ID), eq(PROJECT_ID), eq(PROJECT_NAME), eq(BARE), (RequestCallback<Void>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(constant).initFailed();
    }

    @Test
    public void testOnCancelClicked() throws Exception {
        presenter.onCancelClicked();

        verify(view).close();
    }

    @Test
    public void testOnValueChangedEnableButton() throws Exception {
        String workDir = "workDir";
        when(view.getWorkDir()).thenReturn(workDir);

        presenter.onValueChanged();

        verify(view).getWorkDir();
        verify(view).setEnableOkButton(eq(ENABLE_BUTTON));
    }

    @Test
    public void testOnValueChangedDisableButton() throws Exception {
        when(view.getWorkDir()).thenReturn(EMPTY_TEXT);

        presenter.onValueChanged();

        verify(view).getWorkDir();
        verify(view).setEnableOkButton(eq(DISABLE_BUTTON));
    }
}