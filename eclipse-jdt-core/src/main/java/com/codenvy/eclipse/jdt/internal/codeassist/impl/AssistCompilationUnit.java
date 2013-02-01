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
import com.codenvy.eclipse.jdt.core.ICompilationUnit;
import com.codenvy.eclipse.jdt.core.IImportContainer;
import com.codenvy.eclipse.jdt.core.IPackageDeclaration;
import com.codenvy.eclipse.jdt.core.IType;
import com.codenvy.eclipse.jdt.core.JavaModelException;
import com.codenvy.eclipse.jdt.core.WorkingCopyOwner;
import com.codenvy.eclipse.jdt.internal.core.CompilationUnit;
import com.codenvy.eclipse.jdt.internal.core.JavaElementInfo;
import com.codenvy.eclipse.jdt.internal.core.PackageFragment;

import java.util.Map;


public class AssistCompilationUnit extends CompilationUnit {
	private Map infoCache;
	private Map bindingCache;
	public AssistCompilationUnit(ICompilationUnit compilationUnit, WorkingCopyOwner owner, Map bindingCache, Map infoCache) {
		super((PackageFragment)compilationUnit.getParent(), compilationUnit.getElementName(), owner);
		this.bindingCache = bindingCache;
		this.infoCache = infoCache;
	}

	public Object getElementInfo(IProgressMonitor monitor) throws JavaModelException {
		return this.infoCache.get(this);
	}

	public IImportContainer getImportContainer() {
		return new AssistImportContainer(this, this.infoCache);
	}

	public IPackageDeclaration getPackageDeclaration(String pkg) {
		return new AssistPackageDeclaration(this, pkg, this.infoCache);
	}

	public IType getType(String typeName) {
		return new AssistSourceType(this, typeName, this.bindingCache, this.infoCache);
	}

	public boolean hasChildren() throws JavaModelException {
		JavaElementInfo info = (JavaElementInfo)this.infoCache.get(this);
		return info.getChildren().length > 0;
	}
}
