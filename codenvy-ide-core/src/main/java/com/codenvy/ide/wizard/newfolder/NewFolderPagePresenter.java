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
package com.codenvy.ide.wizard.newfolder;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.ui.wizard.AbstractWizardPagePresenter;
import com.codenvy.ide.api.ui.wizard.WizardPagePresenter;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Folder;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.codenvy.ide.resources.model.ResourceNameValidator;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.wizard.newfolder.NewFolderPageView.ActionDelegate;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;


/**
 * Provides creating new folder.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class NewFolderPagePresenter extends AbstractWizardPagePresenter implements ActionDelegate {
    private NewFolderPageView view;

    private boolean isFolderNameValid;

    private boolean hasSameFolder;

    protected Project project;

    /**
     * Create presenter.
     *
     * @param resources
     * @param resourceProvider
     */
    @Inject
    public NewFolderPagePresenter(Resources resources, ResourceProvider resourceProvider, SelectionAgent selectionAgent) {
        // TODO change icon?
        this("Create a new folder resource", resources.folder(), new NewFolderPageViewImpl(), resourceProvider);
        if (selectionAgent.getSelection() != null) {
            if (selectionAgent.getSelection().getFirstElement() instanceof Resource) {
                Resource resource = (Resource)selectionAgent.getSelection().getFirstElement();
                String path = "";
                if (resource.isFile()) {
                    path = resource.getParent().getRealtivePath() + "/";
                } else {
                    path = resource.getRealtivePath() + "/";
                }
                view.setFolderName(path);
            }
        }
    }

    /**
     * Create presenter.
     * <p/>
     * For tests.
     *
     * @param caption
     * @param image
     * @param view
     * @param resourceProvider
     */
    protected NewFolderPagePresenter(String caption, ImageResource image, NewFolderPageView view,
                                     ResourceProvider resourceProvider) {
        super(caption, image);
        this.view = view;
        view.setDelegate(this);
        this.project = resourceProvider.getActiveProject();
    }

    /** {@inheritDoc} */
    @Override
    public boolean canFinish() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCompleted() {
        return !view.getFolderName().isEmpty() && isFolderNameValid && !hasSameFolder;
    }

    /** {@inheritDoc} */
    @Override
    public String getNotice() {
        if (view.getFolderName().isEmpty()) {
            return "The folder name can't be empty.";
        } else if (!isFolderNameValid) {
            return "The folder name has incorrect symbol.";
        } else if (hasSameFolder) {
            return "The folder with same name already exists.";
        }

        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void doFinish() {
        project.createFolder(project, view.getFolderName(), new AsyncCallback<Folder>() {
            @Override
            public void onSuccess(Folder result) {
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(NewFolderPagePresenter.class, caught);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void checkEnteredInformation() {
        String newFolderName = view.getFolderName();
        isFolderNameValid = ResourceNameValidator.isFolderNameValid(newFolderName);

        // Does the folder with same name exist?
        hasSameFolder = false;
        JsonArray<Resource> children = project.getChildren();
        for (int i = 0; i < children.size() && !hasSameFolder; i++) {
            Resource child = children.get(i);
            if (child.isFolder()) {
                hasSameFolder = child.getName().compareTo(newFolderName) == 0;
            }
        }

        delegate.updateControls();
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

    /** {@inheritDoc} */
    @Override
    public WizardPagePresenter flipToNext() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasNext() {
        return false;
    }
}