/*******************************************************************************
 * Copyright (c) 2005, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/**
 *
 **/
package com.codenvy.ide.ext.java.jdt.internal.corext.fix;

import com.codenvy.ide.ext.java.jdt.core.JavaCore;
import com.codenvy.ide.ext.java.jdt.core.dom.CompilationUnit;
import com.codenvy.ide.ext.java.jdt.core.dom.ForStatement;
import com.codenvy.ide.ext.java.jdt.core.dom.SingleVariableDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.Statement;
import com.codenvy.ide.ext.java.jdt.core.dom.VariableDeclarationFragment;
import com.codenvy.ide.ext.java.jdt.internal.corext.dom.GenericVisitor;
import com.codenvy.ide.ext.java.jdt.internal.corext.dom.ScopeAnalyzer;
import com.codenvy.ide.ext.java.jdt.internal.corext.refactoring.structure.CompilationUnitRewrite;
import com.codenvy.ide.runtime.CoreException;
import com.codenvy.ide.runtime.IStatus;
import com.codenvy.ide.runtime.Status;
import com.codenvy.ide.text.edits.TextEditGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class ConvertLoopOperation extends CompilationUnitRewriteOperationsFix.CompilationUnitRewriteOperation {

    protected static final String FOR_LOOP_ELEMENT_IDENTIFIER = "element"; //$NON-NLS-1$

    protected static final IStatus ERROR_STATUS = new Status(IStatus.ERROR, JavaCore.PLUGIN_ID, ""); //$NON-NLS-1$

    private final ForStatement fStatement;

    private ConvertLoopOperation fOperation;

    private final String[] fUsedNames;

    public ConvertLoopOperation(ForStatement statement, String[] usedNames) {
        fStatement = statement;
        fUsedNames = usedNames;
    }

    public void setBodyConverter(ConvertLoopOperation operation) {
        fOperation = operation;
    }

    public abstract String getIntroducedVariableName();

    public abstract IStatus satisfiesPreconditions();

    protected abstract Statement convert(CompilationUnitRewrite cuRewrite, TextEditGroup group) throws CoreException;

    protected ForStatement getForStatement() {
        return fStatement;
    }

    protected Statement getBody(CompilationUnitRewrite cuRewrite, TextEditGroup group) throws CoreException {
        if (fOperation != null) {
            return fOperation.convert(cuRewrite, group);
        } else {
            return (Statement)cuRewrite.getASTRewrite().createMoveTarget(getForStatement().getBody());
        }
    }

    protected String[] getUsedVariableNames() {
        final List<String> results = new ArrayList<String>();

        ForStatement forStatement = getForStatement();
        CompilationUnit root = (CompilationUnit)forStatement.getRoot();

        Collection<String> variableNames =
                new ScopeAnalyzer(root).getUsedVariableNames(forStatement.getStartPosition(), forStatement.getLength());
        results.addAll(variableNames);

        forStatement.accept(new GenericVisitor() {
            @Override
            public boolean visit(SingleVariableDeclaration node) {
                results.add(node.getName().getIdentifier());
                return super.visit(node);
            }

            @Override
            public boolean visit(VariableDeclarationFragment fragment) {
                results.add(fragment.getName().getIdentifier());
                return super.visit(fragment);
            }
        });

        results.addAll(Arrays.asList(fUsedNames));

        return results.toArray(new String[results.size()]);
    }

}