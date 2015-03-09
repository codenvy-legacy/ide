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
package org.eclipse.che.ide.projecttype.wizard.presenter;

import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.api.mvp.Presenter;
import org.eclipse.che.ide.api.project.type.wizard.ProjectWizardMode;
import org.eclipse.che.ide.ui.window.Window;
import com.google.gwt.core.client.GWT;
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

import javax.annotation.Nullable;

import static org.eclipse.che.ide.api.project.type.wizard.ProjectWizardMode.CREATE;
import static org.eclipse.che.ide.api.project.type.wizard.ProjectWizardMode.CREATE_MODULE;
import static org.eclipse.che.ide.api.project.type.wizard.ProjectWizardMode.UPDATE;

/**
 * @author Evgen Vidolob
 * @author Oleksii Orel
 */
public class ProjectWizardViewImpl extends Window implements ProjectWizardView {
    private static ProjectWizardViewImplUiBinder ourUiBinder = GWT.create(ProjectWizardViewImplUiBinder.class);

    private final CoreLocalizationConstant coreLocalizationConstant;
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
    Button      nextStepButton;
    @UiField
    Button      previousStepButton;
    @UiField
    Button      saveButton;

    private HandlerRegistration nativePreviewHandlerRegistration = null;
    private boolean        isCreatingNewProject;
    private ActionDelegate delegate;

    @Inject
    public ProjectWizardViewImpl(org.eclipse.che.ide.Resources resources, CoreLocalizationConstant coreLocalizationConstant) {
        super(false);
        this.coreLocalizationConstant = coreLocalizationConstant;
        setTitle(coreLocalizationConstant.projectWizardDefaultTitleText());
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
    public void setLoaderVisibility(boolean visible) {
        if (visible) {
            saveButton.setHTML("<i></i>");
            saveButton.setEnabled(false);
        } else {
            if (isCreatingNewProject) {
                saveButton.setText(coreLocalizationConstant.projectWizardDefaultSaveButtonText());
            } else {
                saveButton.setText(coreLocalizationConstant.projectWizardSaveButtonText());
            }
            saveButton.setEnabled(true);
        }
    }

    @Override
    public void setBuilderEnvironmentConfig(@Nullable String text) {
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
    public void showPage(Presenter presenter) {
        presenter.go(wizardPanel);
    }

    @Override
    public void showDialog(ProjectWizardMode wizardMode) {
        this.isCreatingNewProject = wizardMode == CREATE;

        if (wizardMode == CREATE) {
            setTitle(coreLocalizationConstant.projectWizardDefaultTitleText());
            saveButton.setText(coreLocalizationConstant.projectWizardDefaultSaveButtonText());
        } else if (wizardMode == UPDATE) {
            setTitle(coreLocalizationConstant.projectWizardTitleText());
            saveButton.setText(coreLocalizationConstant.projectWizardSaveButtonText());
        } else if (wizardMode == CREATE_MODULE) {
            setTitle(coreLocalizationConstant.projectWizardCreateModuleTitleText());
            saveButton.setText(coreLocalizationConstant.projectWizardDefaultSaveButtonText());
        }

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
    public void close() {
        if (nativePreviewHandlerRegistration != null) {
            nativePreviewHandlerRegistration.removeHandler();
            nativePreviewHandlerRegistration = null;
        }

        hide();
        setLoaderVisibility(false);
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
    public void setPreviousButtonEnabled(boolean enabled) {
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
        delegate.onCancelClicked();
    }

    interface ProjectWizardViewImplUiBinder extends UiBinder<FlowPanel, ProjectWizardViewImpl> {
    }
}