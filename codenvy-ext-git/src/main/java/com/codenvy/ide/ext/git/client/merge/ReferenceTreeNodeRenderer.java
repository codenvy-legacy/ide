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

import elemental.html.DivElement;
import elemental.html.Element;
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