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
package com.codenvy.ide.api.ui.wizard.newfile;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.ui.wizard.AbstractWizardPagePresenter;
import com.codenvy.ide.api.ui.wizard.WizardPagePresenter;
import com.codenvy.ide.api.ui.wizard.newfile.NewGenericFilePageView.ActionDelegate;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.codenvy.ide.resources.model.ResourceNameValidator;
import com.codenvy.ide.util.StringUtils;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;


/**
 * NewGenericFilePagePresenter is an abstract implementation of NewFilePresenter.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public abstract class AbstractNewFilePagePresenter extends AbstractWizardPagePresenter implements ActionDelegate {
    private boolean isExtensionValid;

    private boolean hasExtension;

    private boolean isFileNameValid;

    private boolean hasSameFile;

    protected NewGenericFilePageView view;

    private String fileExtension;

    protected Project project;

    /**
     * Create presenter.
     *
     * @param caption
     * @param image
     * @param view
     * @param fileExtension
     * @param resourceProvider
     */
    public AbstractNewFilePagePresenter(String caption, ImageResource image, NewGenericFilePageView view,
                                        String fileExtension, ResourceProvider resourceProvider, SelectionAgent selectionAgent) {
        super(caption, image);

        this.view = view;
        view.setDelegate(this);
        this.fileExtension = fileExtension;
        this.project = resourceProvider.getActiveProject();

        if (selectionAgent.getSelection() != null) {
            if (selectionAgent.getSelection().getFirstElement() instanceof Resource) {
                Resource resource = (Resource)selectionAgent.getSelection().getFirstElement();
                String path = "";
                if (resource.isFile()) {
                    path = resource.getParent().getRealtivePath() + "/";
                } else {
                    path = resource.getRealtivePath() + "/";
                }
                view.setFileName(path);
            }
        }

    }

    /** {@inheritDoc} */
    @Override
    public boolean isCompleted() {
        return !view.getFileName().isEmpty() && isFileNameValid && isExtensionValid && !hasSameFile;
    }

    /** {@inheritDoc} */
    @Override
    public WizardPagePresenter flipToNext() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean canFinish() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasNext() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public String getNotice() {
        if (view.getFileName().isEmpty()) {
            return "The file name can't be empty.";
        } else if (!isFileNameValid) {
            return "The file name has incorrect symbol.";
        } else if (!isExtensionValid) {
            return "The file name must end in one of the following extensions " + '[' + getFileExtension() + "].";
        } else if (hasSameFile) {
            return "The file with same name already exists.";
        }

        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

    /** {@inheritDoc} */
    @Override
    public void onValueChanged() {
        isExtensionValid = true;
        hasExtension = false;

        String fileName = view.getFileName();
        JsonArray<String> parts = StringUtils.split(fileName, ".");
        int size = parts.size();
        // check have file name extension
        if (size > 1) {
            hasExtension = true;
            // compare last part of file name with extension for this type of file
            isExtensionValid = parts.get(size - 1).compareTo(getFileExtension()) == 0;
        }

        isFileNameValid = ResourceNameValidator.isFileNameValid(parts.get(0));

        // add extension to file name if it needs
        String name = hasExtension ? fileName : fileName + '.' + getFileExtension();

        // Does the file with same name exist?
        hasSameFile = false;
        JsonArray<Resource> children = project.getChildren();
        for (int i = 0; i < children.size() && !hasSameFile; i++) {
            Resource child = children.get(i);
            if (child.isFile()) {
                hasSameFile = child.getName().compareTo(name) == 0;
            }
        }

        delegate.updateControls();
    }

    /**
     * Returns whether entered file name has extension.
     *
     * @return <code>true</code> if the name has extension, and
     *         <code>false</code> otherwise
     */
    protected boolean hasExtension() {
        return hasExtension;
    }

    /**
     * Returns file extension.
     *
     * @return
     */
    protected String getFileExtension() {
        return fileExtension;
    }
}