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
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.*;

public class JavadocSingleNameReference extends SingleNameReference {

    public int tagSourceStart, tagSourceEnd;

    public JavadocSingleNameReference(char[] source, long pos, int tagStart, int tagEnd) {
        super(source, pos);
        this.tagSourceStart = tagStart;
        this.tagSourceEnd = tagEnd;
        this.bits |= InsideJavadoc;
    }

    @Override
    public void resolve(BlockScope scope) {
        resolve(scope, true, scope.compilerOptions().reportUnusedParameterIncludeDocCommentReference);
    }

    /** Resolve without warnings */
    public void resolve(BlockScope scope, boolean warn, boolean considerParamRefAsUsage) {

        LocalVariableBinding variableBinding = scope.findVariable(this.token);
        if (variableBinding != null && variableBinding.isValidBinding()
            && ((variableBinding.tagBits & TagBits.IsArgument) != 0)) {
            this.binding = variableBinding;
            if (considerParamRefAsUsage) {
                variableBinding.useFlag = LocalVariableBinding.USED;
            }
            return;
        }
        if (warn) {
            try {
                MethodScope methScope = (MethodScope)scope;
                scope.problemReporter().javadocUndeclaredParamTagName(this.token, this.sourceStart, this.sourceEnd,
                                                                      methScope.referenceMethod().modifiers);
            } catch (Exception e) {
                scope.problemReporter().javadocUndeclaredParamTagName(this.token, this.sourceStart, this.sourceEnd, -1);
            }
        }
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

    /* (non-Javadoc)
     * Redefine to capture javadoc specific signatures
     * @see com.codenvy.ide.java.client.internal.compiler.ast.ASTNode#traverse(com.codenvy.ide.java.client.internal.compiler.ASTVisitor,
     * com.codenvy.ide.java.client.internal.compiler.lookup.BlockScope)
     */
    @Override
    public void traverse(ASTVisitor visitor, ClassScope scope) {
        visitor.visit(this, scope);
        visitor.endVisit(this, scope);
    }
}
