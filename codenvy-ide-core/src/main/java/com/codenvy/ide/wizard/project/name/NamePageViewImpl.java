/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2014] Codenvy, S.A.
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
package com.codenvy.ide.wizard.project.name;

import com.codenvy.ide.api.ui.wizard.WizardPage;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Evgen Vidolob
 */
public class NamePageViewImpl implements NamePageView {
    private static NameViewImplUiBinder ourUiBinder = GWT.create(NameViewImplUiBinder.class);
    private final DockLayoutPanel rootElement;
    private       ActionDelegate  delegate;
    @UiField
    TextBox     projectName;
    @UiField
    TextArea    projectDescription;
    @UiField
    SimplePanel subView;

    public NamePageViewImpl() {
        rootElement = ourUiBinder.createAndBindUi(this);

    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public Widget asWidget() {
        return rootElement;
    }

    @Override
    public String getProjectName() {
        return projectName.getText();
    }

    @Override
    public void addSubPage(WizardPage wizardPage) {
        wizardPage.go(subView);
    }

    @Override
    public void clearSubPage() {
        subView.clear();
    }

    @Override
    public void focusOnNameField() {
        projectName.setFocus(true);
    }

    @UiHandler("projectName")
    void onProjectNameChanged(ChangeEvent event){
        delegate.projectNameChanged(projectName.getText());
    }

    interface NameViewImplUiBinder
            extends UiBinder<DockLayoutPanel, NamePageViewImpl> {
    }
}