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
package com.codenvy.ide.extension.runner.client.run;

import com.codenvy.api.runner.dto.RunnerEnvironment;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.extension.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.extension.runner.client.RunnerResources;
import com.codenvy.ide.ui.dialogs.info.Info;
import com.codenvy.ide.ui.window.Window;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

/**
 * The implementation of {@link CustomRunView}.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class CustomRunViewImpl extends Window implements CustomRunView {
    interface CustomRunViewImplUiBinder extends UiBinder<Widget, CustomRunViewImpl> {
    }

    @UiField
    ListBox         environmentField;
    @UiField
    TextBox         memoryTotal;
    @UiField
    TextBox         memoryAvailable;
    @UiField
    CheckBox        skipBuild;
    @UiField
    TextArea        descriptionField;
    @UiField
    RadioButton     radioButOther;
    @UiField
    TextBox         otherValueMemory;
    @UiField
    HorizontalPanel memoryPanel1;
    @UiField
    HorizontalPanel memoryPanel2;
    Button runButton;
    Button cancelButton;
    @UiField(provided = true)
    final   RunnerResources            resources;
    @UiField(provided = true)
    final   RunnerLocalizationConstant locale;
    private ActionDelegate             delegate;
    private Array<RunnerEnvironment> runnerEnvironments = Collections.createArray();
    private Array<RadioButton>       radioButtons       = Collections.createArray();

    /** Create view. */
    @Inject
    protected CustomRunViewImpl(RunnerResources resources, RunnerLocalizationConstant constant, CustomRunViewImplUiBinder uiBinder) {
        this.resources = resources;
        this.locale = constant;
        setTitle(constant.runConfigurationViewTitle());
        setWidget(uiBinder.createAndBindUi(this));
        ensureDebugId("customRun-window");

        environmentField.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                RunnerEnvironment environment = runnerEnvironments.get(environmentField.getSelectedIndex());
                descriptionField.setText(environment.getDescription());
            }
        });
        for (int i = 0; i < memoryPanel1.getWidgetCount(); i++) {
            radioButtons.add((RadioButton)memoryPanel1.getWidget(i));
            radioButtons.add((RadioButton)memoryPanel2.getWidget(i));
        }
        createButtons();
    }

    private void createButtons() {
        runButton = createButton(locale.buttonRun(), "project-customRun-run", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onRunClicked();
            }
        });

        cancelButton = createButton(locale.buttonCancel(), "project-customRun-cancel", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onCancelClicked();
            }
        });

        getFooter().add(cancelButton);
        getFooter().add(runButton);
    }

    @NotNull
    @Override
    public RunnerEnvironment getSelectedEnvironment() {
        int selectedIndex = environmentField.getSelectedIndex();
        if (selectedIndex != -1) {
            return runnerEnvironments.get(selectedIndex);
        }
        return null;
    }

    @Override
    public void setEnvironments(@NotNull Array<RunnerEnvironment> environments) {
        runnerEnvironments.clear();
        runnerEnvironments.addAll(environments);
        environmentField.clear();
        for (RunnerEnvironment environment : environments.asIterable()) {
            environmentField.addItem(environment.getId());
        }
        if (environments.size() > 0) {
            descriptionField.setText(environments.get(0).getDescription());
        }
    }

    @UiHandler({"runnerMemory128", "runnerMemory256", "runnerMemory512", "runnerMemory1GB", "runnerMemory2GB"})
    void standardMemoryHandler(ValueChangeEvent<Boolean> event) {
        otherValueMemory.setText("");
    }

    @UiHandler({"otherValueMemory"})
    void otherMemoryHandler(KeyUpEvent event) {
        clearStandardMemoryFilds();
        radioButOther.setValue(true);
    }

    @Override
    public void setRunnerMemorySize(String memorySize) {
        clearStandardMemoryFilds();
        otherValueMemory.setText("");

        int index;
        switch (memorySize) {
            case "128":
                index = 0;
                break;
            case "1024":
                index = 1;
                break;
            case "256":
                index = 2;
                break;
            case "2048":
                index = 3;
                break;
            case "512":
                index = 4;
                break;
            default:
                index = 5; //index = 5 corresponds to 'Other'- radioButton
                otherValueMemory.setText(memorySize);
                break;
        }
        radioButtons.get(index).setValue(true);
    }

    @Override
    public String getRunnerMemorySize() {
        for (RadioButton radioButton : radioButtons.asIterable()) {
            if (radioButton.getValue()) {
                return parseRadioButMemoryValue(radioButton.getText());
            }
        }
        return "";
    }

    @Override
    public void setTotalMemorySize(String memorySize) {
        this.memoryTotal.setText(memorySize);
    }

    @Override
    public String getTotalMemorySize() {
        return memoryTotal.getText();
    }

    @Override
    public void setAvailableMemorySize(String memorySize) {
        this.memoryAvailable.setText(memorySize);
    }

    @Override
    public String getAvailableMemorySize() {
        return memoryAvailable.getText();
    }

    @Override
    public void close() {
        clear();
        this.hide();
    }

    @Override
    public void showDialog() {
        this.show();
    }

    @Override
    public boolean isSkipBuildSelected() {
        return skipBuild.getValue();
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    protected void onClose() {
        //do nothing
    }

    private String parseRadioButMemoryValue(String memory) {
        switch (memory) {
            case "128MB":
                return "128";
            case "256MB":
                return "256";
            case "512MB":
                return "512";
            case "1GB":
                return "1024";
            case "2GB":
                return "2048";
            case "Other (MB):":
                return otherValueMemory.getText();
            default:
                return "256";
        }
    }

    private void clear() {
        descriptionField.setText("");
        skipBuild.setValue(false);
    }

    private void clearStandardMemoryFilds() {
        for (RadioButton radioButton : radioButtons.asIterable()) {
            radioButton.setValue(false);
        }
    }

    @Override
    public void showWarning(String warning) {
        Info warningWindow = new Info("Warning", warning);
        warningWindow.show();
    }
}