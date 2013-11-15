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

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.user.User;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ext.git.client.BaseTest;
import com.codenvy.ide.ext.git.client.github.GitHubClientService;
import com.codenvy.ide.ext.git.client.github.load.ProjectData;
import com.codenvy.ide.ext.git.shared.GitHubRepository;
import com.codenvy.ide.ext.git.shared.RepoInfo;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonStringMap;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Property;
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

import static org.mockito.Mockito.*;

/**
 * Testing {@link ImportPresenter} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class ImportPresenterTest extends BaseTest {
    public static final String USER_ID = "userID";
    @Mock
    private ImportView          view;
    @Mock
    private ProjectData         selectedRepository;
    @Mock
    private GitHubClientService gitHubService;
    @Mock
    private User                user;
    @Mock
    private RepoInfo            repoInfo;
    @Mock
    private Throwable           throwable;
    private ImportPresenter     presenter;

    @Override
    public void disarm() {
        super.disarm();

        presenter = new ImportPresenter(view, gitHubService, eventBus, "restContext", constant, resourceProvider, console, service,
                                        notificationManager, dtoFactory);

        when(user.getUserId()).thenReturn(USER_ID);
        when(repoInfo.getRemoteUri()).thenReturn(REMOTE_URI);
        when(throwable.getMessage()).thenReturn(EMPTY_TEXT);
    }

    @Test
    public void testShowDialogWhenGetUserTokenIsSuccessful() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[1];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, (String)null);
                return callback;
            }
        }).when(gitHubService).getUserToken(anyString(), (AsyncRequestCallback<String>)anyObject());

        presenter.showDialog(user);

        verify(gitHubService).getUserToken(eq(USER_ID), (AsyncRequestCallback<String>)anyObject());
        verify(constant).loginOAuthLabel();
        verify(eventBus, never()).fireEvent((ExceptionThrownEvent)anyObject());
        verify(console, never()).print(anyString());
    }

    @Test
    public void testShowDialogWhenGetUserReposIsSuccessful() throws Exception {
        final JsonStringMap<JsonArray<GitHubRepository>> repository = JsonCollections.createStringMap();
        repository.put(USER_ID, JsonCollections.<GitHubRepository>createArray());

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[1];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, USER_ID);
                return callback;
            }
        }).when(gitHubService).getUserToken(anyString(), (AsyncRequestCallback<String>)anyObject());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<JsonStringMap<JsonArray<GitHubRepository>>> callback =
                        (AsyncRequestCallback<JsonStringMap<JsonArray<GitHubRepository>>>)arguments[0];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, repository);
                return callback;
            }
        }).when(gitHubService).getAllRepositories((AsyncRequestCallback<JsonStringMap<JsonArray<GitHubRepository>>>)anyObject());
        when(view.getAccountName()).thenReturn(USER_ID);

        presenter.showDialog(user);

        verify(gitHubService).getUserToken(eq(USER_ID), (AsyncRequestCallback<String>)anyObject());
        verify(view).setAccountNames((JsonArray<String>)anyObject());
        verify(view).setEnableFinishButton(eq(DISABLE_BUTTON));
        verify(view).showDialog();
        verify(view).setRepositories((JsonArray<ProjectData>)anyObject());
        verify(view).setProjectName(eq(EMPTY_TEXT));
        verify(eventBus, never()).fireEvent((ExceptionThrownEvent)anyObject());
        verify(console, never()).print(anyString());
    }

    @Test
    public void testShowDialogWhenGetUserReposIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[1];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, USER_ID);
                return callback;
            }
        }).when(gitHubService).getUserToken(anyString(), (AsyncRequestCallback<String>)anyObject());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<JsonStringMap<JsonArray<GitHubRepository>>> callback =
                        (AsyncRequestCallback<JsonStringMap<JsonArray<GitHubRepository>>>)arguments[0];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, throwable);
                return callback;
            }
        }).when(gitHubService).getAllRepositories((AsyncRequestCallback<JsonStringMap<JsonArray<GitHubRepository>>>)anyObject());

        presenter.showDialog(user);

        verify(gitHubService).getUserToken(eq(USER_ID), (AsyncRequestCallback<String>)anyObject());
        verify(eventBus).fireEvent((ExceptionThrownEvent)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
    }

    @Test
    public void testShowDialogWhenGetUserReposRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[1];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, USER_ID);
                return callback;
            }
        }).when(gitHubService).getUserToken(anyString(), (AsyncRequestCallback<String>)anyObject());
        doThrow(RequestException.class).when(gitHubService)
                .getAllRepositories((AsyncRequestCallback<JsonStringMap<JsonArray<GitHubRepository>>>)anyObject());

        presenter.showDialog(user);

        verify(gitHubService).getUserToken(eq(USER_ID), (AsyncRequestCallback<String>)anyObject());
        verify(eventBus).fireEvent((ExceptionThrownEvent)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
    }

    @Test
    public void testShowDialogWhenGetUserTokenIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[1];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, throwable);
                return callback;
            }
        }).when(gitHubService).getUserToken(anyString(), (AsyncRequestCallback<String>)anyObject());

        presenter.showDialog(user);

        verify(gitHubService).getUserToken(eq(USER_ID), (AsyncRequestCallback<String>)anyObject());
        verify(constant).loginOAuthLabel();
        verify(eventBus, never()).fireEvent((ExceptionThrownEvent)anyObject());
        verify(console, never()).print(anyString());
    }

    @Test
    public void testShowDialogWhenExceptionHappened() throws Exception {
        doThrow(RequestException.class).when(gitHubService).getUserToken(anyString(), (AsyncRequestCallback<String>)anyObject());

        presenter.showDialog(user);

        verify(gitHubService).getUserToken(eq(USER_ID), (AsyncRequestCallback<String>)anyObject());
        verify(eventBus).fireEvent((ExceptionThrownEvent)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
    }

    @Test
    public void testOnFinishClickedWhenProjectNameIsEmpty() throws Exception {
        when(view.getProjectName()).thenReturn("");

        presenter.onRepositorySelected(selectedRepository);
        presenter.onFinishClicked();

        verify(resourceProvider, never()).createProject(anyString(), (JsonArray<Property>)anyObject(), (AsyncCallback<Project>)anyObject());
        verify(constant).noIncorrectProjectNameMessage();
    }

    @Test
    public void testOnFinishClickedWhenProjectNameHasIncorrectSymbol() throws Exception {
        when(view.getProjectName()).thenReturn("project!");

        presenter.onRepositorySelected(selectedRepository);
        presenter.onFinishClicked();

        verify(resourceProvider, never()).createProject(anyString(), (JsonArray<Property>)anyObject(), (AsyncCallback<Project>)anyObject());
        verify(constant).noIncorrectProjectNameMessage();
    }

    @Test
    public void testOnFinishClickedWhenCloneRepositoryWSIsSuccessful() throws Exception {
        when(view.getProjectName()).thenReturn(PROJECT_NAME);
        when(selectedRepository.getRepositoryUrl()).thenReturn(REMOTE_URI);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<Project> callback = (AsyncCallback<Project>)arguments[2];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, project);
                return callback;
            }
        }).when(resourceProvider).createProject(anyString(), (JsonArray<Property>)anyObject(), (AsyncCallback<Project>)anyObject());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                RequestCallback<String> callback = (RequestCallback<String>)arguments[4];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, repoInfo);
                return callback;
            }
        }).when(service).cloneRepositoryWS(anyString(), (Project)anyObject(), anyString(), anyString(),
                                           (RequestCallback<String>)anyObject());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<Project> callback = (AsyncCallback<Project>)arguments[1];
                callback.onSuccess(project);
                return callback;
            }
        }).when(resourceProvider).getProject(anyString(), (AsyncCallback<Project>)anyObject());

        presenter.onRepositorySelected(selectedRepository);
        presenter.onFinishClicked();

        verify(resourceProvider).createProject(eq(PROJECT_NAME), (JsonArray<Property>)anyObject(), (AsyncCallback<Project>)anyObject());
        verify(resourceProvider).getProject(anyString(), (AsyncCallback<Project>)anyObject());
        verify(service)
                .cloneRepositoryWS(eq(VFS_ID), eq(project), eq(REMOTE_URI), eq(PROJECT_NAME), (RequestCallback<String>)anyObject());
        verify(service, never())
                .cloneRepository(eq(VFS_ID), eq(project), eq(REMOTE_URI), eq(PROJECT_NAME), (AsyncRequestCallback<String>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(constant).cloneSuccess(eq(REMOTE_URI));
        verify(view).close();
    }

    @Test
    public void testOnFinishClickedWhenCloneRepositoryWSIsFailed() throws Exception {
        when(view.getProjectName()).thenReturn(PROJECT_NAME);
        when(selectedRepository.getRepositoryUrl()).thenReturn(REMOTE_URI);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<Project> callback = (AsyncCallback<Project>)arguments[2];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, project);
                return callback;
            }
        }).when(resourceProvider).createProject(anyString(), (JsonArray<Property>)anyObject(), (AsyncCallback<Project>)anyObject());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                RequestCallback<String> callback = (RequestCallback<String>)arguments[4];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, throwable);
                return callback;
            }
        }).when(service).cloneRepositoryWS(anyString(), (Project)anyObject(), anyString(), anyString(),
                                           (RequestCallback<String>)anyObject());

        presenter.onRepositorySelected(selectedRepository);
        presenter.onFinishClicked();

        verify(resourceProvider).createProject(eq(PROJECT_NAME), (JsonArray<Property>)anyObject(), (AsyncCallback<Project>)anyObject());
        verify(resourceProvider).delete(eq(project), (AsyncCallback<String>)anyObject());
        verify(service)
                .cloneRepositoryWS(eq(VFS_ID), eq(project), eq(REMOTE_URI), eq(PROJECT_NAME), (RequestCallback<String>)anyObject());
        verify(service, never())
                .cloneRepository(eq(VFS_ID), eq(project), eq(REMOTE_URI), eq(PROJECT_NAME), (AsyncRequestCallback<String>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(constant).cloneFailed(anyString());
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(view).close();
    }

    @Test
    public void testOnFinishClickedWhenCloneRepositoryIsSuccessful() throws Exception {
        final RepoInfo repoInfo = mock(RepoInfo.class);
        when(repoInfo.getRemoteUri()).thenReturn(REMOTE_URI);
        when(view.getProjectName()).thenReturn(PROJECT_NAME);
        when(selectedRepository.getRepositoryUrl()).thenReturn(REMOTE_URI);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<Project> callback = (AsyncCallback<Project>)arguments[2];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, project);
                return callback;
            }
        }).when(resourceProvider).createProject(anyString(), (JsonArray<Property>)anyObject(), (AsyncCallback<Project>)anyObject());
        doThrow(WebSocketException.class).when(service).cloneRepositoryWS(anyString(), (Project)anyObject(), anyString(), anyString(),
                                                                          (RequestCallback<String>)anyObject());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[4];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, repoInfo);
                return callback;
            }
        }).when(service).cloneRepository(anyString(), (Project)anyObject(), anyString(), anyString(),
                                         (AsyncRequestCallback<String>)anyObject());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<Project> callback = (AsyncCallback<Project>)arguments[1];
                callback.onSuccess(project);
                return callback;
            }
        }).when(resourceProvider).getProject(anyString(), (AsyncCallback<Project>)anyObject());

        presenter.onRepositorySelected(selectedRepository);
        presenter.onFinishClicked();

        verify(resourceProvider).createProject(eq(PROJECT_NAME), (JsonArray<Property>)anyObject(), (AsyncCallback<Project>)anyObject());
        verify(resourceProvider).getProject(anyString(), (AsyncCallback<Project>)anyObject());
        verify(service)
                .cloneRepositoryWS(eq(VFS_ID), eq(project), eq(REMOTE_URI), eq(PROJECT_NAME), (RequestCallback<String>)anyObject());
        verify(service).cloneRepository(eq(VFS_ID), eq(project), eq(REMOTE_URI), eq(PROJECT_NAME),
                                        (AsyncRequestCallback<String>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(constant).cloneSuccess(eq(REMOTE_URI));
        verify(view).close();
    }

    @Test
    public void testOnFinishClickedWhenCloneRepositoryIsFailed() throws Exception {
        when(view.getProjectName()).thenReturn(PROJECT_NAME);
        when(selectedRepository.getRepositoryUrl()).thenReturn(REMOTE_URI);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<Project> callback = (AsyncCallback<Project>)arguments[2];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, project);
                return callback;
            }
        }).when(resourceProvider).createProject(anyString(), (JsonArray<Property>)anyObject(), (AsyncCallback<Project>)anyObject());
        doThrow(WebSocketException.class).when(service).cloneRepositoryWS(anyString(), (Project)anyObject(), anyString(), anyString(),
                                                                          (RequestCallback<String>)anyObject());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[4];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, throwable);
                return callback;
            }
        }).when(service).cloneRepository(anyString(), (Project)anyObject(), anyString(), anyString(),
                                         (AsyncRequestCallback<String>)anyObject());

        presenter.onRepositorySelected(selectedRepository);
        presenter.onFinishClicked();

        verify(resourceProvider).createProject(eq(PROJECT_NAME), (JsonArray<Property>)anyObject(), (AsyncCallback<Project>)anyObject());
        verify(resourceProvider).delete(eq(project), (AsyncCallback<String>)anyObject());
        verify(service)
                .cloneRepositoryWS(eq(VFS_ID), eq(project), eq(REMOTE_URI), eq(PROJECT_NAME), (RequestCallback<String>)anyObject());
        verify(service).cloneRepository(eq(VFS_ID), eq(project), eq(REMOTE_URI), eq(PROJECT_NAME),
                                        (AsyncRequestCallback<String>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(constant).cloneFailed(anyString());
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(view).close();
    }

    @Test
    public void testOnFinishClickedWhenCloneRepositoryIsRequestFailed() throws Exception {
        when(view.getProjectName()).thenReturn(PROJECT_NAME);
        when(selectedRepository.getRepositoryUrl()).thenReturn(REMOTE_URI);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<Project> callback = (AsyncCallback<Project>)arguments[2];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, project);
                return callback;
            }
        }).when(resourceProvider).createProject(anyString(), (JsonArray<Property>)anyObject(), (AsyncCallback<Project>)anyObject());
        doThrow(WebSocketException.class).when(service).cloneRepositoryWS(anyString(), (Project)anyObject(), anyString(), anyString(),
                                                                          (RequestCallback<String>)anyObject());
        doThrow(RequestException.class).when(service).cloneRepository(anyString(), (Project)anyObject(), anyString(), anyString(),
                                                                      (AsyncRequestCallback<String>)anyObject());

        presenter.onRepositorySelected(selectedRepository);
        presenter.onFinishClicked();

        verify(resourceProvider).createProject(eq(PROJECT_NAME), (JsonArray<Property>)anyObject(), (AsyncCallback<Project>)anyObject());
        verify(resourceProvider).delete(eq(project), (AsyncCallback<String>)anyObject());
        verify(service)
                .cloneRepositoryWS(eq(VFS_ID), eq(project), eq(REMOTE_URI), eq(PROJECT_NAME), (RequestCallback<String>)anyObject());
        verify(service).cloneRepository(eq(VFS_ID), eq(project), eq(REMOTE_URI), eq(PROJECT_NAME),
                                        (AsyncRequestCallback<String>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(constant).cloneFailed(anyString());
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(view).close();
    }

    @Test
    public void testOnFinishClickedWhenCreateProjectIsFailed() throws Exception {
        when(view.getProjectName()).thenReturn(PROJECT_NAME);
        when(selectedRepository.getRepositoryUrl()).thenReturn(REMOTE_URI);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<Project> callback = (AsyncCallback<Project>)arguments[2];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, throwable);
                return callback;
            }
        }).when(resourceProvider).createProject(anyString(), (JsonArray<Property>)anyObject(), (AsyncCallback<Project>)anyObject());

        presenter.onRepositorySelected(selectedRepository);
        presenter.onFinishClicked();

        verify(resourceProvider).createProject(eq(PROJECT_NAME), (JsonArray<Property>)anyObject(), (AsyncCallback<Project>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
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