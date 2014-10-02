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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import javax.annotation.Nonnull;

/**
 * Toolbar is visual component, represents IDE toolbar.
 *
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class Toolbar extends Composite {
    public static final ToolbarResources RESOURCES = GWT.create(ToolbarResources.class);

    static {
        RESOURCES.toolbar().ensureInjected();
    }

    private FlowPanel panel;

    public Toolbar() {
        panel = new FlowPanel();
        initWidget(panel);
        setStyleName(RESOURCES.toolbar().toolbarPanel());
    }

    public void add(@Nonnull Widget widget) {
        panel.add(widget);
    }

    public void clear() {
        panel.clear();
    }
}
