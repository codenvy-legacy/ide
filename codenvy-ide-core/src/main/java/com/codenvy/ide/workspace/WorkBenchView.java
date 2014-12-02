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
package com.codenvy.ide.workspace;

import com.codenvy.ide.api.mvp.View;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;

/**
 * Perspective View contains abstract containers for PartStack
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public interface WorkBenchView extends View<WorkBenchView.ActionDelegate> {
    /**
     * Returns central panel.
     *
     * @return
     */
    AcceptsOneWidget getEditorPanel();

    /**
     * Returns left panel.
     *
     * @return
     */
    AcceptsOneWidget getNavigationPanel();

    /**
     * Returns bottom panel.
     *
     * @return
     */
    AcceptsOneWidget getInformationPanel();

    /**
     * Returns right panel.
     *
     * @return
     */
    AcceptsOneWidget getToolPanel();

    FlowPanel getLeftPanelWidget();

    FlowPanel getBottomPanelWidget();

    FlowPanel   getRightPanelWidget();

    SplitLayoutPanel getSplitPanelWidget();

    SimplePanel getNavPanelWidget();

    SimplePanel getInfoPanelWidget();

    SimplePanel getToolPanelWidget();

    /** Handle View events */
    public interface ActionDelegate {
    }
}
