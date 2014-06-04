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
package com.codenvy.ide.welcome.action;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.parts.WelcomeItemAction;
import com.codenvy.ide.welcome.WelcomeLocalizationConstant;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The action what provides some actions when documentation item is clicked.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class ShowDocumentationAction implements WelcomeItemAction {
    private WelcomeLocalizationConstant constant;
    private Resources                   resources;

    /**
     * Create action.
     *
     * @param constant
     * @param resources
     */
    @Inject
    public ShowDocumentationAction(WelcomeLocalizationConstant constant, Resources resources) {
        this.constant = constant;
        this.resources = resources;
    }

    /** {@inheritDoc} */
    @Override
    public String getTitle() {
        return constant.documentationTitle();
    }

    /** {@inheritDoc} */
    @Override
    public String getCaption() {
        return constant.documentationText();
    }

    /** {@inheritDoc} */
    @Override
    public ImageResource getIcon() {
        return resources.documentation();
    }

    /** {@inheritDoc} */
    @Override
    public void execute() {
        Window.open("http://docs.codenvy.com", "_blank", "");
    }
}