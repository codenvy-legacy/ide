/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.eclipse.jdt.internal.corext.refactoring.typeconstraints;

import com.codenvy.eclipse.core.runtime.Assert;
import com.codenvy.eclipse.jdt.core.ICompilationUnit;
import com.codenvy.eclipse.jdt.core.dom.Assignment;
import com.codenvy.eclipse.jdt.core.dom.ConditionalExpression;
import com.codenvy.eclipse.jdt.core.dom.Expression;
import com.codenvy.eclipse.jdt.core.dom.FieldAccess;
import com.codenvy.eclipse.jdt.core.dom.IBinding;
import com.codenvy.eclipse.jdt.core.dom.MethodInvocation;
import com.codenvy.eclipse.jdt.core.dom.Name;
import com.codenvy.eclipse.jdt.core.dom.ParenthesizedExpression;
import com.codenvy.eclipse.jdt.core.dom.SuperFieldAccess;
import com.codenvy.eclipse.jdt.core.dom.SuperMethodInvocation;

public final class ExpressionVariable extends ConstraintVariable {

    private final CompilationUnitRange fRange;

    private final String fSource;

    private final IBinding fExpressionBinding;

    private final int fExpressionType;

    public ExpressionVariable(Expression expression) {
        super(expression.resolveTypeBinding());
        fSource = expression.toString();
        ICompilationUnit cu = ASTCreator.getCu(expression);
        Assert.isNotNull(cu);
        fRange = new CompilationUnitRange(cu, expression);
        fExpressionBinding = resolveBinding(expression);
        fExpressionType = expression.getNodeType();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "[" + fSource + "]"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    public CompilationUnitRange getCompilationUnitRange() {
        return fRange;
    }

    public int getExpressionType() {
        return fExpressionType;
    }

    public IBinding getExpressionBinding() {
        return fExpressionBinding;
    }

    public static IBinding resolveBinding(Expression expression) {
        if (expression instanceof Name) {
            return ((Name)expression).resolveBinding();
        }
        if (expression instanceof ParenthesizedExpression) {
            return resolveBinding(((ParenthesizedExpression)expression).getExpression());
        } else if (expression instanceof Assignment) {
            return resolveBinding(((Assignment)expression).getLeftHandSide());//TODO ???
        } else if (expression instanceof MethodInvocation) {
            return ((MethodInvocation)expression).resolveMethodBinding();
        } else if (expression instanceof SuperMethodInvocation) {
            return ((SuperMethodInvocation)expression).resolveMethodBinding();
        } else if (expression instanceof FieldAccess) {
            return ((FieldAccess)expression).resolveFieldBinding();
        } else if (expression instanceof SuperFieldAccess) {
            return ((SuperFieldAccess)expression).resolveFieldBinding();
        } else if (expression instanceof ConditionalExpression) {
            return resolveBinding(((ConditionalExpression)expression).getThenExpression());
        }
        return null;
    }

}
