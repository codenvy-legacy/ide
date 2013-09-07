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
package com.codenvy.ide.ext.git.client.clone;

import com.codenvy.ide.ext.git.client.BaseTest;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Property;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static com.codenvy.ide.ext.git.client.clone.CloneRepositoryPresenter.DEFAULT_REPO_NAME;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link CloneRepositoryPresenter} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class CloneRepositoryPresenterTest extends BaseTest {
    @Mock
    private CloneRepositoryView      view;
    @InjectMocks
    private CloneRepositoryPresenter presenter;

    @Test
    public void testOnCloneClicked() throws Exception {
        when(view.getProjectName()).thenReturn(PROJECT_NAME);
        when(view.getRemoteName()).thenReturn(REMOTE_NAME);
        when(view.getRemoteUri()).thenReturn(REMOTE_URI);

        presenter.onCloneClicked();

        verify(view).getProjectName();
        verify(view).getRemoteName();
        verify(view).getRemoteUri();

        verify(resourceProvider).createProject(eq(PROJECT_NAME), (JsonArray<Property>)anyObject(), (AsyncCallback<Project>)anyObject());
    }

    @Test
    public void testOnCancelClicked() throws Exception {
        presenter.onCancelClicked();

        verify(view).close();
    }

    @Test
    public void testOnValueChanged() throws Exception {
        when(view.getRemoteUri()).thenReturn(REMOTE_URI);

        presenter.onValueChanged();

        verify(view).setProjectName(eq(PROJECT_NAME));
        verify(view).setEnableCloneButton(eq(ENABLE_BUTTON));
    }

    @Test
    public void testShowDialog() throws Exception {
        presenter.showDialog();

        verify(view).setProjectName(eq(EMPTY_TEXT));
        verify(view).setRemoteUri(eq(EMPTY_TEXT));
        verify(view).setRemoteName(eq(DEFAULT_REPO_NAME));
        verify(view).focusInRemoteUrlField();
        verify(view).setEnableCloneButton(eq(!ENABLE_BUTTON));
        verify(view).showDialog();
    }
}