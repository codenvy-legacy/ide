/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.internal.compiler.ast;

import com.codenvy.ide.ext.java.jdt.internal.compiler.ASTVisitor;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.BlockScope;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.ClassScope;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.Scope;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.TypeBinding;

public class JavadocArraySingleTypeReference extends ArrayTypeReference {

    public JavadocArraySingleTypeReference(char[] name, int dim, long pos) {
        super(name, dim, pos);
        this.bits |= InsideJavadoc;
    }

    @Override
    protected void reportInvalidType(Scope scope) {
        scope.problemReporter().javadocInvalidType(this, this.resolvedType, scope.getDeclarationModifiers());
    }

    @Override
    protected void reportDeprecatedType(TypeBinding type, Scope scope) {
        scope.problemReporter().javadocDeprecatedType(type, this, scope.getDeclarationModifiers());
    }

    /* (non-Javadoc)
     * Redefine to capture javadoc specific signatures
     * @see com.codenvy.ide.java.client.internal.compiler.ast.ASTNode#traverse(com.codenvy.ide.java.client.internal.compiler.ASTVisitor,
     * com.codenvy.ide.java.client.internal.compiler.lookup.BlockScope)
     */
    @Override
    public void traverse(ASTVisitor visitor, BlockScope scope) {
        visitor.visit(this, scope);
        visitor.endVisit(this, scope);
    }

    @Override
    public void traverse(ASTVisitor visitor, ClassScope scope) {
        visitor.visit(this, scope);
        visitor.endVisit(this, scope);
    }
}
