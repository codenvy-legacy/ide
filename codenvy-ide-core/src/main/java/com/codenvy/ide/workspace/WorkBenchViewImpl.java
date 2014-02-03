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

import com.codenvy.ide.api.ui.workspace.WorkBenchView;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * General-purpose Perspective View
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class WorkBenchViewImpl extends Composite implements WorkBenchView<WorkBenchView.ActionDelegate> {

    private static GenericPerspectiveViewUiBinder uiBinder = GWT.create(GenericPerspectiveViewUiBinder.class);

    interface GenericPerspectiveViewUiBinder extends UiBinder<Widget, WorkBenchViewImpl> {
    }

    @UiField(provided = true)
    SplitLayoutPanel splitPanel = new SplitLayoutPanel(6);

    @UiField
    SimplePanel editorPanel;

    @UiField
    SimplePanel navPanel;

    @UiField
    SimplePanel infoPanel;

    @UiField
    SimplePanel toolPanel;
    @UiField
    FlowPanel   rightPanel;
    @UiField
    FlowPanel   leftPanel;
    @UiField
    FlowPanel   bottomPanel;

    @UiField(provided = true)
    final WorkBenchResources res;

    @Inject
    public WorkBenchViewImpl(WorkBenchResources resources) {
        this.res = resources;
        resources.workBenchCss().ensureInjected();
        initWidget(uiBinder.createAndBindUi(this));
        splitPanel.getElement().setId("codenvyIdeWorkbench");
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        // do nothing
    }

    /** {@inheritDoc} */
    @Override
    public AcceptsOneWidget getEditorPanel() {
        return editorPanel;
    }

    /** {@inheritDoc} */
    @Override
    public AcceptsOneWidget getNavigationPanel() {
        return navPanel;
    }

    /** {@inheritDoc} */
    @Override
    public AcceptsOneWidget getInformationPanel() {
        return infoPanel;
    }

    /** {@inheritDoc} */
    @Override
    public AcceptsOneWidget getToolPanel() {
        return toolPanel;
    }

}
