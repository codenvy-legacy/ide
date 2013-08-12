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
package com.codenvy.ide.wizard.newfile;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.ui.wizard.newfile.AbstractNewFilePagePresenter;
import com.codenvy.ide.api.ui.wizard.newfile.NewGenericFilePageView;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.rest.MimeType;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;


/**
 * Provides creating new empty text file.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class NewTextFilePagePresenter extends AbstractNewFilePagePresenter {

    /**
     * Create presenter.
     *
     * @param resources
     * @param resourceProvider
     */
    @Inject
    public NewTextFilePagePresenter(Resources resources, ResourceProvider resourceProvider, SelectionAgent selectionAgent) {
        this(resources.newResourceIcon(), new NewGenericFilePageViewImpl(), resourceProvider, selectionAgent);
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
    protected NewTextFilePagePresenter(ImageResource image, NewGenericFilePageView view, ResourceProvider resourceProvider,
                                       SelectionAgent selectionAgent) {
        super("Create a new empty text file", image, view, "txt", resourceProvider, selectionAgent);
    }

    /** {@inheritDoc} */
    @Override
    public void doFinish() {
        String fileName = view.getFileName() + (hasExtension() ? "" : '.' + getFileExtension());

        project.createFile(project, fileName, "", MimeType.TEXT_PLAIN, new AsyncCallback<File>() {
            @Override
            public void onSuccess(File result) {
            }

            @Override
            public void onFailure(Throwable caught) {
                // TODO : Handle error to be able to display message to the User
                Log.error(NewTextFilePagePresenter.class, caught);
            }
        });
    }
}