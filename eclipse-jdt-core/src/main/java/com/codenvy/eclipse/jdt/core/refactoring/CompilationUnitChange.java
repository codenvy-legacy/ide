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
package com.codenvy.eclipse.jdt.core.refactoring;

import com.codenvy.eclipse.core.resources.IFile;
import com.codenvy.eclipse.core.runtime.Assert;
import com.codenvy.eclipse.core.runtime.CoreException;
import com.codenvy.eclipse.core.runtime.IProgressMonitor;
import com.codenvy.eclipse.core.runtime.SubProgressMonitor;
import com.codenvy.eclipse.jdt.core.ICompilationUnit;
import com.codenvy.eclipse.jdt.internal.core.util.Util;
import com.codenvy.eclipse.ltk.core.refactoring.Change;
import com.codenvy.eclipse.ltk.core.refactoring.ChangeDescriptor;
import com.codenvy.eclipse.ltk.core.refactoring.ContentStamp;
import com.codenvy.eclipse.ltk.core.refactoring.TextFileChange;

import org.exoplatform.ide.editor.shared.text.IDocument;
import org.exoplatform.ide.editor.shared.text.edits.UndoEdit;

/**
 * A {@link TextFileChange} that operates on an {@link ICompilationUnit} in the workspace.
 *
 * @since 1.3
 */
public class CompilationUnitChange extends TextFileChange {

    private final ICompilationUnit fCUnit;

    /** The (optional) refactoring descriptor */
    private ChangeDescriptor fDescriptor;

    /**
     * Creates a new <code>CompilationUnitChange</code>.
     *
     * @param name
     *         the change's name, mainly used to render the change in the UI
     * @param cunit
     *         the compilation unit this change works on
     */
    public CompilationUnitChange(String name, ICompilationUnit cunit) {
        super(name, getFile(cunit));
        Assert.isNotNull(cunit);
        fCUnit = cunit;
        setTextType("java"); //$NON-NLS-1$
    }

    private static IFile getFile(ICompilationUnit cunit) {
        return (IFile)cunit.getResource();
    }

    /** {@inheritDoc} */
    public Object getModifiedElement() {
        return fCUnit;
    }

    /**
     * Returns the compilation unit this change works on.
     *
     * @return the compilation unit this change works on
     */
    public ICompilationUnit getCompilationUnit() {
        return fCUnit;
    }

    /** {@inheritDoc} */
    protected IDocument acquireDocument(IProgressMonitor pm) throws CoreException {
        pm.beginTask("", 2); //$NON-NLS-1$
        fCUnit.becomeWorkingCopy(new SubProgressMonitor(pm, 1));
        return super.acquireDocument(new SubProgressMonitor(pm, 1));
    }

    /** {@inheritDoc} */
    protected void releaseDocument(IDocument document, IProgressMonitor pm) throws CoreException {
        boolean isModified = isDocumentModified();
        super.releaseDocument(document, pm);
        try {
            fCUnit.discardWorkingCopy();
        } finally {
            if (isModified && !isDocumentAcquired()) {
                if (fCUnit.isWorkingCopy()) {
                    fCUnit.reconcile(ICompilationUnit.NO_AST, false /* don't force problem detection */, null /* use primary owner */,
                                     null /* no progress monitor */);
                } else {
                    fCUnit.makeConsistent(pm);
                }
            }
        }
    }

    /** {@inheritDoc} */
    protected Change createUndoChange(UndoEdit edit, ContentStamp stampToRestore) {
        try {
            return new UndoCompilationUnitChange(getName(), fCUnit, edit, stampToRestore, getSaveMode());
        } catch (CoreException e) {
            Util.log(e);
            return null;
        }
    }

    /** {@inheritDoc} */
    public Object getAdapter(Class adapter) {
        if (ICompilationUnit.class.equals(adapter)) {
            return fCUnit;
        }
        return super.getAdapter(adapter);
    }

    /**
     * Sets the refactoring descriptor for this change.
     *
     * @param descriptor
     *         the descriptor to set, or <code>null</code> to set no descriptor
     */
    public void setDescriptor(ChangeDescriptor descriptor) {
        fDescriptor = descriptor;
    }

    /** {@inheritDoc} */
    public ChangeDescriptor getDescriptor() {
        return fDescriptor;
    }
}

