/*******************************************************************************
 * Copyright (c) 2006, 2011 IBM Corporation and others.
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
import com.codenvy.eclipse.core.runtime.OperationCanceledException;
import com.codenvy.eclipse.core.runtime.SubProgressMonitor;
import com.codenvy.eclipse.jdt.internal.corext.refactoring.RefactoringCoreMessages;
import com.codenvy.eclipse.jdt.internal.corext.util.Messages;
import com.codenvy.eclipse.jdt.internal.ui.viewsupport.BasicElementLabels;
import com.codenvy.eclipse.ltk.core.refactoring.Change;
import com.codenvy.eclipse.ltk.core.refactoring.RefactoringStatus;
import com.codenvy.eclipse.ltk.core.refactoring.resource.DeleteResourceChange;
import com.codenvy.eclipse.ui.ide.undo.ResourceDescription;


public class UndoDeleteResourceChange extends Change {

    private final ResourceDescription fResourceDescription;

    public UndoDeleteResourceChange(ResourceDescription resourceDescription) {
        fResourceDescription = resourceDescription;
    }

    @Override
    public void initializeValidationData(IProgressMonitor pm) {

    }

    @Override
    public Object getModifiedElement() {
        return null;
    }

    @Override
    public String getName() {
        return Messages.format(RefactoringCoreMessages.UndoDeleteResourceChange_change_name,
                               BasicElementLabels.getResourceName(fResourceDescription.getName()));
    }

    @Override
    public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        if (!fResourceDescription.isValid()) {
            return RefactoringStatus.createFatalErrorStatus(
                    Messages.format(RefactoringCoreMessages.UndoDeleteResourceChange_cannot_restore,
                                    BasicElementLabels.getResourceName(fResourceDescription.getName())));
        }

        if (fResourceDescription.verifyExistence(true)) {
            return RefactoringStatus.createFatalErrorStatus(
                    Messages.format(RefactoringCoreMessages.UndoDeleteResourceChange_already_exists,
                                    BasicElementLabels.getResourceName(fResourceDescription.getName())));
        }

        return new RefactoringStatus();
    }

    @Override
    public Change perform(IProgressMonitor pm) throws CoreException {
        IResource created = fResourceDescription.createResource(pm);
        created.refreshLocal(IResource.DEPTH_INFINITE, new SubProgressMonitor(pm, 1));
        return new DeleteResourceChange(created.getFullPath(), true);
    }

    @Override
    public String toString() {
        return "Remove " + fResourceDescription.getName(); //$NON-NLS-1$
    }
}
