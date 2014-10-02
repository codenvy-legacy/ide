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
package com.codenvy.ide.wizard.project.importproject;

import com.codenvy.api.project.gwt.client.ProjectImportersServiceClient;
import com.codenvy.api.project.shared.dto.ProjectImporterDescriptor;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.projecttype.wizard.ImportProjectWizard;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizard;
import com.codenvy.ide.api.wizard.AbstractWizardPage;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.wizard.project.ProjectWizardView;
import com.codenvy.ide.wizard.project.importproject.ImportProjectWizardView.EnterPressedDelegate;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * Presenter of the import project wizard's main page.
 *
 * @author Ann Shumilova
 */
public class MainPagePresenter extends AbstractWizardPage implements MainPageView.ActionDelegate {

    private static final RegExp NAME_PATTERN = RegExp.compile("^[A-Za-z0-9_-]*$");
    private final MainPageView                  view;
    private final ProjectImportersServiceClient projectImportersService;
    private final NotificationManager           notificationManager;
    private final DtoUnmarshallerFactory        dtoUnmarshallerFactory;
    private final CoreLocalizationConstant      locale;
    private       ProjectImporterDescriptor     projectImporter;
    private       EnterPressedDelegate          enterPressedDelegate;

    @Inject
    public MainPagePresenter(ProjectImportersServiceClient projectImportersService,
                             MainPageView view,
                             NotificationManager notificationManager,
                             DtoUnmarshallerFactory dtoUnmarshallerFactory, CoreLocalizationConstant locale) {
        super("Choose Project", null);
        this.view = view;
        view.setDelegate(this);
        this.projectImportersService = projectImportersService;
        this.notificationManager = notificationManager;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.locale = locale;
    }

    public void setEnterPressedDelegate(EnterPressedDelegate enterPressedDelegate) {
        this.enterPressedDelegate = enterPressedDelegate;
    }

    /**
     * Disable all page inputs.
     */
    public void disableInputs() {
        view.setInputsEnableState(false);
    }

    /**
     * Enable all page inputs.
     */
    public void enableInputs() {
        view.setInputsEnableState(true);
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
        delegate.updateControls();
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
    public ProjectWizardView.ActionDelegate getProjectWizardDelegate() {
        return (ProjectWizardView.ActionDelegate)delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void projectImporterSelected(ProjectImporterDescriptor importer) {
        this.projectImporter = importer;
        wizardContext.putData(ImportProjectWizard.PROJECT_IMPORTER, importer);
        view.setImporterDescription(importer.getDescription());
        view.focusInUrlInput();
        delegate.updateControls();
    }


    /** {@inheritDoc} */
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
        delegate.updateControls();
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

    @Nullable
    @Override
    public String getNotice() {
        return null;
    }

    @Override
    public boolean isCompleted() {
        return projectImporter != null && wizardContext.getData(ProjectWizard.PROJECT_NAME) != null
               && wizardContext.getData(ImportProjectWizard.PROJECT_URL) != null;
    }

    @Override
    public void focusComponent() {

    }

    @Override
    public void removeOptions() {

    }

    @Override
    public void go(final AcceptsOneWidget container) {
        projectImporter = null;
        view.reset();
        container.setWidget(view);
        view.setInputsEnableState(true);

        final Map<String, Set<ProjectImporterDescriptor>> importers = new HashMap<>();
        projectImportersService.getProjectImporters(new AsyncRequestCallback<Array<ProjectImporterDescriptor>>(
                dtoUnmarshallerFactory.newArrayUnmarshaller(ProjectImporterDescriptor.class)) {
            @Override
            protected void onSuccess(Array<ProjectImporterDescriptor> result) {
                for (int i = 0; i < result.size(); i++) {
                    ProjectImporterDescriptor importer = result.get(i);
                    // do not show internal importers:
                    if (!importer.isInternal()) {
                        if (importer.getCategory() == null) {
                            break;
                        }
                        if (importers.containsKey(importer.getCategory())) {
                            importers.get(importer.getCategory()).add(importer);
                        } else {
                            Set<ProjectImporterDescriptor> importersSet = new HashSet<ProjectImporterDescriptor>();
                            importersSet.add(importer);
                            importers.put(importer.getCategory(), importersSet);
                        }
                    }
                }
                new Timer() {
                    @Override
                    public void run() {
                        view.setImporters(importers);
                        if (importers.keySet().iterator().hasNext()) {
                            view.selectImporter(importers.get(importers.keySet().iterator().next()).iterator().next());
                        }
                        view.focusInUrlInput();
                    }
                }.schedule(300);

            }

            @Override
            protected void onFailure(Throwable exception) {
                Log.error(MainPagePresenter.class, locale.importProjectError() + exception);
                Notification notification = new Notification(exception.getMessage(), ERROR);
                notificationManager.showNotification(notification);
            }
        });
    }


    /** {@inheritDoc} */
    @Override
    public void onEnterClicked() {
        if (enterPressedDelegate != null) {
            enterPressedDelegate.onEnterKeyPressed();
        }
    }

    private boolean isGitUrlCorrect(String url) {
        // An alternative scp-like syntax: [user@]host.xz:path/to/repo.git/
        RegExp scpLikeSyntax = RegExp.compile("([A-Za-z0-9_\\-]+\\.[A-Za-z0-9_\\-:]+)+:");

        // the transport protocol
        RegExp protocol = RegExp.compile("((http|https|git|ssh|ftp|ftps)://)");

        // the address of the remote server between // and /
        RegExp host1 = RegExp.compile("//([A-Za-z0-9_\\-]+\\.[A-Za-z0-9_\\-:]+)+/");

        // the address of the remote server between @ and : or /
        RegExp host2 = RegExp.compile("@([A-Za-z0-9_\\-]+\\.[A-Za-z0-9_\\-:]+)+[:/]");

        // the repository name
        RegExp repoName = RegExp.compile("/[A-Za-z0-9_.\\-]+$");

        // start with white space
        RegExp whiteSpace = RegExp.compile("^\\s");

        if (whiteSpace.test(url)) {
            view.showUrlError(locale.importProjectMessageStartWithWhiteSpace());
            return false;
        }

        if (scpLikeSyntax.test(url) && repoName.test(url)) {
            return true;
        } else if (scpLikeSyntax.test(url) && !repoName.test(url)) {
            view.showUrlError(locale.importProjectMessageNameRepoIncorrect());
            return false;
        }

        if (!protocol.test(url)) {
            view.showUrlError(locale.importProjectMessageProtocolIncorrect());
            return false;
        }
        if (!(host1.test(url) || host2.test(url))) {
            view.showUrlError(locale.importProjectMessageHostIncorrect());
            return false;
        }
        if (!(repoName.test(url))) {
            view.showUrlError(locale.importProjectMessageNameRepoIncorrect());
            return false;
        }
        view.hideUrlError();
        return true;
    }
}
