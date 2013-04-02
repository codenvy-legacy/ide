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
package com.codenvy.eclipse.jdt.internal.corext.refactoring.rename;

import com.codenvy.eclipse.core.resources.IContainer;
import com.codenvy.eclipse.core.resources.IFile;
import com.codenvy.eclipse.core.resources.IResource;
import com.codenvy.eclipse.core.resources.ResourcesPlugin;
import com.codenvy.eclipse.core.runtime.Assert;
import com.codenvy.eclipse.core.runtime.CoreException;
import com.codenvy.eclipse.core.runtime.IPath;
import com.codenvy.eclipse.core.runtime.IProgressMonitor;
import com.codenvy.eclipse.core.runtime.Path;
import com.codenvy.eclipse.jdt.core.IJavaProject;
import com.codenvy.eclipse.jdt.core.IPackageFragmentRoot;
import com.codenvy.eclipse.jdt.core.JavaCore;
import com.codenvy.eclipse.jdt.core.refactoring.IJavaRefactorings;
import com.codenvy.eclipse.jdt.core.refactoring.descriptors.RenameJavaElementDescriptor;
import com.codenvy.eclipse.jdt.internal.core.refactoring.descriptors.RefactoringSignatureDescriptorFactory;
import com.codenvy.eclipse.jdt.internal.corext.refactoring.JDTRefactoringDescriptorComment;
import com.codenvy.eclipse.jdt.internal.corext.refactoring.JavaRefactoringArguments;
import com.codenvy.eclipse.jdt.internal.corext.refactoring.JavaRefactoringDescriptorUtil;
import com.codenvy.eclipse.jdt.internal.corext.refactoring.RefactoringAvailabilityTester;
import com.codenvy.eclipse.jdt.internal.corext.refactoring.RefactoringCoreMessages;
import com.codenvy.eclipse.jdt.internal.corext.refactoring.changes.DynamicValidationRefactoringChange;
import com.codenvy.eclipse.jdt.internal.corext.refactoring.changes.RenameSourceFolderChange;
import com.codenvy.eclipse.jdt.internal.corext.refactoring.participants.JavaProcessors;
import com.codenvy.eclipse.jdt.internal.corext.util.Messages;
import com.codenvy.eclipse.jdt.internal.ui.viewsupport.BasicElementLabels;
import com.codenvy.eclipse.jdt.ui.JavaElementLabels;
import com.codenvy.eclipse.jdt.ui.refactoring.IRefactoringProcessorIds;
import com.codenvy.eclipse.ltk.core.refactoring.Change;
import com.codenvy.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import com.codenvy.eclipse.ltk.core.refactoring.RefactoringStatus;
import com.codenvy.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import com.codenvy.eclipse.ltk.core.refactoring.participants.RenameArguments;

public final class RenameSourceFolderProcessor extends JavaRenameProcessor {

    private static final String ATTRIBUTE_PATH = "path"; //$NON-NLS-1$

    private static final String ATTRIBUTE_NAME = "name"; //$NON-NLS-1$

    private IPackageFragmentRoot fSourceFolder;

    /**
     * Creates a new rename source folder processor.
     *
     * @param root
     *         the package fragment root, or <code>null</code> if invoked by scripting
     */
    public RenameSourceFolderProcessor(IPackageFragmentRoot root) {
        fSourceFolder = root;
        if (root != null) {
            setNewElementName(root.getElementName());
        }
    }

    public RenameSourceFolderProcessor(JavaRefactoringArguments arguments, RefactoringStatus status) {
        this(null);
        status.merge(initialize(arguments));
    }


    @Override
    public String getIdentifier() {
        return IRefactoringProcessorIds.RENAME_SOURCE_FOLDER_PROCESSOR;
    }

    @Override
    public boolean isApplicable() throws CoreException {
        return RefactoringAvailabilityTester.isRenameAvailable(fSourceFolder);
    }

    @Override
    public String getProcessorName() {
        return RefactoringCoreMessages.RenameSourceFolderRefactoring_rename;
    }

    @Override
    protected String[] getAffectedProjectNatures() throws CoreException {
        return JavaProcessors.computeAffectedNatures(fSourceFolder);
    }

    @Override
    public Object[] getElements() {
        return new Object[]{fSourceFolder};
    }

    public Object getNewElement() throws CoreException {
        IPackageFragmentRoot[] roots = fSourceFolder.getJavaProject().getPackageFragmentRoots();
        for (int i = 0; i < roots.length; i++) {
            if (roots[i].getElementName().equals(getNewElementName())) {
                return roots[i];
            }
        }
        return null;
    }

    @Override
    public int getSaveMode() {
        return 2; //RefactoringSaveHelper.SAVE_ALL;
    }

    @Override
    protected RenameModifications computeRenameModifications() throws CoreException {
        RenameModifications result = new RenameModifications();
        result.rename(fSourceFolder, new RenameArguments(getNewElementName(), getUpdateReferences()));
        return result;
    }

    @Override
    protected IFile[] getChangedFiles() throws CoreException {
        return new IFile[0];
    }

    //---- IRenameProcessor ----------------------------------------------

    public String getCurrentElementName() {
        return fSourceFolder.getElementName();
    }

    @Override
    public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException {
        return new RefactoringStatus();
    }

    public RefactoringStatus checkNewElementName(String newName) throws CoreException {
        Assert.isNotNull(newName, "new name"); //$NON-NLS-1$
        if (!newName.trim().equals(newName)) {
            return RefactoringStatus.createFatalErrorStatus(RefactoringCoreMessages.RenameSourceFolderRefactoring_blank);
        }

        IContainer c = fSourceFolder.getResource().getParent();
        if (!c.getFullPath().isValidSegment(newName)) {
            return RefactoringStatus.createFatalErrorStatus(
                    RefactoringCoreMessages.RenameSourceFolderRefactoring_invalid_name);
        }

        RefactoringStatus result = RefactoringStatus.create(c.getWorkspace().validateName(newName, IResource.FOLDER));
        if (result.hasFatalError()) {
            return result;
        }

        result.merge(RefactoringStatus.create(c.getWorkspace().validatePath(createNewPath(newName), IResource.FOLDER)));
        if (result.hasFatalError()) {
            return result;
        }

        IJavaProject project = fSourceFolder.getJavaProject();
        IPath p = project.getProject().getFullPath().append(newName);
        if (project.findPackageFragmentRoot(p) != null) {
            return RefactoringStatus.createFatalErrorStatus(
                    RefactoringCoreMessages.RenameSourceFolderRefactoring_already_exists);
        }

        if (project.getProject().findMember(new Path(newName)) != null) {
            return RefactoringStatus.createFatalErrorStatus(
                    RefactoringCoreMessages.RenameSourceFolderRefactoring_alread_exists);
        }
        return result;
    }

    private String createNewPath(String newName) {
        return fSourceFolder.getPath().removeLastSegments(1).append(newName).toString();
    }

    @Override
    protected RefactoringStatus doCheckFinalConditions(IProgressMonitor pm,
                                                       CheckConditionsContext context) throws CoreException {
        pm.beginTask("", 1); //$NON-NLS-1$
        try {
            return new RefactoringStatus();
        } finally {
            pm.done();
        }
    }

    public boolean getUpdateReferences() {
        return true;
    }

    @Override
    public Change createChange(IProgressMonitor monitor) throws CoreException {
        monitor.beginTask(RefactoringCoreMessages.RenameTypeRefactoring_creating_change, 1);
        try {
            final IResource resource = fSourceFolder.getResource();
            final String project = resource.getProject().getName();
            final String newName = getNewElementName();
            final String description = Messages.format(
                    RefactoringCoreMessages.RenameSourceFolderChange_descriptor_description_short,
                    JavaElementLabels.getElementLabel(fSourceFolder, JavaElementLabels.ALL_DEFAULT));
            final String header = Messages.format(RefactoringCoreMessages.RenameSourceFolderChange_descriptor_description,
                                                  new String[]{BasicElementLabels.getPathLabel(resource.getFullPath(),
                                                                                               false),
                                                               BasicElementLabels.getJavaElementName(newName)});
            final String comment = new JDTRefactoringDescriptorComment(project, this, header).asString();
            final RenameJavaElementDescriptor descriptor = RefactoringSignatureDescriptorFactory.createRenameJavaElementDescriptor(
                    IJavaRefactorings.RENAME_SOURCE_FOLDER);
            descriptor.setProject(project);
            descriptor.setDescription(description);
            descriptor.setComment(comment);
            descriptor.setFlags(RefactoringDescriptor.NONE);
            descriptor.setJavaElement(fSourceFolder);
            descriptor.setNewName(newName);
            return new DynamicValidationRefactoringChange(descriptor,
                                                          RefactoringCoreMessages.RenameSourceFolderRefactoring_rename,
                                                          new Change[]{new RenameSourceFolderChange(fSourceFolder, newName)});
        } finally {
            monitor.done();
        }
    }

    private RefactoringStatus initialize(JavaRefactoringArguments generic) {
        final String path = generic.getAttribute(ATTRIBUTE_PATH);
        if (path != null) {
            final IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(path));
            if (resource == null || !resource.exists()) {
                return JavaRefactoringDescriptorUtil.createInputFatalStatus(resource, getProcessorName(),
                                                                            IJavaRefactorings.RENAME_SOURCE_FOLDER);
            } else {
                fSourceFolder = (IPackageFragmentRoot)JavaCore.create(resource);
            }
        } else {
            return RefactoringStatus.createFatalErrorStatus(
                    Messages.format(RefactoringCoreMessages.InitializableRefactoring_argument_not_exist, ATTRIBUTE_PATH));
        }
        final String name = generic.getAttribute(ATTRIBUTE_NAME);
        if (name != null && !"".equals(name)) //$NON-NLS-1$
        {
            setNewElementName(name);
        } else {
            return RefactoringStatus.createFatalErrorStatus(
                    Messages.format(RefactoringCoreMessages.InitializableRefactoring_argument_not_exist, ATTRIBUTE_NAME));
        }
        return new RefactoringStatus();
    }
}
