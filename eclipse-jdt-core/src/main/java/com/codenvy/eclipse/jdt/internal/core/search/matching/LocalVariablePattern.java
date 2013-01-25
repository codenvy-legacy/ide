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
package com.codenvy.eclipse.jdt.internal.core.search.matching;

import com.codenvy.eclipse.core.runtime.IPath;
import com.codenvy.eclipse.core.runtime.IProgressMonitor;
import com.codenvy.eclipse.core.runtime.OperationCanceledException;
import com.codenvy.eclipse.jdt.core.IJavaElement;
import com.codenvy.eclipse.jdt.core.IPackageFragmentRoot;
import com.codenvy.eclipse.jdt.core.IType;
import com.codenvy.eclipse.jdt.core.search.IJavaSearchScope;
import com.codenvy.eclipse.jdt.core.search.SearchParticipant;
import com.codenvy.eclipse.jdt.internal.compiler.env.AccessRuleSet;
import com.codenvy.eclipse.jdt.internal.compiler.util.SuffixConstants;
import com.codenvy.eclipse.jdt.internal.core.LocalVariable;
import com.codenvy.eclipse.jdt.internal.core.index.Index;
import com.codenvy.eclipse.jdt.internal.core.search.IndexQueryRequestor;
import com.codenvy.eclipse.jdt.internal.core.search.JavaSearchScope;
import com.codenvy.eclipse.jdt.internal.core.util.Util;

public class LocalVariablePattern extends VariablePattern {

LocalVariable localVariable;

public LocalVariablePattern(LocalVariable localVariable, int limitTo, int matchRule) {
	super(LOCAL_VAR_PATTERN, localVariable.getElementName().toCharArray(), limitTo, matchRule);
	this.localVariable = localVariable;
}
public void findIndexMatches(Index index, IndexQueryRequestor requestor, SearchParticipant participant, IJavaSearchScope scope, IProgressMonitor progressMonitor) {
    IPackageFragmentRoot root = (IPackageFragmentRoot)this.localVariable.getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
	String documentPath;
	String relativePath;
    if (root.isArchive()) {
        IType type = (IType)this.localVariable.getAncestor(IJavaElement.TYPE);
        relativePath = (type.getFullyQualifiedName('$')).replace('.', '/') + SuffixConstants.SUFFIX_STRING_class;
        documentPath = root.getPath() + IJavaSearchScope.JAR_FILE_ENTRY_SEPARATOR + relativePath;
    } else {
		IPath path = this.localVariable.getPath();
        documentPath = path.toString();
		relativePath = Util.relativePath(path, 1/*remove project segment*/);
    }

	if (scope instanceof JavaSearchScope) {
		JavaSearchScope javaSearchScope = (JavaSearchScope) scope;
		// Get document path access restriction from java search scope
		// Note that requestor has to verify if needed whether the document violates the access restriction or not
		AccessRuleSet access = javaSearchScope.getAccessRuleSet(relativePath, index.containerPath);
		if (access != JavaSearchScope.NOT_ENCLOSED) { // scope encloses the path
			if (!requestor.acceptIndexMatch(documentPath, this, participant, access))
				throw new OperationCanceledException();
		}
	} else if (scope.encloses(documentPath)) {
		if (!requestor.acceptIndexMatch(documentPath, this, participant, null))
			throw new OperationCanceledException();
	}
}
protected StringBuffer print(StringBuffer output) {
	if (this.findDeclarations) {
		output.append(this.findReferences
			? "LocalVarCombinedPattern: " //$NON-NLS-1$
			: "LocalVarDeclarationPattern: "); //$NON-NLS-1$
	} else {
		output.append("LocalVarReferencePattern: "); //$NON-NLS-1$
	}
	output.append(this.localVariable.toStringWithAncestors());
	return super.print(output);
}
}
