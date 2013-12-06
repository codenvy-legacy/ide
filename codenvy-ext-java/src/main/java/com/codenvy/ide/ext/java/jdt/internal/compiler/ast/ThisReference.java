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
package com.codenvy.ide.ext.java.jdt.internal.compiler.ast;

import com.codenvy.ide.ext.java.jdt.internal.compiler.ASTVisitor;
import com.codenvy.ide.ext.java.jdt.internal.compiler.flow.FlowContext;
import com.codenvy.ide.ext.java.jdt.internal.compiler.flow.FlowInfo;
import com.codenvy.ide.ext.java.jdt.internal.compiler.impl.Constant;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.BlockScope;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.ClassScope;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.MethodScope;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.TypeBinding;

public class ThisReference extends Reference {

    public static ThisReference implicitThis() {

        ThisReference implicitThis = new ThisReference(0, 0);
        implicitThis.bits |= IsImplicitThis;
        return implicitThis;
    }

    public ThisReference(int sourceStart, int sourceEnd) {

        this.sourceStart = sourceStart;
        this.sourceEnd = sourceEnd;
    }

    /*
     * @see Reference#analyseAssignment(...)
     */
    @Override
    public FlowInfo analyseAssignment(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo,
                                      Assignment assignment, boolean isCompound) {

        return flowInfo; // this cannot be assigned
    }

    public boolean checkAccess(MethodScope methodScope) {

        // this/super cannot be used in constructor call
        if (methodScope.isConstructorCall) {
            methodScope.problemReporter().fieldsOrThisBeforeConstructorInvocation(this);
            return false;
        }

        // static may not refer to this/super
        if (methodScope.isStatic) {
            methodScope.problemReporter().errorThisSuperInStatic(this);
            return false;
        }
        return true;
    }

    /*
     * @see Reference#generateAssignment(...)
     */
    @Override
    public void generateAssignment(BlockScope currentScope, Assignment assignment, boolean valueRequired) {

        // this cannot be assigned
    }

    @Override
    public void generateCode(BlockScope currentScope, boolean valueRequired) {
    }

    /*
     * @see Reference#generateCompoundAssignment(...)
     */
    @Override
    public void generateCompoundAssignment(BlockScope currentScope, Expression expression, int operator,
                                           int assignmentImplicitConversion, boolean valueRequired) {

        // this cannot be assigned
    }

    /*
     * @see com.codenvy.ide.java.client.internal.compiler.ast.Reference#generatePostIncrement()
     */
    @Override
    public void generatePostIncrement(BlockScope currentScope, CompoundAssignment postIncrement, boolean valueRequired) {

        // this cannot be assigned
    }

    @Override
    public boolean isImplicitThis() {

        return (this.bits & IsImplicitThis) != 0;
    }

    @Override
    public boolean isThis() {

        return true;
    }

    @Override
    public int nullStatus(FlowInfo flowInfo) {
        return FlowInfo.NON_NULL;
    }

    @Override
    public StringBuffer printExpression(int indent, StringBuffer output) {

        if (isImplicitThis()) {
            return output;
        }
        return output.append("this"); //$NON-NLS-1$
    }

    @Override
    public TypeBinding resolveType(BlockScope scope) {

        this.constant = Constant.NotAConstant;
        if (!isImplicitThis() && !checkAccess(scope.methodScope())) {
            return null;
        }
        return this.resolvedType = scope.enclosingReceiverType();
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope blockScope) {

        visitor.visit(this, blockScope);
        visitor.endVisit(this, blockScope);
    }

    @Override
    public void traverse(ASTVisitor visitor, ClassScope blockScope) {

        visitor.visit(this, blockScope);
        visitor.endVisit(this, blockScope);
    }

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        if (!isImplicitThis()) {
            // explicit this reference, not allowed in static context
            // https://bugs.eclipse.org/bugs/show_bug.cgi?id=335780
            currentScope.resetEnclosingMethodStaticFlag();
        }
        return super.analyseCode(currentScope, flowContext, flowInfo);
    }
}
