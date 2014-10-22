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
package com.codenvy.ide.projectimporter.importerpage;

import com.codenvy.api.project.shared.dto.ProjectImporterDescriptor;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.projectimporter.ImporterPagePresenter;
import com.codenvy.ide.api.projecttype.wizard.ImportProjectWizard;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizard;
import com.codenvy.ide.api.wizard.Wizard;
import com.codenvy.ide.api.wizard.WizardContext;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

/**
 * @author Roman Nikitenko
 */
public class ZipImporterPagePresenter implements ImporterPagePresenter, ZipImporterPageView.ActionDelegate{

    private static final RegExp NAME_PATTERN    = RegExp.compile("^[A-Za-z0-9_-]*$");
    // An alternative scp-like syntax: [user@]host.xz:path/to/repo.git/
    private static final RegExp SCP_LIKE_SYNTAX = RegExp.compile("([A-Za-z0-9_\\-]+\\.[A-Za-z0-9_\\-:]+)+:");
    // the transport protocol
    private static final RegExp PROTOCOL        = RegExp.compile("((http|https|git|ssh|ftp|ftps)://)");
    // the address of the remote server between // and /
    private static final RegExp HOST1           = RegExp.compile("//([A-Za-z0-9_\\-]+\\.[A-Za-z0-9_\\-:]+)+/");
    // the address of the remote server between @ and : or /
    private static final RegExp HOST2           = RegExp.compile("@([A-Za-z0-9_\\-]+\\.[A-Za-z0-9_\\-:]+)+[:/]");
    // the repository name
    private static final RegExp REPO_NAME       = RegExp.compile("/[A-Za-z0-9_.\\-]+$");
    // start with white space
    private static final RegExp WHITE_SPACE     = RegExp.compile("^\\s");

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
    public void setContext(WizardContext wizardContext) {
        this.wizardContext = wizardContext;
    }

    @Override
    public void setProjectWizardDelegate(Wizard.UpdateDelegate updateDelegate) {
        this.updateDelegate = updateDelegate;
    }

    @Override
    public void clear() {
        view.reset();
    }

    @Override
    public void projectNameChanged(String name) {
        if (name == null || name.isEmpty()) {
            wizardContext.removeData(ProjectWizard.PROJECT_NAME);
        } else if (NAME_PATTERN.test(name)) {
            wizardContext.putData(ProjectWizard.PROJECT_NAME, name);
            view.hideNameError();
        } else {
            wizardContext.removeData(ProjectWizard.PROJECT_NAME);
            view.showNameError();
        }
        updateDelegate.updateControls();
    }

    @Override
    public void projectUrlChanged(String url) {
        if (!isGitUrlCorrect(url)) {
            wizardContext.removeData(ImportProjectWizard.PROJECT_URL);
        } else {
            wizardContext.putData(ImportProjectWizard.PROJECT_URL, url);
            view.hideUrlError();

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
    public void projectDescriptionChanged(String projectDescriptionValue) {
        wizardContext.putData(ProjectWizard.PROJECT_DESCRIPTION, projectDescriptionValue);
    }

    @Override
    public void projectVisibilityChanged(Boolean aPublic) {
        wizardContext.putData(ProjectWizard.PROJECT_VISIBILITY, aPublic);
    }

    @Override
    public void onEnterClicked() {

    }

    @Override
    public void go(AcceptsOneWidget container) {
        clear();
        ProjectImporterDescriptor projectImporter = wizardContext.getData(ImportProjectWizard.PROJECT_IMPORTER);
        view.setImporterDescription(projectImporter.getDescription());
        view.setInputsEnableState(true);
        container.setWidget(view);
        view.focusInUrlInput();
    }

    /** Gets project name from uri. */
    private String parseUri(String uri) {
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

    private boolean isGitUrlCorrect(String url) {
        if (WHITE_SPACE.test(url)) {
            view.showUrlError(locale.importProjectMessageStartWithWhiteSpace());
            return false;
        }

        if (SCP_LIKE_SYNTAX.test(url) && REPO_NAME.test(url)) {
            return true;
        } else if (SCP_LIKE_SYNTAX.test(url) && !REPO_NAME.test(url)) {
            view.showUrlError(locale.importProjectMessageNameRepoIncorrect());
            return false;
        }

        if (!PROTOCOL.test(url)) {
            view.showUrlError(locale.importProjectMessageProtocolIncorrect());
            return false;
        }
        if (!(HOST1.test(url) || HOST2.test(url))) {
            view.showUrlError(locale.importProjectMessageHostIncorrect());
            return false;
        }
        if (!(REPO_NAME.test(url))) {
            view.showUrlError(locale.importProjectMessageNameRepoIncorrect());
            return false;
        }
        view.hideUrlError();
        return true;
    }

}
