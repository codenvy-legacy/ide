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
package com.codenvy.ide.wizard.newproject;

import com.codenvy.ide.api.ui.wizard.Wizard;
import com.codenvy.ide.api.ui.wizard.WizardDialog;
import com.codenvy.ide.api.ui.wizard.WizardPage;
import com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import javax.validation.constraints.NotNull;

/**
 * The implementation of {@link com.codenvy.ide.api.ui.wizard.WizardDialog}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class ProjectWizardPresenter implements WizardDialog, Wizard.UpdateDelegate, ProjectWizardView.ActionDelegate {
    private NewProjectWizard  wizard;
    private WizardPage        currentPage;
    private ProjectWizardView view;

    /**
     * Creates Wizard dialog with given view and wizard.
     *
     * @param view
     * @param wizard
     */
    @Inject
    public ProjectWizardPresenter(ProjectWizardView view, @Assisted NewProjectWizard wizard) {
        this.view = view;
        this.view.setDelegate(this);
        this.wizard = wizard;
        this.wizard.setUpdateDelegate(this);
    }

    /** {@inheritDoc} */
    @Override
    public void onNextClicked() {
        currentPage.storeOptions();
        setPage(wizard.flipToNext());
        currentPage.focusComponent();
    }

    /** {@inheritDoc} */
    @Override
    public void onBackClicked() {
        currentPage.removeOptions();
        setPage(wizard.flipToPrevious());
    }

    /** {@inheritDoc} */
    @Override
    public void onFinishClicked() {
        wizard.onFinish();
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void updateControls() {
        // change state of buttons
        view.setBackButtonVisible(wizard.hasPrevious());
        view.setNextButtonVisible(wizard.hasNext());
        view.setNextButtonEnabled(currentPage.isCompleted());
        view.setFinishButtonEnabled(wizard.canFinish() && currentPage.isCompleted());
        view.setCaption(currentPage.getCaption());
        view.setNotice(currentPage.getNotice());
        view.setImage(currentPage.getImage());
    }

    /** {@inheritDoc} */
    @Override
    public void show() {
        setPage(wizard.flipToFirst());
        Array<String> stepsTitles = Collections.createArray();
        for (WizardPage p : wizard.getPages().asIterable()) {
            stepsTitles.add(p.getCaption());
        }
        view.setStepTitles(stepsTitles);
        currentPage.focusComponent();
        view.setTitle(wizard.getTitle());
        view.showDialog();
        view.setEnabledAnimation(true);
    }

    /**
     * Change current page and responds other operation which needed for changing page.
     *
     * @param wizardPage
     *         new current page
     */
    private void setPage(@NotNull WizardPage wizardPage) {
        currentPage = wizardPage;
        updateControls();
        currentPage.go(view.getContentPanel());
    }
}