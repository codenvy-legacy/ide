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
import com.codenvy.eclipse.core.resources.mapping.ResourceMapping;
import com.codenvy.eclipse.core.runtime.CoreException;
import com.codenvy.eclipse.core.runtime.IProgressMonitor;
import com.codenvy.eclipse.core.runtime.OperationCanceledException;
import com.codenvy.eclipse.core.runtime.SubProgressMonitor;
import com.codenvy.eclipse.jdt.core.ICompilationUnit;
import com.codenvy.eclipse.jdt.core.IPackageFragment;
import com.codenvy.eclipse.jdt.core.JavaCore;
import com.codenvy.eclipse.jdt.internal.corext.refactoring.reorg.INewNameQuery;
import com.codenvy.eclipse.jdt.internal.corext.util.JavaElementResourceMapping;
import com.codenvy.eclipse.jdt.ui.JavaElementLabels;
import com.codenvy.eclipse.ltk.core.refactoring.Change;
import com.codenvy.eclipse.ltk.core.refactoring.participants.ReorgExecutionLog;
import com.codenvy.eclipse.ltk.core.refactoring.resource.ResourceChange;

abstract class CompilationUnitReorgChange extends ResourceChange
{

   private String fCuHandle;

   private String fOldPackageHandle;

   private String fNewPackageHandle;

   private INewNameQuery fNewNameQuery;

   CompilationUnitReorgChange(ICompilationUnit cu, IPackageFragment dest, INewNameQuery newNameQuery)
   {
      fCuHandle = cu.getHandleIdentifier();
      fNewPackageHandle = dest.getHandleIdentifier();
      fNewNameQuery = newNameQuery;
      fOldPackageHandle = cu.getParent().getHandleIdentifier();
   }

   CompilationUnitReorgChange(ICompilationUnit cu, IPackageFragment dest)
   {
      this(cu, dest, null);
   }

   CompilationUnitReorgChange(String oldPackageHandle, String newPackageHandle, String cuHandle)
   {
      fOldPackageHandle = oldPackageHandle;
      fNewPackageHandle = newPackageHandle;
      fCuHandle = cuHandle;
   }

   @Override
   public final Change perform(IProgressMonitor pm) throws CoreException, OperationCanceledException
   {
      pm.beginTask(getName(), 1);
      try
      {
         ICompilationUnit unit = getCu();
         ResourceMapping mapping = JavaElementResourceMapping.create(unit);
         Change result = doPerformReorg(new SubProgressMonitor(pm, 1));
         markAsExecuted(unit, mapping);
         return result;
      }
      finally
      {
         pm.done();
      }
   }

   abstract Change doPerformReorg(IProgressMonitor pm) throws CoreException, OperationCanceledException;

   @Override
   public Object getModifiedElement()
   {
      return getCu();
   }

   /* (non-Javadoc)
    * @see org.eclipse.jdt.internal.corext.refactoring.base.JDTChange#getModifiedResource()
    */
   @Override
   protected IResource getModifiedResource()
   {
      ICompilationUnit cu = getCu();
      if (cu != null)
      {
         return cu.getResource();
      }
      return null;
   }

   ICompilationUnit getCu()
   {
      return (ICompilationUnit)JavaCore.create(fCuHandle);
   }

   IPackageFragment getOldPackage()
   {
      return (IPackageFragment)JavaCore.create(fOldPackageHandle);
   }

   IPackageFragment getDestinationPackage()
   {
      return (IPackageFragment)JavaCore.create(fNewPackageHandle);
   }

   String getNewName() throws OperationCanceledException
   {
      if (fNewNameQuery == null)
      {
         return null;
      }
      return fNewNameQuery.getNewName();
   }

   static String getPackageName(IPackageFragment pack)
   {
      return JavaElementLabels.getElementLabel(pack, JavaElementLabels.ALL_DEFAULT);
   }

   private void markAsExecuted(ICompilationUnit unit, ResourceMapping mapping)
   {
      ReorgExecutionLog log = (ReorgExecutionLog)getAdapter(ReorgExecutionLog.class);
      if (log != null)
      {
         log.markAsProcessed(unit);
         log.markAsProcessed(mapping);
      }
   }
}
