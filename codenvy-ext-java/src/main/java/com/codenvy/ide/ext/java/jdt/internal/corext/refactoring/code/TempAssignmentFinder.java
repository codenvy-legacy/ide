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
package com.codenvy.ide.ext.java.jdt.internal.corext.refactoring.code;

import com.codenvy.ide.ext.java.jdt.core.dom.*;
import com.codenvy.ide.ext.java.jdt.core.dom.PrefixExpression.Operator;

public class TempAssignmentFinder extends ASTVisitor {
    private ASTNode fFirstAssignment;

    private IVariableBinding fTempBinding;

    TempAssignmentFinder(VariableDeclaration tempDeclaration) {
        fTempBinding = tempDeclaration.resolveBinding();
    }

    private boolean isNameReferenceToTemp(Name name) {
        return fTempBinding == name.resolveBinding();
    }

    private boolean isAssignmentToTemp(Assignment assignment) {
        if (fTempBinding == null)
            return false;

        if (!(assignment.getLeftHandSide() instanceof Name))
            return false;
        Name ref = (Name)assignment.getLeftHandSide();
        return isNameReferenceToTemp(ref);
    }

    boolean hasAssignments() {
        return fFirstAssignment != null;
    }

    ASTNode getFirstAssignment() {
        return fFirstAssignment;
    }

    //-- visit methods

    @Override
    public boolean visit(Assignment assignment) {
        if (!isAssignmentToTemp(assignment))
            return true;

        fFirstAssignment = assignment;
        return false;
    }

    @Override
    public boolean visit(PostfixExpression postfixExpression) {
        if (postfixExpression.getOperand() == null)
            return true;
        if (!(postfixExpression.getOperand() instanceof SimpleName))
            return true;
        SimpleName simpleName = (SimpleName)postfixExpression.getOperand();
        if (!isNameReferenceToTemp(simpleName))
            return true;

        fFirstAssignment = postfixExpression;
        return false;
    }

    @Override
    public boolean visit(PrefixExpression prefixExpression) {
        if (prefixExpression.getOperand() == null)
            return true;
        if (!(prefixExpression.getOperand() instanceof SimpleName))
            return true;
        if (!prefixExpression.getOperator().equals(Operator.DECREMENT)
            && !prefixExpression.getOperator().equals(Operator.INCREMENT))
            return true;
        SimpleName simpleName = (SimpleName)prefixExpression.getOperand();
        if (!isNameReferenceToTemp(simpleName))
            return true;

        fFirstAssignment = prefixExpression;
        return false;
    }
}
