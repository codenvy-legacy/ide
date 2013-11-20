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
import com.codenvy.ide.ext.java.worker.internal.compiler.codegen.BranchLabel;
import com.codenvy.ide.ext.java.worker.internal.compiler.flow.FlowContext;
import com.codenvy.ide.ext.java.worker.internal.compiler.flow.FlowInfo;
import com.codenvy.ide.ext.java.worker.internal.compiler.flow.LabelFlowContext;
import com.codenvy.ide.ext.java.worker.internal.compiler.flow.UnconditionalFlowInfo;
import com.codenvy.ide.ext.java.worker.internal.compiler.lookup.BlockScope;

public class LabeledStatement extends Statement {

    public Statement statement;

    public char[] label;

    public BranchLabel targetLabel;

    public int labelEnd;

    // for local variables table attributes
    int mergedInitStateIndex = -1;

    /** LabeledStatement constructor comment. */
    public LabeledStatement(char[] label, Statement statement, long labelPosition, int sourceEnd) {

        this.statement = statement;
        // remember useful empty statement
        if (statement instanceof EmptyStatement) {
            statement.bits |= IsUsefulEmptyStatement;
        }
        this.label = label;
        this.sourceStart = (int)(labelPosition >>> 32);
        this.labelEnd = (int)labelPosition;
        this.sourceEnd = sourceEnd;
    }

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {

        // need to stack a context to store explicit label, answer inits in case of normal completion merged
        // with those relative to the exit path from break statement occurring inside the labeled statement.
        if (this.statement == null) {
            return flowInfo;
        } else {
            LabelFlowContext labelContext;
            FlowInfo statementInfo, mergedInfo;
            statementInfo =
                    this.statement.analyseCode(currentScope,
                                               (labelContext =
                                                       new LabelFlowContext(flowContext, this, this.label,
                                                                            (this.targetLabel = new BranchLabel()),
                                                                            currentScope)), flowInfo);
            boolean reinjectNullInfo =
                    (statementInfo.tagBits & FlowInfo.UNREACHABLE) != 0
                    && (labelContext.initsOnBreak.tagBits & FlowInfo.UNREACHABLE) == 0;
            mergedInfo = statementInfo.mergedWith(labelContext.initsOnBreak);
            if (reinjectNullInfo) {
                // an embedded loop has had no chance to reinject forgotten null info
                ((UnconditionalFlowInfo)mergedInfo).addInitializationsFrom(flowInfo.unconditionalFieldLessCopy())
                                                   .addInitializationsFrom(labelContext.initsOnBreak.unconditionalFieldLessCopy());
            }
            this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(mergedInfo);
            if ((this.bits & ASTNode.LabelUsed) == 0) {
                currentScope.problemReporter().unusedLabel(this);
            }
            return mergedInfo;
        }
    }

    @Override
    public ASTNode concreteStatement() {

        // return statement.concreteStatement(); // for supporting nested labels:   a:b:c: someStatement (see 21912)
        return this.statement;
    }

    /**
     * Code generation for labeled statement
     * <p/>
     * may not need actual source positions recording
     *
     * @param currentScope
     *         com.codenvy.ide.java.client.internal.compiler.lookup.BlockScope
     */
    @Override
    public void generateCode(BlockScope currentScope) {

        if ((this.bits & IsReachable) == 0) {
            return;
        }
        if (this.targetLabel != null) {
            if (this.statement != null) {
                this.statement.generateCode(currentScope);
            }
        }
    }

    @Override
    public StringBuffer printStatement(int tab, StringBuffer output) {

        printIndent(tab, output).append(this.label).append(": "); //$NON-NLS-1$
        if (this.statement == null) {
            output.append(';');
        } else {
            this.statement.printStatement(0, output);
        }
        return output;
    }

    @Override
    public void resolve(BlockScope scope) {

        if (this.statement != null) {
            this.statement.resolve(scope);
        }
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope blockScope) {

        if (visitor.visit(this, blockScope)) {
            if (this.statement != null) {
                this.statement.traverse(visitor, blockScope);
            }
        }
        visitor.endVisit(this, blockScope);
    }
}
