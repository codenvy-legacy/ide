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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * Implements {@link WorkspaceView}
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class WorkspaceViewImpl extends LayoutPanel implements WorkspaceView {
    interface WorkspaceViewUiBinder extends UiBinder<Widget, WorkspaceViewImpl> {
    }

    private static WorkspaceViewUiBinder uiBinder = GWT.create(WorkspaceViewUiBinder.class);

    @UiField
    SimpleLayoutPanel perspectivePanel;
    @UiField
    SimplePanel menuPanel;
    @UiField
    SimplePanel toolbarPanel;
    @UiField
    SimplePanel  statusPanel;
    @UiField
    Button      btnUpdate;
    ActionDelegate delegate;

    /** Create view. */
    @Inject
    protected WorkspaceViewImpl() {
        add(uiBinder.createAndBindUi(this));
        getElement().setId("codenvyIdeWorkspaceViewImpl");
    }
    
    /** {@inheritDoc} */
    @Override
    public AcceptsOneWidget getMenuPanel() {
        return menuPanel;
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
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

    /** {@inheritDoc} */
    @Override
    public AcceptsOneWidget getStatusPanel() { return statusPanel;
    }

    /** {@inheritDoc} */
    @Override
    public void setUpdateButtonVisibility(boolean visible) {
        btnUpdate.setVisible(visible);
    }

    @UiHandler("btnUpdate")
    public void onUpdateClicked(ClickEvent event) {
        delegate.onUpdateClicked();
    }
}