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
import com.codenvy.eclipse.core.filebuffers.ITextFileBuffer;
import com.codenvy.eclipse.core.filebuffers.LocationKind;
import com.codenvy.eclipse.core.resources.IFile;
import com.codenvy.eclipse.core.resources.IResource;
import com.codenvy.eclipse.core.runtime.CoreException;
import com.codenvy.eclipse.core.runtime.IProgressMonitor;
import com.codenvy.eclipse.core.runtime.SubProgressMonitor;
import com.codenvy.eclipse.ltk.core.refactoring.Change;
import com.codenvy.eclipse.ltk.core.refactoring.resource.ResourceChange;

abstract class AbstractDeleteChange extends ResourceChange
{

   protected abstract Change doDelete(IProgressMonitor pm) throws CoreException;

   /* non java-doc
    * @see IChange#perform(ChangeContext, IProgressMonitor)
    */
   @Override
   public final Change perform(IProgressMonitor pm) throws CoreException
   {
      try
      {
         Change undo = doDelete(pm);
         return undo;
      }
      finally
      {
         pm.done();
      }
   }

   protected static void saveFileIfNeeded(IFile file, IProgressMonitor pm) throws CoreException
   {
      ITextFileBuffer buffer = FileBuffers.getTextFileBufferManager().getTextFileBuffer(file.getFullPath(),
         LocationKind.IFILE);
      if (buffer != null && buffer.isDirty() && buffer.isStateValidated() && buffer.isSynchronized())
      {
         pm.beginTask("", 2); //$NON-NLS-1$
         buffer.commit(new SubProgressMonitor(pm, 1), false);
         file.refreshLocal(IResource.DEPTH_ONE, new SubProgressMonitor(pm, 1));
      }
      pm.done();
   }
}

