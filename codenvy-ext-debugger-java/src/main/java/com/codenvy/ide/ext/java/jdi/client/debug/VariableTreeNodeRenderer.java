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
package com.codenvy.ide.ext.java.jdi.client.debug;

import elemental.html.DivElement;
import elemental.dom.Element;
import elemental.html.SpanElement;

import com.codenvy.ide.ext.java.jdi.shared.Variable;
import com.codenvy.ide.ui.tree.NodeRenderer;
import com.codenvy.ide.ui.tree.Tree;
import com.codenvy.ide.ui.tree.TreeNodeElement;
import com.codenvy.ide.util.dom.Elements;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * The rendered for debug variable node.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class VariableTreeNodeRenderer implements NodeRenderer<Variable> {
    public interface Css extends CssResource {
        @ClassName("variable-root")
        String variableRoot();

        @ClassName("variable-icon")
        String variableIcon();

        @ClassName("variable-label")
        String variableLabel();
    }

    public interface Resources extends Tree.Resources {
        @Source("Debug.css")
        Css variableCss();

        @Source("localVariable.png")
        ImageResource localVariable();
    }

    private final Css css;

    public VariableTreeNodeRenderer(Resources res) {
        this.css = res.variableCss();
        this.css.ensureInjected();
    }

    /** {@inheritDoc} */
    @Override
    public Element getNodeKeyTextContainer(SpanElement treeNodeLabel) {
        return (Element)treeNodeLabel.getChildNodes().item(1);
    }

    /** {@inheritDoc} */
    @Override
    public SpanElement renderNodeContents(Variable data) {
        SpanElement root = Elements.createSpanElement(css.variableRoot());
        DivElement icon = Elements.createDivElement(css.variableIcon());
        SpanElement label = Elements.createSpanElement(css.variableLabel());
        String content = data.getName() + ": " + data.getValue();
        label.setTextContent(content);

        root.appendChild(icon);
        root.appendChild(label);

        return root;
    }

    /** {@inheritDoc} */
    @Override
    public void updateNodeContents(TreeNodeElement<Variable> treeNode) {
        // do nothing
    }
}