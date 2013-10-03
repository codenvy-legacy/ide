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

import com.codenvy.ide.api.ui.wizard.WizardDialog;
import com.codenvy.ide.api.ui.wizard.WizardModel;
import com.codenvy.ide.api.ui.wizard.WizardPage;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * Wizard presenter manages wizard pages. It's responsible for the
 * communication user and wizard page.
 * In typical usage, the client instantiates this class with a particular
 * wizard page. The wizard serves as the wizard page container and orchestrates the
 * presentation of its pages.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class WizardDialogPresenter implements WizardDialog, WizardModel.UpdateDelegate, WizardDialogView.ActionDelegate {
    private WizardModel      wizardModel;
    private WizardPage       currentPage;
    private WizardDialogView view;

    /**
     * Creates WizardPresenter with given current wizard page and wizard's title
     *
     * @param view
     * @param wizardModel
     */
    @Inject
    public WizardDialogPresenter(WizardDialogView view, @Assisted WizardModel wizardModel) {
        this.view = view;
        this.view.setDelegate(this);
        this.wizardModel = wizardModel;
        this.wizardModel.setUpdateDelegate(this);
    }

    /** {@inheritDoc} */
    @Override
    public void onNextClicked() {
        setPage(wizardModel.flipToNext());
    }

    /** {@inheritDoc} */
    @Override
    public void onBackClicked() {
        setPage(wizardModel.flipToPrevious());
    }

    /** {@inheritDoc} */
    @Override
    public void onFinishClicked() {
        wizardModel.onFinish();
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        // TODO may be need to remove
        wizardModel.onCancel();
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void updateControls() {
        // read the state of the buttons from current page
        view.setBackButtonVisible(wizardModel.hasPrevious());
        view.setNextButtonVisible(wizardModel.hasNext());
        view.setNextButtonEnabled(currentPage.isCompleted());
        // TODO showing finish button
        view.setFinishButtonEnabled(wizardModel.canFinish() && currentPage.isCompleted());
        view.setCaption(currentPage.getCaption());
        view.setNotice(currentPage.getNotice());
        view.setImage(currentPage.getImage());
    }

    /** {@inheritDoc} */
    @Override
    public void show() {
        setPage(wizardModel.flipToFirst());
        view.setTitle(wizardModel.getTitle());
        view.showDialog();
        view.setEnabledAnimation(true);
    }

    private void setPage(WizardPage wizardPage) {
        currentPage = wizardPage;
        updateControls();
        currentPage.go(view.getContentPanel());
    }
}