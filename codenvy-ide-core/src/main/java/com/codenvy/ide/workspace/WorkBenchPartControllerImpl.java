/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.workspace;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;

/**
 * Implementation of WorkBenchPartController, used with SplitLayoutPanel as container
 * 
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class WorkBenchPartControllerImpl implements WorkBenchPartController {

    public static final int  DURATION = 800;
    private SplitLayoutPanel splitLayoutPanel;

    private SimplePanel      widget;

    public WorkBenchPartControllerImpl(SplitLayoutPanel splitLayoutPanel, SimplePanel widget) {
        this.splitLayoutPanel = splitLayoutPanel;
        this.widget = widget;
        splitLayoutPanel.setWidgetHidden(widget, true);
    }

    /** {@inheritDoc} */
    @Override
    public double getSize() {
        return splitLayoutPanel.getWidgetSize(widget);
    }

    /** {@inheritDoc} */
    @Override
    public void setSize(double size) {
        splitLayoutPanel.setWidgetSize(widget, size);
        splitLayoutPanel.animate(DURATION);
    }

    /** {@inheritDoc} */
    @Override
    public void setHidden(boolean hidden) {
        if (!hidden) {
            splitLayoutPanel.setWidgetHidden(widget, hidden);
        }
        splitLayoutPanel.setWidgetSize(widget, hidden ? 0 : getSize());
        splitLayoutPanel.animate(DURATION);
    }
}
