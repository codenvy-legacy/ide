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
package com.codenvy.ide.tutorial.wizard.pages.page1;

import com.codenvy.ide.api.ui.wizard.AbstractWizardPage;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import static com.codenvy.ide.tutorial.wizard.WizardTutorialExtension.PAGE2_NEXT;
import static com.codenvy.ide.tutorial.wizard.WizardTutorialExtension.PAGE4_SKIP;

/**
 * The page for managing visibility and availability of next pages.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class Page1Presenter extends AbstractWizardPage implements Page1View.ActionDelegate {
    private Page1View view;

    @Inject
    public Page1Presenter(Page1View view) {
        super("Page 1", null);
        this.view = view;
        this.view.setDelegate(this);
    }

    /** {@inheritDoc} */
    @Override
    public String getNotice() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCompleted() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void focusComponent() {
        view.setPage2Next(true);
        view.setPage4Show(false);

        wizardContext.putData(PAGE2_NEXT, true);
        wizardContext.putData(PAGE4_SKIP, true);
    }

    /** {@inheritDoc} */
    @Override
    public void removeOptions() {
        wizardContext.removeData(PAGE2_NEXT);
        wizardContext.removeData(PAGE4_SKIP);
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

    /** {@inheritDoc} */
    @Override
    public void onPage2Chosen() {
        wizardContext.putData(PAGE2_NEXT, view.isPage2Next());
    }

    /** {@inheritDoc} */
    @Override
    public void onPage3Chosen() {
        wizardContext.putData(PAGE2_NEXT, view.isPage2Next());
    }

    /** {@inheritDoc} */
    @Override
    public void onPage4Clicked() {
        wizardContext.putData(PAGE4_SKIP, !view.isPage4Show());
    }
}