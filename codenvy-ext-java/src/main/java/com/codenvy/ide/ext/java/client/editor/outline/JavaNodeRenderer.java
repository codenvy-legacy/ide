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
package com.codenvy.ide.ext.java.client.editor.outline;

import elemental.html.DivElement;
import elemental.html.Element;
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
                icon.addClassName(css.packageItem());
            }
            if (BlockTypes.IMPORTS.getType().equals(block.getType())) {
                icon.addClassName(css.imports());
            } else if (BlockTypes.IMPORT.getType().equals(block.getType())) {
                icon.addClassName(css.importItem());
            } else if (BlockTypes.CLASS.getType().equals(block.getType())) {
                icon.addClassName(css.classItem());
            } else if (BlockTypes.INTERFACE.getType().equals(block.getType())) {
                icon.addClassName(css.interfaceItem());
            } else if (BlockTypes.ENUM.getType().equals(block.getType())) {
                icon.addClassName(css.enumItem());
            } else if (BlockTypes.ANNOTATION.getType().equals(block.getType())) {
                icon.addClassName(css.annotationItem());
            } else if (BlockTypes.FIELD.getType().equals(block.getType())) {
                icon.addClassName(getFieldClass(block.getModifiers()));
            } else if (BlockTypes.METHOD.getType().equals(block.getType())) {
                icon.addClassName(getMethodClass(block.getModifiers()));
            }
            label.setTextContent(block.getName());
            UIObject.ensureDebugId((com.google.gwt.dom.client.Element)label, "javaNodeRenderer-codeblock-" + block.getName() + label.hashCode());

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
