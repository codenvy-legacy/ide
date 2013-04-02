/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.eclipse.jdt.internal.corext.util;

import com.codenvy.eclipse.core.runtime.Assert;
import com.codenvy.eclipse.jdt.core.IAccessRule;
import com.codenvy.eclipse.jdt.core.JavaCore;
import com.codenvy.eclipse.jdt.core.search.TypeNameMatch;
import com.codenvy.eclipse.jdt.core.search.TypeNameMatchRequestor;

import java.util.Collection;

public class TypeNameMatchCollector extends TypeNameMatchRequestor {

    private final Collection<TypeNameMatch> fCollection;

    public TypeNameMatchCollector(Collection<TypeNameMatch> collection) {
        Assert.isNotNull(collection);
        fCollection = collection;
    }

    private boolean inScope(TypeNameMatch match) {
        //		if (TypeFilter.isFiltered(match))
        //			return false;

        int accessibility = match.getAccessibility();
        switch (accessibility) {
            case IAccessRule.K_NON_ACCESSIBLE:
                return JavaCore.DISABLED.equals(JavaCore.getOption(JavaCore.CODEASSIST_FORBIDDEN_REFERENCE_CHECK));
            case IAccessRule.K_DISCOURAGED:
                return JavaCore.DISABLED.equals(JavaCore.getOption(JavaCore.CODEASSIST_DISCOURAGED_REFERENCE_CHECK));
            default:
                return true;
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.search.TypeNameMatchRequestor#acceptTypeNameMatch(org.eclipse.jdt.core.search.TypeNameMatch)
     */
    @Override
    public void acceptTypeNameMatch(TypeNameMatch match) {
        if (inScope(match)) {
            fCollection.add(match);
        }
    }

}
