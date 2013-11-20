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
package com.codenvy.ide.ext.java.worker.core.dom;

import com.codenvy.ide.ext.java.worker.internal.compiler.ASTVisitor;
import com.codenvy.ide.ext.java.worker.internal.compiler.ast.ConstructorDeclaration;
import com.codenvy.ide.ext.java.worker.internal.compiler.ast.FieldDeclaration;
import com.codenvy.ide.ext.java.worker.internal.compiler.ast.TypeDeclaration;
import com.codenvy.ide.ext.java.worker.internal.compiler.ast.Initializer;
import com.codenvy.ide.ext.java.worker.internal.compiler.lookup.ClassScope;
import com.codenvy.ide.ext.java.worker.internal.compiler.lookup.CompilationUnitScope;
import com.codenvy.ide.ext.java.worker.internal.compiler.lookup.MethodScope;
import com.codenvy.ide.ext.java.worker.internal.compiler.ast.MethodDeclaration;

class NodeSearcher extends ASTVisitor {
    public com.codenvy.ide.ext.java.worker.internal.compiler.ast.ASTNode found;

    public TypeDeclaration enclosingType;

    public int position;

    NodeSearcher(int position) {
        this.position = position;
    }

    public boolean visit(ConstructorDeclaration constructorDeclaration, ClassScope scope) {

        if (constructorDeclaration.declarationSourceStart <= this.position
            && this.position <= constructorDeclaration.declarationSourceEnd) {
            this.found = constructorDeclaration;
            return false;
        }
        return true;
    }

    public boolean visit(FieldDeclaration fieldDeclaration, MethodScope scope) {
        if (fieldDeclaration.declarationSourceStart <= this.position
            && this.position <= fieldDeclaration.declarationSourceEnd) {
            this.found = fieldDeclaration;
            return false;
        }
        return true;
    }

    public boolean visit(Initializer initializer, MethodScope scope) {
        if (initializer.declarationSourceStart <= this.position && this.position <= initializer.declarationSourceEnd) {
            this.found = initializer;
            return false;
        }
        return true;
    }

    public boolean visit(TypeDeclaration memberTypeDeclaration, ClassScope scope) {
        if (memberTypeDeclaration.declarationSourceStart <= this.position
            && this.position <= memberTypeDeclaration.declarationSourceEnd) {
            this.enclosingType = memberTypeDeclaration;
            return true;

        }
        return false;
    }

    public boolean visit(MethodDeclaration methodDeclaration, ClassScope scope) {

        if (methodDeclaration.declarationSourceStart <= this.position
            && this.position <= methodDeclaration.declarationSourceEnd) {
            this.found = methodDeclaration;
            return false;
        }
        return true;
    }

    public boolean visit(TypeDeclaration typeDeclaration, CompilationUnitScope scope) {
        if (typeDeclaration.declarationSourceStart <= this.position
            && this.position <= typeDeclaration.declarationSourceEnd) {
            this.enclosingType = typeDeclaration;
            return true;
        }
        return false;
    }

}
