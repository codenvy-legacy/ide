/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.eclipse.jdt.internal.core.search.indexing;

import com.codenvy.eclipse.core.runtime.IPath;
import com.codenvy.eclipse.core.runtime.IProgressMonitor;
import com.codenvy.eclipse.jdt.internal.core.index.Index;
import com.codenvy.eclipse.jdt.internal.core.search.processing.JobManager;
import com.codenvy.eclipse.jdt.internal.core.util.Util;

import org.exoplatform.services.security.ConversationState;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/*
 * Save the index of a project.
 */
public class SaveIndex extends IndexRequest
{
   private CountDownLatch latch;

   public SaveIndex(IPath containerPath, IndexManager manager)
   {
      super(containerPath, manager);
   }

   public SaveIndex(IPath containerPath, IndexManager manager, CountDownLatch latch)
   {
      this(containerPath, manager);
      this.latch = latch;
   }

   public boolean execute(IProgressMonitor progressMonitor)
   {
      ConversationState.setCurrent(state);
      if (this.isCancelled || progressMonitor != null && progressMonitor.isCanceled())
      {
         if(latch != null)
         {
            latch.countDown();
         }
         return true;
      }

		/* ensure no concurrent write access to index */
      Index index = this.manager.getIndex(this.containerPath, true /*reuse index file*/,
         false /*don't create if none*/);
      if (index == null)
      {
         return true;
      }
      ReadWriteMonitor monitor = index.monitor;
      if (monitor == null)
      {
         return true; // index got deleted since acquired
      }

      try
      {
         monitor.enterWrite(); // ask permission to write
         this.manager.saveIndex(index);
      }
      catch (IOException e)
      {
         if (JobManager.VERBOSE)
         {
            Util.verbose("-> failed to save index " + this.containerPath + " because of the following exception:",
               System.err); //$NON-NLS-1$ //$NON-NLS-2$
            e.printStackTrace();
         }
         return false;
      }
      finally
      {
         monitor.exitWrite(); // free write lock
         if(latch != null)
         {
            latch.countDown();
         }
      }
      return true;
   }

   public String toString()
   {
      return "saving index for " + this.containerPath; //$NON-NLS-1$
   }
}
