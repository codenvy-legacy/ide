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
package com.codenvy.ide.extension.runner.client.actions;

import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.google.gwt.user.client.Window;

import org.vectomatic.dom.svg.ui.SVGResource;

/**
 * Action to executing a Docker-script as on runner.
 *
 * @author Artem Zatsarynnyy
 */
public class ImageAction extends Action {

    public ImageAction(String title, String description, SVGResource icon) {
        super(title, description, null, icon);
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        Window.alert("Executing " + getTemplatePresentation().getText() + "...");
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        e.getPresentation().setEnabledAndVisible(true);
    }
}
