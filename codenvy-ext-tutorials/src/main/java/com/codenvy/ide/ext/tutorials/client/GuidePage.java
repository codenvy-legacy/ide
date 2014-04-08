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
