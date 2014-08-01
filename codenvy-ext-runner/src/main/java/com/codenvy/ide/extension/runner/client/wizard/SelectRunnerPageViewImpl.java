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

import com.codenvy.api.runner.dto.RunnerDescriptor;
import com.codenvy.api.runner.dto.RunnerEnvironment;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Evgen Vidolob
 */
public class SelectRunnerPageViewImpl implements SelectRunnerPageView {
    private static SelectRunnerViewImplUiBinder ourUiBinder = GWT.create(SelectRunnerViewImplUiBinder.class);
    private final DockLayoutPanel rootElement;
    @UiField
    ListBox runnerBox;
    @UiField
    ListBox environmentBox;
    private ActionDelegate delegate;
    private Map<String, RunnerDescriptor> runnerDescriptorMap = new HashMap<>();
    private List<String>                  runnerNames         = new ArrayList<>();

    public SelectRunnerPageViewImpl() {
        rootElement = ourUiBinder.createAndBindUi(this);
    }

    @UiHandler("runnerBox")
    void runnerChanged(ChangeEvent event) {
        String value = runnerBox.getValue(runnerBox.getSelectedIndex());
        environmentBox.clear();
        if(value == null){
            delegate.runnerSelected(null);
            delegate.runnerEnvironmentSelected(null);
            environmentBox.addItem("---", (String)null);
            return;
        }
        RunnerDescriptor runnerDescriptor = runnerDescriptorMap.get(value);
        if (runnerDescriptor != null) {
            Map<String, RunnerEnvironment> environments = runnerDescriptor.getEnvironments();
            if (environments != null && !environments.isEmpty()) {
                for (String key : environments.keySet()) {
                    RunnerEnvironment environment = environments.get(key);
                    environmentBox.addItem(environment.getDescription(), environment.getId());
                }

                delegate.runnerEnvironmentSelected(environmentBox.getValue(environmentBox.getSelectedIndex()));
            } else {
                environmentBox.addItem("---", (String)null);
            }
            delegate.runnerSelected(runnerDescriptor);
        }

    }

    @UiHandler("environmentBox")
    void environmentChanged(ChangeEvent event) {
        delegate.runnerEnvironmentSelected(environmentBox.getValue(environmentBox.getSelectedIndex()));
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
    public void showRunners(Collection<RunnerDescriptor> runnerDescriptors) {
        runnerDescriptorMap.clear();
        runnerBox.clear();
        runnerNames.clear();
        runnerDescriptorMap.put("---", null);
        runnerBox.addItem("---", (String)null);
        runnerNames.add("---");
        environmentBox.addItem("---", (String)null);
        for (RunnerDescriptor runnerDescriptor : runnerDescriptors) {
            runnerDescriptorMap.put(runnerDescriptor.getName(), runnerDescriptor);
            runnerBox.addItem(runnerDescriptor.getName(), runnerDescriptor.getName());
            runnerNames.add(runnerDescriptor.getName());
        }

    }

    @Override
    public void selectRunner(String runnerName) {
        runnerBox.setSelectedIndex(runnerNames.indexOf(runnerName));
        runnerChanged(null);
    }

    interface SelectRunnerViewImplUiBinder
            extends UiBinder<DockLayoutPanel, SelectRunnerPageViewImpl> {
    }
}