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
package com.codenvy.ide.ext.web.js;

import com.codenvy.ide.ext.web.WebExtensionResource;
import com.codenvy.ide.ext.web.WebLocalizationConstant;
import com.codenvy.ide.newresource.AbstractNewResourceAction;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action to create new JavaScript file.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class NewJavaScriptFileAction extends AbstractNewResourceAction {

    @Inject
    public NewJavaScriptFileAction(WebExtensionResource webExtensionResource, WebLocalizationConstant localizationConstant) {
        super(localizationConstant.newJavaScriptFileActionTitle(),
              localizationConstant.newJavaScriptFileActionDescription(),
              webExtensionResource.js(),
              null);
    }

    @Override
    protected String getExtension() {
        return "js";
    }
}
