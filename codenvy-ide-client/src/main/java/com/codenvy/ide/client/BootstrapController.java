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
package com.codenvy.ide.client;

import com.codenvy.ide.api.ui.perspective.WorkBenchPresenter.PartStackType;
import com.codenvy.ide.client.extensionsPart.ExtensionsPage;
import com.codenvy.ide.core.ComponentException;
import com.codenvy.ide.core.ComponentRegistry;
import com.codenvy.ide.perspective.WorkspacePresenter;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Inject;

/**
 * Performs initial application startup
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class BootstrapController {

    /**
     * @param componentRegistry
     * @param workspacePeresenter
     * @param styleInjector
     * @param extensionInitializer
     */
    @Inject
    public BootstrapController(ComponentRegistry componentRegistry, final WorkspacePresenter workspacePeresenter,
                               StyleInjector styleInjector, final ExtensionInitializer extensionInitializer,
                               final ExtensionsPage extensionsPage) {
        styleInjector.inject();

        // initialize components
        componentRegistry.start(new Callback<Void, ComponentException>() {
            @Override
            public void onSuccess(Void result) {
                // instantiate extensions
                extensionInitializer.startExtensions();
                // Start UI
                SimplePanel mainPanel = new SimplePanel();
                RootLayoutPanel.get().add(mainPanel);
                // Display IDE
                workspacePeresenter.go(mainPanel);
                // TODO FOR DEMO
                workspacePeresenter.openPart(extensionsPage, PartStackType.EDITING);
            }

            @Override
            public void onFailure(ComponentException caught) {
                GWT.log("FAILED to start service:" + caught.getComponent());
            }
        });

    }

}
