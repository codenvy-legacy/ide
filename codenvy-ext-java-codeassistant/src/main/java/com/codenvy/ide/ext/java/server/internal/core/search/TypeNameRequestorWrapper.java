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
package com.codenvy.ide.ext.java.server.internal.core.search;

import com.codenvy.ide.ext.java.server.core.search.TypeNameRequestor;

import org.eclipse.jdt.internal.compiler.env.AccessRestriction;

/**
 * Wrapper used to link {@link org.eclipse.jdt.internal.core.search.IRestrictedAccessTypeRequestor} with {@link org.eclipse.jdt.core.search.TypeNameRequestor}.
 * This wrapper specifically allows usage of internal method {@link org.eclipse.jdt.internal.core.search.BasicSearchEngine#searchAllTypeNames(
 * 	char[] packageName,
 * 	int packageMatchRule,
 * 	char[] typeName,
 * 	int typeMatchRule,
 * 	int searchFor,
 * 	org.eclipse.jdt.core.search.IJavaSearchScope scope,
 *    org.eclipse.jdt.internal.core.search.IRestrictedAccessTypeRequestor nameRequestor,
 * 	int waitingPolicy,
 * 	org.eclipse.core.runtime.IProgressMonitor monitor) }.
 * from  API method {@link org.eclipse.jdt.core.search.SearchEngine#searchAllTypeNames(
 * 	char[] packageName,
 * 	int packageMatchRule,
 * 	char[] typeName,
 * 	int matchRule,
 * 	int searchFor,
 * 	org.eclipse.jdt.core.search.IJavaSearchScope scope,
 *    org.eclipse.jdt.core.search.TypeNameRequestor nameRequestor,
 * 	int waitingPolicy,
 * 	org.eclipse.core.runtime.IProgressMonitor monitor) }.
 */
public class TypeNameRequestorWrapper implements IRestrictedAccessTypeRequestor {
    TypeNameRequestor requestor;

    public TypeNameRequestorWrapper(TypeNameRequestor requestor) {
        this.requestor = requestor;
    }

    public void acceptType(int modifiers, char[] packageName, char[] simpleTypeName, char[][] enclosingTypeNames, String path,
                           AccessRestriction access) {
        this.requestor.acceptType(modifiers, packageName, simpleTypeName, enclosingTypeNames, path);
    }
}
