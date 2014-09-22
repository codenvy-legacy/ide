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
package com.codenvy.ide.extension.runner.client.run.customimages;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.api.event.FileEvent;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.extension.runner.client.BaseTest;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
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
 * Testing {@link EditImagesPresenter} functionality.
 *
 * @author Artem Zatsarynnyy
 */
public class EditImagesPresenterTest extends BaseTest {
    @Mock
    private EditImagesView       view;
    @Mock
    private EventBus             eventBus;
    @Mock
    private ImageActionManager   imageActionManager;
    @Mock
    private ProjectServiceClient projectServiceClient;
    @Mock
    private ProjectDescriptor    currentProjectDescriptor;
    @InjectMocks
    private EditImagesPresenter  presenter;
    private Array<ItemReference> scriptsArray;

    @Before
    @Override
    public void setUp() {
        super.setUp();

        when(currentProject.getProjectDescription()).thenReturn(currentProjectDescriptor);
        scriptsArray = Collections.createArray();
    }

    @Test
    public void shouldShowDialog() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<Array<ItemReference>> callback = (AsyncCallback<Array<ItemReference>>)arguments[1];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, scriptsArray);
                return callback;
            }
        }).when(imageActionManager)
          .retrieveCustomImages(eq(currentProjectDescriptor), Matchers.<AsyncCallback<Array<ItemReference>>>anyObject());

        presenter.showDialog();

        verify(view).showDialog();
        verify(imageActionManager).retrieveCustomImages(Matchers.<ProjectDescriptor>anyObject(),
                                                        Matchers.<AsyncCallback<Array<ItemReference>>>anyObject());
    }

    @Test
    public void shouldCloseDialog() throws Exception {
        presenter.onCloseClicked();

        verify(view).closeDialog();
    }

    @Test
    public void shouldEnableEditAndRemoveButtonsOnSelectingImage() throws Exception {
        presenter.onImageSelected(mock(ItemReference.class));

        verify(view).setEditButtonEnabled(eq(true));
        verify(view).setRemoveButtonEnabled(eq(true));
    }

    @Test
    public void shouldFireEventAndCloseDialogOnEditClicked() throws Exception {
        presenter.onEditClicked();

        verify(eventBus).fireEvent(Matchers.<FileEvent>anyObject());
        verify(view).closeDialog();
    }
}
