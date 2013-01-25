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

package com.codenvy.eclipse.jdt.internal.corext.refactoring.typeconstraints;

import com.codenvy.eclipse.jdt.core.dom.Expression;
import com.codenvy.eclipse.jdt.core.dom.IMethodBinding;
import com.codenvy.eclipse.jdt.core.dom.ITypeBinding;
import com.codenvy.eclipse.jdt.core.dom.IVariableBinding;
import com.codenvy.eclipse.jdt.core.dom.ReturnStatement;
import com.codenvy.eclipse.jdt.core.dom.Type;

public interface IConstraintVariableFactory
{
   ConstraintVariable makeExpressionOrTypeVariable(Expression expression, IContext context);

   DeclaringTypeVariable makeDeclaringTypeVariable(ITypeBinding memberTypeBinding);

   DeclaringTypeVariable makeDeclaringTypeVariable(IVariableBinding fieldBinding);

   DeclaringTypeVariable makeDeclaringTypeVariable(IMethodBinding methodBinding);

   ParameterTypeVariable makeParameterTypeVariable(IMethodBinding methodBinding, int parameterIndex);

   RawBindingVariable makeRawBindingVariable(ITypeBinding binding);

   ReturnTypeVariable makeReturnTypeVariable(ReturnStatement returnStatement);

   ReturnTypeVariable makeReturnTypeVariable(IMethodBinding methodBinding);

   TypeVariable makeTypeVariable(Type type);

   TypeVariable makeTypeVariable(ITypeBinding binding, String source, CompilationUnitRange range);
}
