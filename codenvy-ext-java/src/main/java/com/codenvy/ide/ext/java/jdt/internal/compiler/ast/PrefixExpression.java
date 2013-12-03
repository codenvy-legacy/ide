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
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.BlockScope;

public class PrefixExpression extends CompoundAssignment {

    /**
     * PrefixExpression constructor comment.
     *
     * @param lhs
     *         com.codenvy.ide.java.client.internal.compiler.ast.Expression
     * @param expression
     *         com.codenvy.ide.java.client.internal.compiler.ast.Expression
     * @param operator
     *         int
     */
    public PrefixExpression(Expression lhs, Expression expression, int operator, int pos) {
        super(lhs, expression, operator, lhs.sourceEnd);
        this.sourceStart = pos;
        this.sourceEnd = lhs.sourceEnd;
    }

    @Override
    public boolean checkCastCompatibility() {
        return false;
    }

    @Override
    public String operatorToString() {
        switch (this.operator) {
            case PLUS:
                return "++"; //$NON-NLS-1$
            case MINUS:
                return "--"; //$NON-NLS-1$
        }
        return "unknown operator"; //$NON-NLS-1$
    }

    @Override
    public StringBuffer printExpressionNoParenthesis(int indent, StringBuffer output) {

        output.append(operatorToString()).append(' ');
        return this.lhs.printExpression(0, output);
    }

    @Override
    public boolean restrainUsageToNumericTypes() {
        return true;
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope scope) {
        if (visitor.visit(this, scope)) {
            this.lhs.traverse(visitor, scope);
        }
        visitor.endVisit(this, scope);
    }
}
