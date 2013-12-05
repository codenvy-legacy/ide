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
package com.codenvy.ide.ext.java.jdt.internal.corext.refactoring.code;

import com.codenvy.ide.ext.java.jdt.refactoring.ChangeDescriptor;
import com.codenvy.ide.ext.java.jdt.refactoring.TextFileChange;

import com.codenvy.ide.runtime.CoreException;
import com.codenvy.ide.text.Document;


/**
 * A {@link TextFileChange} that operates on an CompilationUnit in the workspace.
 *
 * @since 1.3
 */
public class CompilationUnitChange extends TextFileChange {

    //	private final ICompilationUnit fCUnit;

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
    public CompilationUnitChange(String name, Document document) {
        super(name, document);
        //		Assert.isNotNull(cunit);
        //		fCUnit= cunit;
        setTextType("java"); //$NON-NLS-1$
    }

    //	private static IFile getFile(ICompilationUnit cunit) {
    //		return (IFile) cunit.getResource();
    //	}

    /** {@inheritDoc} */
    public Object getModifiedElement() {
        //TODO
        return this;
    }

    //   /**
    //    * Returns the compilation unit this change works on.
    //    *
    //    * @return the compilation unit this change works on
    //    */
    //   public ICompilationUnit getCompilationUnit()
    //   {
    //      return fCUnit;
    //   }

    /** {@inheritDoc} */
    protected Document acquireDocument() throws CoreException {
        //      pm.beginTask("", 2); //$NON-NLS-1$
        //      fCUnit.becomeWorkingCopy(new SubProgressMonitor(pm, 1));
        return super.acquireDocument();
    }

    /** {@inheritDoc} */
    protected void releaseDocument(Document document) throws CoreException {
        //TODO
        //      boolean isModified = isDocumentModified();
        //      super.releaseDocument(document, pm);
        //      try
        //      {
        //         fCUnit.discardWorkingCopy();
        //      }
        //      finally
        //      {
        //         if (isModified && !isDocumentAcquired())
        //         {
        //            if (fCUnit.isWorkingCopy())
        //               fCUnit.reconcile(ICompilationUnit.NO_AST, false /* don't force problem detection */,
        //                  null /* use primary owner */, null /* no progress monitor */);
        //
        //            else
        //               fCUnit.makeConsistent(pm);
        //         }
        //      }
    }

    //   /**
    //    * {@inheritDoc}
    //    */
    //   protected Change createUndoChange(UndoEdit edit)
    //   {
    //TODO
    //      try
    //      {
    //         return new UndoCompilationUnitChange(getName(), fCUnit, edit, stampToRestore, getSaveMode());
    //      }
    //      catch (CoreException e)
    //      {
    //         JavaManipulationPlugin.log(e);
    //         return null;
    //      }
    //         return null;
    //   }

    /** {@inheritDoc} */
    public Object getAdapter(Class adapter) {
        //TODO
        //      if (ICompilationUnit.class.equals(adapter))
        //         return fCUnit;
        //      return super.getAdapter(adapter);
        return null;
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
