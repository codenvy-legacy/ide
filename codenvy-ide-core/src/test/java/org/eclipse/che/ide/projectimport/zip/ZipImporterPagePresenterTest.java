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
package org.eclipse.che.ide.projectimport.zip;

import org.eclipse.che.api.project.shared.dto.ImportProject;
import org.eclipse.che.api.project.shared.dto.ImportSourceDescriptor;
import org.eclipse.che.api.project.shared.dto.NewProject;
import org.eclipse.che.api.project.shared.dto.Source;
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.api.wizard.Wizard;

import com.google.gwt.user.client.ui.AcceptsOneWidget;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link ZipImporterPagePresenter} functionality.
 *
 * @author Roman Nikitenko
 */
@RunWith(MockitoJUnitRunner.class)
public class ZipImporterPagePresenterTest {
    private static final String SKIP_FIRST_LEVEL_PARAM_NAME = "skipFirstLevel";

    @Mock
    private ZipImporterPageView      view;
    @Mock
    private CoreLocalizationConstant locale;
    @Mock
    private ImportProject            dataObject;
    @Mock
    private ImportSourceDescriptor   importSourceDescriptor;
    @Mock
    private NewProject               newProject;
    @Mock
    private Wizard.UpdateDelegate    delegate;
    @Mock
    private Map<String, String>      parameters;
    @InjectMocks
    private ZipImporterPagePresenter presenter;

    @Before
    public void setUp() {
        Source source = mock(Source.class);
        when(importSourceDescriptor.getParameters()).thenReturn(parameters);
        when(source.getProject()).thenReturn(importSourceDescriptor);
        when(dataObject.getSource()).thenReturn(source);
        when(dataObject.getProject()).thenReturn(newProject);

        presenter.setUpdateDelegate(delegate);
        presenter.init(dataObject);
    }

    @Test
    public void shouldSkipFirstLevelByDefault() {
        verify(parameters).put(SKIP_FIRST_LEVEL_PARAM_NAME, "true");
    }

    @Test
    public void testGo() {
        AcceptsOneWidget container = mock(AcceptsOneWidget.class);
        when(parameters.get(SKIP_FIRST_LEVEL_PARAM_NAME)).thenReturn("true");

        presenter.go(container);

        verify(container).setWidget(eq(view));
        verify(view).setProjectName(anyString());
        verify(view).setProjectDescription(anyString());
        verify(view).setVisibility(anyBoolean());
        verify(view).setProjectUrl(anyString());
        verify(view).setSkipFirstLevel(anyBoolean());
        verify(view).setInputsEnableState(eq(true));
        verify(view).focusInUrlInput();
    }

    @Test
    public void incorrectProjectUrlEnteredTest() {
        String incorrectUrl = "https//host.com/some/path/angularjs.zip";
        when(view.getProjectName()).thenReturn("");
        when(view.getProjectName()).thenReturn("angularjs");

        presenter.projectUrlChanged(incorrectUrl);

        verify(view).showUrlError(anyString());
        verify(delegate).updateControls();
    }

    @Test
    public void projectUrlWithoutZipEnteredTest() {
        //url without .zip was entered
        String incorrectUrl = "https://host.com/some/path/angularjs.ip";
        when(view.getProjectName()).thenReturn("");

        presenter.projectUrlChanged(incorrectUrl);

        verify(view).showUrlError(anyString());
        verify(delegate).updateControls();
    }

    @Test
    public void projectUrlStartWithWhiteSpaceEnteredTest() {
        String incorrectUrl = " https://host.com/some/path/angularjs.zip";
        when(view.getProjectName()).thenReturn("name");

        presenter.projectUrlChanged(incorrectUrl);

        verify(view).showUrlError(eq(locale.importProjectMessageStartWithWhiteSpace()));
        verify(delegate).updateControls();
    }

    @Test
    public void correctProjectUrlEnteredTest() {
        String correctUrl = "https://host.com/some/path/angularjs.zip";
        when(view.getProjectName()).thenReturn("", "angularjs");

        presenter.projectUrlChanged(correctUrl);

        verify(view, never()).showUrlError(anyString());
        verify(importSourceDescriptor).setLocation(eq(correctUrl));
        verify(view).hideNameError();
        verify(view).setProjectName(anyString());
        verify(delegate).updateControls();
    }

    @Test
    public void correctProjectNameEnteredTest() {
        String correctName = "angularjs";
        when(view.getProjectName()).thenReturn(correctName);

        presenter.projectNameChanged(correctName);

        verify(newProject).setName(eq(correctName));
        verify(view).hideNameError();
        verify(view, never()).showNameError();
        verify(delegate).updateControls();
    }

    @Test
    public void emptyProjectNameEnteredTest() {
        String emptyName = "";
        when(view.getProjectName()).thenReturn(emptyName);

        presenter.projectNameChanged(emptyName);

        verify(newProject).setName(anyString());
        verify(view).showNameError();
        verify(delegate).updateControls();
    }

    @Test
    public void incorrectProjectNameEnteredTest() {
        String incorrectName = "angularjs+";
        when(view.getProjectName()).thenReturn(incorrectName);

        presenter.projectNameChanged(incorrectName);

        verify(newProject).setName(anyString());
        verify(view).showNameError();
        verify(delegate).updateControls();
    }

    @Test
    public void skipFirstLevelSelectedTest() {
        presenter.skipFirstLevelChanged(true);

        verify(parameters, times(2)).put(SKIP_FIRST_LEVEL_PARAM_NAME, "true");
        verify(delegate).updateControls();
    }

    @Test
    public void projectDescriptionChangedTest() {
        String description = "description";
        presenter.projectDescriptionChanged(description);

        verify(newProject).setDescription(eq(description));
        verify(delegate).updateControls();
    }

    @Test
    public void projectVisibilityChangedTest() {
        presenter.projectVisibilityChanged(true);

        verify(newProject).setVisibility(eq("public"));
        verify(delegate).updateControls();
    }
}
