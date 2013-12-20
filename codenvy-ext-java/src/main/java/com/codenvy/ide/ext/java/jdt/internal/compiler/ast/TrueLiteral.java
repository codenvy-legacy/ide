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
package com.codenvy.ide.ext.java.jdt.internal.compiler.ast;

import com.codenvy.ide.ext.java.jdt.internal.compiler.ASTVisitor;
import com.codenvy.ide.ext.java.jdt.internal.compiler.codegen.BranchLabel;
import com.codenvy.ide.ext.java.jdt.internal.compiler.impl.BooleanConstant;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.BlockScope;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.TypeBinding;

public class TrueLiteral extends MagicLiteral {

    static final char[] source = {'t', 'r', 'u', 'e'};

    public TrueLiteral(int s, int e) {
        super(s, e);
    }

    @Override
    public void computeConstant() {
        this.constant = BooleanConstant.fromValue(true);
    }

    /**
     * Code generation for the true literal
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
    public void generateOptimizedBoolean(BlockScope currentScope, BranchLabel trueLabel, BranchLabel falseLabel,
                                         boolean valueRequired) {
    }

    @Override
    public TypeBinding literalType(BlockScope scope) {
        return TypeBinding.BOOLEAN;
    }

    /**
     *
     */
    @Override
    public char[] source() {
        return source;
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope scope) {
        visitor.visit(this, scope);
        visitor.endVisit(this, scope);
    }
}
