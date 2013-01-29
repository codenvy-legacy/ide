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
package com.codenvy.eclipse.jdt.internal.corext.refactoring;

import com.codenvy.eclipse.jdt.core.search.SearchMatch;

/**
 * Interface for search requestors used in conjunction with {@link com.codenvy.eclipse.jdt.internal.corext.refactoring.RefactoringSearchEngine2}.
 *
 * @since 3.1
 */
public interface IRefactoringSearchRequestor
{

   /**
    * Can the search match be accepted?
    *
    * @param match the search match to test
    * @return The accepted match, or <code>null</code> if not accepted
    */
   public SearchMatch acceptSearchMatch(SearchMatch match);
}
