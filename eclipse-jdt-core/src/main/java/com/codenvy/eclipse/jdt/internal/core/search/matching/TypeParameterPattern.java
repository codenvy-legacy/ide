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
import com.codenvy.eclipse.jdt.core.IMember;
import com.codenvy.eclipse.jdt.core.IMethod;
import com.codenvy.eclipse.jdt.core.IPackageFragmentRoot;
import com.codenvy.eclipse.jdt.core.IType;
import com.codenvy.eclipse.jdt.core.ITypeParameter;
import com.codenvy.eclipse.jdt.core.Signature;
import com.codenvy.eclipse.jdt.core.search.IJavaSearchScope;
import com.codenvy.eclipse.jdt.core.search.SearchParticipant;
import com.codenvy.eclipse.jdt.internal.compiler.env.AccessRuleSet;
import com.codenvy.eclipse.jdt.internal.compiler.util.SuffixConstants;
import com.codenvy.eclipse.jdt.internal.core.index.Index;
import com.codenvy.eclipse.jdt.internal.core.search.IndexQueryRequestor;
import com.codenvy.eclipse.jdt.internal.core.search.JavaSearchScope;
import com.codenvy.eclipse.jdt.internal.core.util.Util;

/**
 * Pattern to search type parameters.
 *
 * @since 3.1
 */
public class TypeParameterPattern extends JavaSearchPattern {

	protected boolean findDeclarations;
	protected boolean findReferences;
	protected char[] name;
	protected ITypeParameter typeParameter;
	protected char[] declaringMemberName;
	protected char[] methodDeclaringClassName;
	protected char[][] methodArgumentTypes;

	/**
	 * @param findDeclarations
	 * @param findReferences
	 * @param typeParameter
	 * @param matchRule
	 */
	public TypeParameterPattern(boolean findDeclarations, boolean findReferences, ITypeParameter typeParameter, int matchRule) {
		super(TYPE_PARAM_PATTERN, matchRule);

		this.findDeclarations = findDeclarations; // set to find declarations & all occurences
		this.findReferences = findReferences; // set to find references & all occurences
		this.typeParameter = typeParameter;
		this.name = typeParameter.getElementName().toCharArray(); // store type parameter name
		IMember member = typeParameter.getDeclaringMember();
		this.declaringMemberName = member.getElementName().toCharArray(); // store type parameter declaring member name

		// For method type parameter, store also declaring class name and parameters type names
		if (member instanceof IMethod) {
			IMethod method = (IMethod) member;
			this.methodDeclaringClassName = method.getParent().getElementName().toCharArray();
			String[] parameters = method.getParameterTypes();
			int length = parameters.length;
			this.methodArgumentTypes = new char[length][];
			for (int i=0; i<length; i++) {
				this.methodArgumentTypes[i] = Signature.toCharArray(parameters[i].toCharArray());
			}
		}
	}

	/*
	 * Same than LocalVariablePattern.
	 */
	public void findIndexMatches(Index index, IndexQueryRequestor requestor, SearchParticipant participant, IJavaSearchScope scope, IProgressMonitor progressMonitor) {
	    IPackageFragmentRoot root = (IPackageFragmentRoot) this.typeParameter.getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
		String documentPath;
		String relativePath;
	    if (root.isArchive()) {
 	    	IType type = (IType) this.typeParameter.getAncestor(IJavaElement.TYPE);
    	    relativePath = (type.getFullyQualifiedName('$')).replace('.', '/') + SuffixConstants.SUFFIX_STRING_class;
	        documentPath = root.getPath() + IJavaSearchScope.JAR_FILE_ENTRY_SEPARATOR + relativePath;
	    } else {
			IPath path = this.typeParameter.getPath();
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
				? "TypeParamCombinedPattern: " //$NON-NLS-1$
				: "TypeParamDeclarationPattern: "); //$NON-NLS-1$
		} else {
			output.append("TypeParamReferencePattern: "); //$NON-NLS-1$
		}
		output.append(this.typeParameter.toString());
		return super.print(output);
	}

}
