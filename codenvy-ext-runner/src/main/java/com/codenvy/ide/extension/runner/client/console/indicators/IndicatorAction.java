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
package com.codenvy.ide.extension.runner.client.console.indicators;

import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.api.ui.action.CustomComponentAction;
import com.codenvy.ide.api.ui.action.Presentation;
import com.codenvy.ide.extension.runner.client.RunnerResources;
import com.google.gwt.user.client.ui.Widget;

/**
 * Action used to displaying some information.
 *
 * @author Artem Zatsarynnyy
 */
public class IndicatorAction extends Action implements CustomComponentAction {
    private String          caption;
    private boolean         isURL;
    private RunnerResources resources;

    public IndicatorAction(String caption, boolean isURL, RunnerResources resources) {
        super();
        this.caption = caption;
        this.isURL = isURL;
        this.resources = resources;
    }

    @Override
    public Widget createCustomComponent(Presentation presentation) {
        return new IndicatorView(caption, isURL, presentation, resources);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // no need to process any action, for now
    }

}
