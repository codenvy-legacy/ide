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
package com.codenvy.ide.ext.git.client.merge;

import elemental.html.DragEvent;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.GitResources;
import com.codenvy.ide.ext.git.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.git.shared.Reference;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.ui.tree.Tree;
import com.codenvy.ide.ui.tree.TreeNodeElement;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.ext.git.client.merge.MergePresenter.LOCAL_BRANCHES_TITLE;
import static com.codenvy.ide.ext.git.client.merge.MergePresenter.REMOTE_BRANCHES_TITLE;
import static com.codenvy.ide.ext.git.shared.Reference.RefType.LOCAL_BRANCH;
import static com.codenvy.ide.ext.git.shared.Reference.RefType.REMOTE_BRANCH;

/**
 * The implementation of {@link MergeView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class MergeViewImpl extends DialogBox implements MergeView {
    interface MergeViewImplUiBinder extends UiBinder<Widget, MergeViewImpl> {
    }

    private static MergeViewImplUiBinder ourUiBinder = GWT.create(MergeViewImplUiBinder.class);

    @UiField
    com.codenvy.ide.ui.Button btnCancel;
    @UiField
    com.codenvy.ide.ui.Button btnMerge;
    @UiField
    ScrollPanel               referencesPanel;
    @UiField(provided = true)
    final         GitResources                 res;
    @UiField(provided = true)
    final         GitLocalizationConstant      locale;
    private       Tree<Reference>              references;
    private       ActionDelegate               delegate;
    private final DtoClientImpls.ReferenceImpl localBranches;
    private final DtoClientImpls.ReferenceImpl remoteBranches;

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

        this.setText(locale.mergeTitle());
        this.setWidget(widget);

        this.references =
                Tree.create(rendererResources, new ReferenceTreeNodeDataAdapter(), new ReferenceTreeNodeRenderer(rendererResources));
        this.references.setTreeEventHandler(new Tree.Listener<Reference>() {
            @Override
            public void onNodeAction(TreeNodeElement<Reference> node) {
                delegate.onReferenceSelected(node.getData());
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
            public void onNodeDragStart(TreeNodeElement<Reference> node, DragEvent event) {
                //do nothing
            }

            @Override
            public void onNodeDragDrop(TreeNodeElement<Reference> node, DragEvent event) {
                //do nothing
            }

            @Override
            public void onNodeExpanded(final TreeNodeElement<Reference> node) {
                delegate.onReferenceSelected(node.getData());
            }

            @Override
            public void onRootContextMenu(int mouseX, int mouseY) {
                //do nothing
            }

            @Override
            public void onRootDragDrop(DragEvent event) {
                //do nothing
            }
        });
        this.referencesPanel.add(references.asWidget());

        DtoClientImpls.ReferenceImpl root = (DtoClientImpls.ReferenceImpl)references.getModel().getRoot();
        if (root == null) {
            root = DtoClientImpls.ReferenceImpl.make();
            references.getModel().setRoot(root);
        }

        localBranches = DtoClientImpls.ReferenceImpl.make();
        localBranches.setDisplayName(LOCAL_BRANCHES_TITLE);
        localBranches.setFullName(LOCAL_BRANCHES_TITLE);
        localBranches.setRefType(LOCAL_BRANCH);

        remoteBranches = DtoClientImpls.ReferenceImpl.make();
        remoteBranches.setDisplayName(REMOTE_BRANCHES_TITLE);
        remoteBranches.setFullName(REMOTE_BRANCHES_TITLE);
        remoteBranches.setRefType(REMOTE_BRANCH);

        JsonArray<Reference> branches = JsonCollections.createArray();
        branches.add(localBranches);
        branches.add(remoteBranches);

        root.setBranches(branches);
    }

    /** {@inheritDoc} */
    @Override
    public void setLocalBranches(@NotNull JsonArray<Reference> references) {
        localBranches.setBranches(references);
        this.references.renderTree(0);
    }

    /** {@inheritDoc} */
    @Override
    public void setRemoteBranches(@NotNull JsonArray<Reference> references) {
        remoteBranches.setBranches(references);
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
        this.center();
        this.show();
    }

    @UiHandler("btnCancel")
    public void onCancelClicked(ClickEvent event) {
        delegate.onCancelClicked();
    }

    @UiHandler("btnMerge")
    public void onMergeClicked(ClickEvent event) {
        delegate.onMergeClicked();
    }
}