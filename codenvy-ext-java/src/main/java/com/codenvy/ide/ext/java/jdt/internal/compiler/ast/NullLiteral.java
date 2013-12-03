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
import com.codenvy.ide.ext.java.jdt.internal.compiler.flow.FlowInfo;
import com.codenvy.ide.ext.java.jdt.internal.compiler.impl.Constant;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.BlockScope;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.TypeBinding;

public class NullLiteral extends MagicLiteral {

    static final char[] source = {'n', 'u', 'l', 'l'};

    public NullLiteral(int s, int e) {

        super(s, e);
    }

    @Override
    public void computeConstant() {

        this.constant = Constant.NotAConstant;
    }

    /**
     * Code generation for the null literal
     *
     * @param currentScope
     *         com.codenvy.ide.java.client.internal.compiler.lookup.BlockScope
     * @param valueRequired
     *         boolean
     */
    @Override
    public void generateCode(BlockScope currentScope, boolean valueRequired) {

    }

    @Override
    public TypeBinding literalType(BlockScope scope) {
        return TypeBinding.NULL;
    }

    @Override
    public int nullStatus(FlowInfo flowInfo) {
        return FlowInfo.NULL;
    }

    @Override
    public Object reusableJSRTarget() {
        return TypeBinding.NULL;
    }

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
