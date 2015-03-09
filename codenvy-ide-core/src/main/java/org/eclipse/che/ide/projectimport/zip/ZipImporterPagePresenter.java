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
import org.eclipse.che.api.project.shared.dto.NewProject;

import org.eclipse.che.ide.CoreLocalizationConstant;

import org.eclipse.che.ide.api.wizard.AbstractWizardPage;
import org.eclipse.che.ide.util.NameUtils;

import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * @author Roman Nikitenko
 */
public class ZipImporterPagePresenter extends AbstractWizardPage<ImportProject> implements ZipImporterPageView.ActionDelegate {

    private static final String PUBLIC_VISIBILITY           = "public";
    private static final String PRIVATE_VISIBILITY          = "private";
    private static final String SKIP_FIRST_LEVEL_PARAM_NAME = "skipFirstLevel";

    private static final RegExp URL_REGEX  = RegExp.compile("(https?|ftp)://(-\\.)?([^\\s/?\\.#-]+\\.?)+(/[^\\s]*)?");
    private static final RegExp WHITESPACE = RegExp.compile("^\\s");
    private static final RegExp END_URL    = RegExp.compile(".zip$");

    private CoreLocalizationConstant locale;
    private ZipImporterPageView      view;

    @Inject
    public ZipImporterPagePresenter(ZipImporterPageView view, CoreLocalizationConstant locale) {
        this.view = view;
        this.locale = locale;
        this.view.setDelegate(this);
    }

    @Override
    public void init(ImportProject dataObject) {
        super.init(dataObject);

        setImportParameterValue(SKIP_FIRST_LEVEL_PARAM_NAME, String.valueOf(true));
    }

    @Override
    public boolean isCompleted() {
        return isUrlCorrect(dataObject.getSource().getProject().getLocation());
    }

    @Override
    public void projectNameChanged(@Nonnull String name) {
        dataObject.getProject().setName(name);
        updateDelegate.updateControls();

        validateProjectName();
    }

    private void validateProjectName() {
        if (NameUtils.checkProjectName(view.getProjectName())) {
            view.hideNameError();
        } else {
            view.showNameError();
        }
    }

    @Override
    public void projectUrlChanged(@Nonnull String url) {
        dataObject.getSource().getProject().setLocation(url);
        isUrlCorrect(url);

        String projectName = view.getProjectName();
        if (projectName.isEmpty()) {
            projectName = extractProjectNameFromUri(url);

            dataObject.getProject().setName(projectName);
            view.setProjectName(projectName);
            validateProjectName();
        }

        updateDelegate.updateControls();
    }

    @Override
    public void projectDescriptionChanged(@Nonnull String projectDescription) {
        dataObject.getProject().setDescription(projectDescription);
        updateDelegate.updateControls();
    }

    @Override
    public void projectVisibilityChanged(boolean visible) {
        dataObject.getProject().setVisibility(visible ? PUBLIC_VISIBILITY : PRIVATE_VISIBILITY);
        updateDelegate.updateControls();
    }

    @Override
    public void skipFirstLevelChanged(boolean isSkipFirstLevel) {
        setImportParameterValue(SKIP_FIRST_LEVEL_PARAM_NAME, String.valueOf(isSkipFirstLevel));
        updateDelegate.updateControls();
    }

    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
        updateView();

        view.setInputsEnableState(true);
        view.focusInUrlInput();
    }

    /** Updates view from data-object. */
    private void updateView() {
        final NewProject project = dataObject.getProject();

        view.setProjectName(project.getName());
        view.setProjectDescription(project.getDescription());
        view.setVisibility(PUBLIC_VISIBILITY.equals(project.getVisibility()));
        view.setProjectUrl(dataObject.getSource().getProject().getLocation());

        final String value = getImportParameterValue(SKIP_FIRST_LEVEL_PARAM_NAME);
        if (value != null) {
            view.setSkipFirstLevel(Boolean.valueOf(value));
        }
    }

    @Nullable
    private String getImportParameterValue(String name) {
        Map<String, String> parameters = dataObject.getSource().getProject().getParameters();
        return parameters.get(name);
    }

    private void setImportParameterValue(String name, String value) {
        Map<String, String> parameters = dataObject.getSource().getProject().getParameters();
        parameters.put(name, value);
    }

    private String extractProjectNameFromUri(@Nonnull String uri) {
        final String result;
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
