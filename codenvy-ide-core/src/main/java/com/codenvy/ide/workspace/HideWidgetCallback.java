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

import com.google.gwt.layout.client.Layout;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.HashSet;
import java.util.Set;

/**
 * Callback used to register part widgets to hide after hide animation completion. All parts will have time to register its
 * widget before hide animation starts because animation command is scheduled with {@link
 * com.google.gwt.core.client.Scheduler#scheduleFinally(com.google.gwt.core.client.Scheduler.ScheduledCommand)
 * Scheduler#scheduleFinally}.
 *
 * @author Kevin Pollet
 * @see com.codenvy.ide.workspace.WorkBenchPartControllerImpl#setHidden(boolean)
 */
@Singleton
public class HideWidgetCallback implements Layout.AnimationCallback {
    private final WorkBenchViewImpl workBenchView;
    private final Set<Widget>       widgetsToHide;

    @Inject
    public HideWidgetCallback(WorkBenchViewImpl workBenchView) {
        this.workBenchView = workBenchView;
        this.widgetsToHide = new HashSet<>();
    }

    /**
     * Adds a {@link com.google.gwt.user.client.ui.Widget} to hide when the animation is complete.
     *
     * @param widget
     *         the {@link com.google.gwt.user.client.ui.Widget} to hide.
     */
    public void addWidgetToHide(Widget widget) {
        widgetsToHide.add(widget);
    }

    @Override
    public void onAnimationComplete() {
        for (Widget oneWidget : widgetsToHide) {
            workBenchView.splitPanel.setWidgetHidden(oneWidget, true);
        }
        widgetsToHide.clear();
    }

    @Override
    public void onLayout(Layout.Layer layer, double progress) {
    }
}
