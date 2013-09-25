/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.ext.java.client.env;

import com.codenvy.ide.ext.java.client.core.IJavaElement;
import com.codenvy.ide.ext.java.client.core.IPackageFragment;
import com.codenvy.ide.ext.java.client.core.Signature;
import com.codenvy.ide.ext.java.client.core.dom.Name;

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

    /** @see com.codenvy.ide.ext.java.client.core.IJavaElement#getElementName() */
    @Override
    public String getElementName() {
        return packageFragment;
    }

    /** @see com.codenvy.ide.ext.java.client.core.IJavaElement#getElementType() */
    @Override
    public int getElementType() {
        return IJavaElement.PACKAGE_FRAGMENT;
    }

}
