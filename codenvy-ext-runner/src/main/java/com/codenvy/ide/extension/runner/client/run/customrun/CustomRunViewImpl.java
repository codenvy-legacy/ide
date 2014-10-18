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
package com.codenvy.ide.extension.runner.client.run.customrun;

import elemental.events.KeyboardEvent;
import elemental.events.MouseEvent;

import com.codenvy.api.project.shared.dto.RunnerEnvironmentLeaf;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentTree;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.extension.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.extension.runner.client.RunnerResources;
import com.codenvy.ide.extension.runner.client.wizard.RunnersDataAdapter;
import com.codenvy.ide.extension.runner.client.wizard.RunnersRenderer;
import com.codenvy.ide.ui.dialogs.info.Info;
import com.codenvy.ide.ui.tree.Tree;
import com.codenvy.ide.ui.tree.TreeNodeElement;
import com.codenvy.ide.ui.window.Window;
import com.codenvy.ide.util.input.SignalEvent;
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
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation of {@link CustomRunView}.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class CustomRunViewImpl extends Window implements CustomRunView {
    @UiField(provided = true)
    final         RunnerResources            resources;
    @UiField(provided = true)
    final         RunnerLocalizationConstant locale;
    private final RunnerEnvironmentTree      rootNode;
    @UiField
    SimplePanel     treeContainer;
    @UiField
    TextBox         memoryTotal;
    @UiField
    TextBox         memoryAvailable;
    @UiField
    CheckBox        skipBuild;
    @UiField
    CheckBox        rememberRunMemory;
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
    private ActionDelegate delegate;
    private Tree<Object>   tree;
    private Array<RadioButton> radioButtons = Collections.createArray();
    private Button runButton;

    /** Create view. */
    @Inject
    protected CustomRunViewImpl(com.codenvy.ide.Resources resources, RunnerResources runnerResources, RunnerLocalizationConstant constant,
                                CustomRunViewImplUiBinder uiBinder, RunnersRenderer runnersRenderer, DtoFactory dtoFactory) {
        this.resources = runnerResources;
        this.locale = constant;
        setTitle(constant.runConfigurationViewTitle());
        setWidget(uiBinder.createAndBindUi(this));
        ensureDebugId("customRun-window");

        rootNode = dtoFactory.createDto(RunnerEnvironmentTree.class);
        tree = Tree.create(resources, new RunnersDataAdapter(), runnersRenderer);
        treeContainer.add(tree);
        tree.getModel().setRoot(rootNode);
        tree.setTreeEventHandler(new Tree.Listener<Object>() {
            @Override
            public void onNodeAction(TreeNodeElement<Object> node) {
            }

            @Override
            public void onNodeClosed(TreeNodeElement<Object> node) {
            }

            @Override
            public void onNodeContextMenu(int mouseX, int mouseY, TreeNodeElement<Object> node) {
            }

            @Override
            public void onNodeDragStart(TreeNodeElement<Object> node, MouseEvent event) {
            }

            @Override
            public void onNodeDragDrop(TreeNodeElement<Object> node, MouseEvent event) {
            }

            @Override
            public void onNodeExpanded(TreeNodeElement<Object> node) {
            }

            @Override
            public void onNodeSelected(TreeNodeElement<Object> node, SignalEvent event) {
                Object data = node.getData();
                if (data instanceof RunnerEnvironmentLeaf) {
                    delegate.onEnvironmentSelected(((RunnerEnvironmentLeaf)data).getEnvironment());
                } else {
                    delegate.onEnvironmentSelected(null);
                }
            }

            @Override
            public void onRootContextMenu(int mouseX, int mouseY) {
            }

            @Override
            public void onRootDragDrop(MouseEvent event) {
            }

            @Override
            public void onKeyboard(KeyboardEvent event) {
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
        runButton.addStyleName(resources.runner().runButton());

        Button cancelButton = createButton(locale.buttonCancel(), "project-customRun-cancel", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onCancelClicked();
            }
        });
        cancelButton.addStyleName(resources.runner().cancelButton());

        getFooter().add(runButton);
        getFooter().add(cancelButton);
    }

    @Override
    public void setEnabledRadioButtons(int workspaceRam) {
        for (RadioButton radioButton : radioButtons.asIterable()) {
            int runnerMemory = 0;
            try {
                runnerMemory = Integer.parseInt(parseRadioButMemoryValue(radioButton.getText()));
            } catch (NumberFormatException e) {
                //do nothing
            }
            radioButton.setEnabled(runnerMemory > 0 && runnerMemory <= workspaceRam);
        }
        radioButOther.setEnabled(true);
    }

    @Override
    public void setEnvironmentDescription(String description) {
        descriptionField.setText(description);
    }

    @Override
    public void setRunButtonState(boolean enabled) {
        runButton.setEnabled(enabled);
    }

    @Override
    public void addRunner(RunnerEnvironmentTree environmentTree) {
        rootNode.getNodes().add(environmentTree);
        tree.renderTree(1);
    }

    @UiHandler({"runnerMemory128", "runnerMemory256", "runnerMemory512", "runnerMemory1GB", "runnerMemory2GB"})
    void standardMemoryHandler(ValueChangeEvent<Boolean> event) {
        otherValueMemory.setText("");
    }

    @UiHandler({"otherValueMemory"})
    void otherMemoryHandler(KeyUpEvent event) {
        clearStandardMemoryFields();
        radioButOther.setValue(true);
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
    public void setRunnerMemorySize(String memorySize) {
        clearStandardMemoryFields();
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
    public String getTotalMemorySize() {
        return memoryTotal.getText();
    }

    @Override
    public void setTotalMemorySize(String memorySize) {
        this.memoryTotal.setText(memorySize);
    }

    @Override
    public String getAvailableMemorySize() {
        return memoryAvailable.getText();
    }

    @Override
    public void setAvailableMemorySize(String memorySize) {
        this.memoryAvailable.setText(memorySize);
    }

    @Override
    public void close() {
        onClose();
        this.hide();
    }

    @Override
    public void showDialog() {
        runButton.setEnabled(false);
        rootNode.getNodes().clear();
        tree.renderTree();
        this.show();
    }

    @Override
    public boolean isSkipBuildSelected() {
        return skipBuild.getValue();
    }

    @Override
    public boolean isRememberOptionsSelected() {
        return rememberRunMemory.getValue();
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    protected void onClose() {
        clear();
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
        rememberRunMemory.setValue(false);
    }

    private void clearStandardMemoryFields() {
        for (RadioButton radioButton : radioButtons.asIterable()) {
            radioButton.setValue(false);
        }
    }

    @Override
    public void showWarning(String warning) {
        Info warningWindow = new Info("Warning", warning);
        warningWindow.show();
    }

    interface CustomRunViewImplUiBinder extends UiBinder<Widget, CustomRunViewImpl> {
    }
}