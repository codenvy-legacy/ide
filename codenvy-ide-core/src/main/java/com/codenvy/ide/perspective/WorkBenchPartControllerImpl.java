/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.perspective;

import com.codenvy.ide.api.ui.perspective.WorkBenchPartController;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class WorkBenchPartControllerImpl implements WorkBenchPartController {

    public static final int DURATION = 300;
    private SplitLayoutPanel splitLayoutPanel;

    private SimplePanel widget;

    public WorkBenchPartControllerImpl(SplitLayoutPanel splitLayoutPanel, SimplePanel widget) {
        this.splitLayoutPanel = splitLayoutPanel;
        this.widget = widget;
        setHidden(true);
    }

    @Override
    public double getSize() {
        return splitLayoutPanel.getWidgetSize(widget);
    }

    @Override
    public void setSize(double size) {
        splitLayoutPanel.setWidgetSize(widget, size);
        splitLayoutPanel.animate(DURATION);
    }

    @Override
    public void setHidden(boolean hidden) {
        splitLayoutPanel.setWidgetHidden(widget, hidden);
        splitLayoutPanel.animate(DURATION);
    }
}
