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
import com.codenvy.ide.ext.java.jdt.internal.compiler.ClassFileConstants;
import com.codenvy.ide.ext.java.jdt.internal.compiler.flow.FlowContext;
import com.codenvy.ide.ext.java.jdt.internal.compiler.flow.FlowInfo;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.BlockScope;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.FieldBinding;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.MethodScope;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.ReferenceBinding;
import com.codenvy.ide.ext.java.jdt.internal.compiler.parser.Parser;

public class Initializer extends FieldDeclaration {

    public Block block;

    public int lastVisibleFieldID;

    public int bodyStart;

    public int bodyEnd;

    public Initializer(Block block, int modifiers) {
        this.block = block;
        this.modifiers = modifiers;

        if (block != null) {
            this.declarationSourceStart = this.sourceStart = block.sourceStart;
        }
    }

    @Override
    public FlowInfo analyseCode(MethodScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {

        if (this.block != null) {
            return this.block.analyseCode(currentScope, flowContext, flowInfo);
        }
        return flowInfo;
    }

    /**
     * Code generation for a non-static initializer:
     * standard block code gen
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
        if (this.block != null) {
            this.block.generateCode(currentScope);
        }
    }

    /** @see com.codenvy.ide.ext.java.jdt.internal.compiler.ast.AbstractVariableDeclaration#getKind() */
    @Override
    public int getKind() {
        return INITIALIZER;
    }

    @Override
    public boolean isStatic() {

        return (this.modifiers & ClassFileConstants.AccStatic) != 0;
    }

    public void parseStatements(Parser parser, TypeDeclaration typeDeclaration, CompilationUnitDeclaration unit) {

        //fill up the method body with statement
        parser.parse(this, typeDeclaration, unit);
    }

    @Override
    public StringBuffer printStatement(int indent, StringBuffer output) {

        if (this.modifiers != 0) {
            printIndent(indent, output);
            printModifiers(this.modifiers, output);
            if (this.annotations != null) {
                printAnnotations(this.annotations, output);
            }
            output.append("{\n"); //$NON-NLS-1$
            if (this.block != null) {
                this.block.printBody(indent, output);
            }
            printIndent(indent, output).append('}');
            return output;
        } else if (this.block != null) {
            this.block.printStatement(indent, output);
        } else {
            printIndent(indent, output).append("{}"); //$NON-NLS-1$
        }
        return output;
    }

    @Override
    public void resolve(MethodScope scope) {

        FieldBinding previousField = scope.initializedField;
        int previousFieldID = scope.lastVisibleFieldID;
        try {
            scope.initializedField = null;
            scope.lastVisibleFieldID = this.lastVisibleFieldID;
            if (isStatic()) {
                ReferenceBinding declaringType = scope.enclosingSourceType();
                if (declaringType.isNestedType() && !declaringType.isStatic()) {
                    scope.problemReporter().innerTypesCannotDeclareStaticInitializers(declaringType, this);
                }
            }
            if (this.block != null) {
                this.block.resolve(scope);
            }
        } finally {
            scope.initializedField = previousField;
            scope.lastVisibleFieldID = previousFieldID;
        }
    }

    @Override
    public void traverse(ASTVisitor visitor, MethodScope scope) {
        if (visitor.visit(this, scope)) {
            if (this.block != null) {
                this.block.traverse(visitor, scope);
            }
        }
        visitor.endVisit(this, scope);
    }
}
