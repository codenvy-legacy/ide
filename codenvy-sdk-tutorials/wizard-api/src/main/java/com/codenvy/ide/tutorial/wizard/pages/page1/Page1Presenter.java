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