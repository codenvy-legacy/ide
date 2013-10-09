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
package com.codenvy.ide.wizard;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.ui.wizard.Wizard;
import com.codenvy.ide.api.ui.wizard.WizardDialog;
import com.codenvy.ide.api.ui.wizard.WizardPage;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * Wizard dialog presenter manages wizard pages. It's responsible for the communication user and wizard page.
 * In typical usage, the client instantiates this class with a particular wizard. The wizard dialog orchestrates the presentation of wizard
 * pages.
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