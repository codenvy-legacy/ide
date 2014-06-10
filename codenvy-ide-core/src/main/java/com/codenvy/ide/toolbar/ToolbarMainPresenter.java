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
package com.codenvy.ide.toolbar;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

/**
 * Manages Toolbar items, change style.
 *
 * @author Oleksii Orel
 */
public class ToolbarMainPresenter extends ToolbarPresenter {

    private ToolbarView      view;
    private ToolbarResources res;

    @Inject
    public ToolbarMainPresenter(ToolbarView view, ToolbarResources res) {
        super(view);
        this.view = view;
        this.res = res;
    }

    @Override
    public void go(AcceptsOneWidget container) {
        view.asWidget().setStyleName(res.toolbar().toolbarMenuPanel());
        container.setWidget(view);
    }
}