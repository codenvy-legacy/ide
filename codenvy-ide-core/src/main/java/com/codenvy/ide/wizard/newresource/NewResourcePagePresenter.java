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
package com.codenvy.ide.wizard.newresource;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.ui.wizard.AbstractWizardPagePresenter;
import com.codenvy.ide.api.ui.wizard.WizardPagePresenter;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.wizard.WizardAgentImpl;
import com.codenvy.ide.wizard.newresource.NewResourcePageView.ActionDelegate;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;


/**
 * Provides selecting kind of file which user wish to create.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class NewResourcePagePresenter extends AbstractWizardPagePresenter implements ActionDelegate {
    private NewResourcePageView view;
    private WizardPagePresenter next;

    /**
     * Create presenter.
     *
     * @param resources
     * @param view
     */
    @Inject
    protected NewResourcePagePresenter(Resources resources, NewResourcePageView view, WizardAgentImpl wizardAgent) {
        super("Create a new resource", resources.newResourceIcon());
        this.view = view;
        this.view.setDelegate(this);
        JsonArray<NewResourceWizardData> resourceWizards = wizardAgent.getNewResourceWizards();
        this.view.setResourceWizard(resourceWizards);
    }

    /** {@inheritDoc} */
    public boolean isCompleted() {
        return next != null;
    }

    /** {@inheritDoc} */
    public boolean hasNext() {
        return next != null;
    }

    /** {@inheritDoc} */
    public WizardPagePresenter flipToNext() {
        next.setPrevious(this);
        next.setUpdateDelegate(delegate);
        return next;
    }

    /** {@inheritDoc} */
    public String getNotice() {
        if (next == null) {
            return "Please, select resource type.";
        }

        return null;
    }

    /** {@inheritDoc} */
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

    /** {@inheritDoc} */
    public void selectedFileType(NewResourceWizardData newResourceWizard) {
        next = newResourceWizard.getWizardPage();
        delegate.updateControls();
    }

    /** {@inheritDoc} */
    public boolean canFinish() {
        return false;
    }
}