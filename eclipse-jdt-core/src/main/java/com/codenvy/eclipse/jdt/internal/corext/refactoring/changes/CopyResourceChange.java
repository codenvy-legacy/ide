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

import com.codenvy.eclipse.core.resources.IContainer;
import com.codenvy.eclipse.core.resources.IFile;
import com.codenvy.eclipse.core.resources.IFolder;
import com.codenvy.eclipse.core.resources.IProject;
import com.codenvy.eclipse.core.resources.IResource;
import com.codenvy.eclipse.core.runtime.Assert;
import com.codenvy.eclipse.core.runtime.CoreException;
import com.codenvy.eclipse.core.runtime.IPath;
import com.codenvy.eclipse.core.runtime.IProgressMonitor;
import com.codenvy.eclipse.core.runtime.OperationCanceledException;
import com.codenvy.eclipse.core.runtime.SubProgressMonitor;
import com.codenvy.eclipse.jdt.internal.corext.refactoring.RefactoringCoreMessages;
import com.codenvy.eclipse.jdt.internal.corext.refactoring.reorg.INewNameQuery;
import com.codenvy.eclipse.jdt.internal.corext.refactoring.reorg.ReorgUtils;
import com.codenvy.eclipse.jdt.internal.corext.util.Messages;
import com.codenvy.eclipse.jdt.internal.ui.viewsupport.BasicElementLabels;
import com.codenvy.eclipse.ltk.core.refactoring.Change;
import com.codenvy.eclipse.ltk.core.refactoring.participants.ReorgExecutionLog;
import com.codenvy.eclipse.ltk.core.refactoring.resource.ResourceChange;

public class CopyResourceChange extends ResourceChange
{

   private final INewNameQuery fNewNameQuery;

   private final IResource fSource;

   private final IContainer fTarget;


   public CopyResourceChange(IResource res, IContainer dest, INewNameQuery newNameQuery)
   {
      Assert.isTrue(res instanceof IFile || res instanceof IFolder);
      Assert.isTrue(dest instanceof IProject || dest instanceof IFolder);

      fNewNameQuery = newNameQuery;
      fSource = res;
      fTarget = dest;

      // Copy resource change isn't undoable and isn't used
      // as a redo/undo change right now.
      setValidationMethod(SAVE_IF_DIRTY);
   }

   /* (non-Javadoc)
    * @see org.eclipse.ltk.core.refactoring.Change#getName()
    */
   @Override
   public String getName()
   {
      return Messages.format(RefactoringCoreMessages.CopyResourceString_copy,
         new String[]{BasicElementLabels.getPathLabel(getResource().getFullPath(),
            false), BasicElementLabels.getResourceName(getDestination())});
   }

   /* non java-doc
    * @see IChange#perform(ChangeContext, IProgressMonitor)
    */
   @Override
   public final Change perform(IProgressMonitor pm) throws CoreException, OperationCanceledException
   {
      try
      {
         pm.beginTask(getName(), 2);

         String newName = getNewResourceName();
         IResource resource = getResource();
         boolean performReorg = deleteIfAlreadyExists(new SubProgressMonitor(pm, 1), newName);
         if (!performReorg)
         {
            return null;
         }

         getResource().copy(getDestinationPath(newName), getReorgFlags(), new SubProgressMonitor(pm, 1));

         markAsExecuted(resource);
         return null;
      }
      finally
      {
         pm.done();
      }
   }

   private IPath getDestinationPath(String newName)
   {
      return getDestination().getFullPath().append(newName);
   }

   /**
    * returns false if source and destination are the same (in workspace or on disk)
    * in such case, no action should be performed
    *
    * @param pm      the progress monitor
    * @param newName the new name
    * @return returns <code>true</code> if the resource already exists
    * @throws com.codenvy.eclipse.core.runtime.CoreException
    *          thrown when teh resource cannpt be accessed
    */
   private boolean deleteIfAlreadyExists(IProgressMonitor pm, String newName) throws CoreException
   {
      pm.beginTask("", 1); //$NON-NLS-1$
      IResource current = getDestination().findMember(newName);
      if (current == null)
      {
         return true;
      }
      if (!current.exists())
      {
         return true;
      }

      IResource resource = getResource();
      Assert.isNotNull(resource);

      if (ReorgUtils.areEqualInWorkspaceOrOnDisk(resource, current))
      {
         return false;
      }

      if (current instanceof IFile)
      {
         ((IFile)current).delete(false, true, new SubProgressMonitor(pm, 1));
      }
      else if (current instanceof IFolder)
      {
         ((IFolder)current).delete(false, true, new SubProgressMonitor(pm, 1));
      }
      else
      {
         Assert.isTrue(false);
      }

      return true;
   }


   private String getNewResourceName() throws OperationCanceledException
   {
      if (fNewNameQuery == null)
      {
         return getResource().getName();
      }
      String name = fNewNameQuery.getNewName();
      if (name == null)
      {
         return getResource().getName();
      }
      return name;
   }

   @Override
   protected IResource getModifiedResource()
   {
      return getResource();
   }

   private IResource getResource()
   {
      return fSource;
   }

   private IContainer getDestination()
   {
      return fTarget;
   }

   private int getReorgFlags()
   {
      return IResource.KEEP_HISTORY | IResource.SHALLOW;
   }

   private void markAsExecuted(IResource resource)
   {
      ReorgExecutionLog log = (ReorgExecutionLog)getAdapter(ReorgExecutionLog.class);
      if (log != null)
      {
         log.markAsProcessed(resource);
      }
   }
}

