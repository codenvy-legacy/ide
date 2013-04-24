/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.java.client.template;

import com.codenvy.ide.api.paas.PaaSAgent;
import com.codenvy.ide.api.ui.wizard.AbstractWizardPagePresenter;
import com.codenvy.ide.api.ui.wizard.WizardPagePresenter;
import com.codenvy.ide.java.client.JavaClientBundle;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.ResourceNameValidator;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

/** @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a> */
public class CreateJavaProjectPagePresenter extends AbstractWizardPagePresenter implements CreateJavaProjectPageView.ActionDelegate {
    private CreateJavaProjectPageView  view;
    private boolean                    hasResourceFolderIncorrectSymbol;
    private PaaSAgent                  paasAgent;
    private WizardPagePresenter        paasWizardPage;
    private CreateJavaProjectPresenter createJavaProjectPresenter;

    @Inject
    protected CreateJavaProjectPagePresenter(JavaClientBundle resources, CreateJavaProjectPageView view, PaaSAgent paasAgent,
                                             CreateJavaProjectPresenter createJavaProjectPresenter) {
        super("Java Project", resources.javaProject());

        this.view = view;
        this.view.setDelegate(this);
        this.paasAgent = paasAgent;
        this.createJavaProjectPresenter = createJavaProjectPresenter;
    }

    /** {@inheritDoc} */
    @Override
    public WizardPagePresenter flipToNext() {
        createJavaProjectPresenter.setSourceFolder(view.getSourceFolder());

        return paasWizardPage;
    }

    /** {@inheritDoc} */
    @Override
    public boolean canFinish() {
        return isCompleted() && !hasNext();
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasNext() {
        return paasWizardPage != null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCompleted() {
        return !hasResourceFolderIncorrectSymbol && !view.getSourceFolder().isEmpty();
    }

    /** {@inheritDoc} */
    @Override
    public String getNotice() {
        if (view.getSourceFolder().isEmpty()) {
            return "Please, enter a source folder name.";
        } else if (hasResourceFolderIncorrectSymbol) {
            return "Incorrect source folder name.";
        }

        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);

        paasWizardPage = paasAgent.getSelectedPaaS().getWizardPage();
    }

    /** {@inheritDoc} */
    @Override
    public void checkSourceFolederInput() {
        String resourceFolder = view.getSourceFolder();
        hasResourceFolderIncorrectSymbol = !ResourceNameValidator.isFolderNameValid(resourceFolder);

        delegate.updateControls();
    }

    /** {@inheritDoc} */
    @Override
    public void doFinish() {
        createJavaProjectPresenter.setSourceFolder(view.getSourceFolder());
        createJavaProjectPresenter.create(new AsyncCallback<Project>() {
            @Override
            public void onSuccess(Project result) {
                //do nothing
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(CreateJavaProjectPagePresenter.class, caught);
            }
        });
    }
}