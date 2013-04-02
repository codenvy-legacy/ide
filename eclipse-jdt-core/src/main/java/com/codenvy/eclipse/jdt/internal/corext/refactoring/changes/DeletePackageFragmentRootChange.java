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

import com.codenvy.eclipse.core.filebuffers.FileBuffers;
import com.codenvy.eclipse.core.filebuffers.ITextFileBufferManager;
import com.codenvy.eclipse.core.filebuffers.LocationKind;
import com.codenvy.eclipse.core.resources.IFile;
import com.codenvy.eclipse.core.resources.IResource;
import com.codenvy.eclipse.core.runtime.Assert;
import com.codenvy.eclipse.core.runtime.CoreException;
import com.codenvy.eclipse.core.runtime.IPath;
import com.codenvy.eclipse.core.runtime.IProgressMonitor;
import com.codenvy.eclipse.core.runtime.IStatus;
import com.codenvy.eclipse.core.runtime.NullProgressMonitor;
import com.codenvy.eclipse.core.runtime.Status;
import com.codenvy.eclipse.core.runtime.SubProgressMonitor;
import com.codenvy.eclipse.jdt.core.IJavaProject;
import com.codenvy.eclipse.jdt.core.IPackageFragmentRoot;
import com.codenvy.eclipse.jdt.core.JavaCore;
import com.codenvy.eclipse.jdt.core.JavaModelException;
import com.codenvy.eclipse.jdt.internal.core.util.Util;
import com.codenvy.eclipse.jdt.internal.corext.refactoring.RefactoringCoreMessages;
import com.codenvy.eclipse.jdt.internal.corext.refactoring.reorg.IPackageFragmentRootManipulationQuery;
import com.codenvy.eclipse.jdt.internal.corext.refactoring.util.JavaElementUtil;
import com.codenvy.eclipse.jdt.internal.corext.util.Messages;
import com.codenvy.eclipse.jdt.internal.ui.viewsupport.BasicElementLabels;
import com.codenvy.eclipse.jdt.ui.JavaElementLabels;
import com.codenvy.eclipse.ltk.core.refactoring.Change;
import com.codenvy.eclipse.ltk.core.refactoring.CompositeChange;
import com.codenvy.eclipse.ltk.core.refactoring.NullChange;
import com.codenvy.eclipse.ltk.core.refactoring.TextFileChange;
import com.codenvy.eclipse.ui.ide.undo.ResourceDescription;

import org.exoplatform.ide.editor.shared.text.edits.ReplaceEdit;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class DeletePackageFragmentRootChange extends AbstractDeleteChange {

    private final String fHandle;

    private final IPackageFragmentRootManipulationQuery fUpdateClasspathQuery;

    public DeletePackageFragmentRootChange(IPackageFragmentRoot root, boolean isExecuteChange,
                                           IPackageFragmentRootManipulationQuery updateClasspathQuery) {
        Assert.isNotNull(root);
        Assert.isTrue(!root.isExternal());
        fHandle = root.getHandleIdentifier();
        fUpdateClasspathQuery = updateClasspathQuery;

        if (isExecuteChange) {
            // don't check for read-only resources since we already
            // prompt the user via a dialog to confirm deletion of
            // read only resource. The change is currently not used
            // as
            setValidationMethod(VALIDATE_NOT_DIRTY);
        } else {
            setValidationMethod(VALIDATE_NOT_DIRTY | VALIDATE_NOT_READ_ONLY);
        }
    }

    @Override
    public String getName() {
        String rootName = JavaElementLabels.getElementLabel(getRoot(), JavaElementLabels.ALL_DEFAULT);
        return Messages.format(RefactoringCoreMessages.DeletePackageFragmentRootChange_delete, rootName);
    }

    @Override
    public Object getModifiedElement() {
        return getRoot();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.corext.refactoring.base.JDTChange#getModifiedResource()
     */
    @Override
    protected IResource getModifiedResource() {
        return getRoot().getResource();
    }

    private IPackageFragmentRoot getRoot() {
        return (IPackageFragmentRoot)JavaCore.create(fHandle);
    }

    @Override
    protected Change doDelete(IProgressMonitor pm) throws CoreException {
        if (!confirmDeleteIfReferenced()) {
            return new NullChange();
        }
        int resourceUpdateFlags = IResource.KEEP_HISTORY;
        int jCoreUpdateFlags = IPackageFragmentRoot.ORIGINATING_PROJECT_CLASSPATH | IPackageFragmentRoot.OTHER_REFERRING_PROJECTS_CLASSPATH;

        pm.beginTask("", 2); //$NON-NLS-1$
        IPackageFragmentRoot root = getRoot();
        IResource rootResource = root.getResource();
        CompositeChange result = new CompositeChange(getName());

        ResourceDescription rootDescription = ResourceDescription.fromResource(rootResource);
        IJavaProject[] referencingProjects = JavaElementUtil.getReferencingProjects(root);
        HashMap<IFile, String> classpathFilesContents = new HashMap<IFile, String>();
        for (int i = 0; i < referencingProjects.length; i++) {
            IJavaProject javaProject = referencingProjects[i];
            IFile classpathFile = javaProject.getProject().getFile(".classpath"); //$NON-NLS-1$
            if (classpathFile.exists()) {
                classpathFilesContents.put(classpathFile, getFileContents(classpathFile));
            }
        }

        root.delete(resourceUpdateFlags, jCoreUpdateFlags, new SubProgressMonitor(pm, 1));

        rootDescription.recordStateFromHistory(rootResource, new SubProgressMonitor(pm, 1));
        for (Iterator<Entry<IFile, String>> iterator = classpathFilesContents.entrySet().iterator(); iterator.hasNext(); ) {
            Entry<IFile, String> entry = iterator.next();
            IFile file = entry.getKey();
            String contents = entry.getValue();
            //Restore time stamps? This should probably be some sort of UndoTextFileChange.
            TextFileChange classpathUndo = new TextFileChange(
                    Messages.format(RefactoringCoreMessages.DeletePackageFragmentRootChange_restore_file,
                                    BasicElementLabels.getPathLabel(file.getFullPath(), true)), file);
            classpathUndo.setEdit(new ReplaceEdit(0, getFileLength(file), contents));
            result.add(classpathUndo);
        }
        result.add(new UndoDeleteResourceChange(rootDescription));

        pm.done();
        return result;
    }

    private static String getFileContents(IFile file) throws CoreException {
        ITextFileBufferManager manager = FileBuffers.getTextFileBufferManager();
        IPath path = file.getFullPath();
        manager.connect(path, LocationKind.IFILE, new NullProgressMonitor());
        try {
            return manager.getTextFileBuffer(path, LocationKind.IFILE).getDocument().get();
        } finally {
            manager.disconnect(path, LocationKind.IFILE, new NullProgressMonitor());
        }
    }

    private static int getFileLength(IFile file) throws CoreException {
        // Cannot use file buffers here, since they are not yet in sync at this point.
        InputStream contents = file.getContents();
        InputStreamReader reader;
        try {
            reader = new InputStreamReader(contents, file.getCharset());
        } catch (UnsupportedEncodingException e) {
            Util.log(e);
            reader = new InputStreamReader(contents);
        }
        try {
            return (int)reader.skip(Integer.MAX_VALUE);
        } catch (IOException e) {
            throw new CoreException(new Status(IStatus.ERROR, "IDE", e.getMessage(), e));
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
            }
        }
    }

    private boolean confirmDeleteIfReferenced() throws JavaModelException {
        IPackageFragmentRoot root = getRoot();
        if (!root.isArchive() && !root.isExternal()) //for source folders, you don't ask, just do it
        {
            return true;
        }
        if (fUpdateClasspathQuery == null) {
            return true;
        }
        IJavaProject[] referencingProjects = JavaElementUtil.getReferencingProjects(getRoot());
        if (referencingProjects.length <= 1) {
            return true;
        }
        return fUpdateClasspathQuery.confirmManipulation(getRoot(), referencingProjects);
    }
}
