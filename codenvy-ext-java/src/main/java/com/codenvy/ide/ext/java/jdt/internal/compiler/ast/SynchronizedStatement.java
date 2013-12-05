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
import com.codenvy.ide.ext.java.jdt.internal.compiler.ClassFileConstants;
import com.codenvy.ide.ext.java.jdt.internal.compiler.flow.FlowContext;
import com.codenvy.ide.ext.java.jdt.internal.compiler.flow.FlowInfo;
import com.codenvy.ide.ext.java.jdt.internal.compiler.flow.InsideSubRoutineFlowContext;
import com.codenvy.ide.ext.java.jdt.internal.compiler.impl.Constant;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.BlockScope;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.LocalVariableBinding;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.TypeBinding;

public class SynchronizedStatement extends SubRoutineStatement {

    public Expression expression;

    public Block block;

    public BlockScope scope;

    public LocalVariableBinding synchroVariable;

    static final char[] SecretLocalDeclarationName = " syncValue".toCharArray(); //$NON-NLS-1$

    // for local variables table attributes
    int preSynchronizedInitStateIndex = -1;

    int mergedSynchronizedInitStateIndex = -1;

    public SynchronizedStatement(Expression expression, Block statement, int s, int e) {

        this.expression = expression;
        this.block = statement;
        this.sourceEnd = e;
        this.sourceStart = s;
    }

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {

        this.preSynchronizedInitStateIndex = currentScope.methodScope().recordInitializationStates(flowInfo);
        // TODO (philippe) shouldn't it be protected by a check whether reachable statement ?

        // mark the synthetic variable as being used
        this.synchroVariable.useFlag = LocalVariableBinding.USED;

        // simple propagation to subnodes
        flowInfo =
                this.block.analyseCode(this.scope, new InsideSubRoutineFlowContext(flowContext, this),
                                       this.expression.analyseCode(this.scope, flowContext, flowInfo));

        this.mergedSynchronizedInitStateIndex = currentScope.methodScope().recordInitializationStates(flowInfo);

        // optimizing code gen
        if ((flowInfo.tagBits & FlowInfo.UNREACHABLE_OR_DEAD) != 0) {
            this.bits |= ASTNode.BlockExit;
        }

        return flowInfo;
    }

    @Override
    public boolean isSubRoutineEscaping() {
        return false;
    }

    /**
     * Synchronized statement code generation
     *
     * @param currentScope
     *         com.codenvy.ide.java.client.internal.compiler.lookup.BlockScope
     * @param codeStream
     *         com.codenvy.ide.java.client.internal.compiler.codegen.CodeStream
     */
    @Override
    public void generateCode(BlockScope currentScope) {
        if ((this.bits & IsReachable) == 0) {
            return;
        }
        // in case the labels needs to be reinitialized
        // when the code generation is restarted in wide mode

        // generate the synchronization expression
        this.expression.generateCode(this.scope, true);
        if (!this.block.isEmptyBlock()) {
            // generate  the body of the synchronized block
            this.block.generateCode(this.scope);
        }
    }

    /** @see SubRoutineStatement#generateSubRoutineInvocation(BlockScope, CodeStream, Object, int, LocalVariableBinding) */
    @Override
    public boolean generateSubRoutineInvocation(BlockScope currentScope, Object targetLocation, int stateIndex,
                                                LocalVariableBinding secretLocal) {
        return false;
    }

    @Override
    public void resolve(BlockScope upperScope) {
        // special scope for secret locals optimization.
        this.scope = new BlockScope(upperScope);
        TypeBinding type = this.expression.resolveType(this.scope);
        if (type == null) {
            return;
        }
        switch (type.id) {
            case T_boolean:
            case T_char:
            case T_float:
            case T_double:
            case T_byte:
            case T_short:
            case T_int:
            case T_long:
                this.scope.problemReporter().invalidTypeToSynchronize(this.expression, type);
                break;
            case T_void:
                this.scope.problemReporter().illegalVoidExpression(this.expression);
                break;
            case T_null:
                this.scope.problemReporter().invalidNullToSynchronize(this.expression);
                break;
        }
        //continue even on errors in order to have the TC done into the statements
        this.synchroVariable =
                new LocalVariableBinding(SecretLocalDeclarationName, type, ClassFileConstants.AccDefault, false);
        this.scope.addLocalVariable(this.synchroVariable);
        this.synchroVariable.setConstant(Constant.NotAConstant); // not inlinable
        this.expression.computeConversion(this.scope, type, type);
        this.block.resolveUsing(this.scope);
    }

    @Override
    public StringBuffer printStatement(int indent, StringBuffer output) {
        printIndent(indent, output);
        output.append("synchronized ("); //$NON-NLS-1$
        this.expression.printExpression(0, output).append(')');
        output.append('\n');
        return this.block.printStatement(indent + 1, output);
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope blockScope) {
        if (visitor.visit(this, blockScope)) {
            this.expression.traverse(visitor, this.scope);
            this.block.traverse(visitor, this.scope);
        }
        visitor.endVisit(this, blockScope);
    }
}
