/*******************************************************************************
 * Copyright (c) 2007, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.eclipse.ltk.core.refactoring.resource;

import com.codenvy.eclipse.core.filebuffers.FileBuffers;
import com.codenvy.eclipse.core.filebuffers.ITextFileBuffer;
import com.codenvy.eclipse.core.filebuffers.LocationKind;
import com.codenvy.eclipse.core.resources.IFile;
import com.codenvy.eclipse.core.resources.IProject;
import com.codenvy.eclipse.core.resources.IResource;
import com.codenvy.eclipse.core.resources.IResourceVisitor;
import com.codenvy.eclipse.core.resources.ResourcesPlugin;
import com.codenvy.eclipse.core.runtime.Assert;
import com.codenvy.eclipse.core.runtime.CoreException;
import com.codenvy.eclipse.core.runtime.IPath;
import com.codenvy.eclipse.core.runtime.IProgressMonitor;
import com.codenvy.eclipse.core.runtime.IStatus;
import com.codenvy.eclipse.core.runtime.NullProgressMonitor;
import com.codenvy.eclipse.core.runtime.Status;
import com.codenvy.eclipse.core.runtime.SubProgressMonitor;
import com.codenvy.eclipse.ltk.core.refactoring.Change;
import com.codenvy.eclipse.ltk.core.refactoring.ChangeDescriptor;
import com.codenvy.eclipse.ltk.internal.core.refactoring.BasicElementLabels;
import com.codenvy.eclipse.ltk.internal.core.refactoring.Messages;
import com.codenvy.eclipse.ltk.internal.core.refactoring.RefactoringCoreMessages;
import com.codenvy.eclipse.ltk.internal.core.refactoring.RefactoringCorePlugin;
import com.codenvy.eclipse.ltk.internal.core.refactoring.resource.UndoDeleteResourceChange;
import com.codenvy.eclipse.ltk.internal.core.refactoring.resource.undostates.ResourceUndoState;

import java.net.URI;

/**
 * {@link com.codenvy.eclipse.ltk.core.refactoring.Change} that deletes a resource.
 *
 * @since 3.4
 */
public class DeleteResourceChange extends ResourceChange {

    private final IPath fResourcePath;

    private final boolean fForceOutOfSync;

    private final boolean fDeleteContent;

    private ChangeDescriptor fDescriptor;

    /**
     * Delete a resource.
     *
     * @param resourcePath
     *         the resource path
     * @param forceOutOfSync
     *         if <code>true</code>, deletes the resource with {@link IResource#FORCE}
     */
    public DeleteResourceChange(IPath resourcePath, boolean forceOutOfSync) {
        this(resourcePath, forceOutOfSync, false);
    }

    /**
     * Delete a resource.
     *
     * @param resourcePath
     *         the project path
     * @param forceOutOfSync
     *         if <code>true</code>, deletes the resource with {@link IResource#FORCE}
     * @param deleteContent
     *         if <code>true</code> delete the project contents.
     *         The content delete is not undoable. This setting only applies to projects and is not used when deleting files or folders.
     */
    public DeleteResourceChange(IPath resourcePath, boolean forceOutOfSync, boolean deleteContent) {
        Assert.isNotNull(resourcePath);
        fResourcePath = resourcePath;
        fForceOutOfSync = forceOutOfSync;
        fDeleteContent = deleteContent;
        setValidationMethod(SAVE_IF_DIRTY);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ltk.core.refactoring.resource.ResourceChange#getModifiedResource()
     */
    protected IResource getModifiedResource() {
        return getResource();
    }

    private IResource getResource() {
        IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(fResourcePath);
        if (resource == null && fResourcePath.segmentCount() == 1) {
            resource = ResourcesPlugin.getWorkspace().getRoot().getProject(fResourcePath.segment(0));
        }
        return resource;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ltk.core.refactoring.Change#getName()
     */
    public String getName() {
        IPath path = fResourcePath.makeRelative();
        String label = Messages.format(RefactoringCoreMessages.DeleteResourceChange_name,
                                       BasicElementLabels.getPathLabel(path, false));

        if (path.segmentCount() == 1) {
            IResource resource = getResource();
            if (resource != null) {
                IPath location = resource.getLocation();
                if (location != null) {
                    label = label + BasicElementLabels.CONCAT_STRING + BasicElementLabels.getPathLabel(location, true);
                } else {
                    URI uri = resource.getLocationURI();
                    if (uri != null) {
                        label = label + BasicElementLabels.CONCAT_STRING + BasicElementLabels.getURLPart(uri.toString());
                    }
                }
            }
        }
        return label;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ltk.core.refactoring.Change#perform(org.eclipse.core.runtime.IProgressMonitor)
     */
    public Change perform(IProgressMonitor pm) throws CoreException {
        if (pm == null) {
            pm = new NullProgressMonitor();
        }

        pm.beginTask("", 10); //$NON-NLS-1$
        pm.setTaskName(RefactoringCoreMessages.DeleteResourceChange_deleting);

        try {
            IResource resource = getResource();
            if (resource == null || !resource.exists()) {
                if (fDeleteContent) {
                    return null; // see https://bugs.eclipse.org/343584
                }
                String message = Messages.format(RefactoringCoreMessages.DeleteResourceChange_error_resource_not_exists,
                                                 BasicElementLabels.getPathLabel(fResourcePath.makeRelative(), false));
                throw new CoreException(new Status(IStatus.ERROR, RefactoringCorePlugin.getPluginId(), message));
            }

            // make sure all files inside the resource are saved so restoring works
            if (resource.isAccessible()) {
                resource.accept(new IResourceVisitor() {
                    public boolean visit(IResource curr) throws CoreException {
                        try {
                            if (curr instanceof IFile) {
                                // progress is covered outside.
                                saveFileIfNeeded((IFile)curr, new NullProgressMonitor());
                            }
                        } catch (CoreException e) {
                            // ignore
                        }
                        return true;
                    }
                }, IResource.DEPTH_INFINITE, false);
            }

            ResourceUndoState desc = ResourceUndoState.fromResource(resource);
            if (resource instanceof IProject) {
                ((IProject)resource).delete(fDeleteContent, fForceOutOfSync, new SubProgressMonitor(pm, 10));
            } else {
                int updateFlags;
                if (fForceOutOfSync) {
                    updateFlags = IResource.KEEP_HISTORY | IResource.FORCE;
                } else {
                    updateFlags = IResource.KEEP_HISTORY;
                }
                resource.delete(updateFlags, new SubProgressMonitor(pm, 5));
                desc.recordStateFromHistory(resource, new SubProgressMonitor(pm, 5));
            }
            return new UndoDeleteResourceChange(desc);
        } finally {
            pm.done();
        }
    }

    private static void saveFileIfNeeded(IFile file, IProgressMonitor pm) throws CoreException {
        ITextFileBuffer buffer = FileBuffers.getTextFileBufferManager().getTextFileBuffer(file.getFullPath(),
                                                                                          LocationKind.IFILE);
        if (buffer != null && buffer.isDirty() && buffer.isStateValidated() && buffer.isSynchronized()) {
            pm.beginTask("", 2); //$NON-NLS-1$
            buffer.commit(new SubProgressMonitor(pm, 1), false);
            file.refreshLocal(IResource.DEPTH_ONE, new SubProgressMonitor(pm, 1));
            pm.done();
        } else {
            pm.beginTask("", 1); //$NON-NLS-1$
            pm.worked(1);
            pm.done();
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ltk.core.refactoring.Change#getDescriptor()
     */
    public ChangeDescriptor getDescriptor() {
        return fDescriptor;
    }

    /**
     * Sets the change descriptor to be returned by {@link com.codenvy.eclipse.ltk.core.refactoring.Change#getDescriptor()}.
     *
     * @param descriptor
     *         the change descriptor
     */
    public void setDescriptor(ChangeDescriptor descriptor) {
        fDescriptor = descriptor;
    }

}
