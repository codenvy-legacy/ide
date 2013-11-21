/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.worker.internal.compiler.ast;

import com.codenvy.ide.ext.java.worker.internal.compiler.flow.FlowContext;
import com.codenvy.ide.ext.java.worker.internal.compiler.flow.FlowInfo;
import com.codenvy.ide.ext.java.worker.internal.compiler.lookup.BlockScope;
import com.codenvy.ide.ext.java.worker.internal.compiler.lookup.InvocationSite;
import com.codenvy.ide.ext.java.worker.internal.compiler.lookup.ReferenceBinding;
import com.codenvy.ide.ext.java.worker.internal.compiler.lookup.TypeBinding;

public abstract class AbstractVariableDeclaration extends Statement implements InvocationSite {
    public int declarationEnd;

    /**
     * For local declarations (outside of for statement initialization) and field declarations,
     * the declarationSourceEnd covers multiple locals if any.
     * For local declarations inside for statement initialization, this is not the case.
     */
    public int declarationSourceEnd;

    public int declarationSourceStart;

    public int hiddenVariableDepth; // used to diagnose hiding scenarii

    public Expression initialization;

    public int modifiers;

    public int modifiersSourceStart;

    public Annotation[] annotations;

    public char[] name;

    public TypeReference type;

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        return flowInfo;
    }

    public static final int FIELD = 1;

    public static final int INITIALIZER = 2;

    public static final int ENUM_CONSTANT = 3;

    public static final int LOCAL_VARIABLE = 4;

    public static final int PARAMETER = 5;

    public static final int TYPE_PARAMETER = 6;

    /** @see com.codenvy.ide.ext.java.worker.internal.compiler.lookup.InvocationSite#genericTypeArguments() */
    @Override
    public TypeBinding[] genericTypeArguments() {
        return null;
    }

    /** Returns the constant kind of this variable declaration */
    public abstract int getKind();

    /* (non-Javadoc)
     * @see com.codenvy.ide.java.client.internal.compiler.lookup.InvocationSite#isSuperAccess()
     */
    @Override
    public boolean isSuperAccess() {
        return false;
    }

    /* (non-Javadoc)
     * @see com.codenvy.ide.java.client.internal.compiler.lookup.InvocationSite#isTypeAccess()
     */
    @Override
    public boolean isTypeAccess() {
        return false;
    }

    @Override
    public StringBuffer printStatement(int indent, StringBuffer output) {
        printAsExpression(indent, output);
        switch (getKind()) {
            case ENUM_CONSTANT:
                return output.append(',');
            default:
                return output.append(';');
        }
    }

    public StringBuffer printAsExpression(int indent, StringBuffer output) {
        printIndent(indent, output);
        printModifiers(this.modifiers, output);
        if (this.annotations != null) {
            printAnnotations(this.annotations, output);
        }

        if (this.type != null) {
            this.type.print(0, output).append(' ');
        }
        output.append(this.name);
        switch (getKind()) {
            case ENUM_CONSTANT:
                if (this.initialization != null) {
                    this.initialization.printExpression(indent, output);
                }
                break;
            default:
                if (this.initialization != null) {
                    output.append(" = "); //$NON-NLS-1$
                    this.initialization.printExpression(indent, output);
                }
        }
        return output;
    }

    @Override
    public void resolve(BlockScope scope) {
        // do nothing by default (redefined for local variables)
    }

    /* (non-Javadoc)
     * @see com.codenvy.ide.java.client.internal.compiler.lookup.InvocationSite#setActualReceiverType(com.codenvy.ide.java.client
     * .internal.compiler.lookup.ReferenceBinding)
     */
    @Override
    public void setActualReceiverType(ReferenceBinding receiverType) {
        // do nothing by default
    }

    /* (non-Javadoc)
     * @see com.codenvy.ide.java.client.internal.compiler.lookup.InvocationSite#setDepth(int)
     */
    @Override
    public void setDepth(int depth) {

        this.hiddenVariableDepth = depth;
    }

    /* (non-Javadoc)
     * @see com.codenvy.ide.java.client.internal.compiler.lookup.InvocationSite#setFieldIndex(int)
     */
    @Override
    public void setFieldIndex(int depth) {
        // do nothing by default
    }
}
