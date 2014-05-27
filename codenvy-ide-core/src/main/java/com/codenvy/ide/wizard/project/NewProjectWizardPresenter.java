/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2014] Codenvy, S.A.
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
package com.codenvy.ide.wizard.project;

import com.codenvy.ide.api.ui.wizard.ProjectWizard;
import com.codenvy.ide.api.ui.wizard.Wizard;
import com.codenvy.ide.api.ui.wizard.WizardContext;
import com.codenvy.ide.api.ui.wizard.WizardDialog;
import com.codenvy.ide.api.ui.wizard.WizardPage;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.ui.dialogs.info.Info;
import com.codenvy.ide.wizard.newproject.ProjectWizardView;
import com.codenvy.ide.wizard.project.main.MainPagePresenter;
import com.codenvy.ide.wizard.project.name.NamePagePresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

/**
 * @author Evgen Vidolob
 */
@Singleton
public class NewProjectWizardPresenter  implements WizardDialog, Wizard.UpdateDelegate, ProjectWizardView.ActionDelegate {
    private WizardPage        currentPage;
    private ProjectWizardView view;
    private MainPagePresenter mainPage;
    private NamePagePresenter namePage;
    /** Pages for which 'step tabs' will be showed. */
    private Array<WizardPage> stepsPages = Collections.createArray();
    private WizardContext wizardContext;

    @Inject
    public NewProjectWizardPresenter(ProjectWizardView view, MainPagePresenter mainPage, NamePagePresenter namePage) {
        this.view = view;
        this.mainPage = mainPage;
        this.namePage = namePage;
        mainPage.setUpdateDelegate(this);
        view.setDelegate(this);
        wizardContext = new WizardContext();
    }

    /** {@inheritDoc} */
    @Override
    public void onNextClicked() {
        currentPage.storeOptions();
        final int previousStepPageIndex = stepsPages.indexOf(currentPage);
        WizardPage wizardPage = stepsPages.get(previousStepPageIndex + 1);
        setPage(wizardPage);
        if (wizardPage == namePage) {
            view.setStepTitles(namePage.getStepsCaptions());
            Array<WizardPage> nextPages = namePage.getNextPages();
            if(nextPages != null){
                stepsPages.addAll(nextPages);
            }
        } //else {
        view.setStepArrowPosition(stepsPages.indexOf(currentPage) - previousStepPageIndex);
        currentPage.focusComponent();
//        }
    }

    /** {@inheritDoc} */
    @Override
    public void onBackClicked() {
        currentPage.removeOptions();
        final int previousStepPageIndex = stepsPages.indexOf(currentPage);
        if (previousStepPageIndex == 0) return;
        WizardPage wizardPage = stepsPages.get(previousStepPageIndex - 1);
        if (wizardPage == mainPage) {
            view.setStepTitles(Collections.createArray(mainPage.getCaption(), "..."));
        }
        setPage(wizardPage);
        view.setStepArrowPosition(stepsPages.indexOf(currentPage) - previousStepPageIndex);
    }

    /** {@inheritDoc} */
    @Override
    public void onFinishClicked() {
        currentPage.commit(new WizardPage.CommitCallback() {
            @Override
            public void onSuccess() {
                view.close();
            }

            @Override
            public void onFailure(@NotNull Throwable exception) {
                Info info = new Info("Project already exist");
                info.show();
            }
        });
        namePage.clearFields();
        mainPage.clearProjectTypePanel();
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
        namePage.clearFields();
        mainPage.clearProjectTypePanel();
    }

    /** {@inheritDoc} */
    @Override
    public void updateControls() {
        // change state of buttons
        view.setBackButtonVisible(stepsPages.indexOf(currentPage) != 0);
        view.setNextButtonVisible(stepsPages.indexOf(currentPage) != stepsPages.size() - 1);
        view.setNextButtonEnabled(currentPage.isCompleted());
        if(wizardContext.getData(ProjectWizard.PROJECT_TEMPLATE) != null){
            view.setFinishButtonEnabled(currentPage.isCompleted() && currentPage != mainPage);
        } else {
            view.setFinishButtonEnabled(currentPage.isCompleted() && currentPage != mainPage && currentPage != namePage);
        }
        view.setCaption(currentPage.getCaption());
        view.setNotice(currentPage.getNotice());
        view.setImage(currentPage.getImage());
    }

    /** {@inheritDoc} */
    @Override
    public void show() {
        wizardContext.clear();
        showFirstPage();
    }

    private void showFirstPage() {
        stepsPages.clear();
        stepsPages.add(mainPage);
        stepsPages.add(namePage);
        view.setTitle("New Project");
        setPage(mainPage);
        view.setStepTitles(Collections.createArray(mainPage.getCaption(), "..."));
        view.showDialog();
        view.setEnabledAnimation(true);
    }

    public void show(WizardContext context){
        wizardContext = context;
        showFirstPage();
    }

    /**
     * Change current page and responds other operation which needed for changing page.
     *
     * @param wizardPage
     *         new current page
     */
    private void setPage(@NotNull WizardPage wizardPage) {
        currentPage = wizardPage;
        currentPage.setContext(wizardContext);
        currentPage.setUpdateDelegate(this);
        updateControls();
        currentPage.go(view.getContentPanel());
    }
}
