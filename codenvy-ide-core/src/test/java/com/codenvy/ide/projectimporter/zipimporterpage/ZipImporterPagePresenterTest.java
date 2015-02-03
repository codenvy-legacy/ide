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
package com.codenvy.ide.projectimporter.zipimporterpage;

import com.codenvy.api.project.shared.dto.ProjectImporterDescriptor;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.projecttype.wizard.ImportProjectWizard;
import com.codenvy.ide.api.wizardOld.Wizard;
import com.codenvy.ide.api.wizardOld.WizardContext;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;

import static org.mockito.Matchers.anyObject;
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

    private WizardContext            wizardContext;
    private Wizard.UpdateDelegate    updateDelegate;
    @Mock
    private ZipImporterPageView      view;
    @Mock
    private CoreLocalizationConstant locale;
    @InjectMocks
    private ZipImporterPagePresenter presenter;

    @Before
    public void setUp() {
        wizardContext = mock(WizardContext.class);
        updateDelegate = mock(Wizard.UpdateDelegate.class);
        presenter.setContext(wizardContext);
        presenter.setProjectWizardDelegate(updateDelegate);
    }

    @Test
    public void testGo() {
        String importerDescription = "description";
        AcceptsOneWidget container = mock(AcceptsOneWidget.class);
        ProjectImporterDescriptor projectImporter = mock(ProjectImporterDescriptor.class);
        when(wizardContext.getData(ImportProjectWizard.PROJECT_IMPORTER)).thenReturn(projectImporter);
        when(projectImporter.getDescription()).thenReturn(importerDescription);

        presenter.go(container);

        verify(view).reset();
        verify(wizardContext, times(2)).getData(eq(ImportProjectWizard.PROJECT_IMPORTER));
        verify(view).setImporterDescription(eq(importerDescription));
        verify(view).setInputsEnableState(eq(true));
        verify(container).setWidget(eq(view));
        verify(view).focusInUrlInput();
    }

    @Test
    public void incorrectProjectUrlEnteredTest() {
        String incorrectUrl = "https//host.com/some/path/angularjs.zip";

        presenter.projectUrlChanged(incorrectUrl);

        verify(view).showUrlError(eq(locale.importProjectMessageUrlInvalid()));
        verify(wizardContext).removeData(eq(ImportProjectWizard.PROJECT_URL));
        verify(wizardContext, never()).putData((WizardContext.Key<String>)anyObject(), anyString());
        verify(view, never()).setProjectName(anyString());
        verify(updateDelegate).updateControls();
    }

    @Test
    public void projectUrlWithoutZipEnteredTest() {
        //url without .zip was entered
        String incorrectUrl = "https://host.com/some/path/angularjs.ip";

        presenter.projectUrlChanged(incorrectUrl);

        verify(view).showUrlError(eq(locale.importProjectMessageUrlInvalid()));
        verify(wizardContext).removeData(eq(ImportProjectWizard.PROJECT_URL));
        verify(wizardContext, never()).putData((WizardContext.Key<String>)anyObject(), anyString());
        verify(view, never()).setProjectName(anyString());
        verify(updateDelegate).updateControls();
    }

    @Test
    public void projectUrlStartWithWhiteSpaceEnteredTest() {
        String incorrectUrl = " https://host.com/some/path/angularjs.zip";

        presenter.projectUrlChanged(incorrectUrl);

        verify(view).showUrlError(eq(locale.importProjectMessageStartWithWhiteSpace()));
        verify(wizardContext).removeData(eq(ImportProjectWizard.PROJECT_URL));
        verify(wizardContext, never()).putData(eq(ImportProjectWizard.PROJECT_URL), anyString());
        verify(view, never()).setProjectName(anyString());
        verify(updateDelegate).updateControls();
    }

    @Test
    public void correctProjectUrlEnteredTest() {
        String correctUrl = "https://host.com/some/path/angularjs.zip";
        when(view.getProjectName()).thenReturn("");

        presenter.projectUrlChanged(correctUrl);

        verify(view, never()).showUrlError(anyString());
        verify(wizardContext).putData(eq(ImportProjectWizard.PROJECT_URL), eq(correctUrl));
        verify(view).hideUrlError();
        verify(view).setProjectName(anyString());
        verify(updateDelegate, times(2)).updateControls();
    }

    @Test
    public void correctProjectNameEnteredTest() {
        String correctName = "angularjs";

        presenter.projectNameChanged(correctName);

        verify(wizardContext).putData(eq(ImportProjectWizard.PROJECT_NAME), eq(correctName));
        verify(view).hideNameError();
        verify(view, never()).showNameError();
        verify(updateDelegate).updateControls();
    }

    @Test
    public void correctProjectNameWithPointEnteredTest() {
        String correctName = "Test.project..ForCodenvy";

        presenter.projectNameChanged(correctName);

        verify(wizardContext).putData(eq(ImportProjectWizard.PROJECT_NAME), eq(correctName));
        verify(view).hideNameError();
        verify(updateDelegate).updateControls();
    }

    @Test
    public void replaceSpaceToHyphenTest() {
        String namesWithSpace = "Test project For  Codenvy";
        String fixedName = "Test-project-For--Codenvy";
        presenter.projectNameChanged(namesWithSpace);

        verify(wizardContext).putData(eq(ImportProjectWizard.PROJECT_NAME), eq(fixedName));
        verify(view).hideNameError();
        verify(updateDelegate).updateControls();
    }

    @Test
    public void emptyProjectNameEnteredTest() {
        String emptyName = "";

        presenter.projectNameChanged(emptyName);

        verify(wizardContext, never()).putData(eq(ImportProjectWizard.PROJECT_NAME), anyString());
        verify(wizardContext).removeData(eq(ImportProjectWizard.PROJECT_NAME));
        verify(updateDelegate).updateControls();
    }

    @Test
    public void incorrectProjectNameEnteredTest() {
        String incorrectName = "angularjs+";

        presenter.projectNameChanged(incorrectName);

        verify(wizardContext, never()).putData(eq(ImportProjectWizard.PROJECT_NAME), anyString());
        verify(wizardContext).removeData(eq(ImportProjectWizard.PROJECT_NAME));
        verify(view).showNameError();
        verify(updateDelegate).updateControls();
    }

    @Test
    public void skipFirstLevelSelectedTest() {
        Map<String, String> map = mock(Map.class);
        ProjectImporterDescriptor projectImporter = mock(ProjectImporterDescriptor.class);
        when(wizardContext.getData(ImportProjectWizard.PROJECT_IMPORTER)).thenReturn(projectImporter);
        when(projectImporter.getAttributes()).thenReturn(map);

        presenter.skipFirstLevelChanged(true);

        verify(wizardContext).getData(eq(ImportProjectWizard.PROJECT_IMPORTER));
        verify(projectImporter, times(2)).getAttributes();
        verify(map).put(eq("skipFirstLevel"), eq("true"));
    }

    @Test
    public void projectDescriptionChangedTest() {
        String description = "description";
        presenter.projectDescriptionChanged(description);

        verify(wizardContext).putData(eq(ImportProjectWizard.PROJECT_DESCRIPTION), eq(description));
    }

    @Test
    public void projectVisibilityChangedTest() {
        presenter.projectVisibilityChanged(true);

        verify(wizardContext).putData(eq(ImportProjectWizard.PROJECT_VISIBILITY), eq(true));
    }

}
