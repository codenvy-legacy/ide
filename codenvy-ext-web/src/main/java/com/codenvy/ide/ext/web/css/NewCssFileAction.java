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
package com.codenvy.ide.ext.web.css;

import com.codenvy.ide.ext.web.WebLocalizationConstant;
import com.codenvy.ide.newresource.AbstractNewResourceAction;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action to create new CSS file.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class NewCssFileAction extends AbstractNewResourceAction {
    private static final String DEFAULT_CONTENT = "@CHARSET \"UTF-8\";";

    @Inject
    public NewCssFileAction(WebLocalizationConstant localizationConstant) {
        super(localizationConstant.newCssFileActionTitle(),
              localizationConstant.newCssFileActionDescription(),
              null);
    }

    @Override
    protected String getExtension() {
        return "css";
    }

    @Override
    protected String getDefaultContent() {
        return DEFAULT_CONTENT;
    }
}
