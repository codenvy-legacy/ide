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

package com.codenvy.ide.extension.runner.client.wizard;

import com.codenvy.api.runner.dto.RunnerEnvironment;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.extension.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.extension.runner.client.RunnerResources;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import java.util.Map;
import java.util.Set;

/**
 * @author Evgen Vidolob
 */
public class SelectRunnerPageViewImpl implements SelectRunnerPageView {
    interface SelectRunnerViewImplUiBinder
                                          extends UiBinder<DockLayoutPanel, SelectRunnerPageViewImpl> {
    }

    private final DockLayoutPanel    rootElement;
    @UiField
    ListBox                          technologyBox;
    @UiField
    ListBox                          environmentBox;
    @UiField
    TextBox                          recommendedMemory;
    @UiField
    RadioButton                      consoleTarget, standaloneTarget, webAppTarget, mobileTarget;
    @UiField
    FlowPanel                        subTechPlaceHolder;
    @UiField
    Label                            targetLabel, subTechLabel;

    @UiField(provided = true)
    final RunnerResources            resources;
    @UiField(provided = true)
    final RunnerLocalizationConstant locale;

    private ActionDelegate           delegate;
    private Array<String>            technologyNames    = Collections.createArray();
    private Array<RunnerEnvironment> runnerEnvironments = Collections.createArray();


    @Inject
    public SelectRunnerPageViewImpl(RunnerResources resources, RunnerLocalizationConstant locale, SelectRunnerViewImplUiBinder uiBinder) {
        this.resources = resources;
        this.locale = locale;
        rootElement = uiBinder.createAndBindUi(this);
    }

    @UiHandler("technologyBox")
    void techChanged(ChangeEvent event) {
        String value = technologyBox.getValue(technologyBox.getSelectedIndex());
        delegate.technologySelected(value);
    }

    @UiHandler("environmentBox")
    void environmentChanged(ChangeEvent event) {
        delegate.runnerEnvironmentSelected(environmentBox.getValue(environmentBox.getSelectedIndex()));
    }

    @UiHandler("recommendedMemory")
    void recommendedMemoryChanged(KeyUpEvent event) {
        delegate.recommendedMemoryChanged();
    }

    @UiHandler({"consoleTarget", "standaloneTarget", "webAppTarget", "mobileTarget"})
    void visibilityHandler(ValueChangeEvent<Boolean> event) {
        Target target = null;
        target =
                 consoleTarget.getValue() ? Target.CONSOLE : (standaloneTarget.getValue() ? Target.STANDALONE : (webAppTarget.getValue()
                     ? Target.WEBAPP
                     : (mobileTarget.getValue() ? Target.MOBILE : target)));
        delegate.targetSelected(target);
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
    public void setSelectedEnvironment(String environmentName) {
        if (environmentName == null) {
            // defaultRunnerEnvironment == null => return selected environment
            delegate.runnerEnvironmentSelected(environmentBox.getValue(environmentBox.getSelectedIndex()));
        }
        for (RunnerEnvironment environment : runnerEnvironments.asIterable()) {
            if (environmentName.equals(environment.getDisplayName()) || environmentName.equals(environment.getId())) {
                environmentBox.setSelectedIndex(runnerEnvironments.indexOf(environment));
                delegate.runnerEnvironmentSelected(environmentName);
                return;
            }
        }
        delegate.runnerEnvironmentSelected(environmentBox.getValue(environmentBox.getSelectedIndex()));
    }

    @Override
    public void setRecommendedMemorySize(String recommendedRam) {
        recommendedMemory.setText(recommendedRam);
    }

    @Override
    public String getRecommendedMemorySize() {
        return recommendedMemory.getText();
    }


    /** {@inheritDoc} */
    @Override
    public void showTechnologies(Array<String> values) {
        technologyNames = values;

        for (String value : values.asIterable()) {
            technologyBox.addItem(firstLetterToUpperCase(value), value);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void selectTechnology(String technology) {
        technologyBox.setSelectedIndex(technologyNames.indexOf(technology));
        techChanged(null);
    }

    /** {@inheritDoc} */
    @Override
    public void displayTargets(Array<Target> targets) {
        targetLabel.setVisible(targets != null && targets.size() > 0);

        for (Target target : targets.asIterable()) {
            if (target.equals(Target.CONSOLE)) {
                consoleTarget.setVisible(true);
            } else if (target.equals(Target.MOBILE)) {
                mobileTarget.setVisible(true);
            } else if (target.equals(Target.STANDALONE)) {
                standaloneTarget.setVisible(true);
            } else if (target.equals(Target.WEBAPP)) {
                webAppTarget.setVisible(true);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void hideTargets() {
        targetLabel.setVisible(false);
        webAppTarget.setVisible(false);
        webAppTarget.setValue(false);
        consoleTarget.setVisible(false);
        consoleTarget.setValue(false);
        mobileTarget.setVisible(false);
        mobileTarget.setValue(false);
        standaloneTarget.setVisible(false);
        standaloneTarget.setValue(false);
    }

    /** {@inheritDoc} */
    @Override
    public String getSelectedTechnology() {
        return technologyBox.getValue(technologyBox.getSelectedIndex());
    }

    /** {@inheritDoc} */
    @Override
    public void showSubTechnologies(Set<String> values) {
        subTechLabel.setVisible(values != null && values.size() > 0);

        subTechPlaceHolder.clear();
        for (final String value : values) {
            RadioButton radioButton = new RadioButton("subtech", firstLetterToUpperCase(value));
            radioButton.addStyleName(resources.runner().radioButton());
            radioButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    if (event.getValue()) {
                        delegate.subTechnologySelected(value);
                    }
                }
            });
            subTechPlaceHolder.add(radioButton);
        }

        if (values.size() == 1) {
            ((RadioButton)subTechPlaceHolder.getWidget(0)).setValue(true, true);
        }
    }

    /**
     * Make the first letter of the given string capitalized.
     * 
     * @param str string to capitalize first letter
     * @return {@link String} string with first letter - capitalized
     */
    private String firstLetterToUpperCase(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        return str.length() > 1
            ? Character.toUpperCase(str.charAt(0))
              +
              str.substring(1)
            : str.toUpperCase();
    }

    /** {@inheritDoc} */
    @Override
    public void hideSubTechnologies() {
        subTechPlaceHolder.clear();
        subTechLabel.setVisible(false);
    }

    /** {@inheritDoc} */
    @Override
    public void selectTarget(Target target) {
        if (target.equals(Target.CONSOLE)) {
            consoleTarget.setValue(true, true);
        } else if (target.equals(Target.MOBILE)) {
            mobileTarget.setValue(true, true);
        } else if (target.equals(Target.STANDALONE)) {
            standaloneTarget.setValue(true, true);
        } else if (target.equals(Target.WEBAPP)) {
            webAppTarget.setValue(true, true);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Target getSelectedTarget() {
        return consoleTarget.getValue() ? Target.CONSOLE : (standaloneTarget.getValue() ? Target.STANDALONE : (webAppTarget.getValue()
            ? Target.WEBAPP
            : (mobileTarget.getValue() ? Target.MOBILE : null)));
    }

    /** {@inheritDoc} */
    @Override
    public void showEnvironments(Map<String, RunnerEnvironment> environments) {
        runnerEnvironments.clear();
        environmentBox.clear();
        environmentBox.setEnabled(true);
        if (environments != null && !environments.isEmpty()) {
            for (String key : environments.keySet()) {
                RunnerEnvironment environment = environments.get(key);
                runnerEnvironments.add(environment);
                environmentBox.addItem(environment.getDisplayName(), environment.getId());
            }
        } else {
            environmentBox.addItem("---", (String)null);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setEnvironmentsEnableState(boolean isEnabled) {
        environmentBox.setEnabled(isEnabled);
    }

}
