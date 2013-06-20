/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.ext.java.jdi.client.debug;

import elemental.html.DivElement;
import elemental.html.Element;
import elemental.html.SpanElement;

import com.codenvy.ide.ext.java.jdi.shared.Variable;
import com.codenvy.ide.ui.tree.NodeRenderer;
import com.codenvy.ide.ui.tree.Tree;
import com.codenvy.ide.ui.tree.TreeNodeElement;
import com.codenvy.ide.util.dom.Elements;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * The rendered for debug variables tree.
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

        @Source("local.png")
        ImageResource local();

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