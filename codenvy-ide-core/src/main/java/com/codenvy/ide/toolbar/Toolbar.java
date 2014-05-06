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
package com.codenvy.ide.toolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import javax.validation.constraints.NotNull;

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

    public void add(@NotNull Widget widget) {
        panel.add(widget);
    }

    public void clear() {
        panel.clear();
    }
}
