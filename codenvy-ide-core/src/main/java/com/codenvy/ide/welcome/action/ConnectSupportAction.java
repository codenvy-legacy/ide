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
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The action what provides some actions when connect support item is clicked.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class ConnectSupportAction implements WelcomeItemAction {
    private WelcomeLocalizationConstant constant;
    private Resources                   resources;

    /**
     * Create action.
     *
     * @param constant
     * @param resources
     */
    @Inject
    public ConnectSupportAction(WelcomeLocalizationConstant constant, Resources resources) {
        this.constant = constant;
        this.resources = resources;
    }

    /** {@inheritDoc} */
    @Override
    public String getTitle() {
        return constant.supportTitle();
    }

    /** {@inheritDoc} */
    @Override
    public String getCaption() {
        return constant.supportText();
    }

    /** {@inheritDoc} */
    @Override
    public ImageResource getIcon() {
        return resources.support();
    }

    /** {@inheritDoc} */
    @Override
    public void execute() {
        showWidget();
    }

    /** Show connect to support dialog. */
    private static native void showWidget() /*-{
        $wnd.UserVoice.showPopupWidget();
    }-*/;
}