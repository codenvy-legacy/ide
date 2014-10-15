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
package com.codenvy.ide.wizard.project;

import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.mvp.Presenter;
import com.codenvy.ide.ui.window.Window;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Evgen Vidolob
 * @author Oleksii Orel
 */
public class ProjectWizardViewImpl extends Window implements ProjectWizardView {
    private static ProjectWizardViewImplUiBinder ourUiBinder = GWT.create(ProjectWizardViewImplUiBinder.class);

    private final CoreLocalizationConstant coreLocalizationConstant;
    private Map<Presenter, Widget> pageCache                        = new HashMap<>();
    private HandlerRegistration    nativePreviewHandlerRegistration = null;
    private boolean        isSaveActionTitle;
    private ActionDelegate delegate;

    @UiField
    SimplePanel wizardPanel;
    @UiField
    Label       builderEnvConfText;
    @UiField
    Label       builderEnvConf;
    @UiField
    Label       runnerEnvConfText;
    @UiField
    Label       runnerEnvConf;
    @UiField
    FlowPanel   infoRAMPanel;
    @UiField
    Label       requiredRAM;
    @UiField
    Label       availableRAM;
    @UiField
    Button      nextStepButton;
    @UiField
    Button      previousStepButton;
    @UiField
    Button      saveButton;

    @Inject
    public ProjectWizardViewImpl(com.codenvy.ide.Resources resources, CoreLocalizationConstant coreLocalizationConstant) {
        super(false);
        this.coreLocalizationConstant = coreLocalizationConstant;
        setTitle(coreLocalizationConstant.projectWizardDefaultTitleText());
        isSaveActionTitle = false;
        setWidget(ourUiBinder.createAndBindUi(this));
        nextStepButton.sinkEvents(Event.ONCLICK);
        previousStepButton.sinkEvents(Event.ONCLICK);
        nextStepButton.addHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onNextClicked();
            }
        }, ClickEvent.getType());
        previousStepButton.addHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onBackClicked();
            }
        }, ClickEvent.getType());
        saveButton.addStyleName(resources.Css().buttonLoader());
        saveButton.setText(coreLocalizationConstant.projectWizardDefaultSaveButtonText());
        builderEnvConfText.setVisible(false);
        builderEnvConf.setVisible(false);
        runnerEnvConfText.setVisible(false);
        runnerEnvConf.setVisible(false);
        this.ensureDebugId("projectWizard-window");
    }

    @UiHandler("saveButton")
    void saveClick(ClickEvent event) {
        delegate.onSaveClicked();
    }

    @Override
    public void setSaveActionTitle(boolean isSaveActionTitle) {
        this.isSaveActionTitle = isSaveActionTitle;
        if (isSaveActionTitle) {
            setTitle(coreLocalizationConstant.projectWizardTitleText());
            saveButton.setText(coreLocalizationConstant.projectWizardSaveButtonText());
        } else {
            setTitle(coreLocalizationConstant.projectWizardDefaultTitleText());
            saveButton.setText(coreLocalizationConstant.projectWizardDefaultSaveButtonText());
        }
    }

    public void setLoaderVisible(boolean enabled) {
        if (enabled) {
            saveButton.setHTML("<i></i>");
            saveButton.setEnabled(false);
        } else {
            if (isSaveActionTitle) {
                saveButton.setText(coreLocalizationConstant.projectWizardSaveButtonText());
            } else {
                saveButton.setText(coreLocalizationConstant.projectWizardDefaultSaveButtonText());
            }
            saveButton.setEnabled(true);
        }
    }

    @Override
    public void setRAMRequired(String amountOfRAM) {
        if (amountOfRAM == null) return;
        requiredRAM.setText(amountOfRAM);
    }

    @Override
    public void setRAMAvailable(String amountOfRAM) {
        if (amountOfRAM == null) return;
        availableRAM.setText(amountOfRAM);
    }

    @Override
    public void setBuilderEnvironmentConfig(String text) {
        if (text == null) {
            if (builderEnvConfText.isVisible()) builderEnvConfText.setVisible(false);
            if (builderEnvConf.isVisible()) builderEnvConf.setVisible(false);
        } else {
            if (!builderEnvConfText.isVisible()) builderEnvConfText.setVisible(true);
            if (!builderEnvConf.isVisible()) builderEnvConf.setVisible(true);
            builderEnvConf.setText(text);
        }
    }

    @Override
    public void setRunnerEnvironmentConfig(String text) {
        if (text == null) {
            if (runnerEnvConfText.isVisible()) runnerEnvConfText.setVisible(false);
            if (runnerEnvConf.isVisible()) runnerEnvConf.setVisible(false);
        } else {
            if (!runnerEnvConfText.isVisible()) runnerEnvConfText.setVisible(true);
            if (!runnerEnvConf.isVisible()) runnerEnvConf.setVisible(true);
            runnerEnvConf.setText(text);
        }
    }

    @Override
    public void setInfoVisible(boolean enabled) {
        if (enabled) {
            infoRAMPanel.getElement().getStyle().setVisibility(Visibility.VISIBLE);
        } else {
            infoRAMPanel.getElement().getStyle().setVisibility(Visibility.HIDDEN);
        }
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

        if (nativePreviewHandlerRegistration == null) {
            nativePreviewHandlerRegistration = Event.addNativePreviewHandler(new Event.NativePreviewHandler() {
                @Override
                public void onPreviewNativeEvent(Event.NativePreviewEvent event) {
                    if (event.getTypeInt() == Event.ONKEYUP &&
                        event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
                        if (nextStepButton.isEnabled()) {
                            delegate.onNextClicked();
                        } else if (saveButton.isEnabled()) {
                            delegate.onSaveClicked();
                        }
                    }
                }
            });
        }
    }

    @Override
    public void setEnabledAnimation(boolean enabled) {

    }

    @Override
    public void close() {
        if (nativePreviewHandlerRegistration != null) {
            nativePreviewHandlerRegistration.removeHandler();
            nativePreviewHandlerRegistration = null;
        }

        hide();
        pageCache.clear();
    }

    @Override
    public void setNextButtonEnabled(boolean enabled) {
        nextStepButton.setEnabled(enabled);
    }

    @Override
    public void setFinishButtonEnabled(boolean enabled) {
        saveButton.setEnabled(enabled);
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

    interface ProjectWizardViewImplUiBinder
            extends UiBinder<FlowPanel, ProjectWizardViewImpl> {
    }
}