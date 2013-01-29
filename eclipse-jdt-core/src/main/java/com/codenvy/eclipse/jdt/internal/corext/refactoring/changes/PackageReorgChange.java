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
import com.codenvy.eclipse.jdt.core.IPackageFragment;
import com.codenvy.eclipse.jdt.core.IPackageFragmentRoot;
import com.codenvy.eclipse.jdt.core.JavaCore;
import com.codenvy.eclipse.jdt.core.JavaModelException;
import com.codenvy.eclipse.jdt.internal.corext.refactoring.reorg.INewNameQuery;
import com.codenvy.eclipse.jdt.internal.corext.util.JavaElementResourceMapping;
import com.codenvy.eclipse.ltk.core.refactoring.Change;
import com.codenvy.eclipse.ltk.core.refactoring.participants.ReorgExecutionLog;
import com.codenvy.eclipse.ltk.core.refactoring.resource.ResourceChange;

abstract class PackageReorgChange extends ResourceChange
{

   private String fPackageHandle;

   private String fDestinationHandle;

   private INewNameQuery fNameQuery;

   PackageReorgChange(IPackageFragment pack, IPackageFragmentRoot dest, INewNameQuery nameQuery)
   {
      fPackageHandle = pack.getHandleIdentifier();
      fDestinationHandle = dest.getHandleIdentifier();
      fNameQuery = nameQuery;

      // it is enough to check the package only since package reorg changes
      // are not undoable. Don't check for read only here since
      // we already ask for user confirmation and moving a read
      // only package doesn't go thorugh validate edit (no
      // file content is modified).
      setValidationMethod(VALIDATE_DEFAULT);
   }

   abstract Change doPerformReorg(IProgressMonitor pm) throws JavaModelException, OperationCanceledException;

   @Override
   public final Change perform(IProgressMonitor pm) throws CoreException, OperationCanceledException
   {
      pm.beginTask(getName(), 1);
      try
      {
         IPackageFragment pack = getPackage();
         ResourceMapping mapping = JavaElementResourceMapping.create(pack);
         final Change result = doPerformReorg(pm);
         markAsExecuted(pack, mapping);
         return result;
      }
      finally
      {
         pm.done();
      }
   }

   @Override
   public Object getModifiedElement()
   {
      return getPackage();
   }

   /* (non-Javadoc)
    * @see org.eclipse.jdt.internal.corext.refactoring.base.JDTChange#getModifiedResource()
    */
   @Override
   protected IResource getModifiedResource()
   {
      IPackageFragment pack = getPackage();
      if (pack != null)
      {
         return pack.getResource();
      }
      return null;
   }

   IPackageFragmentRoot getDestination()
   {
      return (IPackageFragmentRoot)JavaCore.create(fDestinationHandle);
   }

   IPackageFragment getPackage()
   {
      return (IPackageFragment)JavaCore.create(fPackageHandle);
   }

   String getNewName() throws OperationCanceledException
   {
      if (fNameQuery == null)
      {
         return null;
      }
      return fNameQuery.getNewName();
   }

   private void markAsExecuted(IPackageFragment pack, ResourceMapping mapping)
   {
      ReorgExecutionLog log = (ReorgExecutionLog)getAdapter(ReorgExecutionLog.class);
      if (log != null)
      {
         log.markAsProcessed(pack);
         log.markAsProcessed(mapping);
      }
   }
}
