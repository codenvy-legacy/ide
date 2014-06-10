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
package com.codenvy.ide.extension.builder.client.console;

import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.api.ui.action.CustomComponentAction;
import com.codenvy.ide.api.ui.action.Presentation;
import com.codenvy.ide.extension.builder.client.BuilderResources;
import com.google.gwt.user.client.ui.Widget;

/**
 * Action used to show some information in console's toolbar.
 *
 * @author Artem Zatsarynnyy
 */
public class InfoAction extends Action implements CustomComponentAction {
    private String  caption;
    private boolean isURL;
    private BuilderResources resources;

    public InfoAction(String caption, boolean isURL, BuilderResources resources) {
        super();
        this.caption = caption;
        this.isURL = isURL;
        this.resources = resources;
    }

    @Override
    public Widget createCustomComponent(Presentation presentation) {
        return new InfoLabel(caption, isURL, presentation, resources);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // no need to process any action, for now
    }

}
