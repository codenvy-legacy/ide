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
import com.codenvy.eclipse.core.runtime.CoreException;
import com.codenvy.eclipse.core.runtime.IProgressMonitor;
import com.codenvy.eclipse.core.runtime.IStatus;
import com.codenvy.eclipse.core.runtime.Status;
import com.codenvy.eclipse.core.runtime.SubProgressMonitor;
import com.codenvy.eclipse.jdt.core.ICompilationUnit;
import com.codenvy.eclipse.jdt.internal.corext.util.Messages;
import com.codenvy.eclipse.ltk.core.refactoring.Change;
import com.codenvy.eclipse.ltk.core.refactoring.ContentStamp;
import com.codenvy.eclipse.ltk.core.refactoring.UndoTextFileChange;

import org.exoplatform.ide.editor.shared.text.edits.UndoEdit;

/* package */ class UndoCompilationUnitChange extends UndoTextFileChange
{

   private ICompilationUnit fCUnit;

   public UndoCompilationUnitChange(String name, ICompilationUnit unit, UndoEdit undo, ContentStamp stampToRestore,
      int saveMode) throws CoreException
   {
      super(name, getFile(unit), undo, stampToRestore, saveMode);
      fCUnit = unit;
   }

   private static IFile getFile(ICompilationUnit cunit) throws CoreException
   {
      IFile file = (IFile)cunit.getResource();
      if (file == null)
      {
         String message = Messages.format("Compilation unit ''{0}'' does not have an underlying file.",
            cunit.getElementName());
         throw new CoreException(new Status(IStatus.ERROR, "IDE", message));
      }
      return file;
   }

   /**
    * {@inheritDoc}
    */
   public Object getModifiedElement()
   {
      return fCUnit;
   }

   /**
    * {@inheritDoc}
    */
   protected Change createUndoChange(UndoEdit edit, ContentStamp stampToRestore) throws CoreException
   {
      return new UndoCompilationUnitChange(getName(), fCUnit, edit, stampToRestore, getSaveMode());
   }

   /**
    * {@inheritDoc}
    */
   public Change perform(IProgressMonitor pm) throws CoreException
   {
      pm.beginTask("", 2); //$NON-NLS-1$
      fCUnit.becomeWorkingCopy(new SubProgressMonitor(pm, 1));
      try
      {
         return super.perform(new SubProgressMonitor(pm, 1));
      }
      finally
      {
         fCUnit.discardWorkingCopy();
      }
   }
}
