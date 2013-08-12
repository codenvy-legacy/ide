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

package com.codenvy.ide.ui.tree;

import elemental.html.Element;

import com.codenvy.ide.ui.tree.TreeNodeMutator.Css;

/**
 * Utility for mutating the String name for the model object backing a
 * {@link TreeNodeElement}, in place in a {@link Tree}.
 * <p/>
 * In other words, this is a utility class that facilitates renaming of
 * {@link TreeNodeElement}s.
 */
public class TreeNodeLabelRenamer<D> {

    /** Callback that gets invoked right after the model has been changed. */
    public interface LabelRenamerCallback<D> {
        void onCommit(String oldLabel, TreeNodeElement<D> node);

        boolean passValidation(TreeNodeElement<D> node, String newLabel);
    }

    private final NodeDataAdapter<D> dataAdapter;

    /**
     * The {@link NodeRenderer} that we use to re-render the label portions of the
     * {@link TreeNodeElement}.
     */
    private final NodeRenderer<D> renderer;

    private LabelRenamerCallback<D> callback;

    private final TreeNodeMutator<D> treeNodeMutator;

    private final TreeNodeMutator.MutationAction<D> mutationAction =
            new TreeNodeMutator.MutationAction<D>() {

                @Override
                public Element getElementForMutation(TreeNodeElement<D> node) {
                    return renderer.getNodeKeyTextContainer(node.getNodeLabel());
                }

                @Override
                public void onBeforeMutation(TreeNodeElement<D> node) {
                    // Nothing to do.
                }

                @Override
                public void onMutationCommit(TreeNodeElement<D> node, String oldLabel, String newLabel) {
                    if (callback != null) {
                        mutateNodeKey(node, newLabel);
                        callback.onCommit(oldLabel, node);
                        callback = null;
                    }
                }

                @Override
                public boolean passValidation(TreeNodeElement<D> node, String newLabel) {
                    return callback == null || callback.passValidation(node, newLabel);
                }
            };

    public TreeNodeLabelRenamer(
            NodeRenderer<D> renderer, NodeDataAdapter<D> dataAdapter, Css treeMutatorCss) {
        this.renderer = renderer;
        this.dataAdapter = dataAdapter;
        this.treeNodeMutator = new TreeNodeMutator<D>(treeMutatorCss);
    }

    public boolean isMutating() {
        return treeNodeMutator.isMutating();
    }

    /**
     * Replaces the nodes text label with an input box to allow the user to
     * rename the node.
     */
    public void enterMutation(TreeNodeElement<D> node, LabelRenamerCallback<D> callback) {

        // If we are already mutating, return.
        if (isMutating()) {
            return;
        }

        this.callback = callback;
        treeNodeMutator.enterMutation(node, mutationAction);
    }

    public void cancel() {
        treeNodeMutator.cancel();
    }

    public NodeDataAdapter<D> getDataAdapter() {
        return dataAdapter;
    }

    public NodeRenderer<D> getRenderer() {
        return renderer;
    }

    public TreeNodeMutator<D> getTreeNodeMutator() {
        return treeNodeMutator;
    }

    /** Commits the current text if it passes validation, or cancels the mutation. */
    public void forceCommit() {
        treeNodeMutator.forceCommit();
    }

    /**
     * Updates the name value for the model object for a node, and then replaces
     * the label's contents with the new value.
     */
    public void mutateNodeKey(TreeNodeElement<D> node, String newName) {
        getDataAdapter().setNodeName(node.getData(), newName);
        Element elem = getRenderer().getNodeKeyTextContainer(node.getNodeLabel());
        elem.setTextContent(newName);
    }
}
