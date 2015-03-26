/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.upload.file;

import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.api.event.RefreshProjectTreeEvent;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.project.tree.generic.FolderNode;
import org.eclipse.che.ide.api.selection.Selection;
import org.eclipse.che.ide.api.selection.SelectionAgent;

import com.google.gwt.user.client.ui.FormPanel;
import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link UploadFilePresenter} functionality.
 *
 * @author Roman Nikitenko.
 */
@RunWith(MockitoJUnitRunner.class)
public class UploadFilePresenterTest {

    @Mock
    private UploadFileView view;

    @Mock
    private AppContext appContext;

    @Mock
    private EventBus eventBus;

    @Mock
    private SelectionAgent selectionAgent;

    @Mock
    private NotificationManager notificationManager;

    @InjectMocks
    private UploadFilePresenter presenter;

    @Before
    public void setUp() {
        CurrentProject project = mock(CurrentProject.class);
        when(project.getProjectDescription()).thenReturn(mock(ProjectDescriptor.class));
        when(project.getRootProject()).thenReturn(mock(ProjectDescriptor.class));
        when(appContext.getCurrentProject()).thenReturn(project);
    }

    @Test
    public void showDialogShouldBeExecuted() {
        presenter.showDialog();

        verify(view).showDialog();
    }

    @Test
    public void onCancelClickedShouldBeExecuted() {
        presenter.onCancelClicked();

        verify(view).closeDialog();
    }

    @Test
    public void onUploadClickedShouldBeExecuted() {
        Selection select = mock(Selection.class);
        FolderNode item = mock(FolderNode.class);
        when(selectionAgent.getSelection()).thenReturn(select);
        when(select.getFirstElement()).thenReturn(item);

        presenter.onUploadClicked();

        verify(view).setEncoding(eq(FormPanel.ENCODING_MULTIPART));
        verify(view).setAction((String)anyObject());
        verify(view).submit();
    }

    @Test
    public void onFileNameChangedShouldBeExecuted() {
        when(view.getFileName()).thenReturn("fileName");

        presenter.onFileNameChanged();

        verify(view).getFileName();
        verify(view).setEnabledUploadButton(eq(true));
    }

    @Test
    public void onSubmitCompleteWhenError() {
        String error = "Error";
        presenter.onSubmitComplete(error);

        verify(view).closeDialog();
        verify(notificationManager).showError(eq(error));
        verify(eventBus).fireEvent((RefreshProjectTreeEvent)anyObject());
    }

    @Test
    public void onSubmitCompleteWhenOverwriteFileNotSelected() {
        Selection select = mock(Selection.class);
        FolderNode item = mock(FolderNode.class);
        when(selectionAgent.getSelection()).thenReturn(select);
        when(select.getFirstElement()).thenReturn(item);
        when(view.isOverwriteFileSelected()).thenReturn(false);
        when(view.getFileName()).thenReturn("fileName");

        presenter.onSubmitComplete("");

        verify(view).closeDialog();
        verify(notificationManager, never()).showError(anyString());
        verify(eventBus).fireEvent(Matchers.<RefreshProjectTreeEvent>anyObject());
    }

    @Test
    public void onSubmitCompleteWhenOverwriteFileSelected() {
        Selection select = mock(Selection.class);
        FolderNode item = mock(FolderNode.class);
        when(selectionAgent.getSelection()).thenReturn(select);
        when(select.getFirstElement()).thenReturn(item);
        when(view.isOverwriteFileSelected()).thenReturn(true);
        when(view.getFileName()).thenReturn("fileName");

        presenter.onSubmitComplete("");

        verify(view).closeDialog();
        verify(notificationManager, never()).showError(anyString());
        verify(eventBus, times(2)).fireEvent((Event)anyObject());
    }
}
