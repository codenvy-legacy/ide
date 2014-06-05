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