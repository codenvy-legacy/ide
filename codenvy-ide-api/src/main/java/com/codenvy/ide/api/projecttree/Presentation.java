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

import javax.annotation.Nonnull;

/**
 * The presentation of a node in 'Project Explorer'.
 *
 * @author Artem Zatsarynnyy
 */
public class Presentation {
    private String                               displayName;
    private SVGImage                             svgIcon;
    private String                               cssClassName;
    private TreeNodeElement<AbstractTreeNode<?>> treeNodeElement;

    /** Returns the node's name that will be displayed in the 'Project Explorer'. */
    @Nonnull
    public String getDisplayName() {
        return displayName;
    }

    /** Set node's display name. */
    public void setDisplayName(@Nonnull String displayName) {
        this.displayName = displayName;
    }

    /** Returns an icon for the node. */
    public SVGImage getSvgIcon() {
        return svgIcon;
    }

    /** Set node's icon. */
    public void setSvgIcon(SVGImage icon) {
        this.svgIcon = icon;
    }

    public String getCssClassName() {
        return cssClassName;
    }

    public void setCssClassName(String cssClassName) {
        this.cssClassName = cssClassName;
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
