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
package com.codenvy.ide.ext.tutorials.client;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.workspace.AbstractPartPresenter;
import com.codenvy.ide.api.resources.model.File;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.ext.tutorials.client.TutorialsExtension.DEFAULT_README_FILE_NAME;

/**
 * Displays a page that contains a tutorial guide.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: GuidePage.java Sep 13, 2013 12:48:08 PM azatsarynnyy $
 */
@Singleton
public class GuidePage extends AbstractPartPresenter {

    private final ResourceProvider   resourceProvider;
    private       TutorialsResources resources;

    @Inject
    public GuidePage(ResourceProvider resourceProvider, TutorialsResources resources) {
        this.resourceProvider = resourceProvider;
        this.resources = resources;
    }

    /** {@inheritDoc} */
    @Override
    public void go(final AcceptsOneWidget container) {
        final StringBuilder builder = new StringBuilder();

        File resource =
                (File)resourceProvider.getActiveProject().findResourceByName(DEFAULT_README_FILE_NAME, File.TYPE);
        resourceProvider.getActiveProject().getContent(resource, new AsyncCallback<File>() {
            @Override
            public void onSuccess(File result) {
                builder.append(result.getContent());
                HTMLPanel htmlPanel = new HTMLPanel(builder.toString());
                htmlPanel.setStyleName(resources.tutorialsCss().scrollPanel());
                container.setWidget(htmlPanel);
            }

            @Override
            public void onFailure(Throwable caught) {
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public String getTitle() {
        return "Tutorial Guide";
    }

    /** {@inheritDoc} */
    @Override
    public ImageResource getTitleImage() {
        return resources.guide();
    }

    /** {@inheritDoc} */
    @Override
    public String getTitleToolTip() {
        return "This view displays a tutorial guide";
    }
}
