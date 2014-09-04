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
package com.codenvy.ide.wizard.project.importproject;

import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.mvp.Presenter;
import com.codenvy.ide.ui.window.Window;
import com.codenvy.ide.wizard.project.ProjectWizardResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import java.util.HashMap;
import java.util.Map;

/**
 * UI implementation for {@link ImportProjectWizardView}.
 * 
 * @author Ann Shumilova
 */
public class ImportProjectWizardViewImpl extends Window implements ImportProjectWizardView {
    private static ImportProjectWizardViewImplUiBinder uiBinder  = GWT.create(ImportProjectWizardViewImplUiBinder.class);


    @UiField
    SimplePanel                                        wizardPanel;
    @UiField
    Button                                             nextStepButton;
    @UiField
    Button                                             previousStepButton;
    @UiField
    Button                                             importButton;
    @UiField(provided = true)
    final ProjectWizardResources                       wizardResources;
    @UiField(provided = true)
    final CoreLocalizationConstant                     locale;


    private ActionDelegate                             delegate;
    private Map<Presenter, Widget>                     pageCache = new HashMap<>();


    @Inject
    public ImportProjectWizardViewImpl(com.codenvy.ide.Resources resources, ProjectWizardResources wizardResources, CoreLocalizationConstant locale) {
        super(false);
        this.wizardResources = wizardResources;
        this.locale = locale;
        wizardResources.css().ensureInjected();
        setTitle(locale.importProjectViewTitle());
        setWidget(uiBinder.createAndBindUi(this));
        importButton.addStyleName(resources.Css().buttonLoader());
        nextStepButton.sinkEvents(Event.ONCLICK);
        previousStepButton.sinkEvents(Event.ONCLICK);
    }

    @UiHandler("importButton")
    void saveClick(ClickEvent event) {
        delegate.onImportClicked();
    }
    
    @UiHandler("nextStepButton")
    void nextStepClick(ClickEvent event) {
        delegate.onNextClicked();
    }
    
    @UiHandler("previousStepButton")
    void previousStepClick(ClickEvent event) {
        delegate.onBackClicked();
    }

    @Override
    public void showPage(Presenter presenter) {
        wizardPanel.clear();
        if (pageCache.containsKey(presenter)) {
            wizardPanel.add(pageCache.get(presenter));
        } else {
            presenter.go(wizardPanel);
            pageCache.put(presenter, wizardPanel.getWidget());
        }
    }

    @Override
    public void showDialog() {
        show();
    }

    @Override
    public void close() {
        hide();
        pageCache.clear();
    }

    @Override
    public void setNextButtonEnabled(boolean enabled) {
        nextStepButton.setEnabled(enabled);
    }

    @Override
    public void setImportButtonEnabled(boolean enabled) {
        importButton.setEnabled(enabled);
    }

    @Override
    public void setBackButtonEnabled(boolean enabled) {
        previousStepButton.setEnabled(enabled);
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public Widget asWidget() {
        return null;
    }

    @Override
    protected void onClose() {
        pageCache.clear();
        delegate.onCancelClicked();
    }

    interface ImportProjectWizardViewImplUiBinder
                                                 extends UiBinder<FlowPanel, ImportProjectWizardViewImpl> {
    }

    /** {@inheritDoc} */
    @Override
    public void setLoaderVisibility(boolean isVisible) {
        if (isVisible) {
            importButton.setHTML("<i></i>");
            importButton.setEnabled(false);
        } else {
            importButton.setText(locale.importProjectButton());
            importButton.setEnabled(true);
        }
    }
}
