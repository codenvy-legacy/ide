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
package com.codenvy.ide.ext.git.client.merge;

import elemental.events.MouseEvent;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.GitResources;
import com.codenvy.ide.ui.tree.Tree;
import com.codenvy.ide.ui.tree.TreeNodeElement;
import com.codenvy.ide.ui.window.Window;
import com.codenvy.ide.util.input.SignalEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

import static com.codenvy.ide.ext.git.client.merge.MergePresenter.LOCAL_BRANCHES_TITLE;
import static com.codenvy.ide.ext.git.client.merge.MergePresenter.REMOTE_BRANCHES_TITLE;
import static com.codenvy.ide.ext.git.client.merge.Reference.RefType.LOCAL_BRANCH;
import static com.codenvy.ide.ext.git.client.merge.Reference.RefType.REMOTE_BRANCH;

/**
 * The implementation of {@link MergeView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class MergeViewImpl extends Window implements MergeView {
    interface MergeViewImplUiBinder extends UiBinder<Widget, MergeViewImpl> {
    }

    private static MergeViewImplUiBinder ourUiBinder = GWT.create(MergeViewImplUiBinder.class);

    Button      btnCancel;
    Button      btnMerge;
    @UiField
    ScrollPanel referencesPanel;
    @UiField(provided = true)
    final         GitResources            res;
    @UiField(provided = true)
    final         GitLocalizationConstant locale;
    private       Tree<Reference>         references;
    private       ActionDelegate          delegate;
    private final Reference               localBranch;
    private final Reference               remoteBranch;

    /**
     * Create view.
     *
     * @param resources
     * @param locale
     * @param rendererResources
     */
    @Inject
    protected MergeViewImpl(GitResources resources, GitLocalizationConstant locale, ReferenceTreeNodeRenderer.Resources rendererResources) {
        this.res = resources;
        this.locale = locale;

        Widget widget = ourUiBinder.createAndBindUi(this);

        this.setTitle(locale.mergeTitle());
        this.setWidget(widget);

        this.references =
                Tree.create(rendererResources, new ReferenceTreeNodeDataAdapter(), new ReferenceTreeNodeRenderer(rendererResources));
        this.references.setTreeEventHandler(new Tree.Listener<Reference>() {
            @Override
            public void onNodeAction(TreeNodeElement<Reference> node) {
            }

            @Override
            public void onNodeClosed(TreeNodeElement<Reference> node) {
                //do nothing
            }

            @Override
            public void onNodeContextMenu(int mouseX, int mouseY, TreeNodeElement<Reference> node) {
                //do nothing
            }

            @Override
            public void onNodeDragStart(TreeNodeElement<Reference> node, MouseEvent event) {
                //do nothing
            }

            @Override
            public void onNodeDragDrop(TreeNodeElement<Reference> node, MouseEvent event) {
                //do nothing
            }

            @Override
            public void onNodeExpanded(final TreeNodeElement<Reference> node) {
                delegate.onReferenceSelected(node.getData());
            }

            @Override
            public void onNodeSelected(TreeNodeElement<Reference> node, SignalEvent event) {
                delegate.onReferenceSelected(node.getData());
            }

            @Override
            public void onRootContextMenu(int mouseX, int mouseY) {
                //do nothing
            }

            @Override
            public void onRootDragDrop(MouseEvent event) {
                //do nothing
            }
        });
        this.referencesPanel.add(references.asWidget());

        Reference root = references.getModel().getRoot();
        if (root == null) {
            root = new Reference("", "", null);
            references.getModel().setRoot(root);
        }

        localBranch = new Reference(LOCAL_BRANCHES_TITLE, LOCAL_BRANCHES_TITLE, LOCAL_BRANCH);

        remoteBranch = new Reference(REMOTE_BRANCHES_TITLE, REMOTE_BRANCHES_TITLE, REMOTE_BRANCH);

        Array<Reference> branches = Collections.createArray(localBranch, remoteBranch);
        root.setBranches(branches);
        
        btnCancel = createButton(locale.buttonCancel(), "git-merge-cancel", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onCancelClicked();
            }
        });
        getFooter().add(btnCancel);

        btnMerge = createButton(locale.buttonMerge(), "git-merge-merge", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onMergeClicked();
            }
        });
        getFooter().add(btnMerge);
    }

    /** {@inheritDoc} */
    @Override
    public void setLocalBranches(@NotNull Array<Reference> references) {
        localBranch.setBranches(references);
        this.references.renderTree(0);
    }

    /** {@inheritDoc} */
    @Override
    public void setRemoteBranches(@NotNull Array<Reference> references) {
        remoteBranch.setBranches(references);
        this.references.renderTree(0);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableMergeButton(boolean enabled) {
        btnMerge.setEnabled(enabled);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        this.show();
    }

    /** {@inheritDoc} */
    @Override
    protected void onClose() {
    }
}