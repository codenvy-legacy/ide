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
package com.codenvy.ide.extension.css.wizard;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.ui.wizard.newfile.AbstractNewFilePagePresenter;
import com.codenvy.ide.api.ui.wizard.newfile.NewGenericFilePageView;
import com.codenvy.ide.extension.css.CssExtensionResource;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.rest.MimeType;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;


/**
 * Provides creating new CSS file.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class NewCSSFilePagePresenter extends AbstractNewFilePagePresenter {
    /**
     * Create presenter.
     *
     * @param view
     *         generic NewFileView
     * @param resourceProvider
     * @param selectionAgent
     */
    @Inject
    public NewCSSFilePagePresenter(CssExtensionResource resources, NewGenericFilePageView view, ResourceProvider resourceProvider,
                                   SelectionAgent selectionAgent) {
        this(resources.file(), view, resourceProvider, selectionAgent);
    }

    /**
     * Create presenter.
     * <p/>
     * For tests.
     *
     * @param image
     * @param view
     * @param resourceProvider
     */
    protected NewCSSFilePagePresenter(ImageResource image, NewGenericFilePageView view, ResourceProvider resourceProvider,
                                      SelectionAgent selectionAgent) {
        super("Create a new CSS file", image, view, "css", resourceProvider, selectionAgent);
    }

    /** {@inheritDoc} */
    @Override
    public void doFinish() {
        String fileName = view.getFileName() + (hasExtension() ? "" : '.' + getFileExtension());

        project.createFile(project, fileName, "@CHARSET \"UTF-8\";", MimeType.TEXT_CSS, new AsyncCallback<File>() {
            @Override
            public void onSuccess(File result) {
            }

            @Override
            public void onFailure(Throwable caught) {
                // TODO : Handle error to be able to display message to the User
                Log.error(NewCSSFilePagePresenter.class, caught);
            }
        });
    }
}