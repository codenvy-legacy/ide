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
import com.codenvy.ide.ui.window.Window;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;
import java.util.Arrays;

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
    ListBox    environmentField;
    @UiField
    SuggestBox memoryField;
    @UiField
    TextArea   descriptionField;
    Button runButton;
    Button cancelButton;
    @UiField(provided = true)
    final   RunnerResources            resources;
    @UiField(provided = true)
    final   RunnerLocalizationConstant locale;
    private ActionDelegate             delegate;
    private Array<RunnerEnvironment> runnerEnvironments = Collections.createArray();

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
        createButtons();

        MultiWordSuggestOracle suggestSizes = (MultiWordSuggestOracle)memoryField.getSuggestOracle();
        String[] memorySizes = {String.valueOf(32), String.valueOf(64), String.valueOf(128), String.valueOf(256), String.valueOf(512),
                                String.valueOf(1024)};
        suggestSizes.addAll(Arrays.asList(memorySizes));
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
        return runnerEnvironments.get(environmentField.getSelectedIndex());
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

    @Override
    public void close() {
        this.hide();
    }

    @Override
    public void showDialog() {
        clear();
        this.show();
    }

    @Override
    public int getMemorySize() throws NumberFormatException{
        return Integer.parseInt(memoryField.getValue());
    }

    @Override
    public void setEnabledRunButton(boolean enabled) {
        runButton.setEnabled(enabled);
    }

    @UiHandler("memoryField")
    public void onMemorySizeChanged(KeyUpEvent event) {
        delegate.onValueChanged();
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    protected void onClose() {
        //do nothing
    }

    private void clear() {
        memoryField.setValue("");
        descriptionField.setText("");
    }
}