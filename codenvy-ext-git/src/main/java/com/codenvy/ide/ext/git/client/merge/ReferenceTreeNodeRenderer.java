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

import elemental.html.DivElement;
import elemental.dom.Element;
import elemental.html.SpanElement;

import com.codenvy.ide.ui.tree.NodeRenderer;
import com.codenvy.ide.ui.tree.Tree;
import com.codenvy.ide.ui.tree.TreeNodeElement;
import com.codenvy.ide.util.dom.Elements;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * The rendered for reference node.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class ReferenceTreeNodeRenderer implements NodeRenderer<Reference> {
    public interface Css extends CssResource {
        @ClassName("reference-root")
        String referenceRoot();

        @ClassName("local-icon")
        String localBranchesIcon();

        @ClassName("remote-icon")
        String remoteBranchesIcon();

        @ClassName("branch-icon")
        String branchIcon();

        @ClassName("reference-label")
        String referenceLabel();
    }

    public interface Resources extends Tree.Resources {
        @Source("Merge.css")
        Css referenceCss();

        @Source("branch.png")
        ImageResource branch();

        @Source("local_branches.png")
        ImageResource localBranches();

        @Source("remote_branches.png")
        ImageResource remoteBranches();
    }

    private final Css css;

    public ReferenceTreeNodeRenderer(Resources res) {
        this.css = res.referenceCss();
        this.css.ensureInjected();
    }

    /** {@inheritDoc} */
    @Override
    public Element getNodeKeyTextContainer(SpanElement treeNodeLabel) {
        return (Element)treeNodeLabel.getChildNodes().item(1);
    }

    /** {@inheritDoc} */
    @Override
    public SpanElement renderNodeContents(Reference data) {
        SpanElement root = Elements.createSpanElement(css.referenceRoot());

        DivElement icon;

        if (data.getFullName().equals(MergePresenter.LOCAL_BRANCHES_TITLE)) {
            icon = Elements.createDivElement(css.localBranchesIcon());
        } else if (data.getFullName().equals(MergePresenter.REMOTE_BRANCHES_TITLE)) {
            icon = Elements.createDivElement(css.remoteBranchesIcon());
        } else {
            icon = Elements.createDivElement(css.branchIcon());
        }

        SpanElement label = Elements.createSpanElement(css.referenceLabel());
        String content = data.getDisplayName();
        label.setTextContent(content);

        root.appendChild(icon);
        root.appendChild(label);

        return root;
    }

    /** {@inheritDoc} */
    @Override
    public void updateNodeContents(TreeNodeElement<Reference> treeNode) {
        // do nothing
    }
}