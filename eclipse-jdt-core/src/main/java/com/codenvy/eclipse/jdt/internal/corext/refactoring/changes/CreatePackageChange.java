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

package com.codenvy.eclipse.jdt.internal.corext.refactoring.changes;

import com.codenvy.eclipse.core.resources.IResource;
import com.codenvy.eclipse.core.runtime.CoreException;
import com.codenvy.eclipse.core.runtime.IProgressMonitor;
import com.codenvy.eclipse.jdt.core.IPackageFragment;
import com.codenvy.eclipse.jdt.core.IPackageFragmentRoot;
import com.codenvy.eclipse.jdt.internal.corext.refactoring.RefactoringCoreMessages;
import com.codenvy.eclipse.ltk.core.refactoring.Change;
import com.codenvy.eclipse.ltk.core.refactoring.NullChange;
import com.codenvy.eclipse.ltk.core.refactoring.RefactoringStatus;
import com.codenvy.eclipse.ltk.core.refactoring.resource.DeleteResourceChange;
import com.codenvy.eclipse.ltk.core.refactoring.resource.ResourceChange;

public class CreatePackageChange extends ResourceChange {

    private IPackageFragment fPackageFragment;

    public CreatePackageChange(IPackageFragment pack) {
        fPackageFragment = pack;
    }

    @Override
    public RefactoringStatus isValid(IProgressMonitor pm) {
        // Don't do any checking. Peform handles the case
        // that the package already exists. Furthermore
        // create package change isn't used as a undo
        // redo change right now
        return new RefactoringStatus();
    }

    @Override
    public Change perform(IProgressMonitor pm) throws CoreException {
        try {
            pm.beginTask(RefactoringCoreMessages.CreatePackageChange_Creating_package, 1);

            if (fPackageFragment.exists()) {
                return new NullChange();
            } else {
                IPackageFragmentRoot root = (IPackageFragmentRoot)fPackageFragment.getParent();
                root.createPackageFragment(fPackageFragment.getElementName(), false, pm);

                return new DeleteResourceChange(fPackageFragment.getPath(), true);
            }
        } finally {
            pm.done();
        }
    }

    @Override
    public String getName() {
        return RefactoringCoreMessages.CreatePackageChange_Create_package;
    }

    @Override
    public Object getModifiedElement() {
        return fPackageFragment;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.corext.refactoring.base.JDTChange#getModifiedResource()
     */
    @Override
    protected IResource getModifiedResource() {
        return fPackageFragment.getResource();
    }
}
