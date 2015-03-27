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
package org.eclipse.che.ide.projecttype.wizard.runnerspage;

import elemental.events.KeyboardEvent;
import elemental.events.MouseEvent;

import org.eclipse.che.api.project.shared.dto.RunnerEnvironment;
import org.eclipse.che.api.project.shared.dto.RunnerEnvironmentLeaf;
import org.eclipse.che.api.project.shared.dto.RunnerEnvironmentTree;
import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.ui.tree.Tree;
import org.eclipse.che.ide.ui.tree.TreeNodeElement;
import org.eclipse.che.ide.util.input.SignalEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Evgen Vidolob
 */
public class RunnersPageViewImpl implements RunnersPageView {

    interface RunnersPageViewImplUiBinder extends UiBinder<DockLayoutPanel, RunnersPageViewImpl> {
    }

    private final DockLayoutPanel       rootElement;
    private final Tree<Object>          tree;
    private final RunnerEnvironmentTree root;
    @UiField
    Label       noEnvLabel;
    @UiField
    TextArea    runnerDescription;
    @UiField
    SimplePanel treeContainer;
    private ActionDelegate delegate;

    private Map<String, RunnerEnvironmentLeaf> environmentMap = new HashMap<>();

    @Inject
    public RunnersPageViewImpl(Resources resources,
                               DtoFactory dtoFactory,
                               RunnersRenderer runnersRenderer,
                               RunnersPageViewImplUiBinder uiBinder) {
        rootElement = uiBinder.createAndBindUi(this);

        root = dtoFactory.createDto(RunnerEnvironmentTree.class);
        tree = Tree.create(resources, new RunnersDataAdapter(), runnersRenderer);
        treeContainer.setWidget(noEnvLabel);
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
                    delegate.environmentSelected(((RunnerEnvironmentLeaf)data).getEnvironment());
                } else {
                    delegate.environmentSelected(null);
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
    public void showRunnerDescription(String description) {
        runnerDescription.setText(description);
    }

    @Override
    public void addRunner(RunnerEnvironmentTree environmentTree) {
        collectRunnerEnvironments(environmentTree);

        root.getNodes().add(environmentTree);
        tree.getModel().setRoot(root);
        tree.renderTree(1);

        checkTreeVisibility(environmentTree);
    }

    private void collectRunnerEnvironments(RunnerEnvironmentTree environmentTree) {
        for (RunnerEnvironmentLeaf leaf : environmentTree.getLeaves()) {
            final RunnerEnvironment environment = leaf.getEnvironment();
            if (environment != null) {
                environmentMap.put(environment.getId(), leaf);
            }
        }

        for (RunnerEnvironmentTree node : environmentTree.getNodes()) {
            collectRunnerEnvironments(node);
        }
    }

    private void checkTreeVisibility(RunnerEnvironmentTree environmentTree) {
        if (environmentTree.getNodes().isEmpty() && environmentTree.getLeaves().isEmpty()) {
            treeContainer.setWidget(noEnvLabel);
        } else {
            treeContainer.setWidget(tree);
        }
    }

    @Override
    public void selectRunnerEnvironment(String environmentId) {
        final RunnerEnvironmentLeaf environment = environmentMap.get(environmentId);
        if (environmentMap.containsKey(environmentId)) {
            // TODO: need to implement RunnersDataAdapter.getParent() to properly working auto-expanding feature
            //tree.autoExpandAndSelectNode(environment, true);
            delegate.environmentSelected(environment.getEnvironment());
        }
    }

    @Override
    public void clearTree() {
        root.getNodes().clear();
        tree.renderTree(1);
    }

}
