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
package com.codenvy.ide.client.extensionsPart;

import com.codenvy.ide.api.ui.workspace.AbstractPartPresenter;
import com.codenvy.ide.client.ExtensionInitializer;
import com.codenvy.ide.client.PageResources;
import com.codenvy.ide.extension.DependencyDescription;
import com.codenvy.ide.extension.ExtensionDescription;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonStringMap.IterationCallback;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * For demo purposes. Displays the list of registered extensions and their dependencies.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
@Singleton
public class ExtensionsPage extends AbstractPartPresenter {

    private final ExtensionInitializer extInitializer;

    private PageResources resources;

    @Inject
    public ExtensionsPage(ExtensionInitializer extInitializer, PageResources resources) {
        this.extInitializer = extInitializer;
        this.resources = resources;
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        final StringBuilder builder = new StringBuilder();

        extInitializer.getExtensionDescriptions().iterate(new IterationCallback<ExtensionDescription>() {

            @Override
            public void onIteration(String key, ExtensionDescription ext) {
                builder.append("<div>");

                builder.append("<h3>");
                builder.append(ext.getId());
                builder.append("(" + (ext.isEnabled() ? "enabled" : "disabled") + ")");
                builder.append("-");
                builder.append(ext.getVersion());
                builder.append("</h3>");

                if (!ext.getDependencies().isEmpty()) {
                    builder.append("<ul>");
                    JsonArray<DependencyDescription> dependencies = ext.getDependencies();

                    for (int i = 0; i < dependencies.size(); i++) {
                        DependencyDescription dep = dependencies.get(i);
                        builder.append("<li>");
                        builder.append(dep.getId());
                        builder.append(":");
                        builder.append(dep.getVersion());
                        builder.append("</li>");

                    }

                    builder.append("</ul>");
                }

                builder.append("</div>");
            }
        });

        HTMLPanel htmlPanel = new HTMLPanel(builder.toString());
        container.setWidget(htmlPanel);
    }

    /** {@inheritDoc} */
    @Override
    public String getTitle() {
        return "Extensions";
    }

    /** {@inheritDoc} */
    @Override
    public ImageResource getTitleImage() {
        return resources.extentionPageIcon();
    }

    /** {@inheritDoc} */
    @Override
    public String getTitleToolTip() {
        return "This view displays the list of extensions";
    }
}
