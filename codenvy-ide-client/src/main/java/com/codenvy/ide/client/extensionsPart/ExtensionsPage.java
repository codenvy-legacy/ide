/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package com.codenvy.ide.client.extensionsPart;

import com.codenvy.ide.api.ui.perspective.AbstractPartPresenter;
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
 * For demo purposes. Displays the list of registered extensions and their dependensies.
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
