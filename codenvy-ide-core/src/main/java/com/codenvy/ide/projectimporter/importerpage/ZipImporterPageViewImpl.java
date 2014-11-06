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
package com.codenvy.ide.projectimporter.importerpage;

import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.projectimporter.basepage.ImporterBasePageView;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * @author Roman Nikitenko
 */
public class ZipImporterPageViewImpl extends Composite implements ZipImporterPageView {
    interface ZipImporterPageViewImplUiBinder extends UiBinder<DockLayoutPanel, ZipImporterPageViewImpl> {
    }

    private ImporterBasePageView importerBasePageView;
    private ActionDelegate       delegate;

    @UiField
    FlowPanel basePagePanel;
    @UiField
    CheckBox  skipFirstLevel;
    @UiField(provided = true)
    final CoreLocalizationConstant locale;

    @Inject
    public ZipImporterPageViewImpl(ImporterBasePageView importerBasePageView,
                                   ZipImporterPageViewImplUiBinder uiBinder,
                                   CoreLocalizationConstant locale) {
        this.importerBasePageView = importerBasePageView;
        this.locale = locale;
        initWidget(uiBinder.createAndBindUi(this));
        basePagePanel.add(importerBasePageView);
    }

    @Override
    public void setDelegate(ImporterBasePageView.ActionDelegate delegate) {
        importerBasePageView.setDelegate(delegate);
    }

    @Override
    public boolean isSkipFirstLevelSelected() {
        return skipFirstLevel.getValue();
    }

    @UiHandler({"skipFirstLevel"})
    void skipFirstLevelHandler(ValueChangeEvent<Boolean> event) {
        delegate.skipFirstLevelChanged(skipFirstLevel.getValue());
    }

    @Override
    public void setProjectUrl(String url) {
        importerBasePageView.setProjectUrl(url);
    }

    @Override
    public void reset() {
        importerBasePageView.reset();
        skipFirstLevel.setValue(false);
    }

    @Override
    public void showNameError() {
        importerBasePageView.showNameError();
    }

    @Override
    public void hideNameError() {
        importerBasePageView.hideNameError();
    }

    @Override
    public void showUrlError(String message) {
        importerBasePageView.showUrlError(message);
    }

    @Override
    public void hideUrlError() {
        importerBasePageView.hideUrlError();
    }

    @Override
    public void setImporterDescription(String text) {
        importerBasePageView.setImporterDescription(text);
    }

    @Override
    public String getProjectName() {
        return importerBasePageView.getProjectName();
    }

    @Override
    public void setProjectName(String projectName) {
        importerBasePageView.setProjectName(projectName);
    }

    @Override
    public void focusInUrlInput() {
        importerBasePageView.focusInUrlInput();
    }

    @Override
    public void setInputsEnableState(boolean isEnabled) {
        importerBasePageView.setInputsEnableState(isEnabled);
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        importerBasePageView.setDelegate(delegate);
        this.delegate = delegate;
    }

    @Override
    public Widget asWidget() {
        return this;
    }
}
