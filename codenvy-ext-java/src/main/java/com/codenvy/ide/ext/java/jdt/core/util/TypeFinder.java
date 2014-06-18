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
package com.codenvy.ide.ext.java.jdt.core.util;

import com.codenvy.ide.ext.java.jdt.core.dom.ASTVisitor;
import com.codenvy.ide.ext.java.jdt.core.dom.AbstractTypeDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.EnumDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.TypeDeclaration;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class TypeFinder extends ASTVisitor {

    private int position;

    public AbstractTypeDeclaration type;

    /** @param position */
    public TypeFinder(int position) {
        super();
        this.position = position;
    }

    /** @see org.eclipse.jdt.client.core.dom.ASTVisitor#visit(org.eclipse.jdt.client.core.dom.EnumDeclaration) */
    @Override
    public boolean visit(EnumDeclaration node) {
        if (node.getStartPosition() < position && node.getStartPosition() + node.getLength() > position)
            type = node;
        return true;
    }

    /** @see org.eclipse.jdt.client.core.dom.ASTVisitor#visit(org.eclipse.jdt.client.core.dom.TypeDeclaration) */
    @Override
    public boolean visit(TypeDeclaration node) {
        if (node.getStartPosition() < position && node.getStartPosition() + node.getLength() > position)
            type = node;
        return true;
    }


}
