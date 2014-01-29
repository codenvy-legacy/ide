/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.rename;

import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test for {@link RenameResourcePresenter}.
 * 
 * @author Ann Shumilova
 */
@RunWith(MockitoJUnitRunner.class)
public class RenameResourceTest {
    public static final String      FOLDER_NAME  = "folder";
    public static final String      NEW_NAME     = "newName";
    public static final String      EMPTY        = "";
    public static final boolean     ENABLE_STATE = true;
    @Mock
    private RenameResourceView      view;
    @Mock
    private Project                 project;
    @Mock
    private Resource                resource;
    @Mock
    private ResourceProvider        resourceProvider;
    @Mock
    private NotificationManager     notificationManager;
    @Mock
    private EditorAgent             editorAgent;
    @Mock
    private EventBus                eventBus;
    private RenameResourcePresenter presenter;

    @Before
    public void setUp() {
        when(resource.getName()).thenReturn(FOLDER_NAME);
        when(resourceProvider.getActiveProject()).thenReturn(project);

        presenter = new RenameResourcePresenter(view, editorAgent, resourceProvider, notificationManager);
    }

    @Test
    public void testShowDialog() throws Exception {
        presenter.renameResource(resource);

        verify(view).setName(anyString());
        verify(view).setEnableRenameButton(eq(!ENABLE_STATE));
        verify(view).showDialog();
        verify(view).selectText(anyString());
    }

    @Test
    public void testCancelClicked() throws Exception {
        presenter.renameResource(resource);
        presenter.onCancelClicked();
        verify(view).close();
    }

    @Test
    public void testValueChanged() throws Exception {
        presenter.renameResource(resource);

        // Disable rename button, when new name is empty:
        when(view.getName()).thenReturn(EMPTY);
        presenter.onValueChanged();
        verify(view, atLeastOnce()).getName();
        verify(view, times(2)).setEnableRenameButton(eq(!ENABLE_STATE));

        // Disable rename button, when new name is the same as old one:
        when(view.getName()).thenReturn(FOLDER_NAME);
        presenter.onValueChanged();
        verify(view, atLeastOnce()).getName();
        verify(view, times(3)).setEnableRenameButton(eq(!ENABLE_STATE));

        // Enable rename button, when new name is not empty:
        when(view.getName()).thenReturn(NEW_NAME);
        presenter.onValueChanged();
        verify(view, atLeastOnce()).getName();
        verify(view).setEnableRenameButton(eq(ENABLE_STATE));
    }

    @Test
    public void testRenameClicked() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<Resource> callback = (AsyncCallback<Resource>)arguments[2];
                callback.onSuccess(resource);
                return callback;
            }
        }).when(project).rename((Resource)anyObject(), anyString(), (AsyncCallback<Resource>)anyObject());

        presenter.renameResource(resource);
        presenter.onRenameClicked();

        verify(view).getName();
        verify(resourceProvider.getActiveProject()).rename((Resource)anyObject(), anyString(), (AsyncCallback<Resource>)anyObject());

        verify(view).close();
    }

    @Test
    public void testRenameFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<Resource> callback = (AsyncCallback<Resource>)arguments[2];
                callback.onFailure(mock(Throwable.class));
                return callback;
            }
        }).when(project).rename((Resource)anyObject(), anyString(), (AsyncCallback<Resource>)anyObject());

        presenter.renameResource(resource);
        presenter.onRenameClicked();

        verify(view).getName();
        verify(resourceProvider.getActiveProject()).rename((Resource)anyObject(), anyString(), (AsyncCallback<Resource>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(view).close();
    }
}
