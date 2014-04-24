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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
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
    ListBox  environmentField;
    @UiField
    TextArea descriptionField;
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
        environmentField.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                RunnerEnvironment environment = runnerEnvironments.get(environmentField.getSelectedIndex());
                descriptionField.setText(environment.getDescription());
            }
        });
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
        this.show();
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    protected void onClose() {
        //do nothing
    }
}