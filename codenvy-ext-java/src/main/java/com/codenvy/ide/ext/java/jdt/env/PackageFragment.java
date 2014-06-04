/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.env;

import com.codenvy.ide.ext.java.jdt.core.IJavaElement;
import com.codenvy.ide.ext.java.jdt.core.IPackageFragment;
import com.codenvy.ide.ext.java.jdt.core.Signature;
import com.codenvy.ide.ext.java.jdt.core.dom.Name;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class PackageFragment implements IPackageFragment {

    private final String packageFragment;

    /** @param fullyQualifiedName */
    public PackageFragment(String fullyQualifiedName) {
        this.packageFragment = Signature.getQualifier(fullyQualifiedName);
    }

    /**
     *
     */
    public PackageFragment(Name name) {
        packageFragment = name.getFullyQualifiedName();
    }

    /** @see com.codenvy.ide.ext.java.jdt.core.IJavaElement#getElementName() */
    @Override
    public String getElementName() {
        return packageFragment;
    }

    /** @see com.codenvy.ide.ext.java.jdt.core.IJavaElement#getElementType() */
    @Override
    public int getElementType() {
        return IJavaElement.PACKAGE_FRAGMENT;
    }

}
