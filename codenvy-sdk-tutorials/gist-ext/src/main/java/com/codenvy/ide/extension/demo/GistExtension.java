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
package com.codenvy.ide.extension.demo;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.extension.demo.actions.CreateGistAction;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/** Extension used to demonstrate the Codenvy SDK features. */
@Singleton
@Extension(title = "GitHub Gist extension", version = "1.0.0")
public class GistExtension {

    @Inject
    public GistExtension(ActionManager actionManager,
                         GistExtensionLocalizationConstant localizationConstants,
                         CreateGistAction createGistAction) {
        // register a new action
        actionManager.registerAction(localizationConstants.createGistActionlId(), createGistAction);
        DefaultActionGroup saveActionGroup = (DefaultActionGroup)actionManager.getAction("saveGroup");
        saveActionGroup.add(createGistAction);
    }
}