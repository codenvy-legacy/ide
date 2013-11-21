/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.worker.internal.compiler.ast;

import com.codenvy.ide.ext.java.worker.internal.compiler.ASTVisitor;
import com.codenvy.ide.ext.java.worker.internal.compiler.flow.FlowContext;
import com.codenvy.ide.ext.java.worker.internal.compiler.flow.FlowInfo;
import com.codenvy.ide.ext.java.worker.internal.compiler.impl.Constant;
import com.codenvy.ide.ext.java.worker.internal.compiler.lookup.BlockScope;
import com.codenvy.ide.ext.java.worker.internal.compiler.lookup.ClassScope;
import com.codenvy.ide.ext.java.worker.internal.compiler.lookup.ReferenceBinding;
import com.codenvy.ide.ext.java.worker.internal.compiler.lookup.TypeBinding;

public class QualifiedThisReference extends ThisReference {

    public TypeReference qualification;

    ReferenceBinding currentCompatibleType;

    public QualifiedThisReference(TypeReference name, int sourceStart, int sourceEnd) {
        super(sourceStart, sourceEnd);
        this.qualification = name;
        name.bits |= IgnoreRawTypeCheck; // no need to worry about raw type usage
        this.sourceStart = name.sourceStart;
    }

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {

        return flowInfo;
    }

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo,
                                boolean valueRequired) {

        return flowInfo;
    }

    /**
     * Code generation for QualifiedThisReference
     *
     * @param currentScope
     *         com.codenvy.ide.java.client.internal.compiler.lookup.BlockScope
     * @param codeStream
     *         com.codenvy.ide.java.client.internal.compiler.codegen.CodeStream
     * @param valueRequired
     *         boolean
     */
    @Override
    public void generateCode(BlockScope currentScope, boolean valueRequired) {
    }

    @Override
    public TypeBinding resolveType(BlockScope scope) {

        this.constant = Constant.NotAConstant;
        // X.this is not a param/raw type as denoting enclosing instance
        TypeBinding type = this.qualification.resolveType(scope, true /* check bounds*/);
        if (type == null || !type.isValidBinding()) {
            return null;
        }
        // X.this is not a param/raw type as denoting enclosing instance
        type = type.erasure();

        // resolvedType needs to be converted to parameterized
        if (type instanceof ReferenceBinding) {
            this.resolvedType = scope.environment().convertToParameterizedType((ReferenceBinding)type);
        } else {
            // error case
            this.resolvedType = type;
        }

        // the qualification MUST exactly match some enclosing type name
        // It is possible to qualify 'this' by the name of the current class
        int depth = 0;
        this.currentCompatibleType = scope.referenceType().binding;
        while (this.currentCompatibleType != null && this.currentCompatibleType != type) {
            depth++;
            this.currentCompatibleType =
                    this.currentCompatibleType.isStatic() ? null : this.currentCompatibleType.enclosingType();
        }
        this.bits &= ~DepthMASK; // flush previous depth if any
        this.bits |= (depth & 0xFF) << DepthSHIFT; // encoded depth into 8 bits

        if (this.currentCompatibleType == null) {
            scope.problemReporter().noSuchEnclosingInstance(type, this, false);
            return this.resolvedType;
        }

        // Ensure one cannot write code like: B() { super(B.this); }
        if (depth == 0) {
            checkAccess(scope.methodScope());
        } // if depth>0, path emulation will diagnose bad scenarii

        return this.resolvedType;
    }

    @Override
    public StringBuffer printExpression(int indent, StringBuffer output) {

        return this.qualification.print(0, output).append(".this"); //$NON-NLS-1$
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope blockScope) {

        if (visitor.visit(this, blockScope)) {
            this.qualification.traverse(visitor, blockScope);
        }
        visitor.endVisit(this, blockScope);
    }

    @Override
    public void traverse(ASTVisitor visitor, ClassScope blockScope) {

        if (visitor.visit(this, blockScope)) {
            this.qualification.traverse(visitor, blockScope);
        }
        visitor.endVisit(this, blockScope);
    }
}
