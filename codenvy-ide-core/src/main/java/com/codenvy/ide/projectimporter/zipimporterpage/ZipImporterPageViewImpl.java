/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.projectimporter.zipimporterpage;

import com.codenvy.ide.projectimporter.ZipProjectImporterResource;
import com.codenvy.ide.ui.Styles;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.inject.Inject;

import javax.annotation.Nonnull;

/**
 * @author Roman Nikitenko
 */
public class ZipImporterPageViewImpl extends Composite implements ZipImporterPageView {
    interface ZipImporterPageViewImplUiBinder extends UiBinder<DockLayoutPanel, ZipImporterPageViewImpl> {
    }

    private ActionDelegate delegate;

    @UiField(provided = true)
    Style       style;
    @UiField
    Label       labelUrlError;
    @UiField
    HTMLPanel   descriptionArea;
    @UiField
    TextBox     projectName;
    @UiField
    TextArea    projectDescription;
    @UiField
    RadioButton projectPrivate;
    @UiField
    RadioButton projectPublic;
    @UiField
    TextBox     projectUrl;
    @UiField
    CheckBox    skipFirstLevel;

    @Inject
    public ZipImporterPageViewImpl(ZipProjectImporterResource resource,
                                   ZipImporterPageViewImplUiBinder uiBinder) {
        style = resource.zipImporterPageStyle();
        style.ensureInjected();
        initWidget(uiBinder.createAndBindUi(this));
        projectName.getElement().setAttribute("maxlength", "32");
        projectDescription.getElement().setAttribute("maxlength", "256");
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

    @UiHandler({"projectPublic", "projectPrivate"})
    void visibilityHandler(ValueChangeEvent<Boolean> event) {
        delegate.projectVisibilityChanged(projectPublic.getValue());
    }

    @UiHandler({"skipFirstLevel"})
    void skipFirstLevelHandler(ValueChangeEvent<Boolean> event) {
        delegate.skipFirstLevelChanged(skipFirstLevel.getValue());
    }

    @Override
    public void setProjectUrl(@Nonnull String url) {
        projectUrl.setText(url);
        delegate.projectUrlChanged(url);
    }

    @Override
    public void reset() {
        projectUrl.setText("");
        projectName.setText("");
        projectDescription.setText("");
        descriptionArea.clear();
        projectPublic.setValue(true);
        projectPrivate.setValue(false);
        skipFirstLevel.setValue(false);
        hideUrlError();
        hideNameError();
    }

    @Override
    public void showNameError() {
        projectName.addStyleName(style.inputError());
    }

    @Override
    public void hideNameError() {
        projectName.removeStyleName(style.inputError());
    }

    @Override
    public void setImporterDescription(@Nonnull String text) {
        descriptionArea.getElement().setInnerText(text);
    }

    @Override
    public void showUrlError(@Nonnull String message) {
        projectUrl.addStyleName(style.inputError());
        labelUrlError.setText(message);
    }

    @Override
    public void hideUrlError() {
        projectUrl.removeStyleName(style.inputError());
        labelUrlError.setText("");
    }

    @Nonnull
    @Override
    public String getProjectName() {
        return projectName.getValue();
    }

    @Override
    public void setProjectName(@Nonnull String projectName) {
        this.projectName.setValue(projectName);
        delegate.projectNameChanged(projectName);
    }

    @Override
    public void focusInUrlInput() {
        projectUrl.setFocus(true);
    }

    @Override
    public void setInputsEnableState(boolean isEnabled) {
        projectName.setEnabled(isEnabled);
        projectDescription.setEnabled(isEnabled);
        projectUrl.setEnabled(isEnabled);

        if (isEnabled) {
            focusInUrlInput();
        }
    }

    @Override
    public boolean isSkipFirstLevelSelected() {
        return skipFirstLevel.getValue();
    }

    public void setDelegate(@Nonnull ActionDelegate delegate) {
        this.delegate = delegate;
    }

    public interface Style extends Styles {
        String mainPanel();

        String namePanel();

        String labelPosition();

        String marginTop();

        String alignRight();

        String alignLeft();

        String labelErrorPosition();

        String radioButtonPosition();

        String description();

        String label();

        String horizontalLine();

        String checkBoxPosition();
    }
}
