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
package com.codenvy.ide.ext.java.worker.internal.codeassist.complete;

import com.codenvy.ide.ext.java.worker.internal.compiler.CompilationResult;
import com.codenvy.ide.ext.java.worker.internal.compiler.ast.MethodDeclaration;
import com.codenvy.ide.ext.java.worker.internal.compiler.ast.TypeReference;

public class CompletionOnMethodReturnType extends MethodDeclaration {
    public CompletionOnMethodReturnType(TypeReference returnType, CompilationResult compilationResult) {
        super(compilationResult);
        this.returnType = returnType;
        this.sourceStart = returnType.sourceStart;
        this.sourceEnd = returnType.sourceEnd;
    }

    public void resolveStatements() {
        throw new CompletionNodeFound(this, this.scope);
    }

    public StringBuffer print(int tab, StringBuffer output) {
        return this.returnType.print(tab, output);
    }

}
