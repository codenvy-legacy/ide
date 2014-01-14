/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.internal.core.dom.rewrite;

import com.codenvy.ide.ext.java.jdt.core.dom.ASTNode;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ITrackedNodePosition;
import com.codenvy.ide.text.Region;
import com.codenvy.ide.text.edits.TextEdit;
import com.codenvy.ide.text.edits.TextEditGroup;


/**
 *
 */
public class TrackedNodePosition implements ITrackedNodePosition {

    private final TextEditGroup group;

    private final ASTNode node;

    public TrackedNodePosition(TextEditGroup group, ASTNode node) {
        this.group = group;
        this.node = node;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jdt.internal.corext.dom.ITrackedNodePosition#getStartPosition ()
     */
    public int getStartPosition() {
        if (this.group.isEmpty()) {
            return this.node.getStartPosition();
        }
        Region coverage = TextEdit.getCoverage(this.group.getTextEdits());
        if (coverage == null) {
            return this.node.getStartPosition();
        }
        return coverage.getOffset();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jdt.internal.corext.dom.ITrackedNodePosition#getLength()
     */
    public int getLength() {
        if (this.group.isEmpty()) {
            return this.node.getLength();
        }
        Region coverage = TextEdit.getCoverage(this.group.getTextEdits());
        if (coverage == null) {
            return this.node.getLength();
        }
        return coverage.getLength();
    }
}
