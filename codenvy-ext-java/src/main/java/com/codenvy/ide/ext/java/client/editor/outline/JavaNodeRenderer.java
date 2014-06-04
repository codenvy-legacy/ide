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
package com.codenvy.ide.ext.java.client.editor.outline;

import elemental.html.DivElement;
import elemental.dom.Element;
import elemental.html.SpanElement;

import com.codenvy.ide.ext.java.client.JavaCss;
import com.codenvy.ide.ext.java.client.JavaResources;
import com.codenvy.ide.ext.java.jdt.core.dom.Modifier;
import com.codenvy.ide.ext.java.messages.BlockTypes;
import com.codenvy.ide.texteditor.api.outline.CodeBlock;
import com.codenvy.ide.ui.tree.NodeRenderer;
import com.codenvy.ide.ui.tree.TreeNodeElement;
import com.codenvy.ide.util.dom.Elements;
import com.google.gwt.user.client.ui.UIObject;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class JavaNodeRenderer implements NodeRenderer<CodeBlock> {

    private JavaCss css;

    /**
     *
     */
    public JavaNodeRenderer(JavaResources resources) {
        css = resources.css();

    }

    /** {@inheritDoc} */
    @Override
    public Element getNodeKeyTextContainer(SpanElement treeNodeLabel) {
        return (Element)treeNodeLabel.getChildNodes().item(1);
    }

    /** {@inheritDoc} */
    @Override
    public SpanElement renderNodeContents(CodeBlock data) {

        if (data instanceof JavaCodeBlock) {
            SpanElement root = Elements.createSpanElement(css.outlineRoot());
            DivElement icon = Elements.createDivElement(css.outlineIcon());
            JavaCodeBlock block = (JavaCodeBlock)data;
            SpanElement label = Elements.createSpanElement(css.outlineLabel());
            if (BlockTypes.PACKAGE.getType().equals(block.getType())) {
                Elements.addClassName(css.packageItem(), icon);
            }
            if (BlockTypes.IMPORTS.getType().equals(block.getType())) {
                Elements.addClassName(css.imports(), icon);
            } else if (BlockTypes.IMPORT.getType().equals(block.getType())) {
                Elements.addClassName(css.importItem(), icon);
            } else if (BlockTypes.CLASS.getType().equals(block.getType())) {
                Elements.addClassName(css.classItem(), icon);
            } else if (BlockTypes.INTERFACE.getType().equals(block.getType())) {
                Elements.addClassName(css.interfaceItem(), icon);
            } else if (BlockTypes.ENUM.getType().equals(block.getType())) {
                Elements.addClassName(css.enumItem(), icon);
            } else if (BlockTypes.ANNOTATION.getType().equals(block.getType())) {
                Elements.addClassName(css.annotationItem(), icon);
            } else if (BlockTypes.FIELD.getType().equals(block.getType())) {
                Elements.addClassName(getFieldClass(block.getModifiers()), icon);
            } else if (BlockTypes.METHOD.getType().equals(block.getType())) {
                Elements.addClassName(getMethodClass(block.getModifiers()), icon);
            }
            label.setTextContent(block.getName());
            UIObject.ensureDebugId((com.google.gwt.dom.client.Element)label, "outline-codeblock-" + block.getName());

            root.appendChild(icon);
            root.appendChild(label);
            if (block.getJavaType() != null) {
                SpanElement type = Elements.createSpanElement(css.fqnStyle());
                type.setTextContent(" : " + block.getJavaType());
                root.appendChild(type);
            }

            //      CssUtils.setClassNameEnabled(label, css.disabled(), !data.isEnabled());

            // TODO: replace with test case
            //      assert root.getChildNodes().item(LABEL_NODE_INDEX) == label;

            return root;
        } else {
            throw new UnsupportedOperationException("This NodeRenderer support only JavaCodeBlock!");
        }
    }

    /**
     * @param modifiers
     * @return
     */
    private String getMethodClass(int modifiers) {
        if (Modifier.isPublic(modifiers))
            return css.publicMethod();
        else if (Modifier.isProtected(modifiers))
            return css.protectedMethod();
        else if (Modifier.isPrivate(modifiers))
            return css.privateMethod();
        else
            return css.defaultMethod();
    }

    /**
     * @param modifiers
     * @return
     */
    private String getFieldClass(int modifiers) {
        if (Modifier.isPublic(modifiers))
            return css.publicField();
        else if (Modifier.isProtected(modifiers))
            return css.protectedField();
        else if (Modifier.isPrivate(modifiers))
            return css.privateField();
        else
            return css.defaultField();
    }

    /** {@inheritDoc} */
    @Override
    public void updateNodeContents(TreeNodeElement<CodeBlock> treeNode) {
        //not used in Outline
    }

}
