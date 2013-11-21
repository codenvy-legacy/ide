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

import com.codenvy.ide.ext.java.worker.internal.compiler.ASTVisitor;
import com.codenvy.ide.ext.java.worker.internal.compiler.ClassFileConstants;
import com.codenvy.ide.ext.java.worker.internal.compiler.impl.Constant;
import com.codenvy.ide.ext.java.worker.internal.compiler.lookup.BlockScope;
import com.codenvy.ide.ext.java.worker.internal.compiler.lookup.ClassScope;
import com.codenvy.ide.ext.java.worker.internal.compiler.lookup.Scope;
import com.codenvy.ide.ext.java.worker.internal.compiler.lookup.TypeBinding;

public class JavadocArgumentExpression extends Expression {
    public char[] token;

    public Argument argument;

    public JavadocArgumentExpression(char[] name, int startPos, int endPos, TypeReference typeRef) {
        this.token = name;
        this.sourceStart = startPos;
        this.sourceEnd = endPos;
        long pos = (((long)startPos) << 32) + endPos;
        this.argument = new Argument(name, pos, typeRef, ClassFileConstants.AccDefault);
        this.bits |= InsideJavadoc;
    }

    /*
     * Resolves type on a Block or Class scope.
     */
    private TypeBinding internalResolveType(Scope scope) {
        this.constant = Constant.NotAConstant;
        if (this.resolvedType != null) {
            return this.resolvedType.isValidBinding() ? this.resolvedType : null; // already reported error
        }

        if (this.argument != null) {
            TypeReference typeRef = this.argument.type;
            if (typeRef != null) {
                this.resolvedType = typeRef.getTypeBinding(scope);
                typeRef.resolvedType = this.resolvedType;
                // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=195374
                // reproduce javadoc 1.3.1 / 1.4.2 behavior
                if (this.resolvedType == null) {
                    return null;
                }
                if (typeRef instanceof SingleTypeReference && this.resolvedType.leafComponentType().enclosingType() != null
                    && scope.compilerOptions().complianceLevel <= ClassFileConstants.JDK1_4) {
                    scope.problemReporter().javadocInvalidMemberTypeQualification(this.sourceStart, this.sourceEnd,
                                                                                  scope.getDeclarationModifiers());
                    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=228648
                    // do not return now but report unresolved reference as expected depending on compliance settings
                } else if (typeRef instanceof QualifiedTypeReference) {
                    TypeBinding enclosingType = this.resolvedType.leafComponentType().enclosingType();
                    if (enclosingType != null) {
                        // https://bugs.eclipse.org/bugs/show_bug.cgi?id=233187
                        // inner type references should be fully qualified
                        int compoundLength = 2;
                        while ((enclosingType = enclosingType.enclosingType()) != null) {
                            compoundLength++;
                        }
                        int typeNameLength = typeRef.getTypeName().length;
                        if (typeNameLength != compoundLength
                            && typeNameLength != (compoundLength + this.resolvedType.getPackage().compoundName.length)) {
                            scope.problemReporter().javadocInvalidMemberTypeQualification(typeRef.sourceStart,
                                                                                          typeRef.sourceEnd,
                                                                                          scope.getDeclarationModifiers());
                        }
                    }
                }
                if (!this.resolvedType.isValidBinding()) {
                    scope.problemReporter().javadocInvalidType(typeRef, this.resolvedType, scope.getDeclarationModifiers());
                    return null;
                }
                if (isTypeUseDeprecated(this.resolvedType, scope)) {
                    scope.problemReporter().javadocDeprecatedType(this.resolvedType, typeRef,
                                                                  scope.getDeclarationModifiers());
                }
                return this.resolvedType =
                        scope.environment()
                             .convertToRawType(this.resolvedType, true /*force the conversion of enclosing types*/);
            }
        }
        return null;
    }

    @Override
    public StringBuffer printExpression(int indent, StringBuffer output) {
        if (this.argument == null) {
            if (this.token != null) {
                output.append(this.token);
            }
        } else {
            this.argument.print(indent, output);
        }
        return output;
    }

    @Override
    public void resolve(BlockScope scope) {
        if (this.argument != null) {
            this.argument.resolve(scope);
        }
    }

    @Override
    public TypeBinding resolveType(BlockScope scope) {
        return internalResolveType(scope);
    }

    @Override
    public TypeBinding resolveType(ClassScope scope) {
        return internalResolveType(scope);
    }

    /* (non-Javadoc)
     * Redefine to capture javadoc specific signatures
     * @see com.codenvy.ide.java.client.internal.compiler.ast.ASTNode#traverse(com.codenvy.ide.java.client.internal.compiler.ASTVisitor,
     * com.codenvy.ide.java.client.internal.compiler.lookup.BlockScope)
     */
    @Override
    public void traverse(ASTVisitor visitor, BlockScope blockScope) {
        if (visitor.visit(this, blockScope)) {
            if (this.argument != null) {
                this.argument.traverse(visitor, blockScope);
            }
        }
        visitor.endVisit(this, blockScope);
    }

    @Override
    public void traverse(ASTVisitor visitor, ClassScope blockScope) {
        if (visitor.visit(this, blockScope)) {
            if (this.argument != null) {
                this.argument.traverse(visitor, blockScope);
            }
        }
        visitor.endVisit(this, blockScope);
    }
}
