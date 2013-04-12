/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.eclipse.jdt.internal.codeassist.impl;

import com.codenvy.eclipse.core.runtime.IProgressMonitor;
import com.codenvy.eclipse.jdt.core.IImportDeclaration;
import com.codenvy.eclipse.jdt.core.JavaModelException;
import com.codenvy.eclipse.jdt.internal.core.CompilationUnit;
import com.codenvy.eclipse.jdt.internal.core.ImportContainer;

import java.util.Map;


public class AssistImportContainer extends ImportContainer {
    private Map infoCache;

    public AssistImportContainer(CompilationUnit parent, Map infoCache) {
        super(parent);
        this.infoCache = infoCache;
    }

    public Object getElementInfo(IProgressMonitor monitor) throws JavaModelException {
        return this.infoCache.get(this);
    }

    protected IImportDeclaration getImport(String importName, boolean isOnDemand) {
        return new AssistImportDeclaration(this, importName, isOnDemand, this.infoCache);
    }
}
