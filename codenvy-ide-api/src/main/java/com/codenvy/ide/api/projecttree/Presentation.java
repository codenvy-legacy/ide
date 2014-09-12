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
package com.codenvy.ide.api.projecttree;

import com.codenvy.ide.ui.tree.TreeNodeElement;

import org.vectomatic.dom.svg.ui.SVGImage;

/**
 * The presentation of a node in 'Project Explorer'.
 *
 * @author Artem Zatsarynnyy
 */
public class Presentation {
    private SVGImage                             svgIcon;
    private TreeNodeElement<AbstractTreeNode<?>> treeNodeElement;

    /** Provides an SVG icon to be used for graphical representation of the node. */
    public SVGImage getSvgIcon() {
        return svgIcon;
    }

    /** Set an SVG icon to be used for graphical representation of the node. */
    public void setSvgIcon(SVGImage icon) {
        this.svgIcon = icon;
    }

    /**
     * Returns the rendered {@link TreeNodeElement} that is a representation of node.
     * <p/>
     * Used internally and not intended to be used directly.
     *
     * @return the rendered {@link TreeNodeElement}
     */
    public TreeNodeElement<AbstractTreeNode<?>> getTreeNodeElement() {
        return treeNodeElement;
    }

    /**
     * Sets the rendered {@link TreeNodeElement} that is a representation of node.
     * <p/>
     * Used internally and not intended to be used directly.
     *
     * @param treeNodeElement
     *         the rendered {@link TreeNodeElement}
     */
    public void setTreeNodeElement(TreeNodeElement<AbstractTreeNode<?>> treeNodeElement) {
        this.treeNodeElement = treeNodeElement;
    }
}
