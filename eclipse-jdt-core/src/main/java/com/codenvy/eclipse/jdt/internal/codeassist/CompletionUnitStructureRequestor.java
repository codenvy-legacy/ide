/*******************************************************************************
 * Copyright (c) 2008, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.eclipse.jdt.internal.codeassist;

import com.codenvy.eclipse.jdt.core.IAnnotation;
import com.codenvy.eclipse.jdt.core.ICompilationUnit;
import com.codenvy.eclipse.jdt.core.IMemberValuePair;
import com.codenvy.eclipse.jdt.internal.codeassist.complete.CompletionOnMarkerAnnotationName;
import com.codenvy.eclipse.jdt.internal.codeassist.complete.CompletionOnMemberValueName;
import com.codenvy.eclipse.jdt.internal.codeassist.complete.CompletionOnParameterizedQualifiedTypeReference;
import com.codenvy.eclipse.jdt.internal.codeassist.complete.CompletionOnQualifiedNameReference;
import com.codenvy.eclipse.jdt.internal.codeassist.complete.CompletionOnQualifiedTypeReference;
import com.codenvy.eclipse.jdt.internal.codeassist.complete.CompletionOnSingleNameReference;
import com.codenvy.eclipse.jdt.internal.codeassist.complete.CompletionOnSingleTypeReference;
import com.codenvy.eclipse.jdt.internal.codeassist.impl.AssistAnnotation;
import com.codenvy.eclipse.jdt.internal.codeassist.impl.AssistImportContainer;
import com.codenvy.eclipse.jdt.internal.codeassist.impl.AssistImportDeclaration;
import com.codenvy.eclipse.jdt.internal.codeassist.impl.AssistInitializer;
import com.codenvy.eclipse.jdt.internal.codeassist.impl.AssistPackageDeclaration;
import com.codenvy.eclipse.jdt.internal.codeassist.impl.AssistSourceField;
import com.codenvy.eclipse.jdt.internal.codeassist.impl.AssistSourceMethod;
import com.codenvy.eclipse.jdt.internal.codeassist.impl.AssistSourceType;
import com.codenvy.eclipse.jdt.internal.codeassist.impl.AssistTypeParameter;
import com.codenvy.eclipse.jdt.internal.compiler.ast.ASTNode;
import com.codenvy.eclipse.jdt.internal.compiler.ast.Expression;
import com.codenvy.eclipse.jdt.internal.compiler.ast.MemberValuePair;
import com.codenvy.eclipse.jdt.internal.compiler.ast.ParameterizedQualifiedTypeReference;
import com.codenvy.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import com.codenvy.eclipse.jdt.internal.compiler.ast.TypeReference;
import com.codenvy.eclipse.jdt.internal.compiler.parser.Parser;
import com.codenvy.eclipse.jdt.internal.core.AnnotatableInfo;
import com.codenvy.eclipse.jdt.internal.core.Annotation;
import com.codenvy.eclipse.jdt.internal.core.CompilationUnit;
import com.codenvy.eclipse.jdt.internal.core.CompilationUnitElementInfo;
import com.codenvy.eclipse.jdt.internal.core.CompilationUnitStructureRequestor;
import com.codenvy.eclipse.jdt.internal.core.ImportContainer;
import com.codenvy.eclipse.jdt.internal.core.ImportDeclaration;
import com.codenvy.eclipse.jdt.internal.core.Initializer;
import com.codenvy.eclipse.jdt.internal.core.JavaElement;
import com.codenvy.eclipse.jdt.internal.core.JavaModelManager;
import com.codenvy.eclipse.jdt.internal.core.PackageDeclaration;
import com.codenvy.eclipse.jdt.internal.core.SourceField;
import com.codenvy.eclipse.jdt.internal.core.SourceMethod;
import com.codenvy.eclipse.jdt.internal.core.SourceType;
import com.codenvy.eclipse.jdt.internal.core.TypeParameter;

import java.util.Map;


public class CompletionUnitStructureRequestor extends CompilationUnitStructureRequestor {
    private ASTNode assistNode;

    private Map bindingCache;
    private Map elementCache;
    private Map elementWithProblemCache;

    public CompletionUnitStructureRequestor(
            ICompilationUnit unit,
            CompilationUnitElementInfo unitInfo,
            Parser parser,
            ASTNode assistNode,
            Map bindingCache,
            Map elementCache,
            Map elementWithProblemCache,
            Map newElements) {
        super(unit, unitInfo, newElements);
        this.parser = parser;
        this.assistNode = assistNode;
        this.bindingCache = bindingCache;
        this.elementCache = elementCache;
        this.elementWithProblemCache = elementWithProblemCache;
    }

    protected Annotation createAnnotation(JavaElement parent, String name) {
        return new AssistAnnotation(parent, name, this.newElements);
    }

    protected SourceField createField(JavaElement parent, FieldInfo fieldInfo) {
        String fieldName = JavaModelManager.getJavaModelManager().intern(new String(fieldInfo.name));
        AssistSourceField field = new AssistSourceField(parent, fieldName, this.bindingCache, this.newElements);
        if (fieldInfo.node.binding != null) {
            this.bindingCache.put(field, fieldInfo.node.binding);
            this.elementCache.put(fieldInfo.node.binding, field);
        } else {
            this.elementWithProblemCache.put(fieldInfo.node, field);
        }
        return field;
    }

    protected ImportContainer createImportContainer(ICompilationUnit parent) {
        return new AssistImportContainer((CompilationUnit)parent, this.newElements);
    }

    protected ImportDeclaration createImportDeclaration(ImportContainer parent, String name, boolean onDemand) {
        return new AssistImportDeclaration(parent, name, onDemand, this.newElements);
    }

    protected Initializer createInitializer(JavaElement parent) {
        return new AssistInitializer(parent, 1, this.bindingCache, this.newElements);
    }

    protected SourceMethod createMethodHandle(JavaElement parent, MethodInfo methodInfo) {
        String selector = JavaModelManager.getJavaModelManager().intern(new String(methodInfo.name));
        String[] parameterTypeSigs = convertTypeNamesToSigs(methodInfo.parameterTypes);
        AssistSourceMethod method = new AssistSourceMethod(parent, selector, parameterTypeSigs, this.bindingCache, this.newElements);
        if (methodInfo.node.binding != null) {
            this.bindingCache.put(method, methodInfo.node.binding);
            this.elementCache.put(methodInfo.node.binding, method);
        } else {
            this.elementWithProblemCache.put(methodInfo.node, method);
        }
        return method;
    }

    protected PackageDeclaration createPackageDeclaration(JavaElement parent, String name) {
        return new AssistPackageDeclaration((CompilationUnit)parent, name, this.newElements);
    }

    protected SourceType createTypeHandle(JavaElement parent, TypeInfo typeInfo) {
        String nameString = new String(typeInfo.name);
        AssistSourceType type = new AssistSourceType(parent, nameString, this.bindingCache, this.newElements);
        if (typeInfo.node.binding != null) {
            this.bindingCache.put(type, typeInfo.node.binding);
            this.elementCache.put(typeInfo.node.binding, type);
        } else {
            this.elementWithProblemCache.put(typeInfo.node, type);
        }
        return type;
    }

    protected TypeParameter createTypeParameter(JavaElement parent, String name) {
        return new AssistTypeParameter(parent, name, this.newElements);
    }

    protected IAnnotation acceptAnnotation(
            com.codenvy.eclipse.jdt.internal.compiler.ast.Annotation annotation,
            AnnotatableInfo parentInfo,
            JavaElement parentHandle) {
        if (annotation instanceof CompletionOnMarkerAnnotationName) {
            if (hasEmptyName(annotation.type, this.assistNode)) {
                super.acceptAnnotation(annotation, null, parentHandle);
                return null;
            }
        }
        return super.acceptAnnotation(annotation, parentInfo, parentHandle);
    }

    protected Object getMemberValue(
            com.codenvy.eclipse.jdt.internal.core.MemberValuePair memberValuePair,
            Expression expression) {
        if (expression instanceof CompletionOnSingleNameReference) {
            CompletionOnSingleNameReference reference = (CompletionOnSingleNameReference)expression;
            if (reference.token.length == 0) return null;
        } else if (expression instanceof CompletionOnQualifiedNameReference) {
            CompletionOnQualifiedNameReference reference = (CompletionOnQualifiedNameReference)expression;
            if (reference.tokens[reference.tokens.length - 1].length == 0) return null;
        }
        return super.getMemberValue(memberValuePair, expression);
    }

    protected IMemberValuePair[] getMemberValuePairs(MemberValuePair[] memberValuePairs) {
        int membersLength = memberValuePairs.length;
        int membersCount = 0;
        IMemberValuePair[] members = new IMemberValuePair[membersLength];
        next:
        for (int j = 0; j < membersLength; j++) {
            if (memberValuePairs[j] instanceof CompletionOnMemberValueName) continue next;

            members[membersCount++] = getMemberValuePair(memberValuePairs[j]);
        }

        if (membersCount > membersLength) {
            System.arraycopy(members, 0, members, 0, membersCount);
        }
        return members;
    }

    protected static boolean hasEmptyName(TypeReference reference, ASTNode assistNode) {
        if (reference == null) return false;

        if (reference.sourceStart <= assistNode.sourceStart && assistNode.sourceEnd <= reference.sourceEnd) return false;

        if (reference instanceof CompletionOnSingleTypeReference ||
            reference instanceof CompletionOnQualifiedTypeReference ||
            reference instanceof CompletionOnParameterizedQualifiedTypeReference) {
            char[][] typeName = reference.getTypeName();
            if (typeName[typeName.length - 1].length == 0) return true;
        }
        if (reference instanceof ParameterizedSingleTypeReference) {
            ParameterizedSingleTypeReference parameterizedReference = (ParameterizedSingleTypeReference)reference;
            TypeReference[] typeArguments = parameterizedReference.typeArguments;
            if (typeArguments != null) {
                for (int i = 0; i < typeArguments.length; i++) {
                    if (hasEmptyName(typeArguments[i], assistNode)) return true;
                }
            }
        } else if (reference instanceof ParameterizedQualifiedTypeReference) {
            ParameterizedQualifiedTypeReference parameterizedReference = (ParameterizedQualifiedTypeReference)reference;
            TypeReference[][] typeArguments = parameterizedReference.typeArguments;
            if (typeArguments != null) {
                for (int i = 0; i < typeArguments.length; i++) {
                    if (typeArguments[i] != null) {
                        for (int j = 0; j < typeArguments[i].length; j++) {
                            if (hasEmptyName(typeArguments[i][j], assistNode)) return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
