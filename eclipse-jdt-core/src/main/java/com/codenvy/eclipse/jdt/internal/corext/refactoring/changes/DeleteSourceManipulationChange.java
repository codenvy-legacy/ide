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

import com.codenvy.eclipse.core.resources.IFile;
import com.codenvy.eclipse.core.resources.IResource;
import com.codenvy.eclipse.core.runtime.Assert;
import com.codenvy.eclipse.core.runtime.CoreException;
import com.codenvy.eclipse.core.runtime.IProgressMonitor;
import com.codenvy.eclipse.core.runtime.SubProgressMonitor;
import com.codenvy.eclipse.jdt.core.ICompilationUnit;
import com.codenvy.eclipse.jdt.core.IJavaElement;
import com.codenvy.eclipse.jdt.core.IPackageFragment;
import com.codenvy.eclipse.jdt.core.ISourceManipulation;
import com.codenvy.eclipse.jdt.core.JavaCore;
import com.codenvy.eclipse.jdt.internal.corext.refactoring.RefactoringCoreMessages;
import com.codenvy.eclipse.jdt.internal.corext.util.Messages;
import com.codenvy.eclipse.jdt.ui.JavaElementLabels;
import com.codenvy.eclipse.ltk.core.refactoring.Change;
import com.codenvy.eclipse.ltk.core.refactoring.NullChange;
import com.codenvy.eclipse.ui.ide.undo.ResourceDescription;

/**
 * Caveat: undo of package fragment deletes is provided by a wrapping
 * UndoablePackageDeleteChange. This change returns a NullChange as undo for package fragments.
 */
public class DeleteSourceManipulationChange extends AbstractDeleteChange
{

   private final String fHandle;

   public DeleteSourceManipulationChange(ISourceManipulation sm, boolean isExecuteChange)
   {
      Assert.isNotNull(sm);
      fHandle = getJavaElement(sm).getHandleIdentifier();

      if (isExecuteChange)
      {
         if (sm instanceof ICompilationUnit)
         {
            // don't check anything in this case. We have a warning dialog
            // already presented to the user that the file is dirty.
            setValidationMethod(VALIDATE_DEFAULT);
         }
         else
         {
            setValidationMethod(VALIDATE_NOT_DIRTY);
         }
      }
      else
      {
         setValidationMethod(VALIDATE_NOT_DIRTY | VALIDATE_NOT_READ_ONLY);
      }
   }

   /*
    * @see IChange#getName()
    */
   @Override
   public String getName()
   {
      IJavaElement javaElement = getJavaElement(getSourceManipulation());
      return Messages.format(RefactoringCoreMessages.DeleteSourceManipulationChange_0,
         JavaElementLabels.getElementLabel(javaElement, JavaElementLabels.ALL_DEFAULT));
   }

   /*
    * @see IChange#getModifiedLanguageElement()
    */
   @Override
   public Object getModifiedElement()
   {
      return JavaCore.create(fHandle);
   }

   /* (non-Javadoc)
    * @see org.eclipse.jdt.internal.corext.refactoring.base.JDTChange#getModifiedResource()
    */
   @Override
   protected IResource getModifiedResource()
   {
      IJavaElement elem = JavaCore.create(fHandle);
      if (elem != null)
      {
         return elem.getResource();
      }
      return null;
   }

   /*
    * @see DeleteChange#doDelete(IProgressMonitor)
    */
   @Override
   protected Change doDelete(IProgressMonitor pm) throws CoreException
   {
      ISourceManipulation element = getSourceManipulation();
      // we have to save dirty compilation units before deleting them. Otherwise
      // we will end up showing ghost compilation units in the package explorer
      // since the primary working copy still exists.
      if (element instanceof ICompilationUnit)
      {
         pm.beginTask("", 2); //$NON-NLS-1$
         ICompilationUnit unit = (ICompilationUnit)element;
         saveCUnitIfNeeded(unit, new SubProgressMonitor(pm, 1));

         IResource resource = unit.getResource();
         ResourceDescription resourceDescription = ResourceDescription.fromResource(resource);
         element.delete(false, new SubProgressMonitor(pm, 1));
         resourceDescription.recordStateFromHistory(resource, new SubProgressMonitor(pm, 1));
         return new UndoDeleteResourceChange(resourceDescription);

      }
      else if (element instanceof IPackageFragment)
      {
         ICompilationUnit[] units = ((IPackageFragment)element).getCompilationUnits();
         pm.beginTask("", units.length + 1); //$NON-NLS-1$
         for (int i = 0; i < units.length; i++)
         {
            // fix https://bugs.eclipse.org/bugs/show_bug.cgi?id=66835
            saveCUnitIfNeeded(units[i], new SubProgressMonitor(pm, 1));
         }
         element.delete(false, new SubProgressMonitor(pm, 1));
         return new NullChange(); // caveat: real undo implemented by UndoablePackageDeleteChange

      }
      else
      {
         element.delete(false, pm);
         return null; //should not happen
      }
   }

   private ISourceManipulation getSourceManipulation()
   {
      return (ISourceManipulation)getModifiedElement();
   }

   private static IJavaElement getJavaElement(ISourceManipulation sm)
   {
      //all known ISourceManipulations are IJavaElements
      return (IJavaElement)sm;
   }

   private static void saveCUnitIfNeeded(ICompilationUnit unit, IProgressMonitor pm) throws CoreException
   {
      saveFileIfNeeded((IFile)unit.getResource(), pm);
   }
}

