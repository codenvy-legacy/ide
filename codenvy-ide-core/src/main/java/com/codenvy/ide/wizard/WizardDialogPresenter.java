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
package com.codenvy.ide.wizard;

import com.codenvy.ide.api.wizard.Wizard;
import com.codenvy.ide.api.wizard.WizardDialog;
import com.codenvy.ide.api.wizard.WizardPage;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import javax.validation.constraints.NotNull;

/**
 * The implementation of {@link WizardDialog}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class WizardDialogPresenter implements WizardDialog, Wizard.UpdateDelegate, WizardDialogView.ActionDelegate {
    private Wizard           wizard;
    private WizardPage       currentPage;
    private WizardDialogView view;

    /**
     * Creates Wizard dialog with given view and wizard.
     *
     * @param view
     * @param wizard
     */
    @Inject
    public WizardDialogPresenter(WizardDialogView view, @Assisted Wizard wizard) {
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