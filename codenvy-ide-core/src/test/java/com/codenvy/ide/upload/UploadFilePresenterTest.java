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
package com.codenvy.ide.upload;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.resources.model.Folder;
import com.codenvy.ide.resources.model.Project;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FormPanel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Roman Nikitenko.
 */
@RunWith(MockitoJUnitRunner.class)
public class UploadFilePresenterTest {

    @Mock
    private UploadFileView view;

    @Mock
    private ResourceProvider resourceProvider;

    @Mock
    private SelectionAgent selectionAgent;

    @InjectMocks
    private UploadFilePresenter presenter;

    @Test
    public void showDialogShouldBeExecuted() {

        presenter.showDialog();

        verify(view).showDialog();
    }

    @Test
    public void onCancelClickedShouldBeExecuted() {

        presenter.onCancelClicked();

        verify(view).close();
    }

    @Test
    public void onUploadClickedShouldBeExecuted() {
        Selection select = mock(Selection.class);
        Folder folder = mock(Folder.class);
        when(selectionAgent.getSelection()).thenReturn(select);
        when(select.getFirstElement()).thenReturn(folder);

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
    public void onSubmitCompleteShouldBeExecuted() {
        Project project = mock(Project.class);
        when(resourceProvider.getActiveProject()).thenReturn(project);

        presenter.onSubmitComplete("Result");

        verify(view).close();
        verify(resourceProvider).getActiveProject();
        verify(project).refreshChildren((Folder)anyObject(), (AsyncCallback<Folder>)anyObject());
    }
}
