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
package com.codenvy.ide.ext.java.server.internal.core.search;


import com.codenvy.ide.ext.java.server.core.search.IJavaSearchScope;

import org.eclipse.jdt.core.IJavaElementDelta;

public abstract class AbstractSearchScope implements IJavaSearchScope {

/**
 * @see org.eclipse.jdt.core.search.IJavaSearchScope#includesBinaries()
 * @deprecated
 */
public boolean includesBinaries() {
	return true;
}

/**
 * @see org.eclipse.jdt.core.search.IJavaSearchScope#includesClasspaths()
 * @deprecated
 */
public boolean includesClasspaths() {
	return true;
}

/* (non-Javadoc)
 * Process the given delta and refresh its internal state if needed.
 * Returns whether the internal state was refreshed.
 */
public abstract void processDelta(IJavaElementDelta delta, int eventType);

/**
 * @see org.eclipse.jdt.core.search.IJavaSearchScope#setIncludesBinaries(boolean)
 * @deprecated
 */
public void setIncludesBinaries(boolean includesBinaries) {
	// implements interface method
}

/**
 * @see org.eclipse.jdt.core.search.IJavaSearchScope#setIncludesClasspaths(boolean)
 * @deprecated
 */
public void setIncludesClasspaths(boolean includesClasspaths) {
	// implements interface method
}

}
