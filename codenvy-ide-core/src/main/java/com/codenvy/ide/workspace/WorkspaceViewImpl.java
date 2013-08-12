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

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * Implements {@link WorkspaceView}
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class WorkspaceViewImpl extends Composite implements WorkspaceView {

    interface WorspaceViewUiBinder extends UiBinder<Widget, WorkspaceViewImpl> {
    }

    private static WorspaceViewUiBinder uiBinder = GWT.create(WorspaceViewUiBinder.class);

    @UiField
    SimplePanel perspectivePanel;

    @UiField
    SimplePanel menuPanel;

    @UiField
    SimplePanel toolbarPanel;
    @UiField
    SimplePanel statusPanel;

    /** Create view. */
    @Inject
    protected WorkspaceViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    /** {@inheritDoc} */
    @Override
    public AcceptsOneWidget getMenuPanel() {
        return menuPanel;
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        // ok
        // there are no events for now
    }

    /** {@inheritDoc} */
    @Override
    public AcceptsOneWidget getPerspectivePanel() {
        return perspectivePanel;
    }

    /** {@inheritDoc} */
    @Override
    public AcceptsOneWidget getToolbarPanel() {
        return toolbarPanel;
    }
}
