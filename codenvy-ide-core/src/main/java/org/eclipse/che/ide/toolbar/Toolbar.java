/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.toolbar;

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

    private FlowPanel rootPanel;
    private FlowPanel leftToolbar;
    private FlowPanel rightToolbar;

    public Toolbar() {
        rootPanel = new FlowPanel();
        rightToolbar = new FlowPanel();
        leftToolbar = new FlowPanel();
        initWidget(rootPanel);
        rootPanel.add(leftToolbar);
        rootPanel.add(rightToolbar);
        rightToolbar.addStyleName(RESOURCES.toolbar().rightPanel());
        setStyleName(RESOURCES.toolbar().toolbarPanel());
    }

    public void addToMainPanel(@Nonnull Widget widget) {
        leftToolbar.add(widget);
    }


    public void addToRightPanel(@Nonnull Widget widget) {
        rightToolbar.add(widget);
    }

    public void clearMainPanel() {
        leftToolbar.clear();
    }
    public void clearRightPanel() {
        rightToolbar.clear();
    }
}
