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
package com.codenvy.ide.ext.git.client.github.githubimport;

import com.codenvy.ide.api.user.User;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.git.client.BaseTest;
import com.codenvy.ide.ext.git.client.github.GitHubClientService;
import com.codenvy.ide.ext.git.client.github.load.ProjectData;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

/**
 * Testing {@link ImportPresenter} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class ImportPresenterTest extends BaseTest {
    public static final String USER_ID = "userID";
    @Mock
    private ImportView          view;
    @Mock
    private ProjectData         selectedRepository;
    @Mock
    private GitHubClientService gitHubService;
    @InjectMocks
    private ImportPresenter     presenter;

    @Test
    public void testShowDialog() throws Exception {
        User user = mock(User.class);
        when(user.getUserId()).thenReturn(USER_ID);

        presenter.showDialog(user);

        verify(gitHubService).getUserToken(eq(USER_ID), (AsyncRequestCallback<String>)anyObject());
        verify(eventBus, never()).fireEvent((ExceptionThrownEvent)anyObject());
        verify(console, never()).print(anyString());
    }

    @Test
    public void testShowDialogWhenExceptionHappened() throws Exception {
        User user = mock(User.class);
        when(user.getUserId()).thenReturn(USER_ID);
        doThrow(RequestException.class).when(gitHubService).getUserToken(anyString(), (AsyncRequestCallback<String>)anyObject());

        presenter.showDialog(user);

        verify(gitHubService).getUserToken(eq(USER_ID), (AsyncRequestCallback<String>)anyObject());
        verify(eventBus).fireEvent((ExceptionThrownEvent)anyObject());
        verify(console).print(anyString());
    }

    @Test
    @Ignore
    // Ignore this test because this method uses native method (Window.alert(constant.noIncorrectProjectNameMessage());)
    public void testOnFinishClickedWhenProjectNameIsEmpty() throws Exception {
        when(view.getProjectName()).thenReturn("");

        presenter.onRepositorySelected(selectedRepository);
        presenter.onFinishClicked();

        verify(resourceProvider, never()).createProject(anyString(), (JsonArray<Property>)anyObject(), (AsyncCallback<Project>)anyObject());
    }

    @Test
    @Ignore
    // Ignore this test because this method uses native method (Window.alert(constant.noIncorrectProjectNameMessage());)
    public void testOnFinishClickedWhenProjectNameHasIncorrectSymbol() throws Exception {
        when(view.getProjectName()).thenReturn("project!");

        presenter.onRepositorySelected(selectedRepository);
        presenter.onFinishClicked();

        verify(resourceProvider, never()).createProject(anyString(), (JsonArray<Property>)anyObject(), (AsyncCallback<Project>)anyObject());
    }

    @Test
    public void testOnFinishClicked() throws Exception {
        when(view.getProjectName()).thenReturn(PROJECT_NAME);
        when(selectedRepository.getRepositoryUrl()).thenReturn(REMOTE_URI);

        presenter.onRepositorySelected(selectedRepository);
        presenter.onFinishClicked();

        verify(resourceProvider).createProject(eq(PROJECT_NAME), (JsonArray<Property>)anyObject(), (AsyncCallback<Project>)anyObject());
    }

    @Test
    public void testOnCancelClicked() throws Exception {
        presenter.onCancelClicked();

        verify(view).close();
    }

    @Test
    public void testOnRepositorySelected() throws Exception {
        when(selectedRepository.getName()).thenReturn(REPOSITORY_NAME);

        presenter.onRepositorySelected(selectedRepository);

        verify(view).setProjectName(eq(REPOSITORY_NAME));
        verify(view).setEnableFinishButton(eq(ENABLE_BUTTON));
    }
}