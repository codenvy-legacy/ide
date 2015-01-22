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
import com.codenvy.ide.api.projectimporter.ImporterPagePresenter;
import com.codenvy.ide.api.projecttype.wizard.ImportProjectWizard;
import com.codenvy.ide.wizard.project.my_wizard.ProjectWizard;
import com.codenvy.ide.api.wizard.Wizard;
import com.codenvy.ide.api.wizard.WizardContext;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import javax.annotation.Nonnull;
import java.util.HashMap;

/**
 * @author Roman Nikitenko
 */
public class ZipImporterPagePresenter implements ImporterPagePresenter, ZipImporterPageView.ActionDelegate {

    private static final RegExp NAME_PATTERN = RegExp.compile("^[A-Za-z0-9_\\-\\.]*$");
    private static final RegExp URL_REGEX    = RegExp.compile("(https?|ftp)://(-\\.)?([^\\s/?\\.#-]+\\.?)+(/[^\\s]*)?");
    private static final RegExp WHITESPACE   = RegExp.compile("^\\s");
    private static final RegExp END_URL      = RegExp.compile(".zip$");

    private CoreLocalizationConstant locale;
    private ZipImporterPageView      view;
    private WizardContext            wizardContext;
    private Wizard.UpdateDelegate    updateDelegate;

    @Inject
    public ZipImporterPagePresenter(ZipImporterPageView view,
                                    CoreLocalizationConstant locale) {
        this.view = view;
        this.view.setDelegate(this);
        this.locale = locale;
    }

    @Nonnull
    @Override
    public String getId() {
        return "zip";
    }

    @Override
    public void disableInputs() {
        view.setInputsEnableState(false);
    }

    @Override
    public void enableInputs() {
        view.setInputsEnableState(true);
    }

    @Override
    public void setContext(@Nonnull WizardContext wizardContext) {
        this.wizardContext = wizardContext;
    }

    @Override
    public void setProjectWizardDelegate(@Nonnull Wizard.UpdateDelegate updateDelegate) {
        this.updateDelegate = updateDelegate;
    }

    @Override
    public void clear() {
        view.reset();
    }

    @Override
    public void projectNameChanged(@Nonnull String name) {
        if (name.isEmpty()) {
            wizardContext.removeData(ProjectWizard.PROJECT_NAME);
        } else {
            name = replaceSpaceToHyphen(name);
            if (NAME_PATTERN.test(name)) {
                wizardContext.putData(ProjectWizard.PROJECT_NAME, name);
                view.hideNameError();
            } else {
                wizardContext.removeData(ProjectWizard.PROJECT_NAME);
                view.showNameError();
            }
        }
        updateDelegate.updateControls();
    }

    private String replaceSpaceToHyphen(String projectName) {
        if (projectName.contains(" ")) {
            projectName  = projectName.replace(" ", "-");
            view.setProjectName(projectName);
        }
        return projectName;
    }

    @Override
    public void projectUrlChanged(@Nonnull String url) {
        if (!isUrlCorrect(url)) {
            wizardContext.removeData(ImportProjectWizard.PROJECT_URL);
        } else {
            wizardContext.putData(ImportProjectWizard.PROJECT_URL, url);
            String projectName = view.getProjectName();
            if (projectName.isEmpty()) {
                projectName = parseUri(url);
                view.setProjectName(projectName);
                projectNameChanged(projectName);
            }
        }
        updateDelegate.updateControls();
    }

    @Override
    public void projectDescriptionChanged(@Nonnull String projectDescriptionValue) {
        wizardContext.putData(ProjectWizard.PROJECT_DESCRIPTION, projectDescriptionValue);
    }

    @Override
    public void projectVisibilityChanged(boolean aPublic) {
        wizardContext.putData(ProjectWizard.PROJECT_VISIBILITY, aPublic);
    }

    @Override
    public void skipFirstLevelChanged(boolean isSkipFirstLevel) {
        ProjectImporterDescriptor projectImporter = wizardContext.getData(ImportProjectWizard.PROJECT_IMPORTER);
        if (projectImporter != null) {
            if (projectImporter.getAttributes() == null) {
                projectImporter.setAttributes(new HashMap<String, String>());
            }
            String skipFirstLevel = isSkipFirstLevel ? "true" : "false";
            projectImporter.getAttributes().put("skipFirstLevel", skipFirstLevel);
        }
    }

    @Override
    public void go(AcceptsOneWidget container) {
        clear();
        ProjectImporterDescriptor projectImporter = wizardContext.getData(ImportProjectWizard.PROJECT_IMPORTER);
        if (projectImporter != null) {
            view.setImporterDescription(projectImporter.getDescription());
        }

        view.setInputsEnableState(true);
        container.setWidget(view);
        view.focusInUrlInput();
    }

    /** Gets project name from uri. */
    private String parseUri(@Nonnull String uri) {
        String result;
        int indexStartProjectName = uri.lastIndexOf("/") + 1;
        int indexFinishProjectName = uri.indexOf(".", indexStartProjectName);
        if (indexStartProjectName != 0 && indexFinishProjectName != (-1)) {
            result = uri.substring(indexStartProjectName, indexFinishProjectName);
        } else if (indexStartProjectName != 0) {
            result = uri.substring(indexStartProjectName);
        } else {
            result = "";
        }
        return result;
    }

    /**
     * Validate url
     *
     * @param url
     *         url for validate
     * @return <code>true</code> if url is correct
     */
    private boolean isUrlCorrect(@Nonnull String url) {
        if (!END_URL.test(url)) {
            view.showUrlError(locale.importProjectMessageUrlInvalid());
            return false;
        }

        if (WHITESPACE.test(url)) {
            view.showUrlError(locale.importProjectMessageStartWithWhiteSpace());
            return false;
        }

        if (!URL_REGEX.test(url)) {
            view.showUrlError(locale.importProjectMessageUrlInvalid());
            return false;
        }

        view.hideUrlError();
        return true;
    }

}
