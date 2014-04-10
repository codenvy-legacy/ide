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

package com.codenvy.ide.importproject;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ImportSourceDescriptor;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.rest.AsyncRequestCallback;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;

import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link com.codenvy.ide.importproject.ImportProjectPresenter} functionality.
 *
 * @author Roman Nikitenko.
 */
@RunWith(MockitoJUnitRunner.class)
public class ImportProjectPresenterTest {

    @Mock
    private ImportProjectView view;

    @Mock
    private ProjectServiceClient projectServiceClient;

    @Mock
    private DtoFactory dtoFactory;

    @InjectMocks
    private ImportProjectPresenter presenter;

    @Test
    public void showDialogShouldBeExecuted() {

        presenter.showDialog();

        verify(view).setUri("");
        verify(view).setProjectName("");
        verify(view).setImporters((java.util.List<String>)anyObject());
        verify(view).setEnabledImportButton(eq(false));
        verify(view).showDialog();
    }

    @Test
    public void onCancelClickedShouldBeExecuted() {
        presenter.showDialog();

        presenter.onCancelClicked();

        verify(view).close();
    }

    @Test
    public void onImportClickedShouldBeExecuted() {
        view.showDialog();
        when(view.getUri()).thenReturn("https://github.com/codenvy/hello.git");
        when(view.getImporter()).thenReturn("git");
        when(view.getProjectName()).thenReturn("ide");
        ImportSourceDescriptor importSourceDescriptor = mock(ImportSourceDescriptor.class);
        when(dtoFactory.createDto(ImportSourceDescriptor.class)).thenReturn(importSourceDescriptor);
        when(importSourceDescriptor.withType("git")).thenReturn(importSourceDescriptor);
        when(importSourceDescriptor.withLocation("https://github.com/codenvy/hello.git")).thenReturn(importSourceDescriptor);

        presenter.onImportClicked();

        verify(view).getUri();
        verify(view).getImporter();
        verify(view).getProjectName();
        verify(dtoFactory).createDto(ImportSourceDescriptor.class);
        verify(importSourceDescriptor).withType(anyString());
        verify(importSourceDescriptor).withLocation(anyString());
        verify(projectServiceClient)
                .importProject(anyString(), (ImportSourceDescriptor)anyObject(), (AsyncRequestCallback<ProjectDescriptor>)anyObject());
    }
}
