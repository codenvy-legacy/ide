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
package com.codenvy.ide.ext.java.jdt.internal.corext.refactoring.types;

import com.codenvy.ide.ext.java.jdt.core.dom.ITypeBinding;
import com.codenvy.ide.runtime.Assert;


public final class TypeVariable extends AbstractTypeVariable {

    private ITypeBinding binding;

    protected TypeVariable(TypeEnvironment environment) {
        super(environment);
    }

    protected void initialize(ITypeBinding binding) {
        this.binding = binding;
        Assert.isTrue(binding.isTypeVariable());
        super.initialize(binding);
    }

    @Override
    public int getKind() {
        return TYPE_VARIABLE;
    }

    @Override
    public boolean doEquals(TType type) {
        return binding.isEqualTo(((TypeVariable)type).binding);
    }

    @Override
    public int hashCode() {
        return binding.hashCode();
    }

    @Override
    protected boolean doCanAssignTo(TType lhs) {
        switch (lhs.getKind()) {
            case NULL_TYPE:
            case VOID_TYPE:
                return false;
            case PRIMITIVE_TYPE:

            case ARRAY_TYPE:
                return false;

            case GENERIC_TYPE:
                return false;

            case STANDARD_TYPE:
            case PARAMETERIZED_TYPE:
            case RAW_TYPE:
                return canAssignOneBoundTo(lhs);

            case UNBOUND_WILDCARD_TYPE:
            case EXTENDS_WILDCARD_TYPE:
            case SUPER_WILDCARD_TYPE:
                return ((WildcardType)lhs).checkAssignmentBound(this);

            case TYPE_VARIABLE:
                return doExtends((TypeVariable)lhs);
            case CAPTURE_TYPE:
                return ((CaptureType)lhs).checkLowerBound(this);
        }
        return false;
    }

    private boolean doExtends(TypeVariable other) {
        for (int i = 0; i < fBounds.length; i++) {
            TType bound = fBounds[i];
            if (other.equals(bound) || (bound.getKind() == TYPE_VARIABLE && ((TypeVariable)bound).doExtends(other)))
                return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return binding.getName();
    }

    @Override
    public String getPrettySignature() {
        if (fBounds.length == 1 && fBounds[0].isJavaLangObject())
            return binding.getName(); // don't print the trivial bound

        StringBuffer result = new StringBuffer(binding.getName());
        if (fBounds.length > 0) {
            result.append(" extends "); //$NON-NLS-1$
            result.append(fBounds[0].getPlainPrettySignature());
            for (int i = 1; i < fBounds.length; i++) {
                result.append(" & "); //$NON-NLS-1$
                result.append(fBounds[i].getPlainPrettySignature());
            }
        }
        return result.toString();
    }

    @Override
    protected String getPlainPrettySignature() {
        return binding.getName();
    }
}
