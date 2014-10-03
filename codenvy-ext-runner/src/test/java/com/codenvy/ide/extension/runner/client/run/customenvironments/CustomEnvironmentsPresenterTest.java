/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.extension.runner.client.run.customenvironments;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.extension.runner.client.BaseTest;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link CustomEnvironmentsPresenter} functionality.
 *
 * @author Artem Zatsarynnyy
 */
public class CustomEnvironmentsPresenterTest extends BaseTest {
    private static final String PROJECT_PATH    = "/project";
    private static final String ENV_FOLDER_PATH = ".codenvy/environments";
    @Mock
    private CustomEnvironmentsView      view;
    @Mock
    private EventBus                    eventBus;
    @Mock
    private EnvironmentActionsManager   environmentActionsManager;
    @Mock
    private ProjectServiceClient        projectServiceClient;
    @Mock
    private ProjectDescriptor           currentProjectDescriptor;
    private Array<ItemReference>        scriptsArray;
    private CustomEnvironmentsPresenter presenter;

    @Before
    @Override
    public void setUp() {
        super.setUp();

        when(currentProject.getProjectDescription()).thenReturn(currentProjectDescriptor);
        when(currentProjectDescriptor.getPath()).thenReturn(PROJECT_PATH);
        scriptsArray = Collections.createArray();

        presenter = new CustomEnvironmentsPresenter(ENV_FOLDER_PATH, view, eventBus, appContext, environmentActionsManager,
                                                    projectServiceClient, dtoUnmarshallerFactory, notificationManager, constant);
    }

    @Test
    public void shouldShowDialog() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<Array<CustomEnvironment>> callback = (AsyncCallback<Array<CustomEnvironment>>)arguments[1];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, scriptsArray);
                return callback;
            }
        }).when(environmentActionsManager)
          .requestCustomEnvironmentsForProject(eq(currentProjectDescriptor), Matchers.<AsyncCallback<Array<CustomEnvironment>>>anyObject());

        presenter.showDialog();

        verify(view).showDialog();
        verify(environmentActionsManager).requestCustomEnvironmentsForProject(Matchers.<ProjectDescriptor>anyObject(),
                                                                              Matchers.<AsyncCallback<Array<CustomEnvironment>>>anyObject
                                                                                      ());
    }

    @Test
    public void shouldCloseDialog() throws Exception {
        presenter.onCloseClicked();

        verify(view).closeDialog();
    }

    @Test
    public void shouldEnableEditAndRemoveButtonsOnSelectingImage() throws Exception {
        presenter.onEnvironmentSelected(mock(CustomEnvironment.class));

        verify(view).setEditButtonEnabled(eq(true));
        verify(view).setRemoveButtonEnabled(eq(true));
    }

    @Test
    public void shouldCloseDialogOnEditClicked() throws Exception {
        presenter.onEnvironmentSelected(mock(CustomEnvironment.class));
        presenter.onEditClicked();

        verify(view).closeDialog();
    }
}
