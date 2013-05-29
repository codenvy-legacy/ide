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
package com.codenvy.ide.toolbar;

import com.codenvy.ide.annotations.NotNull;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Toolbar is visual component, represents IDE toolbar.
 *
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class Toolbar extends Composite {
    static final ToolbarResources RESOURCES = GWT.create(ToolbarResources.class);

    static {
        RESOURCES.toolbar().ensureInjected();
    }

    private FlowPanel panel;

    public Toolbar() {
        panel = new FlowPanel();
        initWidget(panel);
        setStyleName(RESOURCES.toolbar().toolbarPanel());
    }

    public void add(@NotNull Widget widget) {
        panel.add(widget);
    }

    public void clear() {
        panel.clear();
    }
}
