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
 * @author Andrey Plotnikov
 */
public class ProjectWizardPresenter implements WizardDialog, Wizard.UpdateDelegate, ProjectWizardView.ActionDelegate {
    private NewProjectWizard  wizard;
    private WizardPage        currentPage;
    private ProjectWizardView view;
    /** Pages for which 'step tabs' will be showed. */
    private Array<WizardPage> stepsPages = Collections.createArray();

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
        final int previousStepPageIndex = stepsPages.indexOf(currentPage);
        setPage(wizard.flipToNext());
        currentPage.focusComponent();
        view.setStepArrowPosition(stepsPages.indexOf(currentPage) - previousStepPageIndex);
    }

    /** {@inheritDoc} */
    @Override
    public void onBackClicked() {
        currentPage.removeOptions();
        final int previousStepPageIndex = stepsPages.indexOf(currentPage);
        setPage(wizard.flipToPrevious());
        view.setStepArrowPosition(stepsPages.indexOf(currentPage) - previousStepPageIndex);
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
            if (p.getCaption() != null) {
                stepsPages.add(p);
                stepsTitles.add(p.getCaption());
            }
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