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
package com.codenvy.eclipse.jdt.internal.corext.refactoring.reorg;

import com.codenvy.eclipse.core.resources.IContainer;
import com.codenvy.eclipse.core.resources.IFile;
import com.codenvy.eclipse.core.resources.IProject;
import com.codenvy.eclipse.core.resources.IResource;
import com.codenvy.eclipse.core.resources.mapping.IResourceChangeDescriptionFactory;
import com.codenvy.eclipse.core.runtime.CoreException;
import com.codenvy.eclipse.jdt.core.IJavaElement;
import com.codenvy.eclipse.jdt.core.IPackageFragment;
import com.codenvy.eclipse.jdt.core.IPackageFragmentRoot;
import com.codenvy.eclipse.jdt.internal.corext.refactoring.participants.ResourceModifications;
import com.codenvy.eclipse.ltk.core.refactoring.RefactoringStatus;
import com.codenvy.eclipse.ltk.core.refactoring.participants.RefactoringParticipant;
import com.codenvy.eclipse.ltk.core.refactoring.participants.RefactoringProcessor;
import com.codenvy.eclipse.ltk.core.refactoring.participants.SharableParticipants;
import com.codenvy.eclipse.ltk.core.refactoring.participants.ValidateEditChecker;

import java.util.ArrayList;

public abstract class RefactoringModifications {

    private ResourceModifications fResourceModifications;

    public RefactoringModifications() {
        fResourceModifications = new ResourceModifications();
    }

    public ResourceModifications getResourceModifications() {
        return fResourceModifications;
    }

    public abstract RefactoringParticipant[] loadParticipants(RefactoringStatus status, RefactoringProcessor owner,
                                                              String[] natures, SharableParticipants shared);

    public abstract void buildDelta(IResourceChangeDescriptionFactory builder);

    /**
     * Implementors add all resources that need a validate edit
     *
     * @param checker
     *         the validate edit checker
     */
    public void buildValidateEdits(ValidateEditChecker checker) {
        // Default implementation does nothing.
    }

    protected void createIncludingParents(IContainer container) {
        while (container != null && !(container.exists() || getResourceModifications().willExist(container))) {
            getResourceModifications().addCreate(container);
            container = container.getParent();
        }
    }

    protected IResource[] collectResourcesOfInterest(IPackageFragment source) throws CoreException {
        IJavaElement[] children = source.getChildren();
        int childOfInterest = IJavaElement.COMPILATION_UNIT;
        if (source.getKind() == IPackageFragmentRoot.K_BINARY) {
            childOfInterest = IJavaElement.CLASS_FILE;
        }
        ArrayList<IResource> result = new ArrayList<IResource>(children.length);
        for (int i = 0; i < children.length; i++) {
            IJavaElement child = children[i];
            if (child.getElementType() == childOfInterest && child.getResource() != null) {
                result.add(child.getResource());
            }
        }
        // Gather non-java resources
        Object[] nonJavaResources = source.getNonJavaResources();
        for (int i = 0; i < nonJavaResources.length; i++) {
            Object element = nonJavaResources[i];
            if (element instanceof IResource) {
                result.add((IResource)element);
            }
        }
        return result.toArray(new IResource[result.size()]);
    }

    protected IFile getClasspathFile(IResource resource) {
        IProject project = resource.getProject();
        if (project == null) {
            return null;
        }
        IResource result = project.findMember(".classpath"); //$NON-NLS-1$
        if (result instanceof IFile) {
            return (IFile)result;
        }
        return null;
    }
}
