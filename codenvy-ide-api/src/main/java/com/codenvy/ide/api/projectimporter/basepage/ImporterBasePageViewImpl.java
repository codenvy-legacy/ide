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
package com.codenvy.ide.api.projectimporter.basepage;

import elemental.events.KeyboardEvent;

import com.codenvy.ide.api.projectimporter.ProjectImporterResource;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * @author Roman Nikitenko
 */
public class ImporterBasePageViewImpl implements ImporterBasePageView {
    interface ImporterPageViewImplUiBinder extends UiBinder<DockLayoutPanel, ImporterBasePageViewImpl> {
    }

    Style       style;
    @UiField
    protected FlowPanel   importerPanel;
    @UiField
    protected Label       labelUrlError;
    @UiField
    protected HTMLPanel   descriptionArea;
    @UiField
    protected TextBox     projectName;
    @UiField
    protected TextArea    projectDescription;
    @UiField
    protected RadioButton projectPrivate;
    @UiField
    protected RadioButton projectPublic;
    @UiField
    protected TextBox     projectUrl;
    @UiField(provided = true)
    ProjectImporterResource importerResources;

    private       ActionDelegate  delegate;
    private final DockLayoutPanel rootElement;

    @Inject
    public ImporterBasePageViewImpl(ProjectImporterResource importerResources,
                                    ImporterPageViewImplUiBinder uiBinder) {
        this.importerResources = importerResources;
        rootElement = uiBinder.createAndBindUi(this);
        projectName.getElement().setAttribute("maxlength", "32");
        projectDescription.getElement().setAttribute("maxlength", "256");
        importerResources.css().ensureInjected();
    }

    @UiHandler("projectName")
    void onProjectNameChanged(KeyUpEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            return;
        }
        delegate.projectNameChanged(projectName.getValue());
    }

    @UiHandler("projectUrl")
    void onProjectUrlChanged(KeyUpEvent event) {
        delegate.projectUrlChanged(projectUrl.getValue());
    }

    @UiHandler("projectDescription")
    void onProjectDescriptionChanged(KeyUpEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            return;
        }
        delegate.projectDescriptionChanged(projectDescription.getValue());
    }

    @UiHandler({"projectDescription", "projectUrl", "projectName"})
    void onEnterClicked(KeyPressEvent event) {
        if (event.getNativeEvent().getKeyCode() == KeyboardEvent.KeyCode.ENTER) {
            delegate.onEnterClicked();
        }
    }

    @UiHandler({"projectPublic", "projectPrivate"})
    void visibilityHandler(ValueChangeEvent<Boolean> event) {
        delegate.projectVisibilityChanged(projectPublic.getValue());
    }

    @Override
    public void setProjectUrl(String url) {
        projectUrl.setText(url);
        delegate.projectUrlChanged(url);
    }

    @Override
    public void reset() {
        projectName.setText("");
        projectDescription.setText("");
        projectUrl.setText("");
        projectPublic.setValue(true);
        projectPrivate.setValue(false);
        descriptionArea.clear();
        hideUrlError();
        hideNameError();
    }

    @Override
    public Widget asWidget() {
        return rootElement;
    }

    /** {@inheritDoc} */
    @Override
    public void showNameError() {
        projectName.addStyleName(importerResources.css().inputError());
    }

    /** {@inheritDoc} */
    @Override
    public void hideNameError() {
        projectName.removeStyleName(importerResources.css().inputError());
    }

    /** {@inheritDoc} */
    @Override
    public void setImporterDescription(String text) {
        descriptionArea.getElement().setInnerText(text);
    }

    /** {@inheritDoc} */
    @Override
    public void showUrlError(String message) {
        projectUrl.addStyleName(importerResources.css().inputError());
        labelUrlError.setText(message);
    }

    /** {@inheritDoc} */
    @Override
    public void hideUrlError() {
        projectUrl.removeStyleName(importerResources.css().inputError());
        labelUrlError.setText("");
    }

    /** {@inheritDoc} */
    @Override
    public String getProjectName() {
        return projectName.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public void setProjectName(String projectName) {
        this.projectName.setValue(projectName);
        delegate.projectNameChanged(projectName);
    }

    /** {@inheritDoc} */
    @Override
    public void focusInUrlInput() {
        projectUrl.setFocus(true);
    }

    /** {@inheritDoc} */
    @Override
    public void setInputsEnableState(boolean isEnabled) {
        projectName.setEnabled(isEnabled);
        projectDescription.setEnabled(isEnabled);
        projectUrl.setEnabled(isEnabled);
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    interface Style extends CssResource {
        String mainPanel();

        String rightPart();

        String namePanel();

        String projectName();

        String projectDescription();

        String labelPosition();

        String radioButtonPosition();

        String description();

        String label();

        String horizontalLine();
    }

}
